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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ynt.account.R;
import com.ynt.account.SysMessageDetailActivity;
import com.ynt.account.base.BaseActivity;
import com.ynt.account.control.RefreshableView;
import com.ynt.account.control.RefreshableView.PullToRefreshListener;
import com.ynt.account.data.SqlMessagePersistence;
import com.ynt.account.data.SysMessageBean;
import com.ynt.account.task.MessageTask;

public class SysMessageFragment extends Fragment {
	private ListView m_sysmsgList = null;
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
		if(v == m_sysmsgList)
			getActivity().getMenuInflater().inflate(R.menu.message, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_message_del){
			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			persistence.delSysMessage((int)menuInfo.id);
			((SimpleCursorAdapter)m_sysmsgList.getAdapter()).getCursor().requery();
		}
		return true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.message_sysfragment, null);
		
		Cursor cursor = persistence.readSysMessagesForCursor(0, Integer.MAX_VALUE);
		getActivity().startManagingCursor(cursor);
		
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), 
				R.layout.message_listview, cursor, 
				new String[]{"subject","sendtime", "content"}, 
				new int[]{R.id.msg_title, R.id.msg_time, R.id.msg_content}, SimpleCursorAdapter.FLAG_AUTO_REQUERY){
					@Override
					public void bindView(View view, Context context, Cursor cursor) {
						SysMessageBean bean = persistence.readSysMessage(cursor);
						TextView textView = (TextView) view.findViewById(R.id.msg_title);
						textView.setTextColor(bean.isIsread()?Color.GRAY:Color.BLACK);
						
						super.bindView(view, context, cursor);
					}
					
					@Override
					public void setViewText(TextView v, String text) {
						super.setViewText(v, text.trim());  // 去掉空格
					}
				};
		m_sysmsgList = (ListView) vg.findViewById(R.id.sysmsg_list);
		m_sysmsgList.setAdapter(adapter);
		m_sysmsgList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(adapter.getCursor().moveToPosition(position)){
					SysMessageBean bean = persistence.readSysMessage(adapter.getCursor());
					// 设置已读状态
					persistence.markSysMsgReaded(bean.getKey());
					
					Intent intent = new Intent(getActivity(), SysMessageDetailActivity.class);
					intent.putExtra("sysMessageBean", bean);
					getActivity().startActivity(intent);
				}
			}
		});
		m_sysmsgList.setOnCreateContextMenuListener(this);
		
		RefreshableView refreshView = (RefreshableView)vg.findViewById(R.id.sysmsg_refresh);
		refreshView.setOnRefreshListener(SysMessageFragment.class.getName(), new PullToRefreshListener() {
			
			@Override
			public void onRefresh() {
				try {
					messageTask.refreshSysMessage();

					final Cursor cursor = persistence.readSysMessagesForCursor(0, Integer.MAX_VALUE);
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							getActivity().startManagingCursor(cursor);
							adapter.changeCursor(cursor);
						}
					});
				} catch (Exception e) {
					Log.e(SysMessageFragment.class.getName(), e.getMessage());
					((BaseActivity)getActivity()).showErrorMsg(e.getMessage());
				}
			}
		});
		return vg;
	}
}
