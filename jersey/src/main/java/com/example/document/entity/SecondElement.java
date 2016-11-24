package com.example.document.entity;

import java.util.List;

/**
 * @author Administrator 单元格对象(段落可当做一行一列的表格)
 */
public class SecondElement {
    /**
     * 段落、表格
     */
    String elementType;
    /**
     * 在一级元素内容中的先后顺序
     */
    int elementIndex;
    /**
     * 行号（缺省值为1；当elementType=表格，rowNum为表格的行号）
     */
    int rowNum = 1;
    /**
     * 列号（缺省值为1；当elementType=表格，columnNum为表格的列号）
     */
    int columnNum = 1;
    /**
     * 该元素的text内容
     */
    String content;
    int charSize;
    int wordSize;
    List<ThreeElement> threeElements;

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public int getElementIndex() {
        return elementIndex;
    }

    public void setElementIndex(int elementIndex) {
        this.elementIndex = elementIndex;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCharSize() {
        return charSize;
    }

    public void setCharSize(int charSize) {
        this.charSize = charSize;
    }

    public int getWordSize() {
        return wordSize;
    }

    public void setWordSize(int wordSize) {
        this.wordSize = wordSize;
    }

    public List<ThreeElement> getThreeElements() {
        return threeElements;
    }

    public void setThreeElements(List<ThreeElement> threeElements) {
        this.threeElements = threeElements;
    }

}
