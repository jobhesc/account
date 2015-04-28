package com.ynt.account;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.control.AdvanceImageView;
import com.ynt.account.control.AdvanceImageView.OnCheckListner;
import com.ynt.account.control.AdvanceImageView.OnTouchImageListener;
import com.ynt.account.utils.ImageHelper;

public class ImageSelActivity extends BaseActivity implements LoaderCallbacks<Cursor>, ViewBinder, OnScrollListener, OnCheckListner, OnTouchImageListener, OnClickListener {
	private static final int IMAGEVIEW_REQUESTCODE=1;
    private static final int PHOTO_GRAPH = 2;// 拍照
    
	private GridView gvimage;
	private SimpleCursorAdapter adapter;
	// 需要取到的图片库ContentProvider字段集合，该集合中必须包含_ID字段，因为使用SimpleCursorAdapter时，会使用
	// _ID字段作为每一项的ID，即SimpleCursorAdapter会重写BaseAdapter的getItemId返回该id，这样在点击等事件中
	// 就能获取该id，比如onItemClick参数中的id，就是通过getItemId获取的
	private static final String[] STORE_IMAGES = new String[]{MediaStore.Images.Media._ID};
	private LruCache<Uri, Bitmap> imgCache = null;
	private int imgSize = 0;
	private ArrayList<BitmapLoadTask> taskList = new ArrayList<BitmapLoadTask>();
	private int mFirstVisibleItem = -1;
	private int mVisibleItemCount = 0;
	private HashMap<Integer, CheckableUri> uriMap = new HashMap<Integer, CheckableUri>();
	private boolean mFirstLoad = false;
	private int selectedImageSize = 0;
	private Button btnComplete = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagesel);

		adapter = new SimpleCursorAdapter(this, R.layout.imagesel_gridview, null, STORE_IMAGES, new int[]{R.id.imagesel_grid_image}, 0) {
			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				View containView = super.newView(context, cursor, parent);
				AdvanceImageView view = (AdvanceImageView) containView.findViewById(R.id.imagesel_grid_image);
				view.setOnCheckListener(ImageSelActivity.this);
				view.setOnTouchImageListener(ImageSelActivity.this);
				return containView;
			}
		};
		adapter.setViewBinder(this);
		// 初始化loader
		getSupportLoaderManager().initLoader(0, null, this);
		
		gvimage = (GridView)findViewById(R.id.imagesel_gridview);
		gvimage.setAdapter(adapter);
		gvimage.setOnScrollListener(this);
		
		btnComplete = (Button)findViewById(R.id.imagesel_complete);
		btnComplete.setOnClickListener(this);
		
		imgSize = getResources().getDimensionPixelSize(R.dimen.image_size);

		// 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。   
	    // LruCache通过构造函数传入缓存值，以KB为单位。   
	    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
	    // 使用最大可用内存值的1/4作为缓存的大小。   
	    int cacheSize = maxMemory / 4;
		
		imgCache = new LruCache<Uri, Bitmap>(cacheSize){
			protected int sizeOf(Uri key, Bitmap bitmap) {
				// 重写此方法来衡量每张图片的大小，通过cacheSize和sizeOf()即可计算缓存可以存储的 图片数量；
				// 默认该方法为1，即以传入的cacheSize作为缓存的图片数量
				
				// 经研究bitmap.getByteCount要求的API版本过高，在我的android2.3.4中报错，修改为bitmap.getRowBytes()*bitmap.getHeight()
				// 1、getRowBytes：Since API Level 1，用于计算位图每一行所占用的内存字节数。
				// 2、getByteCount：Since API Level 12，用于计算位图所占用的内存字节数。
				// 经实测发现：getByteCount() = getRowBytes() * getHeight()，也就是说位图所占用的内存空间数等于位图的每一行所占用的空间数乘以位图的行数
				return bitmap.getRowBytes()*bitmap.getHeight()/1024;
//	            return bitmap.getByteCount() / 1024;
			};
			
			@Override
			protected void entryRemoved(boolean evicted, Uri key,
					Bitmap oldValue, Bitmap newValue) {
				if(oldValue != null && !oldValue.isRecycled())
					oldValue.recycle();   // 从lrucache移除图片后，对图片进行回收
			}
		};

		getLeftButton().setText(R.string.actionbar_left);
		getRightButton().setText(R.string.actionbar_photo);
		setCompleteButtonText();
	}
	
	private void setCompleteButtonText(){
		String text = getResources().getString(R.string.imagesel_complete);
		btnComplete.setText(String.format("%s(%d)", text,selectedImageSize));
	}
	
	@Override
	public void onRightButtonClick() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoTempUri());
		startActivityForResult(intent, PHOTO_GRAPH);
	}
	
	@Override
	protected void onDestroy() {
		cancelAllLoadTasks();
		taskList.clear();
		uriMap.clear();
		imgCache.evictAll();
		System.gc();
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		ArrayList<String> excludeUris = getIntent().getStringArrayListExtra("ExcludeUris");
		StringBuilder sb = new StringBuilder();
		String[] excludeIds = null;
		if(excludeUris != null && excludeUris.size() > 0){
			excludeIds = new String[excludeUris.size()];
			
			for(int i = 0; i<excludeUris.size(); i++){
				sb.append(String.format("%s!=? ", MediaStore.Images.Media._ID));
				if(i!=excludeUris.size()-1)
					sb.append(" and ");
				
				excludeIds[i] = excludeUris.get(i).replace(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/", "");
			}
		}
		CursorLoader loader = new CursorLoader(this, 
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
				STORE_IMAGES, sb.toString(), excludeIds, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// 使用swapCursor()方法，以使旧的游标不被关闭．
		adapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}
	
	private Uri getPictureUri(long itemId){
		return MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Long.toString(itemId)).build();
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		int position = cursor.getPosition();
		if(!uriMap.containsKey(position)){
			long id = cursor.getLong(columnIndex);
			Uri uri = getPictureUri(id);
			CheckableUri checkableUri = new CheckableUri(uri);
			uriMap.put(position, checkableUri);
		}
		
		view.setTag(uriMap.get(position));
		resetImageBitmap((AdvanceImageView) view);
		// 需要注意：在使用ViewBinder绑定数据时，必须返回真；否则，SimpleCursorAdapter将会用自己的方式绑定数据
		return true;
	}
	

	@Override
	public void onTouchImage(View view) {
		AdvanceImageView aView = (AdvanceImageView)view;
		CheckableUri tag = (CheckableUri)aView.getTag();
		int position = getPosition(tag);
		Intent intent = new Intent(this, ImageViewActivity.class);
		intent.putExtra("Position", position);
		intent.putExtra("ImageUris", getAllImageUris());
		intent.putExtra("SelImageUris", getSelectedUris());
		intent.putExtra("IsSelectMode", true);
		startActivityForResult(intent, IMAGEVIEW_REQUESTCODE);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg0 == IMAGEVIEW_REQUESTCODE){
			if(arg1 == RESULT_OK){
				ArrayList<String> selectedUris = arg2.getStringArrayListExtra("SelImageUris");
				selectedImageSize = selectedUris.size();
				for(Integer position: uriMap.keySet()){
					CheckableUri uri = uriMap.get(position);
					uri.isChecked = selectedUris.contains(uri.uri.toString());
					uriMap.put(position, uri);
				}
				gvimage.invalidateViews();
				setCompleteButtonText();
			}
		} else if(arg0 == PHOTO_GRAPH){
			if(arg1 == RESULT_OK){
				ArrayList<String> selectedUris = new ArrayList<String>();
				selectedUris.add(arg2.getDataString());
				doCompleteSelectPic(selectedUris);
			}
			// 对图片进行裁剪
//			Intent intent = new Intent("com.android.camera.action.CROP");
//			intent.setDataAndType(getPhotoTempUri(), "image/*");
//			intent.putExtra("crop", true);
//			intent.putExtra("aspectX", 1);
//			intent.putExtra("aspectY", 1);
//			intent.putExtra("outputX", 300);
//			intent.putExtra("outputY", 500);
//			intent.putExtra("return-data", true);
//			startActivityForResult(intent, PHOTO_RESOULT);
		}
	}
	
	private ArrayList<String> getSelectedUris() {
		ArrayList<String> selectedUris = new ArrayList<String>();
		for(Integer position: uriMap.keySet()){
			CheckableUri uri = uriMap.get(position);
			if(uri.isChecked)
				selectedUris.add(uri.uri.toString());
		}
		return selectedUris;
	}

	private int getPosition(CheckableUri uri){
		for(Integer position: uriMap.keySet()){
			if(uriMap.get(position) == uri)
				return position;
		}
		return -1;
	}

	@Override
	public void onCheck(View view) {
		AdvanceImageView aView = (AdvanceImageView)view;
		CheckableUri tag = (CheckableUri) aView.getTag();
		int position = getPosition(tag);
		tag.isChecked = !tag.isChecked;
		uriMap.put(position, tag);
		if(tag.isChecked)
			selectedImageSize++;
		else
			selectedImageSize--;
		setCompleteButtonText();
	}
	
	private String[] getAllImageUris(){
		if(adapter.getCount() == 0) return null;
		String[] uris = new String[adapter.getCount()];
		for(int i = 0; i<uris.length; i++){
			uris[i] = getPictureUri(adapter.getItemId(i)).toString();
		}
		return uris;
	}
	
	class CheckableUri{
		public CheckableUri(Uri uri) {
			this.uri = uri;
		}
		
		Uri uri = null;
		boolean isChecked = false;
		
		@Override
		public int hashCode() {
			return uri.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null) return false;
			if(o instanceof CheckableUri){
				return o.hashCode() == hashCode();
			}
			return false;
		}
	}
	
	private void cancelAllLoadTasks(){
		for(BitmapLoadTask task: taskList){
			task.cancel(false);
		}
		
	}
	
	private void loadBitmaps(){
		if(mVisibleItemCount == 0) return;
		for(int i=mFirstVisibleItem; i<mFirstVisibleItem+mVisibleItemCount;i++){
			CheckableUri checkableUri = uriMap.get(i);
			if(checkableUri == null) continue;
			Bitmap bitmap = imgCache.get(checkableUri.uri);
			if(bitmap != null){
				AdvanceImageView imageView = (AdvanceImageView) gvimage.findViewWithTag(checkableUri);
				resetImageBitmap(imageView);
			} else {
				BitmapLoadTask task = new BitmapLoadTask();
				taskList.add(task);
				task.execute(checkableUri);
			}
		}
	}
	
	private static final ColorDrawable TRANSPARENT_DRAWABLE = new ColorDrawable(android.R.color.transparent);      
	
	/**
	 * @author sunglasses      
	 * @category 图片加载效果 
	 * @param imageView
	 * @param bitmap      
	 **/      
	private void fadeInDisplay(ImageView imageView, Bitmap bitmap) {
		//目前流行的渐变效果          
		final TransitionDrawable transitionDrawable = new TransitionDrawable(
				new Drawable[] { TRANSPARENT_DRAWABLE,
						new BitmapDrawable(imageView.getResources(), bitmap) });
		transitionDrawable.setCrossFadeEnabled(true);
		transitionDrawable.startTransition(500);
		imageView.setImageDrawable(transitionDrawable); 
	}
	
	private void resetImageBitmap(AdvanceImageView view){
		if(view.getTag() == null) return;
		CheckableUri tag = (CheckableUri) view.getTag();
		
		Bitmap bitmap = imgCache.get(tag.uri);
		if(bitmap != null) {
			fadeInDisplay(view, bitmap);
		} else {
			view.setImageResource(R.drawable.empty_image);
		}
		view.setChecked(tag.isChecked);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// gridView滚动时取消所有装载图片任务，保证滚动的流畅性
		if(scrollState == SCROLL_STATE_IDLE){  // 静止
			loadBitmaps();
		} else {
			cancelAllLoadTasks();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 装载图片放在onScrollStateChanged中，但是第一次进来时并不会触发onScrollStateChanged，因此第一次装载图片放在这里
		if(!mFirstLoad && visibleItemCount>0){
			loadBitmaps();
			mFirstLoad = true;
		}
		
	}
	
	class BitmapLoadTask extends AsyncTask<CheckableUri, Void, Bitmap>{
		private CheckableUri imgUri = null;
		
		@Override
		protected Bitmap doInBackground(CheckableUri... params) {
			if(params == null || params.length == 0) return null;
			imgUri = params[0];
			Bitmap bitmap = null;
			if(!isCancelled()){
				bitmap = ImageHelper.loadBitmap(getContentResolver(), imgUri.uri, imgSize, imgSize*imgSize);
				if(bitmap != null)
					imgCache.put(imgUri.uri, bitmap);
			}
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			AdvanceImageView view = (AdvanceImageView) gvimage.findViewWithTag(imgUri);
			resetImageBitmap(view);
			taskList.remove(this);
			super.onPostExecute(result);
		}
	}

	@Override
	public void onClick(View v) {
		if(v == btnComplete){
			doCompleteSelectPic(getSelectedUris());
		}
	}
	
	private void doCompleteSelectPic(ArrayList<String> selectedUris){
		Intent intent = new Intent(this, UploadActivity.class);
		intent.putExtra("SelImageUris", selectedUris);
		// FLAG_ACTIVITY_CLEAR_TOP表示把UploadActivity以上的Activity全部清除掉，此时如果UploadActivity
		// 没有设置任何lauchMode，那么就会创建UploadActivity实例，如果设置了lauchMode或者flag为FLAG_ACTIVITY_SINGLE_TOP
		// 则不会创建实例，而是走Activity的OnNewIntent方法
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);		
	}
}
