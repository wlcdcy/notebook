package com.example.weixin;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.commons.CommonUtils;
import com.weixin.qy.entity.Article;
import com.weixin.qy.entity.Material;
import com.weixin.qy.entity.MaterialQuery;
import com.weixin.qy.rests.WeiXinAPIUtil;

public class AsyncTask {
	String access_token = "iJ7JO4r17JvI8EHil4iToHLBzCtV2oaZDBLm2CDyS6sfDTvpHfTQjJHfcd-E-tAVRzKt6IoTDxgDttpkhXsCAg";
	String imagePath = "d:/20150612113904.png";
	String media_id = "2J4IJTOBrh7XAITLbn2qpH1yV1nMf5easSeLte8T5H9zna2qrQmyAePBgPeUvZDp3pGBhzAXK-5mkFjxQzOwXAA";
	String material_id = "20FWTCmros1WAie8W9FCxDuX2xsFKy3X-tdi6bJ9oymR3sfYOuYH20y2mUvjA7KhD";
	int agentid = 0;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// @Test
	public void test() {
		File file = new File(imagePath);
		String jsonString = WeiXinAPIUtil.mediaUpload(access_token, "image",
				file);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		assertNotNull(result.get("media_id"));

	}

	// @Test
	public void materialUpload4Other() {
		File file = new File(imagePath);
		String jsonString = WeiXinAPIUtil.materialUpload4Other(access_token,
				"image", agentid, file);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		media_id = (String) result.get("media_id");
		System.out.println(media_id);
		assertNotNull(media_id);

	}

	// @Test
	public void materialGet4OtherByStream() {
		media_id = "2J4IJTOBrh7XAITLbn2qpH1yV1nMf5easSeLte8T5H9zna2qrQmyAePBgPeUvZDp3pGBhzAXK-5mkFjxQzOwXAA";
		InputStream ins = WeiXinAPIUtil.materialGet4OtherByStream(access_token,
				media_id, agentid);
		try {
			FileOutputStream os = new FileOutputStream(imagePath + "0");
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = ins.read(b)) > 0) {
				os.write(b, 0, len);
			}
			os.close();
			ins.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void materialGetDownload() {
		media_id = "2J4IJTOBrh7XAITLbn2qpH1yV1nMf5easSeLte8T5H9zna2qrQmyAePBgPeUvZDp3pGBhzAXK-5mkFjxQzOwXAA";
		WeiXinAPIUtil
				.materialDown4Other(access_token, media_id, agentid, "d:/");
	}

	// @Test
	public void materialUpload() {
		Material material = new Material();
		material.setAgentid(agentid);
		Map<String, List<Article>> mpnews = new HashMap<String, List<Article>>();
		List<Article> articles = new ArrayList<Article>();
		Article article = new Article();
		article.setAuthor("author");
		article.setContent("content");
		article.setShow_cover_pic("1");
		article.setTitle("title");
		article.setThumb_media_id(media_id);
		articles.add(article);

		mpnews.put("articles", articles);
		material.setMpnews(mpnews);
		String jsonString = WeiXinAPIUtil
				.materialUpload(access_token, material);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		media_id = (String) result.get("media_id");
		System.out.println(media_id);
		assertNotNull(media_id);

	}

	// @Test
	public void materialGet() {

		Object a = WeiXinAPIUtil
				.materialGet(access_token, material_id, agentid);
		Map<?, ?> result = null;
		if (a instanceof String) {
			result = CommonUtils.jsonToObject(Map.class, (String) a);
			String type = (String) result.get("type");
			assertEquals(type, type, "mpnews");
		} else if (a instanceof InputStream) {
			readStream((InputStream) a);
		}

	}

	// @Test
	public void materialDelete() {

		String jsonString = WeiXinAPIUtil.materialDelete(access_token,
				material_id, agentid);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		int errcode = (Integer) result.get("errcode");
		assertEquals((String) result.get("errmsg"), errcode, 0);

	}

	@Test
	public void materialCount() {

		String jsonString = WeiXinAPIUtil.materialCount(access_token, agentid);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		int errcode = (Integer) result.get("errcode");
		assertEquals((String) result.get("errmsg"), errcode, 0);
	}

	@Test
	public void materialList() {
		MaterialQuery param = new MaterialQuery();
		param.setAgentid(agentid);
		param.setType("image");
		;
		param.setOffset(0);
		param.setCount(0);
		String jsonString = WeiXinAPIUtil.materialList(access_token, param);
		Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
		int errcode = (Integer) result.get("errcode");
		assertEquals((String) result.get("errmsg"), errcode, 0);
	}

	private void readStream(InputStream is) {
		try {
			FileOutputStream os = new FileOutputStream(imagePath + "0");
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = is.read(b)) > 0) {
				os.write(b, 0, len);
			}
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
