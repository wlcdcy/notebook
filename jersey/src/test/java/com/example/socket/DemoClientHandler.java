package com.example.socket;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.socket.obj.FileTransfer;
import com.example.socket.obj.MessageRequest;
import com.example.socket.obj.MessageResponse;
import com.example.socket.obj.State;
import com.example.socket.obj.Type;

public class DemoClientHandler extends IoHandlerAdapter {
	private final static Logger logger = LoggerFactory
			.getLogger(DemoClientHandler.class);

	private boolean finished;

	public boolean isFinished() {
		return finished;
	}

	String filePath = "f:/";
	String fileName = "20151215152519.png";

	@Override
	public void sessionOpened(IoSession session) {
		// FileTransfer ft = new FileTransfer();
		// ft.type.set(Type.MSG_REQUSET);
		// ft.sn.set(Short.valueOf("1"));
		// ft.invokeid.set(Short.valueOf("1"));
		// ft.len.set(Short.valueOf("4096"));
		// MessageRequest messageRequest = new MessageRequest();
		//
		// String md5Val = DemoServerHandler.getFileMd5Val(new
		// File(filePath+fileName));
		// messageRequest.filename.set(fileName);
		// messageRequest.md5Val.set(md5Val);
		// ft.body.request.setByteBuffer(messageRequest.getByteBuffer(), 0);
		// parseHandle4C(session,ft);

		MsgRequest mr = new MsgRequest();
		mr.setFilename("filename");
		mr.setMd5val("md5val");
		parseHandle4J(session, mr);
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		MsgRequest mr = (MsgRequest) message;
		parseHandle4J(session, mr);

		// FileTransfer ft = (FileTransfer) message;
		// parseHandle4C(session,ft);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		session.close(true);
	}

	private void parseHandle4J(IoSession session, MsgRequest mr) {
		session.write(mr);
	}

	private void parseHandle4C(IoSession session, FileTransfer ft) {
		if (ft.type.equals(Type.MSG_RESPONSE)) {
			long offset = ft.body.response.offset.get();
			if (ft.body.response.state.get().equals(State.RECVREADY)) {

			}
		} else {
			logger.warn("Server error, disconnecting...");
			session.close(true);
			finished = true;
		}
	}
}
