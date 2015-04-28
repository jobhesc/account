package com.ynt.account;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.base.WaitingActor;
import com.ynt.account.control.AdvanceImageView;
import com.ynt.account.data.IImagePersistence;
import com.ynt.account.data.ImageModel;
import com.ynt.account.data.ImageModelFactory;
import com.ynt.account.data.ImgMessageBean;
import com.ynt.account.data.LoginContext;
import com.ynt.account.data.SqlMessagePersistence;
import com.ynt.account.data.ImageModel.ImageGroup;
import com.ynt.account.data.ImageModel.ImageItem;
import com.ynt.account.request.ServerRequest;
import com.ynt.account.utils.ImageHelper;

public class ImgMessageDetailActivity extends BaseActivity implements OnClickListener  {
    private static final int PHOTO_GRAPH = 1;// 拍照
	private GridView badimgView = null;
	private GridView newimgView = null;
	private ImgMessageBean m_imgBean = null;
	private int imgSize = 0;
	private Bitmap[] m_badImages = null;
	private Bitmap[] m_newImages = null;
	private String[] m_badImagePaths = null;
	private String[] m_newImagePaths = null;
	private Button submitButton;
	private String uploadlot;
	private SqlMessagePersistence msgPersistence;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_imgdetail);
		msgPersistence = new SqlMessagePersistence(this);
		uploadlot = UUID.randomUUID().toString();
		imgSize = getResources().getDimensionPixelSize(R.dimen.image_size);
		m_imgBean = getIntent().getParcelableExtra("imgMessageBean");
		
		TextView statusView = (TextView)findViewById(R.id.message_dealstatus);
		statusView.setText(m_imgBean.isIsupload()?"已处理":"未处理");
		
		TextView timeView = (TextView)findViewById(R.id.message_returntime);
		timeView.setText(m_imgBean.getSendtime());
		
		TextView contentView = (TextView)findViewById(R.id.message_questiondesc);
		contentView.setText(m_imgBean.getContent());
		
		m_badImagePaths = m_imgBean.getBadpathArray();
		m_newImagePaths = m_imgBean.getNewpathArray();
		
		badimgView = (GridView)findViewById(R.id.message_badimage);
		newimgView = (GridView)findViewById(R.id.message_newimage);
		setAdapter();
		
		submitButton = (Button)findViewById(R.id.upload_submit);
		submitButton.setOnClickListener(this);
		
		getRightButton().setText(R.string.actionbar_photo);
		getRightButton().setEnabled(!m_imgBean.isIsupload());
		submitButton.setEnabled(!m_imgBean.isIsupload());
	}
	
	@Override
	public void onRightButtonClick() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoTempUri());
		startActivityForResult(intent, PHOTO_GRAPH);
	}
	
	private String ArrayToString(String[] array){
		if(array == null || array.length == 0) return "";
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i<array.length ; i++){
			buffer.append(array[i]);
			if(i<array.length-1)
				buffer.append(",");
		}
		return buffer.toString();
	}
	
	private <T> T[] addArrayItem(T[] array, T item){
		List<T> list = new ArrayList<T>();
		if(array != null)
			list.addAll(Arrays.asList(array));
		list.add(item);	
		@SuppressWarnings("unchecked")
		T[] newArray = (T[]) Array.newInstance(item.getClass(), list.size());
		return list.toArray(newArray);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg0 == PHOTO_GRAPH && arg1 == RESULT_OK){
			String uri = arg2.getDataString();
			m_newImagePaths = addArrayItem(m_newImagePaths, uri);
			
			Bitmap image = ImageHelper.loadBitmap(getContentResolver(), 
					Uri.parse(uri), imgSize, imgSize*imgSize);
			m_newImages = addArrayItem(m_newImages, image);
			// 保存拍照图片
			msgPersistence.resetNewImgPath(m_imgBean.getKey(), ArrayToString(m_newImagePaths));
			// 重新刷新gridview
			newimgView.setAdapter(new GridViewAdapter(m_newImages, m_newImagePaths, ImageViewActivity.class));
		}
	}
	
	@Override
	protected void onDestroy() {
		recycleImage();
		super.onDestroy();
	}
	
	private void setAdapter(){
		WaitingActor.run(this, "正在装载", new Runnable() {
			
			@Override
			public void run() {
				loadImages();
				badimgView.post(new Runnable() {
					
					@Override
					public void run() {
						badimgView.setAdapter(new GridViewAdapter(m_badImages, m_badImagePaths, ImageView2Activity.class));
					}
				});
				newimgView.post(new Runnable() {
					
					@Override
					public void run() {
						newimgView.setAdapter(new GridViewAdapter(m_newImages, m_newImagePaths, ImageViewActivity.class));
					}
				});
			}
		});
	}
	
	private boolean existPath(String[] paths, String path){
		if(paths == null || paths.length == 0) return false;
		for(String item: paths){
			if(item.equalsIgnoreCase(path))
				return true;
		}
		return false;
	}
	/**
	 * 判断sd卡是否可用
	 * @return
	 */
	private boolean isExternalStorageUsable(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
				!Environment.isExternalStorageRemovable();
	}
	
	private File getLocalCacheDir(){
		if(isExternalStorageUsable()){
			return this.getExternalCacheDir();
		} else {
			return this.getCacheDir();
		}
	}
	
	private void doSubmit(){
		ArrayList<File> tempfiles = new ArrayList<File>();
		try{
			String[] groupPaths = m_imgBean.getGrouppathArray();
			if(groupPaths == null || groupPaths.length == 0) return;

			if(m_newImagePaths == null || m_newImagePaths.length == 0){
				throw new Exception("没有重新选择拍照图片，不允许提交");
			}
			
			ImageModel imageModel = ImageModelFactory.createModel(this);
			imageModel.setUploadlot(uploadlot);
			imageModel.setUploadBy(LoginContext.getInstance().getLoginUser());
			imageModel.setUploadOn(new Date());
			
			ImageGroup imageGroup = imageModel.newGroup();
			imageModel.addGroup(imageGroup);
			ImageItem imageItem = null;
			File localCacheDir = getLocalCacheDir();
			
			for(String groupPath: groupPaths){
				// 添加本组没有问题的图片
				if(!existPath(m_badImagePaths, groupPath)){
					imageItem = imageGroup.addImage("");
					File tempfile = new File(localCacheDir.getAbsolutePath(), imageItem.getImageName());
					ServerRequest.getInstance().downloadImage(groupPath, tempfile);
					tempfiles.add(tempfile);

					imageItem.setImageFile(tempfile);
				}
			}
			
			for(String newPath: m_newImagePaths){
				imageItem = imageGroup.addImage(newPath);
			}
			
			// 保存数据
			IImagePersistence imgPersistence = ImageModelFactory.createPersistence(this);
			imgPersistence.Save(new ImageModel[]{imageModel});
			File zipfile = imageModel.getFile();
			// 上传图片
			ServerRequest.getInstance().uploadImages(zipfile, 
					LoginContext.getInstance().getLoginCorp(), 
					LoginContext.getInstance().getLoginUser());
			
			// 设置为已处理
			msgPersistence.markImgMsgUploaded(m_imgBean.getKey());
			
			finish();
		} catch(Exception e){
			Log.e(ImgMessageDetailActivity.class.getName(), e.getMessage(), e);
			showErrorMsg(e.getMessage());
		} finally {
			// 删除临时文件
			for(File file: tempfiles){
				file.delete();
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v == submitButton){
			WaitingActor.run(this, "正在提交", new Runnable() {
				
				@Override
				public void run() {
					doSubmit();
				}
			});
		}
	}	
	
	private void recycleImage(){
		if(m_badImages != null && m_badImages.length>0){
			for(int i = 0; i<m_badImages.length;i++){
				if(m_badImages[i] != null)
					m_badImages[i].recycle();
			}
			m_badImages=null;
		}
		
		if(m_newImages != null && m_newImages.length>0){
			for(int i = 0; i<m_newImages.length;i++){
				if(m_newImages[i] != null)
					m_newImages[i].recycle();
			}
			m_newImages=null;
		}
	}
	
	private void loadImages(){
		recycleImage();
		// 装载有问题的图片
		String[] badImagePaths = m_badImagePaths;
		if(badImagePaths != null && badImagePaths.length>0){
			m_badImages = new Bitmap[badImagePaths.length];

			try{
				for(int i = 0; i<badImagePaths.length; i++){
					m_badImages[i] = ImageHelper.loadBitmapFromServer(badImagePaths[i],
							imgSize, imgSize*imgSize);
				}			
			} catch(Exception e){
				Log.e(ImgMessageDetailActivity.class.getName(), e.getMessage(), e);
				showErrorMsg(e.getMessage());
			}
		}
		// 装载新图片
		String[] newImagePaths = m_newImagePaths;
		if(newImagePaths != null && newImagePaths.length>0){
			m_newImages = new Bitmap[newImagePaths.length];
			for(int i = 0; i<newImagePaths.length;i++){
				m_newImages[i] = ImageHelper.loadBitmap(getContentResolver(), 
						Uri.parse(newImagePaths[i]), imgSize, imgSize*imgSize);
			}
		}
	}
	
	class GridViewAdapter extends BaseAdapter implements OnClickListener{
		private Bitmap[] images = null;
		private String[] paths = null;
		private Class<?> imageViewActivity = null;
		
		public GridViewAdapter(Bitmap[] images, String[] imagePaths, Class<?> imageViewActivity) {
			this.images = images;
			this.paths = imagePaths;
			this.imageViewActivity = imageViewActivity;
		}
		
		@Override
		public int getCount() {
			return images == null?0:images.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.message_gridview, null);
			}

			AdvanceImageView imgView = (AdvanceImageView) convertView.findViewById(R.id.message_grid_image);
			imgView.setOnClickListener(this);
			
			imgView.setTag(paths[position]);
			Bitmap image = images[position];
			if(image != null)
				imgView.setImageBitmap(image);
			else
				imgView.setImageResource(R.drawable.empty_image);
			return convertView;
		}
		
		@Override
		public void onClick(View v) {
			if(!(v instanceof ImageView)) return;
			String path = v.getTag().toString();
			
			Intent intent = new Intent(ImgMessageDetailActivity.this, imageViewActivity);
			intent.putExtra("ImageUris", new String[]{ path });
			intent.putExtra("Position", 0);
			ImgMessageDetailActivity.this.startActivity(intent);
		}

	}
}
