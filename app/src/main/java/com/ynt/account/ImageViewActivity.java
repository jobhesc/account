package com.ynt.account;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.ynt.account.control.ViewPagerCompat;
import com.ynt.account.control.ViewPagerCompat.OnPageChangeListener;
import com.ynt.account.control.ZoomImageView;
import com.ynt.account.utils.ImageHelper;
import com.ynt.account.viewpage.transformer.ZoomOutSlideTransformer;

public class ImageViewActivity extends Activity implements OnPageChangeListener, OnClickListener, OnCheckedChangeListener {
	private ViewPagerCompat viewPager = null;
	private String[] imageUriArray = null;
	private ArrayList<String> selectedUris = null;
	private int imagePosition = -1;
	private Button btnReturn;
	private Button btnComplete;
	private CheckBox chbSelect;
	private TextView txtTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageview);
		
		btnReturn = (Button)findViewById(R.id.imageview_return);
		btnComplete = (Button)findViewById(R.id.imageview_complete);
		chbSelect = (CheckBox)findViewById(R.id.imageview_check);
		txtTitle = (TextView)findViewById(R.id.imageview_title);
		btnReturn.setOnClickListener(this);
		btnComplete.setOnClickListener(this);
		chbSelect.setOnCheckedChangeListener(this);
		
		chbSelect.setVisibility(isSelectMode()?View.VISIBLE:View.INVISIBLE);
		ViewGroup bottomVG = (ViewGroup)findViewById(R.id.imageview_bottom);
		bottomVG.setVisibility(isSelectMode()?View.VISIBLE:View.GONE);
		txtTitle.setVisibility(isSelectMode()?View.VISIBLE:View.INVISIBLE);

		// 接收传递参数
		imageUriArray = getIntent().getStringArrayExtra("ImageUris");
		imagePosition = getIntent().getIntExtra("Position", -1);
		selectedUris = getIntent().getStringArrayListExtra("SelImageUris");
		// 初始化ViewPager
		initViewPager();
	}
	
	private boolean isSelectMode(){
		return getIntent().getBooleanExtra("IsSelectMode", false);
	}
	
	protected Bitmap loadBitmap(String path){
		Uri uri = Uri.parse(path);
		return ImageHelper.loadBitmap(getContentResolver(), uri, 
					getResources().getDisplayMetrics().widthPixels, 
					getResources().getDisplayMetrics().widthPixels*getResources().getDisplayMetrics().heightPixels);

	}

	private void initViewPager() {

		viewPager = (ViewPagerCompat)findViewById(R.id.imageview_vp);
		viewPager.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return imageUriArray.length;
			}
			
			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				ZoomImageView zoomView = (ZoomImageView) object;
				zoomView.recycle();
				((ViewPagerCompat)container).removeView(zoomView);
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				Bitmap bitmap = loadBitmap(imageUriArray[position]);
				ZoomImageView zoomView = (ZoomImageView) getLayoutInflater().inflate(R.layout.imageview_image, null);
				((ViewPagerCompat)container).addView(zoomView);
				zoomView.setImageBitmap(bitmap);

				return zoomView;
			}
		});
		// 设置切换动画
		viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
		viewPager.setOnPageChangeListener(this);
		
		viewPager.setCurrentItem(imagePosition);  // 跳转到指定Uri的view上
	}
	
	private void setCompleteButtonText(){
		if(!isSelectMode()) return;
		String text = getResources().getString(R.string.imageview_complete);
		btnComplete.setText(String.format("%s(%d)", text,selectedUris.size()));
	}
	
	private void setTitleText(int position){
		txtTitle.setText(String.format("%d/%d", position, imageUriArray.length));
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		if(!isSelectMode()) return;
		String uri = imageUriArray[position];
		chbSelect.setChecked(selectedUris.contains(uri));
		
		setCompleteButtonText();
		setTitleText(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(!isSelectMode()) return;
		int position = viewPager.getCurrentItem();
		String uri = imageUriArray[position];
		if(isChecked)
			selectedUris.add(uri);
		else
			selectedUris.remove(uri);
		setCompleteButtonText();
	}
	
	@Override
	public void onBackPressed() {
		onReturn();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		if(v == btnReturn){
			onReturn();
		} else if(v == btnComplete){
			onComplete();
		}
	}
	
	private void onReturn(){
		if(isSelectMode()){
			Intent intent = new Intent();
			intent.putExtra("SelImageUris", selectedUris);
			setResult(RESULT_OK, intent);
		}
		finish();
	}
	
	private void onComplete(){
		if(!isSelectMode()) return;
		Intent intent = new Intent(this, UploadActivity.class);
		intent.putExtra("SelImageUris", selectedUris);
		// FLAG_ACTIVITY_CLEAR_TOP表示把UploadActivity以上的Activity全部清除掉，此时如果UploadActivity
		// 没有设置任何lauchMode，那么就会创建UploadActivity实例，如果设置了lauchMode或者flag为FLAG_ACTIVITY_SINGLE_TOP
		// 则不会创建实例，而是走Activity的OnNewIntent方法
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
}
