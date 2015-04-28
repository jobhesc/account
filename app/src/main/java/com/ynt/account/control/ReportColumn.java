package com.ynt.account.control;

import android.content.Context;

public class ReportColumn {
	private String text;
	private String fieldName;
	private Object userObject;
	private ReportCellStyle headStyle;
	private ReportCellStyle rowStyle;
	
	public ReportColumn(){
		this("", "");
	}
	
	public ReportColumn(String text, String fieldName){
		this(text, fieldName, new ReportCellStyle(), new ReportCellStyle());
	}
	
	public ReportColumn(Context context, String text, String fieldName, int headStyleId, int rowStyleId){
		this(text, fieldName, 
				ReportCellStyle.createStyle(context, headStyleId), 
				ReportCellStyle.createStyle(context, rowStyleId));
	}
	
	public ReportColumn(String text, String fieldName, ReportCellStyle headStyle, ReportCellStyle rowStyle){
		this.text = text;
		this.fieldName =fieldName;
		this.headStyle = headStyle;
		this.rowStyle = rowStyle;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public ReportCellStyle getHeadStyle() {
		return headStyle;
	}

	public void setHeadStyle(ReportCellStyle headStyle) {
		this.headStyle = headStyle;
	}

	public ReportCellStyle getRowStyle() {
		return rowStyle;
	}

	public void setRowStyle(ReportCellStyle rowStyle) {
		this.rowStyle = rowStyle;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
	
	@Override
	public int hashCode() {
		return fieldName.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof ReportColumn){
			return o.hashCode() == hashCode();
		}
		return false;
	}
}
