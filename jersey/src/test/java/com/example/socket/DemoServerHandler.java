package com.example.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.example.socket.obj.FileTransfer;
import com.example.socket.obj.MessageResponse;
import com.example.socket.obj.State;
import com.example.socket.obj.Type;

public class DemoServerHandler extends IoHandlerAdapter {

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// super.sessionIdle(session, status);
		System.out.println("IDLE " + session.getIdleCount(status));
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		// super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}
	
	
	long offset = 0;
	State state = State.RECVREADY;

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// super.messageReceived(session, message);
		// String str = message.toString();
		// if(str.trim().equalsIgnoreCase("")){
		// session.close(true);
		// return ;
		// }
		FileTransfer msg = (FileTransfer) message;
		if (msg.type.equals(Type.MSG_REQUSET)) {
			String fileName = msg.body.request.filename.get();
			String nd5Val = msg.body.request.md5Val.get();
			// TODO 检查是否已经接收，计算offset值。
//			long offset = 0;
//			State state = State.RECVREADY;
			File file = fileIsExist(fileName);
			if (file != null) {
				offset = file.length();
				String fileMd5Val = getFileMd5Val(file);
				if (StringUtils.equals(nd5Val, fileMd5Val)) {
					state = State.RECVCOMPLE;
				}
			}
			FileTransfer reply = new FileTransfer();
			reply.type.set(Type.MSG_RESPONSE);
			reply.sn.set(msg.sn.get());
			reply.invokeid.set(msg.invokeid.get());
			MessageResponse messageResponse = new MessageResponse();
			messageResponse.offset.set(offset);
			messageResponse.state.set(state);
			reply.body.setByteBuffer(messageResponse.getByteBuffer(), 0);
			session.write(reply);
		} else if (msg.type.equals(Type.MSG_CONTENT)) {
			short length = msg.body.content.len.get();
			String content = msg.body.content.content.get();
			int crc16 = msg.body.content.crc16Val.get();
			// TODO 验证crc值，
			offset+=content.getBytes().length;
			state = State.RECVOK;
			
			FileTransfer reply = new FileTransfer();
			reply.type.set(Type.MSG_RESPONSE);
			reply.sn.set(msg.sn.get());
			reply.invokeid.set(msg.invokeid.get());
			MessageResponse messageResponse = new MessageResponse();
			messageResponse.offset.set(offset);
			messageResponse.state.set(state);
			reply.body.setByteBuffer(messageResponse.getByteBuffer(), 0);
			session.write(reply);
			
		} else if (msg.type.equals(Type.MSG_COMPLETE)) {
			session.close(false);
		}
		System.out.println("Message written...");

	}
	
	
	
	

	public File fileIsExist(String fileName) {
		File file = new File(fileName);
		if (file.isFile()) {
			return file;
		}
		return null;
	}

	public String getFileMd5Val(File file) {
		MessageDigest MD5 = null;
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			return new String(Hex.encodeHex(MD5.digest()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null)
					fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
