package com.example.socket;

import java.io.Serializable;

public class MsgContent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int len;
	private char content[];
	private long crc16val;

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public char[] getContent() {
		return content;
	}

	public void setContent(char[] content) {
		this.content = content;
	}

	public long getCrc16val() {
		return crc16val;
	}

	public void setCrc16val(long crc16val) {
		this.crc16val = crc16val;
	}

}
