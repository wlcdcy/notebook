package com.example.office.docx;

import java.util.List;

public class Paragraph {
    private String index;
    private int pno;
    private int length;
    private String text;
    private List<Run> runs;
    private String[] sentences;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getPno() {
        return pno;
    }

    public void setPno(int pno) {
        this.pno = pno;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<Run> getRuns() {
        return runs;
    }

    public void setRuns(List<Run> runs) {
        this.runs = runs;
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

}
