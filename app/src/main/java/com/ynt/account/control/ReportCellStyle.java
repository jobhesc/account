package com.ynt.account.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.ViewGroup.LayoutParams;

import com.ynt.account.R;

public class ReportCellStyle implements Cloneable {
	private int cellWidth;
	private int cellHeight;
	private float columnWeight;
	private int gravity;
	private Drawable background;
	private int textColor;
	private int textSize;
	private int cellPaddingTop;
	private int cellPaddingLeft;
	private int cellPaddingBottom;
	private int cellPaddingRight;
	
	public ReportCellStyle(){
		setCellWidth(LayoutParams.WRAP_CONTENT);
		setCellHeight(LayoutParams.WRAP_CONTENT);
		setGravity(-1);
		setColumnWeight(0.0f);
		setBackground(null);
		setTextColor(0);
		setTextSize(0);
		setCellPaddingTop(0);
		setCellPaddingRight(0);
		setCellPaddingBottom(0);
		setCellPaddingLeft(0);
	}
	
	public static ReportCellStyle createStyle(Context context, int resid){
		ReportCellStyle cellStyle = new ReportCellStyle();
		TypedArray style = context.obtainStyledAttributes(resid, R.styleable.ReportCellStyle);
		cellStyle.setCellWidth(style.getLayoutDimension(R.styleable.ReportCellStyle_cellWidth, "cellWidth"));
		cellStyle.setCellHeight(style.getLayoutDimension(R.styleable.ReportCellStyle_cellHeight, "cellHeight"));
		cellStyle.setColumnWeight(style.getFloat(R.styleable.ReportCellStyle_columnWeight, 0.0f));
		cellStyle.setGravity(style.getInt(R.styleable.ReportCellStyle_android_gravity, -1));
		cellStyle.setBackground(style.getDrawable(R.styleable.ReportCellStyle_cellBackground));
		cellStyle.setTextColor(style.getColor(R.styleable.ReportCellStyle_textColor, 0));
		cellStyle.setTextSize(style.getDimensionPixelSize(R.styleable.ReportCellStyle_textSize, 0));
		cellStyle.setCellPaddingTop(style.getDimensionPixelSize(R.styleable.ReportCellStyle_cellPaddingTop, 0));
		cellStyle.setCellPaddingRight(style.getDimensionPixelSize(R.styleable.ReportCellStyle_cellPaddingRight, 0));
		cellStyle.setCellPaddingBottom(style.getDimensionPixelSize(R.styleable.ReportCellStyle_cellPaddingBottom, 0));
		cellStyle.setCellPaddingLeft(style.getDimensionPixelSize(R.styleable.ReportCellStyle_cellPaddingLeft, 0));
		style.recycle();
		return cellStyle;
	}
	
	public int getCellWidth() {
		return cellWidth;
	}
	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}
	public int getCellHeight() {
		return cellHeight;
	}
	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}
	public float getColumnWeight() {
		return columnWeight;
	}

	public void setColumnWeight(float columnWeight) {
		this.columnWeight = columnWeight;
	}

	public int getGravity() {
		return gravity;
	}
	public void setGravity(int gravity) {
		this.gravity = gravity;
	}
	public Drawable getBackground() {
		return background;
	}
	public void setBackground(Drawable background) {
		this.background = background;
	}
	public int getTextColor() {
		return textColor;
	}
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	public int getTextSize() {
		return textSize;
	}
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getCellPaddingTop() {
		return cellPaddingTop;
	}

	public void setCellPaddingTop(int cellPaddingTop) {
		this.cellPaddingTop = cellPaddingTop;
	}

	public int getCellPaddingLeft() {
		return cellPaddingLeft;
	}

	public void setCellPaddingLeft(int cellPaddingLeft) {
		this.cellPaddingLeft = cellPaddingLeft;
	}

	public int getCellPaddingBottom() {
		return cellPaddingBottom;
	}

	public void setCellPaddingBottom(int cellPaddingBottom) {
		this.cellPaddingBottom = cellPaddingBottom;
	}

	public int getCellPaddingRight() {
		return cellPaddingRight;
	}

	public void setCellPaddingRight(int cellPaddingRight) {
		this.cellPaddingRight = cellPaddingRight;
	}
}
