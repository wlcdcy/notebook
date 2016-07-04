package com.example.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.document.entity.FirstElement;
import com.example.document.entity.SecondElement;
import com.example.document.entity.ThreeElement;
import com.example.document.entity.TopElement;

public class XWPFWordParse extends DocumentParse {
	private static Logger logger = LoggerFactory.getLogger(XWPFWordParse.class);

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
		//FileChannel channel = null;
		XWPFDocument xdocument = null;
		try {
			fins = new FileInputStream(file);
			//channel = fins.getChannel();
			xdocument = new XWPFDocument(fins);
			TopElement topElement = new TopElement();
			FirstElement  headerElement = parseHeader(xdocument,file.getParent());
			topElement.setHeaderElement(headerElement);
			
			
			return topElement;
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage(), e);
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

	
	private FirstElement parseHeader(XWPFDocument xdocument, String storage){
		List<XWPFHeader> xheaders =xdocument.getHeaderList();
		
		FirstElement firstElement = new FirstElement();
		firstElement.setElementType("HEADER");
		
		List<SecondElement> secondElements = Collections.emptyList();
		firstElement.setSecondElements(secondElements);
		
		int index_h=0;
		int columnNum=1;
		for(XWPFHeader xheader:xheaders){
			List<XWPFParagraph> xparagraphs  = xheader.getListParagraph();
//			xheader.getBodyElements();
			if(xparagraphs!=null && xparagraphs.isEmpty()){
				SecondElement secondElement = new SecondElement();
				secondElement.setElementType("Paragraph");
				secondElement.setColumnNum(columnNum);
				
				secondElement.setElementIndex(index_h);
				
				List<ThreeElement> threeElements = Collections.emptyList();
				secondElement.setThreeElements(threeElements);
				
				int index_p=0;
				for(XWPFParagraph xparagraph:xparagraphs){
					ThreeElement threeElement =parseParagraph(xparagraph,storage);
					if(threeElement!=null){
						threeElement.setElementIndex(index_p);
						threeElements.add(threeElement);
					}
					index_p++;
				}
			}
			columnNum++;
			index_h++;
		}
		return firstElement;
	}
	
	private ThreeElement parseParagraph(XWPFParagraph xparagraph, String storage){
		return null;
	}
}
