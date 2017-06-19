package org.nutz.plugins.ml.image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ml.image.bean.SubImage;

public class ImageTrace {

    private static final Log log = Logs.get();
    public ImageFrame prev;
    public ImageFrame cur;
    public int w = 1024; // 图片宽度
    public int h = 640; // 图片高度
    public int block_w = 8; // 检查块的宽
    public int block_h = 8; // 检测块的高
    public int gray_limit = 248; // 灰度下限,高于此灰度代表空白
    public int obj_w = 64; // 物品的最小宽度
    public int obj_h = 64; // 物品的最小高度
    public int offset_detect_size = 32; // 物品偏移量检测块的宽高

    public void update(int index, BufferedImage img) {
        if (img.getWidth() != w || img.getHeight() != h) {
            log.debugf("%dx%d skip...", img.getWidth(), img.getHeight());
            return;
        }
        // 把图片解析一遍
        ImageFrame frame = buildFrame(this, img);
        frame.index = index;
        update(frame);
    }

    public void update(final ImageFrame next) {
        if (next.subs.size() == 0) {
            log.debug("next frame without any obj image, reset ALL");
            this.prev = null;
            return;
        }
        if (this.prev == null) {
            log.debug("prev frame is NULL, using this frame as base");
            this.prev = next;
            return;
        }
        ImageFrame prev = this.prev;
        ImageFrame cur = next;


        List<String> offsets = new ArrayList<>();
        for (SubImage sub : prev.subs) {
            // 物品的中间的N个像素, 默认应是 8*8=64个像素
            int x_start = (sub.x_bottom + sub.x_top) * block_w / 2 + offset_detect_size / 16;
            int y_start = 60;
            int y_end = prev.image.getHeight() - 120;
            int t = 0;
            // Images.write(prev.image.getSubimage(x_start,
            // y_start,
            // offset_detect_size,
            // y_end - y_start),
            // new File("E:\\ximage\\prev.detect.png"));
            for (int x = x_start; x >= 0; x--) {
                boolean flag = true;
                OUT: for (int i = 0; i < offset_detect_size; i++) {
                    for (int j = y_start; j < y_end; j++) {
                        int prev_pix = prev.gray[i + x_start][j];
                        int cur_pix = cur.gray[i + x][j];
                        if (prev_pix != cur_pix) {
                            flag = false;
                            break OUT;
                        }
                    }
                }
                if (flag) {
                    // log.debug("发现同一个物品!!!");
                    offsets.add("" + t);
                    // Images.write(prev.image.getSubimage(x,
                    // y_start,
                    // offset_detect_size,
                    // y_end - y_start),
                    // new File("E:\\ximage\\prev.detect2.png"));
                    break;
                }
                t++;
            }
        }
        log.debugf("%06d 当前帧检测到%d个物品, 共发现%d个已经出现过的物品 %s",
                   cur.index,
                   cur.subs.size(),
                   offsets.size(),
                   offsets);
        this.prev = next;
    }

    public static ImageFrame buildFrame(ImageTrace trace, BufferedImage img) {
        ImageFrame frame = new ImageFrame();
        frame.image = MlImages.dup(img);
        frame.gray = MlImages.toGray(img, 0, 0, img.getWidth(), img.getHeight());
        frame.gray_avg = MlImages.gray_avg(frame.gray, trace.block_w, trace.block_h);
        frame.gray_bol = MlImages.gray_bol(frame.gray_avg, trace.gray_limit);
        frame.gray_bol = MlImages.gray_bol_ci(frame.gray_bol);
        // 过滤掉太小的物品
        frame.subs = new ArrayList<>();
        for (SubImage sub : MlImages.getSubImages(img,
                                                  frame.gray_bol,
                                                  trace.block_w,
                                                  trace.block_h)) {
            if ((sub.x_bottom - sub.x_top) * trace.block_w < trace.obj_w)
                continue;
            if ((sub.y_bottom - sub.y_top) * trace.block_h < trace.obj_h)
                continue;
            frame.subs.add(sub);
        }
        // // 排序一下,让物品从右到左,依次排列
        // Collections.sort(frame.subs, new Comparator<SubImage>() {
        // public int compare(SubImage prev, SubImage next) {
        // if (prev.x_bottom > next.x_bottom) {
        // return -1;
        // } else if (prev.x_bottom < next.x_bottom)
        // return 1;
        // return 0;
        // }
        // });
        return frame;
    }

    public static class ImageFrame {
        int index;
        public BufferedImage image;
        public List<SubImage> subs;
        public int[][] gray;
        public int[][] gray_avg;
        public boolean[][] gray_bol;
    }
}
