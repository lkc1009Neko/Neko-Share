package cloud.neko_lkc1009.nekoshare.common.process;

import lombok.Data;
import lombok.Getter;
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
 * 图片超分辨率提升工具类
 * 基于 waifu2x-ncnn-vulkan 实现
 */
@Slf4j
@Component
public class ImagePromote {
    @Value("${waifu2x.executable.path:waifu2x-ncnn-vulkan}")
    private String waifu2xExecutablePath;

    @Value("${waifu2x.model.path:models}")
    private String modelPath;

    /**
     * 降噪级别枚举
     */
    @Getter
    public enum NoiseLevel {
        NONE(-1),    // 不降噪
        LOW(0),      // 低降噪
        MEDIUM(1),   // 中等降噪
        HIGH(2),     // 高降噪
        HIGHEST(3);  // 最高降噪

        private final Integer value;

        NoiseLevel(int value) {
            this.value = value;
        }

    }

    /**
     * 图片提升配置
     */
    @Data
    @NoArgsConstructor
    public static class PromoteConfig {
        private Integer scale = 2;                          // 默认2倍放大
        private NoiseLevel noiseLevel = NoiseLevel.LOW; // 默认低降噪
        private Integer tileSize = 0;                       // 分块大小，0为自动
        private String format = "png";                   // 输出格式
        private Boolean verbose = false;                 // 是否输出详细日志

        public void setScale(Integer scale) {
            if (scale <= 0 || scale > 4) {
                throw new IllegalArgumentException("Scale must be between 1 and 4");
            }
            this.scale = scale;
        }

    }

    /**
     * 使用默认配置提升图片质量
     * 默认：2倍放大 + 低降噪
     *
     * @param inputPath  输入文件路径
     * @param outputPath 输出文件路径
     * @return 是否成功
     */
    public boolean promoteImage(String inputPath, String outputPath) {
        return promoteImage(inputPath, outputPath, new PromoteConfig());
    }

    /**
     * 使用指定配置提升图片质量
     *
     * @param inputPath  输入文件路径
     * @param outputPath 输出文件路径
     * @param config     提升配置
     * @return 是否成功
     */
    public boolean promoteImage(String inputPath, String outputPath, PromoteConfig config) {
        try {
            // 验证输入文件
            Path input = Paths.get(inputPath);
            if (!Files.exists(input)) {
                log.error("输入文件不存在: {}", inputPath);
                return false;
            }

            // 确保输出目录存在
            Path output = Paths.get(outputPath);
            Files.createDirectories(output.getParent());

            // 构建命令
            List<String> command = buildCommand(inputPath, outputPath, config);

            log.info("执行 waifu2x 命令: {}", String.join(" ", command));

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取输出
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (config.getVerbose()) {
                        log.info("waifu2x: {}", line);
                    }
                }
            }

            // 等待完成
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("图片提升成功: {} -> {}", inputPath, outputPath);
                return true;
            } else {
                log.error("图片提升失败，退出码: {}", exitCode);
                return false;
            }

        } catch (IOException e) {
            log.error("执行 waifu2x 时发生 IO 错误", e);
            return false;
        } catch (InterruptedException e) {
            log.error("waifu2x 进程被中断", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 批量提升图片质量
     *
     * @param inputDir  输入目录
     * @param outputDir 输出目录
     * @param config    提升配置
     * @return 成功处理的文件数量
     */
    public int promoteBatch(String inputDir, String outputDir, PromoteConfig config) {
        try {
            Path input = Paths.get(inputDir);
            if (!Files.exists(input) || !Files.isDirectory(input)) {
                log.error("输入目录不存在或不是目录: {}", inputDir);
                return 0;
            }

            // 确保输出目录存在
            Files.createDirectories(Paths.get(outputDir));

            // 构建命令（使用目录模式）
            List<String> command = buildBatchCommand(inputDir, outputDir, config);

            log.info("执行批量 waifu2x 命令: {}", String.join(" ", command));

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取输出
            int processedCount = 0;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (config.getVerbose()) {
                        log.info("waifu2x: {}", line);
                    }
                    // 简单计数（实际可根据输出优化）
                    if (line.contains("done")) {
                        processedCount++;
                    }
                }
            }

            // 等待完成
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("批量图片提升成功，处理文件数: {}", processedCount);
                return processedCount;
            } else {
                log.error("批量图片提升失败，退出码: {}", exitCode);
                return 0;
            }

        } catch (IOException e) {
            log.error("执行批量 waifu2x 时发生 IO 错误", e);
            return 0;
        } catch (InterruptedException e) {
            log.error("waifu2x 批量进程被中断", e);
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    /**
     * 构建单文件处理命令
     */
    private List<String> buildCommand(String inputPath, String outputPath, PromoteConfig config) {
        List<String> command = new ArrayList<>();
        command.add(waifu2xExecutablePath);
        command.add("-i");
        command.add(inputPath);
        command.add("-o");
        command.add(outputPath);
        command.add("-s");
        command.add(String.valueOf(config.getScale()));

        // 降噪级别
        if (config.getNoiseLevel() != NoiseLevel.NONE) {
            command.add("-n");
            command.add(String.valueOf(config.getNoiseLevel().getValue()));
        }

        // 分块大小
        if (config.getTileSize() > 0) {
            command.add("-t");
            command.add(String.valueOf(config.getTileSize()));
        }

        // 输出格式
        command.add("-f");
        command.add(config.getFormat());

        // 模型路径
        if (modelPath != null && !modelPath.isEmpty()) {
            File modelDir = new File(modelPath);
            if (modelDir.exists()) {
                command.add("-m");
                command.add(modelPath);
            }
        }

        // 详细输出
        if (config.getVerbose()) {
            command.add("-v");
        }

        return command;
    }

    /**
     * 构建批量处理命令
     */
    private List<String> buildBatchCommand(String inputDir, String outputDir, PromoteConfig config) {
        List<String> command = new ArrayList<>();
        command.add(waifu2xExecutablePath);
        command.add("-i");
        command.add(inputDir);
        command.add("-o");
        command.add(outputDir);
        command.add("-s");
        command.add(String.valueOf(config.getScale()));

        // 降噪级别
        if (config.getNoiseLevel() != NoiseLevel.NONE) {
            command.add("-n");
            command.add(String.valueOf(config.getNoiseLevel().getValue()));
        }

        // 分块大小
        if (config.getTileSize() > 0) {
            command.add("-t");
            command.add(String.valueOf(config.getTileSize()));
        }

        // 输出格式
        command.add("-f");
        command.add(config.getFormat());

        // 模型路径
        if (modelPath != null && !modelPath.isEmpty()) {
            File modelDir = new File(modelPath);
            if (modelDir.exists()) {
                command.add("-m");
                command.add(modelPath);
            }
        }

        // 详细输出
        if (config.getVerbose()) {
            command.add("-v");
        }

        return command;
    }

    /**
     * 检查 waifu2x 是否可用
     *
     * @return 是否可用
     */
    public boolean isWaifu2xAvailable() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(waifu2xExecutablePath, "-h");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0 || exitCode == 1; // 帮助命令可能返回 0 或 1
        } catch (IOException | InterruptedException e) {
            log.warn("waifu2x 不可用: {}", e.getMessage());
            return false;
        }
    }

}
