package org.nutz.plugins.ml.image.bean;

import org.nutz.plugins.ml.image.ImageTrace;

public interface ImageTraceCallback {

    void newObject(ImageTrace trace, ImageFrame frame, String id, SubImage sub);
    
    void removeObject(ImageTrace trace, String id, SubImage sub);
    
    void updateTopX(ImageTrace trace, String id, SubImage sub, int newTopX);
}
