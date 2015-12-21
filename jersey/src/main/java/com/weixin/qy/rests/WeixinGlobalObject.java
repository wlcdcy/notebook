package com.weixin.qy.rests;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WeixinGlobalObject {

	private static WeixinGlobalObject obj = null;
	private BlockingQueue<String> bqueue;

	private WeixinGlobalObject() {
		bqueue = new ArrayBlockingQueue<String>(100);
	}

	public static WeixinGlobalObject getInstance() {
		if (obj == null) {
			obj = new WeixinGlobalObject();
		}
		return obj;
	}

	public BlockingQueue<String> getBqueue() {
		return bqueue;
	}

	public void setBqueue(BlockingQueue<String> bqueue) {
		this.bqueue = bqueue;
	}

}
