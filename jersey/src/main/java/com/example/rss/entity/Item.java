package com.example.rss.entity;

import java.util.Date;

public class Item {
    private String title;
    private String describe;
    private Date pubishDate;
    private String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Date getPubishDate() {
        return pubishDate;
    }

    public void setPubishDate(Date pubishDate) {
        this.pubishDate = pubishDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
