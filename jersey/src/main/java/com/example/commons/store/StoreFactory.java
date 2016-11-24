package com.example.commons.store;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreFactory {
	private IStore storeProvider=null;
	private static StoreFactory factory=null;
	private static final Logger LOG = LoggerFactory.getLogger(StoreFactory.class);
	
	private StoreFactory() {
	}
	
	public static StoreFactory newInstance(){
		if(factory==null){
			factory = new StoreFactory();
		}
		return factory;
	}
	
	public IStore defaultStore(String provider){
	    return createStore(provider,null);
    }
	
	public IStore createStore(String provider,Properties prop){
        try {
            if(storeProvider==null){
                storeProvider = (IStore)Class.forName(provider).newInstance();
                storeProvider.init(prop);
            }
            return storeProvider;
        } catch (Exception e) {
            LOG.warn(e.getMessage(),e);
        }
        return null;
    }
}
