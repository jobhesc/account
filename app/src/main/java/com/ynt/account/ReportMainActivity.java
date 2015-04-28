package com.ynt.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.ynt.account.base.BaseActivity;

public class ReportMainActivity extends BaseActivity implements OnClickListener {
	private ImageView zcfzView;
	private ImageView lrbView;
	private ImageView swbbView;
	private ImageView xjllView;
	private ImageView mxbView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_main);
		
		zcfzView = (ImageView)findViewById(R.id.report_zcfz);
		zcfzView.setOnClickListener(this);
		lrbView = (ImageView)findViewById(R.id.report_lrb);
		lrbView.setOnClickListener(this);
		swbbView = (ImageView)findViewById(R.id.report_swbb);
		swbbView.setOnClickListener(this);
		xjllView = (ImageView)findViewById(R.id.report_xjll);
		xjllView.setOnClickListener(this);
		mxbView = (ImageView)findViewById(R.id.report_mxb);
		mxbView.setOnClickListener(this);
		
		getRightButton().setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		if(v == zcfzView){
			startActivity(new Intent(this, ReportZCFZActivity.class));
		} else if(v == lrbView){
			startActivity(new Intent(this, ReportLRBActivity.class));
		} else if(v == swbbView){
			startActivity(new Intent(this, ReportSWBBActivity.class));
		} else if(v == xjllView){
			startActivity(new Intent(this, ReportXjllActivity.class));
		} else if(v == mxbView){
			startActivity(new Intent(this, ReportMXBActivity.class));
		}
		
	}
}
