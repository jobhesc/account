package com.ynt.account.control;

import java.util.HashMap;

public class ReportRow {
	private HashMap<String, Object> rowMap = new HashMap<String, Object>();
	private Object userObject;
	
	public ReportRow setValue(String fieldName, Object value){
		rowMap.put(fieldName, value);
		return ReportRow.this;
	}
	
	public Object getValue(String fieldName){
		return rowMap.get(fieldName);
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
}
