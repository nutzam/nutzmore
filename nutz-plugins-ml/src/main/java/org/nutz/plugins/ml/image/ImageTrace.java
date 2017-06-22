package org.nutz.plugins.ml.image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ml.image.bean.ImageFrame;
import org.nutz.plugins.ml.image.bean.ImageTraceCallback;
import org.nutz.plugins.ml.image.bean.SubImage;

public class ImageTrace {

    private static final Log log = Logs.get();
    public ImageFrame prev;
    public int w = 1024; // 图片宽度
    public int h = 640; // 图片高度
    public int block_w = 8; // 检查块的宽
    public int block_h = 8; // 检测块的高
    public int gray_limit = 248; // 灰度下限,高于此灰度代表空白
    public int obj_w = 64; // 物品的最小宽度
    public int obj_h = 64; // 物品的最小高度
    public int offset_detect_size = 32; // 物品偏移量检测块的宽高
    public Map<String, SubImage> subs = new LinkedHashMap<>();
    public AtomicLong objIdSeq = new AtomicLong();
    public ImageTraceCallback callback;

    public void update(int index, BufferedImage img) {
        if (img.getWidth() != w || img.getHeight() != h) {
            log.debugf("%dx%d skip...", img.getWidth(), img.getHeight());
            return;
        }
        // 把图片解析一遍
        ImageFrame frame = buildFrame(img);
        frame.index = index;
        update(frame);
    }

    public void update(final ImageFrame next) {
        int next_obj_count = next.subs.size();
        if (next_obj_count == 0) {
            log.debugf("%06d 下一帧图像没有任何物品!! 重置所有东西!!", next.index);
            this.prev = null;
            subs.clear(); // 清除已知物品列表
            for (String id : new HashSet<>(subs.keySet())) {
                removeObject(id, subs.get(id));
            }
            return;
        }
        if (this.prev == null) {
            log.debugf("%06d 上一帧不存在,那把当前帧设置为上一帧,并添加检测到的物品", next.index);
            this.prev = next;
            // 清除已知物品列表
            for (String id : new HashSet<>(subs.keySet())) {
                removeObject(id, subs.get(id));
            }
            for (SubImage sub : this.prev.subs) {
                // 新增物品 
                newObject(prev, sub);
            }
            return;
        }
        
        List<Integer> offsets = new ArrayList<>();
        // 找一找已知物品的位置
        int image_w = next.image.getWidth();
        int y_start = 60;
        int y_end = next.image.getHeight() - 60;
        for (Entry<String, SubImage> en : new HashSet<>(subs.entrySet())) {
            SubImage sub = en.getValue();
            boolean flag = true;
            int x = 0;
            for (; x < image_w - offset_detect_size; x++) {
                flag = true; // 重置标志位
              OUT: for (int i = 0; i < offset_detect_size; i++) {
                  for (int j = y_start; j < y_end; j++) {
                      int prev_pix = sub.gray_finger[i][j];
                      int cur_pix = next.gray[i + x][j];
                      if (prev_pix != cur_pix) {
                          flag = false;
                          break OUT;
                      }
                  }
              }
              if (flag) {
                  //log.debugf("物品(id=%s) 的指纹区域在 x=%d 找到", en.getKey(), x);
                  break;
              }
            }
            if (flag) {
                sub.cur_x_top = x + (offset_detect_size /2) - sub.getRealW()/2 ;
                offsets.add(sub.cur_x_top - sub.getRealTopX());
                log.debugf("%06d 物品(id=%s) 原顶点x=%d 现顶点x=%d", next.index, en.getKey(), sub.getRealTopX(), sub.cur_x_top);
                // 看看新图像中的哪些物品就是当前物品, 判断标准是
                // 原物品的中心坐标,是否在待检测物品的范围之内
                int cur_center_x = sub.cur_x_top + sub.getRealW()/2;
                int cur_center_y = (sub.getRealBottomY() + sub.getRealTopY()) / 2;
                Iterator<SubImage> it = next.subs.iterator();
                while (it.hasNext()) {
                    SubImage check = it.next();
                    if (check.getRealTopX() < cur_center_x && check.getRealBottomX() > cur_center_x) {
                        if (check.getRealTopY() < cur_center_y && check.getRealBottomY() > cur_center_y) {
                            it.remove();
                        }
                    }
                }
            } else {
                log.debugf("%06d 物品(id=%s)已消失? 移除之", next.index,  en.getKey());
                removeObject(en.getKey(), en.getValue());
            }
        }
        for (SubImage sub : next.subs) {
            // 新增物品 
            newObject(prev, sub);
        }
//        log.debugf("%06d 当前帧检测到%d个物品, 共发现%d个已经出现过的物品 %s",
//                   next.index,
//                   next_obj_count,
//                   offsets.size(),
//                   offsets);
        this.prev = next;
    }

