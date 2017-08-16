package com.example.office;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.LogUtils;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class TEXTDocumentParse extends DocumentParse {
    private static Logger logger = LoggerFactory.getLogger(TEXTDocumentParse.class);

    public static void main(String[] args) throws FileNotFoundException {
        DocumentParse documentParse = new TEXTDocumentParse();
        int charLength = documentParse.charNumber("D:/Users/Administrator/Desktop/notes.txt");
        logger.info("charLength: " + charLength);
        String context = "大框架爱爱啊";
        try (InputStream ins = new ByteArrayInputStream(context.getBytes());) {
            charLength = documentParse.charNumber(ins);
            logger.info("charLength: " + charLength);
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        }
    }

    /*
     * 缺省字符集UTF-8；
     */
    @Override
    public Integer charNumber(InputStream ins) {
        BufferedInputStream bins = new BufferedInputStream(ins);
        return charNumber(bins, "UTF-8");
    }

    public Integer charNumber(InputStream ins, String encoding) {
        BufferedReader reader = null;
        int charNumber = 0;

        try (BufferedInputStream bins = new BufferedInputStream(ins);
                InputStreamReader isReader = new InputStreamReader(bins, Charset.forName(encoding));) {

            reader = new BufferedReader(isReader);
            while (true) {
                String lineContent = reader.readLine();
                logger.info(lineContent);
                if (lineContent == null) {
                    break;
                } else {
                    charNumber += charNumberDeleteSpace(lineContent);
                }
            }
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }
        return charNumber > 0 ? charNumber : null;
    }

    @Override
    public Integer charNumber(String filePath) {
        File file = new File(filePath);
        String encoding = "UTF-8";
        try (InputStream fins = new BufferedInputStream(new FileInputStream(file));
                InputStream ins = new FileInputStream(file);) {
            encoding = getTextStreamEncode(fins);
            return charNumber(ins, encoding);
        } catch (IOException e) {
            LogUtils.writeDebugExceptionLog(logger, e);
        }
        return null;
    }

    public static String getTextStreamEncode(InputStream ins) {
        try {
            CharsetDetector detector = new CharsetDetector();
            detector.setText(ins);
            CharsetMatch charsetMatch = detector.detect();
            if (charsetMatch != null)
                return charsetMatch.getName();
        } catch (Exception e) {
            LogUtils.writeDebugExceptionLog(logger, e);
        }
        return null;
    }

    public static String getTextStreamEncode(File file) {
        try (InputStream ins = new BufferedInputStream(new FileInputStream(file));){
            return getTextStreamEncode(ins);
        } catch (Exception e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        }
        return null;
    }
    
    private String getTranText(BElement bElement,boolean checked){
        List<SentenceElement> tranSentences = getTranSentenceElement(bElement, checked);
        return getTranText(tranSentences);
    }
    
    private String getTranText(List<SentenceElement> tranSentences){
        String text="";
        for (SentenceElement tranSentence:tranSentences) {
            text +=getTranText(tranSentence);
        }
        return text;
    }
    
    private String getText(List<SentenceElement> tranSentences){
        String text="";
        for (SentenceElement tranSentence:tranSentences) {
            text +=tranSentence.getText();
        }
        return text;
    }

    @Override
    public String createSubDocument(PElement pElement,File file) {
        File subFile = createSubFile(file, pElement.getPartId());
        
        List<BElement> bElements = pElement.getBodyElements();
        BufferedWriter bwriter = null;
        try (Writer writer = new FileWriter(subFile);){
            bwriter = new BufferedWriter(writer);
            for (BElement bElement : bElements) {
                String text = getText(bElement.getSentences());
                bwriter.write(text);
            }
            return subFile.getPath();
        } catch (IOException e) {
                LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (bwriter != null) {
                try {
                    bwriter.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }
        return null;
    }

    @Override
    public String createSubTranlatedDocument(PElement pElement,String filePath, boolean checked) {
        // 文件不存在时会自动创建
        File file = new File(filePath);
        File subTranlatedFile = createSubTranlatedFile(file, pElement.getPartId());
        
        List<BElement> bElements = pElement.getBodyElements();
        BufferedWriter bwriter = null;
        try (Writer writer = new FileWriter(subTranlatedFile);){
            bwriter = new BufferedWriter(writer);
            for (BElement bElement : bElements) {
                String text = getTranText(bElement, checked);
                bwriter.write(text);
            }
            return subTranlatedFile.getPath();
        } catch (IOException e) {
                LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (bwriter != null) {
                try {
                    bwriter.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }
        return null;
        
    }

    @Override
    public String createTranlatedDocument(DElement dElement, String filePath, boolean isTakeOriginal, boolean checked) {
        File tranlatedFile = createTranlatedFile(new File(filePath), isTakeOriginal);
        Writer writer = null;
        BufferedWriter bwriter = null;
        try {
            writer = new FileWriter(tranlatedFile);
            bwriter = new BufferedWriter(writer);
            if (!isTakeOriginal) {
                for (PElement partEntity : dElement.getParts()) {
                    List<BElement> paragraphs = partEntity.getBodyElements();
                    for (BElement paragraph : paragraphs) {
                        List<SentenceElement> tranSentences = getTranSentenceElement(paragraph, checked);
                        String tranText = "";
                        for (SentenceElement tranSentence : tranSentences) {
                            tranText += getTranText(tranSentence);
                        }
                        bwriter.write(tranText);
                    }
                }
                return tranlatedFile.getPath();
            } else if (isTakeOriginal) {
                for (PElement partEntity : dElement.getParts()) {
                    List<BElement> paragraphs = partEntity.getBodyElements();
                    for (BElement paragraph : paragraphs) {
                        List<SentenceElement> sentences = paragraph.getSentences();
                        List<SentenceElement> tranSentences = getTranSentenceElement(paragraph, checked);
                        String text = "";
                        String tranText = "";
                        for(int i=0;i<sentences.size();i++){
                            text += sentences.get(i).getText();
                            tranText += getTranText(tranSentences.get(i));
                        }
                        
                        bwriter.write(text);
                        bwriter.newLine();
                        bwriter.write(tranText);
                        bwriter.newLine();
                    }
                }
            } else {
                for (PElement partEntity : dElement.getParts()) {
                    List<BElement> paragraphs = partEntity.getBodyElements();
                    for (BElement paragraph : paragraphs) {
                        
                        List<SentenceElement> sentences = paragraph.getSentences();
                        List<SentenceElement> tranSentences = getTranSentenceElement(paragraph, checked);
                        String text = "";
                        String tranText = "";
                        for(int i=0;i<sentences.size();i++){
                            text += sentences.get(i).getText();
                            tranText += getTranText(tranSentences.get(i));
                        }
                        bwriter.write(tranText);
                        bwriter.newLine();
                        bwriter.write(text);
                        bwriter.newLine();
                    }
                }
            }
            return tranlatedFile.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bwriter != null) {
                try {
                    bwriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public File createSubTranlatedFile(File file, int partNo) {
        return new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_t."));
    }

    @Override
    public Integer wordNumber(InputStream ins) {
        return null;
    }

    @Override
    public DElement documentParse(String filePath, boolean wordSplit, int pLength, int pNumber) {
        
        List<PElement> pElments = parseIbody(filePath,wordSplit,pLength,pNumber);
        
        DElement dElment= new DElement();
        dElment.setParts(pElments);
        
//      创建原文的切片文件
        for (PElement pElment : pElments) {
            String subfilePath = createSubDocument(pElment,filePath);
            pElment.setPartPath(subfilePath);
        }
        return dElment;
    }
    
    private List<PElement> parseIbody(String filePath, boolean wordSplit, int pLength, int pNumber){
        List<PElement> parts = new ArrayList<>();
        
        File file = new File(filePath);
        String encoding = getTextStreamEncode(file);
        BufferedReader reader = null;
        try (InputStream ins = new FileInputStream(file);BufferedInputStream bins = new BufferedInputStream(ins);){
            reader = new BufferedReader(new InputStreamReader(bins, Charset.forName(encoding)));
            // 拆分文档字符数
            int length = 0;
            // 拆分文档序号
            int pIndex = 0;
            
            int lineNum=0;
            
            List<BElement> bodyElements4Part =null;
            
            String ltext = reader.readLine();
            while (ltext != null) {
                
                if(StringUtils.isBlank(ltext)){
                    continue;
                }
                LogUtils.writeDebugLog(logger, ltext);
                
                if (length == 0) {
                    bodyElements4Part = new ArrayList<>();
                }
                BElement bElement = new BElement();
                bElement.setIndex(lineNum);
                bodyElements4Part.add(bElement);
                
                int charNumber = charNumberDeleteSpace(ltext);
                int wordNumber = wordNumberDeleteSpace(ltext, wordSplit);
                
                bElement.setCharNumber(charNumber);
                bElement.setWordNumber(wordNumber);
                List<SentenceElement> sentences = newSentenceElements(ltext);
                bElement.setSentences(sentences);
                length += wordNumber;
                
                if (length >= pLength && pIndex<=pNumber) {
                    PElement part = newPElement(bodyElements4Part);
                    part.setPartId(pIndex++);
                    parts.add(part);
                    
                    length = 0;
                }
                
                lineNum++;
                ltext = reader.readLine();
            }
            
            if (length > 0 && bodyElements4Part != null) {
                PElement part = newPElement(bodyElements4Part);
                part.setPartId(pIndex);
                parts.add(part);
            }
            return parts;
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(logger, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogUtils.writeDebugExceptionLog(logger, e);
                }
            }
        }
        return parts;
    }
    
    private List<SentenceElement> newSentenceElements(String text){
        
        List<SentenceElement> sentences = new ArrayList<>(); 
        String[] textChilds =splitContentFirstDeleteBR(text);
        int index=0;
        for(String child:textChilds){
            SentenceElement se = new SentenceElement();

            se.setContentType(ContentType.TEXT);
            se.setSentenceSerial(++index);
//            List<ContentElement> contents = new ArrayList<>()
//            se.setContents(contents)
            se.setText(child);
            // 分割片段内容为句
            String[] childTexts = new String[1];
            childTexts[0] = child;
            se.setChildTexts(childTexts);
            
            sentences.add(se);
        }
        return sentences;
    }

}
