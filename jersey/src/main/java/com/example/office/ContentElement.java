package com.example.office;

public class ContentElement {
    private ContentType contentType;
    private String contentText;
    private int contentSerial;
    
    private Long contentId;
    private Long sentenceId;

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(Long sentenceId) {
        this.sentenceId = sentenceId;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getContentSerial() {
        return contentSerial;
    }

    public void setContentSerial(int contentSerial) {
        this.contentSerial = contentSerial;
    }

}
