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
	
	public static void main(String[] args){
		XWPFWordParse wordParse = new XWPFWordParse();
		String filePath = "d:/hello/wordParse.docx";
		int subLength=5;
		int subSum=2;
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
		
		List<SecondElement> secondElements = new ArrayList<SecondElement>();
		firstElement.setSecondElements(secondElements);
		
		int index_h=0;
		int columnNum=1;
		for(XWPFHeader xheader:xheaders){
			List<XWPFParagraph> xparagraphs  = xheader.getListParagraph();
//			xheader.getBodyElements();
			if(xparagraphs!=null && !xparagraphs.isEmpty()){
				SecondElement secondElement = new SecondElement();
				secondElements.add(secondElement);
				secondElement.setElementType("Paragraph");
				secondElement.setColumnNum(columnNum);
				
				secondElement.setElementIndex(index_h);
				
				List<ThreeElement> threeElements = new ArrayList<ThreeElement>();
				secondElement.setThreeElements(threeElements);
				
				int index_p=0;
				for(XWPFParagraph xparagraph:xparagraphs){
					ThreeElement threeElement =parseParagraph(xdocument,xparagraph,storage);
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
	
	/**
	 * @param xdocument
	 * @param xparagraph
	 * @param storage
	 * @return
	 */
	private ThreeElement parseParagraph(XWPFDocument xdocument,XWPFParagraph xparagraph, String storage){
		//所有图片
		//xdocument.getAllPackagePictures();
		
		ThreeElement threeElement=null;
		List<XWPFRun> xruns = xparagraph.getRuns();
		if(xruns!=null && !xruns.isEmpty()){
			threeElement = new ThreeElement();
			int runNum=0;
			int index_r =0;
			
			boolean isbreak=false;
			String type=null;
			String content="";
			List<FourElement> fourElements =new ArrayList<FourElement>();
			FourElement fourElement = new FourElement();
			List<FiveElement>fiveElements = new ArrayList<FiveElement>();
			fourElement.setFiveElements(fiveElements);
			fourElements.add(fourElement);
			
			for(XWPFRun xrun:xruns){
				//过滤脚注|尾注
				CTR ftn = xrun.getCTR();
				XmlObject o = ftn.copy();
				Node node = o.getDomNode().getLastChild();
				String name = node.getLocalName();
				// 判断脚注
				if (StringUtils.equals(name, "footnoteReference") || StringUtils.equals(name, "endnoteReference")) {
					if (fourElement.getElementType() != null) {
						isbreak = true;
					}
					continue;
				} 
				
				runNum++;
				
				String text = xrun.text().trim();
				
				
				// 软换除是否需要断句?(目前没断句)
				if (StringUtils.isNotBlank(text)) {
					type = "TEXT";
					//content = text;
					if (isbreak) {
						logger.info(fourElement.getContent());
						//断句
						fourElement = new FourElement();
						fourElement.setIndex(index_r);
						
						fourElements =  new ArrayList<FourElement>();
						fourElement.setFiveElements(fiveElements);
						fourElements.add(fourElement);
						content = text;
					} else {
						content += text;
						
						FiveElement fiveElement = new FiveElement();
						fiveElement.setIndex(runNum);
						fiveElement.setContent(text);
						fiveElement.setFontName(xrun.getFontName());
						fiveElement.setFontsize(xrun.getFontSize());
						fiveElements.add(fiveElement);
					}
					fourElement.setElementType(type);
					fourElement.setContent(content);
					isbreak = breakSentence(removeSpace(content));
				} else {
					List<XWPFPicture> xpictures = xrun.getEmbeddedPictures();
					if (xpictures != null && xpictures.size() > 0) {
						// 一张图片一句
						int index_i=0;//图片序列
						isbreak = true;
						for (XWPFPicture xpicture : xpictures) {
							XWPFPictureData pictureData = xpicture.getPictureData();
							type = "IMAGE";
							FileOutputStream fos = null;
							try {
								File imageFile = new File(storage,String.format("%d_%d_%s", index_r, index_i++,pictureData.getFileName()));
								fos = new FileOutputStream(imageFile);
								fos.write(pictureData.getData());
								content = imageFile.getPath();
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
							logger.info(fourElement.getContent());
							if (fourElement.getElementType() != null) {
								fourElements = new ArrayList<FourElement>();
								fourElement = new FourElement();
								fourElement.setIndex(index_r);
								
								fiveElements = new ArrayList<FiveElement>();
								fourElement.setFiveElements(fiveElements);
								fourElements.add(fourElement);
							}
							fourElement.setElementType(type);
							content = text;
							fourElement.setContent(content);
						}
					} else {
						isbreak = true;
						continue;
					}
				}
//				ContentElement contentElement = new ContentElement();
//				contentElement.setContentSerial(runNum - 1);
//				contentElement.setContentType(contentType);
//				contentElement.setContentText(contentText);
//				contents.add(contentElement);
			}
		}
		
		//文本框;
		
		//内嵌图片
		return threeElement;
	}
}
