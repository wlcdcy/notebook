package com.example.document;

import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ISDTContent;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

//只处理了文本(不包括公式) 只处理了正文
public class MainSdtContent {
	public static void main(String[] args) throws Exception {
		MainSdtContent t = new MainSdtContent();
		t.printAllSdtContent("d:/hello/test.docx");
	}

	public void printAllSdtContent(String filePath) throws Exception {
		XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(filePath));
		// 打印独立于段落和表格外的内容控件
		// 关于XWPFSDT可以参考http://svn.apache.org/repos/asf/poi/trunk/src/ooxml/testcases/org/apache/poi/xwpf/usermodel/TestXWPFSDT.java
		Iterator<IBodyElement> itrator = xdoc.getBodyElementsIterator();
		while (itrator.hasNext()) {
			IBodyElement element = itrator.next();
			if (element instanceof XWPFSDT) {
				XWPFSDT sdt = (XWPFSDT) element;
				printXWPFSDTContent(sdt);
			}
		}
		// 打印段落内内容控件
		List<XWPFParagraph> paraList = xdoc.getParagraphs();
		printParaListContent(paraList);
		// 打印表格内内容控件
		List<XWPFTable> tblList = xdoc.getTables();
		for (int i = 0, len = tblList.size(); i < len; i++) {
			XWPFTable table = tblList.get(i);
			for (int j = 0, rcount = table.getNumberOfRows(); j < rcount; j++) {
				XWPFTableRow row = table.getRow(j);
				List<CTSdtCell> tblSdtList = row.getCtRow().getSdtList();
				if (tblSdtList != null && tblSdtList.size() > 0) {
					for (CTSdtCell ctSdtCell : tblSdtList) {
						CTSdtPr sdtPr = ctSdtCell.getSdtPr();
						printSdtPrContent(sdtPr);
						CTSdtContentCell sdtContent = ctSdtCell.getSdtContent();
						printSdtContentCell(sdtContent);
					}
				}
			}
		}
		xdoc.close();
	}

	//无法获取更多的内容(如文本类型,是否可编辑)
	public void printXWPFSDTContent(XWPFSDT sdt) {
		StringBuffer sb = new StringBuffer();
		sb.append(" 标记:").append(sdt.getTag());
		sb.append(" 标题:").append(sdt.getTitle());
		ISDTContent content = sdt.getContent();
		sb.append(" 内容:").append(content.getText());
		System.out.println(sb.toString());
	}

	public void printParaListContent(List<XWPFParagraph> paraList) {
		if (paraList == null || paraList.size() == 0) {
			return;
		}
		for (XWPFParagraph para : paraList) {
			List<CTSdtRun> sdtList = para.getCTP().getSdtList();
			if (sdtList == null || sdtList.size() == 0) {
				continue;
			}
			for (CTSdtRun sdtRun : sdtList) {
				CTSdtPr sdtPr = sdtRun.getSdtPr();
				printSdtPrContent(sdtPr);
				CTSdtContentRun sdtContent = sdtRun.getSdtContent();
				printSdtContent(sdtContent);
			}
		}
	}

	// 解析样式,区分纯文本和格式文本
	public void printSdtPrContent(CTSdtPr sdtPr) {
		if (sdtPr == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		List<CTSdtText> textList = sdtPr.getTextList();
		if (textList != null && textList.size() > 0) {
			sb.append(" 内容控件类型:").append("纯文本");
			CTSdtText sdtText = textList.get(0);
			if (sdtText.getMultiLine() != null) {
				int mulType = sdtText.getMultiLine().intValue();
				if (mulType == 1 || mulType == 3 || mulType == 6) {
					sb.append(" 是否允许回车:").append("是");
				}
			}
		} else {
			sb.append(" 内容控件类型:").append("格式文本");
		}
		List<CTDecimalNumber> idList = sdtPr.getIdList();
		if (idList != null && idList.size() > 0) {
			sb.append(" ID:").append(idList.get(0).getVal());
		}
		List<CTString> aliasList = sdtPr.getAliasList();
		if (aliasList != null && aliasList.size() > 0) {
			sb.append(" 标题:").append(aliasList.get(0).getVal());
		}
		List<CTString> tagList = sdtPr.getTagList();
		if (tagList != null && tagList.size() > 0) {
			sb.append(" 标记:").append(tagList.get(0).getVal());
		}
		List<CTLock> lockList = sdtPr.getLockList();
		if (lockList != null && lockList.size() > 0) {
			CTLock ctLock = lockList.get(0);
			int lockType = ctLock.getVal().intValue();
			switch (lockType) {
			case 1:
				sb.append(" 锁定方式:").append("无法删除内容控件");
				break;
			case 2:
				sb.append(" 锁定方式:").append("无法编辑内容");
				break;
			case 4:
				sb.append(" 锁定方式:").append("无法删除内容控件，无法编辑内容");
				break;
			default:
				sb.append(" 锁定方式:").append(ctLock.getVal());
				break;
			}
		}
		List<CTOnOff> tempList = sdtPr.getTemporaryList();
		if (tempList != null && tempList.size() > 0) {
			if (tempList.get(0).getVal() != null) {
				int isOn = tempList.get(0).getVal().intValue();
				if (isOn == 1 || isOn == 3 || isOn == 6) {
					sb.append(" 替换后是否删除内容控件:").append("是");
				}
			} else {
				sb.append(" 替换后是否删除内容控件:").append("是");
			}
		}
		List<CTRPr> rprList = sdtPr.getRPrList();
		if (rprList != null && rprList.size() > 0) {
			CTRPr rpr = rprList.get(0);
			CTString rprStyle = rpr.getRStyle();
			if (rprStyle != null) {
				sb.append(" 样式名称:").append(rprStyle.getVal());
			}
		}
		System.out.println(sb.toString());
	}

	// 段落内内容控件
	public void printSdtContent(CTSdtContentRun sdtContentRun) {
		if (sdtContentRun == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		List<CTR> ctrList = sdtContentRun.getRList();
		sb.append(getCTRContent(ctrList));
		System.out.println("内容:" + sb.toString());
	}

	// 表格内内容控件
	public void printSdtContentCell(CTSdtContentCell sdtContentCell) {
		if (sdtContentCell == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		List<CTTc> cttcList = sdtContentCell.getTcList();
		sb.append(getCTTcContent(cttcList));
		System.out.println("内容:" + sb.toString());
	}

	// 段落
	public String getCTRContent(List<CTR> ctrList) {
		StringBuffer sb = new StringBuffer();
		if (ctrList != null && ctrList.size() > 0) {
			for (CTR ctr : ctrList) {
				List<CTText> tList = ctr.getTList();
				if (tList != null && tList.size() > 0) {
					for (CTText ctText : tList) {
						sb.append(ctText.getStringValue());
					}
				}
			}
		}
		return sb.toString();
	}

	// 表格
	public String getCTTcContent(List<CTTc> cttcList) {
		StringBuffer sb = new StringBuffer();
		if (cttcList != null && cttcList.size() > 0) {
			for (CTTc cttc : cttcList) {
				List<CTP> pList = cttc.getPList();
				if (pList != null && pList.size() > 0) {
					for (CTP ctp : pList) {
						List<CTR> rList = ctp.getRList();
						sb.append(getCTRContent(rList));
					}
				}
			}
		}
		return sb.toString();
	}
}