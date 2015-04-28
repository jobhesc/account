package com.ynt.account.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import com.ynt.account.HistoryActivity;

public class UIHelper {
	public static String toDateString(Date date, String dateFormat){
		DateFormat format = new SimpleDateFormat(dateFormat, Locale.CHINA);
		return format.format(date);
	}
	
	/**
	 * 弹出日期选择窗体
	 * @param context
	 * @param dateFormat
	 * @param textView
	 */
	public static void showDatePickerDialog(Context context, final String dateFormat, final TextView textView){
		DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar calendar = Calendar.getInstance(Locale.CHINA);
				calendar.set(year, monthOfYear, dayOfMonth);
				textView.setText(toDateString(calendar.getTime(), dateFormat));
			}
		};

		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		DatePickerDialog dialog = new DatePickerDialog(context, 
				dateSetListener, 
				calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}
}
