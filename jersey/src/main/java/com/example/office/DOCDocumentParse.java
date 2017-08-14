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
import com.example.commons.LogUtils;

public class DOCDocumentParse extends DOCXDocumentParse {
    private static Logger log = LoggerFactory.getLogger(DOCDocumentParse.class);

    @Override
    public Integer charNumber(String filePath) {
        File file = doc2docx(filePath);
        return super.charNumber(file.getPath());
    }

    @Override
    public String createSubDocument(PElement pElement,File file) {
        File docxfile = doc2docx(file);
        return super.createSubDocument(pElement,docxfile);
    }

    @Override
    public String createSubTranlatedDocument(PElement pElement, String filePath, boolean checked) {
        File docxfile = doc2docx(filePath);
        return super.createSubTranlatedDocument(pElement, docxfile.getPath(), checked);
    }

    @Override
    
    public String createTranlatedDocument(DElement dElement, String filePath, Boolean afterTranlated,boolean checked) {
        File docxfile = doc2docx(filePath);
        return super.createTranlatedDocument(dElement, docxfile.getPath(), afterTranlated,checked);
    }

    private File doc2docx(String docPath) {
        return doc2docx(new File(docPath));
    }

    @SuppressWarnings("unused")
    private File doc2docxOld(File docFile) {
        String docxFilePath = docFile.getPath() + "x";
        File docxFile = new File(docxFilePath);
        if (!docxFile.exists()) {
            XWPFDocument document = null;
            try (InputStream ins = new FileInputStream(docxFile);OutputStream out = new FileOutputStream(docxFile);) {
                Document doc = new Document(docFile.getPath());
                doc.save(docxFile.getPath());
                document = new XWPFDocument(ins);
                
                // document.removeBodyElement(0)
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

                document.write(out);
            } catch (Exception e) {
                LogUtils.writeWarnExceptionLog(log, e);
            } finally {
                try {
                    if (document != null)
                        document.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(log, e);
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
        try (InputStream ins = new FileInputStream(docxFile); OutputStream out = new FileOutputStream(docxFile);) {
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

            document.write(out);
        } catch (Exception e) {
            LogUtils.writeWarnExceptionLog(log, e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(log,e);
                }
            }
            
        }
    }

    public static boolean getLicense() {
        boolean result = false;

        try (InputStream is = DOCDocumentParse.class.getResourceAsStream("license.xml");) {
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            LogUtils.writeWarnExceptionLog(log, e);
        }
        return result;
    }
}
