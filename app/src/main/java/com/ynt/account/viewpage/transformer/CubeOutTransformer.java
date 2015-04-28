package com.ynt.account.viewpage.transformer;

import com.nineoldandroids.view.ViewHelper;

import android.view.View;

public class CubeOutTransformer extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		ViewHelper.setPivotX(view, position < 0f ? view.getWidth() : 0f);
		ViewHelper.setPivotY(view, view.getHeight() * 0.5f);
		ViewHelper.setRotationY(view, 90f * position);
	}

	@Override
	public boolean isPagingEnabled() {
		return true;
	}

}
