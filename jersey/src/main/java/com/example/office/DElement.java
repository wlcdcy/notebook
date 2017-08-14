package com.example.office;

import java.util.List;

public class DElement {
    /**
     * 正文
     */
    List<PElement> parts;
    /**
     * 页眉
     */
    private List<BElement> headers;
    /**
     * 页脚
     */
    private List<BElement> footers;
    /**
     * 脚注
     */
    private List<BElement> footnotes;
    
    /**
     * 尾注
     */
    private List<BElement> endnotes;
    
    /**
     * 文本框
     */
    private List<BElement> textboxs;
    
    /**
     * 字数
     */
    private Integer wordNnumber;
    /**
     * 字符数
     */
    private Integer charNumber;
    public List<PElement> getParts() {
        return parts;
    }
    public void setParts(List<PElement> parts) {
        this.parts = parts;
    }
    public List<BElement> getHeaders() {
        return headers;
    }
    public void setHeaders(List<BElement> headers) {
        this.headers = headers;
    }
    public List<BElement> getFooters() {
        return footers;
    }
    public void setFooters(List<BElement> footers) {
        this.footers = footers;
    }
    public List<BElement> getFootnotes() {
        return footnotes;
    }
    public void setFootnotes(List<BElement> footnotes) {
        this.footnotes = footnotes;
    }
    public List<BElement> getEndnotes() {
        return endnotes;
    }
    public void setEndnotes(List<BElement> endnotes) {
        this.endnotes = endnotes;
    }
    public List<BElement> getTextboxs() {
        return textboxs;
    }
    public void setTextboxs(List<BElement> textboxs) {
        this.textboxs = textboxs;
    }
    public Integer getWordNnumber() {
        return wordNnumber;
    }
    public void setWordNnumber(Integer wordNnumber) {
        this.wordNnumber = wordNnumber;
    }
    public Integer getCharNumber() {
        return charNumber;
    }
    public void setCharNumber(Integer charNumber) {
        this.charNumber = charNumber;
    }
    
}