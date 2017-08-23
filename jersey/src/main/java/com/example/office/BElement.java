package com.example.office;

import java.util.List;

public class BElement {
    int index;
    
    /**
     * 元素在文档中的位置层级关系 父 子间用“|”隔开
     * 
     * 段落       n       n 段落序号。
     * 文本框  n*t     t 文本框序号。
     * 表格       n*r*c   r 表格单元格的行号，c表格单元格的列号。
     * 
     */
    String path;
    /**
     * p,t,b
     */
    String pName;
    boolean txbox = false;
    String name;
    int rowNum;
    int columnNum;
    /**
     * 字符数
     */
    private int charNumber;
    /**
     * 字数
     */
    private int wordNumber;
    
    /**
     * 原文
     */
    private List<SentenceElement> sentences;
    /**
     * 译文
     */
    private List<SentenceElement> tranSentences;
    /**
     * 校对
     */
    private List<SentenceElement> checkSentences;
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCharNumber() {
        return charNumber;
    }
    public void setCharNumber(int charNumber) {
        this.charNumber = charNumber;
    }
    public int getWordNumber() {
        return wordNumber;
    }
    public void setWordNumber(int wordNumber) {
        this.wordNumber = wordNumber;
    }
    public List<SentenceElement> getSentences() {
        return sentences;
    }
    public void setSentences(List<SentenceElement> sentences) {
        this.sentences = sentences;
    }
    public List<SentenceElement> getTranSentences() {
        return tranSentences;
    }
    public void setTranSentences(List<SentenceElement> tranSentences) {
        this.tranSentences = tranSentences;
    }
    public List<SentenceElement> getCheckSentences() {
        return checkSentences;
    }
    public void setCheckSentences(List<SentenceElement> checkSentences) {
        this.checkSentences = checkSentences;
    }
    public int getRowNum() {
        return rowNum;
    }
    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }
    public int getColumnNum() {
        return columnNum;
    }
    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getpName() {
        return pName;
    }
    public void setpName(String pName) {
        this.pName = pName;
    }
    public boolean isTxbox() {
        return txbox;
    }
    public void setTxbox(boolean txbox) {
        this.txbox = txbox;
    }
    
}
