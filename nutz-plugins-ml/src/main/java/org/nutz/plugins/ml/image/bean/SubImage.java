package org.nutz.plugins.ml.image.bean;

import java.awt.image.BufferedImage;

public class SubImage {

    public int x_top;
    public int y_top;
    public int x_bottom;
    public int y_bottom;
    public transient BufferedImage image;
}
