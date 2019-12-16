package com.doc.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
//import java.net.URLConnection;
import java.util.ResourceBundle;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import org.json.JSONException;



public class SaveTemplateFromURL {
	ResourceBundle bundle = ResourceBundle.getBundle("config");
	static ResourceBundle bundleststic = ResourceBundle.getBundle("config");

	public static void main(String args[]) throws IOException, JSONException {
		SaveTemplateFromURL sta = new SaveTemplateFromURL();

		String savepath = "D:\\DoctigerSAlesforce\\";
		String fr = "http://34.74.13.213:8082/portal/bin/cpm/nodes/property.bin/content/user/doctiger_xyz.com/DocTigerAdvanced/TemplateLibrary/newtem6june/TemplateFile/File/welcomtempyy.docx/_jcr_content?name=jcr%3Adata";
		sta.saveTemplate(fr, savepath, "dct1.docx");

	}

	/* get template url and save to server */

	public String saveTemplate(String fileurl, String savepath, String filename) {
		String resp = "";

		try {
			
			ignoreHttps(fileurl);
			
			fileurl=fileurl.replace(" ", "%20");
			BufferedInputStream in = new BufferedInputStream(new URL(fileurl).openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(savepath + filename);
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
			resp = "success";
			System.out.println("files saved");
		} catch (Exception e) {
			// TODO: handle exception
			resp = e.getMessage();
			System.out.println("e :: " + e);
		} finally {

		}
		return resp;
	}
	
	public void ignoreHttps(String urlstring){
		try{
		if(urlstring.indexOf("https:") != -1){
		TrustManager[] trustAllCerts = new TrustManager[] {
		new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) { }

		public void checkServerTrusted(X509Certificate[] certs, String authType) { }

		}
		};


		try {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		}

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
		return true;
		}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		/*
		* end of the fix
		*/
		}
		}catch(Exception e){

		}
		}
}
