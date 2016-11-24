package com.example.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.words.Document;
import com.aspose.words.License;

public class DOCDocumentParse extends DOCXDocumentParse {
    Logger logger = LoggerFactory.getLogger(DOCDocumentParse.class);

    @Override
    public Integer charLength(String filePath) {
        File file = doc2docx(filePath);
        return super.charLength(file.getPath());
    }

    @Override
    public List<PartEntity> document2Parts(String filePath, int partLength) {
        File file = doc2docx(filePath);
        return super.document2Parts(file.getPath(), partLength);
    }

    @Override
    public List<ParagraphEntity> document2Paragraphs(File file) {
        File docxFile = doc2docx(file);
        return super.document2Paragraphs(docxFile);
    }

    public String createSubDocument_(File file, PartEntity partEntity) {
        File docxfile = doc2docx(file);
        return super.createSubDocument(docxfile, partEntity);
    }

    @Override
    public String createSubTranlatedDocument(PartEntity partEntity, String filePath, boolean checked) {
        File docxfile = doc2docx(filePath);
        return super.createSubTranlatedDocument(partEntity, docxfile.getPath(), checked);
    }

    @Override
    public String createTranlatedDocument(List<PartEntity> partEntitys, String filePath, Boolean afterTranlated) {
        File docxfile = doc2docx(filePath);
        return super.createTranlatedDocument(partEntitys, docxfile.getPath(), afterTranlated);
    }

    private File doc2docx(String docPath) {
        return doc2docx(new File(docPath));
    }

    private File doc2docxOld(File docFile) {
        String docxFilePath = docFile.getPath() + "x";
        File docxFile = new File(docxFilePath);
        if (!docxFile.exists()) {
            XWPFDocument document = null;
            OutputStream out = null;
            InputStream ins = null;
            try {
                Document doc = new Document(docFile.getPath());
                doc.save(docxFile.getPath());
                try {
                    ins = new FileInputStream(docxFile);
                    document = new XWPFDocument(ins);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    ins.close();
                }
                // document.removeBodyElement(0);
                List<IBodyElement> elements = document.getBodyElements();
                IBodyElement element = elements.get(elements.size() - 1);
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), element.getElementType().name())) {
                    XWPFParagraph xp = ((XWPFParagraph) element);
                    String text = xp.getText();
                    if (StringUtils.isNotBlank(text)) {
                        if (text.contains("Evaluation") && text.contains("Aspose")) {
                            document.removeBodyElement(elements.size() - 1);
                        }
                    }
                }
                IBodyElement element0 = elements.get(0);
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), element0.getElementType().name())) {
                    XWPFParagraph xp = ((XWPFParagraph) element0);
                    String text = xp.getText();
                    if (StringUtils.isNotBlank(text)) {
                        if (text.contains("Evaluation") && text.contains("Aspose")) {
                            document.removeBodyElement(0);
                        }
                    }
                }

                out = new FileOutputStream(docxFile);
                document.write(out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (document != null)
                        document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (ins != null)
                        ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return docxFile;
    }

    private File doc2docx(File docFile) {
        String docxFilePath = docFile.getPath() + "x";
        File docxFile = new File(docxFilePath);
        // 验证License
        boolean license = getLicense();
        if (!docxFile.exists()) {
            Document doc;
            try {
                doc = new Document(docFile.getPath());
                doc.save(docxFile.getPath());
                if (license) {
                    deleteAsposeInfo(docxFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return docxFile;
    }

    private void deleteAsposeInfo(File docxFile) {
        XWPFDocument document = null;
        OutputStream out = null;
        InputStream ins = null;
        try {
            ins = new FileInputStream(docxFile);
            document = new XWPFDocument(ins);
            List<IBodyElement> elements = document.getBodyElements();
            IBodyElement element = elements.get(elements.size() - 1);
            if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), element.getElementType().name())) {
                XWPFParagraph xp = ((XWPFParagraph) element);
                String text = xp.getText();
                if (StringUtils.isNotBlank(text)) {
                    if (text.contains("Evaluation") && text.contains("Aspose")) {
                        document.removeBodyElement(elements.size() - 1);
                    }
                }
            }
            IBodyElement element0 = elements.get(0);
            if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), element0.getElementType().name())) {
                XWPFParagraph xp = ((XWPFParagraph) element0);
                String text = xp.getText();
                if (StringUtils.isNotBlank(text)) {
                    if (text.contains("Evaluation") && text.contains("Aspose")) {
                        document.removeBodyElement(0);
                    }
                }
            }

            out = new FileOutputStream(docxFile);
            document.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (document != null)
                    document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ins != null)
                    ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public Integer charLength(InputStream ins) {
        return null;
    }

    public static boolean getLicense() {
        boolean result = false;
        InputStream is = null;
        try {
            is = DOCDocumentParse.class.getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        }
        return result;
    }
}
