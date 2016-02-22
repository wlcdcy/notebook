package com.example.socket;

import java.io.Serializable;

public class MsgResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int offset;
	private MsgTranState state;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public MsgTranState getState() {
		return state;
	}

	public void setState(MsgTranState state) {
		this.state = state;
	}

}
