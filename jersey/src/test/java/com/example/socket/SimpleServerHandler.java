package com.example.socket;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class SimpleServerHandler extends IoHandlerAdapter {

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		//super.sessionIdle(session, status);
		System.out.println("IDLE " + session.getIdleCount(status));
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		//super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		//super.messageReceived(session, message);
		String str = message.toString();
//		FileTranMsg msg = (FileTranMsg)message;
//		MsgType type = msg.getType();
		
		if(str.trim().equalsIgnoreCase("")){
			session.close(true);
			return ;
		}
		String reply = "";
		session.write(reply);
		System.out.println("Message written...");
		
	}

}
