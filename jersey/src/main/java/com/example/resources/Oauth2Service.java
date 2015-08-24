package com.example.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.oauth.APIOauth2;
import com.example.oauth.Oauth2Factory;

/**
 * Oauth2 认证服务接口
 * 
 * @author Administrator
 *
 */
@Path("/oauth")
public class Oauth2Service {

	private static Logger logger = LoggerFactory.getLogger(Oauth2Service.class);
	private Oauth2Factory oauth2Factory = Oauth2Factory.getInstance();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@Context HttpServletRequest req) {
		return "jersey : oauth service";
	}

	/**
	 * 重定向到认证授权地址
	 * 
	 * @param req
	 * @return
	 */
	@Path("/{appcode}")
	@GET
	public Response oauth2Auth(@Context HttpServletRequest req,
			@PathParam("appcode") String appcode) {

		APIOauth2 api = oauth2Factory.getAppOauth2Pravider(appcode);
		if(api==null){
			return Response.ok("not support "+appcode , MediaType.TEXT_PLAIN).build();
		}
		String callback=String.format("http://36.46.254.200/jersey/oauth/%s/callback", appcode);
		String oauth_url = api.getOauth2Url(callback);
		return Response.seeOther(UriBuilder.fromUri(oauth_url).build()).build();
	}

	/**
	 * 认证授权成功回调地址
	 * 
	 * @param code
	 * @param state
	 * @return
	 */
	@Path("{appcode}/callback")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String auth_back(@PathParam("appcode") String appcode,
			@QueryParam("code") String auth_code,
			@QueryParam("state") String state) {

		logger.info(String.format(
				"oauth code is : %s	res state is : %s", auth_code,state));

		// TODO 检查state是否来自授权服务的请求

		// get access_token request
		APIOauth2 api = oauth2Factory.getAppOauth2Pravider(appcode);
		String access_token = api.getAccessToken(auth_code);

		// TODO 持久化用户的 auth_code&access_cod;
		// TeambitionApi.auth_code = auth_code;
		// TeambitionApi.access_token = access_token;

		return "";
	}
	
}
