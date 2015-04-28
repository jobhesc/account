package com.ynt.account.control;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NonScrollableGridView extends GridView {

	public NonScrollableGridView(Context context) {
		super(context);
	}

	public NonScrollableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NonScrollableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 由于GridView嵌套在ListView中，会存在滚动条问题而不能显示所有内容，因此需要设置GridView的高度足够大以便显示所有内容不会出现滚动条
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
