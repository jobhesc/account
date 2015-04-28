package com.ynt.account.viewpage.transformer;

import com.nineoldandroids.view.ViewHelper;

import android.view.View;

public class AccordionTransformer extends ABaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		ViewHelper.setPivotX(view, position < 0 ? 0 : view.getWidth());
		ViewHelper.setScaleX(view, position < 0 ? 1f + position : 1f - position);
	}

}
