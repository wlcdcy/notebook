package com.example.office;

import org.apache.commons.lang.StringUtils;

public enum DocumentType {
    DOCX(1, "docx"), DOC(2, "doc"), XLSX(3, "xlsx"), XLS(4, "xls"), PDF(5, "pdf"), TEXT(6, "text");

    private int value;
    private String name;

    private DocumentType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static DocumentType getNameOf(String name) {
        if (StringUtils.equals(name, DocumentType.DOCX.getName())) {
            return DocumentType.DOCX;
        } else if (StringUtils.equals(name, DocumentType.DOC.getName())) {
            return DocumentType.DOC;
        } else if (StringUtils.equals(name, DocumentType.XLSX.getName())) {
            return DocumentType.XLSX;
        } else if (StringUtils.equals(name, DocumentType.XLS.getName())) {
            return DocumentType.XLS;
        } else if (StringUtils.equals(name, DocumentType.PDF.getName())) {
            return DocumentType.PDF;
        } else {
            return DocumentType.TEXT;
        }
    }

    @Override
    public String toString() {
        return super.toString() + this.name + this.value;
    }

}
