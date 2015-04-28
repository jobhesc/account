package com.ynt.account;

import com.ynt.account.base.Profile;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(Profile.getInstance(getApplication()).isNeedGuide()){
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, GuideActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
					
					Profile.getInstance(getApplication()).setNeedGuide(false);
				} else {
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, LoginActivity.class);
					startActivity(intent);
				}
				
				finish();
			}
		}, 3000);
	}
}
