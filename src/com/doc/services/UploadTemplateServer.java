package com.doc.services;




import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.doc.generation.SFDCDocumentGeneration;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class UploadTemplateServer {
	final static Logger logger = Logger.getLogger(UploadTemplateServer.class); 

	private String host;
	private Integer port;
	private String user;
	private String password;
	
	private JSch jsch;
	private Session session1;
	private Channel channel;
	private ChannelSftp sftpChannel;
	
	
	public UploadTemplateServer(String host, Integer port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public String connect() {
		String r="";
		System.out.println("connecting..."+host+ " user :: "+user+ "password :: "+password);
		try {
			
//			JSch jsch = new JSch();
//
//		
//			Session session = jsch.getSession(USERNAME, host, Integer.parseInt(port));
//			// session.setConfig("StrictHostKeyChecking", "no");
//			java.util.Properties config = new java.util.Properties();
//			config.put("StrictHostKeyChecking", "no");
//			session.setConfig(config);
//
//			session.setPassword(PASSWORD);
//			session.connect();
//			System.out.println("Done!");
			jsch = new JSch();
			session1 = jsch.getSession(user, host,port);
//			session1.setConfig("StrictHostKeyChecking", "no");
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session1.setConfig(config);
//			String d="192.168.0.202";
			
			session1.setPassword(password);
			r="1w";
			System.setProperty("https.proxyHost", host);
			System.setProperty("https.proxyPort", "22");
			r=r+"1w";
			session1.connect();
			r=r+"2w";
			channel = session1.openChannel("sftp");
			r=r+"2w";
			channel.connect();
			r=r+"3w";
			sftpChannel = (ChannelSftp) channel;
			r=r+"4w";
System.out.println(r);
logger.info(r);
		} catch (JSchException e) {
//			e.printStackTrace();
			r=r+e.getMessage();
			logger.info("error "+r);
		}
		return r;

	}
	
	public void disconnect() {
		System.out.println("disconnecting...");
		sftpChannel.disconnect();
		channel.disconnect();
		session1.disconnect();
	}
	
	public String upload(String fileName, String remoteDir) {
		String res="";
		FileInputStream fis = null;
		String a=connect();
		res=a;
		try {
			// Change to output directory
			sftpChannel.cd(remoteDir);
			res="1";
			// Upload file
			File file = new File(fileName);
			fis = new FileInputStream(file);
			sftpChannel.put(fis, file.getName());

			fis.close();
			res=res+"2";
			System.out.println("File uploaded successfully - "+ file.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
		disconnect();
		return res;
	}
	
	public String  download(String fileName, String localDir) {
		
		File file= null;
		byte[] buffer = new byte[1024];
		BufferedInputStream bis;
		connect();
		try {
			// Change to output directory
			String cdDir = fileName.substring(0, fileName.lastIndexOf("/") + 1);
			sftpChannel.cd(cdDir);

			 file = new File(fileName);
			 
			bis = new BufferedInputStream(sftpChannel.get(file.getName()));
System.out.println(bis);

			File newFile = new File(localDir + "" + file.getName());
			
			// Download file
			OutputStream os = new FileOutputStream(newFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bis.close();
			bos.close();
			System.out.println("File downloaded successfully - "+ file.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
		disconnect();
		return file.getAbsolutePath();
	}

	public static void main(String[] args) {
		
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		String localPath = "D:\\DocTiger\\CRF - RP Termination.docx";
		String remotePath = "/home/ubuntu/uploaded-templates/";
		String SFservIP= bundle.getString("SFservIP");//35.221.183.246
		String SFservusername=bundle.getString("SFservusername");
			String 	SFservpass=bundle.getString("SFservpass");//B!zL3M786
			
			
		      System.out.println(("SFservIP "+SFservIP+" SFservusername "+ SFservusername+" SFservpass "+SFservpass));
		//UploadTemplateServer ftp = new UploadTemplateServer("35.188.238.145",22,"ubuntu","$DocTiger@123$");
		UploadTemplateServer ftp = new UploadTemplateServer(SFservIP,22,SFservusername,SFservpass);
		
	//	ftp.upload(localPath, remotePath);
		
	      String a=ftp.connect();
	      System.out.print(a);
//		String serv222path= "/home/vil/sling\\ tomcat/apache-tomcat-6.0.35/webapps/ROOT/SFTemplateLibrary/";
//		String servsavepah="D:\\docgenlocal";
//	ftp.download(serv222path+"DocTemplate.docx", servsavepah);

	}

}