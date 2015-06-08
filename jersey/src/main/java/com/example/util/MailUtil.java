package com.example.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

public class MailUtil {

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

	public static void proxySend(String subject, String content, String from,
			String to) {
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
			msg.setFrom(new InternetAddress("10000@163.com", "10000"));
			msg.setSender(new InternetAddress("fromAddress", "hello"));

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

	
	//代发邮件
	public static void main(String[] args) {
		String sender, recipient, subject, filename, server, cc;
		List<String> ccList = new ArrayList<String>();
		BufferedReader stdin;
		FileReader fileReader = null;
		Writer writer;
		ProxySenderSMTPHeader header;
		SMTPClient client;

		if (args.length < 1) {
			System.err.println("Usage: mail smtpserver");
			System.exit(1);
		}

		server = args[0];

		stdin = new BufferedReader(new InputStreamReader(System.in));

		try {
			System.out.print("From: ");
			System.out.flush();

			sender = stdin.readLine();

			System.out.print("To: ");
			System.out.flush();

			recipient = stdin.readLine();

			System.out.print("Subject: ");
			System.out.flush();

			subject = stdin.readLine();

//			header = new SimpleSMTPHeader(sender, recipient, subject);ProxySenderSMTPHeader
			header = new ProxySenderSMTPHeader(sender, recipient, subject);

			while (true) {
				System.out
						.print("CC <enter one address per line, hit enter to end>: ");
				System.out.flush();

				cc = stdin.readLine();

				if (cc == null || cc.length() == 0) {
					break;
				}

				header.addCC(cc.trim());
				ccList.add(cc.trim());
			}
			
			header.addSender("wangyq@16feng.com");

			System.out.print("Filename: ");
			System.out.flush();

			filename = stdin.readLine();

			try {
				fileReader = new FileReader(filename);
			} catch (FileNotFoundException e) {
				System.err.println("File not found. " + e.getMessage());
			}

			client = new SMTPClient();
			client.addProtocolCommandListener(new PrintCommandListener(
					new PrintWriter(System.out), true));

//			client.connect(server);
			client.connect("127.0.0.1");

			if (!SMTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				System.err.println("SMTP server refused connection.");
				System.exit(1);
			}

			client.login();

			client.setSender(sender);
			client.addRecipient(recipient);

			for (String recpt : ccList) {
				client.addRecipient(recpt);
			}

			writer = client.sendMessageData();
			System.out.println(header.toString());

			if (writer != null) {
				writer.write(header.toString());
				Util.copyReader(fileReader, writer);
				writer.close();
				client.completePendingCommand();
			}

			if (fileReader != null) {
				fileReader.close();
			}

			client.logout();

			client.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
