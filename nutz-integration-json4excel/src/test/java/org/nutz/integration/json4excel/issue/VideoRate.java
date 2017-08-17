package org.nutz.integration.json4excel.issue;

import org.nutz.integration.json4excel.J4EColumnType;
import org.nutz.integration.json4excel.annotation.J4EDateFormat;
import org.nutz.integration.json4excel.annotation.J4EDefine;
import org.nutz.integration.json4excel.annotation.J4EExt;
import org.nutz.integration.json4excel.annotation.J4EIgnore;
import org.nutz.integration.json4excel.annotation.J4EName;

@J4EName("videorate")
@J4EExt(passContentRow = 1)
public class VideoRate {

    public static final String CNAME = "videorate";

    @J4EIgnore
    private String _id;

    @J4EDefine(type = J4EColumnType.DATE)
    @J4EDateFormat(from = "yyyy/MM/dd", to = "yyyyMMdd")
    private String time;

    @J4EDefine(type = J4EColumnType.NUMERIC)
    private int aiVideoClick;

    @J4EDefine(type = J4EColumnType.NUMERIC)
    private int refresh;

    @J4EDefine(type = J4EColumnType.NUMERIC)
    private int totalClick;

    @J4EDefine(type = J4EColumnType.NUMERIC)
    private int viewAboutClick;

    @J4EDefine(type = J4EColumnType.NUMERIC)
    private int searchClick;

    @J4EDefine(type = J4EColumnType.NUMERIC)
    private int searchCount;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getTotalClick() {
        return totalClick;
    }

    public void setTotalClick(int totalClick) {
        this.totalClick = totalClick;
    }

    public int getAiVideoClick() {
        return aiVideoClick;
    }

    public void setAiVideoClick(int aiVideoClick) {
        this.aiVideoClick = aiVideoClick;
    }

    public int getRefresh() {
        return refresh;
    }

    public void setRefresh(int refresh) {
        this.refresh = refresh;
    }

    public int getViewAboutClick() {
        return viewAboutClick;
    }

    public void setViewAboutClick(int viewAboutClick) {
        this.viewAboutClick = viewAboutClick;
    }

    public int getSearchClick() {
        return searchClick;
    }

    public void setSearchClick(int searchClick) {
        this.searchClick = searchClick;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}