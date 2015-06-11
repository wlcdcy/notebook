package com.example.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class NetUtil {
	public static void main1(String[] args) throws SocketException, IOException {

		if (args.length != 2 && args.length != 3 && args.length != 5) {
			System.out
					.println("Usage: MessageThreading <hostname> <groupname> [<article specifier> [<user> <password>]]");
			return;
		}

		String hostname = args[0];
		String newsgroup = args[1];
		// Article specifier can be numeric or Id in form <m.n.o.x@host>
		String articleSpec = args.length >= 3 ? args[2] : null;

		NNTPClient client = new NNTPClient();
		client.addProtocolCommandListener(new PrintCommandListener(
				new PrintWriter(System.out), true));
		client.connect(hostname);

		if (args.length == 5) { // Optional auth
			String user = args[3];
			String password = args[4];
			if (!client.authenticate(user, password)) {
				System.out.println("Authentication failed for user " + user
						+ "!");
				System.exit(1);
			}
		}

		NewsgroupInfo group = new NewsgroupInfo();
		client.selectNewsgroup(newsgroup, group);

		BufferedReader br;
		String line;
		if (articleSpec != null) {
			br = (BufferedReader) client.retrieveArticleHeader(articleSpec);
		} else {
			long articleNum = group.getLastArticleLong();
			br = client.retrieveArticleHeader(articleNum);
		}
		if (br != null) {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		}
		if (articleSpec != null) {
			br = (BufferedReader) client.retrieveArticleBody(articleSpec);
		} else {
			long articleNum = group.getLastArticleLong();
			br = client.retrieveArticleBody(articleNum);
		}
		if (br != null) {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		}
	}

	/** 获取同时支持http和https的HttpClient对象
	 * @param ssl	true支持https
	 * @return
	 */
	public static CloseableHttpClient getHttpClient(boolean ssl) {
		if (!ssl) {
			return HttpClients.createDefault();
		}

		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);

		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
				.getSocketFactory();

		Registry<ConnectionSocketFactory> r = RegistryBuilder
				.<ConnectionSocketFactory> create().register("http", plainsf)
				.register("https", sslsf).build();

		HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
				r);
		CloseableHttpClient httpclinet = HttpClients.custom()
				.setConnectionManager(cm).build();
		return httpclinet;
	}

	/**
	 * @param keystore			default【 "d:/hiwork.keystore"】;
	 * @return
	 */
	public static CloseableHttpClient getHttpClient(String keystore) {
		// 自定义证书（自己生成证书或非信任机构颁发证书），需要手动导入时，使用下面的方式加载正式;
		// 1、需要从浏览器导出证书 xxx.cer；
		// 2、使用java自带的keytool工具将签名证书xxx.cer 导出密钥库文件keystore（java所能识别的）。
		
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			FileInputStream instream = new FileInputStream(new File(keystore));
			try {
				trustStore.load(instream, "111111".toCharArray());
			} finally {
				instream.close();
			}
			SSLContext sslcontext = SSLContexts.custom().useTLS()
					.loadTrustMaterial(trustStore).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
