package com.example.resources;

import org.junit.*;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class TeambitionResourcesTest extends JerseyTest {

    public TeambitionResourcesTest() {
        super("com.example.resources");
    }

    public void testProject() {
        WebResource web_resources = resource();
        String resp_body = web_resources.path("webhook/teambition/index").get(String.class);
        Assert.assertEquals("welcome to hiwork.", resp_body);
    }
}
