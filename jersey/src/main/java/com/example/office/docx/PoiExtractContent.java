package com.example.office.docx;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class PoiExtractContent<T> {
	static int defaultSplitLength = 2;
	static String[] splitChars = { "?", "!", ". ", "？", "！", "。" };
	static String CHAR_SPACE=" ";
	static String CHAR_ALL_SPACE="　";
	static String CHAR_TAB_SPACE="	";
	static String CHAR_SOFTENTER="";
	
	
	
	public Integer characterLength(File file) {
		InputStream ins = null;
		try {
			ins = new FileInputStream(file);
			return characterLength(ins);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
		
	public abstract Integer characterLength(InputStream ins);
	
	public List<Part> document2Parts(File file,int partLength){
		InputStream ins = null;
		try {
			ins = new FileInputStream(file);
			return document2Parts(ins, partLength);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
	public abstract List<Part> document2Parts(InputStream ins,int partLength);
	
	public Part document2Part(File file){
		InputStream ins = null;
		try {
			ins = new FileInputStream(file);
			return document2Part(ins);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public abstract Part document2Part(InputStream ins);
	
	public String[] splitContent(String text) {
		String lineSep = System.getProperty("line.separator");
		for (String splitchar : splitChars) {
			if (text.contains(splitchar)) {
				text = text.replace(splitchar, splitchar + lineSep);
			}
		}
		return text.split(lineSep);
	}
	
	public int getLengthRemoveSpace(String text){
		return text.replaceAll(CHAR_SPACE, "").replaceAll(CHAR_ALL_SPACE, "").replaceAll(CHAR_TAB_SPACE, "").replaceAll(CHAR_SOFTENTER, "").length();
	}

	public void createDocument(List<Part> parts, String tempTemple) {
		
	}

	public void createDocument(Part part, String tempTemple) {
		
	}
}
