package com.example.document.entity;

/**
 * @author Administrator
 * 格式对象
 */
public class FiveElement {
	int index;
	String content;
	String fontName;
	int fontSize;
	/**
	 * 断句标志
	 */
	boolean isbreak;
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
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public boolean isIsbreak() {
		return isbreak;
	}
	public void setIsbreak(boolean isbreak) {
		this.isbreak = isbreak;
	}
	
}
