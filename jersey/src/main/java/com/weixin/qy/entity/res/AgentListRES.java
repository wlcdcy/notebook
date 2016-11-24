package com.weixin.qy.entity.res;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.weixin.qy.entity.AgentDetail;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AgentListRES {
    private int errcode;
    private String errmsg;
    private List<AgentDetail> agentlist;

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

    public List<AgentDetail> getAgentlist() {
        return agentlist;
    }

    public void setAgentlist(List<AgentDetail> agentlist) {
        this.agentlist = agentlist;
    }

}
