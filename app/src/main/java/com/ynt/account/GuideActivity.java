package com.ynt.account;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.ynt.account.control.ViewPagerCompat;
import com.ynt.account.control.ViewPagerCompat.OnPageChangeListener;

public class GuideActivity extends Activity {
	private ViewPagerCompat viewPager = null;
	private int[] imageViewIds = new int[]{R.drawable.guide1, R.drawable.guide2};
	private ArrayList<ImageView> pageViews = null; 
	private Handler handler = null;
	private boolean isActionMove = false;
	private ImageView[] dotViews = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ViewGroup mainGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.guide, null);
		// 初始化pageViews
		initPageViews();
		// 初始化圆点
		initdot(mainGroup);
		// 初始化ViewPager
		initViewPager(mainGroup);
		// 初始化handler
		initHandler();
		
		setContentView(mainGroup);
	}
	
	private void initdot(ViewGroup mainGroup){
		ViewGroup group = (ViewGroup) mainGroup.findViewById(R.id.guidedot_g);
		dotViews = new ImageView[pageViews.size()];
		for(int i = 0; i<pageViews.size(); i++){
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(10, 10));
			dotViews[i] = imageView;
			
			if(i == 0){  // 默认第一个选中
				imageView.setBackgroundResource(R.drawable.circle_solid);
			} else {
				imageView.setBackgroundResource(R.drawable.circle_null);
			}
			
			group.addView(imageView);
		}
	}
	
	@SuppressLint("HandlerLeak")
	private void initHandler(){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0 && !isActionMove){
					Intent intent = new Intent();
					intent.setClass(GuideActivity.this, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
				}
				super.handleMessage(msg);
			}
		};
	}
	
	private void initPageViews(){
		pageViews = new ArrayList<ImageView>();
		
		LayoutInflater inflater = getLayoutInflater();
		ImageView imageView = null;
		for(int imageViewId: imageViewIds){
			imageView = (ImageView) inflater.inflate(R.layout.guide_image, null);
			imageView.setBackgroundResource(imageViewId);
			pageViews.add(imageView);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(viewPager.getCurrentItem() == pageViews.size() - 1){
			switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				isActionMove = false;
				break; 
			case MotionEvent.ACTION_MOVE:
				isActionMove = true;
				break;
			case MotionEvent.ACTION_UP:
				handler.sendEmptyMessage(0);
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private void initViewPager(ViewGroup mainGroup){
		viewPager = (ViewPagerCompat) mainGroup.findViewById(R.id.guidevp);
		viewPager.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return pageViews.size();
			}
			
			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPagerCompat)container).removeView(pageViews.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPagerCompat)container).addView(pageViews.get(position));
				return pageViews.get(position);
			}
		});	
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				for(int i = 0; i<dotViews.length; i++){
					if(i == arg0)
						dotViews[i].setBackgroundResource(R.drawable.circle_solid);
					else
						dotViews[i].setBackgroundResource(R.drawable.circle_null);
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
}

