package com.example.office;

import java.util.List;

public class PElement {
    private int partId;// 编号
    private int charNumber;// 字符数
    private int wordNumber;// 字数
    private List<BElement> bodyElements;// 句短
    private int beginBodyId;// 标号，起始位置，
    private int endBodyId;// 结束位置
    private String partPath;// 存储路径
    public int getPartId() {
        return partId;
    }
    public void setPartId(int partId) {
        this.partId = partId;
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
    public List<BElement> getBodyElements() {
        return bodyElements;
    }
    public void setBodyElements(List<BElement> bodyElements) {
        this.bodyElements = bodyElements;
    }
    public int getBeginBodyId() {
        return beginBodyId;
    }
    public void setBeginBodyId(int beginBodyId) {
        this.beginBodyId = beginBodyId;
    }
    public int getEndBodyId() {
        return endBodyId;
    }
    public void setEndBodyId(int endBodyId) {
        this.endBodyId = endBodyId;
    }
    public String getPartPath() {
        return partPath;
    }
    public void setPartPath(String partPath) {
        this.partPath = partPath;
    }

}
