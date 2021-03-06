package com.ynt.account;

import java.util.Date;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.base.WaitingActor;
import com.ynt.account.control.ReportColumn;
import com.ynt.account.control.ReportRow;
import com.ynt.account.control.ReportView;
import com.ynt.account.data.LoginContext;
import com.ynt.account.request.ReportXjllBean;
import com.ynt.account.request.ReportZcfzbBean;
import com.ynt.account.request.ServerRequest;
import com.ynt.account.utils.UIHelper;

public class ReportXjllActivity extends BaseActivity {
	private static final String dateFormat="yyyy-MM";
	private ReportView reportView = null;
	private String m_period = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_xjll);
		
		reportView = (ReportView)findViewById(R.id.report_xjll_view);
		
		ReportColumn projColumn = new ReportColumn(this, "项目", "project", R.style.report_head, R.style.report_body);
		projColumn.getHeadStyle().setColumnWeight(1.5f);
		projColumn.getRowStyle().setColumnWeight(1.5f);
		projColumn.getRowStyle().setGravity(Gravity.CENTER_VERTICAL);
		reportView.addColumn(projColumn);
		
		ReportColumn totalMnyColumn = new ReportColumn(this, "本年金额", "totalMny", R.style.report_head, R.style.report_body);
		totalMnyColumn.getRowStyle().setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		reportView.addColumn(totalMnyColumn);
		
		getRightButton().setText(R.string.actionbar_query);
		
		loadReportData(UIHelper.toDateString(new Date(), dateFormat));
	}
	
	private void invalidateReportView(){
		reportView.post(new Runnable() {
			
			@Override
			public void run() {
				reportView.invalidateViews();
			}
		}); 
	}
	
	private void loadReportData(String period){
		m_period = period;
		WaitingActor.run(this, "正在读取", new Runnable() {
			
			@Override
			public void run() {
				try {
					ReportXjllBean[] beans = ServerRequest.getInstance().queryReportForXjll(
						LoginContext.getInstance().getLoginCorp(), m_period);
					reportView.clearRows();
					if(beans == null || beans.length == 0) return;
					
					for(ReportXjllBean bean:beans){
						reportView.addRow(new ReportRow()
							.setValue("project", bean.getProjectname())
							.setValue("totalMny", bean.getBnmny()));
					}
					invalidateReportView();
				} catch (Exception e) {
					e.printStackTrace();
					showErrorMsg(e.getMessage());
				}
			}
		});
		
		
	}
	
	@Override
	public void onRightButtonClick() {
		ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.report_period_query, null);
		final EditText txtPeriodView = (EditText)layout.findViewById(R.id.report_period_query);
		txtPeriodView.setText(m_period);
		txtPeriodView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					txtPeriodView.requestFocus();
					UIHelper.showDatePickerDialog(ReportXjllActivity.this, dateFormat, txtPeriodView);
				}
				return true;
			}
		});
		
		showDialog(layout, new OnDialogOKListener() {
			
			@Override
			public boolean onDialogOK(View v) {
				String period = txtPeriodView.getText().toString().trim();
				if(period.equals("")){
					showErrorMsg("查询期间不能为空");
					return false;
				}
				loadReportData(period);
				return true;
			}
		});
	}
}
