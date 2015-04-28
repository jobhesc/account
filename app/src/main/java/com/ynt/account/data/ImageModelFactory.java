package com.ynt.account.data;

import android.content.Context;

public class ImageModelFactory {
	
	public static ImageModel createModel(Context context){
		return new ImageModel();
	}
	
	public static IImagePersistence createPersistence(Context context){
//		return new SqlImagePersistence(context);
		return new SqlcipherImagePersistence(context);
	}
}
