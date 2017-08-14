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

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFFootnote;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.EndnotesDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.commons.LogUtils;

public class DOCXDocumentParse extends DocumentParse {
    private static Logger logger = LoggerFactory.getLogger(DOCXDocumentParse.class);
    public static final int TRANS = 0;// 翻译

    public DOCXDocumentParse() {
        ZipSecureFile.setMinInflateRatio(0.001);
    }

    public static void main(String[] args) throws Exception {
        DOCXDocumentParse documentParse = new DOCXDocumentParse();
        boolean checked=true;
        boolean wordSplite = false;
        String tempTemple = "D:/Users/Yahoo/Documents/360js/公式不识别.docx";
        File file = new File(tempTemple);
        int length = documentParse.charNumber(tempTemple) / 2;
        LogUtils.writeDebugLog(logger , "document word number : " + length);

        try (InputStream ins = new FileInputStream(file);) {
            DElement dElement = documentParse.documentParse(tempTemple, wordSplite, 10, 2);
            List<PElement> parts = dElement.getParts();
            for (PElement part : parts) {
                documentParse.createSubTranlatedDocument(part, tempTemple, checked);
            }
            documentParse.createTranlatedDocument(dElement, tempTemple, null,checked);
        } catch (FileNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public Integer charNumber(InputStream ins){
        XWPFDocument document = null;
        int charNumber = 0;
        try {
            document = new XWPFDocument(ins);
            List<IBodyElement> bodys = document.getBodyElements();
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    XWPFParagraph xp = (XWPFParagraph) body;
                    String content = xp.getText().trim();
                    charNumber += countWordNumber(deleteSpace(content));
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = ((XWPFTable) body);
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            charNumber += countWordNumber(deleteSpace(tc.getText().trim()));
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {

                }
            }
            return charNumber;
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage(), e);
                    }
                }
            }
        }
        return null;
    }
    

    @Override
    public Integer wordNumber(InputStream ins) {
        XWPFDocument document = null;
        int charLength = 0;
        try (InputStream in = ins) {
            document = new XWPFDocument(in);
            List<IBodyElement> bodys = document.getBodyElements();
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    XWPFParagraph xp = ((XWPFParagraph) body);
                    String content = xp.getText().trim();
                    charLength += wordNumberDeleteSpace(content,false);
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = ((XWPFTable) body);
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            charLength += wordNumberDeleteSpace(tc.getText().trim(),false);
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {
                }
            }
            return charLength;
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }
        return null;
    }

    @Override
    public DElement documentParse(String filePath, boolean wordSplit, int pLength, int pNumber) {

        XWPFDocument document = null;
        File file = new File(filePath);
        DElement dElement = new DElement();

        try (InputStream ins = new FileInputStream(file);) {

            document = new XWPFDocument(ins);
            // 脚注
            List<XWPFFootnote> footnotes = document.getFootnotes();
            List<BElement> footnoteElements = parseFootnote(footnotes, document, file.getParent(), wordSplit);
            if (footnoteElements != null) {
                dElement.setFootnotes(footnoteElements);
            }

            // 尾注
            List<XWPFFootnote> endnotes = getEndnote(document);
            List<BElement> endnoteElements = parseEndnote(endnotes, document, file.getParent(), wordSplit);
            if (endnoteElements != null) {
                dElement.setEndnotes(endnoteElements);
            }

            // 页眉
            List<XWPFHeader> headers = document.getHeaderList();
            List<BElement> headerElements = parseHeader(headers, document, file.getParent(), wordSplit);
            if (headerElements != null) {
                dElement.setHeaders(headerElements);
            }

            // 页脚
            List<XWPFFooter> footers = document.getFooterList();
            List<BElement> footerElements = parseFooter(footers, document, file.getParent(), wordSplit);
            if (footerElements != null) {
                dElement.setFooters(footerElements);
            }

            // 文本框
            List<IBodyElement> bodys = document.getBodyElements();
            List<BElement> tbxElements = parseTXBoxs(bodys, file.getParent(), wordSplit);
            if (tbxElements != null) {
                dElement.setTextboxs(tbxElements);
            }

            // 正文
            List<PElement> parts = parseBodys(bodys, document, file.getParent(),wordSplit,pLength, pNumber);
            dElement.setParts(parts);

            // 创建原文的切片文件
            for (PElement pElement : parts) {
                String subfilePath = createSubDocument(pElement,file);
                pElement.setPartPath(subfilePath);
            }
            return dElement;
        } catch (Exception e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage(), e);
                    }
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
    public String createSubDocument(PElement pElement,File file) {
        XWPFDocument document = null;

        int endBodyId = pElement.getEndBodyId();
        int beginBodyId = pElement.getBeginBodyId();
        int partId = pElement.getPartId();
        // 文件不存在时会自动创建
        File subFile = createSubFile(file, partId);
        try (InputStream ins = new FileInputStream(file); OutputStream outs = new FileOutputStream(subFile);) {
            document = new XWPFDocument(ins);

            int documentBodySize = document.getBodyElements().size();
            // 删除尾部元素
            for (int j = endBodyId; j < documentBodySize; j++) {
                document.removeBodyElement(endBodyId);
            }
            // 删除头部元素
            for (int i = 0; i < beginBodyId; i++) {
                document.removeBodyElement(0);
            }

            document.write(outs);
            return subFile.getPath();
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }

        return null;
    }

    @Override
    public String createSubTranlatedDocument(PElement pElement, String filePath, boolean checked) {
        XWPFDocument document = null;

        File file = new File(filePath);
        // 文件不存在时会自动创建
        File subTranlatedFile = createSubTranlatedFile(file, pElement.getPartId());
        try (InputStream ins = new FileInputStream(file); OutputStream outs = new FileOutputStream(subTranlatedFile);) {
            // document = new XWPFDocument(POIXMLDocument.openPackage(filePath))
            document = new XWPFDocument(ins);

            List<BElement> bodys = pElement.getBodyElements();
            for (BElement bElement : bodys) {
                IBodyElement ibody = document.getBodyElements().get(bElement.getIndex());
                updateIBody(ibody, bElement, null, checked);
            }

            int bodyLength = document.getBodyElements().size();
            // 删除尾部元素
            for (int j = pElement.getEndBodyId(); j < bodyLength; j++) {
                document.removeBodyElement(pElement.getEndBodyId());
            }
            // 删除头部元素
            for (int i = 0; i < pElement.getBeginBodyId(); i++) {
                document.removeBodyElement(0);
            }
            document.write(outs);
            return subTranlatedFile.getPath();
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger,e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }
        return null;
    }

    private void updateIBody(IBodyElement ibody, BElement bElement, Boolean afterTranlated, boolean checked) {
        if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), bElement.getName())) {
            updateIBody((XWPFParagraph) ibody, bElement, afterTranlated, checked);
        } else if (StringUtils.equals(BodyElementType.TABLE.name(), bElement.getName())) {
            XWPFTableCell cell = ((XWPFTable) ibody).getRow(bElement.getRowNum() - 1)
                    .getCell(bElement.getColumnNum() - 1);
            for (BElement be_ : bElement.getChilds()) {
                IBodyElement ibd = cell.getBodyElements().get(be_.getIndex());
                updateIBody(ibd, be_, afterTranlated, checked);
            }
        }
    }

    @Override
    public String createTranlatedDocument(DElement dElement, String filePath, Boolean afterTranlated,boolean checked) {
        XWPFDocument document = null;
        File tranlatedFile = createTranlatedFile(new File(filePath), afterTranlated);
        if (tranlatedFile.exists() && !tranlatedFile.delete()) {
            LogUtils.writeWarnLog(logger, "tranlatedFile delete fail");
        }
        
        File file = new File(filePath);
        try (InputStream ins = new FileInputStream(file);OutputStream outs = new FileOutputStream(tranlatedFile);) {

            document = new XWPFDocument(ins);
            // 更新页眉
            List<XWPFHeader> xheader = document.getHeaderList();
            List<BElement> headers = dElement.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                for (BElement be : headers) {
                    XWPFHeader header = xheader.get(be.getIndex());
                    List<IBodyElement> ibodys = header.getBodyElements();
                    List<BElement> childs = be.getChilds();
                    updateIbodys(childs, ibodys, afterTranlated, checked);
                }
            }

            // 更新页脚
            List<XWPFFooter> xfooters = document.getFooterList();
            List<BElement> footers = dElement.getFooters();
            updateFooters(xfooters,footers,afterTranlated,checked);

            // 更新脚注
            List<XWPFFootnote> xfootnote = document.getFootnotes();
            List<BElement> footnotes = dElement.getFootnotes();
            updateFootnotes(xfootnote,footnotes,afterTranlated,checked);

            // 更新尾注
            List<XWPFFootnote> xendnotes = getEndnote(document);
            List<BElement> endnotes = dElement.getEndnotes();
            if (endnotes != null && !endnotes.isEmpty()) {
                for (BElement bodyElement : endnotes) {
                    int index = bodyElement.getIndex();
                    int id = xendnotes.get(index).getCTFtnEdn().getId().intValue();
                    XWPFFootnote xhd = document.getEndnoteByID(id);
                    List<BElement> childs = bodyElement.getChilds();
                    List<IBodyElement> ibodys = xhd.getBodyElements();
                    updateIbodys(childs, ibodys, afterTranlated, checked);
                }
            }

            // 更新正文
            List<PElement> pElements = dElement.getParts();
            for (PElement pElement : pElements) {
                List<BElement> bodys = pElement.getBodyElements();
                for (BElement body : bodys) {
                    int index = body.getIndex();
                    IBodyElement ibody = document.getBodyElements().get(index);
                    updateIBody(ibody, body, afterTranlated, checked);
                }
            }

            // 更新文本框
            List<BElement> tbxElements = dElement.getTextboxs();
            updateTXBoxs(document, tbxElements, afterTranlated, checked);

            document.write(outs);
            return tranlatedFile.getPath();
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }
        return null;
    }

    /**
     * 译文更新段落；afterTranlated=null时生成纯译文，afterTranlated=true时生成原译文对照文，
     * afterTranlated=false时生成译原文对照文
     * 
     * @param paragraph
     * @param body
     * @param afterTranlated
     *            afterTranlated={null，true，false}
     */
    private void updateIBody(XWPFParagraph paragraph, BElement body, Boolean afterTranlated, boolean checked) {
        if (afterTranlated != null && afterTranlated) {
            updateIBodyAppendTran(paragraph, body, checked);
        } else {
            updateIBodyWithTran(paragraph, body, checked);
        }
    }

    /**
     * 纯译文
     * 
     * @param paragraph
     * @param body
     * @param checked
     */
    private void updateIBodyWithTran(XWPFParagraph paragraph, BElement bElement, boolean checked) {
        List<SentenceElement> tranSentences = getTranSentenceElement(bElement,checked);
        if (tranSentences == null || tranSentences.isEmpty()) {
            return;
        }
        
        for (SentenceElement sentence : tranSentences) {
            updateXWPFRunsWithTran(paragraph,sentence);
        }
    }
    
    private void updateXWPFRunsWithTran(XWPFParagraph paragraph, SentenceElement sentence) {
        String text = getTranText(sentence);
        ContentType contentType = sentence.getContentType();
        
        if (ContentType.TEXT.equals(contentType)) {
            List<ContentElement> contents = sentence.getContents();
            int runIndex = contents.get(0).getContentSerial();
            int runLast = contents.get(contents.size() - 1).getContentSerial();
            List<XWPFRun> xruns = paragraph.getRuns();
            if (xruns.isEmpty()) {
               return;
            }
            
            for (int i = runIndex; i <= runLast; i++) {
                XWPFRun xrun = xruns.get(i);
                xrun.setText("", 0);
            }
            xruns.get(runLast).setText(text, 0);
        } else {
            // 图片不处理
            List<ContentElement> contents = sentence.getContents();
            List<XWPFRun> xruns = paragraph.getRuns();
            if (contents != null && !contents.isEmpty()) {
                sentence.getSentenceSerial();
                int runIndex = contents.get(0).getContentSerial();
                XWPFRun xrun = xruns.get(runIndex);
                xrun.addBreak();
                xrun.setText(text);
            } else {
                XWPFRun xrun = paragraph.createRun();
                xrun.addBreak();
                xrun.setText(text);
            }
        }
    }

    /**
     * 原译文
     * 
     * @param paragraph
     * @param body
     * @param checked
     */

    private void updateIBodyAppendTran(XWPFParagraph paragraph, BElement bElement, boolean checked) {
        List<SentenceElement> tranSentences = getTranSentenceElement(bElement, checked);
        
        if (tranSentences == null  || tranSentences.isEmpty()) {
            return;
        }
        
        // 最后一个XWPFRun后增加软换行标记
        StringBuilder text = new StringBuilder();
        for (SentenceElement sentence : tranSentences) {
            updateXWPFRunAppendTran(paragraph,sentence,text);
        }
        //默认为翻译内容   0
        if (StringUtils.isNotBlank(text.toString())) {
            int ext_contenttype = tranSentences.get(tranSentences.size()-1).getExt_contenttype();
            List<XWPFRun> xruns = paragraph.getRuns();
            XWPFRun lastXRun = xruns.get(xruns.size() - 1);
            if (TRANS == ext_contenttype) {
                lastXRun.addBreak();
                lastXRun.setText(text.toString());
            } else {
                lastXRun.setText("");// 为非译
            }
        }
    }
    
    
    private void updateXWPFRunAppendTran(XWPFParagraph paragraph,SentenceElement sentence,StringBuilder text){
        List<XWPFRun> xruns = paragraph.getRuns();
        XWPFRun lastXRun;
        
        ContentType contentType = sentence.getContentType();
        int runIndex = sentence.getSentenceSerial();
        
        if (ContentType.TEXT.equals(contentType)) {
            text.append(getTranText(sentence));
        } else {
            if (runIndex == 0) {
                if (!xruns.isEmpty()) {
                    lastXRun = xruns.get(0);
                } else {
                    lastXRun = paragraph.createRun();
                }
            } else {
                lastXRun = xruns.get(runIndex - 1);
            }
            if (lastXRun == null) {
                lastXRun = paragraph.createRun();
            }
            lastXRun.addBreak();
            if (StringUtils.isNotBlank(text.toString())) {
                lastXRun.setText(text.toString());
            } else {
                lastXRun.setText(sentence.getText());
            }
            text.delete(0, text.length());
        }
    }
    
    private BElement parseParagraphTXBox(XWPFParagraph paragraph, String directory, XWPFDocument xdocument, boolean wordSplit) {
//      段落中的TXBOX
        List<BElement> childs = parseTXBox(paragraph,wordSplit);
        BElement be = new BElement();
        be.setChilds(childs);
        return be;
    }
    
    private BElement parseParagraphImage(XWPFParagraph paragraph, String directory, XWPFDocument xdocument, boolean isWord) {
//      段落中的内嵌图片
        CTP ctp = paragraph.getCTP();
        XmlObject ctpXml = ctp.copy();
        if (ctpXml.getDomNode().getLastChild() != null) {
            Node domNode = ctpXml.getDomNode().getLastChild().getLastChild();
            if (domNode != null && StringUtils.equals(domNode.getLocalName(), "pict")) {
                XWPFPictureData pdata = null;
                String imageId = "";
                if (domNode != null && !StringUtils.equals(domNode.getLastChild().getLocalName(), "group")) {
                    NodeList nds = domNode.getLastChild().getChildNodes();
                    for (int i = 0; i < nds.getLength(); i++) {
                        Element e = (Element) nds.item(i);
                        if (StringUtils.equals(e.getLocalName(), "imagedata")) {
                            imageId = e.getAttribute("r:id");
                            pdata = xdocument.getPictureDataByID(imageId);
                            break;
                        }
                    }
                    if (pdata != null) {
                        String picname = pdata.getFileName();
                        byte[] picdata = pdata.getData();
                        String contentText = null;
                        ContentType contentType = null;
                        File imageFile = new File(directory, imageId + "_" + picname);
                        try (FileOutputStream fos = new FileOutputStream(imageFile);) {
                            fos.write(picdata);
                            contentType = ContentType.IMAGE;
                            contentText = imageFile.getPath();
                        } catch (IOException e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(e.getMessage(), e);
                            }
                        }
                        BElement bodyElement = new BElement();
                        List<SentenceElement> sentences = new ArrayList<>();
                        bodyElement.setSentences(sentences);
                        SentenceElement sentenceElement = new SentenceElement();
                        sentenceElement = new SentenceElement();
                        sentences.add(sentenceElement);
                        sentenceElement.setContentType(contentType);
                        sentenceElement.setSentenceSerial(0);
                        List<ContentElement> contents = new ArrayList<>();
                        sentenceElement.setContents(contents);
                        sentenceElement.setText(contentText);

                        // 分割片段内容为句
                        String[] childTexts = new String[1];
                        childTexts[0] = contentText;
                        for (String childText : childTexts) {
                            logger.info(childText);
                        }
                        sentenceElement.setChildTexts(childTexts);

                        return bodyElement;
                    }
                }
            }
        }
        return null;
    }

    private BElement parseParagraph(XWPFParagraph paragraph, String directory, XWPFDocument xdocument, boolean isWord) {
        BElement bodyElement = null;
      
        // 内嵌图片
        CTP ctp = paragraph.getCTP();
        XmlObject ctpXml = ctp.copy();
        if (ctpXml.getDomNode().getLastChild() != null) {
            Node domNode = ctpXml.getDomNode().getLastChild().getLastChild();
            if (domNode != null && StringUtils.equals(domNode.getLocalName(), "pict")) {
                XWPFPictureData pdata = null;
                String imageId = "";
                if (domNode != null && !StringUtils.equals(domNode.getLastChild().getLocalName(), "group")) {
                    NodeList nds = domNode.getLastChild().getChildNodes();
                    for (int i = 0; i < nds.getLength(); i++) {
                        Element e = (Element) nds.item(i);
                        if (StringUtils.equals(e.getLocalName(), "imagedata")) {
                            imageId = e.getAttribute("r:id");
                            pdata = xdocument.getPictureDataByID(imageId);
                            break;
                        }
                    }
                    if (pdata != null) {
                        String picname = pdata.getFileName();
                        byte[] picdata = pdata.getData();
                        String contentText = null;
                        ContentType contentType = null;
                        File imageFile = new File(directory, imageId + "_" + picname);
                        try (FileOutputStream fos = new FileOutputStream(imageFile);) {
                            fos.write(picdata);
                            contentType = ContentType.IMAGE;
                            contentText = imageFile.getPath();
                        } catch (IOException e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(e.getMessage(), e);
                            }
                        }
                        bodyElement = new BElement();
                        List<SentenceElement> sentences = new ArrayList<>();
                        bodyElement.setSentences(sentences);
                        SentenceElement sentenceElement = new SentenceElement();
                        sentenceElement = new SentenceElement();
                        sentences.add(sentenceElement);
                        sentenceElement.setContentType(contentType);
                        sentenceElement.setSentenceSerial(0);
                        List<ContentElement> contents = new ArrayList<>();
                        sentenceElement.setContents(contents);
                        sentenceElement.setText(contentText);

                        // 分割片段内容为句
                        String[] childTexts = new String[1];
                        childTexts[0] = contentText;
                        for (String childText : childTexts) {
                            logger.info(childText);
                        }
                        sentenceElement.setChildTexts(childTexts);

                        return bodyElement;
                    }
                }
            }
        }
        String content = paragraph.getText().trim();
        boolean isEmpty = contentIsEmpty(paragraph);
        if (!isEmpty) {
            int sentenceSerial = 0;
            int runNum = 0;
            String sentenceText = "";
            int charNumber = countWordNumber(deleteSpace(content));
            int wordNumber = isWord ? wordNumberDeleteSpace(content,isWord) : charNumber;
            bodyElement = new BElement();
            bodyElement.setCharNumber(charNumber);
            bodyElement.setWordNumber(wordNumber);
            List<SentenceElement> sentences = new ArrayList<>();
            bodyElement.setSentences(sentences);

            SentenceElement sentenceElement = new SentenceElement();
            sentences.add(sentenceElement);
            sentenceElement.setSentenceSerial(sentenceSerial);
            List<ContentElement> contents = new ArrayList<>();
            sentenceElement.setContents(contents);

            List<XWPFRun> xruns = paragraph.getRuns();
            String contentText = null;
            ContentType contentType = null;
            boolean isbreak = false;
            for (XWPFRun xrun : xruns) {
                runNum++;
                String text = xrun.text();

                CTR ftn = xrun.getCTR();
                XmlObject o = ftn.copy();
                Node node = o.getDomNode().getLastChild();
                String name = node.getLocalName();
                // 判断脚注
                if (StringUtils.equals(name, "footnoteReference") || StringUtils.equals(name, "endnoteReference")) {
                    if (sentenceElement.getContentType() != null) {
                        isbreak = true;
                    }
                    continue;
                }
                // 软换除是否需要断句?(目前没断句)

                if (isbreak || StringUtils.isBlank(text)) {
                    if (ContentType.TEXT.equals(sentenceElement.getContentType())) {
                        String[] childTexts = splitContentFirstDeleteBR(sentenceElement.getText());
                        if (childTexts != null) {
                            logger.info("句开始====");
                            for (String childText : childTexts) {
                                logger.info(childText);
                            }
                            logger.info("句结束====");
                            sentenceElement.setChildTexts(childTexts);
                        }
                    }
                    logger.info(sentenceElement.getText());
                }

                if (StringUtils.isNotEmpty(text)) {
                    contentType = ContentType.TEXT;
                    contentText = text;
                    if (isbreak) {
                        // 创建一个新的句段元素对象
                        sentenceElement = new SentenceElement();
                        sentences.add(sentenceElement);
                        sentenceElement.setSentenceSerial(++sentenceSerial);
                        contents = new ArrayList<>();
                        sentenceElement.setContents(contents);
                        sentenceText = text;
                    } else {
                        sentenceText += text;
                    }
                    sentenceElement.setContentType(contentType);
                    sentenceElement.setText(sentenceText);
                    isbreak = breakSentence(deleteSpace(sentenceText));
                } else {
                    List<XWPFPicture> xpics = xrun.getEmbeddedPictures();
                    if (xpics != null && !xpics.isEmpty()) {
                        for (XWPFPicture xpic : xpics) {
                            XWPFPictureData xpdata = xpic.getPictureData();

                            File imageFile = new File(directory, sentenceSerial + "_" + xpdata.getFileName());
                            try (FileOutputStream fos = new FileOutputStream(imageFile);) {
                                fos.write(xpdata.getData());
                                contentType = ContentType.IMAGE;
                                contentText = imageFile.getPath();
                            } catch (IOException e) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(e.getMessage(), e);
                                }
                            }
                            // 遇见图片直接断句；一张图片一句
                            isbreak = true;
                            if (sentenceElement.getContentType() != null) {
                                sentenceElement = new SentenceElement();
                                sentences.add(sentenceElement);
                            }
                            sentenceElement.setContentType(contentType);
                            sentenceElement.setSentenceSerial(++sentenceSerial);
                            contents = new ArrayList<>();
                            sentenceElement.setContents(contents);
                            sentenceText = text;
                            sentenceElement.setText(contentText);
                            // 分割片段内容为句
                            String[] childTexts = new String[1];
                            childTexts[0] = contentText;
                            sentenceElement.setChildTexts(childTexts);
                        }
                    } else {
                        CTR ctr = xrun.getCTR();
                        XmlObject ctrXml = ctr.copy();
                        if (ctrXml.getDomNode().getLastChild() != null) {
                            Node domNode = ctrXml.getDomNode().getLastChild();
                            if (domNode != null && StringUtils.equals(domNode.getLocalName(), "pict")) {
                                logger.info("image --");
                                XWPFPictureData pdata = null;
                                String imageId = "";
                                NodeList nds = domNode.getLastChild().getChildNodes();
                                for (int i = 0; i < nds.getLength(); i++) {
                                    Element e = (Element) nds.item(i);
                                    if (StringUtils.equals(e.getLocalName(), "imagedata")) {
                                        imageId = e.getAttribute("r:id");
                                        pdata = xdocument.getPictureDataByID(imageId);
                                        break;
                                    }
                                }
                                if (pdata != null) {
                                    String picname = pdata.getFileName();
                                    byte[] picdata = pdata.getData();

                                    File imageFile = new File(directory, imageId + "_" + picname);
                                    try (FileOutputStream fos = new FileOutputStream(imageFile);) {
                                        fos.write(picdata);
                                        contentType = ContentType.IMAGE;
                                        contentText = imageFile.getPath();
                                    } catch (IOException e) {
                                        if (logger.isDebugEnabled()) {
                                            logger.debug(e.getMessage(), e);
                                        }
                                    }
                                    // 遇见图片直接断句；一张图片一句
                                    isbreak = true;
                                    if (sentenceElement.getContentType() != null) {
                                        sentenceElement = new SentenceElement();
                                        sentences.add(sentenceElement);
                                    }
                                    sentenceElement.setContentType(contentType);
                                    sentenceElement.setSentenceSerial(++sentenceSerial);
                                    contents = new ArrayList<>();
                                    sentenceElement.setContents(contents);
                                    sentenceText = text;
                                    sentenceElement.setText(contentText);
                                    // 分割片段内容为句
                                    String[] childTexts = new String[1];
                                    childTexts[0] = contentText;
                                    sentenceElement.setChildTexts(childTexts);
                                }
                            } else if (domNode != null && StringUtils.equals(domNode.getLocalName(), "object")) {
                                Node imagedataNode = getNodePicts(domNode);
                                if (imagedataNode != null) {
                                    String imageId = ((Element) imagedataNode).getAttribute("r:id");
                                    XWPFPictureData pdata = xdocument.getPictureDataByID(imageId);
                                    if (pdata != null) {
                                        contentText = saveXWPFPictureData(directory, pdata, imageId);
                                        contentType = ContentType.IMAGE;
                                        // 遇见图片直接断句；一张图片一句
                                        isbreak = true;
                                        if (sentenceElement.getContentType() != null) {
                                            sentenceElement = new SentenceElement();
                                            sentences.add(sentenceElement);
                                        }
                                        sentenceElement.setContentType(contentType);
                                        sentenceElement.setSentenceSerial(++sentenceSerial);
                                        contents = new ArrayList<>();
                                        sentenceElement.setContents(contents);
                                        sentenceText = text;
                                        sentenceElement.setText(contentText);
                                        // 分割片段内容为句
                                        String[] childTexts = new String[1];
                                        childTexts[0] = contentText;
                                        sentenceElement.setChildTexts(childTexts);
                                    }
                                }
                            } else {
                                LogUtils.writeDebugLog(logger, "TXBElement");
                            }
                        }
                        continue;
                    }
                }
                ContentElement contentElement = new ContentElement();
                contentElement.setContentSerial(runNum - 1);
                contentElement.setContentType(contentType);
                contentElement.setContentText(contentText);
                contents.add(contentElement);
            }
            // 分割片段内容为句;文本内容拆分，图片内容地址不拆分;（段落末尾元素）
            if (ContentType.TEXT.equals(sentenceElement.getContentType())) {
                String[] childTexts = splitContentFirstDeleteBR(sentenceElement.getText());
                if (childTexts != null) {
                    LogUtils.writeDebugLog(logger, "句开始====");
                    for (String childText : childTexts) {
                        LogUtils.writeDebugLog(logger, childText);
                    }
                    LogUtils.writeDebugLog(logger, "句结束====");
                    sentenceElement.setChildTexts(childTexts);
                }
            }
            logger.info(sentenceElement.getText());
        }
        return bodyElement;
    }

    private boolean contentIsEmpty(XWPFParagraph paragraph) {
        String content = paragraph.getText().trim();
        if (content.length() > 0) {
            return false;
        }
        List<XWPFRun> xruns = paragraph.getRuns();
        for (XWPFRun xrun : xruns) {
            List<XWPFPicture> xpics = xrun.getEmbeddedPictures();
            if (xpics != null && !xpics.isEmpty()) {
                return false;
            } else {
                CTR ftn = xrun.getCTR();
                XmlObject o = ftn.copy();
                Node node = o.getDomNode().getLastChild();
                String name = node.getLocalName();
                if (StringUtils.equals(name, "object")) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<BElement> parseIbody(List<IBody> ibodys, XWPFDocument document, String directory, boolean isWord) {
        List<BElement> hds = null;
        if (ibodys != null) {
            hds = new ArrayList<>();
            int index = 0;
            for (IBody ibody : ibodys) {
                List<IBodyElement> ibodyClilds = ibody.getBodyElements();
                List<BElement> childs = parseIBodyElements(ibodyClilds, document, directory, isWord);

                BElement be = new BElement();
                be.setIndex(index++);
                be.setChilds(childs);
                hds.add(be);
            }
        }
        return hds;
    }

    @SuppressWarnings("unchecked")
    private <T> List<BElement> parseFootnote(List<T> footnotes, XWPFDocument document, String directory,
            boolean isWord) {
        return parseIbody((List<IBody>) footnotes, document, directory, isWord);
    }

    @SuppressWarnings("unchecked")
    private <T> List<BElement> parseEndnote(List<T> endnotes, XWPFDocument document, String directory, boolean isWord) {
        return parseIbody((List<IBody>) endnotes, document, directory, isWord);
    }

    private List<BElement> parseHeader(List<XWPFHeader> headers, XWPFDocument document, String directory,
            boolean isWord) {
        List<BElement> hds = null;
        if (headers != null) {
            hds = new ArrayList<>();
            int index = 0;
            for (XWPFHeader header : headers) {
                List<IBodyElement> ibodys = header.getBodyElements();

                List<BElement> childs = parseIBodyElements(ibodys, document, directory, isWord);
                BElement be = new BElement();
                be.setIndex(index++);
                be.setChilds(childs);
                hds.add(be);
            }
        }
        return hds;
    }

    private List<BElement> parseFooter(List<XWPFFooter> footers, XWPFDocument document, String directory,
            boolean isWord) {
        List<BElement> hds = null;
        if (footers != null) {
            hds = new ArrayList<>();
            int index = 0;
            for (XWPFFooter footer : footers) {
                List<IBodyElement> ibodys = footer.getBodyElements();

                List<BElement> childs = parseIBodyElements(ibodys, document, directory, isWord);
                BElement be = new BElement();
                be.setIndex(index++);
                be.setChilds(childs);
                hds.add(be);
            }
        }
        return hds;
    }

    @SuppressWarnings("deprecation")
    private List<XWPFFootnote> getEndnote(XWPFDocument document) {
        List<XWPFFootnote> endnotes = new ArrayList<>();
        try {
            for (POIXMLDocumentPart p : document.getRelations()) {
                String relation = p.getPackageRelationship().getRelationshipType();
                if (relation.equals(XWPFRelation.ENDNOTE.getRelation())) {
                    EndnotesDocument endnotesDocument = EndnotesDocument.Factory
                            .parse(p.getPackagePart().getInputStream());
                    for (CTFtnEdn ctFtnEdn : endnotesDocument.getEndnotes().getEndnoteArray()) {
                        endnotes.add(new XWPFFootnote(document, ctFtnEdn));
                    }
                }
            }
            return endnotes;
        } catch (XmlException | IOException e) {
             LogUtils.writeWarnExceptionLog(logger, e);
        }
        return endnotes;
    }

//  解析 全文中的文本框
    private List<BElement> parseTXBoxs(List<IBodyElement> bodys, String directory, boolean isWord) {
        // 元素序号
        int bodySerial = 0;
        List<BElement> bodyElements = new ArrayList<>();
        for (IBodyElement body : bodys) {
            logger.info(body.getElementType().name());
            String etype = body.getElementType().name();

            if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), etype)) {
                List<BElement> childs = parseTXBox((XWPFParagraph) body,isWord);
                BElement be = new BElement();
                be.setIndex(bodySerial);
                be.setChilds(childs);

                bodyElements.add(be);
            } else if (StringUtils.equals(BodyElementType.TABLE.name(), etype)) {
                XWPFTable xt = (XWPFTable) body;
                List<XWPFTableRow> rows = xt.getRows();
                int rowNum = 0;
                for (XWPFTableRow row : rows) {
                    rowNum += 1;
                    List<XWPFTableCell> cells = row.getTableCells();

                    int columnNum = 0;
                    for (XWPFTableCell cell : cells) {
                        columnNum += 1;
                        List<IBodyElement> bodyElementsOfCell = cell.getBodyElements();

                        BElement be = new BElement();
                        List<BElement> childs = parseTXBoxs(bodyElementsOfCell, directory, isWord);
                        be.setRowNum(rowNum);
                        be.setColumnNum(columnNum);
                        be.setIndex(bodySerial);
                        be.setChilds(childs);

                        bodyElements.add(be);
                    }
                }
            } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), etype)) {
                XWPFSDT sdt = (XWPFSDT) body;
                logger.info(sdt.getContent().getText());
            }
            bodySerial += 1;
        }
        return bodyElements;
    }
    
