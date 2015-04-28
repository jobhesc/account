package com.ynt.account.viewpage.transformer;

import com.nineoldandroids.view.ViewHelper;

import android.view.View;

public class ZoomOutTranformer extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		final float scale = 1f + Math.abs(position);
		ViewHelper.setScaleX(view, scale);
		ViewHelper.setScaleY(view, scale);
		ViewHelper.setPivotX(view, view.getWidth() * 0.5f);
		ViewHelper.setPivotY(view, view.getHeight() * 0.5f);
		ViewHelper.setAlpha(view, position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
		if(position == -1){
			ViewHelper.setTranslationX(view, view.getWidth() * -1);
		}
	}

}
