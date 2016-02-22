package com.example.socket;

public enum MsgType {
	MSG_REQUSET("0xo1"), MSG_RESPONSE("0x02"), MSG_CONTENT("0x03"), MSG_COMPLETE(
			"0x04"), MSG_ROUTERERROR("0x11");

	private MsgType(String key) {
		this.key = key;
	}

	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
