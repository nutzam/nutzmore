package org.nutz.plugins.ml.image.bean;

import java.awt.image.BufferedImage;

import org.nutz.plugins.ml.image.ImageTrace;

public class SubImage {

    public long timestamp;
    public int x_top;
    public int y_top;
    public int x_bottom;
    public int y_bottom;
    public transient BufferedImage image;
    public int[][] gray_finger;
    public int cur_x_top; // 相对于最初的位置的偏移量
    
    public int block_w;
    public int block_h;
    
    public boolean equals(Object obj) {
        SubImage next = (SubImage)obj;
        return this.x_bottom == next.x_bottom 
                && this.x_top == next.x_top
                && this.y_bottom == next.y_bottom
                && this.y_top == next.y_top;
    }
    
    public String toString() {
        return String.format("TOP[%d,%d] Bottom[%d,%d]", x_top, y_top, x_bottom, y_bottom);
    }
    
    public int getRealTopX() {
        return x_top*block_w;
    }
    
    public int getRealTopY() {
        return y_top*block_h;
    }
    
    public int getRealBottomX() {
        return x_bottom*block_w;
    }
    
    public int getRealBottomY() {
        return y_bottom*block_h;
    }
    
    public int getRealW() {
        return (x_bottom - x_top) * block_w;
    }
    
    public int getRealH() {
        return (y_bottom - y_top) * block_h;
    }
    
    public String toString(ImageTrace trace) {
        return String.format("Real Top[%d,%d] Bottom[%d,%d] 宽高[%dx%d]", 
                             getRealTopX(), 
                             getRealTopY(), 
                             getRealBottomX(),
                             getRealBottomY(),
                             getRealW(),
                             getRealH()
                             );
    }
}
