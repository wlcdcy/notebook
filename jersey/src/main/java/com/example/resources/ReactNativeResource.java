package com.example.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/react")
public class ReactNativeResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(@Context SecurityContext sc) {
        String user = sc.getUserPrincipal().getName();
        if (sc.isUserInRole("tomcat")) {
            return String.format("Hello %s, Let go! use Application Model Set", user);
        }
        return "Hello, Let go! use ResourceConfig Scanning";
    }
}
