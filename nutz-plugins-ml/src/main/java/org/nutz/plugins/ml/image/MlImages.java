package org.nutz.plugins.ml.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MlImages {

    public static int[][] toARGB(BufferedImage image, int[][] argb) {
        int w = image.getWidth();
        int h = image.getHeight();
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = dst.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            image = dst;
        }
        if (argb == null)
            argb = new int[image.getWidth()][image.getHeight()];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                argb[i][j] = image.getRGB(i, j);
            }
        }
        return argb;
    }

    /**
     * 将图片变成灰度矩阵
     * @param image 不可以是null
     * @param x 起始位置,x轴,通常是0
     * @param y 起始位置,y轴,通常是0
     * @param w 矩阵宽度,通常是图片宽度
     * @param h 矩阵高度,通常是图片高度
     * @return 灰度矩阵
     */
    public static int[][] toGray(BufferedImage image, int x, int y, int w, int h) {
        int[][] argb = toARGB(image, null);
        int[][] buf = new int[w][h];
        for (int i = x; i < image.getWidth() && i < w; i++) {
            for (int j = y; j < image.getHeight() && j < h; j++) {
                buf[i - x][j - y] = rgb2gray(argb[i][j]);
            }
        }
        return buf;
    }

    /**
     * RGB颜色转灰度值
     */
    public static int rgb2gray(int color) {
        final int r = (color >> 16) & 0xff;
        final int g = (color >> 8) & 0xff;
        final int b = color & 0xff;
        int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
        return gray;
    }

    static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }
    
    /**
     * 计算特定矩阵区域的算术平均值
     * @param gray 矩阵
     * @param x 起始位置,x轴
     * @param y 起始位置,y轴
     * @param w 子矩阵的宽度
     * @param h 子矩阵的高度
     * @return 子矩阵内的算术平均值
     */
    public static int avg(int[][] gray, int x, int y, int w, int h) {
        if (x+w > gray.length || y+h > gray[0].length)
            return 255;
        long count = 0;
        for (int i = x; i < gray.length && i < w+x; i++) {
            for (int j = y; j < gray[0].length && j < h+y; j++) {
                count += gray[i][j];
                //System.out.println(String.format("i=%s,j=%s,gray=%s", i, j,gray[i][j]));
            }
        }
        //System.out.println(String.format("x=%s,y=%s,count=%s,w=%s,h=%s", x, y, count, w, h));
        return (int)(count / (w*h));
    }
    
    /**
     * 按指定的块大小,将矩阵转为算术平均值矩阵
     * @param gray 矩阵数据
     * @param block_w 块的宽度
     * @param block_h 块的高度
     * @return 算术平均值矩阵
     */
    public static int[][] gray_avg(int[][] gray, int block_w, int block_h) {
        int bw = gray.length/block_w;
        int bh = gray[0].length / block_h;
        int[][] gray_avg = new int[bw][bh];
        for (int i = 0; i < gray_avg.length; i++) {
            for (int j = 0; j < gray_avg[0].length; j++) {
                gray_avg[i][j] = MlImages.avg(gray, i*block_w, j*block_h, block_w, block_h);
            }
        }
        return gray_avg;
    }
    
    /**
     * 根据阀值大小,将矩阵变成二值矩阵
     * @param gray_avg 矩阵数据
     * @param gray_min 阀值
     * @return 二值矩阵, 仅有真假两种值
     */
    public static boolean[][] gray_bol(int[][] gray_avg, int gray_min) {
        boolean[][] gray_bol = new boolean[gray_avg.length][gray_avg[0].length];
        for (int i = 0; i < gray_bol.length; i++) {
            for (int j = 0; j < gray_bol[0].length; j++) {
                gray_bol[i][j] = gray_avg[i][j] > gray_min;
            }
        }
        return gray_bol;
    }
    
    /**
     * 根据二值矩阵,从边缘开始查找1值,避开封闭空间内的1值
     * @param gray_bol 矩阵数据
     * @return 连续的二值矩阵
     */
    public static boolean[][] gray_bol_ci(boolean[][] gray_bol) {
        boolean[][] gray_bol_ci = new boolean[gray_bol.length][gray_bol[0].length];
        boolean[][] gray_walked = new boolean[gray_bol.length][gray_bol[0].length];
        // 按4条边走一遍
        for (int i = 0; i < gray_bol.length; i++) {
            for (int j = 0; j < gray_bol[0].length; j++) {
                if ((i == 0 || i == gray_bol.length - 1) || (j == 0 || j == gray_bol[0].length - 1)) {
                    _gray_bol_sell(i, j, gray_bol_ci, gray_walked, gray_bol);
                }
            }
        }
        return gray_bol_ci;
    }
    
    /**
     * 矩阵内的一个点, 判断自身是否true, 并查找4个方向上临近点是否为true
     * @param x 点的x轴
     * @param y 点的y轴
     * @param gray_bol_ci 存放结果的二值矩阵
     * @param gray_walked 存放遍历过程的二值矩阵,用于避开已经判断过的点
     * @param gray_bol 原始的二值矩阵
     */
    public static void _gray_bol_sell(int x, int y, 
                                      boolean[][] gray_bol_ci,
                                      boolean[][] gray_walked,
                                      boolean[][] gray_bol) {
        if (x < 0 || x >= gray_bol.length || y < 0 || y >= gray_bol[0].length)
            return;
        // 如果这个点已经检查过,直接返回
        if (gray_walked[x][y])
            return;
        
        // 标注当前点位已经走过, 无论后面判断啥,否则很容易死循环
        gray_walked[x][y] = true;
        
        // 如果这个点本身就不符合条件,直接返回
        if (!gray_bol[x][y]) {
            return;
        }
        // 既然这个点符合条件,将其设置为true
        gray_bol_ci[x][y] = true;
        
        // 检查这个点四周,共4个点的情况
        /*
           X    
         X 0 X
           X  
         */
        _gray_bol_sell(x+1, y, gray_bol_ci, gray_walked, gray_bol);
        _gray_bol_sell(x-1, y, gray_bol_ci, gray_walked, gray_bol);
        _gray_bol_sell(x, y+1, gray_bol_ci, gray_walked, gray_bol);
        _gray_bol_sell(x, y-1, gray_bol_ci, gray_walked, gray_bol);
    }
    
    public static BufferedImage draw(boolean[][] gray_bol, int[][] gray_avg, int x, int y, int block_w, int block_h, BufferedImage in) {
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(in, null, 0, 0);
        
        for (int i = 0; i < gray_bol.length; i++) {
            for (int j = 0; j < gray_bol[0].length; j++) {
                g2d.setColor(Color.GRAY);
                if (gray_bol[i][j])
                    g2d.fillRect(x+(i*block_w), y+(j*block_h), block_w, block_h);
                g2d.setColor(Color.BLUE);
                if (gray_avg != null)
                    g2d.drawString(""+gray_avg[i][j], x+(i*block_w), y+(j*block_h)+block_w/2);
            }
        }
        g2d.dispose();
        
        return out;
    }
}
