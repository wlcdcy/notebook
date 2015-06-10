package com.example.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.SocketException;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtils {
	private static Logger logger = LoggerFactory.getLogger(MailUtils.class);

	public static void send(String subject, String content) {
		String smtp = "smtp.qq.com";
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", smtp);
		props.setProperty("mail.smtp.auth", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("wangyq@16feng.com", "123qaz");
			}
		});
		session.setDebug(true);

		try {
			MimeMessage msg = new MimeMessage(session);
			// msg.setFrom(new InternetAddress("10000@163.com","10000"));
			msg.setSender(new InternetAddress("wangyq@16feng.com", "hello"));

			msg.setRecipients(Message.RecipientType.TO, "2319221561@qq.com");
			// msg.setRecipients(Message.RecipientType.CC, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setContent(content, "text/html;charset=utf-8");
			Transport.send(msg);
		} catch (MessagingException mex) {
			System.out.println("send failed, exception: " + mex);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/** 代发邮件
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件内容
	 * @param from
	 *            代发地址
	 * @param to
	 *            收件人
	 * @param sender
	 *            发件人
	 */
	public static void proxySend(String subject, String content, String from,
			String to, String sender) {
		// 127.0.0.1 为自己搭建的邮件服务器，这样可以免于代发邮箱的认证的烦恼。
		String server = "127.0.01";
		// sender = "wangyq@16feng.com";
		// from = "wangyq@hiwork.com";
		// to = "wlcdcy@163.com";

		Writer writer;
		ProxySenderSMTPHeader header;
		SMTPClient client;

		header = new ProxySenderSMTPHeader(from, to, subject);
		header.addSender(sender);

		try {
			client = new SMTPClient();
			client.addProtocolCommandListener(new PrintCommandListener(
					new PrintWriter(System.out), true));

			client.connect(server);

			if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				System.err.println("SMTP server refused connection.");
				System.exit(1);
			}

			client.login();
			client.setSender(from);
			client.addRecipient(to);
			writer = client.sendMessageData();
			logger.info(header.toString());

			if (writer != null) {
				writer.write(header.toString());
				writer.write(content);
				writer.close();
				client.completePendingCommand();
			}
			try {
				client.logout();
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (CopyStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 代发邮件
	public static void main(String[] args) {
		proxySend("pig", "pig，the world is too large", "wangyq@hiwork.com",
				"wlcdcy@163.com", "wangyq@16feng.com");
	}

}

class ProxySenderSMTPHeader extends SimpleSMTPHeader {
	private final String __subject, __from, __to;
	private final StringBuffer __headerFields;
	private StringBuffer __cc;
	private String __sender;

	public ProxySenderSMTPHeader(String from, String to, String subject) {
		super(from, to, subject);
		__to = to;
		__from = from;
		__subject = subject;
		__headerFields = new StringBuffer();
		__cc = null;
		__sender = null;
	}

	@Override
	public String toString() {
		StringBuilder header = new StringBuilder();

		if (__headerFields.length() > 0) {
			header.append(__headerFields.toString());
		}

		header.append("From: ");
		header.append(__sender);

		header.append("\nSender: ");
		header.append(__from);

		header.append("\nTo: ");
		header.append(__to);

		if (__cc != null) {
			header.append("\nCc: ");
			header.append(__cc.toString());
		}

		if (__subject != null) {
			header.append("\nSubject: ");
			header.append(__subject);
		}

		header.append('\n');
		header.append('\n');

		return header.toString();
	}

	public void addSender(String sender) {
		this.__sender = sender;
	}
}
