package com.ynt.account;

import com.ynt.account.base.BaseActivity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {
	private Button btnUpload = null;
	private Button btnHistory = null;
	private Button btnReport = null;
	private Button btnMessage = null;
	private Button btnAbout = null;
	private int hitBackCount=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		getLeftButton().setVisibility(View.INVISIBLE);
		getRightButton().setVisibility(View.INVISIBLE);
		
		btnUpload = (Button)findViewById(R.id.main_upload);
		btnHistory = (Button)findViewById(R.id.main_history);
		btnReport = (Button)findViewById(R.id.main_report);
		btnMessage = (Button)findViewById(R.id.main_message);
		btnAbout = (Button)findViewById(R.id.main_about);
		btnUpload.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
		btnReport.setOnClickListener(this);
		btnMessage.setOnClickListener(this);
		btnAbout.setOnClickListener(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(hitBackCount == 0){
				Toast.makeText(this, "再按一次返回键关闭程序", Toast.LENGTH_SHORT).show();
				hitBackCount = 1;
				
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hitBackCount = 0;
					}
				}, 2000);
				
				return true;
			} else {
				finish();
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(btnUpload)){
			startActivity(new Intent(this, UploadActivity.class));
		} else if(v.equals(btnHistory)){
			startActivity(new Intent(this, HistoryActivity.class));
		} else if(v.equals(btnReport)){
			startActivity(new Intent(this, ReportMainActivity.class));
		} else if(v.equals(btnMessage)){
			startActivity(new Intent(this, MessageActivity.class));
		} else if(v.equals(btnAbout)){
			
		}
	}
}
