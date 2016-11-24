package com.ovturn.hdfs.entity;

import java.io.Serializable;

public class StoreInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long storeId;
    private String storePath;
    private String compPath;
    
    public String getStorePath() {
        return storePath;
    }
    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }
    public String getCompPath() {
        return compPath;
    }
    public void setCompPath(String compPath) {
        this.compPath = compPath;
    }
    public long getStoreId() {
        return storeId;
    }
    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }
    

}
