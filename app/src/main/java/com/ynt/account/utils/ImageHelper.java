package com.ynt.account.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.ynt.account.request.ServerRequest;

public class ImageHelper {

	/**
	 * 给位图加上灰色滤镜
	 * @param context
	 * @param bitmap
	 * @return
	 */
    public static Drawable addGrayColorFilter(Context context, Bitmap bitmap){
    	Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
    	drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    	return drawable;
    }
    
    /**
     * 给图片加圆角
     * @param bitmap
     * @param width
     * @param height
     * @param cornerRadius
     * @return
     */
    public static Bitmap addBitmatRoundRect(Bitmap bitmap, int width, int height, float cornerRadius){
    	Paint paint = new Paint();
    	paint.setAntiAlias(true);  // 去掉锯齿效果
    	
    	Bitmap target = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    	Canvas canvas = new Canvas(target);
    	canvas.drawRoundRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), 
    			cornerRadius, cornerRadius, paint);
    	// 取交集
    	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    	canvas.drawBitmap(bitmap, 0, 0, paint);
    	return target;
    }
    
    public static Bitmap loadBitmapFromServer(String imagePath, int minSideLength, int maxNumOfPixels) throws Exception{
    	byte[] imageBytes = ServerRequest.getInstance().downloadImage(imagePath);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opt);
		
		opt.inSampleSize = computeSampleSize(opt, minSideLength, maxNumOfPixels);
		opt.inJustDecodeBounds = false;	
		return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opt);
    }
    	
	/**
	 * 装载图片
	 * @param imagePath
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static Bitmap loadBitmap(String imagePath, int minSideLength, int maxNumOfPixels){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, opt);
		opt.inSampleSize = computeSampleSize(opt, minSideLength, maxNumOfPixels);
		opt.inJustDecodeBounds = false;			
		// inPurgeable 是控制 Bitmap 对象是否使用软引用机制, 在系统需要的时候可以回收该对象, 如果在此后, 程序又需要使用该对象, 
		// 则系统自动重新 decode 该对象.
		// inInputShareable和 inPurgeable组合使用的, 是控制是否复制 inputfile 对象的引用, 如果不复制, 
		// 那么要实现 inPurgeable 机制就需要复制一份 file 数据, 才能在系统需要 decode 的时候创建一个 bitmap 对象
//		opt.inPurgeable = true;
//		opt.inInputShareable = true;
		return BitmapFactory.decodeFile(imagePath, opt);
	}
	
	/**
	 * 装载图片
	 * @param resolver
	 * @param uri
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static Bitmap loadBitmap(ContentResolver resolver, Uri uri, int minSideLength, int maxNumOfPixels){
		InputStream stream = null;
		InputStream stream2 = null;
		try{
			stream = resolver.openInputStream(uri);
			stream2 = resolver.openInputStream(uri);
			
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(stream, null, opt);
			opt.inSampleSize = computeSampleSize(opt, minSideLength, maxNumOfPixels);
			opt.inJustDecodeBounds = false;
			// inPurgeable 是控制 Bitmap 对象是否使用软引用机制, 在系统需要的时候可以回收该对象, 如果在此后, 程序又需要使用该对象, 
			// 则系统自动重新 decode 该对象.
			// inInputShareable和 inPurgeable组合使用的, 是控制是否复制 inputfile 对象的引用, 如果不复制, 
			// 那么要实现 inPurgeable 机制就需要复制一份 file 数据, 才能在系统需要 decode 的时候创建一个 bitmap 对象
//			opt.inPreferredConfig = Config.RGB_565;
//			opt.inPurgeable = true;
//			opt.inInputShareable = true;
			return BitmapFactory.decodeStream(stream2, null, opt);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		} finally {
			try{
				if(stream != null)
					stream.close();
				if(stream2 != null)
					stream2.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private static int computeSampleSize(BitmapFactory.Options options,  int minSideLength, int maxNumOfPixels) { 
	     int initialSize = computeInitialSampleSize(options, minSideLength, 
	             maxNumOfPixels); 
	  
	     int roundedSize; 
	     if (initialSize <= 8) { 
	         roundedSize = 1; 
	         while (roundedSize < initialSize) { 
	             roundedSize <<= 1; 
	         } 
	     } else { 
	         roundedSize = (initialSize + 7) / 8 * 8; 
	     } 
	  
	     return roundedSize; 
	 } 
	  	  
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) { 
	     double w = options.outWidth; 
	     double h = options.outHeight; 
	  
	     int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels)); 
	     int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength)); 
	  
	     if (upperBound < lowerBound) { 
	         return lowerBound; 
	     } 
	  
	     if ((maxNumOfPixels == -1) && (minSideLength == -1)) { 
	         return 1; 
	     } else if (minSideLength == -1) { 
	         return lowerBound; 
	     } else { 
	         return upperBound; 
	     } 
	}
}
