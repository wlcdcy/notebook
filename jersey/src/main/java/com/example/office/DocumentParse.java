package com.example.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.LogUtils;

public abstract class DocumentParse {
    private static Logger log = LoggerFactory.getLogger(DocumentParse.class);

    static int defaultSplitLength = 2;
    static String[] splitChars = { "?", "!", ". ", "？", "！", "。" };
    static String CHAR_SPACE = " ";
    static String CHAR_ALL_SPACE = "　";
    static String CHAR_TAB_SPACE = "	";
    static String CHAR_SOFTENTER = "";

    /**
     * 获取文件包含字符数
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public Integer charNumber(String filePath) {
        try (InputStream ins = new FileInputStream(filePath);) {
            return charNumber(ins);
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(log,e);
        }
        return null;
    }

    public abstract Integer charNumber(InputStream ins);

    /**
     * 统计文档字数
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public Integer wordNumber(String filePath) {
        try (InputStream ins = new FileInputStream(filePath);) {
            return wordNumber(ins);
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(log, e);
        }
        return null;
    }

    public abstract Integer wordNumber(InputStream ins);

    /**
     * 统计文档字数
     * 
     * @param filePath
     * @param isWordModel
     * @return
     * @throws Exception
     */
    public Integer wordNumber(String filePath, boolean isWordModel) {
        try (InputStream ins = new FileInputStream(filePath);) {
            return wordNumber(ins, isWordModel);
        } catch (IOException e) {
            LogUtils.writeWarnExceptionLog(log,e);
        }
        return null;
    }

    public Integer wordNumber(InputStream ins, boolean isWordModel) {
        if(isWordModel){
            return wordNumber(ins);
        }else{
            return charNumber(ins);
        }
    }

    /**
     * 按预定的分隔符拆分
     * 
     * @param text
     * @return
     */
    private String[] splitContent(String text) {
        String lineSep = System.getProperty("line.separator");
        for (String splitchar : splitChars) {
            if (text.contains(splitchar)) {
                text = text.replace(splitchar, splitchar + lineSep);
            }
        }
        return text.split(lineSep);
    }

    /**
     * 删除换行符 ，按预定的分隔符拆分
     * 
     * @param text
     * @return
     */
    public String[] splitContentFirstDeleteBR(String text) {
        String[] contents = text.split("\\n");
        String[] result = null;
        for (String content : contents) {
            result = (String[]) ArrayUtils.addAll(result, splitContent(content));
        }
        return result;
    }
    
    /**
     * 删除空格
     * 
     * @param text
     * @return
     */
    public String deleteSpace(String text) {
        return text.replaceAll(CHAR_SPACE, "").replaceAll(CHAR_ALL_SPACE, "").replaceAll(CHAR_TAB_SPACE, "")
                .replaceAll(CHAR_SOFTENTER, "");
    }

    /**
     * 获取移除空格后字符串的长度
     * 
     * @param text
     * @return
     */
    public int charNumberDeleteSpace(String text) {
        return deleteSpace(text).length();
    }
    
    
    
    public int wordNumberDeleteSpace(String text,boolean wordSplit) {
        if(wordSplit){
            return wordNumberDeleteSpace(text);
        }else{
            return charNumberDeleteSpace(text);
        }
    }
    
    private int wordNumberDeleteSpace(String text) {
        return wordDeleteSpaces(text).length;
    }
    
    /**
     * 分词
     * 
     * @param text
     * @return
     */
    private String[] wordDeleteSpaces(String text) {
        String textOfWord = text.replaceAll(CHAR_ALL_SPACE, CHAR_SPACE).replaceAll(CHAR_TAB_SPACE, CHAR_SPACE)
                .replaceAll(CHAR_SOFTENTER, CHAR_SPACE);
        return textOfWord.split("\\s+");
    }

    public abstract DElement documentParse(String file, boolean wordSplit, int pLength, int pNumber);
    public abstract DElement documentParse(String file, boolean wordSplit);

    /**
     * 生成原文子文档 /**
     * 
     * @param file
     * @param partEnty
     * @return
     */
    public String createSubDocument(PElement pElement,String filePath){
        File file = new File(filePath);
        return createSubDocument(pElement,file);
    }
    public abstract String createSubDocument(PElement pElement,File file);

    /**
     * 生成子文档的译文
     * 
     * @param part
     * @param tempTemple
     */
    public abstract String createSubTranlatedDocument(PElement pElement, String filePath, boolean checked);

    /**
     * 生成原译文对照文档
     * 
     * @param partEntitys
     * @param tempTemple
     */
    public abstract String createTranlatedDocument(DElement dElement, String filePath, boolean isTakeOriginal,boolean checked);

    public File createSubFile(File file, int partNo) {
        return new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_" + partNo + "."));
    }

    public static File createTranlatedFile(File file, boolean isTakeOriginal) {
        if (!isTakeOriginal) {
            return new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_t."));
        } else {
            return new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_yt."));
        } 
    }

    public File createSubTranlatedFile(File file, int partNo) {
        return new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_" + partNo + "_t."));
    }

    public boolean breakSentence(String text) {
        for (String splitchar : splitChars) {
            if (text.lastIndexOf(splitchar) == 0) {
                return true;
            }
        }
        return false;
    }

    public int countWordNumber(String input) {
        int length = 0;
        if (input != null && input.length() > 0) {
            String regEx = "[-a-zA-Z0-9——.%@_-—…]+";
            Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(input);
            length = input.length();
            while (matcher.find()) {
                length = length - matcher.group().length() + 1;
            }
            // 处理空格
            String regSpaceEx = "\\s+";
            pattern = Pattern.compile(regSpaceEx, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(input);
            int spaceCounts = 0;
            while (matcher.find()) {
                spaceCounts += matcher.group().length();
            }
            if (length <= spaceCounts) {
                length = 0;
            } else {
                length = length - spaceCounts;
            }
        }
        return length;
    }
    
    public PElement newPElement(List<BElement> bElements) {
        PElement part = new PElement();
        part.setBodyElements(bElements);
        part.setBeginBodyId(bElements.get(0).getIndex());
        part.setEndBodyId(bElements.get(bElements.size() - 1).getIndex()+1);
        int wordNumber = 0;
        int charNumber = 0;
        for(BElement bElement:bElements){
            wordNumber += bElement.getWordNumber();
            charNumber += bElement.getCharNumber();
        }
         part.setCharNumber(charNumber);
         part.setWordNumber(wordNumber);
        return part;
    }
    
    public List<SentenceElement> getTranSentenceElement(BElement bElement,boolean checked){
        List<SentenceElement> tranSentences = bElement.getSentences();
        if (checked) {
            tranSentences = bElement.getCheckSentences() != null ? bElement.getCheckSentences() : tranSentences;
        } else {
            tranSentences = bElement.getTranSentences() != null ? bElement.getTranSentences() : tranSentences;
        }
        return tranSentences;
    }
    
    public String getTranText(SentenceElement sentence){
        return "hello" + sentence.getText();
        //return sentence.getText();
    }

}
