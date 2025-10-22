package cloud.neko_lkc1009.nekoshare.common.process;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频超分工具类
 * 方案：
 * 1) ffmpeg 抽帧到临时目录
 * 2) 使用 waifu2x-ncnn-vulkan 对帧图批处理放大与降噪
 * 3) ffmpeg 将处理后的帧回封装为视频并复用原音频
 */
@Slf4j
@Component
public class VideoPromote {
    @Value("${ffmpeg.executable.path:ffmpeg}")
    private String ffmpegPath;

    @Value("${probe.executable.path}")
    private String probePath;

    @Value("${waifu2x.executable.path:waifu2x-ncnn-vulkan}")
    private String waifu2xPath;

    @Value("${waifu2x.model.path:models}")
    private String modelPath;

    @Data
    @NoArgsConstructor
    public static class PromoteConfig {
        private Integer scale = 2;                 // 放大倍数 1-4
        private Integer noiseLevel = 0;            // 降噪级别 -1~3（-1表示不降噪）
        private Integer tileSize = 0;              // 分块大小，0 自动
        private String frameFormat = "png";        // 帧图片格式：png/jpg
        private String videoCodec = "libx264";    // 输出视频编码
        private String pixelFormat = "yuv420p";   // 输出像素格式
        private Integer crf = 18;                  // 输出视频CRF
        private String preset = "slow";           // 编码预设
        private Integer fps = 0;                   // 0=自动检测；否则固定帧率
        private Boolean copyAudio = true;          // 复用原音频
        private Boolean keepTemp = false;          // 是否保留临时目录
        private Boolean verbose = false;           // 详细日志
    }

    /**
     * 使用默认配置进行视频超分
     */
    public boolean promote(String inputPath, String outputPath) {
        return promote(inputPath, outputPath, new PromoteConfig());
    }

    /**
     * 使用指定配置进行视频超分
     */
    public boolean promote(String inputPath, String outputPath, PromoteConfig config) {
        try {
            Path in = Paths.get(inputPath);
            if (!Files.exists(in)) {
                log.error("输入视频不存在: {}", inputPath);
                return false;
            }
            Path out = Paths.get(outputPath);
            Files.createDirectories(out.getParent());

            // 临时目录
            String base = System.getProperty("java.io.tmpdir");
            String stamp = String.valueOf(Instant.now().toEpochMilli());
            Path framesDir = Paths.get(base, "nekoshare_frames_" + stamp);
            Path upFramesDir = Paths.get(base, "nekoshare_frames_up_" + stamp);
            Files.createDirectories(framesDir);
            Files.createDirectories(upFramesDir);

            try {
                // 1) 抽帧
                if (!extractFrames(inputPath, framesDir.toString(), config)) return false;

                // 2) 超分帧
                if (!promoteFrames(framesDir.toString(), upFramesDir.toString(), config)) return false;

                // 3) 回封装
                Integer fps = config.getFps() != null && config.getFps() > 0 ? config.getFps() : detectFps(inputPath);
                if (fps == null || fps <= 0) fps = 30; // 兜底
                if (!assembleVideo(inputPath, upFramesDir.toString(), outputPath, fps, config)) return false;

                log.info("视频超分成功: {} -> {}", inputPath, outputPath);
                return true;
            } finally {
                if (!Boolean.TRUE.equals(config.getKeepTemp())) {
                    safeDeleteDirectory(framesDir);
                    safeDeleteDirectory(upFramesDir);
                } else {
                    log.info("保留临时目录: {}, {}", framesDir, upFramesDir);
                }
            }
        } catch (IOException e) {
            log.error("视频超分 IO 异常", e);
            return false;
        }
    }

    private boolean extractFrames(String inputPath, String framesDir, PromoteConfig config) throws IOException {
        String pattern = Paths.get(framesDir, "%08d." + config.getFrameFormat()).toString();
        List<String> cmd = new ArrayList<>();
        cmd.add(ffmpegPath);
        cmd.add("-y");
        cmd.add("-i");
        cmd.add(inputPath);
        cmd.add("-vsync");
        cmd.add("0");
        cmd.add(pattern);
        log.info("抽帧命令: {}", String.join(" ", cmd));
        return runProcess(cmd, config.getVerbose(), "ffmpeg");
    }

