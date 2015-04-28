package com.ynt.account;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.base.WaitingActor;
import com.ynt.account.request.ServerRequest;

public class RegisterActivity extends BaseActivity {
	private static final long ValidcodeEffiectiveTime=1000*60*10;  // 验证码生效时间（单位:毫秒）
	
	private EditText editUser;
	private EditText editPassword;
	private EditText editPassword2;
	private EditText editName;
	private EditText editCompany;
	private EditText editValidcode;
	private ViewGroup vgError;
	private TextView tvError;
	private Button btnSubmit;
	private Button btnSendValidcode;
	private String validcode = "";
	private Thread monitorValidcodeThread = null;
	private Runnable monitorValidcode = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		getRightButton().setVisibility(View.INVISIBLE);
		
		editUser = (EditText)findViewById(R.id.regUser);
		editPassword = (EditText)findViewById(R.id.regpwd);
		editPassword2 = (EditText)findViewById(R.id.regpwd2);
		editName = (EditText)findViewById(R.id.regName);
		editCompany = (EditText)findViewById(R.id.regCompany);
		editValidcode = (EditText)findViewById(R.id.regValidcode);
		
		
		vgError = (ViewGroup)findViewById(R.id.reg_err_vg);
		tvError = (TextView)findViewById(R.id.reg_err_txt);
		btnSubmit = (Button)findViewById(R.id.reg_submit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				register();
			}
		});
		btnSendValidcode = (Button)findViewById(R.id.reg_sendValidcode);
		btnSendValidcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendValidcode();
			}
		});
		
		vgError.setVisibility(View.GONE);  // 错误信息默认不显示
		// 监控验证码的失效
		monitorValidcode = new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(ValidcodeEffiectiveTime); 
					validcode = "";
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	private void sendValidcode(){
		vgError.setVisibility(View.GONE);
		try{
			final String regCompany = editCompany.getText().toString();
			if(regCompany.equals(""))
				throw new Exception("公司名称不能为空，请重新输入");
			validcode = "";
			// 中断之前的线程
			if(monitorValidcodeThread != null)
				monitorValidcodeThread.interrupt();

			WaitingActor.run(this, "正在发送", new Runnable() {
				
				@Override
				public void run() {
					try {
						validcode = ServerRequest.getInstance().getValidcode(regCompany);
						// 更新[发送验证码]按钮状态
						updateSendValidcodeButton();
						// 监控验证码失效
						monitorValidcodeThread = new Thread(monitorValidcode);
						monitorValidcodeThread.start();
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
	
	private void updateSendValidcodeButton(){

		btnSendValidcode.post(new Runnable() {
			
			@Override
			public void run() {
				btnSendValidcode.setEnabled(false);
			}
		});
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					for(int i = 60; i>0; i--){
						// 修改[发送验证码]按钮的文字
						changeSendValidcodeButtonText(i);
						Thread.sleep(1000);
					}
					// 启用[发送验证码]按钮
					enableSendValidcodeButton();
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void enableSendValidcodeButton(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				btnSendValidcode.setEnabled(true);
				btnSendValidcode.setText(R.string.reg_sendValidcode_text);
			}
		});		
	}
	
	private void changeSendValidcodeButtonText(final int index){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				btnSendValidcode.setText(String.format("%s(%d)", getResources().getString(R.string.reg_sendValidcode_text), index));
			}
		});
	}
	
	private void register(){
		vgError.setVisibility(View.GONE);
		try{
			final String regUser = editUser.getText().toString().trim();
			final String password = editPassword.getText().toString();
			String password2 = editPassword2.getText().toString();
			final String regName = editName.getText().toString();
			final String regCompany = editCompany.getText().toString();
			String regValidcode = editValidcode.getText().toString();
			
			if(regUser.equals("")){
				throw new Exception("用户名不能为空，请重新输入");
			}
			
			if(password.equals("")){
				throw new Exception("密码不能为空，请重新输入");
			}
			
			if(!password.equals(password2)){
				throw new Exception("两次输入密码不一致，请检查");
			}
			
			if(regCompany.equals("")){
				throw new Exception("公司名称不能为空，请重新输入");
			}
			
			if(regValidcode.equals("")){
				throw new Exception("验证码不能为空，请输入");
			}
			
//			if(!regValidcode.equals(validcode)){
//				throw new Exception("输入的验证码不正确，请重新输入");
//			}
			
			WaitingActor.run(this, "正在注册", new Runnable() {
				
				@Override
				public void run() {
					try {
						if(ServerRequest.getInstance().register(regUser, password, regName, regCompany)){
							finish();
						}
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
