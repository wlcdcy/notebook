package com.weixin.qy.rests;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.config.ConfigurationException;

public class WeixinListener implements ServletContextListener {

	WeixinService ws;
	Thread wsthraed;

	public static final String MESSAGE_REPLY_SYNC = "messageReplySync";
	public static final String MESSAGE_QUEUE_SIZE = "messageQueueSize";

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ws.shutdownAndAwaitTermination();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String reply_model = sce.getServletContext().getInitParameter(
				MESSAGE_REPLY_SYNC);
		if (StringUtils.isNotEmpty(reply_model)) {
			if (StringUtils.equals("true", reply_model)) {
				WeixinGlobalObject.getInstance().setMessageReplySync(true);
			} else if (StringUtils.equals("false", reply_model)) {
				WeixinGlobalObject.getInstance().setMessageReplySync(false);

				ws = new WeixinService(WeixinGlobalObject.getInstance()
						.getBqueue(), 100);
				wsthraed = new Thread(ws);
				wsthraed.start();

			} else {
				throw new ConfigurationException(
						"valid custom Weixin Configuration [messageSyncReply],it value is true or false");
			}
		}

		int queueSize = Integer.valueOf(sce.getServletContext()
				.getInitParameter(MESSAGE_QUEUE_SIZE));
		if (queueSize > 0) {
			WeixinGlobalObject.getInstance().setMessageQueueSize(queueSize);
		}

	}
}
