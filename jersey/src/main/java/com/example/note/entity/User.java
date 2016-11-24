package com.example.note.entity;

public class User {

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户总的空间大小，单位字节
     */
    private long totalSize;

    /**
     * 用户已经使用了的空间大小，单位字节
     */
    private long usedSize;

    /**
     * 用户注册时间，单位毫秒
     */
    private long registerTime;

    /**
     * 用户最后登录时间，单位毫秒
     */
    private long lastLoginTime;

    /**
     * 用户最后修改时间，单位毫秒
     */
    private long lastModifyTime;

    /**
     * 应用的默认笔记本（对于每个第三方应用，都分别在用户的笔记空间中对应一个默认笔记本，笔记本名可以在申请应用时指定，如果不指定则默认为“来自
     * <应用名称 >”，这样在使用OpenAPI创建笔记时，如果第三方应用不指定笔记本，则自动创建在该默认笔记本中。）
     */
    private String default_notebook;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(long usedSize) {
        this.usedSize = usedSize;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getDefault_notebook() {
        return default_notebook;
    }

    public void setDefault_notebook(String default_notebook) {
        this.default_notebook = default_notebook;
    }

}
