package com.weixin.qy.rests;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WeixinService implements Runnable {

	private final BlockingQueue<String> bq;
	private final ExecutorService pool;
	private String accessToken = "_woFjJl6sW3tCWME_M_sZmPsgIgN1mLlDwagomU9_Tw4I4_26rloRPUDxT_L2CP2IFu3AumRAFZv71r3l3RC0w";

	public WeixinService(BlockingQueue<String> bqueue, int poolSize) {
		this.bq = bqueue;
		pool = Executors.newFixedThreadPool(poolSize);
	}

	@Override
	public void run() {
		for (;;) {
			String xmlStr;
			try {
				xmlStr = bq.take();
				if (org.apache.commons.lang3.StringUtils.isNotEmpty(accessToken)) {
					accessToken = takeAccessToken();
				}
				pool.execute(new WeiXinHandler(accessToken, xmlStr));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private String takeAccessToken(){
		String resp = WeiXinAPIUtil.getAccessToken();
		Map<?,?> resp_obj = WeiXinAPIUtil.jsonToObject(Map.class, resp);
		return (String) resp_obj.get("access_token");
	}

	public void shutdownAndAwaitTermination() {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

}
