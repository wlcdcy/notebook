package com.weixin.qy.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xml")
public class WeixinMessage {
    @XmlElement(name = "ToUserName")
    String ToUserName;
    @XmlElement(name = "FromUserName")
    String FromUserName;
    @XmlElement(name = "CreateTime")
    String CreateTime;
    @XmlElement(name = "MsgType")
    String MsgType;
    @XmlElement(name = "AgentID")
    String AgentID;

    // TODO 接收普通消息
    @XmlElement(name = "MsgId")
    String MsgId;
    // TODO text消息
    @XmlElement(name = "Content")
    String Content;
    // TODO image消息
    @XmlElement(name = "PicUrl")
    String PicUrl;
    @XmlElement(name = "MediaId")
    String MediaId;
    // TODO voice消息
    @XmlElement(name = "Format")
    String Format;
    // TODO video消息|小视频消息
    @XmlElement(name = "ThumbMediaId")
    String ThumbMediaId;
    // TODO location消息
    @XmlElement(name = "Location_X")
    float Location_X;
    @XmlElement(name = "Location_Y")
    float Location_Y;
    @XmlElement(name = "Scale")
    int Scale;
    @XmlElement(name = "Label")
    String Label;

    // TODO 接收事件
    @XmlElement(name = "Event")
    String Event;

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getAgentID() {
        return AgentID;
    }

    public void setAgentID(String agentID) {
        AgentID = agentID;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getThumbMediaId() {
        return ThumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        ThumbMediaId = thumbMediaId;
    }

    public float getLocation_X() {
        return Location_X;
    }

    public void setLocation_X(float location_X) {
        Location_X = location_X;
    }

    public float getLocation_Y() {
        return Location_Y;
    }

    public void setLocation_Y(float location_Y) {
        Location_Y = location_Y;
    }

    public int getScale() {
        return Scale;
    }

    public void setScale(int scale) {
        Scale = scale;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }
}
