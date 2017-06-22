package org.nutz.plugins.ml.image.bean;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageFrame {
    
    public int index;
    public BufferedImage image;
    public List<SubImage> subs;
    public int[][] gray;
    public int[][] gray_avg;
    public boolean[][] gray_bol;
}