package com.example.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFFootnote;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.example.document.entity.FirstElement;
import com.example.document.entity.FiveElement;
import com.example.document.entity.FourElement;
import com.example.document.entity.SecondElement;
import com.example.document.entity.ThreeElement;
import com.example.document.entity.TopElement;

public class XWPFWordParse extends DocumentParse {
    private static Logger logger = LoggerFactory.getLogger(XWPFWordParse.class);

    public static void main(String[] args) {
        XWPFWordParse wordParse = new XWPFWordParse();
        String filePath = "d:/hello/wordParse.docx";
        int subLength = 5;
        int subSum = 2;
        wordParse.documentParse(filePath, subLength, subSum);
    }

    @Override
    public Integer numberOfCharacters(InputStream ins) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer numberOfWords(InputStream ins, boolean isWordModel) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TopElement documentParse(String filePath, int subLength, int subSum) {
        File file = new File(filePath);
        FileInputStream fins = null;
        // FileChannel channel = null;
        XWPFDocument xdocument = null;
        try {
            fins = new FileInputStream(file);
            // channel = fins.getChannel();
            xdocument = new XWPFDocument(fins);
            TopElement topElement = new TopElement();
            FirstElement headerElement = parseHeader(xdocument, file.getParent());
            topElement.setHeaderElement(headerElement);
            FirstElement footerElement = parseFooter(xdocument, file.getParent());
            topElement.setHeaderElement(footerElement);
            FirstElement footnoteElement = parseFootnote(xdocument, file.getParent());
            topElement.setHeaderElement(footnoteElement);

            return topElement;
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
        } finally {
            if (fins != null) {
                try {
                    fins.close();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage(), e);
                    }
                }
            }
            if (xdocument != null) {
                try {
                    xdocument.close();
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
    public List<FirstElement> documentBodyParse(String filePath, int subLength, int subSum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FirstElement documentBodyParse(String filePath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String createSubDocument(File file, List<FirstElement> partElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String createSubTranlatedDocument(List<FirstElement> partElement, String filePath, boolean checked) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String createTranlatedDocument(TopElement topElement, String filePath, boolean is) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 解析页眉
     * 
     * @param xdocument
     * @param storage
     * @return
     */
    private FirstElement parseHeader(XWPFDocument xdocument, String storage) {
        List<XWPFHeader> xheaders = xdocument.getHeaderList();

        FirstElement firstElement = new FirstElement();
        firstElement.setElementType("HEADER");

        List<SecondElement> secondElements = new ArrayList<SecondElement>();
        firstElement.setSecondElements(secondElements);

        int index_h = 0;
        int columnNum = 1;
        for (XWPFHeader xheader : xheaders) {
            List<XWPFParagraph> xparagraphs = xheader.getListParagraph();

            List<ThreeElement> threeElements = parseParagraphs(xdocument, xparagraphs, storage);
            if (threeElements != null && threeElements.size() > 0) {
                SecondElement secondElement = new SecondElement();
                secondElement.setElementType("Paragraph");
                secondElement.setColumnNum(columnNum);
                secondElement.setElementIndex(index_h);
                secondElement.setThreeElements(threeElements);

                secondElements.add(secondElement);
            }
            columnNum++;
            index_h++;
        }
        return firstElement;
    }

    /**
     * 解析页脚
     * 
     * @param xdocument
     * @param storage
     * @return
     */
    private FirstElement parseFooter(XWPFDocument xdocument, String storage) {
        List<XWPFFooter> xfooters = xdocument.getFooterList();

        FirstElement firstElement = new FirstElement();
        firstElement.setElementType("FOOTER");

        List<SecondElement> secondElements = new ArrayList<SecondElement>();
        firstElement.setSecondElements(secondElements);

        int index_h = 0;
        int columnNum = 1;
        for (XWPFFooter xfooter : xfooters) {
            List<XWPFParagraph> xparagraphs = xfooter.getParagraphs();

            List<ThreeElement> threeElements = parseParagraphs(xdocument, xparagraphs, storage);
            if (threeElements != null && threeElements.size() > 0) {
                SecondElement secondElement = new SecondElement();
                secondElement.setElementType("Paragraph");
                secondElement.setColumnNum(columnNum);
                secondElement.setElementIndex(index_h);
                secondElement.setThreeElements(threeElements);

                secondElements.add(secondElement);
            }
            columnNum++;
            index_h++;
        }
        return firstElement;
    }

    /**
     * 解析脚注
     * 
     * @param xdocument
     * @param storage
     * @return
     */
    private FirstElement parseFootnote(XWPFDocument xdocument, String storage) {
        List<XWPFFootnote> xfootnotes = xdocument.getFootnotes();

        FirstElement firstElement = new FirstElement();
        firstElement.setElementType("FOOTNOTE");

        List<SecondElement> secondElements = new ArrayList<SecondElement>();
        firstElement.setSecondElements(secondElements);

        int index_h = 0;
        int columnNum = 1;
        for (XWPFFootnote xfootnote : xfootnotes) {
            List<XWPFParagraph> xparagraphs = xfootnote.getParagraphs();

            List<ThreeElement> threeElements = parseParagraphs(xdocument, xparagraphs, storage);
            if (threeElements != null && threeElements.size() > 0) {
                SecondElement secondElement = new SecondElement();
                secondElement.setElementType("Paragraph");
                secondElement.setColumnNum(columnNum);
                secondElement.setElementIndex(index_h);
                secondElement.setThreeElements(threeElements);

                secondElements.add(secondElement);
            }
            columnNum++;
            index_h++;
        }
        return firstElement;
    }

    /**
     * 解析尾注
     * 
     * @param xdocument
     * @param storage
     * @return
     */
    private SecondElement parseEndnote(XWPFDocument xdocument, int id, String storage) {
        SecondElement secondElement = null;
        XWPFFootnote endnote = xdocument.getEndnoteByID(id);
        List<XWPFParagraph> xparagraphs = endnote.getParagraphs();
        List<ThreeElement> threeElements = parseParagraphs(xdocument, xparagraphs, storage);
        if (threeElements != null && threeElements.size() > 0) {
            secondElement = new SecondElement();
            secondElement.setElementType("Paragraph");
            secondElement.setThreeElements(threeElements);
            // secondElements.add(secondElement);
        }
        return secondElement;
    }

    /**
     * 段落解析，返回ThreeElement对象
     * 
     * @param xdocument
     * @param xparagraph
     * @param storage
     * @return
     */
    private ThreeElement parseParagraph(XWPFDocument xdocument, XWPFParagraph xparagraph, String storage) {
        // 所有图片
        // xdocument.getAllPackagePictures();

        ThreeElement threeElement = null;
        List<XWPFRun> xruns = xparagraph.getRuns();
        if (xruns != null && !xruns.isEmpty()) {
            threeElement = new ThreeElement();
            int index_r = 0;

            boolean isbreak = false;

            List<FourElement> fourElements = new ArrayList<FourElement>();
            threeElement.setFourElements(fourElements);

            FourElement fourElement = new FourElement();
            List<FiveElement> fiveElements = new ArrayList<FiveElement>();
            fourElement.setFiveElements(fiveElements);
            fourElements.add(fourElement);

            for (XWPFRun xrun : xruns) {
                // 过滤脚注|尾注
                boolean isFootnote = checkFootnote(xrun);
                if (isFootnote) {
                    if (fourElement.getElementType() != null) {
                        isbreak = true;
                    }
                    continue;
                }
                // runNum++;

                String text = xrun.text().trim();

                ///////
                if (StringUtils.isNotBlank(text)) {
                    FiveElement fiveElement = parseTextFromXWPFRun(xrun);
                    if (fiveElement != null) {
                        if (isbreak && StringUtils.isNotEmpty(fourElement.getElementType())) {
                            fourElement = new FourElement();
                            fourElement.setIndex(index_r);
                            fiveElements = new ArrayList<FiveElement>();
                            fourElement.setFiveElements(fiveElements);
                            fourElements.add(fourElement);
                        }

                        fiveElements.add(fiveElement);
                        fourElement.setContent(fourElement.getContent() + fiveElement.getContent());
                        fourElement.setElementType("TEXT");
                        isbreak = fiveElement.isIsbreak();
                    }
                } else {
                    List<FiveElement> fiveElements_ = parseImageFromXWPFRun(xrun, index_r, storage);
                    if (fiveElements_ != null && !fiveElements_.isEmpty()) {
                        for (FiveElement fiveElement_ : fiveElements_) {
                            if (isbreak && StringUtils.isNotEmpty(fourElement.getElementType())) {
                                fourElement = new FourElement();
                                fourElement.setIndex(index_r);
                                fiveElements = new ArrayList<FiveElement>();
                                fourElement.setFiveElements(fiveElements);
                                fourElements.add(fourElement);
                            }
                            fiveElements.add(fiveElement_);
                            fourElement.setContent(fiveElement_.getContent());
                            fourElement.setElementType("IMAGE");
                        }
                    } else {
                        isbreak = true;
                    }
                }
                index_r++;
            }
            logger.info(fourElement.getContent());
        }

        // 文本框;

        // 内嵌图片
        return threeElement;
    }

    /**
     * @param xdocument
     * @param xparagraphs
     * @param storage
     * @return
     */
    private List<ThreeElement> parseParagraphs(XWPFDocument xdocument, List<XWPFParagraph> xparagraphs,
            String storage) {
        if (xparagraphs != null && !xparagraphs.isEmpty()) {
            List<ThreeElement> threeElements = new ArrayList<ThreeElement>();
            int index_p = 0;
            for (XWPFParagraph xparagraph : xparagraphs) {
                ThreeElement threeElement = parseParagraph(xdocument, xparagraph, storage);
                if (threeElement != null) {
                    threeElement.setElementIndex(index_p);
                    threeElements.add(threeElement);
                }
                index_p++;
            }
            return threeElements;
        }
        return null;
    }

    /**
     * 是否为脚趾|尾注
     * 
     * @param xrun
     * @return
     */
    private boolean checkFootnote(XWPFRun xrun) {
        CTR ftn = xrun.getCTR();
        XmlObject o = ftn.copy();
        Node node = o.getDomNode().getLastChild();
        String name = node.getLocalName();
        if (StringUtils.equals(name, "footnoteReference") || StringUtils.equals(name, "endnoteReference")) {
            return true;
        }
        return false;
    }

    private FiveElement parseTextFromXWPFRun(XWPFRun xrun) {
        String text = xrun.text().trim();
        // 软换除是否需要断句?(目前没断句)
        if (StringUtils.isNotBlank(text)) {
            FiveElement fiveElement = new FiveElement();
            fiveElement.setContent(text);
            fiveElement.setFontName(xrun.getFontName());
            fiveElement.setFontSize(xrun.getFontSize());
            boolean isbreak = breakSentence(removeSpace(text));
            fiveElement.setIsbreak(isbreak);
            return fiveElement;
        }
        return null;
    }

    private List<FiveElement> parseImageFromXWPFRun(XWPFRun xrun, int index_r, String storage) {
        List<XWPFPicture> xpictures = xrun.getEmbeddedPictures();
        if (xpictures != null && xpictures.size() > 0) {
            List<FiveElement> fiveElements = new ArrayList<FiveElement>();
            // 一张图片一句
            int index_i = 0;// 图片序列
            for (XWPFPicture xpicture : xpictures) {
                XWPFPictureData pictureData = xpicture.getPictureData();
                FileOutputStream fos = null;
                String imagePath = "";
                try {
                    File imageFile = new File(storage,
                            String.format("%d_%d_%s", index_r, index_i++, pictureData.getFileName()));
                    fos = new FileOutputStream(imageFile);
                    fos.write(pictureData.getData());
                    imagePath = imageFile.getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FiveElement fiveElement = new FiveElement();
                fiveElement.setContent(imagePath);
                fiveElement.setIndex(index_i);
                fiveElement.setIsbreak(true);
                fiveElements.add(fiveElement);
            }
            return fiveElements;
        }
        return null;
    }
}
