package com.example.office;

import java.util.List;

public class BElement {
    int index;
    /**
     * p,t,b
     */
    String name;
    int rowNum;
    int columnNum;
    List<BElement> childs;
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
    public List<BElement> getChilds() {
        return childs;
    }
    public void setChilds(List<BElement> childs) {
        this.childs = childs;
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
    
}
