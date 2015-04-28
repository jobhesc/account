package com.ynt.account.control;

import java.util.ArrayList;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ynt.account.R;

public class ReportView extends ListView {
	private ArrayList<ReportColumn> columns = new ArrayList<ReportColumn>();
	private ArrayList<ReportRow> rows = new ArrayList<ReportRow>();
	
	private class ReportAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return rowCount() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = buildRowView();
			}
			
			boolean isHeadRow = (position == 0);  // 是否标题行
			boolean changeStyle = false;   // 是否需要变更样式
			if(convertView.getTag() == null){
				changeStyle = true;
				convertView.setTag(Boolean.valueOf(isHeadRow));
			} else {
				Boolean originalTag = (Boolean) convertView.getTag();
				if(originalTag.booleanValue() != isHeadRow){
					changeStyle = true;
					convertView.setTag(Boolean.valueOf(isHeadRow));
				}
			}
			
			TextView textView = null;
			ReportRow row = null;
			for(ReportColumn column: columns){
				textView = (TextView) convertView.findViewWithTag(column);
				if(isHeadRow){
					textView.setText(column.getText());
					if(changeStyle)
						applyStyle(textView, column.getHeadStyle());
				} else {
					row = findRow(position-1);
					Object value = row.getValue(column.getFieldName());
					textView.setText(value == null?"":value.toString());
					if(changeStyle)
						applyStyle(textView, column.getRowStyle());
				}
			}
			return convertView;
		}
		
	}
	
	protected class ReportRowLayout extends LinearLayout{
		
		public ReportRowLayout(Context context) {
			super(context);
			// 因为一行的单元格中，有的单元格只有有汉字，有的单元格只有数字，这两者的baseline是不一致的
			// 因此为了保证一行的单元格的layout一致，必须设置baselineAligned为false
			setBaselineAligned(false);
		}

		public ReportRowLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
			setBaselineAligned(false);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			
			int count = getChildCount();
			View child = null;
			int maxHeight = 0, childHeight = 0;
			// 取最大行高
			for(int i = 0; i<count; i++){
				child = getChildAt(i);
				childHeight = child.getMeasuredHeight();
				if(maxHeight<childHeight){
					maxHeight = childHeight;
				}
			}
			// 设置子控件最大行高，保证每一个子控件的高度一致
			for(int i = 0; i<count; i++){
				child = getChildAt(i);
				child.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), MeasureSpec.EXACTLY), 
						MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY));
			}
		}
	}
	
    public ReportView(Context context) {
        super(context, null);
    	setAdapter(new ReportAdapter());
    }

    public ReportView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	setAdapter(new ReportAdapter());
    }

    public ReportView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	setAdapter(new ReportAdapter());
    }
    
    public void addColumn(ReportColumn column){
    	columns.add(column);
    }
    
    public void removeColumn(ReportColumn column){
    	columns.remove(column);
    }
    
    public void removeColumn(int index){
    	columns.remove(index);
    }
    
    public void clearColumns(){
    	columns.clear();
    }
    
    public int columnCount(){
    	return columns.size();
    }
    
    public ReportColumn findColumn(int index){
    	return columns.get(index);
    }
    
    public ReportColumn findColumn(String fieldName){
    	for(ReportColumn column: columns){
    		if(column.getFieldName().equals(fieldName))
    			return column;
    	}
    	return null;
    }
    
    public void addRow(ReportRow row){
    	rows.add(row);
    }
    
    public void removeRow(ReportRow row){
    	rows.remove(row);
    }
    
    public void removeRow(int index){
    	rows.remove(index);
    }
    
    public void clearRows(){
    	rows.clear();
    }
    
    public int rowCount(){
    	return rows.size();
    }
    
    public ReportRow findRow(int index){
    	return rows.get(index);
    }
    
    protected TextView createCellControl(ReportColumn column){
    	TextView textView = new TextView(getContext());
		textView.setText("");
		textView.setTag(column);
		return textView;
    }
	
	private ViewGroup createViewGroup(){
		// 线性布局
		LinearLayout layout = new ReportRowLayout(getContext());
    	layout.setOrientation(LinearLayout.HORIZONTAL);
    	return layout;
	}

	protected View buildRowView(){
		ViewGroup viewGroup = createViewGroup();
    	TextView textView = null;
    	for(ReportColumn column: columns){
    		textView = createCellControl(column);
    		viewGroup.addView(textView);
    	}
    	
    	return viewGroup;
    }
	
	@SuppressWarnings("deprecation")
	protected void applyStyle(TextView cellControl, ReportCellStyle style){
		if(style == null) return;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.width = style.getCellWidth();
		layoutParams.height = style.getCellHeight();
		layoutParams.weight = style.getColumnWeight();
		cellControl.setLayoutParams(layoutParams);
		
		cellControl.setGravity(style.getGravity());
		if(style.getBackground() != null){
			// 由于background是绑定在列上的，针对每个单元格需要复制drawable
			Drawable cloneDrawable = style.getBackground().getConstantState().newDrawable();
			cellControl.setBackgroundDrawable(cloneDrawable);
		} else
			cellControl.setBackgroundDrawable(null);
		
		if(style.getTextColor() != 0){
			cellControl.setTextColor(style.getTextColor());
		}
		if(style.getTextSize() != 0){
			cellControl.setTextSize(style.getTextSize());
		}
		cellControl.setPadding(style.getCellPaddingLeft(), 
				style.getCellPaddingTop(), 
				style.getCellPaddingRight(), 
				style.getCellPaddingBottom());
	}
}
