package com.ynt.account;

import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.base.Profile;
import com.ynt.account.base.WaitingActor;
import com.ynt.account.data.LoginContext;
import com.ynt.account.request.ServerRequest;

public class LoginActivity extends BaseActivity {

	private EditText editUser;
	private EditText editPassword;
	private ViewGroup vgError;
	private TextView tvError;
	private Button btnSubmit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		getLeftButton().setVisibility(View.INVISIBLE);
		getRightButton().setText("注册");
		
		editUser = (EditText)findViewById(R.id.loginUser);
		editPassword = (EditText)findViewById(R.id.loginPwd);
		vgError = (ViewGroup)findViewById(R.id.login_err_vg);
		tvError = (TextView)findViewById(R.id.login_err_txt);
		btnSubmit = (Button)findViewById(R.id.login_submit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();
			}
		});
		
		vgError.setVisibility(View.GONE);  // 错误信息默认不显示
		editUser.setText(Profile.getInstance(getApplication()).getLastLoginUser());  // 设置默认用户
	}
	
	@Override
	public void onRightButtonClick() {
		startActivity(new Intent(this, RegisterActivity.class));
	}
	
	private void login(){
		vgError.setVisibility(View.GONE);
		try{
			final String loginUser = editUser.getText().toString().trim();
			final String password = editPassword.getText().toString();
			if(loginUser.equals("")){
				throw new Exception("用户名不能为空，请重新输入");
			}
			
			WaitingActor.run(this, "正在登陆", new Runnable() {
				
				@Override
				public void run() {
					try {

//						if(ServerRequest.getInstance().login(loginUser, password)){
							LoginContext.getInstance().setLoginUser(loginUser);
							LoginContext.getInstance().setLoginTime(new Date());
							LoginContext.getInstance().setLoginCorp("华科芯创");
							// 保存用户
							Profile.getInstance(getApplication()).setLastLoginUser(loginUser);
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);
							finish();
//						}
					} catch(Exception e){
						e.printStackTrace();
						showErrorMessage(e.getMessage());
					}
				}
			});
			
		} catch(Exception e){
			e.printStackTrace();
			showErrorMessage(e.getMessage());
		}
	}
	
	private void showErrorMessage(final String errMsg){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				vgError.setVisibility(View.VISIBLE);
				tvError.setText(errMsg);
			}
		});
	}
}
