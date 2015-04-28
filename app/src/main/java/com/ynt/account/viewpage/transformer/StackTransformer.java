package com.ynt.account.viewpage.transformer;

import com.nineoldandroids.view.ViewHelper;

import android.view.View;

public class StackTransformer extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		ViewHelper.setTranslationX(view, position < 0 ? 0f : -view.getWidth() * position);
	}

}