//  解析段落中的文本框
    private List<BElement> parseTXBox(XWPFParagraph paragraph,boolean wordSplit) {
        XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(
                "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent");

        List<BElement> tbxElements = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < textBoxObjects.length; i++) {
            try {
                XmlObject[] paraObjects = textBoxObjects[i]
                        .selectChildren(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));
                BElement tbxElement = new BElement();
                tbxElements.add(tbxElement);

                tbxElement.setIndex(index++);
                tbxElement.setName("TEXTBOX");
                tbxElement.setColumnNum(1);
                tbxElement.setRowNum(1);
                if (paraObjects.length > 0) {
                    List<SentenceElement> sentences = new ArrayList<>();
                    tbxElement.setSentences(sentences);
                    int charNumber = 0;
                    int wordNumber = 0 ;
                    for (int j = 0; j < paraObjects.length; j++) {

                        XWPFParagraph embeddedPara = new XWPFParagraph(CTP.Factory.parse(paraObjects[j].xmlText()),
                                paragraph.getBody());
                        String text = embeddedPara.getText();
                        charNumber+=charNumberAfterDeleteSpace(text);
                        wordNumber+=wordNumberDeleteSpace(text, wordSplit);
                        SentenceElement sentenceElement = new SentenceElement();
                        sentenceElement.setContentType(ContentType.TEXT);
                        sentenceElement.setSentenceSerial(j);
                        logger.info(text);
                        sentenceElement.setText(text);
                        // 分割片段内容为句
                        String[] childTexts = splitContentFirstDeleteBR(sentenceElement.getText());
                        for (String childText : childTexts) {
                            logger.info(childText);
                        }
                        sentenceElement.setChildTexts(childTexts);
                        sentences.add(sentenceElement);
                    }
                    tbxElement.setCharNumber(charNumber);
                    tbxElement.setWordNumber(wordNumber);
                }
            } catch (XmlException e) {
                LogUtils.writeWarnExceptionLog(logger, e);
            }
        }
        return tbxElements;
    }

    // 更新文本框
    private void updateTXBoxs(XWPFDocument document, List<BElement> tbxElements, Boolean afterTranlated,
            boolean checked) {
        for (BElement tbxElement : tbxElements) {
            int index = tbxElement.getIndex();
            IBodyElement ibody = document.getBodyElements().get(index);
            if (ibody instanceof XWPFParagraph) {
                updateTXBox((XWPFParagraph) ibody, tbxElement, afterTranlated, checked);
                // updateTextBoxBody_2007((XWPFParagraph) ibody,
                // bodyElement,afterTranlated, true)
            } else if (ibody instanceof XWPFTable) {
                updateTXBox((XWPFTable) ibody, tbxElement, afterTranlated, checked);
            }
        }
    }

    private void updateTXBox(XWPFParagraph paragraph, BElement bElement, Boolean afterTranlated,
            boolean checked) {
        XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(
                "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent");

        List<BElement> childs = bElement.getChilds();

        for (BElement child : childs) {

            try {
                XmlObject[] paraObjects = textBoxObjects[child.getIndex()]
                        .selectChildren(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));

                List<SentenceElement> sentenceElements = getTranSentenceElement(child, checked);
                
                for (SentenceElement sentenceElement : sentenceElements) {

                    String tranText = getTranText(sentenceElement);

                    XmlObject xobj = paraObjects[sentenceElement.getSentenceSerial()];
                    XmlCursor xmlCursor = xobj.newCursor();
                    xmlCursor.push();// 保存当前位置
                    xmlCursor.toLastChild();// w:r
                    boolean updated = false;
                    do {
                        // 更新译文
                        xmlCursor.toLastChild();// w:t
                        QName qname = xmlCursor.getName();
                        String pname = qname.getPrefix();
                        String lname = qname.getLocalPart();
                        if (pname.equalsIgnoreCase("w") && lname.equalsIgnoreCase("t")) {

                            logger.info(xmlCursor.getTextValue());
                            if (!updated) {
                                xmlCursor.setTextValue(tranText);
                                updated = true;
                            } else {
                                xmlCursor.setTextValue("");
                            }
                            logger.info(xmlCursor.getTextValue());
                        }
                        // xmlCursor.toParent()
                    } while (xmlCursor.toPrevSibling());
                }
            } catch (Exception e) {
                LogUtils.writeWarnExceptionLog(logger, e);
            }
        }
    }

    @SuppressWarnings("unused")
    private void updateTextBoxBody_2007(XWPFParagraph paragraph, BElement bElement, Boolean afterTranlated,
            boolean checked) {
        String nameSpace = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/v:textbox/w:txbxContent";

        XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(nameSpace);

        int index = bElement.getIndex();

        try {
            XmlObject[] paraObjects = textBoxObjects[index]
                    .selectChildren(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));

            List<SentenceElement> sentenceElements;
            if (checked) {
                sentenceElements = bElement.getCheckSentences() != null ? bElement.getCheckSentences()
                        : bElement.getSentences();
            } else {
                sentenceElements = bElement.getTranSentences() != null ? bElement.getTranSentences()
                        : bElement.getSentences();
            }
            for (SentenceElement sentenceElement : sentenceElements) {

                String tranText = getTranText(sentenceElements.get(0));
                XmlObject xobj = paraObjects[sentenceElement.getSentenceSerial()];
                XmlCursor xmlCursor = xobj.newCursor();
                xmlCursor.push();// 保存当前位置
                xmlCursor.toLastChild();// w:r
                boolean updated = false;
                do {
                    // 更新译文
                    xmlCursor.toLastChild();// w:t
                    QName qname = xmlCursor.getName();
                    String pname = qname.getPrefix();
                    String lname = qname.getLocalPart();
                    if (pname.equalsIgnoreCase("w") && lname.equalsIgnoreCase("t")) {

                        logger.info(xmlCursor.getTextValue());
                        if (!updated) {
                            xmlCursor.setTextValue(tranText);
                            updated = true;
                        } else {
                            xmlCursor.setTextValue("");
                        }
                        logger.info(xmlCursor.getTextValue());
                    }
                    xmlCursor.toParent();
                } while (xmlCursor.toPrevSibling());
            }
        } catch (Exception e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        }
        paragraph.getCTP();
    }

    private void updateTXBox(XWPFTable itable, BElement bElement, Boolean afterTranlated, boolean checked) {
        int rowNum = bElement.getRowNum();
        int columnNum = bElement.getColumnNum();
        int index = bElement.getIndex();

        XWPFTableRow tableRow = itable.getRow(rowNum);
        if (tableRow != null) {
            if (tableRow.getCell(columnNum) != null) {
                XWPFTableCell cell = tableRow.getCell(columnNum);
                List<XWPFParagraph> paragraphs = cell.getParagraphs();
                if (paragraphs != null && !paragraphs.isEmpty()) {
                    if (index < paragraphs.size()) {
                        XWPFParagraph paragraph = paragraphs.get(index);
                        updateTXBox(paragraph, bElement, afterTranlated, checked);
                    }
                }
            }
        }
    }

    private void updateFootnotes(List<XWPFFootnote> xfooters, List<BElement> footers, Boolean afterTranlated,
            boolean checked) {
        if (footers != null && !footers.isEmpty()) {
            for (BElement be : footers) {
                XWPFFootnote header = xfooters.get(be.getIndex());
                List<IBodyElement> ibodys = header.getBodyElements();
                List<BElement> childs = be.getChilds();
                updateIbodys(childs, ibodys, afterTranlated, checked);
            }
        }
    }

    private void updateFooters(List<XWPFFooter> xfooters, List<BElement> footers, Boolean afterTranlated,
            boolean checked) {
        if (footers != null && !footers.isEmpty()) {
            for (BElement be : footers) {
                XWPFFooter xfooter = xfooters.get(be.getIndex());
                List<IBodyElement> ibodys = xfooter.getBodyElements();
                List<BElement> childs = be.getChilds();
                updateIbodys(childs, ibodys, afterTranlated, checked);
            }
        }
    }

    private void updateIbodys(List<BElement> childs, List<IBodyElement> ibodys, Boolean afterTranlated,
            boolean checked) {
        if (childs != null && !childs.isEmpty()) {
            for (BElement body : childs) {
                int bodyIndex = body.getIndex();
                IBodyElement ibody = ibodys.get(bodyIndex);
                updateIBody(ibody, body, afterTranlated, checked);
            }
        }
    }

    @SuppressWarnings("unused")
    private void printContentsOfTextBox(XWPFParagraph paragraph) {
        XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(
                "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent");

        for (int i = 0; i < textBoxObjects.length; i++) {
            XWPFParagraph embeddedPara = null;
            try {
                XmlObject[] paraObjects = textBoxObjects[i]
                        .selectChildren(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));

                for (int j = 0; j < paraObjects.length; j++) {
                    embeddedPara = new XWPFParagraph(CTP.Factory.parse(paraObjects[j].xmlText()), paragraph.getBody());
                    // Here you have your paragraph
                    LogUtils.writeDebugLog(logger,"[printContentsOfTextBox] : "+ embeddedPara.getText());
                }

            } catch (XmlException e) {
                LogUtils.writeWarnExceptionLog(logger, e);
            }
        }

    }

    private Node getNodePicts(Node node) {
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node childNode = childs.item(i);
            String qname = childNode.getLocalName();
            if (StringUtils.equals(qname, "imagedata")) {
                logger.info("object-shape-imagedata:");
                return childNode;
            } else {
                Node result = getNodePicts(childNode);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private String saveXWPFPictureData(String directory, XWPFPictureData pdata, String imageId) {
        File imageFile = new File(directory, imageId + "_" + pdata.getFileName());
        try (FileOutputStream fos = new FileOutputStream(imageFile);){
            fos.write(pdata.getData());
            return imageFile.getPath();
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } 
        return null;
    }

    private List<BElement> parseIBodyElement(int index, IBodyElement body, XWPFDocument document, String directory,
            boolean wordSplit) {
        List<BElement> bodyElements = new ArrayList<>();
        String elementType = body.getElementType().name();
        logger.info("IBodyElement elementType : " + elementType);

        if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
            BElement bodyElement = parseParagraph((XWPFParagraph) body, directory, document, wordSplit);
            if (bodyElement != null) {
                bodyElement.setIndex(index);
                bodyElement.setName(BodyElementType.PARAGRAPH.name());
                bodyElement.setRowNum(1);
                bodyElement.setColumnNum(1);
                bodyElements.add(bodyElement);
            }

        } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
            XWPFTable xt = (XWPFTable) body;
            List<XWPFTableRow> rows = xt.getRows();
            int rowNum = 0;
            for (XWPFTableRow row : rows) {
                rowNum += 1;
                List<XWPFTableCell> cells = row.getTableCells();

                int columnNum = 0;
                for (XWPFTableCell cell : cells) {
                    columnNum += 1;
                    List<IBodyElement> bodysOfCell = cell.getBodyElements();
                    List<BElement> childs = parseIBodyElements(bodysOfCell, document, directory, wordSplit);
                    
                    BElement bodyElement = createBElementByChild(childs);
                    bodyElement.setIndex(index);
                    bodyElement.setName(BodyElementType.TABLE.name());
                    bodyElement.setRowNum(rowNum);
                    bodyElement.setColumnNum(columnNum);
                    
                    bodyElements.add(bodyElement);
                }
            }
        } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {
            XWPFSDT sdt = (XWPFSDT) body;
            logger.info(sdt.getContent().getText());
        }
        return bodyElements;
    }

    private List<BElement> parseIBodyElements(List<IBodyElement> bodys, XWPFDocument document, String directory,
            boolean isWord) {
        int bodySerial = 0;
        List<BElement> bodyElements = new ArrayList<>();
        for (IBodyElement body : bodys) {
            List<BElement> bElements = parseIBodyElement(bodySerial, body, document, directory, isWord);
            bodyElements.addAll(bElements);
            bodySerial += 1;
        }
        return bodyElements;
    }

    private List<PElement> parseBodys(List<IBodyElement> bodys, XWPFDocument document, String directory,
            boolean wordSplit,long pLength,int pNumber) {

        List<PElement> parts = new ArrayList<>();
        // 拆分文档序号
        int partSerial = 1;
        // 元素序号
        int bodySerial = 0;
        int length = 0;
        List<BElement> bodyElements4Part = null;

        for (IBodyElement body : bodys) {
            List<BElement> bElements = parseIBodyElement(bodySerial, body, document, directory, wordSplit);
            if (length == 0) {
                bodyElements4Part = new ArrayList<>();
            }

            bodyElements4Part.addAll(bElements);
            length += getWordNumberOfBElements(bElements, wordSplit);

            if (length >= pLength && partSerial<pNumber) {
                PElement part = newPElement(bodyElements4Part);
                part.setPartId(partSerial++);
                parts.add(part);

                length = 0;
            }
            bodySerial += 1;
        }

        if (length > 0 && bodyElements4Part != null) {
            PElement part = newPElement(bodyElements4Part);
            part.setPartId(partSerial);
            parts.add(part);
        }
        return parts;
    }

    private BElement createBElementByChild(List<BElement> childs){
        BElement bElement = new BElement();
        bElement.setChilds(childs);
        int charNumber = 0;
        int wordNumber = 0;
        for (BElement child : childs) {
            charNumber += child.getCharNumber();
            wordNumber += child.getWordNumber();
        }
        bElement.setCharNumber(charNumber);
        bElement.setWordNumber(wordNumber);
        return bElement;
    }
    
    private int getWordNumberOfBElements(List<BElement> bElements, boolean wordSplit) {
        int wordNumber = 0;
        int charNumber = 0;
        for (BElement bElement : bElements) {
            wordNumber += bElement.getWordNumber();
            charNumber += bElement.getCharNumber();
        }
        if (wordSplit) {
            return wordNumber;
        } else {
            return charNumber;
        }
    }

}
