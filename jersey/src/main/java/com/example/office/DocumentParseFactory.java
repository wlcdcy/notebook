package com.example.office;

public class DocumentParseFactory {
    private DocumentParse docxParse = null;
    private DocumentParse docParse = null;
    private DocumentParse textParse = null;
    private DocumentParse pdfParse = null;
    private DocumentParse xlsxParse = null;
    private DocumentParse xlsParse = null;

    private static DocumentParseFactory docParseFactory = null;

    private DocumentParseFactory() {
    }

    public static DocumentParseFactory getInstance() {
        if (docParseFactory == null) {
            docParseFactory = new DocumentParseFactory();
        }
        return docParseFactory;
    }

    public DocumentParse getDocumentParse(DocumentType type) {
        switch (type.getValue()) {
        case 1:
            return getDOCXDecumentParse();
        case 2:
            return getDOCDecumentParase();
        case 3:
            return getXLSXDecumentParase();
        case 4:
            return getXLSDecumentParase();
        case 5:
            return getPDFDecumentParase();
        case 6:
            return getTEXTDecumentParase();
        }
        return null;
    }

    private DocumentParse getTEXTDecumentParase() {
        if (textParse == null) {
            textParse = new TEXTDocumentParse();
        }
        return textParse;
    }

    private DocumentParse getPDFDecumentParase() {
        if (pdfParse == null) {
            pdfParse = new PDFDocumentParse();
        }
        return pdfParse;
    }

    private DocumentParse getXLSDecumentParase() {
        if (xlsParse == null) {
            xlsParse = new XSLDocumentParse();
        }
        return xlsParse;
    }

    private DocumentParse getXLSXDecumentParase() {
        if (xlsxParse == null) {
            xlsxParse = new XSLXDocumentParse();
        }
        return xlsxParse;
    }

    private DocumentParse getDOCDecumentParase() {
        if (docParse == null) {
            docParse = new DOCDocumentParse();
        }
        return docParse;
    }

    private DocumentParse getDOCXDecumentParse() {
        if (docxParse == null) {
            docxParse = new DOCXDocumentParse();
        }
        return docxParse;
    }

}
