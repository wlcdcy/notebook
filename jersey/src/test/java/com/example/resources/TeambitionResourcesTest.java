package com.example.resources;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

import junit.framework.Assert;



public class TeambitionResourcesTest extends JerseyTest {
	
	
	public TeambitionResourcesTest(){
		super("com.example.resources");
	}

	public void testProject(){
		WebResource web_resources = resource();
		String resp_body = web_resources.path("webhook/teambition/index").get(String.class);
		Assert.assertEquals("welcome to hiwork.", resp_body);
	}
}
