package com.weixin.qy.entity.res;

import java.util.List;

import com.weixin.qy.entity.Department;

public class DeparmentRES {
	String errcode;
	String errmsg;
	List<Department> department;

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

	public List<Department> getDepartment() {
		return department;
	}

	public void setDepartment(List<Department> department) {
		this.department = department;
	}

}
