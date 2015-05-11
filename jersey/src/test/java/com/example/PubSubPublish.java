package com.example;


import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class PubSubPublish {
	
	public static void publish(){
		try {
			XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration
			.builder().setHost("192.168.1.227").setPort(5222)
			.setResource("test")
			.setCompressionEnabled(false)
			.setSecurityMode(SecurityMode.disabled)
			.setServiceName("im")
			.build();
			AbstractXMPPConnection conn = new XMPPTCPConnection(conf).connect();
			conn.login("hello", "hello");
			PubSubManager psManager = new PubSubManager(conn);
			psManager.deleteNode("svn2");
			// Create the node
			ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
			form.setAccessModel(AccessModel.open);
			form.setPublishModel(PublishModel.open);
			form.setDeliverPayloads(true);
			form.setNotifyRetract(true);
			form.setPersistentItems(true);
			form.setPublishModel(PublishModel.open);
//			form.setDataType(FormField.ELEMENT);

			LeafNode leaf = (LeafNode) psManager.createNode("svn2", form);
//			LeafNode leaf = (LeafNode) psManager.getNode("svn2");
			
			//leaf.send(new Item());
			//leaf.deleteItem("incoming svn");
			
			 //Now publish something – See Javadocs
			SimplePayload payload = new SimplePayload("book","pubsub_book", "");

			PayloadItem payloadItem = new PayloadItem(null, payload);
			leaf.send(payloadItem);
			//leaf.publish(payloadItem);
			//leaf.send(new PayloadItem("test" + System.currentTimeMillis(),new SimplePayload("book", "pubsub:test:book", "Two Towers")));
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (SmackException e) {
			e.printStackTrace();
		} catch (XMPPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		
		
			System.out.println(StringUtils.escapeForXML("你好，的进货价接口").toString());
			Form form = null;
	}

}
