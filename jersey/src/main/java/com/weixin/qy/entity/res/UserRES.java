package com.weixin.qy.entity.res;

import java.util.List;

import com.weixin.qy.entity.User;

public class UserRES {
	String errcode;
	String errmsg;
	List<User> userlist;

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

	public List<User> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<User> userlist) {
		this.userlist = userlist;
	}

}
