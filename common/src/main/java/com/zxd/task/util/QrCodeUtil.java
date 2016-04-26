package com.zxd.task.util;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成二维码工具
 * Created by zxd on 16/2/27.
 */
public class QrCodeUtil {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成二维码矩阵
     */
    public BitMatrix generateQRCode(String content, int width, int height) {
        BitMatrix bitMatrix = null;
        try {
            if ("".equals(content)) {
                content = "http://www.kaola.com";
            }
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitMatrix;
    }

    /**
     * 返回二维码Image
     *
     * @param content content
     * @param width   width
     * @param height  height
     * @return
     */
    public BufferedImage getQRCodeImage(String content, int width, int height) {
        BitMatrix matrix = generateQRCode(content, width, height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 生成指定格式的二维码文件
     *
     * @param content  content
     * @param width    width
     * @param height   height
     * @param format   format
     * @param filePath filePath
     * @throws IOException
     */
    public void generateQRCodeToFile(String content, int width, int height, String format, String filePath) throws IOException {
        File file = new File(filePath);
        BufferedImage image = getQRCodeImage(content, width, height);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    /**
     * 生成指定格式的二维码，并传递给输出流
     *
     * @param content content
     * @param width   width
     * @param height  height
     * @param format  format
     * @param stream  stream
     * @throws IOException
     */
    public void generateQRCodeToStrem(String content, int width, int height, String format, OutputStream stream) throws IOException {
        BufferedImage image = getQRCodeImage(content, width, height);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    /**
     * 生成指定格式的二维码，并传递给输出流
     *
     * @param content content
     * @param width   width
     * @param height  height
     * @param format  format
     * @throws IOException
     */
    public byte[] generateQRCode2Byte(String content, int width, int height, String format) throws IOException {
        BufferedImage image = getQRCodeImage(content, width, height);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
