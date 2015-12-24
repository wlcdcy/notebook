package com.weixin.qy.entity.res;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.weixin.qy.entity.AgentDetail;
import com.weixin.qy.entity.User;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AgentRES extends AgentDetail {
	private int errcode;
	private String errmsg;
	private Map<String, List<User>> allow_userinfos;
	private Map<String, List<Integer>> allow_partys;
	private Map<String, List<Integer>> allow_tags;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public Map<String, List<User>> getAllow_userinfos() {
		return allow_userinfos;
	}

	public void setAllow_userinfos(Map<String, List<User>> allow_userinfos) {
		this.allow_userinfos = allow_userinfos;
	}

	public Map<String, List<Integer>> getAllow_partys() {
		return allow_partys;
	}

	public void setAllow_partys(Map<String, List<Integer>> allow_partys) {
		this.allow_partys = allow_partys;
	}

	public Map<String, List<Integer>> getAllow_tags() {
		return allow_tags;
	}

	public void setAllow_tags(Map<String, List<Integer>> allow_tags) {
		this.allow_tags = allow_tags;
	}

}
