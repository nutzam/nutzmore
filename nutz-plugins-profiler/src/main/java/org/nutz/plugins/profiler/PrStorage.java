package org.nutz.plugins.profiler;

import java.util.List;

import org.nutz.lang.util.NutMap;

public interface PrStorage {

    void save(PrSpan span);

    List<PrSpan> query(NutMap params);

    void clear();
}
