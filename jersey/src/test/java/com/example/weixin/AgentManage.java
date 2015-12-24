package com.example.weixin;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.example.commons.CommonUtils;
import com.weixin.qy.entity.Agent;
import com.weixin.qy.entity.AgentDetail;
import com.weixin.qy.entity.res.AgentRES;
import com.weixin.qy.entity.res.AgentsRES;
import com.weixin.qy.rests.WeiXinAPIUtil;

public class AgentManage {
	String access_token = "iJ7JO4r17JvI8EHil4iToHLBzCtV2oaZDBLm2CDyS6sfDTvpHfTQjJHfcd-E-tAVRzKt6IoTDxgDttpkhXsCAg";
	int agentid;
	Agent agent;

	@Test
	public void test() {
		List<AgentDetail> agentlist = AgentList();
		agentid = agentlist.get(agentlist.size() - 1).getAgentid();
		agent = AgentGet();
		AgentSet();
	}

	List<AgentDetail> AgentList() {
		String jsonString = WeiXinAPIUtil.AgentList(access_token);
		AgentsRES agentsres = CommonUtils.jsonToObject(AgentsRES.class,
				jsonString);
		assertEquals(agentsres.getErrmsg(), agentsres.getErrcode(), 0);

		return agentsres.getAgentlist();
	}

	Agent AgentGet() {
		String jsonString = WeiXinAPIUtil.AgentGet(access_token, agentid);
		AgentRES agentres = CommonUtils
				.jsonToObject(AgentRES.class, jsonString);
		assertEquals(agentres.getErrmsg(), agentres.getErrcode(), 0);

		return (Agent) agentres;
	}

	void AgentSet() {
		Agent ag = new Agent();
		ag.setAgentid(agent.getAgentid());
		ag.setDescription(agent.getDescription() + "|");
		String jsonString = WeiXinAPIUtil.AgentSet(access_token, ag);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		assertEquals((String) result.get("errmsg"), result.get("errcode"), 0);
	}

}
