package com.ynt.account.base;

import com.ynt.account.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


/** 等待窗口类
 * @author hesc
 *
 */
public class WaitingDialog {
	
	private static Dialog _dialog = null;
	private static TextView textView = null;
	
	public static void show(Context context){
		show(context, "正在载入");
	}
	
	public static void show(Context context, String message){
				
		_dialog = new Dialog(context, R.style.waiting_progress);
		
		ViewGroup layout = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.layout_wait_progress, null);
		textView = (TextView)layout.findViewById(R.id.waiting_tv);
		
		_dialog.setContentView(layout);
		// 不可用返回键取消
		_dialog.setCancelable(false);
		// 单击对话框之外的地方，不能dismiss掉dialog
		_dialog.setCanceledOnTouchOutside(false);
		
		Window window = _dialog.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		// 设定进入window时，隐藏软键盘
		layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
		// 设定布局
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		// 当FLAG_DIM_BEHIND设置后生效。该变量指示后面的窗口变暗的程度。1.0表示完全不透明，0.0表示没有变暗;
		layoutParams.dimAmount=0.0F;
		
		//TextView textView = (TextView)_dialog.getWindow().getDecorView().findViewById(R.id.waiting_tv);
		textView.setText(message);
				
		_dialog.show();
	}
	
	public static void dismiss(){
		if(_dialog != null){
			_dialog.dismiss();   // 该方法是现场安全的
			_dialog=null;
		}
	}
}