    private boolean promoteFrames(String inputDir, String outputDir, PromoteConfig config) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add(waifu2xPath);
        cmd.add("-i");
        cmd.add(inputDir);
        cmd.add("-o");
        cmd.add(outputDir);
        cmd.add("-s");
        cmd.add(String.valueOf(Math.max(1, Math.min(4, config.getScale()))));
        if (config.getNoiseLevel() != null && config.getNoiseLevel() >= 0) {
            cmd.add("-n");
            cmd.add(String.valueOf(Math.min(3, config.getNoiseLevel())));
        }
        if (config.getTileSize() != null && config.getTileSize() > 0) {
            cmd.add("-t");
            cmd.add(String.valueOf(config.getTileSize()));
        }
        cmd.add("-f");
        cmd.add(config.getFrameFormat());
        if (modelPath != null && !modelPath.isEmpty()) {
            File m = new File(modelPath);
            if (m.exists()) {
                cmd.add("-m");
                cmd.add(modelPath);
            }
        }
        if (Boolean.TRUE.equals(config.getVerbose())) {
            cmd.add("-v");
        }
        log.info("waifu2x 批量命令: {}", String.join(" ", cmd));
        return runProcess(cmd, config.getVerbose(), "waifu2x");
    }

    private boolean assembleVideo(String inputVideo, String framesDir, String outputPath, int fps, PromoteConfig config) throws IOException {
        String pattern = Paths.get(framesDir, "%08d." + config.getFrameFormat()).toString();
        List<String> cmd = new ArrayList<>();
        cmd.add(ffmpegPath);
        cmd.add("-y");
        cmd.add("-framerate");
        cmd.add(String.valueOf(fps));
        cmd.add("-i");
        cmd.add(pattern);
        // 引入原视频以复用音频
        cmd.add("-i");
        cmd.add(inputVideo);
        cmd.add("-map");
        cmd.add("0:v");
        cmd.add("-map");
        cmd.add("1:a?");
        cmd.add("-c:v");
        cmd.add(config.getVideoCodec());
        cmd.add("-crf");
        cmd.add(String.valueOf(config.getCrf()));
        cmd.add("-preset");
        cmd.add(config.getPreset());
        cmd.add("-pix_fmt");
        cmd.add(config.getPixelFormat());
        if (Boolean.TRUE.equals(config.getCopyAudio())) {
            cmd.add("-c:a");
            cmd.add("copy");
        } else {
            cmd.add("-c:a");
            cmd.add("aac");
            cmd.add("-b:a");
            cmd.add("192k");
        }
        cmd.add("-shortest");
        cmd.add(outputPath);
        log.info("封装命令: {}", String.join(" ", cmd));
        return runProcess(cmd, config.getVerbose(), "ffmpeg");
    }

    private Integer detectFps(String inputPath) throws IOException {
        // 使用 probe 获取帧率 (r_frame_rate)
        List<String> cmd = new ArrayList<>();
        cmd.add(probePath);
        cmd.add("-v");
        cmd.add("0");
        cmd.add("-of");
        cmd.add("csv=p=0");
        cmd.add("-select_streams");
        cmd.add("v:0");
        cmd.add("-show_entries");
        cmd.add("stream=r_frame_rate");
        cmd.add(inputPath);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String value = null;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            value = r.readLine();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (value == null || value.isEmpty()) return null;
        // 解析分数，如 30000/1001
        try {
            if (value.contains("/")) {
                String[] parts = value.trim().split("/");
                double num = Double.parseDouble(parts[0]);
                double den = Double.parseDouble(parts[1]);
                double fps = den == 0 ? 0 : num / den;
                return (int) Math.round(fps);
            } else {
                double fps = Double.parseDouble(value.trim());
                return (int) Math.round(fps);
            }
        } catch (Exception e) {
            log.warn("解析帧率失败: {}", value);
            return null;
        }
    }

    private boolean runProcess(List<String> cmd, boolean verbose, String tag) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (verbose) log.info("{}: {}", tag, line);
            }
        }
        try {
            int code = p.waitFor();
            if (code != 0) log.error("{} 退出码: {}", tag, code);
            return code == 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("{} 进程被中断", tag, e);
            return false;
        }
    }

    private void safeDeleteDirectory(Path dir) {
        if (dir == null) return;
        try {
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted((a, b) -> b.compareTo(a)) // 先删文件再删目录
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                            }
                        });
            }
        } catch (IOException ignored) {
        }
    }

    public boolean isFfmpegAvailable() {
        try {
            Process p = new ProcessBuilder(ffmpegPath, "-version").start();
            int code = p.waitFor();
            return code == 0;
        } catch (IOException | InterruptedException e) {
            log.warn("ffmpeg 不可用: {}", e.getMessage());
            return false;
        }
    }

    public boolean isWaifu2xAvailable() {
        try {
            Process p = new ProcessBuilder(waifu2xPath, "-h").start();
            int code = p.waitFor();
            return code == 0 || code == 1;
        } catch (IOException | InterruptedException e) {
            log.warn("waifu2x 不可用: {}", e.getMessage());
            return false;
        }
    }
}
