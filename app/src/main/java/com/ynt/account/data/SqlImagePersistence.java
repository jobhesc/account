package com.ynt.account.data;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.ynt.account.data.ImageModel.ImageGroup;
import com.ynt.account.data.ImageModel.ImageItem;

public class SqlImagePersistence implements IImagePersistence {
	public static final String DBNAME="upload.db";
	
	private class DBHelper extends SQLiteOpenHelper{
		DBHelper(Context context){
			super(context, DBNAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("create table image (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"groupnum INTEGER," +
					"groupname NVARCHAR(50)," +
					"busidate VARCHAR(20)," +
					"uploadlot VARCHAR(50)," +
					"uploadon VARCHAR(20)," +
					"uploadby VARCHAR(20)," +
					"imagename VARCHAR(20)," +
					"imageuri VARCHAR(200))");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
	
	private DBHelper dbHelper = null;
	private ImageFileManager fileManager = null;
	
	public SqlImagePersistence(Context context){
		dbHelper = new DBHelper(context);
		fileManager = new ImageFileManager(context);
	}
	
	@Override
	public ImageModel[] load(String filter) throws Exception {
		// 从数据库装载数据
		ImageModel[] models = loadFromDB(filter);
		if(models == null || models.length == 0) return null;
		// 从文件中装载数据
		loadFromFiles(models);
		
		return models;
	}
	
	/**
	 * 从数据库装载数据
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	private ImageModel[] loadFromDB(String filter) throws Exception {
		if(filter == null || filter.equals(""))
			filter = "1=1";
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			db = dbHelper.getReadableDatabase();
			c = db.rawQuery("select groupnum, groupname,busidate, uploadlot, uploadon, uploadby, imagename, imageuri from image where " + filter, null);
			HashMap<String, ImageModel> modelMap = new HashMap<String, ImageModel>();
			ImageModel model = null;
			while(c.moveToNext()){
				String uploadlot = c.getString(3);
				if(!modelMap.containsKey(uploadlot)){
					model = new ImageModel();
					model.setUploadlot(uploadlot);
					model.setUploadOn(c.getString(4));
					model.setUploadBy(c.getString(5));
					modelMap.put(uploadlot, model);
				} else {
					model = modelMap.get(uploadlot);
				}
				
				ImageGroup group = new ImageGroup();
				group.setGroupNum(c.getInt(0));
				group.setGroupName(c.getString(1));
				group.setBusidate(c.getString(2));
				
				ImageItem item = new ImageItem();
				item.setItemKind(ImageModel.ITEMKIND_IMAGE);
				item.setImageName(c.getString(6));
				item.setImageUri(c.getString(7));
				
				int index =model.indexOfGroup(group);
				if(index<0){
					model.addGroup(group);
					group.addImage(item);
				} else {
					group = model.get(index);
					group.addImage(item);
				}
				model.acceptChanged();
			}
			
			if(modelMap.size() == 0) return null;
			ImageModel[] models = new ImageModel[modelMap.size()];
			int index = 0;
			for(String key: modelMap.keySet())
				models[index++] = modelMap.get(key);
			return models;
		} finally {
			if(c != null)
				c.close();
			if(db != null)
				db.close();
		}
	}
	
	/**
	 * 从文件中装载数据
	 * @param models
	 * @throws Exception
	 */
	private void loadFromFiles(ImageModel[] models) throws Exception{
		if(models == null || models.length == 0) return;
		for(ImageModel model: models){
			File file = fileManager.load(model.getUploadlot());
			model.setFile(file);
			setModelFiles(model);
		}
	}
	
	private void setModelFiles(ImageModel model) throws Exception{
		for(int i=0; i<model.size(); i++){
			ImageGroup group = model.get(i);
			for(int j =0; j<group.size(); j++){
				ImageItem item = group.getImage(j);
				File file = fileManager.find(item.getImageName());
				if(file == null || !file.exists())
					throw new Exception("文件" + item.getImageName() + "在系统中不存在！");
				
				item.setImageFile(file);
			}
		}
	}

	@Override
	public void Save(ImageModel[] models) throws Exception {
		if(models == null || models.length == 0) return;
		// 把数据保存到数据库
		saveToDB(models);
		// 保存文件
		SaveToFiles(models);
	}
	
	private void SaveToFiles(ImageModel[] models) throws Exception{
		if(models == null || models.length == 0) return;
		for(ImageModel model: models){
			SaveToFile(model);
		}
	}
	
	private void SaveToFile(ImageModel model) throws Exception{
		fileManager.clear();
		for(int i = 0; i<model.size(); i++){
			ImageGroup group = model.get(i);
			for(int j=0; j<group.size(); j++){
				ImageItem item = group.getImage(j);
				if(item.getItemKind() == ImageModel.ITEMKIND_BUTTON) continue;
				
				if(item.getImageFile() != null && item.getImageFile().exists()){
					fileManager.addFileItem(item.getImageFile());
				} else {
					fileManager.addFileItem(Uri.parse(item.getImageUri()), item.getImageName());
					item.setImageFile(fileManager.find(item.getImageName()));
				}
			}
		}
		File file = fileManager.save(model.getUploadlot());
		model.setFile(file);
	}

	/**
	 * 把数据保存到数据库
	 * @param models
	 */
	private void saveToDB(ImageModel[] models){
		if(models == null || models.length == 0) return;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for(int i = 0; i<models.length; i++) {
				SaveToDB(db, models[i]);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	private void SaveToDB(SQLiteDatabase db, ImageModel model){
		for(int i = 0; i<model.size(); i++) {
			ImageGroup group = model.get(i);
			Vector<ImageItem> changedItems = group.getChangedItems();
			if(changedItems.size() == 0) continue;
			for(ImageItem changedItem: changedItems){
				if(changedItem.getItemState() == ImageModel.ITEMSTATE_NEW){
					insertImage(db, model, group, changedItem);
				} else if(changedItem.getItemState() == ImageModel.ITEMSTATE_DEL){
					deleteImage(db, model, group, changedItem);
				}
			}
		}
		model.acceptChanged();
	}
	
	private void insertImage(SQLiteDatabase db, ImageModel model, ImageGroup group, ImageItem imageItem){
		ContentValues cv = new ContentValues();
		cv.put("groupnum", group.getGroupNum());
		cv.put("groupname", group.getGroupName());
		cv.put("busidate", group.getBusidate());
		cv.put("uploadlot", model.getUploadlot());
		cv.put("uploadon", model.getUploadOn());
		cv.put("uploadby", model.getUploadBy());
		cv.put("imagename", imageItem.getImageName());
		cv.put("imageuri", imageItem.getImageUri());
		
		db.insert("image", null, cv);
	}
	
	private void deleteImage(SQLiteDatabase db, ImageModel model, ImageGroup group, ImageItem imageItem){
		db.delete("image", "uploadlot=? and groupname=? and imagename=?", new String[]{model.getUploadlot(), group.getGroupName(), imageItem.getImageName()});
	}

}
