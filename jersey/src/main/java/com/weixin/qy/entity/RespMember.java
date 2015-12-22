package com.weixin.qy.entity;

import java.util.List;

public class RespMember {
	String errcode;
	String errmsg;
	List<Member> userlist;

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public List<Member> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<Member> userlist) {
		this.userlist = userlist;
	}

}
