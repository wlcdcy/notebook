package com.example.document.entity;

import java.util.List;

/**
 * @author Administrator
 * 单元格内类容（段落、表格）对象
 */
public class ThreeElement {
	int elementIndex;
	String content;
	List<FourElement> fourElements;
	public int getElementIndex() {
		return elementIndex;
	}
	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<FourElement> getFourElements() {
		return fourElements;
	}
	public void setFourElements(List<FourElement> fourElements) {
		this.fourElements = fourElements;
	}
	
}
