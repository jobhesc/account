package com.ynt.account;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.base.Profile;
import com.ynt.account.base.WaitingActor;
import com.ynt.account.control.AdvanceImageView;
import com.ynt.account.control.AdvanceImageView.OnCheckListner;
import com.ynt.account.data.IImagePersistence;
import com.ynt.account.data.ImageModel;
import com.ynt.account.data.ImageModel.ImageGroup;
import com.ynt.account.data.ImageModel.ImageItem;
import com.ynt.account.data.ImageModelFactory;
import com.ynt.account.data.LoginContext;
import com.ynt.account.request.ServerRequest;
import com.ynt.account.utils.ImageHelper;

public class UploadActivity extends BaseActivity implements OnClickListener {
	private Button btnSumbit;
	private ListView lvlist;
	private ImageModel imageModel;
	private int currGroupIndex=-1;
	private int imgSize = 0;
	private boolean isEditMode = false;
	private String uploadlot = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);
		
		getLeftButton().setText(R.string.actionbar_left);
		getRightButton().setText(R.string.actionbar_edit);
		
		imgSize = getResources().getDimensionPixelSize(R.dimen.image_size);
		imageModel = ImageModelFactory.createModel(this);
		// 初始有一组
		addNewGroup();
		
		lvlist = (ListView)findViewById(R.id.upload_list);
		lvlist.setAdapter(new ListViewAdapter(this));
		
		btnSumbit = (Button)findViewById(R.id.upload_submit);
		btnSumbit.setOnClickListener(this);
		
		// 上传批号
		uploadlot = UUID.randomUUID().toString();
	}
	
	@Override
	public void onRightButtonClick() {
		if(isEditMode){
			// 删除图片
			for(int i=0; i<imageModel.size();i++){
				ImageGroup group = imageModel.get(i);
				for(int j=group.size()-1; j>=0;j--){
					ImageItem item = group.getImage(j);
					if(item.getItemKind() == ImageModel.ITEMKIND_IMAGE && item.isChecked()){
						group.removeImage(item);
					}
				}
			}
			isEditMode = !isEditMode;
			getRightButton().setText(R.string.actionbar_edit);
		} else {
			isEditMode = !isEditMode;
			getRightButton().setText(R.string.actionbar_delete);			
		}
		lvlist.invalidateViews();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		ArrayList<String> selectedUris = intent.getStringArrayListExtra("SelImageUris");
		if(currGroupIndex < 0 || selectedUris == null || selectedUris.size() == 0) return;
		for(String selectedUri: selectedUris)
			imageModel.addImage(currGroupIndex, selectedUri);
		// 如果当前组是最后一组，则添加完图片后自动增加组
		if(currGroupIndex==imageModel.size()-1){
			addNewGroup();
		}
		lvlist.invalidateViews();
	}
	
	private ImageGroup addNewGroup(){
		ImageGroup imageGroup = imageModel.newGroup();
		imageGroup.addButton();  // 添加按钮
		try {
			imageModel.addGroup(imageGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageGroup;
	}
	
	private ArrayList<String> getAllSelUris(){
		ArrayList<String> selectedUris = new ArrayList<String>();
		for(int i=0; i<imageModel.size();i++){
			ImageGroup group = imageModel.get(i);
			for(int j=0; j<group.size();j++){
				ImageItem item = group.getImage(j);
				if(item.getItemKind() == ImageModel.ITEMKIND_IMAGE)
					selectedUris.add(item.getImageUri());
			}
		}
		return selectedUris;
	}
	
	class ListViewHolder{
		public TextView textView;
		public GridView gridView;
	}
	
	class GridViewHolder{
		public AdvanceImageView imageView;
	}
	
	class GridViewAdapter extends BaseAdapter implements OnClickListener, OnCheckListner{
		LayoutInflater inflater = null;
		ImageGroup imageGroup = null;
		Context context = null;
		public GridViewAdapter(Context context, ImageGroup imageGroup) {
			this.context = context;
			this.imageGroup = imageGroup;
			this.inflater = LayoutInflater.from(context);
		}
		
		public ImageGroup getImageGroup() {
			return imageGroup;
		}

		public void setImageGroup(ImageGroup imageGroup) {
			this.imageGroup = imageGroup;
		}
		
		@Override
		public int getCount() {
			return imageGroup.size();
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
			GridViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.upload_gridview, null);
				holder = new GridViewHolder();
				holder.imageView = (AdvanceImageView)convertView.findViewById(R.id.upload_grid_image);
				convertView.setTag(holder);
			} else {
				holder = (GridViewHolder) convertView.getTag();
			}
			
			ImageItem imageItem = imageGroup.getImage(position);
			
			holder.imageView.setOnClickListener(null);
			holder.imageView.setOnCheckListener(null);
			holder.imageView.setShowCheckBox(false);
			holder.imageView.setCheckBoxPadding(5);
			if(isEditMode && imageItem.getItemKind() == ImageModel.ITEMKIND_IMAGE){
				holder.imageView.setShowCheckBox(true);
				holder.imageView.setOnCheckListener(this);
			} else {
				holder.imageView.setOnClickListener(this);
			}
			
			if(imageItem.getItemKind() == ImageModel.ITEMKIND_BUTTON){
				holder.imageView.setImageResource(R.drawable.upload_add);
				holder.imageView.setBackgroundResource(R.drawable.image_add_select);
			} else {
				holder.imageView.setBackgroundResource(android.R.color.transparent);
				holder.imageView.setImageBitmap(ImageHelper.loadBitmap(getContentResolver(), Uri.parse(imageItem.getImageUri()), imgSize, imgSize*imgSize));
			}
			holder.imageView.setTag(imageItem);
			return convertView;
		}
		
		@Override
		public void onClick(View v) {
			if(!(v instanceof ImageView)) return;
			ImageItem imageItem = (ImageItem) v.getTag();
			if(imageItem == null) return;
			
			if(imageItem.getItemKind() == ImageModel.ITEMKIND_BUTTON){  // 点击增加按钮
				currGroupIndex = imageModel.indexOfGroup(imageGroup);
				Intent intent = new Intent(context, ImageSelActivity.class);
				intent.putExtra("ExcludeUris", getAllSelUris());
				UploadActivity.this.startActivity(intent);
			} else {  // 点击图片预览
				Intent intent = new Intent(context, ImageViewActivity.class);
				intent.putExtra("ImageUris", new String[]{imageItem.getImageUri()});
				intent.putExtra("Position", 0);
				UploadActivity.this.startActivity(intent);
			}
		}

		@Override
		public void onCheck(View view) {
			ImageItem item = (ImageItem) view.getTag();
			if(item.getItemKind() == ImageModel.ITEMKIND_BUTTON) return;
			item.setChecked(!item.isChecked());   // 选择图片
		}
		
	}
	
	class ListViewAdapter extends BaseAdapter{
		Context context = null;
		LayoutInflater inflater = null;
		public ListViewAdapter(Context context) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return imageModel.size();
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
			ImageGroup group = imageModel.get(position);
			ListViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.upload_listview, null);
				holder = new ListViewHolder();
				holder.textView = (TextView)convertView.findViewById(R.id.upload_list_text);
				holder.gridView = (GridView)convertView.findViewById(R.id.upload_list_gridview);
				convertView.setTag(holder);
			} else {
				holder = (ListViewHolder) convertView.getTag();
			}
			
			holder.textView.setText(group.getGroupName());
			holder.gridView.setAdapter(new GridViewAdapter(context, group));
			
			return convertView;
		}
	}

	@Override
	public void onClick(final View v) {
		WaitingActor.run(this, "正在提交", new Runnable() {
			
			@Override
			public void run() {
				try{
					if(v == btnSumbit){
						doSubmit();
					}
				} catch(Exception e){
					e.printStackTrace();
					showErrorMsg(e.getMessage());
				}			
			}
		});

	}
	
	private void doSubmit() throws Exception{
		imageModel.setUploadlot(uploadlot);
		imageModel.setUploadBy(LoginContext.getInstance().getLoginUser());
		imageModel.setUploadOn(new Date());
		// 保存数据
		IImagePersistence persistence = ImageModelFactory.createPersistence(this);
		persistence.Save(new ImageModel[]{imageModel});
		File zipfile = imageModel.getFile();
		// 上传图片
		ServerRequest.getInstance().uploadImages(zipfile, 
				LoginContext.getInstance().getLoginCorp(), 
				LoginContext.getInstance().getLoginUser());
		finish();
	}
	
}
