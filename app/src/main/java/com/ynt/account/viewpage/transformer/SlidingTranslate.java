package com.ynt.account.viewpage.transformer;

import com.nineoldandroids.view.ViewHelper;

import android.view.View;

public class SlidingTranslate extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		ViewHelper.setX(view, view.getWidth()*-position);
	}

}
