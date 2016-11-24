package com.example.office;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public abstract class DocumentParse {
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
     */
    public abstract Integer charLength(String filePath);

    public abstract Integer charLength(InputStream ins);

    /**
     * 文件按照给定长度分割成子文件，并拆分成句段。
     * 
     * @param file
     * @param partLength
     * @return
     */
    public abstract List<PartEntity> document2Parts(String filePath, int partLength);

    /**
     * 文件拆分成句段
     * 
     * @param file
     * @return
     */
    public abstract List<ParagraphEntity> document2Paragraphs(File file);

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
     * 生成原文子文档 /**
     * 
     * @param file
     * @param partEnty
     * @return
     */
    public abstract String createSubDocument(File file, PartEntity partEntity);

    /**
     * 生成子文档的译文
     * 
     * @param part
     * @param tempTemple
     */
    public abstract String createSubTranlatedDocument(PartEntity part, String filePath, boolean checked);

    /**
     * 生成原译文对照文档
     * 
     * @param partEntitys
     * @param tempTemple
     */
    public abstract String createTranlatedDocument(List<PartEntity> partEntitys, String filePath,
            Boolean afterTranlated);

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
}
