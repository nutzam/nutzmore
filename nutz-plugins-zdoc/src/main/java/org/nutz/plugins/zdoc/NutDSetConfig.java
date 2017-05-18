package org.nutz.plugins.zdoc;

import java.util.List;

/**
 * 描述了一个文档集合的配置
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutDSetConfig {

    /**
     * 集合的标题
     */
    private String title;

    /**
     * 集合下属的文档和目录列表
     */
    private List<NutD> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NutD> getList() {
        return list;
    }

    public void setList(List<NutD> list) {
        this.list = list;
    }

}
