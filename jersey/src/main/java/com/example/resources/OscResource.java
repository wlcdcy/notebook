package com.example.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.OscUtils;

@Path("/webhook/osc")
public class OscResource {
	private static Logger logger = LoggerFactory.getLogger(OscResource.class);

	@Path("/index")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public static Response index(@Context HttpServletResponse resp)
			throws IOException {
		String html = "welcome to <a href='http://hiwork.cc'>hiwork</a>.";
		return Response.ok(html).build();
	}

	@Path("/redirect")
	@GET
	public static Response redirect(@QueryParam("source") String source)
			throws IOException {
		String default_url = "http://oschina.net";
		URI location = null;
		if (source != null) {
			try {
				location = new URI(source);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		if (location == null) {
			try {
				location = new URI(default_url);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return Response.temporaryRedirect(location).build();
	}

	@Path("/auth")
	@GET
	public static void oauth2Auth(@Context HttpServletResponse resp) {
		try {
			resp.sendRedirect(OscUtils.getOauth2AuthUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Path("/authback")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static String auth_back(@QueryParam("code") String code,
			@QueryParam("state") String state) {
		OscUtils.auth_code = code;
		logger.info(String.format("auth code is : %s", code));
		logger.info(String.format("res state is : %s", state));

		// fetch access token
		OscUtils.fetchOauth2Token(code);
		return "";
	}

	@Path("/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public static String search(String catalog, String words) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("access_token", OscUtils.access_token);
		data.put("catalog", catalog);
		data.put("q", words);
		data.put("dataType", "json");
		String req_data = "";
		try {
			req_data = new ObjectMapper().writeValueAsString(data);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (StringUtils.isEmpty(req_data)) {
			logger.error("req_data is empty : return");
			return "";
		}
		logger.info(String.format("req_data is : %s", req_data));

		return OscUtils.search(catalog, words);

	}

}
