package org.nutz.qrcode;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import org.nutz.img.Images;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * QRCode 生成器的格式
 * 
 * @author ywjno(ywjno.dev@gmail.com)
 */
public class QRCodeFormat {

    /** 图片大小 */
    private int size;

    /** 内容编码格式 */
    private String encode;

    /** 错误修正等级 (Error Collection Level) */
    private ErrorCorrectionLevel errorCorrectionLevel;

    /** 错误修正等级的具体值 */
    private double errorCorrectionLevelValue;

    /** 前景色 */
    private Color foreGroundColor;

    /** 背景色 */
    private Color backGroundColor;

    /** 图片的文件格式 */
    private String imageFormat;

    /** 图片的外边距大小 (Quiet Zone) */
    private int margin;

    /** 提供给编码器额外的参数 */
    private Hashtable<EncodeHintType, Object> hints;

    /** 需要添加的图片 */
    private BufferedImage icon;

    /**
     * 创建一个带有默认值的 QRCode 生成器的格式。默认值如下
     * 
     * <ul>
     * <li>图片大小: 256px</li>
     * <li>内容编码格式: UTF-8</li>
     * <li>错误修正等级: Level M (有15% 的内容可被修正)</li>
     * <li>前景色: 黑色</li>
     * <li>背景色: 白色</li>
     * <li>输出图片的文件格式: png</li>
     * <li>图片空白区域大小: 0个单位</li>
     * </ul>
     * 
     * @return QRCode 生成器格式
     */
    public static QRCodeFormat NEW() {
        return new QRCodeFormat();
    }

    private QRCodeFormat() {
        this.size = 256;
        this.encode = "utf-8";
        this.errorCorrectionLevel = ErrorCorrectionLevel.M;
        this.errorCorrectionLevelValue = 0.15;
        this.foreGroundColor = Color.BLACK;
        this.backGroundColor = Color.WHITE;
        this.imageFormat = "png";
        this.margin = 0;
        this.hints = new Hashtable<EncodeHintType, Object>();
    }

    /**
     * 返回图片的大小。
     * 
     * @return 图片的大小
     */
    public int getSize() {
        return this.size;
    }

    /**
     * 设置图片的大小。图片的大小等于实际内容与外边距的值（建议设置成偶数值）。
     * 
     * @param size
     *            图片的大小
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * 返回内容编码格式。
     * 
     * @return 内容编码格式
     */
    public String getEncode() {
        return encode;
    }

    /**
     * 设置内容编码格式。
     * 
     * @param encode
     *            内容编码格式
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setEncode(String encode) {
        if (null != encode) {
            this.encode = encode.toLowerCase();
        }
        return this;
    }

    /**
     * 返回错误修正等级。
     * 
     * @return 错误修正等级
     */
    public ErrorCorrectionLevel getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    /**
     * 返回错误修正等级的具体值。
     * 
     * @return 错误修正等级的具体值
     */
    public double getErrorCorrectionLevelValue() {
        return errorCorrectionLevelValue;
    }

    /**
     * 设置错误修正等级。其定义如下
     * 
     * <ul>
     * <li>L: 有 7% 的内容可被修正</li>
     * <li>M: 有15% 的内容可被修正</li>
     * <li>Q: 有 25% 的内容可被修正</li>
     * <li>H: 有 30% 的内容可被修正</li>
     * </ul>
     * 
     * @param errorCorrectionLevel
     *            错误修正等级
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setErrorCorrectionLevel(char errorCorrectionLevel) {
        switch (Character.toUpperCase(errorCorrectionLevel)) {
        case 'L':
            this.errorCorrectionLevel = ErrorCorrectionLevel.L;
            this.errorCorrectionLevelValue = 0.07;
            break;
        case 'M':
            this.errorCorrectionLevel = ErrorCorrectionLevel.M;
            this.errorCorrectionLevelValue = 0.15;
            break;
        case 'Q':
            this.errorCorrectionLevel = ErrorCorrectionLevel.Q;
            this.errorCorrectionLevelValue = 0.25;
            break;
        case 'H':
            this.errorCorrectionLevel = ErrorCorrectionLevel.H;
            this.errorCorrectionLevelValue = 0.3;
            break;
        default:
            this.errorCorrectionLevel = ErrorCorrectionLevel.M;
        }

        return this;
    }

    /**
     * 返回前景色。
     * 
     * @return 前景色
     */
    public Color getForeGroundColor() {
        return foreGroundColor;
    }

