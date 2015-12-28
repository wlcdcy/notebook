package com.weixin.qy.rests;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WeixinService implements Runnable {

	private final BlockingQueue<String> bq;
	private final ExecutorService pool;

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
				pool.execute(new WeiXinHandler(xmlStr));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
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
