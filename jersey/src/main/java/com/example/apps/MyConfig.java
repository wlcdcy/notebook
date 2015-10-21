package com.example.apps;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;


public class MyConfig extends ResourceConfig {

	public MyConfig() {
		packages("com.example.apps");
		
		register(org.glassfish.jersey.server.filter.UriConnegFilter.class);
        register(org.glassfish.jersey.server.validation.ValidationFeature.class);
        register(org.glassfish.jersey.server.spring.SpringComponentProvider.class);
        register(org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider.class);
        property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
	}

}
