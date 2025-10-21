package cloud.neko_lkc1009.nekoshare.common.process;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * 图片压缩工具类
 * 支持可选择性压缩和默认压缩配置
 */
@Slf4j
public class ImageCompress {
    /**
     * 默认压缩质量 (0.0-1.0)
     */
    private static final float DEFAULT_QUALITY = 0.75f;

    /**
     * 默认最大宽度 (像素)
     */
    private static final int DEFAULT_MAX_WIDTH = 1920;

    /**
     * 默认最大高度 (像素)
     */
    private static final int DEFAULT_MAX_HEIGHT = 1080;

    /**
     * 默认文件大小限制 (字节) - 5MB
     */
    private static final long DEFAULT_MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 使用默认配置压缩图片
     *
     * @param sourceFile 源图片文件
     * @param targetFile 目标图片文件
     * @return 是否压缩成功
     */
    public static boolean compress(File sourceFile, File targetFile) {
        return compress(sourceFile, targetFile, DEFAULT_QUALITY, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
    }

    /**
     * 使用默认配置压缩图片 (字节数组)
     *
     * @param imageBytes 源图片字节数组
     * @param formatName 图片格式 (jpg, png等)
     * @return 压缩后的字节数组
     */
    public static byte[] compress(byte[] imageBytes, String formatName) {
        return compress(imageBytes, formatName, DEFAULT_QUALITY, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
    }

    /**
     * 根据文件大小自动判断是否需要压缩
     *
     * @param sourceFile 源图片文件
     * @param targetFile 目标图片文件
     * @return 是否压缩成功
     */
    public static boolean compressIfNeeded(File sourceFile, File targetFile) {
        if (sourceFile.length() > DEFAULT_MAX_FILE_SIZE) {
            log.info("文件大小超过限制，开始压缩: {} bytes", sourceFile.length());
            return compress(sourceFile, targetFile);
        } else {
            log.info("文件大小未超过限制，跳过压缩: {} bytes", sourceFile.length());
            try {
                copyFile(sourceFile, targetFile);
                return true;
            } catch (IOException e) {
                log.error("文件复制失败", e);
                return false;
            }
        }
    }

    /**
     * 自定义压缩质量的压缩方法
     *
     * @param sourceFile 源图片文件
     * @param targetFile 目标图片文件
     * @param quality    压缩质量 (0.0-1.0)
     * @return 是否压缩成功
     */
    public static boolean compress(File sourceFile, File targetFile, float quality) {
        return compress(sourceFile, targetFile, quality, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
    }

    /**
     * 完全自定义的压缩方法
     *
     * @param sourceFile 源图片文件
     * @param targetFile 目标图片文件
     * @param quality    压缩质量 (0.0-1.0)
     * @param maxWidth   最大宽度
     * @param maxHeight  最大高度
     * @return 是否压缩成功
     */
    public static boolean compress(File sourceFile, File targetFile, float quality, int maxWidth, int maxHeight) {
        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(targetFile)) {

            BufferedImage sourceImage = ImageIO.read(inputStream);
            if (sourceImage == null) {
                log.error("无法读取图片文件: {}", sourceFile.getAbsolutePath());
                return false;
            }

            // 计算压缩后的尺寸
            int[] newSize = calculateNewSize(sourceImage.getWidth(), sourceImage.getHeight(), maxWidth, maxHeight);

            // 缩放图片
            BufferedImage scaledImage = scaleImage(sourceImage, newSize[0], newSize[1]);

            // 获取图片格式
            String formatName = getImageFormat(sourceFile.getName());

            // 压缩并写入文件
            return compressAndWrite(scaledImage, outputStream, formatName, quality);

        } catch (IOException e) {
            log.error("图片压缩失败: {}", sourceFile.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * 压缩字节数组格式的图片
     *
     * @param imageBytes 源图片字节数组
     * @param formatName 图片格式
     * @param quality    压缩质量 (0.0-1.0)
     * @param maxWidth   最大宽度
     * @param maxHeight  最大高度
     * @return 压缩后的字节数组
     */
    public static byte[] compress(byte[] imageBytes, String formatName, float quality, int maxWidth, int maxHeight) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BufferedImage sourceImage = ImageIO.read(inputStream);
            if (sourceImage == null) {
                log.error("无法读取图片字节数组");
                return null;
            }

            // 计算压缩后的尺寸
            int[] newSize = calculateNewSize(sourceImage.getWidth(), sourceImage.getHeight(), maxWidth, maxHeight);

            // 缩放图片
            BufferedImage scaledImage = scaleImage(sourceImage, newSize[0], newSize[1]);

            // 压缩并写入流
            if (compressAndWrite(scaledImage, outputStream, formatName, quality)) {
                return outputStream.toByteArray();
            }
            return null;

        } catch (IOException e) {
            log.error("图片压缩失败", e);
            return null;
        }
    }

    /**
     * 按比例压缩图片
     *
     * @param sourceFile 源图片文件
     * @param targetFile 目标图片文件
     * @param scale      缩放比例 (0.0-1.0)
     * @return 是否压缩成功
     */
    public static boolean compressByScale(File sourceFile, File targetFile, float scale) {
        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(targetFile)) {

            BufferedImage sourceImage = ImageIO.read(inputStream);
            if (sourceImage == null) {
                log.error("无法读取图片文件: {}", sourceFile.getAbsolutePath());
                return false;
            }

            int newWidth = (int) (sourceImage.getWidth() * scale);
            int newHeight = (int) (sourceImage.getHeight() * scale);

            BufferedImage scaledImage = scaleImage(sourceImage, newWidth, newHeight);

            String formatName = getImageFormat(sourceFile.getName());
            return compressAndWrite(scaledImage, outputStream, formatName, DEFAULT_QUALITY);

        } catch (IOException e) {
            log.error("图片按比例压缩失败: {}", sourceFile.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * 计算新的图片尺寸（保持宽高比）
     *
     * @param originalWidth  原始宽度
     * @param originalHeight 原始高度
     * @param maxWidth       最大宽度
     * @param maxHeight      最大高度
     * @return 新的尺寸 [宽度, 高度]
     */
    private static int[] calculateNewSize(int originalWidth, int originalHeight, int maxWidth, int maxHeight) {
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // 如果图片尺寸超过最大限制，按比例缩小
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);

            newWidth = (int) (originalWidth * ratio);
            newHeight = (int) (originalHeight * ratio);
        }

        return new int[]{newWidth, newHeight};
    }

    /**
     * 缩放图片
     *
     * @param sourceImage  源图片
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @return 缩放后的图片
     */
    private static BufferedImage scaleImage(BufferedImage sourceImage, int targetWidth, int targetHeight) {
        // 如果尺寸相同，直接返回原图
        if (sourceImage.getWidth() == targetWidth && sourceImage.getHeight() == targetHeight) {
            return sourceImage;
        }

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaledImage.createGraphics();

        // 设置高质量渲染
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawImage(sourceImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        return scaledImage;
    }

    /**
     * 压缩并写入图片
     *
     * @param image        待压缩的图片
     * @param outputStream 输出流
     * @param formatName   图片格式
     * @param quality      压缩质量 (0.0-1.0)
     * @return 是否成功
     */
    private static boolean compressAndWrite(BufferedImage image, OutputStream outputStream,
                                            String formatName, float quality) throws IOException {
        // PNG格式不支持质量压缩，直接写入
        if ("png".equalsIgnoreCase(formatName)) {
            return ImageIO.write(image, formatName, outputStream);
        }

        // 对于JPG等格式，使用质量压缩
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
        if (!writers.hasNext()) {
            log.error("不支持的图片格式: {}", formatName);
            return false;
        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
            return true;
        }
    }

    /**
     * 获取图片格式
     *
     * @param fileName 文件名
     * @return 图片格式
     */
    private static String getImageFormat(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        // 将jpeg统一转换为jpg
        return "jpeg".equals(extension) ? "jpg" : extension;
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     */
    private static void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    /**
     * 获取图片信息
     *
     * @param imageFile 图片文件
     * @return 图片信息字符串
     */
    public static String getImageInfo(File imageFile) {
        try (InputStream inputStream = new FileInputStream(imageFile)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return "无法读取图片信息";
            }
            return String.format("文件大小: %d bytes, 宽度: %d, 高度: %d, 格式: %s",
                    imageFile.length(),
                    image.getWidth(),
                    image.getHeight(),
                    getImageFormat(imageFile.getName()));
        } catch (IOException e) {
            log.error("获取图片信息失败", e);
            return "获取图片信息失败: " + e.getMessage();
        }
    }

}
