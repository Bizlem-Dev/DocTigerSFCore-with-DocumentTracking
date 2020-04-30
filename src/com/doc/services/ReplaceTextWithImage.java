package com.doc.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
//import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.docx4j.Docx4jProperties;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.utils.Log4jConfigurator;
import org.docx4j.utils.SingleTraversalUtilVisitorCallback;
import org.docx4j.wml.Body;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.json.JSONArray;
import org.json.JSONObject;

import com.doc.convertors.DocxToPdfConvertor;

public class ReplaceTextWithImage {

	
	final static Logger log = Logger.getLogger(ReplaceTextWithImage.class);
	public static void main(String[] args) throws Exception {
//OperationtestTemplate.docx"; 
		String outputDocxPath = "D:\\pallavi\\DocTiger\\SalesForce Doc tiger\\DOCTIGER SF WITH RULE\\31-05-19\\WelcomeLetter - Copy.docx"	;//DocImgLink.docx";
		String igp = "D:\\DOCTIGER114IPProject\\testing docx\\down1.png";
		WordprocessingMLPackage wordMLPackage = null;
		wordMLPackage = getTemplate(outputDocxPath);
		System.out.println("wordMLPackage  "+wordMLPackage);
		String saveimgpath="D:\\pallavi\\DocTiger\\SalesForce Doc tiger\\DOCTIGER SF WITH RULE\\31-05-19\\";
		try {
			BufferedImage image = null;
			String imageUrl="http://34.74.13.213:8082/centerimage.png";
			int o= imageUrl.lastIndexOf("/");
			String imgname = imageUrl.substring(o+1,imageUrl.length());
			System.out.println("imgname=== "+imgname);
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> replimg = new HashMap<String, Object>();
//		if() {}
//		"<<Imglink>>":"http://34.74.13.213:8082/centerimage
		replimg.put("<<Imglink>>", "http://34.74.13.213:8082/centerimage.png");
		replimg.put("fds", "http://34.74.13.213:8082/centerimage.png");
//		replimg.put("Hello", igp);
		System.out.println("maplist = " + replimg);
		for (Map.Entry<String, Object> entry : replimg.entrySet()) {
//			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		ReplaceLinkWithImage(replimg, saveimgpath, wordMLPackage);
	

		wordMLPackage.save(new java.io.File("D:\\pallavi\\DocTiger\\SalesForce Doc tiger\\DOCTIGER SF WITH RULE\\31-05-19\\resultrplc.docx"));

	}

	/**this method adds image in table cell paragraph
	 * @param inline
	 * @return
	 */
	private static P addInlineImageToParagraph(Inline inline) {
		// Now add the in-line image to a paragraph
		P paragraph=null;
		try {
		log.info("start addInlineImageToParagraph ");
		ObjectFactory factory = new ObjectFactory();
		 paragraph = factory.createP();
		R run = factory.createR();
		log.info(" 1");
		paragraph.getContent().add(run);
		Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		log.info(" done3 ");
		}catch (Exception e) {
			log.info(" exc in addInlineImageToParagraph "+e);
			// TODO: handle exception
		}
		return paragraph;
	}

	private static Inline createInlineImage(File file,WordprocessingMLPackage wordMLPackage) throws Exception {
		int docPrId = 1;
		int cNvPrId = 2;
		BinaryPartAbstractImage imagePart=null;
		try {
			
			log.info("start createInlineImage ");
		byte[] bytes = convertImageToByteArray(file);

		 imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

		
		}catch (Exception e) {
			// TODO: handle exception
			log.info("exc in  createInlineImage "+e);
			
		}
		return imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);
		
	}

	private static byte[] convertImageToByteArray(File file) throws FileNotFoundException, IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		// You cannot create an array using a long, it needs to be an int.
		if (length > Integer.MAX_VALUE) {
			System.out.println("File too large!!");
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;

		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
//     while (offset= 0) {
//            offset += numRead;
//     }
		// Ensure all the bytes have been read
		if (offset < bytes.length) {
			System.out.println("Could not completely read file " + file.getName());
		}
		is.close();
		return bytes;
	}

	private static WordprocessingMLPackage getTemplate(String name) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(name)));
		return template;
	}

	private static List getAllElementFromObject(Object obj, Class toSearch) {
		List result = new ArrayList();
		if (obj instanceof JAXBElement)
			obj = ((JAXBElement) obj).getValue();

		if (obj.getClass().equals(toSearch)) {
			result.add(obj);
		} else if (obj instanceof ContentAccessor) {
			List children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}

		}
		return result;
	}

	/**
	 * save image in folder from image url
	 * @param imageUrl
	 * @param saveimgpath
	 * @return
	 * @throws IOException
	 */
	public static String saveImage(String imageUrl, String saveimgpath) throws IOException {
		String imgname ="";
		try {
			int o= imageUrl.lastIndexOf("/");
			 imgname = imageUrl.substring(o+1,imageUrl.length()).replaceAll(":", "_");
			System.out.println("imgname= "+imgname);
			URL url = new URL(imageUrl);
			if(!imgname.contains(".")) {
				imgname="img.jpg";
			}else {
				
			}
			
//			url.getFile();
			
			System.out.println("url.getFile()= ");
			// read the url

			InputStream is = url.openStream();
			System.out.println("done");
			OutputStream os = new FileOutputStream(saveimgpath+imgname);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return saveimgpath+imgname;
	}
	
/**this method iterate on replimg finds the table in which this images to add and replace pattern with image saved from image url
 * @param replimg
 * @param saveimgpath
 * @param wordMLPackage
 * @throws Exception
 */
public static void ReplaceLinkWithImage(Map<String, Object> replimg,String saveimgpath,WordprocessingMLPackage wordMLPackage) throws Exception {

	try {
		System.out.println("start= ");
		log.info("start ReplaceLinkWithImage");
	List elemetns = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), Tbl.class);
log.info(elemetns.size());
log.info("replimg "+replimg.size());
for (Map.Entry<String, Object> entry : replimg.entrySet()) {
	log.info("entry  "+entry);
//for(int i=0; i<elemetns.size(); i++) {
Tbl table = getTemplateTable(elemetns, entry.getKey());
log.info("in table method call");
log.info("table"+table);

//	for (Object obj : tempTable) {
//		if (obj instanceof Tbl) {
//			Tbl table = (Tbl) obj;
if(table!=null) {
			List rows = getAllElementFromObject(table, Tr.class);
			for (Object trObj : rows) {
				
				Tr tr = (Tr) trObj;
				List cols = getAllElementFromObject(tr, Tc.class);
				for (Object tcObj : cols) {
					
					Tc tc = (Tc) tcObj;
					//List<Object> texts = getAllElementFromObject(wordMLPackage.getMainDocumentPart(), Text.class);
					List<Object> texts = getAllElementFromObject(tc, Text.class);
					
					for (Object textObj : texts) {
						
						Text text = (Text) textObj;
						//for (Map.Entry<String, Object> entry : replimg.entrySet()) {
							try{
								System.out.println("entry.getKey() ***===== "+entry.getKey().trim()+"  text.getValue()=== "+text.getValue());
								log.info("entry.getKey() ***===== "+entry.getKey().trim()+"  text.getValue()=== "+text.getValue());

								//log.info("entry.getKey() = "+entry.getKey().trim());
							//if (text.getValue().trim().equalsIgnoreCase(entry.getKey().trim())) { // "${MY_PLACE_HOLDER}"
								// save image to server
								 System.out.println("text.getValue() if--------= "+text.getValue());
								log.info("entry.getKey().trim() = "+entry.getKey().trim());
								log.info("entry.getKey().trim() = "+entry.getValue().toString().trim());
								String filepath="";
								try {
								 filepath=saveImage(entry.getValue().toString().trim(), saveimgpath);
								 System.out.println("filepath= "+filepath);
								 log.info(" filepath= " + filepath);
								}catch (Exception e) {
									// TODO: handle exception
									 log.info(" exc in =saveImage " + e);
								}
								try {
									if(!filepath.equals(saveimgpath)) {
									//  /home/ubuntu/generationTomcat/Images/centerimage.png
									//String fp="/home/ubuntu/generationTomcat/Images/centerimage.png";
									// log.info(" img file  filepath== " + fp);
								File myfile = new File(filepath);
								 log.info(" img file  filepathnnn111111== " + filepath);
								P paragraphWithImage = addInlineImageToParagraph(createInlineImage(myfile,wordMLPackage));
								
								tc.getContent().remove(0);
								log.info("22" );
								tc.getContent().add(paragraphWithImage);
								log.info(" 33");
								}else {
									ObjectFactory factory = new ObjectFactory();
									P blankparagraph = factory.createP();
									tc.getContent().remove(0);
									log.info("22 else" );
									tc.getContent().add(blankparagraph);
									log.info(" 33 else");

									}
								}catch (Exception e) {
									// TODO: handle exception
									 log.info(" ex in  file== " + e);
								}
							//}
						}catch (Exception e) {
							// TODO: handle exception
							 log.info(" ex in  file== " + e);
						}
						//}
					}
					System.out.println("Done");
				}
			}
			log.info("done1" );
			System.out.println("here");
		//}
	//}
}//tablnot equal to null;
		//}

	}
	}catch (Exception e) {
		// TODO: handle exception
		log.info("exception = "+e );
	}
}




