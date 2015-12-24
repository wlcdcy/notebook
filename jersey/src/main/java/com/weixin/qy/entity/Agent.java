package com.weixin.qy.entity;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Agent {
	private int agentid;
	private String name;
	private int report_location_flag;
	private String logo_mediaid;
	private String description;
	private String redirect_domain;
	private int isreportuser;
	private int isreportenter;

	public int getAgentid() {
		return agentid;
	}

	public void setAgentid(int agentid) {
		this.agentid = agentid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getReport_location_flag() {
		return report_location_flag;
	}

	public void setReport_location_flag(int report_location_flag) {
		this.report_location_flag = report_location_flag;
	}

	public String getLogo_mediaid() {
		return logo_mediaid;
	}

	public void setLogo_mediaid(String logo_mediaid) {
		this.logo_mediaid = logo_mediaid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRedirect_domain() {
		return redirect_domain;
	}

	public void setRedirect_domain(String redirect_domain) {
		this.redirect_domain = redirect_domain;
	}

	public int getIsreportuser() {
		return isreportuser;
	}

	public void setIsreportuser(int isreportuser) {
		this.isreportuser = isreportuser;
	}

	public int getIsreportenter() {
		return isreportenter;
	}

	public void setIsreportenter(int isreportenter) {
		this.isreportenter = isreportenter;
	}

}
