package com.ynt.account;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.base.WaitingActor;
import com.ynt.account.control.AdvanceImageView;
import com.ynt.account.data.IImagePersistence;
import com.ynt.account.data.ImageModel;
import com.ynt.account.data.ImageModel.ImageGroup;
import com.ynt.account.data.ImageModel.ImageItem;
import com.ynt.account.data.ImageModelFactory;
import com.ynt.account.utils.ImageHelper;
import com.ynt.account.utils.UIHelper;

public class HistoryActivity extends BaseActivity  {
	private static final String dateFormat="yyyyMMdd";
	private ListView lvlist;
	private ImageModel[] imageModels;
	private int imgSize = 0;
	private IImagePersistence persistence = null;
	private String m_beginDate;
	private String m_endDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		persistence = ImageModelFactory.createPersistence(this);
		getLeftButton().setText(R.string.actionbar_left);
		getRightButton().setText(R.string.actionbar_query);
		
		imgSize = getResources().getDimensionPixelSize(R.dimen.image_size);
		
		lvlist = (ListView)findViewById(R.id.history_list);
		lvlist.setAdapter(new ListViewAdapter(this));
		// 默认装载当天上传的图片
		String currDate = UIHelper.toDateString(new Date(), dateFormat);
		loadImageModel(currDate, currDate);
	}
	
	private void loadImageModel(String beginDate,String endDate){
		m_beginDate = beginDate;
		m_endDate = endDate;
		WaitingActor.run(this, "正在装载", new Runnable() {
			
			@Override
			public void run() {
				try {
					imageModels = persistence.load(String.format("uploadon>='%s' and uploadon<='%s'", m_beginDate, m_endDate ));
					lvlist.post(new Runnable() {
						
						@Override
						public void run() {
							lvlist.invalidateViews();
						}
					}); 
				} catch (Exception e) {
					e.printStackTrace();
					showErrorMsg(e.getMessage());
				}
			}
		});
		
	}
	
	
	@Override
	public void onRightButtonClick() {
		ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.history_query, null);
		final TextView txtBeginView = (TextView)layout.findViewById(R.id.history_filter_begin);
		final TextView txtEndView = (TextView)layout.findViewById(R.id.history_filter_end);
		txtBeginView.setText(m_beginDate);
		txtEndView.setText(m_endDate);
		txtBeginView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					txtBeginView.requestFocus();
					UIHelper.showDatePickerDialog(HistoryActivity.this, dateFormat, txtBeginView);
				}
				return true;
			}
		});
		txtEndView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					txtEndView.requestFocus();
					UIHelper.showDatePickerDialog(HistoryActivity.this, dateFormat, txtEndView);
				}
				return true;
			}
		});
		
		showDialog(layout, new OnDialogOKListener() {
			
			@Override
			public boolean onDialogOK(View v) {
				String begin = txtBeginView.getText().toString().trim();
				String end = txtEndView.getText().toString().trim();
				if(begin.equals("")){
					showErrorMsg("开始日期不能为空");
					return false;
				}
				if(end.equals("")){
					showErrorMsg("结束日期不能为空");
					return false;
				}
				loadImageModel(begin, end);
				return true;
			}
		});
	}
	
	class ListViewHolder{
		public TextView textView;
		public GridView gridView;
	}
	
	class GridViewHolder{
		public AdvanceImageView imageView;
	}
	
	class GridViewAdapter extends BaseAdapter implements OnClickListener{
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
				convertView = inflater.inflate(R.layout.history_gridview, null);
				holder = new GridViewHolder();
				holder.imageView = (AdvanceImageView)convertView.findViewById(R.id.history_grid_image);
				convertView.setTag(holder);
			} else {
				holder = (GridViewHolder) convertView.getTag();
			}
			
			ImageItem imageItem = imageGroup.getImage(position);
			
			holder.imageView.setOnClickListener(null);
			holder.imageView.setOnCheckListener(null);
			holder.imageView.setShowCheckBox(false);
			holder.imageView.setCheckBoxPadding(5);
			holder.imageView.setOnClickListener(this);
			
			if(imageItem.getItemKind() == ImageModel.ITEMKIND_BUTTON){
				holder.imageView.setImageResource(R.drawable.upload_add);
				holder.imageView.setBackgroundResource(R.drawable.image_add_select);
			} else {
				holder.imageView.setBackgroundResource(android.R.color.transparent);
				Bitmap bitmap = ImageHelper.loadBitmap(imageItem.getImageFile().getAbsolutePath(), imgSize, imgSize*imgSize);
				holder.imageView.setImageBitmap(bitmap);
			}
			holder.imageView.setTag(imageItem);
			return convertView;
		}
		
		@Override
		public void onClick(View v) {
			if(!(v instanceof ImageView)) return;
			ImageItem imageItem = (ImageItem) v.getTag();
			if(imageItem == null) return;
			
			Intent intent = new Intent(context, ImageView3Activity.class);
			intent.putExtra("ImageUris", new String[]{imageItem.getImageFile().getAbsolutePath()});
			intent.putExtra("Position", 0);
			HistoryActivity.this.startActivity(intent);
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
			if(imageModels == null || imageModels.length == 0) return 0;
			int count = 0;
			for(ImageModel model: imageModels){
				count += model.size();
			}
			
			return count;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		private ImageGroup findGroup(int position){
			if(imageModels == null || imageModels.length == 0) return null;
			
			int pos = position;
			for(ImageModel model: imageModels){
				if(pos < model.size()){
					return model.get(pos);
				} else {
					pos -= model.size();
					continue;
				}
			}
			return null;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageGroup group = findGroup(position);
			if(group == null) return null;
			ListViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.history_listview, null);
				holder = new ListViewHolder();
				holder.textView = (TextView)convertView.findViewById(R.id.history_list_text);
				holder.gridView = (GridView)convertView.findViewById(R.id.history_list_gridview);
				convertView.setTag(holder);
			} else {
				holder = (ListViewHolder) convertView.getTag();
			}
			
			holder.textView.setText(group.getGroupName());
			holder.gridView.setAdapter(new GridViewAdapter(context, group));
			
			return convertView;
		}
	}
	
}
