package com.ynt.account.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;

import com.ynt.account.utils.ImageHelper;
import com.ynt.account.utils.ZipFileHelper;

@SuppressLint("DefaultLocale")
public class ImageFileManager {
	private class ImageFileItem{
		ImageFileItem(Uri uri, String filename){
			this.uri = uri;
			this.file=new File(getCacheDir(), filename);
		}
		
		public ImageFileItem(File file) {
			this.file =file;
		}
		
		private Uri uri;
		private File file;
	}
	
	private static final int IMAGEMINLEN = 600;   // 图片最小宽高
	private static final int IMAGEMAXPIXELS = 600*400;  // 图片最大像素
	private static final String IMAGEDIRNAME="YntAccount";    // 图片文件夹名称
	private static final int IMAGECOMPRESSQUALITY=80;   // 图片压缩比例(0-100)
	private Context context = null;
	private ArrayList<ImageFileItem> fileItems = new ArrayList<ImageFileItem>();
	private HashMap<String, String> unzipDirMap = new HashMap<String, String>();  
	
	public ImageFileManager(Context context){
		this.context = context;
	}
	
	public void addFileItem(Uri uri, String filename){
		fileItems.add(new ImageFileItem(uri, filename));
	}
	
	public void addFileItem(File file){
		fileItems.add(new ImageFileItem(file));
	}
	
	public void clear(){
		fileItems.clear();
	}
	
	public int size(){
		return fileItems.size();
	}
	
	public File get(int index){
		ImageFileItem fileItem = fileItems.get(index);
		if(fileItem != null)
			return fileItem.file;
		return null;
	}
	
	public File find(String filename){
		for(ImageFileItem fileItem: fileItems){
			if(fileItem.file.getName().equalsIgnoreCase(filename))
				return fileItem.file;
		}
		return null;
	}
	
	public File load(String filename) throws Exception{
		File zipfile = new File(getZipfilePath(filename));
		if(!zipfile.exists())
			throw new Exception("文件名" + filename + "在系统中不存在");
		ArrayList<File> files = ZipFileHelper.unzipFile(zipfile, getUnzipDir(filename));
		fileItems.clear();
		if(files != null){
			for(File file: files){
				fileItems.add(new ImageFileItem(file));
			}
		}
		return zipfile;
	}
	
	/**
	 * 保存文件
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public File save(String filename) throws Exception{
		ArrayList<File> files = new ArrayList<File>();
		try {
			for(ImageFileItem fileItem: fileItems){
				// 从uri中获取文件并保存到临时目录下
				File file = null;
				if(fileItem.file != null && fileItem.file.exists())
					file = fileItem.file;
				else
					file = writeToCacheDir(fileItem);
				if(file != null)
					files.add(file);
			}
			// 打包压缩
			String zipfilePath = getZipfilePath(filename);
			File zipfile = ZipFileHelper.zipFile(zipfilePath, files);
			return zipfile;
		} finally {
			// 删除临时文件
			for(File file: files){
				file.delete();
			}
		}
	}
	
	private String getUnzipDir(String filename){
		String f = filename.toLowerCase().trim();
		if(!unzipDirMap.containsKey(f)){
			String unzipDir = getCacheDir() + File.separator + filename;
			File dir = new File(unzipDir);
			dir.mkdir();
			unzipDirMap.put(f, unzipDir);
		}
		return unzipDirMap.get(f);
	}
	
	/**
	 * 清空所有缓存文件
	 */
	public void clearCacheFiles(){
		for(String key: unzipDirMap.keySet()){
			String unzipDir = unzipDirMap.get(key);
			File dir = new File(unzipDir);
			recurDelfiles(dir);
		}
		unzipDirMap.clear();
	}
	
	private void recurDelfiles(File file){
		if(file.isDirectory()){
			File[] childfiles = file.listFiles();
			if(childfiles != null){
				for(int i=childfiles.length-1; i>=0; i--)
					recurDelfiles(childfiles[i]);
			}
		} else if(file.isFile()){
			file.delete();
		}
	}
	
	/**
	 * 根据文件名获取文件路径
	 * @param filename
	 * @return
	 */
	private String getZipfilePath(String filename){
		return getBaseDir() + File.separator + forceZipfileExt(filename);
	}
	
	/**
	 * 文件名后面加上zip扩展名
	 * @param filename
	 * @return
	 */
	private String forceZipfileExt(String filename){
		String f = filename.toLowerCase();
		if(f.endsWith(".zip")) return filename;
		return filename + ".zip";
	}
	
	/**
	 * 从uri中获取到图片文件，并保存到临时目录中
	 * @param fileItem
	 * @return
	 * @throws Exception
	 */
	private File writeToCacheDir(ImageFileItem fileItem) throws Exception{
		File file = fileItem.file;
		FileOutputStream outstream = null;
		Bitmap bitmap = null;
		try {
			outstream = new FileOutputStream(file);
			bitmap = ImageHelper.loadBitmap(context.getContentResolver(), fileItem.uri, IMAGEMINLEN, IMAGEMAXPIXELS);
			bitmap.compress(CompressFormat.JPEG, IMAGECOMPRESSQUALITY, outstream);
			outstream.flush();
			return file;
		} finally {
			if(bitmap != null)
				bitmap.recycle();
			if(outstream != null)
				outstream.close();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		clearCacheFiles();
		super.finalize();
	}
	
	/**
	 * 判断sd卡是否可用
	 * @return
	 */
	private boolean isExternalStorageUsable(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
				!Environment.isExternalStorageRemovable();
	}
	
	private File getBaseDir(){
		if(isExternalStorageUsable()){   // sd卡可用
			File dir = new File(Environment.getExternalStorageDirectory(), IMAGEDIRNAME);
			dir.mkdir();
			return dir;
		} else {
			return context.getFilesDir();
		}
	}
	
	private File getCacheDir(){
		if(isExternalStorageUsable()){
			return context.getExternalCacheDir();
		} else {
			return context.getCacheDir();
		}
	}
}
