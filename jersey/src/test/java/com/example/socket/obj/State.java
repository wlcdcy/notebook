package com.example.socket.obj;

public enum State {
	RECVREADY("0x01"), RECVOK("0x02"), RECVCOMPLE("0x03"), RECVTIMEOUT("0x10"), RECVCRCERROR(
			"0x11"), RECVMD5ERROR("0x12"), RECVDATALINKERROR("0x13"), RECVUNKNOWNERROR("0x1f");
	private String key;

	private State(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
