package com.ynt.account.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ImgMessageBean implements Parcelable{
	private int key = -1;
	private String sendtime;
	private String content;
	private String grouppaths;
	private String badpaths;
	private boolean isread = false;
	private boolean isupload = false;
	private String imgmsgkey;
	private String newpaths;
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getSendtime() {
		return sendtime;
	}
	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String[] getGrouppathArray(){
		if(grouppaths == null || grouppaths.equals("")) return null;
		return grouppaths.split(",");
	}
	public String getGrouppaths() {
		return grouppaths;
	}
	public void setGrouppaths(String grouppaths) {
		this.grouppaths = grouppaths;
	}
	public String[] getBadpathArray(){
		if(badpaths == null || badpaths.equals("")) return null;
		return badpaths.split(",");
	}
	public String getBadpaths() {
		return badpaths;
	}
	public void setBadpaths(String badpaths) {
		this.badpaths = badpaths;
	}
	public boolean isIsread() {
		return isread;
	}
	public void setIsread(boolean isread) {
		this.isread = isread;
	}
	public boolean isIsupload() {
		return isupload;
	}
	public void setIsupload(boolean isupload) {
		this.isupload = isupload;
	}
	public String getImgmsgkey() {
		return imgmsgkey;
	}
	public void setImgmsgkey(String imgmsgkey) {
		this.imgmsgkey = imgmsgkey;
	}
	public String[] getNewpathArray(){
		if(newpaths == null || newpaths.equals("")) return null;
		return newpaths.split(",");
	}	
	public String getNewpaths() {
		return newpaths;
	}
	public void setNewpaths(String newpaths) {
		this.newpaths = newpaths;
	}
	
	public static final Parcelable.Creator<ImgMessageBean> CREATOR = new Creator<ImgMessageBean>() {
		
		@Override
		public ImgMessageBean[] newArray(int size) {
			return new ImgMessageBean[size];
		}
		
		@Override
		public ImgMessageBean createFromParcel(Parcel source) {
			ImgMessageBean bean = new ImgMessageBean();
			bean.key = source.readInt();
			bean.sendtime = source.readString();
			bean.content = source.readString();
			bean.grouppaths = source.readString();
			bean.badpaths = source.readString();
			bean.newpaths = source.readString();
			bean.imgmsgkey = source.readString();
			boolean[] array = new boolean[2];
			source.readBooleanArray(array);
			bean.isread = array[0];
			bean.isupload=array[1];
			return bean;
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(key);
		dest.writeString(sendtime);
		dest.writeString(content);
		dest.writeString(grouppaths);
		dest.writeString(badpaths);
		dest.writeString(newpaths);
		dest.writeString(imgmsgkey);
		dest.writeBooleanArray(new boolean[]{isread, isupload});
	}
}
