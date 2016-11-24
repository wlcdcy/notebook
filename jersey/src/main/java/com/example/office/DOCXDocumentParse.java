package com.example.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DOCXDocumentParse extends DocumentParse {
    Logger logger = LoggerFactory.getLogger(DOCXDocumentParse.class);

    public DOCXDocumentParse() {
        ZipSecureFile.setMinInflateRatio(0.001);
    }

    @Override
    public Integer charLength(String filePath) {
        XWPFDocument document = null;
        int charLength = 0;
        InputStream ins = null;
        try {
            // OPCPackage opk =POIXMLDocument.openPackage(filePath);
            ins = new FileInputStream(filePath);
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();
                    int contentCharLength = getLengthRemoveSpace(content);
                    charLength += contentCharLength;
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = ((XWPFTable) body);
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            charLength += getLengthRemoveSpace(tc.getText().trim());
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {
                }
            }
            return charLength;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public List<PartEntity> document2Parts(String filePath, int partLength) {
        XWPFDocument document = null;
        File file = new File(filePath);
        InputStream ins = null;
        try {
            ins = new FileInputStream(file);
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();
            // 拆分文档字符数
            int partCharLength = 0;
            // 拆分文档序号
            int partNo = 0;

            List<PartEntity> parts = new ArrayList<PartEntity>();
            PartEntity pe = new PartEntity();
            pe.setPartNo(partNo);
            parts.add(pe);
            List<ParagraphEntity> paragraphs = new ArrayList<ParagraphEntity>();
            pe.setParagraphs(paragraphs);

            // 段落元素序号
            int paragraphNo = 0;
            int firstNo = 0, lastNo = 0;
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();

                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();
                    if (content.length() > 0) {
                        if (partCharLength >= partLength) {
                            logger.info("partCharLength : " + partCharLength);
                            logger.info("paragraphNo : " + paragraphNo);
                            pe = new PartEntity();
                            partCharLength = 0;
                            partNo += 1;
                            pe.setPartNo(partNo);
                            paragraphs = new ArrayList<ParagraphEntity>();
                            pe.setParagraphs(paragraphs);
                            parts.add(pe);
                            firstNo = lastNo;
                        }

                        int contentCharLength = getLengthRemoveSpace(content);
                        ParagraphEntity paragraph = new ParagraphEntity();
                        paragraph.setLength(contentCharLength);
                        paragraph.setNo(paragraphNo);
                        String[] texts = removeNewlineAndSplitContent(content);
                        paragraph.setSentences(texts);
                        paragraph.setPartNo(partNo);
                        partCharLength += contentCharLength;
                        paragraphs.add(paragraph);
                        List<XWPFRun> xruns = xp.getRuns();
                        int runNo = 0;
                        for (XWPFRun xrun : xruns) {
                            if (StringUtils.isNotEmpty(xrun.text())) {
                                paragraph.setRunNo(runNo);
                                break;
                            }
                            ;
                            runNo++;
                        }

                    }
                    pe.setCharacters(partCharLength);
                    paragraphNo += 1;
                    logger.info(content);
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = ((XWPFTable) body);
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            partCharLength += getLengthRemoveSpace(tc.getText().trim());
                            logger.info(tc.getText().trim());
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {

                }
                pe.setFirstNo(firstNo);
                pe.setLasteNo(lastNo);
                lastNo += 1;
            }

            // 创建原文的切片文件
            for (PartEntity partEnty : parts) {
                String subfilePath = createSubDocument(file, partEnty);
                partEnty.setPartPath(subfilePath);
            }
            return parts;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 创建原文子文档
     * 
     * @param file
     * @param firstNo
     * @param lastNo
     */
    @Override
    public String createSubDocument(File file, PartEntity partEntity) {
        InputStream ins = null;
        XWPFDocument document = null;
        try {
            ins = new FileInputStream(file);
            document = new XWPFDocument(ins);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ins != null)
                    ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int lastNo = partEntity.getLasteNo(), firstNo = partEntity.getFirstNo(), partNo = partEntity.getPartNo();
        int bodyLength = document.getBodyElements().size();
        // 删除尾部元素
        for (int j = lastNo + 1; j < bodyLength; j++) {
            document.removeBodyElement(lastNo + 1);
        }
        // 删除头部元素
        for (int i = 0; i < firstNo; i++) {
            document.removeBodyElement(0);
        }
        // 文件不存在时会自动创建
        OutputStream outs = null;
        File subFile = createSubFile(file, partNo);
        try {
            outs = new FileOutputStream(subFile);
            document.write(outs);
            return subFile.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public List<ParagraphEntity> document2Paragraphs(File file) {
        XWPFDocument document = null;
        InputStream ins = null;
        try {
            ins = new FileInputStream(file);
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();
            List<ParagraphEntity> paragraphs = new ArrayList<ParagraphEntity>();
            // 段落元素序号
            int paragraphNo = 0;
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();
                    if (content.length() > 0) {
                        int contentCharLength = getLengthRemoveSpace(content);
                        ParagraphEntity paragraph = new ParagraphEntity();
                        paragraph.setLength(contentCharLength);
                        paragraph.setNo(paragraphNo);
                        String[] texts = removeNewlineAndSplitContent(content);
                        paragraph.setSentences(texts);
                        paragraphs.add(paragraph);
                        List<XWPFRun> xruns = xp.getRuns();
                        int runNo = 0;
                        for (XWPFRun xrun : xruns) {
                            if (StringUtils.isNotEmpty(xrun.text())) {
                                paragraph.setRunNo(runNo);
                                break;
                            }
                            ;
                            runNo++;
                        }
                    }
                    paragraphNo += 1;
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = ((XWPFTable) body);
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            logger.info(tc.getText().trim());
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {

                }
            }
            return paragraphs;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public String createSubTranlatedDocument(PartEntity partEntity, String filePath, boolean checked) {
        XWPFDocument document = null;
        OutputStream outs = null;
        InputStream ins = null;
        File file = new File(filePath);
        try {
            // document = new
            // XWPFDocument(POIXMLDocument.openPackage(filePath));
            ins = new FileInputStream(file);
            document = new XWPFDocument(ins);
            List<ParagraphEntity> paragraphs = partEntity.getParagraphs();
            for (ParagraphEntity paragraph : paragraphs) {
                int no = paragraph.getNo();
                XWPFParagraph _paragraph = document.getParagraphArray(no);
                updateParagraph(_paragraph, paragraph, null, checked);
            }

            int bodyLength = document.getBodyElements().size();
            // 删除尾部元素
            for (int j = partEntity.getLasteNo() + 1; j < bodyLength; j++) {
                document.removeBodyElement(partEntity.getLasteNo() + 1);
            }
            // 删除头部元素
            for (int i = 0; i < partEntity.getFirstNo(); i++) {
                document.removeBodyElement(0);
            }
            // 文件不存在时会自动创建
            File subTranlatedFile = createSubTranlatedFile(file, partEntity.getPartNo());

            outs = new FileOutputStream(subTranlatedFile);
            document.write(outs);
            return subTranlatedFile.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 译文更新段落；afterTranlated=null时生成纯译文，afterTranlated=true时生成原译文对照文，
     * afterTranlated=false时生成译原文对照文
     * 
     * @param para
     * @param paragraph
     * @param afterTranlated
     *            afterTranlated={null，true，false}
     */
    private void updateParagraph(XWPFParagraph para, ParagraphEntity paragraph, Boolean afterTranlated,
            boolean checked) {
        String[] tranSentences = null;
        if (checked) {
            tranSentences = paragraph.getCheckSentences();
        } else {
            tranSentences = paragraph.getTranSentences();
        }
        StringBuffer sb = new StringBuffer();
        for (String sentence : tranSentences) {
            sb.append(sentence);
        }
        String text = sb.toString();
        List<XWPFRun> xruns = para.getRuns();
        int size = xruns.size();
        if (afterTranlated != null && afterTranlated) {
            // 原译文，原文在前译文在后
            // xruns.get(size-1).addCarriageReturn();
            xruns.get(size - 1).addBreak();
            xruns.get(size - 1).setText(text);
        } else {
            String _text = "";
            try {
                para.getText();
            } catch (Exception e) {

            }
            // 译文
            XWPFRun _xrun = null;
            for (int i = 0; i < size; i++) {
                try {
                    XWPFRun xrun = xruns.get(i);
                    boolean bold = false;
                    boolean italic = false;
                    String fontName = null;
                    int fontSize = 0;
                    String color = null;
                    UnderlinePatterns underline = null;
                    try {
                        bold = xrun.isBold();
                        italic = xrun.isItalic();
                        fontName = xrun.getFontName();
                        fontSize = xrun.getFontSize();
                        color = xrun.getColor();
                        underline = xrun.getUnderline();
                    } catch (Exception e) {

                    }

                    if (para.removeRun(i)) {
                        _xrun = para.insertNewRun(i);
                        _xrun.setText(text);
                        if (bold) {
                            _xrun.setBold(bold);
                        }
                        if (italic) {
                            _xrun.setItalic(italic);
                        }
                        if (StringUtils.isNotEmpty(fontName)) {
                            _xrun.setFontFamily(fontName);
                        }
                        if (fontSize > 0) {
                            _xrun.setFontSize(fontSize);
                        }
                        if (StringUtils.isNotEmpty(color)) {
                            _xrun.setColor(color);
                        }
                        if (underline != null) {
                            _xrun.setUnderline(underline);
                        }
                    }
                    // else{
                    // xrun.setText(text, 0);
                    // }
                    text = "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if ((afterTranlated != null && !afterTranlated)) {
                // 译原文，译文在前原文在后
                // _xrun.addCarriageReturn();
                _xrun.addBreak();
                _xrun.setText(_text);
            }
        }
    }

    @Override
    public String createTranlatedDocument(List<PartEntity> partEntitys, String filePath, Boolean afterTranlated) {
        XWPFDocument document = null;
        OutputStream outs = null;
        InputStream ins = null;
        File tranlatedFile = createTranlatedFile(new File(filePath), afterTranlated);
        if (tranlatedFile.exists()) {
            return tranlatedFile.getPath();
        }
        File file = new File(filePath);
        try {
            ins = new FileInputStream(file);
            document = new XWPFDocument(ins);
            for (PartEntity partEntity : partEntitys) {
                List<ParagraphEntity> paragraphs = partEntity.getParagraphs();
                for (ParagraphEntity paragraph : paragraphs) {
                    int no = paragraph.getNo();
                    XWPFParagraph _paragraph = document.getParagraphArray(no);
                    updateParagraph(_paragraph, paragraph, afterTranlated, true);
                }
            }
            outs = new FileOutputStream(tranlatedFile);
            document.write(outs);
            return tranlatedFile.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        DocumentParse documentPrase = new DOCXDocumentParse();
        InputStream ins = null;
        String tempTemple = "D:/Users/Administrator/Documents/Tencent Files/2319221561/FileRecv/v1.docx";// "D:/Users/Administrator/Documents/ipass问题.docx";//
                                                                                                         // "D:/Users/Administrator/Documents/家园互联APP.docx";//
        File file = new File(tempTemple);
        int length = documentPrase.charLength(tempTemple) / 2;

        try {
            ins = new FileInputStream(file);
            List<PartEntity> parts = documentPrase.document2Parts(tempTemple, length);
            for (PartEntity part : parts) {
                documentPrase.createSubTranlatedDocument(part, tempTemple, false);
            }
            // documentPrase.createTranlatedDocument(parts, tempTemple, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Integer charLength(InputStream ins) {
        XWPFDocument document = null;
        int charLength = 0;
        try {
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();
                    int contentCharLength = getLengthRemoveSpace(content);
                    charLength += contentCharLength;
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = ((XWPFTable) body);
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            charLength += getLengthRemoveSpace(tc.getText().trim());
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {
                }
            }
            return charLength;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
