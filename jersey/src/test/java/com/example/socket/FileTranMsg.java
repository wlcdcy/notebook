package com.example.socket;

import java.io.Serializable;

public class FileTranMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MsgType type;
	private int sn;
	private int invokeid;
	private int len;
	private Object body;
	public MsgType getType() {
		return type;
	}
	public void setType(MsgType type) {
		this.type = type;
	}
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public int getInvokeid() {
		return invokeid;
	}
	public void setInvokeid(int invokeid) {
		this.invokeid = invokeid;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	
}
