package com.ynt.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.control.PagerSlidingTabStrip;
import com.ynt.account.fragment.ImgMessageFragment;
import com.ynt.account.fragment.SysMessageFragment;

public class MessageActivity extends BaseActivity  {
	private SysMessageFragment sysMsgFragment = null;
	private ImgMessageFragment imgMsgFragment = null;
	private PagerSlidingTabStrip tabs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		
		getRightButton().setVisibility(View.INVISIBLE);
		
		tabs = (PagerSlidingTabStrip)findViewById(R.id.msg_tabs);
		ViewPager viewPager = (ViewPager) findViewById(R.id.msg_pager);
		viewPager.setAdapter(new MessageFragmentAdapter(getSupportFragmentManager()));
		tabs.setViewPager(viewPager);
	}
	
	private class MessageFragmentAdapter extends FragmentPagerAdapter{

		private CharSequence[] pageTitles = null;
		
		public MessageFragmentAdapter(FragmentManager fm) {
			super(fm);
			
			pageTitles = new CharSequence[]{
					getResources().getText(R.string.tab_img_message),
					getResources().getText(R.string.tab_sys_message)};
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return pageTitles[position];
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case 0:
				if(imgMsgFragment == null){
					imgMsgFragment = new ImgMessageFragment();
				}
				return imgMsgFragment;
			case 1:
				if(sysMsgFragment == null){
					sysMsgFragment = new SysMessageFragment();
				}
				return sysMsgFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return pageTitles.length;
		}
		
	}
}
