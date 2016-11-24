package com.example.document.entity;

import java.util.List;

/**
 * @author Administrator 句子对象
 */
public class FourElement {
    int index;
    String content = "";
    /**
     * 句子内容类型（文本，图片，超链接）
     */
    String elementType;
    List<FiveElement> fiveElements;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public List<FiveElement> getFiveElements() {
        return fiveElements;
    }

    public void setFiveElements(List<FiveElement> fiveElements) {
        this.fiveElements = fiveElements;
    }

}
