package com.ynt.account.base;

import com.ynt.account.LoginActivity;
import com.ynt.account.MainActivity;
import com.ynt.account.request.ServerRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.inputmethod.InputMethodManager;


public class WaitingActor {
	/**
	 * 隐藏软键盘
	 * @param activity
	 */
	private static void hideSoftInput(Activity activity){
		if(activity.getCurrentFocus() == null) return;
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static void run(Activity activity, String waitingMsg, final Runnable r){
		// 隐藏软键盘
		hideSoftInput(activity);
		try {
			Thread.sleep(100);  // 等待隐藏软键盘
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 显示loading
		WaitingDialog.show(activity, waitingMsg);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					r.run();
					WaitingDialog.dismiss();
				} catch(Exception e){
					e.printStackTrace();
					WaitingDialog.dismiss();
				}
			}
		}).start();
	}
}