private static Tbl getTemplateTable(List<Object> tables, String templateKey) throws Docx4JException, JAXBException {
	for (Iterator<Object> iterator = tables.iterator(); iterator.hasNext();) {
		Object tbl = iterator.next();
		List<?> textElements = getAllElementFromObject(tbl, Text.class);
		for (Object text : textElements) {
			Text textElement = (Text) text;
			if (textElement.getValue() != null && textElement.getValue().equals(templateKey))
				return (Tbl) tbl;
		}
	}
	return null;
}





public static void fillTable(JSONArray removePtblArr , WordprocessingMLPackage  parentObject, Map<String, Object> paramsMap) {
	try{
		List<Object> tables = getAllElementFromObject(parentObject.getMainDocumentPart(), Tbl.class);
		//System.out.println("tables "+tables);

			for(int i=0; i<removePtblArr.length(); i++) {
				String placeholder=removePtblArr.getString(i);
		Tbl tempTable = getTemplateTable(tables, placeholder);
		System.out.println("tempTable "+tempTable);
		if(tempTable==null)return;
		List<Object> tcs = getAllElementFromObject(tempTable, Tc.class);
		System.out.println("tcs "+tcs);
//		Tc  tc=(Tc)tcs.get(0);
//		System.out.println("tc "+tc);
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
							if(paramsMap.containsKey(textElement.getValue().trim())) {
							      if( paramsMap.get(textElement.getValue().trim()).toString().equals("")) {
							        //wordMLPackage.getMainDocumentPart().getContent().remove(p);
							       tc.getContent().remove(ps.get(psI));
							       }else {break;
							       }
						       }else {
							       tc.getContent().remove(ps.get(psI));
						       }
							}else {
						    	System.out.println("in else part of fillTable");
						    	
						    	String textelementstr= textElement.toString();
						    	//log.info("textelementstr "+textelementstr);

						    	while (textelementstr.indexOf("<<") != -1 && textelementstr.indexOf(">>") != -1) {
									log.info("*textelementstr.indexOf(\"<<\"): " + textelementstr.indexOf("<<"));
									log.info("*textelementstr.indexOf(\">>\"): " + textelementstr.indexOf(">>"));

									String key = textelementstr.substring(textelementstr.indexOf("<<"),
											textelementstr.indexOf(">>") + 2);
									log.info("*key " + key + " *textelementstr " + textelementstr);
									
									if(key.equals(placeholder)) {
										log.info("key.equals(placeholder) :"+key.equals(placeholder) );

										if (paramsMap.containsKey(key)) {
											log.info("paramsMap.get(key)"+paramsMap.get(key).toString());
											log.info("paramsMap.get(key).toString().equals(\"\") "+paramsMap.get(key).toString().equals(""));
											if(key.equals(placeholder) && paramsMap.get(key).toString().equals("")){
											       tc.getContent().remove(ps.get(psI));
					                			textelementstr =textelementstr.replace(key, "");
					                			log.info("1 :"+textelementstr);
											}else {
					                			textelementstr =textelementstr.replace(key, "");
					                			log.info("2 :"+textelementstr);

											}
										}else{
											log.info("key  not in paramMap" );
										       tc.getContent().remove(ps.get(psI));
				                			textelementstr =textelementstr.replace(key, "");
				                			log.info("3 :"+textelementstr);

										}
									}else { 
										log.info("key not in map and not equals placeholde" );

			                			textelementstr =textelementstr.replace(key, "");
			                			log.info("4 :"+textelementstr);

				                    }
									log.info( " textelementstr  5" + textelementstr);
							}
						}
					}
		 }
		}
	}
	}catch (Exception e) {
		throw new RuntimeException(e);
	}
}


}
