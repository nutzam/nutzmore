package org.nutz.plugins.ml.image;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.nutz.img.Images;

public class MlImagesTest {

//    @Test
//    public void testToGray() {
//        InputStream ins = getClass().getClassLoader().getResourceAsStream("snap.jpg");
//        BufferedImage image = Images.read(ins);
//        assertNotNull(image);
//        int[][] gray = MlImages.toGray(image, 0, 0, image.getWidth(), image.getHeight());
////        System.out.println(Json.toJson(gray));
//        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//        for (int i = 0; i < gray.length; i++) {
//            for (int j = 0; j < gray[0].length; j++) {
//                out.setRGB(i, j, MlImages.colorToRGB(255, gray[i][j], gray[i][j], gray[i][j]));
//            }
//        }
//        File tmp = new File("out.png");
//        System.out.println(tmp.getAbsolutePath());
//        Images.write(out, tmp);
//    }

    @Test
    public void test_999() {
        // 定义块大小
        int block_w = 32;
        int block_h = 32;
        // 定义阀值, 数字越大,代表越白
        int gray_min = 248;
        // 读取图片
        InputStream ins = getClass().getClassLoader().getResourceAsStream("snap.jpg");
        BufferedImage image = Images.read(ins);
        assertNotNull(image);
        // 全图转灰度
        int[][] gray = MlImages.toGray(image, 0, 0, image.getWidth(), image.getHeight());
        // 按块大小计算块内的平均灰度
        int[][] gray_avg = MlImages.gray_avg(gray, block_w, block_h);
        // 根据阀值输出0/1矩阵
        boolean[][] gray_bol = MlImages.gray_bol(gray_avg, gray_min);
        
        // 直接标注空白区域,输出成图片, debug用
        BufferedImage out = MlImages.draw(gray_bol, gray_avg, 0, 0, block_w, block_h, image);
        Images.write(out, new File("out_color_bol.png"));
        
        // 标注连续的空白区域,输出成图片, debug用
        out = MlImages.draw(MlImages.gray_bol_ci(gray_bol), gray_avg, 0, 0, block_w, block_h, image);
        Images.write(out, new File("out_color_bol_ci.png"));

        // 直接标注空白区域,并且转为灰度
        //out = MlImages.draw(gray_bol, gray_avg, 0, 0, block_w, block_h, Images.read(new File("out.png")));
        //Images.write(out, new File("out_gray_bol.png"));
        
        // 标注连续的空白区域,并且转为灰度
        //out = MlImages.draw(MlImages.gray_bol_ci(gray_bol), gray_avg, 0, 0, block_w, block_h, Images.read(new File("out.png")));
        //Images.write(out, new File("out_gray_bol_ci.png"));
    }
    
//    @Test
//    public void xxx() {
//        InputStream ins = getClass().getClassLoader().getResourceAsStream("snap.jpg");
//        BufferedImage image = Images.read(ins);
//        assertNotNull(image);
//        int[][] gray = MlImages.toGray(image, 0, 0, image.getWidth(), image.getHeight());
//        int block_w = 32;
//        int block_h = 32;
//        BufferedImage block = new BufferedImage(32, 32, BufferedImage.TYPE_3BYTE_BGR);
//        for (int i = 0; i < 32; i++) {
//            for (int j = 0; j < 32; j++) {
//                block.setRGB(i, j, image.getRGB(i+160, j+160));
//            }
//        }
//        Images.write(block, new File("block.png"));
//        int avg = MlImages.avg(gray, 160, 160, block_w, block_h);
//        System.out.println(avg);
//    }
}
