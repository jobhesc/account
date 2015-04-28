package com.ynt.account.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SqlMessagePersistence {
	public static final String DBNAME="message.db";
	private DBHelper dbHelper = null;
	
	public SqlMessagePersistence(Context context){
		dbHelper = new DBHelper(context);
	}
	
	private class DBHelper extends SQLiteOpenHelper{
		DBHelper(Context context){
			super(context, DBNAME, null, 3);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// 系统公告
			db.execSQL("create table sysmsg (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"sendtime VARCHAR(20)," +
					"subject NVARCHAR(100)," +
					"content NVARCHAR(1000)," +
					"isread INTEGER," +
					"isdel INTEGER," +
					"sysmsgkey VARCHAR(20))");
			// 重拍消息
			db.execSQL("create table imgmsg (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"sendtime VARCHAR(20)," +
					"content NVARCHAR(1000)," +
					"grouppaths NVARCHAR(4000)," +
					"badpaths NVARCHAR(4000)," +
					"newpaths NVARCHAR(4000)," +
					"isread INTEGER," +
					"isupload INTEGER," +
					"imgmsgkey VARCHAR(20))");			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(oldVersion == 1 && newVersion==2)
				db.execSQL("alter table imgmsg add newpaths NVARCHAR(4000)");
			if(newVersion == 3)
				db.execSQL("alter table sysmsg add isdel INTEGER");
		}
	}
	
	private <T> T queryValue(String sql, String[] selectionArgs){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		Object value = null;
		try{
			cursor = db.rawQuery(sql, selectionArgs);
			if(cursor.moveToFirst()){
				int type = cursor.getType(0);
				if(type == Cursor.FIELD_TYPE_NULL)
					return null;
				else if(type == Cursor.FIELD_TYPE_FLOAT)
					return (T)Float.valueOf(cursor.getFloat(0));
				else if(type == Cursor.FIELD_TYPE_BLOB)
					return (T)cursor.getBlob(0);
				else if(type == Cursor.FIELD_TYPE_INTEGER)
					return (T)Integer.valueOf(cursor.getInt(0));
				else if(type == Cursor.FIELD_TYPE_STRING)
					return (T)cursor.getString(0);
				else 
					return (T)cursor.getString(0);
			}
			return null;
		} finally {
			if(cursor != null)
				cursor.close();
			db.close();
		}
	}
	
	/**
	 * 获取系统公告数量
	 * @return
	 */
	public int getSysMessageCount(){
		return queryValue("select count(*) from sysmsg", null);
	}
	
	/**
	 * 获取重拍图片消息数量
	 * @return
	 */
	public int getImgMessageCount(){
		return queryValue("select count(*) from imgmsg", null);
	}
	
	/**
	 * 标记系统公告已读
	 * @param id
	 */
	public void markSysMsgReaded(int id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("isread", 1);
		db.update("sysmsg", c, "_id=?", new String[]{id+""});
	}
	
	public void resetNewImgPath(int id, String newPaths){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("newpaths", newPaths);
		db.update("imgmsg", c, "_id=?", new String[]{id+""});
	}
	
	/**
	 * 标记重拍图片消息已读
	 * @param id
	 */
	public void markImgMsgReaded(int id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("isread", 1);
		db.update("imgmsg", c, "_id=?", new String[]{id+""});
	}
	
	
	/**
	 * 标记重拍图片消息已上传
	 * @param id
	 */
	public void markImgMsgUploaded(int id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("isupload", 1);
		db.update("imgmsg", c, "_id=?", new String[]{id+""});
	}
	
	/**
	 * 删除重拍图片消息
	 * @param id
	 */
	public void delImgMessage(int id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("imgmsg", "_id=?", new String[]{id + ""});
		
	}
	
	/**
	 * 删除系统消息
	 * @param id
	 */
	public void delSysMessage(int id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues c = new ContentValues();
		c.put("isdel", 1);
		db.update("sysmsg", c, "_id=?", new String[]{id+""});
	}
	
	/**
	 * 插入系统公告到数据库
	 * @param beans
	 */
	public void InsertSysMessages(SysMessageBean[] beans){
		if(beans == null || beans.length == 0) return;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try{
			for(SysMessageBean bean: beans){
				ContentValues c = new ContentValues();
				c.put("sendtime", bean.getSendtime());
				c.put("subject", bean.getSubject());
				c.put("content", bean.getContent());
				c.put("isread", bean.isIsread()?1:0);
				c.put("isdel", 0);
				c.put("sysmsgkey", bean.getSysmsgkey());
				db.insert("sysmsg", null, c);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	/**
	 * 插入重拍图片消息到数据库
	 * @param beans
	 */
	public void InsertImgMessages(ImgMessageBean[] beans){
		if(beans == null || beans.length == 0) return;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try{
			for(ImgMessageBean bean: beans){
				ContentValues c = new ContentValues();
				c.put("sendtime", bean.getSendtime());
				c.put("content", bean.getContent());
				c.put("grouppaths", bean.getGrouppaths());
				c.put("badpaths", bean.getBadpaths());
				c.put("isread", bean.isIsread()?1:0);
				c.put("isupload", bean.isIsupload()?1:0);
				c.put("imgmsgkey", bean.getImgmsgkey());
				c.put("newpaths", bean.getNewpaths());
				db.insert("imgmsg", null, c);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public SysMessageBean readSysMessage(Cursor cursor){
		SysMessageBean bean = new SysMessageBean();
		bean.setKey(cursor.getInt(0));
		bean.setSendtime(cursor.getString(1));
		bean.setSubject(cursor.getString(2));
		bean.setContent(cursor.getString(3));
		bean.setIsread(cursor.getInt(4)==0?false:true);
		bean.setSysmsgkey(cursor.getString(5));
		return bean;
	}
	
	public Cursor readSysMessagesForCursor(int beginIndex, int endIndex){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return readSysMessagesForCursor(db, beginIndex, endIndex, false);
	}
	
	private Cursor readSysMessagesForCursor(SQLiteDatabase db, int beginIndex, int endIndex, boolean containDelRec){
		String sql = String.format("select _id, sendtime, subject, content, isread, sysmsgkey " +
				"from sysmsg where %s order by sendtime desc limit %d,%d",
				containDelRec?"1=1":"ifnull(isdel,0)=0", beginIndex, endIndex);
		return db.rawQuery(sql, null);	
	}
	
	/**
	 * 查询系统公告
	 * @param beginIndex 查询开始索引
	 * @param endIndex 查询结束索引
	 * @return
	 */
	public SysMessageBean[] readSysMessages(int beginIndex, int endIndex, boolean containDelRec){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try{
			cursor = readSysMessagesForCursor(db, beginIndex, endIndex, containDelRec);
			if(cursor.getCount() == 0) return null;
			
			SysMessageBean[] beans = new SysMessageBean[cursor.getCount()];
			int index = 0;
			while(cursor.moveToNext()){
				beans[index++] = readSysMessage(cursor);
			}
			
			return beans;
		} finally {
			if(cursor != null)
				cursor.close();
			db.close();
		}
	}
	
	public ImgMessageBean readImgMessage(Cursor cursor){
		ImgMessageBean bean = new ImgMessageBean();
		bean.setKey(cursor.getInt(0));
		bean.setSendtime(cursor.getString(1));
		bean.setContent(cursor.getString(2));
		bean.setGrouppaths(cursor.getString(3));
		bean.setBadpaths(cursor.getString(4));
		bean.setIsread(cursor.getInt(5)==0?false:true);
		bean.setIsupload(cursor.getInt(6)==0?false:true);
		bean.setImgmsgkey(cursor.getString(7));	
		bean.setNewpaths(cursor.getString(8));
		return bean;
	}
	
	public Cursor readImgMessagesForCursor(int beginIndex, int endIndex){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return readImgMessagesForCursor(db, beginIndex, endIndex);
	}
	
	private Cursor readImgMessagesForCursor(SQLiteDatabase db, int beginIndex, int endIndex){
		String sql = "select _id, sendtime, content, grouppaths, badpaths, isread, isupload, imgmsgkey, newpaths " +
				"from imgmsg order by sendtime desc limit ?,?";
		return db.rawQuery(sql, new String[]{beginIndex + "", endIndex + ""});
	}
	
	/**
	 * 查询重拍图片消息
	 * @param beginIndex 查询开始索引
	 * @param endIndex 查询结束索引
	 * @return
	 */
	public ImgMessageBean[] readImgMessages(int beginIndex, int endIndex){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try{
			cursor = readImgMessagesForCursor(db, beginIndex, endIndex);
			if(cursor.getCount() == 0) return null;
			
			ImgMessageBean[] beans = new ImgMessageBean[cursor.getCount()];
			int index = 0;
			while(cursor.moveToNext()){
				beans[index++] = readImgMessage(cursor);
			}
			
			return beans;
		} finally {
			if(cursor != null)
				cursor.close();
			db.close();
		}
	}
}
