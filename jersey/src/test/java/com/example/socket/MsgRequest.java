package com.example.socket;

import java.io.Serializable;

public class MsgRequest implements Serializable {

	/**
	 * 
	 */
	private String filename;
	private String md5val;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getMd5val() {
		return md5val;
	}
	public void setMd5val(String md5val) {
		this.md5val = md5val;
	}

	
}
