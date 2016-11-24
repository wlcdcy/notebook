package com.example.apps;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("example")
public class HelloService {
    private static final Logger LOG = LoggerFactory.getLogger(HelloService.class);

    @SuppressWarnings("unused")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(@Context UriInfo ui, @Context HttpHeaders hh) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        MultivaluedMap<String, String> pathParams = ui.getPathParameters();
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> cookieParams = hh.getCookies();

        if (LOG.isDebugEnabled()) {
            LOG.info("queryParams - " + queryParams.size());
            Set<String> queryParamKeys = queryParams.keySet();
            for (String queryParamKey : queryParamKeys) {
                LOG.debug(queryParamKey + " - " + queryParams.getFirst(queryParamKey));
            }

            LOG.info("pathParams - " + pathParams.size());
            Set<String> pathParamKeys = pathParams.keySet();
            for (String pathParamKey : pathParamKeys) {
                LOG.debug(pathParamKey + " - " +pathParams.getFirst(pathParamKey));
            }

            LOG.info("headerParams - " + headerParams.size());
            Set<String> headerParamKeys = headerParams.keySet();
            for (String headerParamKey : headerParamKeys) {
                LOG.debug(String.format("%s - %s", headerParamKey, headerParams.getFirst(headerParamKey)));
            }
            
            LOG.info("cookieParams - " + cookieParams.size());
            Set<String> cookieParamKeys = cookieParams.keySet();
            for (String cookieParamKey : cookieParamKeys) {
                LOG.debug(String.format("%s - %s", cookieParamKey, cookieParams.get(cookieParamKey)));
            }
        }

        LOG.info(ui.getBaseUri().getPath());
        return "Hello, Let go! use Application Model Set";
    }
}
