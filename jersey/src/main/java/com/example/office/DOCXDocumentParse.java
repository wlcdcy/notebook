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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
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
        DocumentParse documentParse = new DOCXDocumentParse();
        boolean checked=true;
        boolean wordSplite = false;
        boolean isTakeOriginal=false;
        String tempTemple = "D:/Users/Yahoo/Documents/360js/公式不识别.docx";
        File file = new File(tempTemple);
        int length = documentParse.charNumber(tempTemple) / 2;
        LogUtils.writeDebugLog(logger , "document word number : " + length);

        try (InputStream ins = new FileInputStream(file);) {
            DElement dElement = documentParse.documentParse(tempTemple, wordSplite, 10, 2);
            LogUtils.writeDebugLog(logger, "document word " + dElement.getWordNnumber());
            List<PElement> parts = dElement.getParts();
            for (PElement part : parts) {
                documentParse.createSubTranlatedDocument(part, tempTemple, checked);
            }
            documentParse.createTranlatedDocument(dElement, tempTemple, isTakeOriginal,checked);
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
        try(InputStream in = ins) {
            document = new XWPFDocument(in);
            List<IBodyElement> bodys = document.getBodyElements();
            for (IBodyElement body : bodys) {
                logger.info(body.getElementType().name());
                String elementType = body.getElementType().name();
                if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), elementType)) {
                    XWPFParagraph xp = (XWPFParagraph) body;
                    String content = xp.getText().trim();
                    charNumber += countWordNumber(deleteSpace(content));
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = (XWPFTable) body;
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
                    XWPFParagraph xp = (XWPFParagraph) body;
                    String content = xp.getText().trim();
                    charLength += wordNumberDeleteSpace(content,false);
                } else if (StringUtils.equals(BodyElementType.TABLE.name(), elementType)) {
                    XWPFTable xt = (XWPFTable) body;
                    List<XWPFTableRow> trs = xt.getRows();
                    for (XWPFTableRow tr : trs) {
                        List<XWPFTableCell> tcs = tr.getTableCells();
                        for (XWPFTableCell tc : tcs) {
                            charLength += wordNumberDeleteSpace(tc.getText().trim(),false);
                        }
                    }
                } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), elementType)) {
                    LogUtils.writeWarnLog(logger, BodyElementType.CONTENTCONTROL.name() +" don't parse");
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
//          页眉|字数统计不包含页眉页脚内容（与office保持一致）
            List<XWPFHeader> headers = document.getHeaderList();
            List<BElement> headerElements = parseHeaderOfDocument(headers, document, file.getParent(), wordSplit);
            if (headerElements != null) {
                dElement.setHeaders(headerElements);
            }

//          页脚|字数统计不包含页眉页脚内容（与office保持一致）
            List<XWPFFooter> footers = document.getFooterList();
            List<BElement> footerElements = parseFooterOfDocument(footers, document, file.getParent(), wordSplit);
            if (footerElements != null) {
                dElement.setFooters(footerElements);
            }
            
//          脚注
            List<XWPFFootnote> footnotes = document.getFootnotes();
            List<BElement> footnoteElements = parseFootnoteOfDocument(footnotes, document, file.getParent(), wordSplit);
            if (footnoteElements != null) {
                dElement.setFootnotes(footnoteElements);
                dElement.setWordNnumber(dElement.getWordNnumber()+countWordNumberOfBElements(footnoteElements, wordSplit));
            }

//          尾注
            List<XWPFFootnote> endnotes = getEndnote(document);
            List<BElement> endnoteElements = parseEndnoteOfDocument(endnotes, document, file.getParent(), wordSplit);
            if (endnoteElements != null && !endnoteElements.isEmpty()) {
                dElement.setEndnotes(endnoteElements);
                dElement.setWordNnumber(dElement.getWordNnumber()+countWordNumberOfBElements(endnoteElements, wordSplit));
            }

//          正文
            List<IBodyElement> bodys = document.getBodyElements();
            List<PElement> parts = parseBodys(bodys, document, file.getParent(),wordSplit,pLength, pNumber);
            dElement.setParts(parts);

//          创建原文的切片文件
            for (PElement pElement : parts) {
                String subfilePath = createSubDocument(pElement,file);
                pElement.setPartPath(subfilePath);
                dElement.setWordNnumber(dElement.getWordNnumber()+pElement.getWordNumber());
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
    
    @Override
    public DElement documentParse(String filePath, boolean wordSplit) {

        XWPFDocument document = null;
        File file = new File(filePath);
        DElement dElement = new DElement();

        try (InputStream ins = new FileInputStream(file);) {

            document = new XWPFDocument(ins);
//          脚注
            List<XWPFFootnote> footnotes = document.getFootnotes();
            List<BElement> footnoteElements = parseFootnoteOfDocument(footnotes, document, file.getParent(), wordSplit);
            if (footnoteElements != null) {
                dElement.setFootnotes(footnoteElements);
                dElement.setWordNnumber(dElement.getWordNnumber()+countWordNumberOfBElements(footnoteElements, wordSplit));
            }

//          尾注
            List<XWPFFootnote> endnotes = getEndnote(document);
            List<BElement> endnoteElements = parseEndnoteOfDocument(endnotes, document, file.getParent(), wordSplit);
            if (endnoteElements != null && !endnoteElements.isEmpty()) {
                dElement.setEndnotes(endnoteElements);
                dElement.setWordNnumber(dElement.getWordNnumber()+countWordNumberOfBElements(endnoteElements, wordSplit));
            }

//          页眉
            List<XWPFHeader> headers = document.getHeaderList();
            List<BElement> headerElements = parseHeaderOfDocument(headers, document, file.getParent(), wordSplit);
            if (headerElements != null) {
                dElement.setHeaders(headerElements);
            }

//          页脚
            List<XWPFFooter> footers = document.getFooterList();
            List<BElement> footerElements = parseFooterOfDocument(footers, document, file.getParent(), wordSplit);
            if (footerElements != null) {
                dElement.setFooters(footerElements);
            }

//          正文
            List<IBodyElement> bodys = document.getBodyElements();
            List<BElement> bodyElements = parseBodyOfDocument(bodys, document, file.getParent(),wordSplit);
            
            List<PElement> parts = new ArrayList<>();
            PElement part = new PElement();
            part.setPartId(1);
            part.setBodyElements(bodyElements);
            parts.add(part);
            
            dElement.setParts(parts);
//          正文内容字数统计
            for (PElement pElement : parts) {
                dElement.setWordNnumber(dElement.getWordNnumber()+pElement.getWordNumber());
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
        boolean isTakeOriginal = false;
        File file = new File(filePath);
        // 文件不存在时会自动创建
        File subTranlatedFile = createSubTranlatedFile(file, pElement.getPartId());
        try (InputStream ins = new FileInputStream(file); OutputStream outs = new FileOutputStream(subTranlatedFile);) {
            // document = new XWPFDocument(POIXMLDocument.openPackage(filePath))
            document = new XWPFDocument(ins);

            List<BElement> bodys = pElement.getBodyElements();
            List<IBodyElement> ibodys = document.getBodyElements();
            for (BElement bElement : bodys) {
                IBodyElement ibody = getIBodyElement(bElement, ibodys);
                updateIBody(ibody, bElement, isTakeOriginal, checked);
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
    
    private void updateIBody(IBodyElement ibody, BElement bElement, boolean isTakeOriginal, boolean checked) {
       updateIBody((XWPFParagraph) ibody, bElement, isTakeOriginal, checked);
        
    }

    @Override
    public String createTranlatedDocument(DElement dElement, String filePath, boolean isTakeOriginal,boolean checked) {
        XWPFDocument document = null;
        File tranlatedFile = createTranlatedFile(new File(filePath), isTakeOriginal);
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
                for (BElement bElement : headers) {
                    IBodyElement ibody = getIBodyElementOfHFF(bElement, xheader);
                    updateIBodyWithBElement(bElement, ibody, isTakeOriginal, checked);
                }
            }

            // 更新页脚
            List<XWPFFooter> xfooters = document.getFooterList();
            List<BElement> footers = dElement.getFooters();
            updateFooters(xfooters,footers,isTakeOriginal,checked);

            // 更新脚注
            List<XWPFFootnote> xfootnote = document.getFootnotes();
            List<BElement> footnotes = dElement.getFootnotes();
            updateFootnotes(xfootnote,footnotes,isTakeOriginal,checked);

            // 更新尾注
            List<XWPFFootnote> xendnotes = getEndnote(document);
            List<BElement> endnotes = dElement.getEndnotes();
            if (endnotes != null && !endnotes.isEmpty()) {
                for (BElement bodyElement : endnotes) {
                    int index = bodyElement.getIndex();
                    int id = xendnotes.get(index).getCTFtnEdn().getId().intValue();
                    XWPFFootnote xhd = document.getEndnoteByID(id);
                    IBodyElement ibody = getIBodyElementOfHFF(bodyElement, xendnotes);
                    updateIBody(ibody, bodyElement, isTakeOriginal, checked);
                }
            }

            // 更新正文
            List<PElement> pElements = dElement.getParts();
            List<IBodyElement>  ibodys =document.getBodyElements();
            for (PElement pElement : pElements) {
                List<BElement> bElements = pElement.getBodyElements();
                for (BElement bElement : bElements) {
                    IBodyElement ibody = getIBodyElement(bElement, ibodys);
                    updateIBodyWithBElement(bElement, ibody, isTakeOriginal, checked);
                }
            }

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
    
    private IBodyElement getIBodyElement(BElement bElement,List<IBodyElement> ibodys){
        List<IBodyElement> iBElements=ibodys;
        Object obj=null; 
        
        String path = bElement.getPath();
        String[] pIndexs = path.split(PATHSPLITSIGN);
        for(String pIndex:pIndexs){
            obj =  getIBodyElement(pIndex,iBElements);
            //文本框元素，直接返回。
            if(pIndex.split(PATHLINKSIGN).length==2){
                return (XWPFParagraph)obj;
            }
            
            if(obj instanceof XWPFTableCell ){
                iBElements = ((XWPFTableCell)obj).getBodyElements();
            }
        }
        return (XWPFParagraph)obj;
    }
    
    private IBodyElement getIBodyElementOfHFF(BElement bElement,List<?> xfooters){
        String path = bElement.getPath();
        String[] pIndexs = path.split(PATHSPLITSIGN);
        Object xwpfObj = xfooters.get(Integer.parseInt(pIndexs[0]));
        List<IBodyElement> ibodys=null;
        if(xwpfObj instanceof XWPFFooter){
            ibodys = ((XWPFFooter)xwpfObj).getBodyElements();
        }else if (xwpfObj instanceof XWPFHeader){
            ibodys = ((XWPFHeader)xwpfObj).getBodyElements();
        }else if (xwpfObj instanceof XWPFFootnote){
            ibodys = ((XWPFFootnote)xwpfObj).getBodyElements();
        }
        
        Object obj=null; 
        for(int i=1;i<pIndexs.length;i++){
            obj =  getIBodyElement(pIndexs[i],ibodys);
//          文本框元素，直接返回。
            if(pIndexs[i].split(PATHLINKSIGN).length==2){
                return (XWPFParagraph)obj;
            }
            if(obj instanceof XWPFTableCell ){
                ibodys = ((XWPFTableCell)obj).getBodyElements();
            }
        }
        return (XWPFParagraph)obj;
    }
    
    private Object getIBodyElement(String pIndex,List<IBodyElement> ibodys){
        String[] ss = pIndex.split(PATHLINKSIGN);
        int index = Integer.parseInt(ss[0]);
        IBodyElement ibody = ibodys.get(index);
        
        switch(ss.length){
            case 1:
                LogUtils.writeDebugLog(logger, "paragraph");
                return ibody;
            case 2:
                LogUtils.writeDebugLog(logger, "txtBox");
                return ibody;
            case 3:
                LogUtils.writeDebugLog(logger, "table");
                XWPFTable table =(XWPFTable) ibody;
                XWPFTableCell cell = table.getRow(Integer.parseInt(ss[1])).getCell(Integer.parseInt(ss[2]));
                return cell;
            default : return null;
        }
    }

    /**
     * 译文更新段落；isTakeOriginal=false时生成纯译文，isTakeOriginal=true时生成原译文对照文，
     * 
     * @param paragraph
     * @param body
     * @param isTakeOriginal
     *            isTakeOriginal={true，false}
     */
    private void updateIBody(XWPFParagraph paragraph, BElement body, boolean isTakeOriginal, boolean checked) {
        if (isTakeOriginal) {
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
        LogUtils.writeDebugLog(logger, "tran text : "+text);
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
            
//          图片处理 
//          1、后面直接跟图片地址
//          2、后面考虑图片回写
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
            
//          图片处理 
//          1、后面直接跟图片地址
//          2、后面考虑图片回写
            List<ContentElement> contents = sentence.getContents();
            if (contents != null && !contents.isEmpty()) {
                sentence.getSentenceSerial();
                runIndex = contents.get(0).getContentSerial();
                XWPFRun xrun = xruns.get(runIndex);
                xrun.addBreak();
                xrun.setText(getTranText(sentence));
            } else {
                XWPFRun xrun = paragraph.createRun();
                xrun.addBreak();
                xrun.setText(getTranText(sentence));
            }
        }
    }
    
    @SuppressWarnings("unused")
    private BElement parseParagraphImage(XWPFParagraph paragraph, String directory, XWPFDocument xdocument, boolean isWord) {
//      段落中的内嵌图片
        CTP ctp = paragraph.getCTP();
        XmlObject ctpXml = ctp.copy();
        if (ctpXml.getDomNode().getLastChild() != null) {
            Node domNode = ctpXml.getDomNode().getLastChild().getLastChild();
            if (domNode != null && StringUtils.equals(domNode.getLocalName(), "pict")) {
                XWPFPictureData pdata = null;
                String imageId = "";
                if (!StringUtils.equals(domNode.getLastChild().getLocalName(), "group")) {
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

    private BElement parseParagraph(XWPFParagraph paragraph, XWPFDocument xdocument, String directory, boolean isWord) {
        BElement bodyElement = null;
//      内嵌图片
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
                        
                        bodyElement.setCharNumber(1);
                        bodyElement.setWordNumber(1);
                        return bodyElement;
                    }
                }
            }
        }
        
        boolean isEmpty = contentIsEmpty(paragraph);
        if (!isEmpty) {
            int sentenceSerial = 0;
            int runNum = 0;
            String sentenceText = "";
            bodyElement = new BElement();
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
                            for (String childText : childTexts) {
                                logger.info(childText);
                            }
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
                    LogUtils.writeDebugLog(logger,"段落拆文本内容 : "+ StringUtils.join(childTexts, "    "));
                    sentenceElement.setChildTexts(childTexts);
                }
            }
            
            int charNumber = countWordNumberOfSentenceElements(sentences,isWord);
            int wordNumber = countWordNumberOfSentenceElements(sentences,isWord);
            bodyElement.setCharNumber(charNumber);
            bodyElement.setWordNumber(wordNumber);
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

    private List<BElement> parseFootnote(List<IBody> ibodys, XWPFDocument document, String directory, boolean wordSplit) {
        List<BElement> hds = null;
        if (ibodys != null) {
            hds = new ArrayList<>();
            int index = 0;
            for (IBody ibody : ibodys) {
                List<IBodyElement> ibodyClilds = ibody.getBodyElements();
                List<BElement> childs = parseIBodyElements(String.valueOf(index),ibodyClilds, document, directory, wordSplit);
                hds.addAll(childs);
                index++;
            }
        }
        return hds;
    }

    @SuppressWarnings("unchecked")
    private <T> List<BElement> parseFootnoteOfDocument(List<T> footnotes, XWPFDocument document, String directory,
            boolean isWord) {
        return parseFootnote((List<IBody>) footnotes, document, directory, isWord);
    }

    @SuppressWarnings("unchecked")
    private <T> List<BElement> parseEndnoteOfDocument(List<T> endnotes, XWPFDocument document, String directory, boolean isWord) {
        return parseFootnote((List<IBody>) endnotes, document, directory, isWord);
    }

    private List<BElement> parseHeaderOfDocument(List<XWPFHeader> headers, XWPFDocument document, String directory,
            boolean isWord) {
        List<BElement> hds = null;
        if (headers != null) {
            hds = new ArrayList<>();
            int index = 0;
            for (XWPFHeader header : headers) {
                List<IBodyElement> ibodys = header.getBodyElements();
                List<BElement> childs = parseIBodyElements(String.valueOf(index),ibodys, document, directory, isWord);
                hds.addAll(childs);
                index++;
            }
        }
        return hds;
    }

    private List<BElement> parseFooterOfDocument(List<XWPFFooter> footers, XWPFDocument document, String directory,
            boolean isWord) {
        List<BElement> hds = null;
        if (footers != null) {
            hds = new ArrayList<>();
            int index = 0;
            for (XWPFFooter footer : footers) {
                List<IBodyElement> ibodys = footer.getBodyElements();
                List<BElement> childs = parseIBodyElements(String.valueOf(index),ibodys, document, directory, isWord);
                hds.addAll(childs);
                index++;
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
    
//  解析段落中的文本框
    private List<BElement> parseTXTBox(String pIndex,int index,XWPFParagraph paragraph,boolean wordSplit) {
        XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(
                "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent");

        List<BElement> tbxElements = new ArrayList<>();
        
        for (int i = 0; i < textBoxObjects.length; i++) {
            try {
                XmlObject xmlObj = textBoxObjects[i];
                XmlCursor  cursor = xmlObj.newCursor();
                cursor.push();
                int nodeNum = cursor.getDomNode().getChildNodes().getLength();
                
                List<IBodyElement> ibodys = new ArrayList<>();
                for(int n =0;n<nodeNum;n++){
                    cursor.toChild(n);
                    String name = cursor.getName().getLocalPart();
                    LogUtils.writeDebugLog(logger, "Qname : " + name);
                    if(StringUtils.equals(name, "p")){ 
                        XWPFParagraph embeddedPara = new XWPFParagraph(CTP.Factory.parse(convertTXTBoxPToPar(cursor.xmlText())),paragraph.getBody());
                        ibodys.add(embeddedPara);
                    }else if (StringUtils.equals(name, "tbl")){
                        LogUtils.writeDebugLog(logger, "cursor.xmlText() : " + cursor.xmlText());
                        XWPFTable embeddedTable = new XWPFTable(CTTbl.Factory.parse(convertTXTBoxTb1ToTable(cursor.xmlText())),paragraph.getBody());
                        ibodys.add(embeddedTable);
                    }
                    cursor.toParent();
                }
                String p=createPathOfTXTBox(pIndex, index, i);
                List<BElement> bElements = parseIBodyElements(p, ibodys, null, "", wordSplit);
                tbxElements.addAll(bElements);
                
            } catch (Exception e) {
                LogUtils.writeWarnExceptionLog(logger, e);
            }
        }
        return tbxElements;
    }

//  更新文本框
    private void updateTXBox(XWPFParagraph paragraph, BElement bElement, boolean isTakeOriginal,
            boolean checked) {
        LogUtils.writeDebugLog(logger, "updateTXBox [pIndex] :  " + bElement.getPath());
        XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(
                "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent");
        
        BElement child = bElement;
        int index = 0;
        String pIndex="";
        
        String path = bElement.getPath();
        String pIndexs[] = path.split(PATHSPLITSIGN);
        for(int i=0;i<pIndexs.length;i++){
            String[] indexs = pIndexs[i].split(PATHLINKSIGN);
            if(indexs.length==2){
                index = Integer.parseInt(indexs[1]);
                pIndex =pIndexs[i+1];
                break;
            }
        }
        
        if(!pIndex.contains(PATHLINKSIGN)){
            int tIndex = Integer.parseInt(pIndex);
            try {
                List<SentenceElement> sentenceElements = getTranSentenceElement(child, checked);
                XmlObject xobj = textBoxObjects[index];
                XmlCursor xmlCursor = xobj.newCursor();
                if(!isTakeOriginal){
                    xmlCursor.toChild(tIndex);
                    xmlCursor.push();// 保存当前位置
                    for (SentenceElement sentenceElement : sentenceElements) {
                        String tranText = getTranText(sentenceElement);
                        xmlCursor.pop();
                        xmlCursor.toLastChild();// w:r
                        boolean updated = false;
                        do {
                            // 更新译文
                            xmlCursor.toLastChild();// w:t
                            QName qname = xmlCursor.getName();
                            String pname = qname.getPrefix();
                            String lname = qname.getLocalPart();
                            if (pname.equalsIgnoreCase("w") && lname.equalsIgnoreCase("t")) {
                                if (!updated) {
                                    xmlCursor.setTextValue(tranText);
                                    updated = true;
                                } else {
                                    xmlCursor.setTextValue("");
                                }
                                logger.info(xmlCursor.getTextValue());
                            }
                        } while (xmlCursor.toPrevSibling());
                    }   
                }else{
                    xmlCursor.toChild(tIndex);
                    xmlCursor.push();// 保存当前位置p
                    for (SentenceElement sentenceElement : sentenceElements) {
                        String tranText= getTranText(sentenceElement);
                        xmlCursor.pop();
                        xmlCursor.toLastChild();// w:r
                        xmlCursor.toParent();
                        xmlCursor.toEndToken();
                        xmlCursor.beginElement(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main","r","w"));
                        xmlCursor.toEndToken();
                        xmlCursor.beginElement(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main","br","w"));
                       
                        xmlCursor.pop();
                        xmlCursor.toLastChild();// w:r
                        xmlCursor.toParent();
                        xmlCursor.toEndToken();
                        xmlCursor.beginElement(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main","r","w"));
                        xmlCursor.toEndToken();
                        xmlCursor.insertElementWithText(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main","t","w"), tranText);
                    } 
                }
            } catch (Exception e) {
                LogUtils.writeWarnExceptionLog(logger, e);
            }
        }else{
            String[] tIndexs = pIndex.split(PATHLINKSIGN);
            LogUtils.writeDebugLog(logger, "The TXBox is Multilayer nested  is table");
            XmlObject xobj = textBoxObjects[index];
            XmlCursor xmlCursor = xobj.newCursor();
            int tIndex = Integer.parseInt(tIndexs[0]);
            int rowNum = Integer.parseInt(tIndexs[1]);
            int columnNum = Integer.parseInt(tIndexs[2]);
            int num = Integer.parseInt(pIndexs[pIndexs.length-1]);
            List<SentenceElement> sentenceElements = getTranSentenceElement(child, checked);
            if(!isTakeOriginal){
                xmlCursor.toChild(tIndex);
                xmlCursor.push();// 保存当前位置
                xmlCursor.toChild(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main","tr","w"), rowNum);
                xmlCursor.toChild(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main","tc","w"), columnNum);
                xmlCursor.toChild(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main","p","w"), num);
                
                xmlCursor.push();// 保存当前位置
                for (SentenceElement sentenceElement : sentenceElements) {
                    String tranText = getTranText(sentenceElement);
                    xmlCursor.pop();
                    xmlCursor.toLastChild();// w:r
                    boolean updated = false;
                    do {
                        // 更新译文
                        xmlCursor.toLastChild();// w:t
                        QName qname = xmlCursor.getName();
                        String pname = qname.getPrefix();
                        String lname = qname.getLocalPart();
                        if (pname.equalsIgnoreCase("w") && lname.equalsIgnoreCase("t")) {
                            if (!updated) {
                                xmlCursor.setTextValue(tranText);
                                updated = true;
                            } else {
                                xmlCursor.setTextValue("");
                            }
                            logger.info(xmlCursor.getTextValue());
                        }
                    } while (xmlCursor.toPrevSibling());
                }   
            }
        }
    }

    @SuppressWarnings("unused")
    private void updateTextBoxBodyFor2007(XWPFParagraph paragraph, BElement bElement, boolean isTakeOriginal,
            boolean checked) {
        String nameSpace = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/v:textbox/w:txbxContent";

        XmlObject[] textBoxObjects = paragraph.getCTP().selectPath(nameSpace);

        int index = bElement.getIndex();

        try {
            XmlObject[] paraObjects = textBoxObjects[index]
                    .selectChildren(new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));

            List<SentenceElement> sentenceElements= getTranSentenceElement(bElement, checked);
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

    private void updateFootnotes(List<XWPFFootnote> xfooters, List<BElement> footers, boolean isTakeOriginal,
            boolean checked) {
        if (footers != null && !footers.isEmpty()) {
            for (BElement be : footers) {
                IBodyElement ibody =  getIBodyElementOfHFF(be,xfooters);
                updateIBody(ibody, be, isTakeOriginal, checked);
            }
        }
    }

    private void updateFooters(List<XWPFFooter> xfooters, List<BElement> footers, boolean isTakeOriginal,
            boolean checked) {
        if (footers != null && !footers.isEmpty()) {
            for (BElement be : footers) {
                IBodyElement ibody =  getIBodyElementOfHFF(be,xfooters);
                updateIBody(ibody, be, isTakeOriginal, checked);
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

    private List<BElement> parseIBodyElement(String pIndex ,int index, IBodyElement body, XWPFDocument document, String directory,
            boolean wordSplit) {
        List<BElement> bodyElements = new ArrayList<>();
        String beType = body.getElementType().name();

        if (StringUtils.equals(BodyElementType.PARAGRAPH.name(), beType)) {
            BElement bodyElement = parseParagraph((XWPFParagraph) body, document, directory, wordSplit);
            if (bodyElement != null) {
                LogUtils.writeDebugLog(logger, "path : " + createPathOfParagraph(pIndex, index));
                
                bodyElement.setIndex(index);
                bodyElement.setPath(createPathOfParagraph(pIndex, index));
                bodyElement.setName(BodyElementType.PARAGRAPH.name());
                bodyElement.setpName(BodyElementType.PARAGRAPH.name());
                bodyElement.setRowNum(0);
                bodyElement.setColumnNum(0);
                bodyElements.add(bodyElement);
            }
//          parse txtBox
            List<BElement> childs = parseTXTBox(pIndex,index,(XWPFParagraph) body,wordSplit);
            for(BElement child:childs){
                //child.setpName(BodyElementType.TXTBOX.name())
                child.setpName(BodyElementType.PARAGRAPH.name());
                child.setTxbox(true);
                child.setIndex(index);
            }
            bodyElements.addAll(childs);

        } else if (StringUtils.equals(BodyElementType.TABLE.name(), beType)) {
            List<BElement> bElementsOfTables = parseTable(pIndex,index, (XWPFTable) body, document, directory, wordSplit);
            for(BElement child:bElementsOfTables){
                child.setpName(BodyElementType.TABLE.name());
                child.setIndex(index);
            }
            bodyElements.addAll(bElementsOfTables);
            
        } else if (StringUtils.equals(BodyElementType.CONTENTCONTROL.name(), beType)) {
            XWPFSDT sdt = (XWPFSDT) body;
            logger.info(sdt.getContent().getText());
        }
        return bodyElements;
    }
    
    private List<BElement> parseTable(String pIndex,int index,XWPFTable xtable,XWPFDocument document, String directory,boolean wordSplit ){
        List<XWPFTableRow> rows = xtable.getRows();
        int rowNum = 0;
        List<BElement> bodyElements = new ArrayList<>();
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            int columnNum = 0;
            for (XWPFTableCell cell : cells) {
                List<IBodyElement> bodysOfCell = cell.getBodyElements();
                List<BElement> childs = parseIBodyElements(createPathOfTable(pIndex, index, rowNum, columnNum),bodysOfCell, document, directory, wordSplit);
                bodyElements.addAll(childs);
                columnNum += 1;
            }
            rowNum += 1;
        }
        return bodyElements;
    }

    private List<BElement> parseIBodyElements(String pIndex ,List<IBodyElement> bodys, XWPFDocument document, String directory,
            boolean isWord) {
        int index = 0;
        List<BElement> bodyElements = new ArrayList<>();
        for (IBodyElement body : bodys) {
            List<BElement> bElements = parseIBodyElement(pIndex,index,body, document, directory, isWord);
            bodyElements.addAll(bElements);
            index += 1;
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
            List<BElement> bElements = parseIBodyElement("",bodySerial, body, document, directory, wordSplit);
            if (length == 0) {
                bodyElements4Part = new ArrayList<>();
            }

            bodyElements4Part.addAll(bElements);
            length += countWordNumberOfBElements(bElements, wordSplit);

            if (length >= pLength && partSerial<pNumber) {
                PElement part = newPElement(bodyElements4Part);
                part.setPartId(partSerial++);
                part.setWordNumber(countWordNumberOfBElements(bodyElements4Part, wordSplit));
                parts.add(part);

                length = 0;
            }
            bodySerial += 1;
        }

        if (length > 0 && bodyElements4Part != null) {
            PElement part = newPElement(bodyElements4Part);
            part.setPartId(partSerial);
            part.setWordNumber(countWordNumberOfBElements(bodyElements4Part, wordSplit));
            parts.add(part);
        }
        return parts;
    }
    
    private List<BElement> parseBodyOfDocument(List<IBodyElement> bodys, XWPFDocument document, String directory,
            boolean wordSplit) {
        return parseIBodyElements("",bodys, document, directory, wordSplit);
    }
    
    private int countWordNumberOfBElements(List<BElement> bElements, boolean wordSplit) {
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
    
    private int countWordNumberOfSentenceElements(List<SentenceElement> sentenceElement, boolean wordSplit) {
        int wordNumber = 0;
        int charNumber = 0;
        for (SentenceElement bElement : sentenceElement) {
            if(bElement.getContentType().equals(ContentType.TEXT)){
                wordNumber += wordNumberDeleteSpace(bElement.getText(), wordSplit);
                charNumber += charNumberDeleteSpace(bElement.getText());
            }
        }
        if (wordSplit) {
            return wordNumber;
        } else {
            return charNumber;
        }
    }
    
    private String  convertTXTBoxPToPar(String xmlText){
        return xmlText.replace("<w:p ","<xml-fragment ").replace("w:p>", "xml-fragment>");
    }
    
    private String  convertTXTBoxTb1ToTable(String xmlText){
        return xmlText.replace("<w:tbl ","<xml-fragment ").replace("w:tbl>", "xml-fragment>");
    }
    
    private String createPathOfParagraph(String pIndex,int index){
        if(StringUtils.isBlank(pIndex)){
            return String.valueOf(index);
        }
        return pIndex+PATHSPLITSIGN+index;
    }
    
    private String createPathOfTXTBox(String pIndex,int index,int tIndex){
        if(StringUtils.isBlank(pIndex)){
            return index+PATHLINKSIGN+tIndex;
        }
        return pIndex+PATHSPLITSIGN+index+PATHLINKSIGN+tIndex;
    }
    
    private String createPathOfTable(String pIndex,int index,int rowNum,int columnNum){
        if(StringUtils.isBlank(pIndex)){
            return index+PATHLINKSIGN+rowNum+PATHLINKSIGN+columnNum;
        }
        return pIndex+PATHSPLITSIGN+index+PATHLINKSIGN+rowNum+PATHLINKSIGN+columnNum;
    }
    
    private void updateIBodyWithBElement(BElement bElement,IBodyElement ibody,boolean isTakeOriginal,boolean checked){
        if(bElement.isTxbox()){
            updateTXBox((XWPFParagraph)ibody,bElement,isTakeOriginal, checked);
        }else{
            updateIBody(ibody, bElement, isTakeOriginal, checked);
        }
    }
    
}
