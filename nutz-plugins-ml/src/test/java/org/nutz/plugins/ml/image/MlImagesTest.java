package org.nutz.plugins.ml.image;

import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.nutz.img.Images;
import org.nutz.lang.Files;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.plugins.ml.image.bean.ImageFrame;
import org.nutz.plugins.ml.image.bean.SubImage;

public class MlImagesTest {

    // @Test
    // public void testToGray() {
    // InputStream ins =
    // getClass().getClassLoader().getResourceAsStream("snap.jpg");
    // BufferedImage image = Images.read(ins);
    // assertNotNull(image);
    // int[][] gray = MlImages.toGray(image, 0, 0, image.getWidth(),
    // image.getHeight());
    //// System.out.println(Json.toJson(gray));
    // BufferedImage out = new BufferedImage(image.getWidth(),
    // image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    // for (int i = 0; i < gray.length; i++) {
    // for (int j = 0; j < gray[0].length; j++) {
    // out.setRGB(i, j, MlImages.colorToRGB(255, gray[i][j], gray[i][j],
    // gray[i][j]));
    // }
    // }
    // File tmp = new File("out.png");
    // System.out.println(tmp.getAbsolutePath());
    // Images.write(out, tmp);
    // }

    @Test
    public void test_999() {
        // 定义块大小
        int block_w = 32;
        int block_h = 32;
        // 定义阀值, 数字越大,代表越白
        int gray_min = 225;
        // 读取图片
        InputStream ins = getClass().getClassLoader().getResourceAsStream("id-49jmldjau2h1do5j1ot7m55n9r.jpg");
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
        out = MlImages.draw(MlImages.gray_bol_ci(gray_bol),
                            gray_avg,
                            0,
                            0,
                            block_w,
                            block_h,
                            image);
        Images.write(out, new File("out_color_bol_ci.png"));

//        // 直接标注空白区域,并且转为灰度
//         out = MlImages.draw(gray_bol, gray_avg, 0, 0, block_w, block_h,
//         Images.read(new File("out.png")));
//         Images.write(out, new File("out_gray_bol.png"));
//
//        // 标注连续的空白区域,并且转为灰度
//         out = MlImages.draw(MlImages.gray_bol_ci(gray_bol), gray_avg, 0, 0,
//         block_w, block_h, Images.read(new File("out.png")));
//         Images.write(out, new File("out_gray_bol_ci.png"));
    }

