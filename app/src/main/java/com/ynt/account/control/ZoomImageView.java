package com.ynt.account.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZoomImageView extends View {
	private static final long MAXDOUBLECLICKSCRAPTIME=300;  // 双击事件最大时间间隔（毫秒）
	private Drawable mDrawable = null;
	private Matrix matrix = new Matrix();
	private float mImageWidth = 0;  // 原始图片宽度
	private float mImageHeight = 0; // 原始图片高度
	private float mInitScale = 0;  // 初始缩放比例
	private float mInitTranslateX = 0; // 初始缩放位移X坐标
	private float mInitTranslateY = 0; // 初始缩放位移Y坐标
	private float mScale = 0;    // 缩放比例
	private float mTranslateX = 0;  // 位移X值
	private float mTranslateY = 0;  // 位移Y值
	private float mLastFingerDistance = 0;  // 上一次两指之间的距离
	private float mLastTranslateCenterX = 0; // 上一次位移中心点X值
	private float mLastTranslateCenterY = 0; // 上一次位移中心点Y值
	private float mLastCenterDistanceX = 0; // 上一次位移中心点到图片X坐标的距离
	private float mLastCenterDistanceY = 0; // 上一次位移中心点到图片Y坐标的距离
	private int clickCount=0; // 点击屏幕次数
	
	public ZoomImageView(Context context) {
		super(context);
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Drawable getDrawable(){
		return mDrawable;
	}
	
	public void setImageDrawable(Drawable drawable){
		mDrawable = drawable;
		invalidate();
	}
	
	public void setImageBitmap(Bitmap bitmap){
		Drawable drawable = new BitmapDrawable(getResources(), bitmap);
		setImageDrawable(drawable);
	}
	
	/**
	 * 计算两指之间的距离
	 * @param event
	 * @return
	 */
	private float calcPointDistance(MotionEvent event){
		float x1 = event.getX(0);
		float y1 = event.getY(0);
		float x2 = event.getX(1);
		float y2 = event.getY(1);
		
		return (float) Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
	}
	
	/**
	 * 保存移动的快照
	 * @param event
	 */
	private void snapMoveShot(MotionEvent event){
		mLastTranslateCenterX = event.getX();
		mLastTranslateCenterY = event.getY();
	}
	
	/**
	 * 保存缩放的快照
	 * @param event
	 */
	private void snapZoomShot(MotionEvent event){
		mLastFingerDistance = calcPointDistance(event);
		
		float scaledWidth = mImageWidth*mScale;
		float scaledHeight = mImageHeight*mScale;
		int width = getWidth();
		int height = getHeight();
		if(scaledWidth<width || scaledHeight<height){ // 只有图片的宽或者高比控件的宽或者高小，则取图片的中点作为位移中心点
			mLastTranslateCenterX = width/2.0f;
			mLastTranslateCenterY = height/2.0f;
		} else {  // 否则取两指之间的中点作为位移中心点
			mLastTranslateCenterX =  event.getX(1) + (event.getX(0) - event.getX(1))/2.0f;
			mLastTranslateCenterY = event.getY(1) + (event.getY(0) - event.getY(1))/2.0f;
		}
		
		mLastCenterDistanceX = mLastTranslateCenterX-mTranslateX;
		mLastCenterDistanceY = mLastTranslateCenterY-mTranslateY;
	}
	
	/**
	 * 校验边界值
	 */
	private void checkBounds(){
		if(mTranslateX>mInitTranslateX){
			mTranslateX=mInitTranslateX;
		} 
		if(mTranslateX<mInitTranslateX+mImageWidth*mInitScale-mImageWidth*mScale){
			mTranslateX=mInitTranslateX+mImageWidth*mInitScale-mImageWidth*mScale;
		}
		
		if(mTranslateY>=mInitTranslateY){
			mTranslateY=mInitTranslateY;
		}
		if(mTranslateY<=mInitTranslateY+mImageHeight*mInitScale-mImageHeight*mScale){
			mTranslateY=mInitTranslateY+mImageHeight*mInitScale-mImageHeight*mScale;
		}
	}
	
	private void autoZoom(){
		if(mScale!=mInitScale){  // 不是初始缩放比例，则还原为缩放比例
			mScale = mInitScale;
			mTranslateX=mInitTranslateX;
			mTranslateY=mInitTranslateY;
		} else { // 否则为初始缩放比例的2倍
			mScale = mInitScale*2;
			mTranslateX=mInitTranslateX+(mImageWidth*mInitScale-mImageWidth*mScale)/2.0f;
			mTranslateY=mInitTranslateY+(mImageHeight*mInitScale-mImageHeight*mScale)/2.0f;
		}
	}
	
	public void recycle(){
		if(mDrawable != null && mDrawable instanceof BitmapDrawable){
			((BitmapDrawable)mDrawable).getBitmap().recycle();
		}
		mDrawable=null;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mDrawable == null) return super.onTouchEvent(event);
		
		switch(event.getActionMasked()){
		case MotionEvent.ACTION_DOWN:   // 当一个手指触摸屏幕时，触发的是ACTION_DOWN事件而不是ACTION_POINTER_DOWN事件，只有两个以上手指触摸时才触发ACTION_POINTER_DOWN事件
			if(clickCount==0){
				clickCount++;
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						clickCount=0;
					}
				}, MAXDOUBLECLICKSCRAPTIME);
			} else if(clickCount>0){
				autoZoom();  // 双击自动调整缩放
			}
			
			snapMoveShot(event); // 保存移动的快照
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if(event.getPointerCount()==2){   // 两个手指触摸屏幕，表示要进行缩放
				snapZoomShot(event); // 保存缩放的快照
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(event.getPointerCount()==1){  // 一个手指表示要移动
				float newTranslateCenterX = event.getX();
				float newTranslateCenterY = event.getY();
				
				mTranslateX += newTranslateCenterX-mLastTranslateCenterX;
				mTranslateY += newTranslateCenterY-mLastTranslateCenterY;
				checkBounds();
				snapMoveShot(event);
			} else if(event.getPointerCount()==2){
				float fingerDistance = calcPointDistance(event);
				float scale = fingerDistance/mLastFingerDistance;  // 根据两指之间的距离计算缩放比例
				// 计算缩放后 位移中心点X坐标到图片X坐标的距离
				float newCenterDistanceX = mLastCenterDistanceX*scale;
				// 计算缩放后 位移中心点Y坐标到图片Y坐标的距离
				float newCenterDistanceY = mLastCenterDistanceY*scale;
				
				mScale = mScale*scale;
				if(mScale>mInitScale*4) {  // 缩放范围：初始缩放比例的4倍以内
					mScale=mInitScale*4;
				} else if(mScale<mInitScale) {
					mScale=mInitScale;
				} 
				
				mTranslateX = mLastTranslateCenterX-newCenterDistanceX;
				mTranslateY = mLastTranslateCenterY-newCenterDistanceY;
				checkBounds();
				snapZoomShot(event); // 保存缩放的快照
				
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
//			if(event.getPointerCount()==2){
//				mTranslateX = 0;
//				
//			}
			break;
		}
		return true;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(changed){
			initDrawableBounds();
		}
	}
	
	/**
	 * 初始化drawable的位置、大小
	 */
	private void initDrawableBounds(){
		if(mDrawable == null) return;
		int imageWidth = mDrawable.getIntrinsicWidth();
		int imageHeight = mDrawable.getIntrinsicHeight();

		// 由于要缩放，因此先设置drawable为原始大小
		mDrawable.setBounds(0, 0, imageWidth, imageHeight);
		
		int width = getWidth();
		int height = getHeight();

		float scale1 = height*1.0f/imageHeight;
		float scale2 = width*1.0f/imageWidth;
		mScale = scale1>scale2?scale2:scale1;
		mTranslateX=(width-imageWidth*mScale)/2;
		mTranslateY=(height-imageHeight*mScale)/2;
		// 保存初始状态
		mImageWidth = imageWidth;
		mImageHeight = imageHeight;
		mInitScale = mScale;
		mInitTranslateX = mTranslateX;
		mInitTranslateY = mTranslateY;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(mDrawable != null){
			matrix.reset();
			canvas.save();
			matrix.postScale(mScale, mScale);
			matrix.postTranslate(mTranslateX, mTranslateY);
			canvas.setMatrix(matrix);
			mDrawable.draw(canvas);
			canvas.restore();	
		}
		super.onDraw(canvas);
	}
}
