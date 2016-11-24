package com.example.office;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class XSLXDocumentParse extends DocumentParse {

    @Override
    public Integer charLength(String filePath) {
        return null;
    }

    @Override
    public List<PartEntity> document2Parts(String filePath, int partLength) {
        return null;
    }

    @Override
    public List<ParagraphEntity> document2Paragraphs(File file) {
        return null;
    }

    @Override
    public String createSubDocument(File file, PartEntity partEnty) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String createSubTranlatedDocument(PartEntity part, String subFilePath, boolean checked) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String createTranlatedDocument(List<PartEntity> partEntitys, String filePath, Boolean afterTranlated) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer charLength(InputStream ins) {
        // TODO Auto-generated method stub
        return null;
    }

}
