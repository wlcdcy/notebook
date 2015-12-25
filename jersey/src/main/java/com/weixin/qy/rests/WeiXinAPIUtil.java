package com.weixin.qy.rests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
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
import com.weixin.qy.entity.Agent;
import com.weixin.qy.entity.Department;
import com.weixin.qy.entity.InviteUser;
import com.weixin.qy.entity.Material;
import com.weixin.qy.entity.Member;
import com.weixin.qy.entity.MaterialQuery;
import com.weixin.qy.entity.RespDeparment;

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

	// TODO
	// ------异步任务接口[异步任务接口用于大批量数据的处理，提交后接口即返回，企业号会在后台继续执行任务。执行完成后，通过任务事件通知企业获取结果]------
	/**
	 * 邀请成员关注
	 * 
	 * @param access_token
	 * @param inviteUser
	 * @return
	 */
	public static String batchInviteMember(String access_token,
			InviteUser inviteUser) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/batch/inviteuser?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(inviteUser);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	// TODO ------管理素材文件------
	/**
	 * 上传临时素材文件
	 * 
	 * @param access_token
	 * @param type
	 * @param file
	 * @return
	 */
	public static String mediaUpload(String access_token, String type, File file) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s",
						access_token, type);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(file.getName(), file);
		return NETUtils.httpPostWithMultipart(url, params);
	}

	/**
	 * 获取临时素材文件
	 * 
	 * @param access_token
	 * @param media_id
	 * @param clazz
	 *            [support type: InputStream|byte[]|String]
	 * @return
	 */
	public static <T> T mediaGet(String access_token, String media_id,
			Class<T> clazz) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s",
						access_token, media_id);
		return NETUtils.httpGet(url, clazz);
	}

	/**
	 * 上传图文消息素材
	 * 
	 * @param access_token
	 * @param type
	 * @param agentid
	 * @param file
	 * @return
	 */
	public static String materialUpload(String access_token, Material material) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/add_mpnews?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(material);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 上传其他类型永久素材【图片、语音、视频等媒体资源文件以及普通文件（如doc，ppt）】
	 * 
	 * @param access_token
	 * @param type
	 * @param agentid企业应用的id
	 * @param file
	 * @return
	 */
	public static String materialUpload4Other(String access_token, String type,
			int agentid, File file) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/add_material?agentid=%s&type=%s&access_token=%s",
						agentid, type, access_token);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(file.getName(), file);
		return NETUtils.httpPostWithMultipart(url, params);
	}

	/**
	 * 获取永久素材【图片、语音、视频等媒体资源文件以及普通文件（如doc，ppt）】
	 * 
	 * @param access_token
	 * @param media_id
	 * @param agentid
	 * @param clazz
	 *            [support type: InputStream|byte[]|String]
	 * @return
	 */
	public static <T> T materialGet(String access_token, String media_id,
			int agentid) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/get?access_token=%s&media_id=%s&agentid=%s",
						access_token, media_id, agentid);
		return NETUtils.httpGet2(url);
	}

	/**
	 * 获取永久素材
	 * 
	 * @param access_token
	 * @param media_id
	 * @param agentid
	 * @return
	 */
	public static InputStream materialGet4OtherByStream(String access_token,
			String media_id, int agentid) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/get?access_token=%s&media_id=%s&agentid=%s",
						access_token, media_id, agentid);
		return NETUtils.httpGet(url, InputStream.class);
	}

	/**
	 * 下载 永久素材
	 * 
	 * @param access_token
	 * @param media_id
	 *            素材id
	 * @param agentid
	 *            应用id
	 * @param dist
	 *            保存位置
	 */
	public static void materialDown4Other(String access_token, String media_id,
			int agentid, String dist) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/get?access_token=%s&media_id=%s&agentid=%s",
						access_token, media_id, agentid);
		CloseableHttpResponse response = NETUtils.httpGetStream(url);
		try {
			if (response.getStatusLine().getStatusCode() < 300) {
				HttpEntity entity = response.getEntity();
				// Header contentType = entity.getContentType();
				NameValuePair nvp = response.getFirstHeader(
						"Content-disposition").getElements()[0].getParameters()[0];
				InputStream is = null;
				OutputStream os = null;
				try {
					is = entity.getContent();
					os = new FileOutputStream(new File(dist, nvp.getValue()));
					byte[] b = new byte[1024 * 8];
					int len = 0;
					while ((len = is.read(b)) > 0) {
						os.write(b, 0, len);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} else {
				logger.info(response.toString());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除永久素材
	 * 
	 * @param access_token
	 * @param media_id
	 * @param agentid
	 * @return
	 */
	public static String materialDelete(String access_token, String media_id,
			int agentid) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/del?access_token=%s&agentid=%s&media_id=%s",
						access_token, agentid, media_id);
		return NETUtils.httpGet(url);
	}

	/**
	 * 修改永久图文素材
	 * 
	 * @param access_token
	 * @param material
	 * @return
	 */
	public static String materialUpdate(String access_token, Material material) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/update_mpnews?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(material);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 获取素材总数
	 * 
	 * @param access_token
	 * @param agentid
	 * @return
	 */
	public static String materialCount(String access_token, int agentid) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/get_count?access_token=%s&agentid=%s",
						access_token, agentid);
		return NETUtils.httpGet(url);
	}

	/**
	 * 获取素材列表
	 * 
	 * @param access_token
	 * @param param
	 * @return
	 */
	public static String materialList(String access_token, MaterialQuery param) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/material/batchget?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(param);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	// TODO ------管理企业号应用------
	/**
	 * 获取企业号应用
	 * 
	 * @param access_token
	 * @param agentid
	 * @return
	 */
	public static String AgentGet(String access_token, int agentid) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/agent/get?access_token=%s&agentid=%s",
						access_token, agentid);
		return NETUtils.httpGet(url);
	}

	/**
	 * 设置企业号应用
	 * 
	 * @param access_token
	 * @param agent
	 * @return
	 */
	public static String AgentSet(String access_token, Agent agent) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/agent/set?access_token=%s",
						access_token);
		String jsonString = CommonUtils.object2Json(agent);
		return NETUtils.httpPostWithJson(url, jsonString);
	}

	/**
	 * 获取应用概况列表
	 * 
	 * @param access_token
	 * @return
	 */
	public static String AgentList(String access_token) {
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/agent/list?access_token=%s",
						access_token);
		return NETUtils.httpGet(url);
	}

	public static void main(String[] args) {
		getAccessToken();
		String access_token = "m6Vr_nPgO_CIKH6C-xmUWlLzo29CAtX5p0gPMbLH9oEVGqwmdjCHP8_j23FXWSFVU-_aCa_QWj_5JK7yR22q0w";

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
