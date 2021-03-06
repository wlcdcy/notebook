package com.example.apps;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("app")
public class MyApp extends Application {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet();
        s.add(HelloService.class);
        return s;
    }
}
