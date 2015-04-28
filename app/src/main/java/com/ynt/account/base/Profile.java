package com.ynt.account.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Profile {
	private static final String PROFILENAME="profile";
	private static Profile instance = null;
	private SharedPreferences pref = null;
	private Editor editor = null;
	private Profile(Application app){
		pref = app.getApplicationContext().getSharedPreferences(PROFILENAME, Context.MODE_PRIVATE);
		editor = pref.edit();
	}
	
	/**
	 * 是否需要显示引导界面
	 * @return
	 */
	public boolean isNeedGuide(){
		return pref.getBoolean("needGuide", true);
	}
	
	/**
	 * 设置需要引导界面的值
	 * @param needGuide
	 */
	public void setNeedGuide(boolean needGuide){
		editor.putBoolean("needGuide", needGuide);
		editor.commit();
	}
	/**
	 * 获取上一次登陆用户
	 * @return
	 */
	public String getLastLoginUser(){
		return pref.getString("lastLoginUser", "");
	}
	/**
	 * 保存上一次登陆用户
	 * @param lastLoginUser
	 */
	public void setLastLoginUser(String lastLoginUser){
		editor.putString("lastLoginUser", lastLoginUser);
		editor.commit();		
	}
	
	public SharedPreferences getPref(){
		return pref;
	}
	
	public synchronized static Profile getInstance(Application app){
		if(instance == null)
			instance = new Profile(app);
		return instance;
	}
}
