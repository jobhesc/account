package com.ynt.account.base;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ynt.account.R;

public abstract class BaseActivity extends ActionBarActivity {
    public interface OnDialogOKListener {
        boolean onDialogOK(View v);
    }
	
	private ViewGroup actionBarLayout;
	private Button leftButton;   	// 左边按钮
	private Button rightButton;		// 右边按钮
	private TextView titleView;     // 标题
	private Toast m_toast = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.app_bg));
		
		LayoutInflater inflater = LayoutInflater.from(this);
		actionBarLayout = (ViewGroup)inflater.inflate(R.layout.simple_actionbar, null);
		// 标题
		titleView = (TextView)actionBarLayout.findViewById(R.id.actionBar_title);
		titleView.setText(getTitle());
		// 左边按钮
		leftButton = (Button)actionBarLayout.findViewById(R.id.actionBar_leftButton);
		leftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				onLeftButtonClick();
			}
		});
		// 右边按钮
		rightButton = (Button)actionBarLayout.findViewById(R.id.actionBar_rightButton);
		rightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				onRightButtonClick();
			}
		});
		// 显示到ActionBar
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,  
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME |  
                ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(actionBarLayout, 
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}	
	/**
	 * 左边按钮点击事件
	 */
	public void onLeftButtonClick(){
		finish();  // 返回上一级
	}
	/**
	 * 右边按钮点击事件
	 */
	public void onRightButtonClick(){
		
	}

	public ViewGroup getActionBarLayout() {
		return actionBarLayout;
	}

	public Button getLeftButton() {
		return leftButton;
	}

	public Button getRightButton() {
		return rightButton;
	}
	
	private Toast getErrorToast(){
		if(m_toast == null){
			m_toast = new Toast(this);
			TextView errorView = new TextView(this);
			errorView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error_img, 0, 0, 0);
			errorView.setBackgroundResource(R.drawable.login_error_bg);
			errorView.setTextColor(Color.WHITE);
			m_toast.setView(errorView);
			m_toast.setDuration(Toast.LENGTH_LONG);
		}
		return m_toast;
	}
	
	/**
	 * 显示错误信息
	 * @param errorMsg
	 */
	public void showErrorMsg(final String errorMsg){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				((TextView)getErrorToast().getView()).setText(errorMsg);
				getErrorToast().show();
			}
		});
		
	}
	
	public void showConfirmDialog(String message){
		
	}
	
	public void showDialog(View contentView, final OnDialogOKListener okClickListner){
		// 自定义弹出框两种写法：
		// 1、使用Style方式，通过new Dialog(this, R.style.ynt_dialog)方式实例化；并且后面使用dialog.setContentView()设置视图布局
		// 2、使用new AlertDialog.Builder(this).create()方式实例化，后面使用dialog.getWindow().setContentView()设置窗体视图布局
		// 注意dialog.getWindow().setContentView()必须放在dialog.show()方法后面，否则会报错
		final Dialog filterDialog = new Dialog(this, R.style.ynt_dialog);
//		final Dialog filterDialog = new AlertDialog.Builder(this).create();
		
		ViewGroup layout = (ViewGroup) getLayoutInflater().inflate(R.layout.base_dialog, null);
		Button btnOk = (Button) layout.findViewById(R.id.dialog_ok);
		Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
		ViewGroup contentlayout = (ViewGroup)layout.findViewById(R.id.dialog_content);
		contentlayout.addView(contentView);
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(okClickListner != null && okClickListner.onDialogOK(v))
					filterDialog.dismiss();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				filterDialog.dismiss();
			}
		});

		// 单击对话框之外的地方，不能dismiss掉dialog
		filterDialog.setCanceledOnTouchOutside(false);
		
		filterDialog.setContentView(layout);
		filterDialog.show();
		// 这一句必须放在show()方法后面，否则就会报"requestFeature() must before add content"错误
//		filterDialog.getWindow().setContentView(layout);  
	}
}
