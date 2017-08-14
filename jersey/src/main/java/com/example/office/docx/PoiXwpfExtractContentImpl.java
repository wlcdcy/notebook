package com.example.office.docx;

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
import org.apache.poi.POIXMLDocument;
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

import com.example.commons.LogUtils;

public class PoiXwpfExtractContentImpl extends PoiExtractContent<XWPFDocument> {
    Logger log = LoggerFactory.getLogger(PoiXwpfExtractContentImpl.class);

    @Override
    public Integer characterLength(InputStream ins) {
        return null;
    }

    @Override
    public List<Part> document2Parts(InputStream ins, int _partLength) {

        XWPFDocument document = null;
        try {
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();

            List<Part> parts = new ArrayList<Part>();
            List<Paragraph> paragraphs = new ArrayList<Paragraph>();
            Part part = new Part();
            part.setParagraphs(paragraphs);
            parts.add(part);

            // 段落元素序号
            int paragraphNo = 0;
            int documentLength = 0;
            int partLength = 0;
            int partNo = 0;

            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    paragraphNo += 1;
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();
                    logger.info("---content-- : " + content);
                    if (content.length() > 0) {
                        // 长度是否大于partLength
                        if (partLength >= _partLength) {
                            part = new Part();
                            partNo += 1;
                            part.setPartNum(partNo);
                            paragraphs = new ArrayList<Paragraph>();
                            part.setParagraphs(paragraphs);
                            parts.add(part);
                            partLength = 0;
                            // 输出拆分文档
                        }
                        Paragraph p = new Paragraph();
                        paragraphs.add(p);
                        List<Run> runs = new ArrayList<Run>();
                        p.setRuns(runs);
                        p.setPno(paragraphNo);

                        int paragraphLength = 0;
                        List<XWPFRun> xruns = xp.getRuns();
                        int rno = 0;
                        if (xruns.isEmpty()) {
                            continue;
                        }
                        StringBuffer sb = new StringBuffer();
                        for (XWPFRun run : xruns) {
                            String text = run.text().trim();
                            if (StringUtils.isNotEmpty(text)) {
                                int rlength = text.length();
                                logger.info("----- : " + text);
                                Run r = new Run();
                                r.setLength(rlength);
                                r.setText(text);
                                r.setRno(rno);
                                runs.add(r);
                                paragraphLength += rlength;
                                sb.append(text);
                            }
                            rno += 1;
                        }
                        p.setLength(paragraphLength);
                        p.setText(sb.toString());
                        String[] sentences = splitContent(content);
                        p.setSentences(sentences);
                        logger.info("---text-- : " + p.getText());
                        logger.info("---length-- : " + paragraphLength);
                        documentLength += paragraphLength;
                        partLength += paragraphLength;
                        part.setLength(partLength);
                    }

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
            logger.info("document length : " + documentLength);
            return parts;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
    
    public List<Part> documentParse(InputStream ins) {

        XWPFDocument document = null;
        try {
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();

            List<Paragraph> paragraphs = new ArrayList<Paragraph>();

            // 段落元素序号
            int paragraphNo = 0;
            int documentLength = 0;

            for (IBodyElement body : bodys) {
                LogUtils.writeDebugLog(log, "bodyType :" +body.getElementType().name());
                String bodyType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), bodyType)) {
                    paragraphNo += 1;
                    XWPFParagraph xp = (XWPFParagraph) body;
                    Paragraph p = parseXWPFParagraph(xp);
                    if(p!=null){
                        p.setPno(paragraphNo);
                        paragraphs.add(p);
                    }
                    

                } else if (StringUtils.equals(BodyElementType.TABLE.name(), bodyType)) {
                    XWPFTable xt = ((XWPFTable) body);
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            List<IBodyElement>  ibodyOfCell = tc.getBodyElements();
                            
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), bodyType)) {

                }
            }
            logger.info("document length : " + documentLength);
            return parts;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
    
    private List<Paragraph> parseIBodyElements(List<IBodyElement> ibodys){
        int bodyIndex=0;
        List<Paragraph> paragraphs = new ArrayList<>();
        for (IBodyElement body : ibodys) {
            LogUtils.writeDebugLog(log, "bodyType :" +body.getElementType().name());
            String bodyType = body.getElementType().name();
            if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), bodyType)) {
                bodyIndex += 1;
                XWPFParagraph xp = (XWPFParagraph) body;
                Paragraph p = parseXWPFParagraph(xp);
                if(p!=null){
                    p.setPno(bodyIndex);
                    paragraphs.add(p);
                }
            } else if (StringUtils.equals(BodyElementType.TABLE.name(), bodyType)) {
                XWPFTable xt = ((XWPFTable) body);
                List<XWPFTableRow> trs = xt.getRows();
                for (XWPFTableRow tr : trs) {
                    List<XWPFTableCell> tcs = tr.getTableCells();
                    for (XWPFTableCell tc : tcs) {
                        List<Paragraph> paragraphsOfCell = parseIBodyElements(tc.getBodyElements());
                        new Tablee
                        
                    }
                }
            } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), bodyType)) {

            }
        }
        return paragraphs;
    }
    
    private  Paragraph parseXWPFParagraph(XWPFParagraph xParagraph){
        String content = xParagraph.getText().trim();
        LogUtils.writeDebugLog(log, "PARAGRAPH content : " +content);
        if (content.length() > 0) {
            
            Paragraph p = new Paragraph();
            List<Run> runs = new ArrayList<>();
            p.setRuns(runs);

            int paragraphLength = 0;
            List<XWPFRun> xruns = xParagraph.getRuns();
            int rno = 0;
            if (xruns.isEmpty()) {
                return null; 
            }
            StringBuilder sb = new StringBuilder();
            for (XWPFRun run : xruns) {
                String text = run.text().trim();
                if (StringUtils.isNotEmpty(text)) {
                    int rlength = text.length();
                    Run r = new Run();
                    r.setLength(rlength);
                    r.setText(text);
                    r.setRno(rno);
                    runs.add(r);
                    paragraphLength += rlength;
                    sb.append(text);
                }
                rno += 1;
            }
            p.setLength(paragraphLength);
            p.setText(sb.toString());
            String[] sentences = splitContent(p.getText());
            p.setSentences(sentences);
            LogUtils.writeDebugLog(log, "PARAGRAPH content : " +p.getText());
            LogUtils.writeDebugLog(log, "PARAGRAPH length : " +paragraphLength);
            
            return p;
        }
        return null;
    }

    public void document2Files(InputStream ins, int _partLength, String tempTemple) {
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();

            List<Part> parts = new ArrayList<Part>();
            List<Paragraph> paragraphs = new ArrayList<Paragraph>();
            Part part = new Part();
            part.setParagraphs(paragraphs);
            parts.add(part);

            // 段落元素序号
            int paragraphNo = 0;
            int documentLength = 0;
            int partLength = 0;
            int partNo = 0;
            int index = 0;
            int offset = 0;
            for (IBodyElement body : bodys) {
                offset += 1;
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    paragraphNo += 1;
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();
                    logger.info("---content-- : " + content);
                    if (content.length() > 0) {
                        // 长度是否大于partLength
                        if (partLength >= _partLength) {
                            part = new Part();
                            partNo += 1;
                            part.setPartNum(partNo);
                            paragraphs = new ArrayList<Paragraph>();
                            part.setParagraphs(paragraphs);
                            parts.add(part);
                            partLength = 0;
                            // 输出拆分文档
                            createDoucument(tempTemple, index, offset - 1);
                            index = offset - 1;
                        }

                        Paragraph p = new Paragraph();
                        paragraphs.add(p);
                        List<Run> runs = new ArrayList<Run>();
                        p.setRuns(runs);
                        p.setPno(paragraphNo);

                        int paragraphLength = 0;
                        List<XWPFRun> xruns = xp.getRuns();
                        int rno = 0;
                        if (xruns.isEmpty()) {
                            continue;
                        }
                        StringBuffer sb = new StringBuffer();
                        for (XWPFRun run : xruns) {
                            String text = run.text().trim();
                            if (StringUtils.isNotEmpty(text)) {
                                int rlength = text.length();
                                logger.info("----- : " + text);
                                Run r = new Run();
                                r.setLength(rlength);
                                r.setText(text);
                                r.setRno(rno);
                                runs.add(r);
                                paragraphLength += rlength;
                                sb.append(text);
                            }
                            rno += 1;
                        }
                        p.setLength(paragraphLength);
                        p.setText(sb.toString());
                        String[] sentences = splitContent(content);
                        p.setSentences(sentences);
                        logger.info("---text-- : " + p.getText());
                        logger.info("---length-- : " + paragraphLength);
                        documentLength += paragraphLength;
                        partLength += paragraphLength;
                        part.setLength(partLength);
                    }

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
            logger.info("document length : " + documentLength);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createDoucument(String tempTemple, int index, int offset) {
        XWPFDocument tempDocument = null;
        try {
            tempDocument = new XWPFDocument(POIXMLDocument.openPackage(tempTemple));
        } catch (IOException e) {

        }
        int bodyLength = tempDocument.getBodyElements().size();
        // 删除尾部元素
        for (int j = offset; j < bodyLength; j++) {
            tempDocument.removeBodyElement(offset);
        }
        // 删除头部元素
        for (int i = 0; i < index; i++) {
            tempDocument.removeBodyElement(0);
        }

        // 文件不存在时会自动创建
        OutputStream outs = null;
        try {
            outs = new FileOutputStream(String.format("D:\\table_%s.docx", index));
            tempDocument.write(outs);

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
        }
    }

    @Override
    public Part document2Part(InputStream ins) {
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();
            Part part = new Part();
            part.setPartNum(1);
            List<Paragraph> paragraphs = new ArrayList<Paragraph>();
            part.setParagraphs(paragraphs);
            // 段落元素序号
            int pno = 0;
            int dlength = 0;
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    pno += 1;
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();

                    logger.info("---content-- : " + content);
                    if (content.length() > 0) {
                        StringBuffer sb = new StringBuffer();
                        Paragraph p = new Paragraph();
                        List<Run> runs = new ArrayList<Run>();
                        p.setRuns(runs);
                        p.setPno(pno);
                        int plength = 0;
                        List<XWPFRun> xruns = xp.getRuns();
                        int rno = 0;
                        for (XWPFRun xrun : xruns) {
                            String text = xrun.text().trim();
                            if (StringUtils.isNotEmpty(text)) {
                                int rlength = text.length();
                                logger.info("----- : " + text);
                                Run run = new Run();
                                run.setLength(rlength);
                                run.setText(text);
                                run.setRno(rno);
                                run.setBold(xrun.isBold());
                                run.setItalic(xrun.isItalic());
                                run.setFontColor(xrun.getColor());
                                run.setFontName(xrun.getFontFamily());
                                run.setFontSize(xrun.getFontSize());
                                run.setUnderlineNname(xrun.getUnderline().name());
                                runs.add(run);
                                plength += rlength;
                                sb.append(text);
                            }
                            rno += 1;
                        }
                        p.setLength(plength);
                        p.setText(sb.toString());
                        String[] sentences = splitContent(content);
                        p.setSentences(sentences);
                        logger.info("---text-- : " + p.getText());
                        logger.info("---length-- : " + plength);
                        dlength += plength;
                        paragraphs.add(p);
                    }

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
            part.setLength(dlength);
            logger.info("document length : " + dlength);
            return part;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

    private void replaceParagraph(XWPFParagraph _paragraph, Paragraph paragraph) {
        String text = "";
        String[] sentences = paragraph.getSentences();
        for (int i = 0; i < sentences.length; i++) {
            text += sentences[i];
        }

        int rno = 0;
        List<Run> runs = paragraph.getRuns();
        XWPFRun xrun = null;
        for (Run run : runs) {
            rno = run.getRno();
            _paragraph.removeRun(rno);
            xrun = _paragraph.insertNewRun(rno);
            // xrun.setText(text);
            xrun.setText(run.getText());
            if (run.isBold()) {
                xrun.setBold(run.isBold());
            }
            if (run.isItalic()) {
                xrun.setItalic(run.isItalic());
            }
            if (StringUtils.isNotEmpty(run.getFontName())) {
                xrun.setFontFamily(run.getFontName());
            }
            if (run.getFontSize() > 0) {
                xrun.setFontSize(run.getFontSize());
            }
            if (StringUtils.isNotEmpty(run.getFontColor())) {
                xrun.setColor(run.getFontColor());
            }
            if (StringUtils.isNotEmpty(run.getUnderlineNname())) {
                xrun.setUnderline(UnderlinePatterns.valueOf(run.getUnderlineNname()));
            }

            text = "";
        }
        xrun.addCarriageReturn();
        xrun.setText("hello");

    }

    public void createDocument(Part part, String tempTemple) {
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(POIXMLDocument.openPackage(tempTemple));
        } catch (IOException e) {

        }
        List<XWPFParagraph> _paragraphs = document.getParagraphs();
        List<Paragraph> paragraphs = part.getParagraphs();
        for (Paragraph paragraph : paragraphs) {
            int pno = paragraph.getPno();
            // String elementType = paragraph.getElementType();
            XWPFParagraph _paragraph = _paragraphs.get(pno - 1);
            replaceParagraph(_paragraph, paragraph);
        }
        // 文件不存在时会自动创建
        OutputStream outs = null;
        try {
            outs = new FileOutputStream(String.format("D:\\table_%s.docx", part.getPartNum() + "_"));
            document.write(outs);

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
        }
    }

    public void createDocument(List<Part> parts, String tempTemple) {
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(POIXMLDocument.openPackage(tempTemple));
        } catch (IOException e) {

        }
        List<XWPFParagraph> _paragraphs = document.getParagraphs();
        for (Part part : parts) {
            List<Paragraph> paragraphs = part.getParagraphs();
            for (Paragraph paragraph : paragraphs) {
                int pno = paragraph.getPno();
                XWPFParagraph _paragraph = _paragraphs.get(pno - 1);
                replaceParagraph(_paragraph, paragraph);
            }
        }

        // 文件不存在时会自动创建
        OutputStream outs = null;
        try {
            outs = new FileOutputStream(String.format("D:\\table_%s.docx", 1));
            document.write(outs);

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
        }
    }

    public static void main(String[] args) {
        // String tempTemple =
        // "D:/Users/Administrator/Documents/Tencent
        // Files/2319221561/FileRecv/补充问题.docx";
        String tempTemple = "D:/Users/Administrator/Documents/家园互联APP.docx";
        InputStream ins = null;
        PoiXwpfExtractContentImpl documentPrase = new PoiXwpfExtractContentImpl();
        try {
            ins = new FileInputStream(new File(tempTemple));
            Part part = documentPrase.document2Part(ins);
            documentPrase.createDocument(part, tempTemple);

            // List<Part> parts = documentPrase.document2Parts(ins, 10);
            // documentPrase.createDocument(parts, tempTemple);

            // documentPrase.document2Files(ins, 500, tempTemple);
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

}