    @Test
    public synchronized void test_dir() throws IOException {
        // 定义块大小
        int block_w = 8;
        int block_h = 8;
        // 定义阀值, 数字越大,代表越白
        int gray_min = 248;
        String sourceDir = "E:\\ximage\\images";
        String targetDir = "E:\\ximage\\images_out";
        Files.createDirIfNoExists(targetDir);
        Map<String, BufferedImage> map = new LinkedHashMap<>();
        Disks.visitFile(sourceDir, null, false, new FileVisitor() {
            public void visit(File file) {
                // 读取图片
                try {
                    if (map.size() == 100)
                        return;
                    BufferedImage image = Images.read(file);
                    if (image != null)
                        map.put(file.getAbsolutePath(), image);
                }
                catch (OutOfMemoryError e) {
                    // throw new ExitLoop();
                }
            }
        });
        Stopwatch sw = Stopwatch.begin();
        for (Entry<String, BufferedImage> en : map.entrySet()) {
            File file = new File(en.getKey());
            BufferedImage image = en.getValue();

            // 全图转灰度
            int[][] gray = MlImages.toGray(image, 0, 0, image.getWidth(), image.getHeight());
            // 按块大小计算块内的平均灰度
            int[][] gray_avg = MlImages.gray_avg(gray, block_w, block_h);
            // 根据阀值输出0/1矩阵
            boolean[][] gray_bol = MlImages.gray_bol(gray_avg, gray_min);
            gray_bol = MlImages.gray_bol_ci(gray_bol);

            // //直接标注空白区域,输出成图片, debug用
            // BufferedImage out = MlImages.draw(gray_bol, gray_avg, 0, 0,
            // block_w, block_h, image);
            // Images.write(out, new File("E:\\tmp3\\" + file.getName()));
            //
            // BufferedImage bout = new BufferedImage(image.getWidth()*2,
            // image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            // Graphics2D g2d = bout.createGraphics();
            // g2d.drawImage(image, 0, 0, null);
            // g2d.drawImage(out, out.getWidth(), 0, null);
            // g2d.dispose();
            //
            // Images.write(bout, new File("E:\\tmp4\\" + file.getName()));

            List<SubImage> subs = MlImages.getSubImages(image, gray_bol, block_w, block_h);
            if (subs.size() > 0) {
                BufferedImage bm = new BufferedImage(image.getWidth(),
                                                     image.getHeight(),
                                                     BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g2d = bm.createGraphics();
                g2d.drawImage(image, 0, 0, null);
                g2d.setColor(Color.BLUE);
                for (int i = 0; i < subs.size(); i++) {
                    SubImage sub = subs.get(i);
                    // System.out.println(Json.toJson(sub));
                    int x = sub.x_top * block_w;
                    int y = sub.y_top * block_h;
                    BufferedImage tmpi = image.getSubimage(x,
                                                           y,
                                                           (sub.x_bottom - sub.x_top) * block_w,
                                                           (sub.y_bottom - sub.y_top) * block_h);
                    // 长宽起码64个像素, 而且不能靠近边缘
                    if (tmpi.getWidth() > 64 && tmpi.getHeight() > 64 && sub.x_top > 1 && sub.x_bottom < (image.getWidth()/block_w) - 1) {
                        File tmp = Files.createFileIfNoExists(targetDir
                                                              + "_object\\"
                                                              + Files.getMajorName(file)
                                                              + "_"
                                                              + i
                                                              + ".png");
                        Images.write(tmpi, tmp);
                        g2d.drawRect(x, y, tmpi.getWidth(), tmpi.getHeight());
                        g2d.drawString(tmpi.getWidth() + "x" + tmpi.getHeight(), x, y);
                    }
                }
                System.out.println("count=" + subs.size());
                Images.write(bm, new File(targetDir + "\\" + Files.getMajorName(file) + ".png"));
            }
        }
        sw.stop();
        System.out.println(sw);
        System.out.println(map.size());
    }

    // @Test
    // public void xxx() {
    // InputStream ins =
    // getClass().getClassLoader().getResourceAsStream("snap.jpg");
    // BufferedImage image = Images.read(ins);
    // assertNotNull(image);
    // int[][] gray = MlImages.toGray(image, 0, 0, image.getWidth(),
    // image.getHeight());
    // int block_w = 32;
    // int block_h = 32;
    // BufferedImage block = new BufferedImage(32, 32,
    // BufferedImage.TYPE_3BYTE_BGR);
    // for (int i = 0; i < 32; i++) {
    // for (int j = 0; j < 32; j++) {
    // block.setRGB(i, j, image.getRGB(i+160, j+160));
    // }
    // }
    // Images.write(block, new File("block.png"));
    // int avg = MlImages.avg(gray, 160, 160, block_w, block_h);
    // System.out.println(avg);
    // }

    @Test
    public void test_video_pic_create() throws IOException {
        String source = "E:\\ximage\\base\\";
        String target = "E:\\ximage\\seqs\\";
        ImageTrace trace = new ImageTrace();
        // 创建图片缓存
        List<BufferedImage> images = new ArrayList<>();
        // 读取图片
        for (File file : new File(source).listFiles()) {
            images.add(Images.read(file));
        }
        // 打乱顺序
        // Collections.shuffle(images);
        int w = 1024;
        int h = 640;
        int time_limit = 6;
        int fps = 24;
        int count = time_limit * fps * images.size();
        int fps_move = (w / fps / time_limit);
        int[] offset = new int[images.size()];
        for (int i = 0; i < offset.length; i++) {
            if (i == 0)
                offset[i] = w - 7; // 随便定义个初始位移
            else {
                offset[i] = offset[i - 1] + images.get(i - 1).getWidth() + 133;
            }
        }
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        int out_count = 0;
        OUT: for (int i = 0; i < count; i++) {
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, w, h);
            for (int j = 0; j < images.size(); j++) {
                int of = offset[j] - i * fps_move;
                BufferedImage objImg = images.get(j);
                // System.out.println("of=" + of);
                if (of + objImg.getWidth() < 0) {
                    if (j == images.size() - 1) {
                        break OUT;
                    }
                    continue;
                }
                if (of > w)
                    continue;
                int x = of;
                int y = (h - objImg.getHeight()) / 2;
                g2d.drawImage(objImg, x, y, null);
            }
            g2d.dispose();
            out_count++;
            //Images.write(image, new File(target + String.format("%06d", i) + ".png"));
            trace.update(i, image);
        }
        System.out.println(out_count);
    }

    @Test
    public void test_trace() {
        String prev_path = "E:\\id-49jmldjau2h1do5j1ot7m55n9r.jpg";
        String next_path = "E:\\id-49jmldjau2h1do5j1ot7m55n9r.jpg";
        ImageTrace trace = new ImageTrace();
        trace.gray_limit = 230;
        ImageFrame prev = trace.buildFrame(Images.read(new File(prev_path)));
        ImageFrame next = trace.buildFrame(Images.read(new File(next_path)));
        trace.update(prev);
        trace.update(prev);
        trace.update(next);

    }
    
    @Test
    public void test_trace2() {
        ImageTrace trace = new ImageTrace();
        trace.gray_limit = 225;
        trace.diffMax = 25;
        for (int i = 1; i < 10000; i++) {
            String path = String.format("D:\\ximage\\real\\%06d.jpg", i);
            File f = new File(path);
            if (!f.exists())
                break;
            ImageFrame prev = trace.buildFrame(Images.read(new File(path)));
            prev.index = i;
            trace.update(prev);
        }

    }
}
