package com.ynt.account;

import com.ynt.account.utils.ImageHelper;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class ImageView3Activity extends ImageViewActivity {
	@Override
	protected Bitmap loadBitmap(String path) {
		try {
			return ImageHelper.loadBitmap(path,  
						getResources().getDisplayMetrics().widthPixels, 
						getResources().getDisplayMetrics().widthPixels*getResources().getDisplayMetrics().heightPixels);
		} catch (Exception e) {
			Log.e(ImageView3Activity.class.getName(), e.getMessage(), e);
			return null;
		}

	}
}
