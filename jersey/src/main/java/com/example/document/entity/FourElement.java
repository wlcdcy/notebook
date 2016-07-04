package com.example.document.entity;

import java.util.List;

/**
 * @author Administrator
 * 句子对象
 */
public class FourElement {
	String index;
	String content;
	/**
	 * 句子内容类型（文本，图片，超链接）
	 */
	String elementType;
	List<FiveElement> fiveElements;
}
