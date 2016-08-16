package org.nutz.wxrobot.bean;

import org.nutz.lang.util.NutMap;

@SuppressWarnings("serial")
public class WxInMsg extends NutMap {

    public WxInMsg setFromUserName(String fromUserName) {
        put("fromUserName", fromUserName);
        return this;
    }

    public String getFromUserName() {
        return (String) get("fromUserName");
    }

    public WxInMsg setToUserName(String toUserName) {
        put("toUserName", toUserName);
        return this;
    }

    public String getToUserName() {
        return (String) get("toUserName");
    }

    public WxInMsg setEvent(String event) {
        put("event", event);
        return this;
    }

    public String getEvent() {
        return (String) get("event");
    }

    public WxInMsg setEventKey(String eventKey) {
        put("eventKey", eventKey);
        return this;
    }

    public String getEventKey() {
        return (String) get("eventKey");
    }

    public WxInMsg setMsgType(int msgType) {
        put("msgType", msgType);
        return this;
    }

    public int getMsgType() {
        return getInt("msgType");
    }

    public WxInMsg setContent(String content) {
        put("content", content);
        return this;
    }

    public String getContent() {
        return (String) get("content");
    }

    public WxInMsg setCreateTime(long createTime) {
        put("createTime", createTime);
        return this;
    }

    public long getCreateTime() {
        return getLong("createTime", 0);
    }

    public WxInMsg setMsgID(long msgID) {
        put("msgID", msgID);
        return this;
    }

    public String getMsgID() {
        return getString("msgId");
    }

    public WxInMsg setPicUrl(String picUrl) {
        put("picUrl", picUrl);
        return this;
    }

    public String getPicUrl() {
        return (String) get("picUrl");
    }

    public WxInMsg setMediaId(String mediaId) {
        put("mediaId", mediaId);
        return this;
    }

    public String getMediaId() {
        return (String) get("mediaId");
    }

    public WxInMsg setFormat(String format) {
        put("format", format);
        return this;
    }

    public String getFormat() {
        return (String) get("format");
    }

    public WxInMsg setThumbMediaId(String thumbMediaId) {
        put("thumbMediaId", thumbMediaId);
        return this;
    }

    public String getThumbMediaId() {
        return (String) get("thumbMediaId");
    }

    public WxInMsg setRecognition(String recognition) {
        put("recognition", recognition);
        return this;
    }

    public String getRecognition() {
        return (String) get("recognition");
    }

    public WxInMsg setLocation_X(double location_X) {
        put("location_X", location_X);
        return this;
    }

    public double getLocation_X() {
        return getDouble("location_X", 0);
    }

    public WxInMsg setLocation_Y(double location_Y) {
        put("location_Y", location_Y);
        return this;
    }

    public double getLocation_Y() {
        return getDouble("location_Y", 0);
    }

    public WxInMsg setScale(double scale) {
        put("scale", scale);
        return this;
    }

    public double getScale() {
        return getDouble("scale", 0);
    }

    public WxInMsg setLabel(String label) {
        put("label", label);
        return this;
    }

    public String getLabel() {
        return (String) get("label");
    }

    public WxInMsg setTitle(String title) {
        put("title", title);
        return this;
    }

    public String getTitle() {
        return (String) get("title");
    }

    public WxInMsg setDescription(String description) {
        put("description", description);
        return this;
    }

    public String getDescription() {
        return (String) get("description");
    }

    public WxInMsg setUrl(String url) {
        put("url", url);
        return this;
    }

    public String getUrl() {
        return (String) get("url");
    }

    public WxInMsg setStatus(String status) {
        put("status", status);
        return this;
    }

    public String getStatus() {
        return (String) get("status");
    }

    // public WxInMsg setScanCodeInfo(WxScanCodeInfo scanCodeInfo) {
    // put("scanCodeInfo", scanCodeInfo);
    // return this;
    // }
    //
    // public WxScanCodeInfo getScanCodeInfo() {
    // return (WxScanCodeInfo) get("scanCodeInfo");
    // }
    //
    // public WxInMsg setSendLocationInfo(WxSendLocationInfo sendLocationInfo) {
    // put("sendLocationInfo", sendLocationInfo);
    // return this;
    // }
    //
    // public WxSendLocationInfo getSendLocationInfo() {
    // return (WxSendLocationInfo) get("sendLocationInfo");
    // }

    public WxInMsg setExtkey(String extkey) {
        put("extkey", extkey);
        return this;
    }

    public String getExtkey() {
        return (String) get("extkey");
    }

    public WxInMsg setTotalCount(int totalCount) {
        put("totalCount", totalCount);
        return this;
    }

    public int getTotalCount() {
        return getInt("totalCount", 0);
    }

    public WxInMsg setFilterCount(int filterCount) {
        put("filterCount", filterCount);
        return this;
    }

    public int getFilterCount() {
        return getInt("filterCount", 0);
    }

    public WxInMsg setSentCount(int sentCount) {
        put("sentCount", sentCount);
        return this;
    }

    public int getSentCount() {
        return getInt("sentCount", 0);
    }

    public WxInMsg setErrorCount(int errorCount) {
        put("errorCount", errorCount);
        return this;
    }

    public int getErrorCount() {
        return getInt("errorCount", 0);
    }
}