    /**
     * 设置前景色。值为十六进制的颜色值（与 CSS 定义颜色的值相同，不支持简写），可以忽略「#」符号。
     * 
     * @param foreGroundColor
     *            前景色的值
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setForeGroundColor(String foreGroundColor) {
        try {
            this.foreGroundColor = getColor(foreGroundColor);
        }
        catch (NumberFormatException e) {
            this.foreGroundColor = Color.BLACK;
        }
        return this;
    }

    /**
     * 设置前景色。
     * 
     * @param foreGroundColor
     *            前景色的值
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setForeGroundColor(Color foreGroundColor) {
        this.foreGroundColor = foreGroundColor;
        return this;
    }

    /**
     * 返回背景色。
     * 
     * @return 背景色
     */
    public Color getBackGroundColor() {
        return backGroundColor;
    }

    /**
     * 设置背景色。值为十六进制的颜色值（与 CSS 定义颜色的值相同，不支持简写），可以忽略「#」符号。
     * 
     * @param backGroundColor
     *            前景色的值
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setBackGroundColor(String backGroundColor) {
        try {
            this.backGroundColor = getColor(backGroundColor);
        }
        catch (NumberFormatException e) {
            this.backGroundColor = Color.WHITE;
        }
        return this;
    }

    /**
     * 设置背景色。
     * 
     * @param backGroundColor
     *            前景色的值
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setBackGroundColor(Color backGroundColor) {
        this.backGroundColor = backGroundColor;
        return this;
    }

    /**
     * 返回图片的文件格式。
     * 
     * @return 图片的文件格式
     */
    public String getImageFormat() {
        return imageFormat.toUpperCase();
    }

    /**
     * 设置图片的文件格式 。
     * 
     * @param imageFormat
     *            图片的文件格式
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    /**
     * 返回图片的外边距大小。
     * 
     * @return 图片的外边距大小
     */
    public int getMargin() {
        return margin;
    }

    /**
     * 设置图片的外边距大小 。
     * 
     * @param margin
     *            图片的外边距大小
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setMargin(int margin) {
        this.margin = margin;
        return this;
    }

    /**
     * 返回提供给编码器额外的参数。
     * 
     * @return 提供给编码器额外的参数
     */
    public Hashtable<EncodeHintType, ?> getHints() {
        hints.clear();
        hints.put(EncodeHintType.ERROR_CORRECTION, getErrorCorrectionLevel());
        hints.put(EncodeHintType.CHARACTER_SET, getEncode());
        hints.put(EncodeHintType.MARGIN, getMargin());
        return hints;
    }

    /**
     * 返回添加的图片。
     * 
     * @return 添加的图片
     */
    protected BufferedImage getIcon() {
        return icon;
    }

    public QRCodeFormat setIcon(BufferedImage icon) {
        this.icon = icon;
        return this;
    }

    /**
     * 设置添加的图片 。
     * 
     * @param icon
     *            添加的图片
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setIcon(File icon) {
        this.icon = Images.read(icon);
        return this;
    }

    /**
     * 设置添加的图片 。
     * 
     * @param iconPath
     *            添加的图片
     * 
     * @return QRCode生成器的格式
     */
    public QRCodeFormat setIcon(String iconPath) {
        return setIcon(new File(iconPath));
    }

    private Color getColor(String hexString) {
        if (hexString.charAt(0) == '#') {
            return new Color(Long.decode(hexString).intValue());
        } else {
            return new Color(Long.decode("0xFF" + hexString).intValue());
        }
    }
}
