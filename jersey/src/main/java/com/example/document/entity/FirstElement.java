package com.example.document.entity;

import java.util.List;

public class FirstElement {
    /**
     * 页眉。页脚，正文，脚注
     */
    String elementType;
    private List<SecondElement> secondElements;

    public List<SecondElement> getSecondElements() {
        return secondElements;
    }

    public void setSecondElements(List<SecondElement> secondElements) {
        this.secondElements = secondElements;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

}
