package com.example.weixin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.example.commons.CommonUtils;
import com.weixin.qy.entity.Member;
import com.weixin.qy.entity.RespMember;
import com.weixin.qy.rests.WeiXinAPIUtil;

public class MemberManage {

	String access_token = "iJ7JO4r17JvI8EHil4iToHLBzCtV2oaZDBLm2CDyS6sfDTvpHfTQjJHfcd-E-tAVRzKt6IoTDxgDttpkhXsCAg";
	Member member = new Member();
	List<Integer> depts = new ArrayList<Integer>();
	String userid = "lisa";
	String name = "丽萨";
	String name_ = name + "2";
	int dept = 2;

	@Test
	public void createMember() {
		member.setUserid(userid);
		member.setName(name);
		member.setMobile("18092687919");
		depts.add(dept);
		member.setDepartment(depts);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class,
				WeiXinAPIUtil.createMember(access_token, member));
		assertEquals((String) result.get("errmsg"),
				((Integer) result.get("errcode")).intValue(), 0);
	}

	@Test
	public void updateMember() {
		member.setUserid(userid);
		member.setName(name_);
		member.setMobile("18092687919");
		depts.add(dept);
		member.setDepartment(depts);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class,
				WeiXinAPIUtil.updateMember(access_token, member));
		assertEquals((String) result.get("errmsg"),
				((Integer) result.get("errcode")).intValue(), 0);
	}

	@Test
	public void getMember() {
		String jsonString = WeiXinAPIUtil.getMember(access_token, userid);
		Map<?, ?> m = CommonUtils.jsonToObject(Map.class, jsonString);
		assertEquals("++++++++++++++++++", m.get("name"), name_);
	}

	@Test
	public void listMember() {
		String jsonString = WeiXinAPIUtil.listMember(access_token, 2, 1, 0);
		RespMember m = CommonUtils.jsonToObject(RespMember.class, jsonString);
		assertEquals("++++++++++++++++++", m.getUserlist().size(), 3);
		assertNotNull("listMember error", m.getUserlist().get(0).getStatus());
	}

	@Test
	public void listSimpleMember() {
		String jsonString = WeiXinAPIUtil.simpleListMember(access_token, 2, 1,
				0);
		RespMember m = CommonUtils.jsonToObject(RespMember.class, jsonString);
		assertEquals("++++++++++++++++++", m.getUserlist().size(), 3);
		assertNull("simpleListMember error", m.getUserlist().get(0).getStatus());
	}

	// @Test
	public void deleteMember() {
		String jsonString = WeiXinAPIUtil.deleteMember(access_token, userid);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		assertEquals((String) result.get("errmsg"),
				((Integer) result.get("errcode")).intValue(), 0);
	}

	@Test
	public void batchDeleteMember() {
		List<String> useridlist = new ArrayList<String>();
		useridlist.add(userid);
		String jsonString = WeiXinAPIUtil.batchDeleteMember(access_token,
				useridlist);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		assertEquals((String) result.get("errmsg"),
				((Integer) result.get("errcode")).intValue(), 0);
	}
}
