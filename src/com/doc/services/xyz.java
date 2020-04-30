package com.doc.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Level;
import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.PresentationMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PresentationML.SlidePart;
import org.docx4j.utils.Log4jConfigurator;
import org.docx4j.utils.SingleTraversalUtilVisitorCallback;
import org.docx4j.wml.Body;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.pptx4j.pml.Presentation;

import com.doc.convertors.DocxToPdfConvertor.TraversalUtilParagraphVisitor;

public class xyz {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
//		try {
//		
		//WordprocessingMLPackage wordMLPackage =  readDocxFile("C:\\Users\\admin\\Downloads\\AgreementLetter_TestingTable.docx");
		//prepare(wordMLPackage);
//			
//			fillTable("<<propstrength__application_booking__c.propstrength__applicant_detail__c.3rdapplicant.name>>"  ,   wordMLPackage);
//			writeDocxToStream(wordMLPackage, "C:\\Users\\admin\\Downloads\\AgreementLetter_TestingTable_creat.docx");
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		InputStream is = new FileInputStream(new File(""));
//
//		PresentationMLPackage ppt = (PresentationMLPackage)   OpcPackage.load(is);
//
//		Presentation wmlDocumentEl = ppt.getMainPresentationPart().getJaxbElement();
//		
//
//		SingleTraversalUtilVisitorCallback paragraphVisitor = new SingleTraversalUtilVisitorCallback(
//				new TraversalUtilParagraphVisitor());
//		paragraphVisitor.walkJAXBElements(body);
//
//
//		
//		
//	    SlidePart slide = ppt.getMainPresentationPart().getSlide(0);
//	    HashMap h = new HashMap<String, String>();
//	    h.put("key", "green");
//
//	    slide.variableReplace(h);
//		
		
		

	}
	
	
	
	static void fillTable(String placeholder , WordprocessingMLPackage  parentObject) {
		try{
			List<Object> tables = getAllElementFromObject(parentObject.getMainDocumentPart(), Tbl.class);
			//System.out.println("tables "+tables);

			Tbl tempTable = getTemplateTable(tables, placeholder);
			System.out.println("tempTable "+tempTable);
			if(tempTable==null)return;
			List<Object> tcs = getAllElementFromObject(tempTable, Tc.class);
			System.out.println("tcs "+tcs);
//			Tc  tc=(Tc)tcs.get(0);
//			System.out.println("tc "+tc);
			for (Object tcObj : tcs) {
				
				Tc tc = (Tc) tcObj;

			List<Object> ps = getAllElementFromObject(tc, P.class);
			System.out.println("ps "+ps);
			 
			for (int psI=0;psI<ps.size();psI++) {
				 System.out.println("each ps :: "+ps.get(psI));
					List<Object> texts = getAllElementFromObject(ps.get(psI), Text.class);
						for (Object text : texts) {
							Text textElement = (Text) text;
							if(textElement.getValue().trim().equals(placeholder)){
								//wordMLPackage.getMainDocumentPart().getContent().remove(p);
								tc.getContent().remove(ps.get(psI));
							}
						}
			 }
			}
			//tc.getContent().remove(ps);
			//tc.getContent().remove(ps.get(0));
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();
 
		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
 
		}
		return result;
	}
	
	

	private static Tbl getTemplateTable(List<Object> tables, String templateKey) throws Docx4JException, JAXBException {
		int count=0;
		for (Iterator<Object> iterator = tables.iterator(); iterator.hasNext();) {
			//System.out.println(count);
			Object tbl = iterator.next();
			List<?> textElements = getAllElementFromObject(tbl, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				//System.out.println("textElement "+textElement);
				if (textElement.getValue() != null && textElement.getValue().equals(templateKey))
					return (Tbl) tbl;
			}
			count++;
		}
		return null;
	}	
	
	
	
	
	
	
	
	
	private static WordprocessingMLPackage readDocxFile(String docxPath)
			throws FileNotFoundException, Docx4JException, Exception {
		// try {
		Docx4jProperties.getProperties().setProperty("docx4j.Log4j.Configurator.disabled", "true");
		Log4jConfigurator.configure();
		org.docx4j.convert.out.pdf.viaXSLFO.Conversion.log.setLevel(Level.OFF);

		InputStream is = new FileInputStream(new File(docxPath));
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);
		return wordMLPackage;

	}
	
	public static void prepare(WordprocessingMLPackage wmlPackage) throws Exception {

		// Apply the filter
		WordprocessingMLPackage.FilterSettings filterSettings = new WordprocessingMLPackage.FilterSettings();
		filterSettings.setRemoveProofErrors(true);
		filterSettings.setRemoveContentControls(true);
		filterSettings.setRemoveRsids(true);
		wmlPackage.filter(filterSettings);
		// Note the filter is deprecated, since its questionable whether this
		// is important enough to live in WordprocessingMLPackage,
		// and in any case probably should be replaced with a TraversalUtil
		// approach (which wouldn't involve marshal/unmarshall, and
		// so should be more efficient).

		// if(log.isInfoEnabled()) {
		// log.info(XmlUtils.marshaltoString(wmlPackage.getMainDocumentPart().getJaxbElement(),
		// true, true));
		// }

		// Now clean up some more
		org.docx4j.wml.Document wmlDocumentEl = wmlPackage.getMainDocumentPart().getJaxbElement();
		Body body = wmlDocumentEl.getBody();

		SingleTraversalUtilVisitorCallback paragraphVisitor = new SingleTraversalUtilVisitorCallback(
				new TraversalUtilParagraphVisitor());
		paragraphVisitor.walkJAXBElements(body);

		// if(log.isInfoEnabled()) {
		// log.info(XmlUtils.marshaltoString(wmlPackage.getMainDocumentPart().getJaxbElement(),
		// true, true));
		// }
	}
	
	private static void writeDocxToStream(WordprocessingMLPackage template, String target) {
		try {
			File f = new File(target);
			template.save(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
