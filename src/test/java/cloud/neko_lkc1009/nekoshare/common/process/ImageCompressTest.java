
package cloud.neko_lkc1009.nekoshare.common.process;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ImageCompress 工具类测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageCompressTest {
    @TempDir
    private Path tempDir;
    private File testImageFile;
    private File outputImageFile;

    /**
     * 创建测试用的图片文件
     */
    @BeforeEach
    void setUp() throws IOException {
        testImageFile = tempDir.resolve("test-image.jpg").toFile();
        outputImageFile = tempDir.resolve("output-image.jpg").toFile();

        // 创建一个测试图片 (2000x1500)
        BufferedImage testImage = new BufferedImage(2000, 1500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = testImage.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 2000, 1500);
        g.setColor(Color.RED);
        g.fillOval(500, 375, 1000, 750);
        g.dispose();

        ImageIO.write(testImage, "jpg", testImageFile);
    }

    @Test
    @Order(1)
    @DisplayName("测试默认配置压缩图片")
    void testCompressWithDefaultConfig() {
        boolean result = ImageCompress.compress(testImageFile, outputImageFile);

        assertTrue(result, "压缩应该成功");
        assertTrue(outputImageFile.exists(), "输出文件应该存在");
        assertTrue(outputImageFile.length() > 0, "输出文件大小应该大于0");
    }

    @Test
    @Order(2)
    @DisplayName("测试自定义压缩质量")
    void testCompressWithCustomQuality() {
        float quality = 0.5f;
        boolean result = ImageCompress.compress(testImageFile, outputImageFile, quality);

        assertTrue(result, "压缩应该成功");
        assertTrue(outputImageFile.exists(), "输出文件应该存在");
        assertTrue(outputImageFile.length() < testImageFile.length(),
                "压缩后文件应该小于原文件");
    }

    @Test
    @Order(3)
    @DisplayName("测试完全自定义压缩参数")
    void testCompressWithFullCustomParams() {
        float quality = 0.6f;
        int maxWidth = 1024;
        int maxHeight = 768;

        boolean result = ImageCompress.compress(testImageFile, outputImageFile,
                quality, maxWidth, maxHeight);

        assertTrue(result, "压缩应该成功");
        assertTrue(outputImageFile.exists(), "输出文件应该存在");

        // 验证图片尺寸
        try {
            BufferedImage outputImage = ImageIO.read(outputImageFile);
            assertTrue(outputImage.getWidth() <= maxWidth,
                    "输出图片宽度应该小于等于最大宽度");
            assertTrue(outputImage.getHeight() <= maxHeight,
                    "输出图片高度应该小于等于最大高度");
        } catch (IOException e) {
            fail("读取输出图片失败: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("测试压缩字节数组")
    void testCompressByteArray() throws IOException {
        byte[] imageBytes = Files.readAllBytes(testImageFile.toPath());

        byte[] compressedBytes = ImageCompress.compress(imageBytes, "jpg");

        assertNotNull(compressedBytes, "压缩结果不应该为null");
        assertTrue(compressedBytes.length > 0, "压缩后的字节数组长度应该大于0");
    }

    @Test
    @Order(5)
    @DisplayName("测试自定义参数压缩字节数组")
    void testCompressByteArrayWithCustomParams() throws IOException {
        byte[] imageBytes = Files.readAllBytes(testImageFile.toPath());
        float quality = 0.7f;
        int maxWidth = 800;
        int maxHeight = 600;

        byte[] compressedBytes = ImageCompress.compress(imageBytes, "jpg",
                quality, maxWidth, maxHeight);

        assertNotNull(compressedBytes, "压缩结果不应该为null");
        assertTrue(compressedBytes.length > 0, "压缩后的字节数组长度应该大于0");

        // 验证压缩后的图片尺寸
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(compressedBytes));
        assertNotNull(image, "从压缩字节数组读取图片应该成功");
        assertTrue(image.getWidth() <= maxWidth, "压缩后图片宽度应该小于等于最大宽度");
        assertTrue(image.getHeight() <= maxHeight, "压缩后图片高度应该小于等于最大高度");
    }

    @Test
    @Order(6)
    @DisplayName("测试按比例压缩")
    void testCompressByScale() {
        float scale = 0.5f;
        boolean result = ImageCompress.compressByScale(testImageFile, outputImageFile, scale);

        assertTrue(result, "按比例压缩应该成功");
        assertTrue(outputImageFile.exists(), "输出文件应该存在");

        try {
            BufferedImage originalImage = ImageIO.read(testImageFile);
            BufferedImage outputImage = ImageIO.read(outputImageFile);

            assertEquals((int) (originalImage.getWidth() * scale), outputImage.getWidth(),
                    "输出图片宽度应该是原图的" + scale + "倍");
            assertEquals((int) (originalImage.getHeight() * scale), outputImage.getHeight(),
                    "输出图片高度应该是原图的" + scale + "倍");
        } catch (IOException e) {
            fail("读取图片失败: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("测试根据文件大小自动判断是否压缩")
    void testCompressIfNeeded() {
        boolean result = ImageCompress.compressIfNeeded(testImageFile, outputImageFile);

        assertTrue(result, "处理应该成功");
        assertTrue(outputImageFile.exists(), "输出文件应该存在");
    }

    @Test
    @Order(8)
    @DisplayName("测试PNG格式压缩")
    void testCompressPngFormat() throws IOException {
        File pngFile = tempDir.resolve("test-image.png").toFile();
        File outputPngFile = tempDir.resolve("output-image.png").toFile();

        // 创建PNG测试图片
        BufferedImage testImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = testImage.createGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 800, 600);
        g.dispose();
        ImageIO.write(testImage, "png", pngFile);

        boolean result = ImageCompress.compress(pngFile, outputPngFile);

        assertTrue(result, "PNG格式压缩应该成功");
        assertTrue(outputPngFile.exists(), "输出文件应该存在");
    }

    @Test
    @Order(9)
    @DisplayName("测试获取图片信息")
    void testGetImageInfo() {
        String info = ImageCompress.getImageInfo(testImageFile);

        assertNotNull(info, "图片信息不应该为null");
        assertTrue(info.contains("文件大小"), "应该包含文件大小信息");
        assertTrue(info.contains("宽度"), "应该包含宽度信息");
        assertTrue(info.contains("高度"), "应该包含高度信息");
        assertTrue(info.contains("格式"), "应该包含格式信息");
    }

    @Test
    @Order(10)
    @DisplayName("测试压缩不存在的文件")
    void testCompressNonExistentFile() {
        File nonExistentFile = tempDir.resolve("non-existent.jpg").toFile();

        boolean result = ImageCompress.compress(nonExistentFile, outputImageFile);

        assertFalse(result, "压缩不存在的文件应该失败");
    }

    @Test
    @Order(11)
    @DisplayName("测试压缩无效的图片数据")
    void testCompressInvalidImageData() {
        byte[] invalidBytes = "This is not an image".getBytes();

        byte[] result = ImageCompress.compress(invalidBytes, "jpg");

        assertNull(result, "压缩无效图片数据应该返回null");
    }

    @Test
    @Order(12)
    @DisplayName("测试小图片不需要缩放")
    void testCompressSmallImage() throws IOException {
        // 创建一个小图片 (500x400)
        File smallImageFile = tempDir.resolve("small-image.jpg").toFile();
        BufferedImage smallImage = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = smallImage.createGraphics();
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 500, 400);
        g.dispose();
        ImageIO.write(smallImage, "jpg", smallImageFile);

        boolean result = ImageCompress.compress(smallImageFile, outputImageFile);

        assertTrue(result, "压缩应该成功");

        BufferedImage outputImage = ImageIO.read(outputImageFile);
        assertEquals(500, outputImage.getWidth(), "小图片宽度应该保持不变");
        assertEquals(400, outputImage.getHeight(), "小图片高度应该保持不变");
    }

    @Test
    @Order(13)
    @DisplayName("测试压缩质量边界值")
    void testCompressQualityBoundary() {
        // 测试最低质量
        boolean resultMin = ImageCompress.compress(testImageFile, outputImageFile, 0.1f);
        assertTrue(resultMin, "最低质量压缩应该成功");

        // 测试最高质量
        File outputMaxQuality = tempDir.resolve("output-max-quality.jpg").toFile();
        boolean resultMax = ImageCompress.compress(testImageFile, outputMaxQuality, 1.0f);
        assertTrue(resultMax, "最高质量压缩应该成功");
    }

    @Test
    @Order(14)
    @DisplayName("测试压缩保持宽高比")
    void testCompressKeepsAspectRatio() throws IOException {
        // 创建一个宽高比为 4:3 的图片 (2400x1800)
        File wideImageFile = tempDir.resolve("wide-image.jpg").toFile();
        BufferedImage wideImage = new BufferedImage(2400, 1800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = wideImage.createGraphics();
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, 2400, 1800);
        g.dispose();
        ImageIO.write(wideImage, "jpg", wideImageFile);

        boolean result = ImageCompress.compress(wideImageFile, outputImageFile,
                0.75f, 1920, 1080);

        assertTrue(result, "压缩应该成功");

        BufferedImage outputImage = ImageIO.read(outputImageFile);
        double originalRatio = 2400.0 / 1800.0;
        double outputRatio = (double) outputImage.getWidth() / outputImage.getHeight();

        assertEquals(originalRatio, outputRatio, 0.01,
                "压缩后应该保持原始宽高比");
    }

    @Test
    @Order(15)
    @DisplayName("测试获取不存在文件的图片信息")
    void testGetImageInfoNonExistentFile() {
        File nonExistentFile = tempDir.resolve("non-existent.jpg").toFile();

        String info = ImageCompress.getImageInfo(nonExistentFile);

        assertNotNull(info, "即使文件不存在，也应该返回信息");
        assertTrue(info.contains("失败") || info.contains("无法"),
                "应该包含失败或无法读取的提示");
    }
}