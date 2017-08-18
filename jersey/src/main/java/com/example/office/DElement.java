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
     * 字数
     */
    private int wordNnumber;
    /**
     * 字符数
     */
    private int charNumber;
    
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
    public int getWordNnumber() {
        return wordNnumber;
    }
    public void setWordNnumber(int wordNnumber) {
        this.wordNnumber = wordNnumber;
    }
    public int getCharNumber() {
        return charNumber;
    }
    public void setCharNumber(int charNumber) {
        this.charNumber = charNumber;
    }
}