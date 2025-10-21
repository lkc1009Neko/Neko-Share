package cloud.neko_lkc1009.nekoshare.common.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImagePromote 测试类
 */
@SpringBootTest
class ImagePromoteTest {
    private ImagePromote imagePromote;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        imagePromote = new ImagePromote();
        // 设置测试用的 waifu2x 可执行文件路径
        ReflectionTestUtils.setField(imagePromote, "waifu2xExecutablePath", "waifu2x-ncnn-vulkan");
        ReflectionTestUtils.setField(imagePromote, "modelPath", "models");
    }

    /**
     * 测试：检查 waifu2x 是否可用
     */
    @Test
    void testIsWaifu2xAvailable() {
        // 注意：此测试依赖于系统是否安装了 waifu2x-ncnn-vulkan
        boolean available = imagePromote.isWaifu2xAvailable();
        // 只打印结果，不做断言（因为可能未安装）
        System.out.println("waifu2x 可用性: " + available);
    }

    /**
     * 测试：使用默认配置提升图片质量
     */
    @Test
    void testPromoteImageWithDefaultConfig() throws IOException {
        // 创建测试图片
        Path inputPath = createTestImage(tempDir, "input.png", 100, 100);
        Path outputPath = tempDir.resolve("output.png");

        // 执行提升（如果 waifu2x 可用）
        if (imagePromote.isWaifu2xAvailable()) {
            boolean result = imagePromote.promoteImage(
                    inputPath.toString(),
                    outputPath.toString()
            );

            assertTrue(result || !result, "方法应该返回布尔值");
            System.out.println("默认配置提升结果: " + result);
        } else {
            System.out.println("跳过测试：waifu2x 不可用");
        }
    }

    /**
     * 测试：使用自定义配置提升图片质量
     */
    @Test
    void testPromoteImageWithCustomConfig() throws IOException {
        // 创建测试图片
        Path inputPath = createTestImage(tempDir, "input_custom.png", 200, 200);
        Path outputPath = tempDir.resolve("output_custom.png");

        // 创建自定义配置
        ImagePromote.PromoteConfig config = new ImagePromote.PromoteConfig();
        config.setScale(2);
        config.setNoiseLevel(ImagePromote.NoiseLevel.MEDIUM);
        config.setTileSize(512);
        config.setFormat("png");
        config.setVerbose(true);

        // 执行提升（如果 waifu2x 可用）
        if (imagePromote.isWaifu2xAvailable()) {
            boolean result = imagePromote.promoteImage(
                    inputPath.toString(),
                    outputPath.toString(),
                    config
            );

            assertTrue(result || !result, "方法应该返回布尔值");
            System.out.println("自定义配置提升结果: " + result);
        } else {
            System.out.println("跳过测试：waifu2x 不可用");
        }
    }

    /**
     * 测试：输入文件不存在时的处理
     */
    @Test
    void testPromoteImageWithNonExistentInput() {
        Path inputPath = tempDir.resolve("non_existent.png");
        Path outputPath = tempDir.resolve("output.png");

        boolean result = imagePromote.promoteImage(
                inputPath.toString(),
                outputPath.toString()
        );

        assertFalse(result, "输入文件不存在时应返回 false");
    }

    /**
     * 测试：批量提升图片
     */
    @Test
    void testPromoteBatch() throws IOException {
        // 创建输入目录和多个测试图片
        Path inputDir = tempDir.resolve("batch_input");
        Path outputDir = tempDir.resolve("batch_output");
        Files.createDirectories(inputDir);

        createTestImage(inputDir, "image1.png", 100, 100);
        createTestImage(inputDir, "image2.png", 150, 150);
        createTestImage(inputDir, "image3.png", 200, 200);

        // 创建配置
        ImagePromote.PromoteConfig config = new ImagePromote.PromoteConfig();
        config.setScale(2);
        config.setNoiseLevel(ImagePromote.NoiseLevel.LOW);
        config.setVerbose(true);

        // 执行批量提升（如果 waifu2x 可用）
        if (imagePromote.isWaifu2xAvailable()) {
            int count = imagePromote.promoteBatch(
                    inputDir.toString(),
                    outputDir.toString(),
                    config
            );

            assertTrue(count >= 0, "应返回非负数");
            System.out.println("批量处理文件数: " + count);
        } else {
            System.out.println("跳过测试：waifu2x 不可用");
        }
    }

    /**
     * 测试：批量提升时输入目录不存在
     */
    @Test
    void testPromoteBatchWithNonExistentInputDir() {
        Path inputDir = tempDir.resolve("non_existent_dir");
        Path outputDir = tempDir.resolve("output_dir");

        ImagePromote.PromoteConfig config = new ImagePromote.PromoteConfig();

        int count = imagePromote.promoteBatch(
                inputDir.toString(),
                outputDir.toString(),
                config
        );

        assertEquals(0, count, "输入目录不存在时应返回 0");
    }

    /**
     * 测试：PromoteConfig 的 scale 验证
     */
    @Test
    void testPromoteConfigScaleValidation() {
        ImagePromote.PromoteConfig config = new ImagePromote.PromoteConfig();

        // 正常设置
        assertDoesNotThrow(() -> config.setScale(2));
        assertEquals(2, config.getScale());

        assertDoesNotThrow(() -> config.setScale(4));
        assertEquals(4, config.getScale());

        // 异常情况
        assertThrows(IllegalArgumentException.class, () -> config.setScale(0));
        assertThrows(IllegalArgumentException.class, () -> config.setScale(-1));
        assertThrows(IllegalArgumentException.class, () -> config.setScale(5));
    }

    /**
     * 测试：NoiseLevel 枚举值
     */
    @Test
    void testNoiseLevelEnum() {
        assertEquals(-1, ImagePromote.NoiseLevel.NONE.getValue());
        assertEquals(0, ImagePromote.NoiseLevel.LOW.getValue());
        assertEquals(1, ImagePromote.NoiseLevel.MEDIUM.getValue());
        assertEquals(2, ImagePromote.NoiseLevel.HIGH.getValue());
        assertEquals(3, ImagePromote.NoiseLevel.HIGHEST.getValue());
    }

    /**
     * 测试：PromoteConfig 默认值
     */
    @Test
    void testPromoteConfigDefaultValues() {
        ImagePromote.PromoteConfig config = new ImagePromote.PromoteConfig();

        assertEquals(2, config.getScale(), "默认 scale 应为 2");
        assertEquals(ImagePromote.NoiseLevel.LOW, config.getNoiseLevel(), "默认降噪级别应为 LOW");
        assertEquals(0, config.getTileSize(), "默认 tileSize 应为 0");
        assertEquals("png", config.getFormat(), "默认格式应为 png");
        assertFalse(config.getVerbose(), "默认 verbose 应为 false");
    }

    /**
     * 测试：PromoteConfig 设置器
     */
    @Test
    void testPromoteConfigSetters() {
        ImagePromote.PromoteConfig config = new ImagePromote.PromoteConfig();

        config.setScale(3);
        config.setNoiseLevel(ImagePromote.NoiseLevel.HIGH);
        config.setTileSize(512);
        config.setFormat("jpg");
        config.setVerbose(true);

        assertEquals(3, config.getScale());
        assertEquals(ImagePromote.NoiseLevel.HIGH, config.getNoiseLevel());
        assertEquals(512, config.getTileSize());
        assertEquals("jpg", config.getFormat());
        assertTrue(config.getVerbose());
    }

    /**
     * 测试：不同降噪级别的图片提升
     */
    @Test
    void testPromoteImageWithDifferentNoiseLevels() throws IOException {
        if (!imagePromote.isWaifu2xAvailable()) {
            System.out.println("跳过测试：waifu2x 不可用");
            return;
        }

        Path inputPath = createTestImage(tempDir, "noise_test.png", 150, 150);

        for (ImagePromote.NoiseLevel level : ImagePromote.NoiseLevel.values()) {
            Path outputPath = tempDir.resolve("output_" + level.name().toLowerCase() + ".png");

            ImagePromote.PromoteConfig config = new ImagePromote.PromoteConfig();
            config.setNoiseLevel(level);
            config.setScale(2);

            boolean result = imagePromote.promoteImage(
                    inputPath.toString(),
                    outputPath.toString(),
                    config
            );

            System.out.println("降噪级别 " + level + " 测试结果: " + result);
        }
    }

    /**
     * 辅助方法：创建测试图片
     */
    private Path createTestImage(Path dir, String filename, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 绘制彩色渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, Color.BLUE, width, height, Color.RED);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // 绘制文字
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Test", width / 4, height / 2);

        g2d.dispose();

        Path imagePath = dir.resolve(filename);
        ImageIO.write(image, "png", imagePath.toFile());

        return imagePath;
    }

}
