package com.example.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.document.entity.FirstElement;
import com.example.document.entity.TopElement;

public abstract class DocumentParse {
    private static Logger logger = LoggerFactory.getLogger(DocumentParse.class);

    static int defaultSplitLength = 2;
    static String[] splitChars = { "?", "!", ". ", "？", "！", "。" };
    static String CHAR_SPACE = " ";
    static String CHAR_ALL_SPACE = "　";
    static String CHAR_TAB_SPACE = "	";
    static String CHAR_SOFTENTER = "";

    /**
     * 统计文档字符数
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public Integer numberOfCharacters(String filePath) throws Exception {
        InputStream ins = null;
        try {
            ins = new FileInputStream(filePath);
            return numberOfCharacters(ins);
        } catch (FileNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage(), e);
                    }
                }
            }
        }
        return null;
    }

    public abstract Integer numberOfCharacters(InputStream ins) throws Exception;

    /**
     * 统计文档字数
     * 
     * @param filePath
     * @param isWordModel
     * @return
     * @throws Exception
     */
    public Integer numberOfWords(String filePath, boolean isWordModel) throws Exception {
        InputStream ins = null;
        try {
            ins = new FileInputStream(filePath);
            return numberOfWords(ins, isWordModel);
        } catch (FileNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage(), e);
                    }
                }
            }
        }
        return null;
    }

    public abstract Integer numberOfWords(InputStream ins, boolean isWordModel) throws Exception;

    /**
     * 源文档按照给的子文档长度和子文档总数拆分，并拆分成句段。
     * 
     * @param filePath
     * @param subLength
     * @param subSum
     * @return
     */
    public abstract TopElement documentParse(String filePath, int subLength, int subSum);

    /**
     * 源文档正文按照指定的子文档长度和子文档数拆分，并拆分成句段。
     * 
     * @param filePath
     *            原文档路径
     * @param subLength
     *            子文档长度
     * @param subSum
     *            子文档数量
     * @return
     */
    public abstract List<FirstElement> documentBodyParse(String filePath, int subLength, int subSum);

    /**
     * 源文档拆分成句段
     * 
     * @param filePath
     * @return
     */
    public abstract FirstElement documentBodyParse(String filePath);

    /**
     * 按原文档的样式分割成子文档
     * 
     * @param file
     *            源文档
     * @param partElement
     * @return
     */
    public abstract String createSubDocument(File file, List<FirstElement> partElement);

    /**
     * 生成子文档的译文
     * 
     * @param part
     * @param tempTemple
     */
    public abstract String createSubTranlatedDocument(List<FirstElement> partElement, String filePath, boolean checked);

    /**
     * 生成原文档的译文对照文档
     * 
     * @param partEntitys
     * @param tempTemple
     */
    public abstract String createTranlatedDocument(TopElement topElement, String filePath, boolean is);

    public File createSubFile(File file, int partNo) {
        File subFile = new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_" + partNo + "."));
        return subFile;
    }

    public static File createTranlatedFile(File file, Boolean afterTranlated) {
        File subFile = null;
        if (afterTranlated == null) {
            subFile = new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_t."));
            return subFile;
        } else if (afterTranlated) {
            subFile = new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_yt."));
            return subFile;
        } else {
            subFile = new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_ty."));
            return subFile;
        }
    }

    public File createSubTranlatedFile(File file, int partNo) {
        File subTranlatedFile = new File(file.getParent(),
                StringUtils.join(file.getName().split("\\."), "_" + partNo + "_t."));
        return subTranlatedFile;
    }

    /**
     * 按照预定的分隔符拆分
     * 
     * @param text
     * @return
     */
    public String[] splitContent(String text) {
        String lineSep = System.getProperty("line.separator");
        for (String splitchar : splitChars) {
            if (text.contains(splitchar)) {
                text = text.replace(splitchar, splitchar + lineSep);
            }
        }
        return text.split(lineSep);
    }

    public boolean breakSentence(String text) {
        for (String splitchar : splitChars) {
            if (text.lastIndexOf(splitchar) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按照预定的分隔符拆分
     * 
     * @param text
     * @return
     */
    public String[] removeNewlineAndSplitContent(String text) {
        String[] contents = text.split("\\n");
        String[] result = null;
        for (String content : contents) {
            result = (String[]) ArrayUtils.addAll(result, splitContent(content));
        }
        return result;
    }

    /**
     * 获取移除空格后字符串的长度
     * 
     * @param text
     * @return
     */
    public int getLengthRemoveSpace(String text) {
        return text.replaceAll(CHAR_SPACE, "").replaceAll(CHAR_ALL_SPACE, "").replaceAll(CHAR_TAB_SPACE, "")
                .replaceAll(CHAR_SOFTENTER, "").length();
    }

    /**
     * 移除空格
     * 
     * @param text
     * @return
     */
    public String removeSpace(String text) {
        return text.replaceAll(CHAR_SPACE, "").replaceAll(CHAR_ALL_SPACE, "").replaceAll(CHAR_TAB_SPACE, "")
                .replaceAll(CHAR_SOFTENTER, "");
    }

    /**
     * 分词
     * 
     * @param text
     * @return
     */
    public String[] getWords(String text) {
        String _text = text.replaceAll(CHAR_ALL_SPACE, CHAR_SPACE).replaceAll(CHAR_TAB_SPACE, CHAR_SPACE)
                .replaceAll(CHAR_SOFTENTER, CHAR_SPACE);
        return _text.split("\\s+");
    }
}
