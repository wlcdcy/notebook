package com.weixin.qy.rests;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.CommonUtils;
import com.example.commons.NETUtils;
import com.weixin.qy.entity.Department;
import com.weixin.qy.entity.Member;
import com.weixin.qy.entity.RespDeparment;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public class WeiXinAPIUtil {
	static Logger logger = LoggerFactory.getLogger(WeiXinAPIUtil.class);

	/**
	 * 获取访问token
	 * 
	 * @return
	 */
	public static String getAccessToken() {
		// https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=id&corpsecret=secrect
		String req_url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
						WeixinResource.appCorpID,
						WeixinResource.appEncodingAESKey);
		CloseableHttpClient hc = NETUtils.getHttpClient(req_url
				.indexOf("https") == 0 ? true : false);
		HttpGet get = new HttpGet(req_url);
		try {
			CloseableHttpResponse response = hc.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				String res_body = EntityUtils.toString(response.getEntity());
				logger.info(res_body);
				return res_body;
			} else {
				String res_body = EntityUtils.toString(response.getEntity());
				logger.info(res_body);
			}
			;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送消息
	 * 
	 * @param access_token
	 * @param msg
	 */
	public static void sendMessage(String access_token, String msg) {
		// "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN"
		String req_url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s",
						access_token);

		boolean ssl = req_url.indexOf("https") == 0 ? true : false;

		CloseableHttpClient hc = NETUtils.getHttpClient(ssl);
		HttpPost post = new HttpPost(req_url);

		StringEntity entity = new StringEntity(msg,
				ContentType.APPLICATION_JSON);
		post.setEntity(entity);
		try {
			CloseableHttpResponse response = hc.execute(post);
			String res_body = EntityUtils.toString(response.getEntity());
			logger.info(res_body);
			if (response.getStatusLine().getStatusCode() != 200) {
				// String res_body = EntityUtils.toString(response.getEntity());
				// logger.info(res_body);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// TODO ------管理部门------
	/**
	 * 获取部门信息
	 * 
	 * @param access_token
	 * @param pid
	 * @return
	 */
	public static String listDept(String access_token, String pid) {
		// https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s",
						access_token);
		if (StringUtils.isNotEmpty(pid)) {
			url += "&id=" + pid;
		}
		return NETUtils.httpGet(url);
	}

	/**
	 * 增加部门
	 * 
	 * @param access_token
	 * @param dept
	 * @return
	 */
	public static String createDept(String access_token, Department dept) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(dept);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 更新部门信息
	 * 
	 * @param access_token
	 * @param dept
	 * @return
	 */
	public static String updateDept(String access_token, Department dept) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(dept);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 删除部门
	 * 
	 * @param access_token
	 * @param id
	 * @return
	 */
	public static String deleteDept(String access_token, String id) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=%s&id=%s",
						access_token, id);
		return NETUtils.httpGet(url);
	}

	// TODO ------管理成员 ------
	/**
	 * 创建成员
	 * 
	 * @param access_token
	 * @param member
	 * @return
	 */
	public static String createMember(String access_token, Member member) {
		// https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=ACCESS_TOKEN
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(member);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 更新成员
	 * 
	 * @param access_token
	 * @param member
	 * @return
	 */
	public static String updateMember(String access_token, Member member) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(member);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 删除成员
	 * 
	 * @param access_token
	 * @param userid
	 * @return
	 */
	public static String deleteMember(String access_token, String userid) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=%s&userid=%s",
						access_token, userid);
		return NETUtils.httpGet(url);
	}

	/**
	 * 批量删除成员
	 * 
	 * @param access_token
	 * @param useridlist
	 * @return
	 */
	public static String batchDeleteMember(String access_token,
			List<String> useridlist) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/user/batchDelete?access_token=%s",
						access_token);
		Map<String, List<String>> userids = new HashMap<String, List<String>>();
		userids.put("useridlist", useridlist);
		String jsonString = CommonUtils.object2Json(userids);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 获取成员
	 * 
	 * @param access_token
	 * @param userid
	 * @return
	 */
	public static String getMember(String access_token, String userid) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s",
						access_token, userid);
		return NETUtils.httpGet(url);
	}

	/**
	 * 获取部门成员
	 * 
	 * @param access_token
	 * @param deptid
	 * @param fetch_child
	 * @param status
	 * @return
	 */
	public static String simpleListMember(String access_token, int deptid,
			int fetch_child, int status) {
		// https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD&status=STATUS
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=%s&department_id=%s&fetch_child=%s&status=%s",
						access_token, deptid, fetch_child, status);
		return NETUtils.httpGet(url);
	}

	/**
	 * 获取部门成员(详情)
	 * 
	 * @param access_token
	 * @param deptid
	 * @param fetch_child
	 * @param status
	 * @return
	 */
	public static String listMember(String access_token, int deptid,
			int fetch_child, int status) {
		// https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD&status=STATUS
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%s&fetch_child=%s&status=%s",
						access_token, deptid, fetch_child, status);
		return NETUtils.httpGet(url);
	}

	/**
	 * 邀请成员关注 [为避免骚扰成员，企业应遵守以下邀请规则：每月邀请的总人次不超过成员上限的2倍；每7天对同一个成员只能邀请一次]
	 * 
	 * @param access_token
	 * @param userid
	 * @return
	 */
	public static String inviteMember(String access_token, String userid) {
		// https://qyapi.weixin.qq.com/cgi-bin/invite/send?access_token=ACCESS_TOKEN
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/invite/send?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(userid);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	public static void main(String[] args) {
		getAccessToken();
		String access_token = "aKOHhhnPsTNqgfYZ188uTRsOPcyUqagpAPl0DchZ-yco1dYQTKI4I5B5Ifd152Ud_oHZXgwFO3apa6UAFNT_9w";

		Department dept = new Department();
		String name = "jdj";
		String id = null;
		dept.setName(name);
		dept.setParentid("1");
		logger.info(createDept(access_token, dept));

		String deptsJson = listDept(access_token, "1");
		logger.info(deptsJson);
		RespDeparment obj = CommonUtils.jsonToObject(RespDeparment.class,
				deptsJson);
		List<Department> depts = obj.getDepartment();
		for (Department dept_ : depts) {
			id = dept_.getId();
			logger.info("id : " + id);
			if (StringUtils.equals(dept_.getName(), name)) {
				name += "空时";
				dept_.setName(name);
				updateDept(access_token, dept_);
				break;
			}
		}

		deptsJson = listDept(access_token, "1");
		logger.info(deptsJson);

		deleteDept(access_token, id);

		deptsJson = listDept(access_token, "1");
		logger.info(deptsJson);
	}
}
