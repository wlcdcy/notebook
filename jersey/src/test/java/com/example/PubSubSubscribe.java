package com.example;

import java.io.IOException;
import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.ConfigurationEvent;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.NodeConfigListener;

public class PubSubSubscribe {

	public static void main(String[] args) {

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
			// Get the node
			LeafNode node = psManager.getNode("svn2");
			List<? extends Item> items = node.getItems(100);
			System.out.println(items.get(2).getId());
			
			/*node.addConfigurationListener(new NodeConfigCoordinator());
			node.subscribe(conn.getStreamId());

			ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
			form.setAccessModel(AccessModel.open);
			form.setDeliverPayloads(false);
			form.setNotifyRetract(true);
			form.setPersistentItems(true);
			form.setPublishModel(PublishModel.open);
			
			node.sendConfigurationForm(form);
			*/
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

}

class NodeConfigCoordinator implements NodeConfigListener {
	public void handleNodeConfiguration(ConfigurationEvent config) {
		System.out.println("New configuration");
		System.out.println(config.getConfiguration());
	}
}
