package com.ynt.account.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ynt.account.ImgMessageDetailActivity;
import com.ynt.account.R;
import com.ynt.account.base.BaseActivity;
import com.ynt.account.control.RefreshableView;
import com.ynt.account.control.RefreshableView.PullToRefreshListener;
import com.ynt.account.data.ImgMessageBean;
import com.ynt.account.data.LoginContext;
import com.ynt.account.data.SqlMessagePersistence;
import com.ynt.account.task.MessageTask;

public class ImgMessageFragment extends Fragment {
	private ListView m_imgmsgList = null;
	private SqlMessagePersistence persistence = null;
	private MessageTask messageTask = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		persistence = new SqlMessagePersistence(getActivity());
		messageTask = new MessageTask(getActivity());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if(v == m_imgmsgList)
			getActivity().getMenuInflater().inflate(R.menu.message, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_message_del){
			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			persistence.delImgMessage((int)menuInfo.id);
			((SimpleCursorAdapter)m_imgmsgList.getAdapter()).getCursor().requery();
		}
		return true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.message_imgfragment, null);
		
		Cursor cursor = persistence.readImgMessagesForCursor(0, Integer.MAX_VALUE);
		getActivity().startManagingCursor(cursor);
		
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), 
				R.layout.message_listview, cursor, 
				new String[]{"sendtime", "content"}, 
				new int[]{R.id.msg_title, R.id.msg_content}, SimpleCursorAdapter.FLAG_AUTO_REQUERY){
					@Override
					public void bindView(View view, Context context, Cursor cursor) {
						ImgMessageBean bean = persistence.readImgMessage(cursor);
						TextView textView = (TextView) view.findViewById(R.id.msg_title);
						textView.setTextColor(bean.isIsread()?Color.GRAY:Color.BLACK);
						
						super.bindView(view, context, cursor);
					}
					
					@Override
					public void setViewText(TextView v, String text) {
						super.setViewText(v, text.trim());  // 去掉空格
					}
				};
		m_imgmsgList = (ListView) vg.findViewById(R.id.imgmsg_list);
		m_imgmsgList.setAdapter(adapter);
		m_imgmsgList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(adapter.getCursor().moveToPosition(position)){
					ImgMessageBean bean = persistence.readImgMessage(adapter.getCursor());
					// 设置已读状态
					persistence.markImgMsgReaded(bean.getKey());
					
					Intent intent = new Intent(getActivity(), ImgMessageDetailActivity.class);
					intent.putExtra("imgMessageBean", bean);
					getActivity().startActivity(intent);
				}
			}
		});
		m_imgmsgList.setOnCreateContextMenuListener(this);
		
		RefreshableView refreshView = (RefreshableView)vg.findViewById(R.id.imgmsg_refresh);
		refreshView.setOnRefreshListener(ImgMessageFragment.class.getName(), new PullToRefreshListener() {
			
			@Override
			public void onRefresh() {
				try {
					messageTask.refreshImgMessage(LoginContext.getInstance().getLoginCorp(), 
							LoginContext.getInstance().getLoginUser());

					final Cursor cursor = persistence.readImgMessagesForCursor(0, Integer.MAX_VALUE);
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							getActivity().startManagingCursor(cursor);
							adapter.changeCursor(cursor);
						}
					});
				} catch (Exception e) {
					Log.e(ImgMessageFragment.class.getName(), e.getMessage());
					((BaseActivity)getActivity()).showErrorMsg(e.getMessage());
				}
			}
		});
		return vg;
	}
	
	
}
