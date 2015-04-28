package com.ynt.account.base;

import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 布局动画类
 * @author hesc
 *
 */
public class LayoutAnimation {
	
	public static final long DEFAULT_DURATION = 300L;
	private ViewGroup viewGroup;
	private AnimationSet animationSet;
	
	public LayoutAnimation(ViewGroup viewGroup){
		this.viewGroup = viewGroup;
		initAnimation();
	}
	
	private void initAnimation(){
		// AnimationSet 可以包含多个Animation，但都是在同一个时间执行的，是并行，不是串行执行的
		animationSet = new AnimationSet(false);
		LayoutAnimationController controller = new LayoutAnimationController(animationSet);
		viewGroup.setLayoutAnimation(controller); 
	}
	
	/**
	 * 设置viewGroup显示的动画效果
	 * @param animation
	 */
	public LayoutAnimation addAnimation(Animation animation){
		animationSet.addAnimation(animation);
		return LayoutAnimation.this;
	}
	
	
	/**
	 * 设置viewGroup渐变透明的效果
	 * @param fromAlpha 动画起始时透明度
	 * @param toAlpha 动画结束时透明度
	 */
	public LayoutAnimation setAlpha(float fromAlpha, float toAlpha){
		return setAlpha(fromAlpha, toAlpha, DEFAULT_DURATION);
	}
	
	/**
	 * 设置viewGroup渐变透明的效果
	 * @param fromAlpha 动画起始时透明度
	 * @param toAlpha 动画结束时透明度
	 * @param durationMillis 动画持续时间
	 */
	public LayoutAnimation setAlpha(float fromAlpha, float toAlpha, long durationMillis){
		AlphaAnimation animation = new AlphaAnimation(fromAlpha, toAlpha);
		animation.setDuration(durationMillis);
		return addAnimation(animation);
	}
	
	/**
	 * 设置viewGroup画面平移的动画效果
	 * @param fromXDelta 动画起始时 X坐标上的位置
	 * @param toXDelta   动画结束时 X坐标上的位置
	 * @param fromYDelta 动画起始时 Y坐标上的位置 
	 * @param toYDelta   动画结束时 Y坐标上的位置
	 */
	public LayoutAnimation setTranslate(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta){
		return setTranslate(fromXDelta, toXDelta, fromYDelta, toYDelta, DEFAULT_DURATION);
	}
	
	/**
	 * 设置viewGroup画面平移的动画效果
	 * @param fromXDelta 动画起始时 X坐标上的位置
	 * @param toXDelta   动画结束时 X坐标上的位置
	 * @param fromYDelta 动画起始时 Y坐标上的位置 
	 * @param toYDelta   动画结束时 Y坐标上的位置
	 * @param durationMillis 动画持续时间
	 */
	public LayoutAnimation setTranslate(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long durationMillis){
		TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
		animation.setDuration(durationMillis);
		return addAnimation(animation);
	}
	
	/**
	 * 设置viewGroup尺寸伸缩的动画效果
	 * @param fromX 动画起始时 X坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 * @param toX   动画结束时 X坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 * @param fromY 动画起始时Y坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 * @param toY   动画结束时Y坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 */
	public LayoutAnimation setScale(float fromX, float toX, float fromY, float toY){
		return setScale(fromX, toX, fromY, toY, DEFAULT_DURATION);
	}
	
	/**
	 * 设置viewGroup尺寸伸缩的动画效果
	 * @param fromX 动画起始时 X坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 * @param toX   动画结束时 X坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 * @param fromY 动画起始时Y坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 * @param toY   动画结束时Y坐标上的伸缩尺寸, 0.0表示收缩到没有, 1.0表示正常无伸缩, 值小于1.0表示收缩, 值大于1.0表示放大
	 * @param durationMillis 动画持续时间
	 */
	public LayoutAnimation setScale(float fromX, float toX, float fromY, float toY, long durationMillis){
		ScaleAnimation animation = new ScaleAnimation(fromX, toX, fromY, toY);
		animation.setDuration(durationMillis);
		return addAnimation(animation);
	}
	
	
	/**
	 * 设置viewGroup画面角度变化的动画效果
	 * @param fromDegrees 动画起始时物件的角度  
	 * @param fromDegrees 动画结束时物件旋转的角度  可以大于360度  
	 */
	public LayoutAnimation setRotate(float fromDegrees, float toDegrees){
		return setRotate(fromDegrees, toDegrees, DEFAULT_DURATION);
	}
	
	/**
	 * 设置viewGroup画面角度变化的动画效果
	 * @param fromDegrees 动画起始时物件的角度  
	 * @param fromDegrees 动画结束时物件旋转的角度  可以大于360度  
	 * @param durationMillis 动画持续时间
	 */
	public LayoutAnimation setRotate(float fromDegrees, float toDegrees, long durationMillis){
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees);
		animation.setDuration(durationMillis);
		return addAnimation(animation);
	}
	
	/**
	 * 解析动画资源ID，并应用到viewGroup中
	 * @param context
	 * @param animId
	 */
	public LayoutAnimation inflater(Context context, int animId){
		Animation animation = AnimationUtils.loadAnimation(context, animId);
		return addAnimation(animation);
	}
}
