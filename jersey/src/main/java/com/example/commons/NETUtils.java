package com.example.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
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

public class NETUtils {
    private static final Logger LOG = LoggerFactory.getLogger(NETUtils.class);
    public static final int HTTPSTATUOK = 200;
    public static final String USER_AGENT_VALUE = "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803";
    public static final String HTTPPROTOCOL = "http";
    public static final String HTTPSPROTOCOL = "https";

    private NETUtils() {

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
        SSLContext ctx = getSslContext();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx);
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();

        Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("HTTPPROTOCOL", plainsf).register("HTTPSPROTOCOL", sslsf).build();

        HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    public static CloseableHttpClient getHttpsClient() {
        SSLContext ctx = getSslContext();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx);
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.INSTANCE;
        Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("HTTPPROTOCOL", plainsf).register("HTTPSPROTOCOL", sslsf).build();

        HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * @param keystore
     *            default【 "d:/hiwork.keystore"】;
     * @return
     */
    public static CloseableHttpClient getHttpsClient(String keystore) {
        /*
         * 自定义证书（自己生成证书或非信任机构颁发证书），需要手动导入时，使用下面的方式加载正式; 1、需要从浏览器导出证书 xxx.cer；
         * 2、使用java自带的keytool工具将签名证书xxx.cer 导出密钥库文件keystore（java所能识别的）。
         */
        try {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(new File(keystore), "111111".toCharArray(), new TrustSelfSignedStrategy())
                    .build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);

            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException
                | IOException e) {
            LOG.error(e.getMessage(), e);

        }
        return null;
    }

    private static SSLContext getSslContext() {
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    return;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    return;
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return (X509Certificate[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
        }
        return ctx;
    }

    public static String httpGet(HttpGet get, boolean isSSL) {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = NETUtils.getHttpClient(isSSL);
            HttpResponse response = httpclient.execute(get);
            return parseResponse(response, String.class);
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseResponse(HttpResponse response, Class<T> clazz) {
        try {
            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                HttpEntity entity = response.getEntity();
                if (clazz.equals(String.class)) {
                    return (T) EntityUtils.toString(entity);
                }
                if (clazz.equals(byte[].class)) {
                    return (T) EntityUtils.toByteArray(entity);
                }
                if (clazz.equals(InputStream.class)) {
                    return (T) entity.getContent();
                }
                LOG.info("invalid return type: " + clazz.getName());
            } else {
                LOG.info(response.toString());
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
        return null;
    }

    public static String httpGet(String url) {
        boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;
        HttpGet hget = createHttpGet(url);
        return httpGet(hget, ssl);
    }

    /**
     * @param url
     * @return String|InputStream
     */
    @SuppressWarnings("unchecked")
    public static <T> T httpGet2(String url) {
        CloseableHttpClient httpclient = null;
        try {
            boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;
            httpclient = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = null;
            response = httpclient.execute(createHttpGet(url));
            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                HttpEntity entity = response.getEntity();
                String contenType = response.getFirstHeader("Content-Type").getValue();
                if (StringUtils.startsWith(contenType, ContentType.APPLICATION_JSON.getMimeType())) {
                    return (T) EntityUtils.toString(entity);
                } else {
                    return (T) entity.getContent();
                }
            } else {
                LOG.info(response.toString());
            }
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static CloseableHttpResponse httpGetStream(String url) {
        boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = NETUtils.getHttpClient(ssl);
            return httpclient.execute(createHttpGet(url));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static <T> T httpGet(String url, Class<T> clazz) {
        CloseableHttpClient httpclient = null;
        try {
            boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;
            httpclient = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = null;
            response = httpclient.execute(createHttpGet(url));
            return parseResponse(response, clazz);
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static String httpPost(HttpPost post, boolean https) {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = NETUtils.getHttpClient(https);
            CloseableHttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                LOG.info(response.toString());
            }
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static String httpPostWithJson(String url, String jsonString) {
        CloseableHttpClient httpclient = null;
        try {
            boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;

            httpclient = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = httpclient.execute(createPOSTWithJson(url, jsonString));
            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                LOG.info(response.toString());
            }
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static String httpPostWithForm(String url, String params) {
        CloseableHttpClient httpclient = null;
        try {
            boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;
            httpclient = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = httpclient.execute(createPOSTWithForm(url, params));
            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                LOG.info(response.toString());
            }
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static String httpPostWithMultipart(String url, Map<String, Object> params) {
        CloseableHttpClient httpclient = null;
        try {
            boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;
            httpclient = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = httpclient.execute(createPOSTWithMultipart(url, params));
            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                LOG.info(response.toString());
            }
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static String request(String method, String url, String params) {
        CloseableHttpClient httpclient = null;
        try {
            boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;
            httpclient = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = null;

            if (StringUtils.equals(HttpGet.METHOD_NAME, method)) {
                response = httpclient.execute(createHttpGet(url));
            } else if (StringUtils.equals(HttpPost.METHOD_NAME, method)) {
                response = httpclient.execute(createPOSTWithForm(url, params));
            } else {
                return null;
            }

            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                LOG.info(response.toString());
            }
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static String request(String method, String url, Map<String, Object> params) {
        CloseableHttpClient httpclient = null;
        try {
            boolean ssl = StringUtils.startsWith(url, HTTPSPROTOCOL) ? true : false;

            httpclient = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = null;

            if (StringUtils.equals(HttpGet.METHOD_NAME, method)) {
                response = httpclient.execute(createHttpGet(url));
            } else if (StringUtils.equals(HttpPost.METHOD_NAME, method)) {
                response = httpclient.execute(createPOSTWithMultipart(url, params));
            } else {
                return null;
            }

            if (response.getStatusLine().getStatusCode() == HTTPSTATUOK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                LOG.info(response.toString());
            }
        } catch (UnsupportedCharsetException | IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static HttpGet createHttpGet(String reqUrl) {
        HttpGet httpRequest = new HttpGet(reqUrl);
        httpRequest.addHeader(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
        return httpRequest;
    }

    public static HttpPost createPOSTWithJson(String reqUrl, String jsonStr) {
        HttpPost httpPost = new HttpPost(reqUrl);
        httpPost.addHeader(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
        httpPost.addHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
        StringEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        return httpPost;
    }

    private static HttpPost createPOSTWithForm(String reqUrl, String params) {
        HttpPost httpPost = new HttpPost(reqUrl);
        httpPost.addHeader(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
        httpPost.addHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
        List<NameValuePair> formparams = URLEncodedUtils.parse(params, Consts.UTF_8, '&');
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);
        return httpPost;
    }

    private static HttpPost createPOSTWithMultipart(String reqUrl, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(reqUrl);
        httpPost.addHeader(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = params.get(key);
            if (value instanceof String) {
                /*
                 * 使用part代替body解决乱码问题[entityBuilder.addTextBody(key, (String)
                 * value,ContentType.TEXT_PLAIN);]
                 */
                ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8);
                StringBody stringBody = new StringBody((String) value, contentType);
                entityBuilder.addPart(key, stringBody);
            }
            if (value instanceof File) {
                ContentType contentType = ContentType.create(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
                FileBody fileBody = new FileBody((File) value, contentType);
                entityBuilder.addPart(key, fileBody);
            }
            if (value instanceof InputStream) {
                InputStreamBody inputBody = new InputStreamBody((InputStream) value, "img.png");
                entityBuilder.addPart(key, inputBody);
            }
            if (value instanceof byte[]) {
                ByteArrayBody byteBody = new ByteArrayBody((byte[]) value, "img.png");
                entityBuilder.addPart(key, byteBody);
            }
            LOG.info(String.format("not found object type for %s param", key));
        }
        httpPost.setEntity(entityBuilder.build());
        return httpPost;
    }

    // ############################test##################################
    public static void testJenkins() {

        String webHooks = "https://api.hiwork.cc/api/sendmsg";
        String token = "204913c1-9669-4f71-8afa-35445bb721e1";
        CloseableHttpClient client = getHttpsClient("d:/hiwork.ks");
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
                LOG.info("HiWork post may have failed. Response: "
                        + IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            } else {
                LOG.info("Posting succeeded, Response data:"
                        + IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
            }
        } catch (Exception e) {
            LOG.info("Error posting to hiwork", e);
        } finally {
            post.releaseConnection();
        }
    }

    public static void useNNTPClient(String[] args) throws IOException {
        int twoParam = 2;
        int threeParam = 3;
        int fiveParam = 5;

        if (args.length != twoParam && args.length != threeParam && args.length != fiveParam) {
            LOG.info("Usage: MessageThreading <hostname> <groupname> [<article specifier> [<user> <password>]]");
            return;
        }

        String hostname = args[0];
        /* Article specifier can be numeric or Id in form <m.n.o.x@host> */
        String articleSpec = args.length >= threeParam ? args[2] : null;

        NNTPClient client = new NNTPClient();
        client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
        client.connect(hostname);

        /* Optional auth */
        if (args.length == fiveParam) {
            String user = args[3];
            String password = args[4];
            if (!client.authenticate(user, password)) {
                LOG.info("Authentication failed for user " + user + "!");
                return;
            }
        }
        parseArticle(args, articleSpec);

    }

    /**
     * NNTP --
     * 
     * @param args
     * @param articleSpec
     */
    public static void parseArticle(String[] args, String articleSpec) {
        String newsgroup = args[1];
        NewsgroupInfo group = new NewsgroupInfo();

        BufferedReader brHeader = null;
        BufferedReader brBody = null;
        try {
            NNTPClient client = new NNTPClient();
            client.selectNewsgroup(newsgroup, group);
            if (articleSpec != null) {
                brHeader = (BufferedReader) client.retrieveArticleHeader(articleSpec);
                brBody = (BufferedReader) client.retrieveArticleBody(articleSpec);
            } else {
                long articleNum = group.getLastArticleLong();
                brHeader = client.retrieveArticleHeader(articleNum);
                brBody = client.retrieveArticleBody(articleNum);
            }
            String header = readContent(brHeader);
            String body = readContent(brBody);
            if (LOG.isDebugEnabled()) {
                LOG.debug(header);
                LOG.debug(body);
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    /**
     * NNTP--
     * 
     * @param br
     * @return
     */
    public static String readContent(BufferedReader br) {
        if (br != null) {
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    LOG.info(line);
                }
            } catch (IOException e) {
                LOG.warn(e.getMessage(), e);
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        testJenkins();
    }

}
