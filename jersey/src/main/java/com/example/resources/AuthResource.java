package com.example.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.sun.jersey.spi.resource.Singleton;

@Path("/auth")
@Singleton
public class AuthResource {
	@Context
	SecurityContext securityContext;

	@GET
	public String getUserPrincipal() {
		return securityContext.getUserPrincipal().getName();
	}
	
	public String d(@Context SecurityContext sc){
		return "";
	}
	@Path("/sayhello")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sayHello(@Context SecurityContext sc){
		return "hello";
	}
}
