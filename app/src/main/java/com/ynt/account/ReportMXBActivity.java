package com.ynt.account;

import java.util.ArrayList;
import java.util.Date;

import android.R.color;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.base.WaitingActor;
import com.ynt.account.control.ReportColumn;
import com.ynt.account.control.ReportRow;
import com.ynt.account.control.ReportView;
import com.ynt.account.data.LoginContext;
import com.ynt.account.request.ReportMxbBean;
import com.ynt.account.request.ReportXjllBean;
import com.ynt.account.request.ReportZcfzbBean;
import com.ynt.account.request.ServerRequest;
import com.ynt.account.utils.UIHelper;

public class ReportMXBActivity extends BaseActivity {
	private static final String dateFormat="yyyy-MM";
	private static final String[] subjectAtts = new String[]{"资产", "负债", "共同", "所有者权益", "成本", "损益"};
	private ReportView reportView = null;
	private String m_beginPeriod = "";
	private String m_endPeriod = "";
	private ArrayList<Integer> m_kmsx = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_mxb);
		
		reportView = (ReportView)findViewById(R.id.report_mxb_view);
		
		ReportColumn dateColumn = new ReportColumn(this, "日期", "date", R.style.report_head, R.style.report_body);
		dateColumn.getHeadStyle().setCellWidth(100);
		dateColumn.getRowStyle().setCellWidth(100);
		dateColumn.getRowStyle().setGravity(Gravity.CENTER_VERTICAL);
		reportView.addColumn(dateColumn);
		
		ReportColumn subjectsColumn = new ReportColumn(this, "科目", "subjects", R.style.report_head, R.style.report_body);
		subjectsColumn.getHeadStyle().setCellWidth(150);
		subjectsColumn.getRowStyle().setCellWidth(150);
		subjectsColumn.getHeadStyle().setColumnWeight(0);
		subjectsColumn.getRowStyle().setColumnWeight(0);
		subjectsColumn.getRowStyle().setGravity(Gravity.CENTER_VERTICAL);
		reportView.addColumn(subjectsColumn);
		
		ReportColumn abstractColumn = new ReportColumn(this, "摘要", "abstract", R.style.report_head, R.style.report_body);
		abstractColumn.getHeadStyle().setCellWidth(150);
		abstractColumn.getRowStyle().setCellWidth(150);
		subjectsColumn.getHeadStyle().setColumnWeight(0);
		subjectsColumn.getRowStyle().setColumnWeight(0);
		abstractColumn.getRowStyle().setGravity(Gravity.CENTER_VERTICAL);
		reportView.addColumn(abstractColumn);
		
		ReportColumn debitMnyColumn = new ReportColumn(this, "借方", "debitMny", R.style.report_head, R.style.report_body);
		debitMnyColumn.getHeadStyle().setCellWidth(150);
		debitMnyColumn.getRowStyle().setCellWidth(150);
		debitMnyColumn.getHeadStyle().setColumnWeight(0);
		debitMnyColumn.getRowStyle().setColumnWeight(0);
		debitMnyColumn.getRowStyle().setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		reportView.addColumn(debitMnyColumn);
		
		ReportColumn creditMnyColumn = new ReportColumn(this, "贷方", "creditMny", R.style.report_head, R.style.report_body);
		creditMnyColumn.getHeadStyle().setCellWidth(150);
		creditMnyColumn.getRowStyle().setCellWidth(150);
		creditMnyColumn.getHeadStyle().setColumnWeight(0);
		creditMnyColumn.getRowStyle().setColumnWeight(0);
		creditMnyColumn.getRowStyle().setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		reportView.addColumn(creditMnyColumn);
		
		ReportColumn balanceMnyColumn = new ReportColumn(this, "余额", "balanceMny", R.style.report_head, R.style.report_body);
		balanceMnyColumn.getHeadStyle().setCellWidth(150);
		balanceMnyColumn.getRowStyle().setCellWidth(150);
		balanceMnyColumn.getHeadStyle().setColumnWeight(0);
		balanceMnyColumn.getRowStyle().setColumnWeight(0);
		balanceMnyColumn.getRowStyle().setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		reportView.addColumn(balanceMnyColumn);
		
		getRightButton().setText(R.string.actionbar_query);
		
		m_beginPeriod = UIHelper.toDateString(new Date(), dateFormat);
		m_endPeriod = UIHelper.toDateString(new Date(), dateFormat);
	}
	
	private void invalidateReportView(){
		reportView.post(new Runnable() {
			
			@Override
			public void run() {
				reportView.invalidateViews();
			}
		}); 
	}
	
	private String intListToString(ArrayList<Integer> intlist){
		if(intlist == null || intlist.size() == 0) return "";
		StringBuilder builder = new StringBuilder();
		for(int index = 0; index<intlist.size(); index++){
			builder.append(intlist.get(index) + ",");
		}
		return builder.toString();
	}
	
	private void loadReportData(String beginPeriod, String endPeriod, ArrayList<Integer> kmsx){
		m_beginPeriod = beginPeriod;
		m_endPeriod = endPeriod;
		m_kmsx = kmsx;
		
		WaitingActor.run(this, "正在读取", new Runnable() {
			
			@Override
			public void run() {
				try {
					ReportMxbBean[] beans = ServerRequest.getInstance().queryReportForMXB(
						LoginContext.getInstance().getLoginCorp(), m_beginPeriod, m_endPeriod, intListToString(m_kmsx));
					reportView.clearRows();
					if(beans == null || beans.length == 0) return;
					
					for(ReportMxbBean bean:beans){
						reportView.addRow(new ReportRow()
							.setValue("date", bean.getRq())
							.setValue("subjects", bean.getKm())
							.setValue("abstract", bean.getZy())
							.setValue("debitMny", bean.getJfmny())
							.setValue("creditMny", bean.getDfmny())
							.setValue("balanceMny", bean.getYe()));
					}
					invalidateReportView();
				} catch (Exception e) {
					e.printStackTrace();
					showErrorMsg(e.getMessage());
				}
			}
		});
	}
	
	private String subjectAttrsIndexToString(ArrayList<Integer> subjectAttrsIndexs){
		if(subjectAttrsIndexs == null || subjectAttrsIndexs.equals("")) return "";
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<subjectAttrsIndexs.size(); i++){
			builder.append(subjectAtts[subjectAttrsIndexs.get(i)]);
			if(i<subjectAttrsIndexs.size()-1){
				builder.append(",");
			}
		}
		return builder.toString();
	}
	
	@Override
	public void onRightButtonClick() {
		ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.report_mxb_query, null);
		final EditText beginPeriodView = (EditText)layout.findViewById(R.id.report_mxb_filter_begin);
		final EditText endPeriodView = (EditText)layout.findViewById(R.id.report_mxb_filter_end);
		final EditText kmsxView = (EditText)layout.findViewById(R.id.report_mxb_filter_kmsx);
		beginPeriodView.setText(m_beginPeriod);
		endPeriodView.setText(m_endPeriod);
		kmsxView.setText(subjectAttrsIndexToString(m_kmsx));
		kmsxView.setTag(m_kmsx);
		
		beginPeriodView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					beginPeriodView.requestFocus();
					UIHelper.showDatePickerDialog(ReportMXBActivity.this, dateFormat, beginPeriodView);
				}
				return true;
			}
		});
		endPeriodView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					endPeriodView.requestFocus();
					UIHelper.showDatePickerDialog(ReportMXBActivity.this, dateFormat, endPeriodView);
				}
				return true;
			}
		});
		kmsxView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					kmsxView.requestFocus();
					
					final boolean[] checkeds = new boolean[subjectAtts.length];
					ArrayList<Integer> kmsxIndexs = null;
					if(kmsxView.getTag() == null)
						kmsxIndexs = new ArrayList<Integer>();
					else
						kmsxIndexs = (ArrayList<Integer>) kmsxView.getTag();
					
					for(int i = 0; i<checkeds.length; i++){
						checkeds[i] = (kmsxIndexs.contains(i));
					}
					
					OnMultiChoiceClickListener multiListener = new OnMultiChoiceClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							checkeds[which] = isChecked;
						}
					};
					
					DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							ArrayList<Integer> checkedIndexs = new ArrayList<Integer>();
							for(int i = 0; i<checkeds.length; i++){
								if(checkeds[i]){
									checkedIndexs.add(i);
								}
							}
							if(checkedIndexs.size()>0){
								kmsxView.setTag(checkedIndexs);
								kmsxView.setText(subjectAttrsIndexToString(checkedIndexs));
							} else {
								kmsxView.setTag(null);
								kmsxView.setText("");
							}
						}
					};
					DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					};
					
					AlertDialog.Builder builder = new AlertDialog.Builder(ReportMXBActivity.this);
					builder.setMultiChoiceItems(subjectAtts, checkeds, multiListener);
					builder.setPositiveButton("取消", cancelListener);
					builder.setNegativeButton("确定", okListener).show();

				}
				return true;
			}
		});
		
		showDialog(layout, new OnDialogOKListener() {
			
			@Override
			public boolean onDialogOK(View v) {
				String beginPeriod = beginPeriodView.getText().toString().trim();
				if(beginPeriod.equals("")){
					showErrorMsg("期间起不能为空");
					return false;
				}
				String endPeriod = endPeriodView.getText().toString().trim();
				if(beginPeriod.equals("")){
					showErrorMsg("期间止不能为空");
					return false;
				}
				ArrayList<Integer> kmsx = kmsxView.getTag() != null? (ArrayList<Integer>)kmsxView.getTag():null;
				if(kmsx == null || kmsx.size() == 0){
					showErrorMsg("科目属性不能为空");
					return false;
				}
				loadReportData(beginPeriod, endPeriod, kmsx);
				return true;
			}
		});
	}
}
