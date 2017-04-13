package org.nutz.qrcode;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;

import org.nutz.img.Images;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * QRCode 处理器
 *
 * @author ywjno(ywjno.dev@gmail.com)
 */
public final class QRCode {

    /** QRCode 生成器格式 */
    private QRCodeFormat format = null;

    /** 生成的 QRCode 图像对象 */
    private BufferedImage qrcodeImage = null;

    /** 生成的 QRCode 图片文件 */
    private File qrcodeFile = null;

    /**
     * 返回生成的 QRCode 图像对象
     *
     * @return 生成的 QRCode 图像对象
     */
    public BufferedImage getQrcodeImage() {
        return qrcodeImage;
    }

    /**
     * 返回生成的 QRCode 图片文件
     *
     * @return 生成的 QRCode 图片文件
     */
    public File getQrcodeFile() {
        return qrcodeFile;
    }

    private QRCode() {

    }

    /**
     * 使用带默认值的「QRCode 生成器格式」来创建一个 QRCode 处理器。
     *
     * @param content
     *            所要生成 QRCode 的内容
     *
     * @return QRCode 处理器
     */
    public static QRCode NEW(final String content) {
        return NEW(content, QRCodeFormat.NEW());
    }

    /**
     * 使用指定的「QRCode 生成器格式」来创建一个 QRCode 处理器。
     *
     * @param content
     *            所要生成 QRCode 的内容
     * @param format
     *            QRCode 生成器格式
     *
     * @return QRCode 处理器
     */
    public static QRCode NEW(final String content, QRCodeFormat format) {
        QRCode qrcode = new QRCode();
        qrcode.format = format;
        qrcode.qrcodeImage = toQRCode(content, format);
        return qrcode;
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，之后保存到指定的文件中。
     *
     * @param f
     *            指定的文件
     *
     * @return QRCode 处理器
     */
    public QRCode toFile(String f) {
        return toFile(new File(f), this.format.getIcon());
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，之后保存到指定的文件中。
     *
     * @param qrcodeFile
     *            指定的文件
     *
     * @return QRCode 处理器
     */
    public QRCode toFile(File qrcodeFile) {
        return toFile(qrcodeFile, this.format.getIcon());
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，并在该图片中间添加上指定的图片；之后保存到指定的文件内。
     *
     * @param qrcodeFile
     *            QRCode 图片生成的指定的文件
     * @param appendFile
     *            需要添加的图片。传入的文件路径如果没有（null 或者为空）的时候将忽略该参数
     *
     * @return QRCode 处理器
     */
    public QRCode toFile(String qrcodeFile, String appendFile) {
        if (null == appendFile || appendFile.length() == 0) {
            return toFile(new File(qrcodeFile));
        }
        return toFile(new File(qrcodeFile), Images.read(new File(appendFile)));
    }

    public QRCode toFile(File qrcodeFile, File appendFile) {
        return toFile(qrcodeFile, Images.read(appendFile));
    }

    /**
     * 把指定的内容生成为一个 QRCode 的图片，并在该图片中间添加上指定的图片；之后保存到指定的文件内。
     *
     * @param qrcodeFile
     *            QRCode 图片生成的指定的文件
     * @param appendFile
     *            需要添加的图片。传入的图片不存在的时候将忽略该参数
     *
     * @return QRCode 处理器
     */
    public QRCode toFile(File qrcodeFile, BufferedImage appendFile) {
        try {
            if (!qrcodeFile.exists()) {
                qrcodeFile.getParentFile().mkdirs();
                //20170413,ecoolper，注释，解决生成二维码图片失败后，存在0大小的文件
                //qrcodeFile.createNewFile();
            }

            if (null != appendFile) {
                appendImage(appendFile);
            }

            if (!ImageIO.write(this.qrcodeImage, getSuffixName(qrcodeFile), qrcodeFile)) {
                throw new RuntimeException("Unexpected error writing image");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.qrcodeFile = qrcodeFile;
        return this;
    }

    private void appendImage(BufferedImage appendImage) {
        appendImage(this.qrcodeImage, appendImage, this.format);
    }

    private static void appendImage(BufferedImage qrcodeImage,
                                    BufferedImage appendImage,
                                    QRCodeFormat format) {
        int baseWidth = qrcodeImage.getWidth();
        int baseHeight = qrcodeImage.getHeight();

        // 计算 icon 的最大边长
        // 公式为 二维码面积*错误修正等级*0.4 的开方
        int maxWidth = (int) Math.sqrt(baseWidth
                                       * baseHeight
                                       * format.getErrorCorrectionLevelValue()
                                       * 0.4);
        int maxHeight = maxWidth;

        // 获取 icon 的实际边长
        int roundRectWidth = (maxWidth < appendImage.getWidth()) ? maxWidth
                                                                 : appendImage.getWidth();
        int roundRectHeight = (maxHeight < appendImage.getHeight()) ? maxHeight
                                                                    : appendImage.getHeight();

        BufferedImage roundRect = new BufferedImage(roundRectWidth,
                                                    roundRectHeight,
                                                    BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = roundRect.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, roundRectWidth, roundRectHeight, 27, 27);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(appendImage, 0, 0, roundRectWidth, roundRectHeight, null);
        g2.dispose();

        Graphics gc = qrcodeImage.getGraphics();
        gc.setColor(format.getBackGroundColor());
        gc.drawImage(roundRect,
                     (baseWidth - roundRectWidth) / 2,
                     (baseHeight - roundRectHeight) / 2,
                     null);
        gc.dispose();
    }

    /**
     * 使用带默认值的「QRCode 生成器格式」，把指定的内容生成为一个 QRCode 的图像对象。
     *
     * @param content
     *            所需生成 QRCode 的内容
     *
     * @return QRCode 的图像对象
     */
    public static BufferedImage toQRCode(String content) {
        return toQRCode(content, null);
    }

    /**
     * 使用指定的「QRCode生成器格式」，把指定的内容生成为一个 QRCode 的图像对象。
     *
     * @param content
     *            所需生成 QRCode 的内容
     * @param format
     *            QRCode 生成器格式
     * @return QRCode 的图像对象
     */
    public static BufferedImage toQRCode(String content, QRCodeFormat format) {
        if (format == null) {
            format = QRCodeFormat.NEW();
        }

        content = new String(content.getBytes(Charset.forName(format.getEncode())));
        BitMatrix matrix = null;
        try {
            matrix = new QRCodeWriter().encode(content,
                                               BarcodeFormat.QR_CODE,
                                               format.getSize(),
                                               format.getSize(),
                                               format.getHints());
        }
        catch (WriterException e) {
            throw new RuntimeException(e);
        }

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int fgColor = format.getForeGroundColor().getRGB();
        int bgColor = format.getBackGroundColor().getRGB();
        BufferedImage image = new BufferedImage(width, height, ColorSpace.TYPE_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? fgColor : bgColor);
            }
        }
        if (null != format.getIcon()) {
            appendImage(image, format.getIcon(), format);
        }
        return image;
    }

    /**
     * 从指定的 QRCode 图片文件中解析出其内容。
     *
     * @param qrcodeFile
     *            QRCode 文件
     *
     * @return QRCode 中的内容
     */
    public static String from(String qrcodeFile) {
        if (qrcodeFile.startsWith("http://") || qrcodeFile.startsWith("https://")) {
            try {
                return from(new URL(qrcodeFile));
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return from(new File(qrcodeFile));
        }
    }

    /**
     * 从指定的 QRCode 图片文件中解析出其内容。
     *
     * @param qrcodeFile
     *            QRCode 图片文件
     *
     * @return QRCode 中的内容
     */
    public static String from(File qrcodeFile) {
        try {
            BufferedImage image = ImageIO.read(qrcodeFile);
            return from(image);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从指定的 QRCode 图片链接中解析出其内容。
     *
     * @param qrcodeUrl
     *            QRCode 图片链接
     *
     * @return QRCode 中的内容
     */
    public static String from(URL qrcodeUrl) {
        try {
            BufferedImage image = ImageIO.read(qrcodeUrl);
            return from(image);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从指定的 QRCode 图像对象中解析出其内容。
     *
     * @param qrcodeImage
     *            QRCode 图像对象
     *
     * @return QRCode 中的内容
     */
    public static String from(BufferedImage qrcodeImage) {
        LuminanceSource source = new BufferedImageLuminanceSource(qrcodeImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        String content = null;
        try {
            Result result = new QRCodeReader().decode(bitmap);
            content = result.getText();
        }
        catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (ChecksumException e) {
            throw new RuntimeException(e);
        }
        catch (FormatException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    private String getSuffixName(File file) {
        String path = file.getAbsolutePath();

        if (null == path) {
            return this.format.getImageFormat();
        }
        int pos = path.lastIndexOf('.');
        if (-1 == pos) {
            return this.format.getImageFormat();
        }
        return path.substring(pos + 1).toUpperCase();
    }
}
