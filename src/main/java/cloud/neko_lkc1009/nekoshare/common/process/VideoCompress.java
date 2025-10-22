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
import java.util.ArrayList;
import java.util.List;

/**
 * 视频压缩工具类
 * 基于 ffmpeg 实现
 */
@Slf4j
@Component
public class VideoCompress {
    @Value("${ffmpeg.executable.path}")
    private String ffmpegPath;

    /**
     * 压缩配置
     */
    @Data
    @NoArgsConstructor
    public static class CompressConfig {
        private Integer maxWidth = 1920;       // 最大宽度，保持比例缩放，不放大
        private Integer maxHeight = 1080;      // 最大高度，保持比例缩放，不放大
        private Integer crf = 23;              // 质量参数（0-51, 值越小质量越好体积越大）
        private String preset = "medium";     // 预设：ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow
        private String videoCodec = "libx264";// 视频编码器
        private String pixelFormat = "yuv420p";// 像素格式，保证兼容性
        private Integer targetBitrateKbps = null; // 目标码率（kbps），优先于CRF
        private Boolean copyAudio = true;      // 是否拷贝音频编码
        private Boolean twoPass = false;       // 是否开启双 pass（仅在指定码率时常用）
        private Boolean verbose = false;       // 是否输出详细日志
    }

    /**
     * 使用默认配置压缩
     */
    public boolean compress(String inputPath, String outputPath) {
        return compress(inputPath, outputPath, new CompressConfig());
    }

    /**
     * 使用自定义配置压缩
     */
    public boolean compress(String inputPath, String outputPath, CompressConfig config) {
        try {
            Path in = Paths.get(inputPath);
            if (!Files.exists(in)) {
                log.error("输入文件不存在: {}", inputPath);
                return false;
            }
            Path out = Paths.get(outputPath);
            Files.createDirectories(out.getParent());

            // 构建压缩命令
            List<String> command = buildCompressCommand(inputPath, outputPath, config);
            log.info("执行 ffmpeg 命令: {}", String.join(" ", command));

            if (Boolean.TRUE.equals(config.getTwoPass()) && config.getTargetBitrateKbps() != null) {
                // 两遍编码
                List<String> pass1 = new ArrayList<>(command);
                // 将输出文件改为 NUL 丢弃（Windows），或 /dev/null（非Windows），这里假设 Windows
                pass1.add(0, ffmpegPath);
                int outIndex = pass1.lastIndexOf(outputPath);
                if (outIndex >= 0) pass1.set(outIndex, "NUL");
                pass1.add("-pass");
                pass1.add("1");

                List<String> pass2 = new ArrayList<>(command);
                pass2.add(0, ffmpegPath);
                pass2.add("-pass");
                pass2.add("2");

                if (!runProcess(pass1, config.getVerbose())) return false;
                return runProcess(pass2, config.getVerbose());
            } else {
                command.add(0, ffmpegPath);
                return runProcess(command, config.getVerbose());
            }
        } catch (IOException e) {
            log.error("视频压缩 IO 异常", e);
            return false;
        }
    }

    /**
     * 构建 ffmpeg 压缩命令（不包含可执行文件名）
     */
    private List<String> buildCompressCommand(String inputPath, String outputPath, CompressConfig config) {
        List<String> cmd = new ArrayList<>();
        cmd.add("-y");
        cmd.add("-i");
        cmd.add(inputPath);

        // 限制分辨率但不放大，保持比例
        String scaleExpr = String.format("scale='min(iw, %d)':'min(ih, %d)':force_original_aspect_ratio=decrease",
                Math.max(1, config.getMaxWidth()), Math.max(1, config.getMaxHeight()));
        cmd.add("-vf");
        cmd.add(scaleExpr);

        // 视频编码
        cmd.add("-c:v");
        cmd.add(config.getVideoCodec());

        if (config.getTargetBitrateKbps() != null && config.getTargetBitrateKbps() > 0) {
            cmd.add("-b:v");
            cmd.add(config.getTargetBitrateKbps() + "k");
        } else {
            cmd.add("-crf");
            cmd.add(String.valueOf(config.getCrf()));
        }

        cmd.add("-preset");
        cmd.add(config.getPreset());

        cmd.add("-pix_fmt");
        cmd.add(config.getPixelFormat());

        // 音频处理
        if (Boolean.TRUE.equals(config.getCopyAudio())) {
            cmd.add("-c:a");
            cmd.add("copy");
        } else {
            // 也可指定重新编码参数：例如 aac 128k
            cmd.add("-c:a");
            cmd.add("aac");
            cmd.add("-b:a");
            cmd.add("128k");
        }

        cmd.add(outputPath);
        return cmd;
    }

    /**
     * 运行进程
     */
    private boolean runProcess(List<String> command, boolean verbose) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (verbose) log.info("ffmpeg: {}", line);
            }
        }
        try {
            int code = p.waitFor();
            if (code != 0) {
                log.error("ffmpeg 退出码: {}", code);
            }
            return code == 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ffmpeg 进程被中断", e);
            return false;
        }
    }

    /**
     * 可用性检测
     */
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
}
