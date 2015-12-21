package com.weixin.qy.rests;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WeixinListener implements ServletContextListener {

	WeixinService ws;
	Thread wsthraed;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ws.shutdownAndAwaitTermination();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		ws = new WeixinService(WeixinGlobalObject.getInstance().getBqueue(),
				100);
		wsthraed = new Thread(ws);
		wsthraed.start();
	}
}
