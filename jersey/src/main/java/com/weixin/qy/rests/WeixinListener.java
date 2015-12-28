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
	public static final int poolSize = 5;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		ws.shutdownAndAwaitTermination();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		String queueSize = sce.getServletContext().getInitParameter(
				MESSAGE_QUEUE_SIZE);
		if (StringUtils.isNotEmpty(queueSize)) {
			try {
				WeixinConfig.getInstance().setMessageQueueSize(
						Integer.valueOf(queueSize));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new ConfigurationException(
						"valid custom Weixin Configuration [queueSize],it value is int type");
			}
		}
		String reply_model = sce.getServletContext().getInitParameter(
				MESSAGE_REPLY_SYNC);
		if (StringUtils.isNotEmpty(reply_model)) {
			if (StringUtils.equals("true", reply_model)) {
				WeixinConfig.getInstance().setMessageReplySync(true);
			} else if (StringUtils.equals("false", reply_model)) {
				WeixinConfig.getInstance().setMessageReplySync(false);
				ws = new WeixinService(WeixinConfig.getInstance().getBqueue(),
						poolSize);
				wsthraed = new Thread(ws);
				wsthraed.start();

			} else {
				throw new ConfigurationException(
						"valid custom Weixin Configuration [messageSyncReply],it value is true or false");
			}
		}

	}
}
