package com.example.document.entity;

import java.util.List;

public class TopElement {
	/**
	 * 字符数
	 */
	long charSize;
	/**
	 * 字数
	 */
	long wordSize;
	/**
	 * 页眉
	 */
	FirstElement headerElement;
	/**
	 * 页脚
	 */
	List<FirstElement> footerElements;
	/**
	 * 脚注
	 */
	List<FirstElement> footnoteElements;
	/**
	 * 正文
	 */
	List<FirstElement> bodyElements;
	
	
	public List<FirstElement> getFooterElements() {
		return footerElements;
	}
	public void setFooterElements(List<FirstElement> footerElements) {
		this.footerElements = footerElements;
	}
	public List<FirstElement> getFootnoteElements() {
		return footnoteElements;
	}
	public void setFootnoteElements(List<FirstElement> footnoteElements) {
		this.footnoteElements = footnoteElements;
	}
	public List<FirstElement> getBodyElements() {
		return bodyElements;
	}
	public void setBodyElements(List<FirstElement> bodyElements) {
		this.bodyElements = bodyElements;
	}
	public long getCharSize() {
		return charSize;
	}
	public void setCharSize(long charSize) {
		this.charSize = charSize;
	}
	public long getWordSize() {
		return wordSize;
	}
	public void setWordSize(long wordSize) {
		this.wordSize = wordSize;
	}
	public FirstElement getHeaderElement() {
		return headerElement;
	}
	public void setHeaderElement(FirstElement headerElement) {
		this.headerElement = headerElement;
	}
	
}
