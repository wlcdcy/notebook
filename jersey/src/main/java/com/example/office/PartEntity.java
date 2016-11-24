package com.example.office;

import java.util.List;

public class PartEntity {
    private int partNo;// 编号
    private int characters;// 字符数
    private List<ParagraphEntity> paragraphs;// 句短
    private int firstNo;// 标号，起始位置，
    private int lasteNo;// 结束位置
    private String partPath;// 路径

    public int getPartNo() {
        return partNo;
    }

    public void setPartNo(int partNo) {
        this.partNo = partNo;
    }

    public int getCharacters() {
        return characters;
    }

    public void setCharacters(int characters) {
        this.characters = characters;
    }

    public List<ParagraphEntity> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<ParagraphEntity> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public String getPartPath() {
        return partPath;
    }

    public void setPartPath(String partPath) {
        this.partPath = partPath;
    }

    public int getFirstNo() {
        return firstNo;
    }

    public void setFirstNo(int firstNo) {
        this.firstNo = firstNo;
    }

    public int getLasteNo() {
        return lasteNo;
    }

    public void setLasteNo(int lasteNo) {
        this.lasteNo = lasteNo;
    }

}
