package com.example.listener;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.NETUtils;
import com.example.commons.OscUtils;

public class OSCJob implements Job {
    static long since_id = 0;
    private OscUtils oscUtil = new OscUtils();
    private ObjectMapper mapper = new ObjectMapper();
    private String token = "";
    private String url = "";
    private Map<String, String> resq_data = new HashMap<String, String>();
    Logger logger = LoggerFactory.getLogger(OSCJob.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("触发[" + OSCJob.class.getName() + "]定时任务....");
        JobDataMap job_data = context.getJobDetail().getJobDataMap();
        token = job_data.getString("token");
        url = job_data.getString("url");
        // recommend-推荐|time-最新|view-热门|cn-国产
        String resp_content = oscUtil.project_list("time");
        List<Map> projects = null;
        String data = null;
        try {
            projects = (List<Map>) ((Map) mapper.readValue(resp_content, Map.class)).get("projectlist");
        } catch (IOException e) {
            e.printStackTrace();
            data = e.getMessage();
        }

        if (projects != null && projects.size() > 0) {
            data = "";
            for (Map project : projects) {
                data += String.format("<li> <a target=\"_blank\" href=\"%s\"><b>%s</b></a> <br>%s</li>",
                        project.get("url"), project.get("name"), project.get("description"));
            }
        }

        resq_data.clear();
        resq_data.put("token", token);
        resq_data.put("data", data);

        try {
            boolean ssl = StringUtils.startsWith(url, "https") ? true : false;

            CloseableHttpClient client = NETUtils.getHttpClient(ssl);
            CloseableHttpResponse response = null;

            HttpPost httppost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(10000)
                    .build();
            httppost.setConfig(requestConfig);

            ContentType contentType = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Consts.UTF_8);

            StringEntity entity = new StringEntity(mapper.writeValueAsString(resq_data), contentType);
            httppost.setEntity(entity);
            response = client.execute(httppost);

            logger.info(EntityUtils.toString(response.getEntity(), "UTF-8"));

        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
