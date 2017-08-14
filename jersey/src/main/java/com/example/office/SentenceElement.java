package com.example.office;

import java.util.List;

public class SentenceElement {
    private int sentenceSerial;
    private ContentType contentType;
    private List<ContentElement> contents;
    private String text;//拆分时整段语句，程序中仅用来做变量
    private String[] childTexts;
    
    private Long bodyId;
    private Long sentenceId;
    /**
     * 译文内容
     */
    private String tranSyntagma;
    /**
     * 校对内容
     */
    private String proOfreadsyntagma;
    /**
     * 翻译留言
     */
    private String tranRemark;
    
    /**
     * 前台提交译文、校对、备注时使用
     */
    private int flag;
    
    private long orderId;
    
    private long sentenceTextId;
    
    private int ext_contenttype;//0:正常内容，1:非译
    
    public int getExt_contenttype() {
        return ext_contenttype;
    }

    public void setExt_contenttype(int ext_contenttype) {
        this.ext_contenttype = ext_contenttype;
    }

    public long getSentenceTextId() {
        return sentenceTextId;
    }

    public void setSentenceTextId(long sentenceTextId) {
        this.sentenceTextId = sentenceTextId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTranSyntagma() {
        return tranSyntagma;
    }

    public void setTranSyntagma(String tranSyntagma) {
        this.tranSyntagma = tranSyntagma;
    }

    public String getProOfreadsyntagma() {
        return proOfreadsyntagma;
    }

    public void setProOfreadsyntagma(String proOfreadsyntagma) {
        this.proOfreadsyntagma = proOfreadsyntagma;
    }

    public String getTranRemark() {
        return tranRemark;
    }

    public void setTranRemark(String tranRemark) {
        this.tranRemark = tranRemark;
    }

    public Long getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(Long sentenceId) {
        this.sentenceId = sentenceId;
    }

    public Long getBodyId() {
        return bodyId;
    }

    public void setBodyId(Long bodyId) {
        this.bodyId = bodyId;
    }

    public int getSentenceSerial() {
        return sentenceSerial;
    }

    public void setSentenceSerial(int sentenceSerial) {
        this.sentenceSerial = sentenceSerial;
    }

    public List<ContentElement> getContents() {
        return contents;
    }

    public void setContents(List<ContentElement> contents) {
        this.contents = contents;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String[] getChildTexts() {
        return childTexts;
    }

    public void setChildTexts(String[] childTexts) {
        this.childTexts = childTexts;
    }
    
}
