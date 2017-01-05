package com.example.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(@Context SecurityContext sc) {
        String user = sc.getUserPrincipal().getName();
        if (sc.isUserInRole("tomcat")) {
            return String.format("Hello %s, Let go! use Application Model Set", user);
        }
        return "Hello, Let go! use ResourceConfig Scanning";
    }
    @Path("/json")
    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public List<Map<String,String>> getJson() {
        List<Map<String,String>> books = new ArrayList<>();
        for(int i=0;i<10 ;i++){
            Map<String,String> book = new HashMap<>();
            book.put("title", "THE WHISTLER"+i);
            book.put("author", "John Hello"+i);
            book.put("book_image", "https://s1.nyt.com/du/books/images/9780385541206.jpg");
            books.add(book);
        }
      return books;
  }

    @Path("/file")
    @POST
    @Produces({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void upload(@Context HttpServletRequest req) {

    }

    @Path("/form")
    @POST
    @Produces({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void form(@Context HttpServletRequest req) {

    }

}
