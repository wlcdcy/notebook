package com.example.office;



public class ParagraphEntity {
	private int partNo;//
	private int no;//编号
	private int length;//长度
	private String text;//内容
	private String[] sentences;//拆分句子
	private String tranText;//
	private String[] tranSentences;//
	private String checkText;//校对
	private String[] checkSentences; //校对句子
	private int runNo;
	
	public int getPartNo() {
		return partNo;
	}
	public void setPartNo(int partNo) {
		this.partNo = partNo;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String[] getSentences() {
		return sentences;
	}
	public void setSentences(String[] sentences) {
		this.sentences = sentences;
	}
	public int getRunNo() {
		return runNo;
	}
	public void setRunNo(int runNo) {
		this.runNo = runNo;
	}
	public String getTranText() {
		return tranText;
	}
	public void setTranText(String tranText) {
		this.tranText = tranText;
	}
	public String[] getTranSentences() {
		return tranSentences;
	}
	public void setTranSentences(String[] tranSentences) {
		this.tranSentences = tranSentences;
	}
	public String getCheckText() {
		return checkText;
	}
	public void setCheckText(String checkText) {
		this.checkText = checkText;
	}
	public String[] getCheckSentences() {
		return checkSentences;
	}
	public void setCheckSentences(String[] checkSentences) {
		this.checkSentences = checkSentences;
	}
	
}
