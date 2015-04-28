package com.ynt.account.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SysMessageBean implements Parcelable{
	private int key = -1;
	private String sendtime;
	private String subject;
	private String content;
	private boolean isread;
	private String sysmsgkey;
	
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isIsread() {
		return isread;
	}
	public void setIsread(boolean isread) {
		this.isread = isread;
	}
	public String getSysmsgkey() {
		return sysmsgkey;
	}
	public void setSysmsgkey(String sysmsgkey) {
		this.sysmsgkey = sysmsgkey;
	}
	
	public static final Parcelable.Creator<SysMessageBean> CREATOR = new Creator<SysMessageBean>() {
		
		@Override
		public SysMessageBean[] newArray(int size) {
			return new SysMessageBean[size];
		}
		
		@Override
		public SysMessageBean createFromParcel(Parcel source) {
			SysMessageBean bean = new SysMessageBean();
			bean.key = source.readInt();
			bean.sendtime = source.readString();
			bean.content = source.readString();
			bean.subject = source.readString();
			bean.sysmsgkey = source.readString();
			boolean[] array = new boolean[1];
			source.readBooleanArray(array);
			bean.isread = array[0];
			return bean;
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(key);
		dest.writeString(sendtime);
		dest.writeString(content);
		dest.writeString(subject);
		dest.writeString(sysmsgkey);
		dest.writeBooleanArray(new boolean[]{isread});
	}
}
