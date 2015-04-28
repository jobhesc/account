package com.ynt.account;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.data.SysMessageBean;

public class SysMessageDetailActivity extends BaseActivity  {
	private SysMessageBean m_sysBean = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_sysdetail);
		m_sysBean = getIntent().getParcelableExtra("sysMessageBean");
		
		TextView subjectView = (TextView)findViewById(R.id.message_subject);
		subjectView.setText(m_sysBean.getSubject());
		
		TextView sendtimeView = (TextView)findViewById(R.id.message_sendtime);
		sendtimeView.setText(m_sysBean.getSendtime());
		
		TextView contentView = (TextView)findViewById(R.id.message_content);
		contentView.setText(m_sysBean.getContent());
		
		getRightButton().setVisibility(View.INVISIBLE);
	}
}
