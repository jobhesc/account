package com.ynt.account.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.ynt.account.R;

public class AdvanceImageView extends ImageView {
	
	public interface OnCheckListner{
		public void onCheck(View view);
	}
	
	public interface OnTouchImageListener{
		public void onTouchImage(View view);
	}
	
    /**
     * 默认选择框大小
     */
	private static final int DEFAULTCHECKBOXSIZE=30;
	
	private Drawable uncheckDrawable = null;
	private Drawable checkedDrawable = null;
	private Path clipPath;
	private float cornerRadius = 0.0f;   // 圆角的半径
	private boolean showCorner = false;   // 显示圆角
	private boolean showCheckBox = false;  // 显示选择框
	private boolean isChecked = false;    // 选择状态
	private int checkBoxSize;  // 选择框大小
	private int checkBoxPadding; // 选择框离图片边缘距离
	private OnCheckListner onCheckListener;
	private OnTouchImageListener onTouchImageListener;
	
	public AdvanceImageView(Context context) {
		super(context);
	}

	public AdvanceImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
        initAttr(context, attrs);
	}

	public AdvanceImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        initAttr(context, attrs);
	}
	
    private void initAttr(Context context, AttributeSet attrs){
    	TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AdvanceImageView);
    	setCornerRadius(typedArray.getFloat(R.styleable.AdvanceImageView_cornerRadius, 0.0f));
    	setShowCorner(typedArray.getBoolean(R.styleable.AdvanceImageView_showCorner, false));
    	setShowCheckBox(typedArray.getBoolean(R.styleable.AdvanceImageView_showCheckBox, false));
    	setChecked(typedArray.getBoolean(R.styleable.AdvanceImageView_isChecked, false));
    	setCheckBoxSize(typedArray.getDimensionPixelSize(R.styleable.AdvanceImageView_checkBoxSize, DEFAULTCHECKBOXSIZE));
    	setCheckBoxPadding(typedArray.getDimensionPixelSize(R.styleable.AdvanceImageView_checkBoxPadding, 0));
    	typedArray.recycle();
    }
	
	public float getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(float cornerRadius) {
		if(this.cornerRadius != cornerRadius){
			this.cornerRadius = cornerRadius;
			clipPath = null;
		}
	}

	public boolean isShowCorner() {
		return showCorner;
	}

	public void setShowCorner(boolean showCorner) {
		this.showCorner = showCorner;
	}

	public boolean isShowCheckBox() {
		return showCheckBox;
	}

	public void setShowCheckBox(boolean showCheckBox) {
		this.showCheckBox = showCheckBox;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		if(this.isChecked != isChecked){
			this.isChecked = isChecked;
			if(this.isChecked)
				setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);  // 选择后，设置灰色滤镜
			else
				clearColorFilter();
			invalidate();
		}
	}
	
	public int getCheckBoxSize() {
		return checkBoxSize;
	}

	public void setCheckBoxSize(int checkBoxSize) {
		this.checkBoxSize = checkBoxSize;
	}

	public int getCheckBoxPadding() {
		return checkBoxPadding;
	}

	public void setCheckBoxPadding(int checkBoxPadding) {
		this.checkBoxPadding = checkBoxPadding;
	}

	private void setCheckBoxBounds(Drawable drawable){
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		drawable.setBounds(width - checkBoxSize - checkBoxPadding + getPaddingLeft(), 
				checkBoxPadding + getPaddingTop(), width - checkBoxPadding + getPaddingLeft(), 
				checkBoxPadding + getPaddingTop() + checkBoxSize);	
		
	}
	
	private Drawable getUncheckDrawable(){
		if(uncheckDrawable == null){
			uncheckDrawable = getResources().getDrawable(R.drawable.check_normal);
			setCheckBoxBounds(uncheckDrawable);
		}
		return uncheckDrawable;
	}
	
	private Drawable getCheckedDrawable(){
		if(checkedDrawable == null){
			checkedDrawable = getResources().getDrawable(R.drawable.check_checked);
			setCheckBoxBounds(checkedDrawable);
		}
		return checkedDrawable;		
	}

	/**
	 * 获取圆角切块
	 * @return
	 */
	private Path getClipPath(){
		if(clipPath == null){
			clipPath = new Path();  
	        int w = this.getWidth();
	        int h = this.getHeight();
	        clipPath.addRoundRect(new RectF(0, 0, w, h), cornerRadius, cornerRadius, Path.Direction.CW); 
		}
		return clipPath;
	}

	public void setOnCheckListener(OnCheckListner onCheckListener) {
		this.onCheckListener = onCheckListener;
	}

	public void setOnTouchImageListener(OnTouchImageListener onTouchImageListener) {
		this.onTouchImageListener = onTouchImageListener;
	}
	
	private boolean inCheckBoxBounds(int x, int y){
		if(!showCheckBox) return false;
		Rect checkBoxBounds = getUncheckDrawable().getBounds();
		return checkBoxBounds.contains(x, y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(onCheckListener == null && onTouchImageListener == null){
			return super.onTouchEvent(event);
		}
		
		switch(event.getAction()){
		case MotionEvent.ACTION_UP:
			if(inCheckBoxBounds((int)event.getX(), (int)event.getY())){  // 在选择框范围内
				setChecked(!isChecked());
				if(onCheckListener != null){
					onCheckListener.onCheck(this);
				}
			} else if(onTouchImageListener != null){
				onTouchImageListener.onTouchImage(this);
			}
			break;
		}
		return true;
	}
	
    @Override  
    public void draw(Canvas canvas) {  
    	if(showCorner)
    		canvas.clipPath(getClipPath());  
        super.draw(canvas);  
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	drawCheckBox(canvas);
    }
    
    protected void drawCheckBox(Canvas canvas){
    	if(showCheckBox){
    		if(isChecked){
    			getCheckedDrawable().draw(canvas);
    		} else {
    			getUncheckDrawable().draw(canvas);
    		}
    	}
    }
}
