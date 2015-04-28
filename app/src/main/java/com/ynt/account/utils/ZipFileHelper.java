package com.ynt.account.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileHelper {
	/**
	 * 对文件进行解压缩
	 * @param file
	 * @param unzipDir
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<File> unzipFile(File file, String unzipDir) throws Exception{
		if(file == null || !file.exists()) return null;
		ArrayList<File> unzipfiles = new ArrayList<File>();
		ZipFile zipfile = new ZipFile(file);
		Enumeration enumeration = zipfile.entries();
		ZipEntry entry = null;
		File unzipfile = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		while(enumeration.hasMoreElements()){
			entry = (ZipEntry) enumeration.nextElement();
			unzipfile = new File(unzipDir, entry.getName());
			try {
				in = new BufferedInputStream(zipfile.getInputStream(entry));
				out = new BufferedOutputStream(new FileOutputStream(unzipfile));
				int count = 0; 
				byte[] buffer = new byte[1024];
				while((count = in.read(buffer))>0){
					out.write(buffer, 0, count);
				}
				out.flush();
				unzipfiles.add(unzipfile);
			} finally {
				if(out != null)
					out.close();
				if(in != null)
					in.close();
			}
			
		}
		return unzipfiles;
	}
	
	/**
	 * 对文件进行压缩打包
	 * @param zipfilePath
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public static File zipFile(String zipfilePath, ArrayList<File> files) throws Exception{
		if(files == null || files.size() == 0) return null;
		File zipfile = new File(zipfilePath);
		ZipOutputStream zipout = null;
		try{
			zipout = new ZipOutputStream(new FileOutputStream(zipfile));
			ZipEntry zipEntry = null;
			BufferedInputStream in = null;
			for(File file: files){
				in = new BufferedInputStream(new FileInputStream(file));
				try{
					zipEntry = new ZipEntry(file.getName());
					zipout.putNextEntry(zipEntry);
					int count = 0;
					byte[] buffer = new byte[1024];
					while((count = in.read(buffer))>0){
						zipout.write(buffer, 0, count);
					}
					zipout.flush();
				} finally {
					if(in != null)
						in.close();
				}
			}
			return zipfile;
		} finally {
			if(zipout != null)
				zipout.close();
		}
	}
}
