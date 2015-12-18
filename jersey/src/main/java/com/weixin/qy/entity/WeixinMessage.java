package com.weixin.qy.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="xml")
public class WeixinMessage {
	@XmlElement(name="ToUserName")
	String ToUserName; 
	@XmlElement(name="FromUserName")
	String FromUserName;
	@XmlElement(name="CreateTime")
	String CreateTime;
	@XmlElement(name="MsgType")
	String MsgType;
	@XmlElement(name="Content")
	String Content;
	@XmlElement(name="MsgId")
	String MsgId;
	@XmlElement(name="AgentID")
	String AgentID;
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
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
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

}
