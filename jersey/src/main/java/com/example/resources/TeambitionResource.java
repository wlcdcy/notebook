package com.example.resources;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.TeambitionUtils;

@Path("/webhook/teambition")
public class TeambitionResource {
	private static Logger logger = LoggerFactory.getLogger(TeambitionResource.class);
	
	
	@Path("/index")
	@GET
	public void index(@Context HttpServletResponse resp) throws IOException{
		String html="welcome to hiwork.";
		resp.getOutputStream().write(html.getBytes());
	}
	
	@Path("/auth")
	@GET
	public void oauth2Auth(@Context HttpServletRequest req,@Context HttpServletResponse resp){
		try {
			resp.sendRedirect(TeambitionUtils.getOAuthUrl(TeambitionUtils.redirect_uri));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Path("/auth/callback")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String auth_back(@QueryParam("code") String code,@QueryParam("state") String state){
		TeambitionUtils.auth_code = code;
		logger.info(String.format("auth code is : %s", code));
		logger.info(String.format("res state is : %s", state));
		
		//fetch access_token request
		String resp_data = TeambitionUtils.fetchAccessToken(code);
		
		try {
			String access_token = (String) new ObjectMapper().readValue(resp_data, Map.class).get("access_token");
			//check access_token
			if(TeambitionUtils.checkAccessToken(access_token)){
				TeambitionUtils.access_token = access_token;
				return access_token;
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return "";
	}
	
	@Path("/project")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String project(@Context HttpServletRequest req ,Map<String, Object> jsonData){
		String content_type = req.getContentType();
		logger.info(content_type);
		
		return content_type;
	}
	
	@Path("/organization")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String organization(@Context HttpServletRequest req ,Map<String, Object> jsonData){
		String content_type = req.getContentType();
		logger.info(content_type);

		return content_type;
	}
	
}
