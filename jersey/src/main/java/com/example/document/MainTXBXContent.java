package com.example.document;

import java.io.FileOutputStream;
import java.util.Formatter;
import java.util.List;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

public class MainTXBXContent {
    public static void main(String[] args) throws Exception {
        MainTXBXContent t = new MainTXBXContent();
        String filePath = "d:/hello/test.docx";
        // t.readTextBoxContentUseXPath(filePath);
        t.readTextBoxContentUseCursor(filePath);
    }

    public void readTextBoxContentUseXPath(String filePath) throws Exception {
        XWPFDocument xdoc = openDocument(filePath);
        List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
        // 取第一段
        XWPFParagraph paragrap = paragraphList.get(0);
        // 文本框位于第一个<w:r></w:r>内
        XmlObject object = paragrap.getCTP().getRArray(0);
        // System.out.println(object);
        XmlCursor selectPathCursor = getXmlObjectByXPath(object,
                "declare namespace ve='http://schemas.openxmlformats.org/markup-compatibility/2006'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships'; declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace w10='urn:schemas-microsoft-com:office:word'; declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wne='http://schemas.microsoft.com/office/word/2006/wordml';",
                "$this/w:pict/v:shape/v:textbox/w:txbxContent/w:p");
        selectPathCursor.push();// 保存当前位置
        selectPathCursor.selectPath(
                "declare namespace ve='http://schemas.openxmlformats.org/markup-compatibility/2006'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships'; declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace w10='urn:schemas-microsoft-com:office:word'; declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wne='http://schemas.microsoft.com/office/word/2006/wordml';"
                        + "$this/w:r/w:t");
        while (selectPathCursor.toNextSelection()) {
            System.out.println("文本框原来内容=" + selectPathCursor.getTextValue());
            selectPathCursor.setTextValue("修改");
        }
        selectPathCursor.pop();// 恢复上次位置
        selectPathCursor.toParent();// w:txbxContent
        selectPathCursor.toChild(1);// w:p[1]
        selectPathCursor.selectPath(
                "declare namespace ve='http://schemas.openxmlformats.org/markup-compatibility/2006'; declare namespace o='urn:schemas-microsoft-com:office:office'; declare namespace r='http://schemas.openxmlformats.org/officeDocument/2006/relationships'; declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math'; declare namespace v='urn:schemas-microsoft-com:vml'; declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing'; declare namespace w10='urn:schemas-microsoft-com:office:word'; declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'; declare namespace wne='http://schemas.microsoft.com/office/word/2006/wordml';"
                        + "$this/w:r/w:t");
        while (selectPathCursor.toNextSelection()) {
            System.out.println("文本框原来内容2=" + selectPathCursor.getTextValue());
            selectPathCursor.setTextValue("修_改");
        }
        selectPathCursor.dispose();
        saveDocument(xdoc, "d:/hello/sys_" + System.currentTimeMillis() + ".docx");
    }

    public XmlCursor getXmlObjectByXPath(XmlObject xml, String nameSpace, String xpath) {
        // Create a temporary cursor for the XPath passed in
        XmlCursor xpathCursor = xml.newCursor();
        // Create a formatter to format the XPath
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder);
        formatter.format("%s %s", nameSpace, xpath);
        // Select the XPath
        xpathCursor.selectPath(formatter.toString());
        xpathCursor.toNextSelection();
        formatter.close();
        return xpathCursor;
    }

    public void readTextBoxContentUseCursor(String filePath) throws Exception {
        XWPFDocument xdoc = openDocument(filePath);
        List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
        // 取第一段
        XWPFParagraph paragrap = paragraphList.get(0);
        // 文本框位于第一个<w:r></w:r>内
        XmlObject object = paragrap.getCTP().getRArray(0);
        // 参考https://www.ibm.com/developerworks/cn/xml/x-beans1/ 高级特性部分
        // System.out.println(object);
        XmlCursor cursor = object.newCursor();
        cursor.toChild(1);// <xml-fragment> --> <w:pict>
        cursor.toChild(1);// <w:pict> --> <v:shape>
        cursor.toChild(0);// <v:shape> --> <v:textbox>
        cursor.toChild(0);// <v:textbox> --><w:txbxContent>

        cursor.toChild(0);// <w:txbxContent> --> <w:p>
        cursor.toChild(1);// <w:p> --> <w:r>
        cursor.toChild(1);// <w:r> --> <w:t>
        System.out.println("文本框原来值:" + cursor.getTextValue());
        cursor.setTextValue("修改后");

        // 回到<w:txbxContent>位置
        cursor.toParent();// <w:t>--><w:r>
        cursor.toParent();// <w:r>--><w:p>
        cursor.toParent();// <w:p>--><w:txbxContent>
        cursor.toChild(1);// <w:txbxContent>--><w:p>
        cursor.toChild(1);// <w:p> --> <w:r>
        cursor.toChild(1);// <w:r> --> <w:t>
        System.out.println("文本框原来值:" + cursor.getTextValue());
        cursor.setTextValue("修改后");
        // 回到<w:p>位置
        cursor.toParent();// <w:t>--><w:r>
        cursor.toParent();// <w:r>--><w:p>
        cursor.toChild(2);// <w:p>--><w:r>
        cursor.toChild(1);// <w:r> --> <w:t>
        System.out.println("文本框原来值:" + cursor.getTextValue());
        cursor.setTextValue("4");
        cursor.dispose();
        saveDocument(xdoc, "d:/hello/sys_" + System.currentTimeMillis() + ".docx");
    }

    public void saveDocument(XWPFDocument document, String savePath) throws Exception {
        FileOutputStream fos = new FileOutputStream(savePath);
        document.write(fos);
        fos.close();
    }

    public XWPFDocument openDocument(String filePath) throws Exception {
        XWPFDocument xdoc = new XWPFDocument(POIXMLDocument.openPackage(filePath));
        return xdoc;
    }
}