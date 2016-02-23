package com.example.socket;

import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class DemoClient {
	static final int CONNECT_TIMEOUT = 5000;
	static final boolean USE_CUSTOM_CODEC = false;
	static final String HOST = "36.46.255.99";
	static final int PORT = 9180;

	public static void main(String[] args) {
		NioSocketConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);

		connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.setHandler(new DemoClientHandler());
		IoSession session;

		for (;;) {
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(
						HOST, PORT));
				future.awaitUninterruptibly();
				session = future.getSession();
				break;
			} catch (RuntimeIoException e) {
				System.err.println("quot;Failed to connect.");
				e.printStackTrace();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

		// wait until the summation is done
		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();
	}
}
