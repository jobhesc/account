package com.ynt.account.request;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MessageUtil
{
  public static String DesEncrypt(InputStream paramInputStream)
    throws Exception
  {
    GZIPInputStream gzipStream = null;
    ByteArrayOutputStream outputStream = null;
    try{
    	gzipStream = new GZIPInputStream(paramInputStream);
    	outputStream = new ByteArrayOutputStream();
    	
	    byte[] arrayOfByte1 = new byte[4096];
	    int count = 0;
	    while((count = gzipStream.read(arrayOfByte1)) != -1){
	    	outputStream.write(arrayOfByte1, 0, count);
	    }
	    
	    byte[] arrayOfByte2 = outputStream.toByteArray();
	    String str = new String(arrayOfByte2, "utf-8");
	    byte[] arrayOfByte3 = new BASE64Decoder().decodeBuffer(str);
	    return new String(DesEncrypt.getInstance().decode(arrayOfByte3));
    } finally {
    	if(gzipStream != null)
    		gzipStream.close();
    	if(outputStream != null)
    		outputStream.close();
    }
    
  }

  public static byte[] Encrypt(String paramString)
    throws Exception
  {
    return Encrypt(paramString.getBytes("utf-8"));
  }

  public static byte[] Encrypt(byte[] paramArrayOfByte)
    throws Exception
  {
    byte[] arrayOfByte1 = DesEncrypt.getInstance().encode(paramArrayOfByte);
    byte[] arrayOfByte2 = new BASE64Encoder().encode(arrayOfByte1).getBytes("utf-8");
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
    localGZIPOutputStream.write(arrayOfByte2);
    localGZIPOutputStream.flush();
    localGZIPOutputStream.close();
    return localByteArrayOutputStream.toByteArray();
  }
}