    public ImageFrame buildFrame(BufferedImage img) {
        long now = System.currentTimeMillis();
        ImageFrame frame = new ImageFrame();
        frame.image = MlImages.dup(img);
        frame.gray = MlImages.toGray(img, 0, 0, img.getWidth(), img.getHeight());
        frame.gray_avg = MlImages.gray_avg(frame.gray, block_w, block_h);
        frame.gray_bol = MlImages.gray_bol(frame.gray_avg, gray_limit);
        frame.gray_bol = MlImages.gray_bol_ci(frame.gray_bol);
        // 过滤掉太小的物品
        frame.subs = new ArrayList<>();
        for (SubImage sub : MlImages.getSubImages(img,
                                                  frame.gray_bol,
                                                  block_w,
                                                  block_h)) {
            sub.block_w = block_w;
            sub.block_h = block_h;
            // 宽度要符合要求
            if (sub.getRealW() < this.obj_w)
                continue;
            // 高度也要符合要求
            if (sub.getRealH() < this.obj_h)
                continue;
            // 如果物品贴近屏幕左边缘,移除掉
            if (sub.x_top < 1) {
                //log.debug("物品贴近左边缘, 移除之 : " + sub);
                continue;
            }
           // 如果物品贴近屏幕右边缘,移除掉
            if (sub.getRealBottomX() >= (img.getWidth() - this.block_w)) {
                //log.debug("物品贴近右边缘, 移除之 : " + sub);
                continue;
            }
            sub.timestamp = now;
            frame.subs.add(sub);
        }
        return frame;
    }
    
    public void newObject(ImageFrame frame, SubImage sub) {
        String id = ""+ objIdSeq.incrementAndGet();
        int finger_start = sub.getRealTopX() + (sub.getRealW()/2) -(offset_detect_size/2);
        log.debugf("%06d 新增物品 id=%s %s 指纹区域: [%d, %d] ", frame.index, id, sub.toString(this), finger_start, finger_start+offset_detect_size);
        // 摘取物品图像
        BufferedImage obj_image = frame.image.getSubimage(sub.getRealTopX(), sub.getRealTopY(), sub.getRealW(), sub.getRealH());
        sub.image = MlImages.dup(obj_image);
        // 摘取用于检测该物品的灰度矩阵, 中心区域往两侧各抽取16像素
        sub.gray_finger = new int[offset_detect_size][frame.image.getHeight()];
        System.arraycopy(frame.gray, finger_start, sub.gray_finger, 0, offset_detect_size);
        subs.put(id, sub);
        if (callback != null)
            callback.newObject(this, frame, id, sub);
    }
    
    public void removeObject(String id, SubImage sub) {
        subs.remove(id);
        if (callback != null)
            callback.removeObject(this, id, sub);
    }
    
    public static void printAsImage(int[][] array) {
        System.out.println("w=" + array.length + ",h=" + array[0].length);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] > 248 ? "  " : "X ");
            }
            System.out.println();
        }
    }
}
