package com.example.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtils {
	public static Logger logger = LoggerFactory.getLogger(NetUtils.class);

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

	/**
	 * 获取同时支持http和https的HttpClient对象
	 * 
	 * @param ssl
	 *            true支持https
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

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx);
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

	public static CloseableHttpClient getHttpClient() {
		SSLContext ctx = getSslContext();// SSLContexts.createSystemDefault();

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx);
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.INSTANCE;
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
	 * @param keystore
	 *            default【 "d:/hiwork.keystore"】;
	 * @return
	 */
	public static CloseableHttpClient getHttpClient(String keystore) {
		// 自定义证书（自己生成证书或非信任机构颁发证书），需要手动导入时，使用下面的方式加载正式;
		// 1、需要从浏览器导出证书 xxx.cer；
		// 2、使用java自带的keytool工具将签名证书xxx.cer 导出密钥库文件keystore（java所能识别的）。
		try {
			SSLContext sslcontext = SSLContexts
					.custom()
					.loadTrustMaterial(new File(keystore),
							"111111".toCharArray(),
							new TrustSelfSignedStrategy()).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext);

			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static SSLContext getSslContext() {
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
		return ctx;
	}
	
	
	public static String request(String method,String url,String params){
		try {
			boolean ssl =StringUtils.startsWith(url, "https")? true:false;
			
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
			CloseableHttpResponse response = null ;
			
			if(StringUtils.equals(HttpGet.METHOD_NAME, method)){
				response =httpclient.execute(getHttpGet(url));
			}else if(StringUtils.equals(HttpPost.METHOD_NAME, method)){
				response =httpclient.execute(getHttpPost(url,params));
			}else{
				return null;
			}
			
			if(response.getStatusLine().getStatusCode()<300){
				return EntityUtils.toString(response.getEntity());
			}else{
				logger.info(response.toString());
			}
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String request(String method,String url,Map<String,Object> params){
		try {
			boolean ssl =StringUtils.startsWith(url, "https")? true:false;
			
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
			CloseableHttpResponse response = null ;
			
			if(StringUtils.equals(HttpGet.METHOD_NAME, method)){
				response =httpclient.execute(getHttpGet(url));
			}else if(StringUtils.equals(HttpPost.METHOD_NAME, method)){
				response =httpclient.execute(getHttpPost(url,params));
			}else{
				return null;
			}
			logger.info(response.toString());
			if(response.getStatusLine().getStatusCode()<300){
				return EntityUtils.toString(response.getEntity());
			}
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static HttpGet getHttpGet(String req_url){
		HttpGet httpRequest = new HttpGet(req_url);
		httpRequest.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		return httpRequest;
	}
	
	private static HttpPost getHttpPost(String req_url,String params){
		HttpPost httpPost = new HttpPost(req_url);
		httpPost.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		httpPost.addHeader(HttpHeaders.CONTENT_ENCODING,"utf-8");
		List<NameValuePair> formparams = URLEncodedUtils.parse(params, Consts.UTF_8,'&');
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,Consts.UTF_8);
		httpPost.setEntity(entity);
		return httpPost;
	}
	
	private static HttpPost getHttpPost(String req_url,Map<String,Object> params){
		HttpPost httpPost = new HttpPost(req_url);
		httpPost.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		Iterator<String> keys = params.keySet().iterator();
		while(keys.hasNext()){
			String key  = keys.next();
			Object value  = params.get(key);
			if(value instanceof String){
//				使用part代替body解决乱码问题[entityBuilder.addTextBody(key, (String) value,ContentType.TEXT_PLAIN);]
				ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8);
				StringBody stringBody = new StringBody((String)value,contentType);
				entityBuilder.addPart(key, stringBody);
				continue;
			}
			if(value instanceof File){
				ContentType contentType = ContentType.create(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
				FileBody fileBody = new FileBody((File)value,contentType);
				entityBuilder.addPart(key, fileBody);
				continue;
			}
			if(value instanceof InputStream){
				InputStreamBody inputBody = new InputStreamBody((InputStream)value, "img.png");
				entityBuilder.addPart(key, inputBody);
				continue;
			}
			if(value instanceof byte[]){
				ByteArrayBody  byteBody = new ByteArrayBody((byte[])value,"img.png");
				entityBuilder.addPart(key, byteBody);
				continue;
			}
			logger.info(String.format("not found object type for %s param",key));
		}
		httpPost.setEntity(entityBuilder.build());
		return httpPost;
	}
	
	
//	############################test##################################
	public static void testJenkins(){

		String webHooks = "https://api.hiwork.cc/api/sendmsg";
		String token = "204913c1-9669-4f71-8afa-35445bb721e1";
		CloseableHttpClient client = getHttpClient("d:/hiwork.ks");
		HttpPost post = new HttpPost(webHooks);
		try {
			JSONObject json = new JSONObject();
			json.put("token", token);
			json.put("data", "hello hiwork");
			StringEntity postData = new StringEntity(json.toString());
			post.setEntity(postData);
			post.setHeader("content-type", "application/json");
			CloseableHttpResponse response = client.execute(post);
			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != HttpStatus.SC_OK) {
				logger.info("HiWork post may have failed. Response: "+ IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
			} else {
				logger.info("Posting succeeded, Response data:"+ IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
			}
		} catch (Exception e) {
			logger.info("Error posting to hiwork", e);
		} finally {
			post.releaseConnection();
		}
	}

	public static void main(String[] args) {
		testJenkins();
	}

}
