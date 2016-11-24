package com.ovturn.hdfs.entity;

import java.io.Serializable;

public class FileInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String fileName;
    private long fileStoreId;
    private long fileSize;
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public long getFileStoreId() {
        return fileStoreId;
    }
    public void setFileStoreId(long fileStoreId) {
        this.fileStoreId = fileStoreId;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
}
