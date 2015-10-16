package com.example.apps;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

@Path("example")
public class HelloService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt(@Context UriInfo ui,@Context HttpHeaders hh) {
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		MultivaluedMap<String, String> pathParams = ui.getPathParameters();
		
		MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
		Map<String,Cookie> cookieParams = hh.getCookies();
		System.out.println(ui.getBaseUri());
		return "Hello, Let go! use Application Model Set";
	}
}
