package com.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

/**
 * 
 * 由于ASPOSE比较吃内存，操作大一点的文件就会堆溢出，所以请先设置好java虚拟机参数：-Xms512m -Xmx512m(参考值)
 * 如有疑问，请在CSDN下载界面留言,或者联系QQ569925980
 * 
 * @author Spark
 *
 */
public class Test {

    /**
     * 获取license
     * 
     * @return
     */
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = Test.class.getClassLoader().getResourceAsStream("\\license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        // 验证License
        if (!getLicense()) {
            return;
        }

        try {
            File file = new File("D:\\test.pdf");
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document("D:\\test.docx");
            
            doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
