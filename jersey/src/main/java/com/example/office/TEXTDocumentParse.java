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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class TEXTDocumentParse extends DocumentParse {
    static Logger logger = LoggerFactory.getLogger(TEXTDocumentParse.class);

    public static void main(String[] args) throws FileNotFoundException {
        DocumentParse documentParse = new TEXTDocumentParse();
        int charLength = documentParse.charLength("D:/Users/Administrator/Desktop/notes.txt");
        logger.info("charLength: " + charLength);
        String context = "大框架爱爱啊";
        InputStream ins = null;
        try {
            ins = new ByteArrayInputStream(context.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null)
                try {
                    ins.close();
                } catch (IOException e) {
                }
        }
        charLength = documentParse.charLength(ins);
        logger.info("charLength: " + charLength);
    }

    /*
     * 缺省字符集UTF-8；
     */
    public Integer charLength(InputStream ins) {
        BufferedInputStream bins = new BufferedInputStream(ins);
        return charLength(bins, "UTF-8");
    }

    public Integer charLength(InputStream ins, String encoding) {
        BufferedInputStream bins = new BufferedInputStream(ins);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bins, Charset.forName(encoding)));
        int charLength = 0;
        try {
            while (true) {
                try {
                    String lineContent = reader.readLine();
                    logger.info(lineContent);
                    if (lineContent == null) {
                        break;
                    } else {
                        charLength += getLengthRemoveSpace(lineContent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return charLength > 0 ? charLength : null;
    }

    @Override
    public Integer charLength(String filePath) {
        File file = new File(filePath);
        String encoding = "UTF-8";
        InputStream fins = null;
        try {
            fins = new BufferedInputStream(new FileInputStream(file));
            encoding = getTextStreamEncode(fins);
        } catch (Exception e) {

        } finally {
            if (fins != null) {
                try {
                    fins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        InputStream ins = null;
        try {
            ins = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return charLength(ins, encoding);
    }

    @Override
    public List<PartEntity> document2Parts(String filePath, int partLength) {
        File file = new File(filePath);
        String encoding = getTextStreamEncode(file);
        InputStream ins = null;
        BufferedInputStream bins = null;
        BufferedReader reader = null;
        try {
            ins = new FileInputStream(file);
            bins = new BufferedInputStream(ins);
            reader = new BufferedReader(new InputStreamReader(bins, Charset.forName(encoding)));
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

            while (true) {
                try {
                    String lineText = reader.readLine();
                    logger.info(lineText);
                    if (lineText == null) {
                        break;
                    }
                    if (!"".equals(lineText)) {
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
                        int contentCharLength = getLengthRemoveSpace(lineText);
                        partCharLength += contentCharLength;
                        ParagraphEntity paragraph = new ParagraphEntity();
                        paragraph.setLength(contentCharLength);
                        paragraph.setNo(paragraphNo);
                        String[] texts = splitContent(lineText);
                        paragraph.setSentences(texts);
                        paragraph.setText(lineText);
                        paragraph.setPartNo(partNo);
                        paragraphs.add(paragraph);
                        pe.setCharacters(partCharLength);
                        pe.setFirstNo(firstNo);
                        pe.setLasteNo(lastNo);
                        paragraphNo += 1;
                        lastNo += 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            // 创建原文的切片文件
            for (PartEntity partEnty : parts) {
                String subfilePath = createSubDocument(file, partEnty);
                partEnty.setPartPath(subfilePath);
            }
            return parts;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bins != null) {
                try {
                    bins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public List<ParagraphEntity> document2Paragraphs(File file) {
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
            e.printStackTrace();
        }
        return null;
    }

    public static String getTextStreamEncode(File file) {
        InputStream ins = null;
        try {
            ins = new BufferedInputStream(new FileInputStream(file));
            return getTextStreamEncode(ins);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String createSubDocument(File file, PartEntity partEntity) {
        File subFile = createSubFile(file, partEntity.getPartNo());
        Writer writer = null;
        BufferedWriter bwriter = null;
        try {
            List<ParagraphEntity> paragraphs = partEntity.getParagraphs();
            writer = new FileWriter(subFile);
            bwriter = new BufferedWriter(writer);
            for (ParagraphEntity paragraph : paragraphs) {
                String[] sentences = paragraph.getSentences();
                String text = "";
                for (String sentence : sentences) {
                    text += sentence;
                }
                bwriter.write(text);
            }
            return subFile.getPath();
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
    public String createSubTranlatedDocument(PartEntity translatePart, String filePath, boolean checked) {
        // 文件不存在时会自动创建
        File file = new File(filePath);
        File subTranlatedFile = createSubTranlatedFile(file, translatePart.getPartNo());
        Writer writer = null;
        BufferedWriter bwriter = null;
        try {
            List<ParagraphEntity> paragraphs = translatePart.getParagraphs();
            writer = new FileWriter(subTranlatedFile);
            bwriter = new BufferedWriter(writer);
            for (ParagraphEntity paragraph : paragraphs) {
                String[] tranSentences = null;
                if (checked) {
                    tranSentences = paragraph.getCheckSentences();
                } else {
                    tranSentences = paragraph.getTranSentences();
                }
                String tranText = "";
                for (String tranSentence : tranSentences) {
                    tranText += tranSentence;
                }
                bwriter.write(tranText);
                bwriter.newLine();
            }
            return subTranlatedFile.getPath();
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
    public String createTranlatedDocument(List<PartEntity> partEntitys, String filePath, Boolean afterTranlated) {
        File tranlatedFile = createTranlatedFile(new File(filePath), afterTranlated);
        Writer writer = null;
        BufferedWriter bwriter = null;
        try {
            writer = new FileWriter(tranlatedFile);
            bwriter = new BufferedWriter(writer);
            if (afterTranlated == null) {
                for (PartEntity partEntity : partEntitys) {
                    List<ParagraphEntity> paragraphs = partEntity.getParagraphs();
                    for (ParagraphEntity paragraph : paragraphs) {
                        String[] tranSentences = paragraph.getCheckSentences();
                        String tranText = "";
                        for (String tranSentence : tranSentences) {
                            tranText += tranSentence;
                        }
                        bwriter.write(tranText);
                    }
                }
                return tranlatedFile.getPath();
            } else if (afterTranlated) {
                for (PartEntity partEntity : partEntitys) {
                    List<ParagraphEntity> paragraphs = partEntity.getParagraphs();
                    for (ParagraphEntity paragraph : paragraphs) {
                        String[] sentences = paragraph.getSentences();
                        String text = "";
                        for (String tranSentence : sentences) {
                            text += tranSentence;
                        }
                        bwriter.write(text);
                        bwriter.newLine();

                        String[] tranSentences = paragraph.getCheckSentences();
                        String tranText = "";
                        for (String tranSentence : tranSentences) {
                            tranText += tranSentence;
                        }
                        bwriter.write(tranText);
                        bwriter.newLine();
                    }
                }
            } else {
                for (PartEntity partEntity : partEntitys) {
                    List<ParagraphEntity> paragraphs = partEntity.getParagraphs();
                    for (ParagraphEntity paragraph : paragraphs) {
                        String[] tranSentences = paragraph.getCheckSentences();
                        String tranText = "";
                        for (String tranSentence : tranSentences) {
                            tranText += tranSentence;
                        }
                        bwriter.write(tranText);
                        bwriter.newLine();

                        String[] sentences = paragraph.getSentences();
                        String text = "";
                        for (String tranSentence : sentences) {
                            text += tranSentence;
                        }
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
        File subTranlatedFile = new File(file.getParent(), StringUtils.join(file.getName().split("\\."), "_t."));
        return subTranlatedFile;
    }
}
