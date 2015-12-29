package com.weixin.qy.rests;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.example.commons.CommonUtils;

public class WeixinConfig {

	public static int access_token_expired = 42001;
	private static WeixinConfig obj = null;

	private BlockingQueue<String> bqueue;
	private boolean messageReplySync = true;
	private int messageQueueSize = 100;
	private WeixinAccess weixinAccess;

	private WeixinConfig() {
		bqueue = new ArrayBlockingQueue<String>(100);
	}

	public static WeixinConfig getInstance() {
		if (obj == null) {
			obj = new WeixinConfig();
		}
		return obj;
	}

	public BlockingQueue<String> getBqueue() {
		return bqueue;
	}

	public void setBqueue(BlockingQueue<String> bqueue) {
		this.bqueue = bqueue;
	}

	public boolean isMessageReplySync() {
		return messageReplySync;
	}

	public void setMessageReplySync(boolean messageReplySync) {
		this.messageReplySync = messageReplySync;
	}

	public int getMessageQueueSize() {
		return messageQueueSize;
	}

	public void setMessageQueueSize(int messageQueueSize) {
		this.messageQueueSize = messageQueueSize;
	}

	public static WeixinConfig getObj() {
		return obj;
	}

	public static void setObj(WeixinConfig obj) {
		WeixinConfig.obj = obj;
	}

	public WeixinAccess buildWeixinAccess() {
		if (weixinAccess == null) {
			weixinAccess = new WeixinAccess();
		}
		return weixinAccess;
	}

	public synchronized WeixinAccess rebuildWeixinAccess(WeixinAccess old) {
		if (weixinAccess != null && weixinAccess.getTime() <= old.getTime()) {
			weixinAccess=null;
			buildWeixinAccess();
		}
		return weixinAccess;
	}

	class WeixinAccess {
		String token;
		int expires_in;
		long time;

		WeixinAccess() {
			String resp = WeiXinAPIUtil.getAccessToken();
			Map<?, ?> resp_obj = CommonUtils.jsonToObject(Map.class, resp);
			token = (String) resp_obj.get("access_token");
			expires_in = (Integer) resp_obj.get("expires_in");
			time = System.currentTimeMillis();
		}

		public String getToken() {
			return token;
		}

		public int getExpires_in() {
			return expires_in;
		}

		public long getTime() {
			return time;
		}

	}

}
