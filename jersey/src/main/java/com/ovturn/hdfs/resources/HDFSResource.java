package com.ovturn.hdfs.resources;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ovturn.hdfs.entity.FileInfo;
import com.ovturn.hdfs.service.HDFSHandler;

@Path("/file")
public class HDFSResource {

    HDFSHandler handler = new HDFSHandler();
    private static final Logger LOG = LoggerFactory.getLogger(HDFSResource.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(@Context SecurityContext sc) {
        String user = sc.getUserPrincipal().getName();
        if (sc.isUserInRole("tomcat")) {
            return String.format("Hello %s, Let go! use Application Model Set", user);
        }
        return "Hello, Let go! use ResourceConfig Scanning";
    }

    @Path("/list/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<FileInfo> list(@QueryParam("page") int page,
            @QueryParam("fname") String fname, @QueryParam("fmime") String fmime) {
        LOG.debug("--------------- "+fname);
        return handler.listFiles(fname, fmime, page<1 ? 0:(page - 1) * 1000);
    }

    @Path("/down/{fstoreid}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downlond(@PathParam("fstoreid") long fstoreid) {
        FileInfo fileInfo = handler.getFileInfo(fstoreid);
        String fname = String.valueOf(fstoreid);
        if (fileInfo != null) {
            fname = fileInfo.getFileName();
        }
        InputStream ins = handler.downFile(fstoreid);
        if (ins == null) {
            return Response.status(404).build();
        }
        try {
            fname =URLEncoder.encode(fname,Charset.forName("utf-8").name());
        } catch (UnsupportedEncodingException e) {
            LOG.warn(e.getMessage(),e);
        }
        return Response.ok(ins).header("Content-disposition", "attachment;filename=" +fname ).header("content-type", "vide/mpeg").build();
    }

    @Path("/delete/{fstoreid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean delete(@PathParam("fstoreid") long fstoreid) {
        return handler.removeFile(fstoreid);
    }
    
    @Path("/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public int[] deleteAll(List<Long> fstoreids) {
        return handler.removeFiles(fstoreids);
    }

}
