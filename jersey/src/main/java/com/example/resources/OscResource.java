package com.example.resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.OscUtil;

@Path("/webhook/osc")
public class OscResource {
	private static Logger logger = LoggerFactory.getLogger(OscResource.class);
	
	
	@Path("/index")
	@GET
	public static void index(@Context HttpServletResponse resp) throws IOException{
		String html="welcome to hiwork.";
		resp.getOutputStream().write(html.getBytes());
	}
	
	@Path("/auth")
	@GET
	public static void oauth2Auth(@Context HttpServletResponse resp){
		try {
			resp.sendRedirect(OscUtil.getOauth2AuthUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Path("/authback")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static String auth_back(@QueryParam("code") String code,@QueryParam("state") String state){
		OscUtil.auth_code = code;
		logger.info(String.format("auth code is : %s", code));
		logger.info(String.format("res state is : %s", state));
		
		//fetch access token
		OscUtil.fetchOauth2Token(code);
		return "";
	}
	
	
	
	@Path("/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public static String search(String catalog,String words){
		Map<String,String> data = new HashMap<String,String>();
		data.put("access_token", OscUtil.access_token);
		data.put("catalog", catalog);
		data.put("q", words);
		data.put("dataType", "json");
		String req_data="";
		try {
			req_data= new ObjectMapper().writeValueAsString(data);
			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(StringUtils.isEmpty(req_data)){
			logger.error("req_data is empty : return");
			return "";
		}
		logger.info(String.format("req_data is : %s", req_data));
		
		return OscUtil.search(catalog,words);
		
	}
	
}
