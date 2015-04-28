package com.ynt.account.request;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ynt.account.data.ImgMessageBean;
import com.ynt.account.data.SysMessageBean;

public class ServerRequest implements ServletURL{
	private static final String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
	
	private ServerRequest(){
		
	}
	
	private static class ServerRequestInstance{
		private static ServerRequest instance = new ServerRequest();
	}
	
	/**
	 * 单例的实现，利用类级内部类初始化实例，可以解决线程并发并保证实例化对象有且只有一个
	 * @return
	 */
	public static ServerRequest getInstance(){
		return ServerRequestInstance.instance;
	}
	
	/**
	 * 打开http连接
	 * @param strUrl
	 * @return
	 * @throws Exception
	 */
	private HttpURLConnection openGETConnection(String strUrl) throws Exception{
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// post提交方式
		conn.setRequestMethod("GET");
		// 设置连接主机超时（单位：毫秒）
		conn.setConnectTimeout(5000);
		// 设置从主机读取数据超时（单位：毫秒）
		conn.setReadTimeout(30000);
		
		conn.setRequestProperty("accept", "*/*"); 
		conn.setRequestProperty("connection", "Keep-Alive"); 
		// 连接IE
		conn .setRequestProperty("User-Agent",  "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)"); 
		
		return conn;
	}
	
	/**
	 * 打开http连接
	 * @param strUrl
	 * @return
	 * @throws Exception
	 */
	private HttpURLConnection openPOSTConnection(String strUrl) throws Exception{
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// post提交方式
		conn.setRequestMethod("POST");
		// 设置连接主机超时（单位：毫秒）
		conn.setConnectTimeout(5000);
		// 设置从主机读取数据超时（单位：毫秒）
		conn.setReadTimeout(30000);
		// 设置是否从httpUrlConnection读入
		conn.setDoInput(true);
		// 设置是否向httpUrlConnection输出
		conn.setDoOutput(true);
		// Post 请求不能使用缓存
		conn.setUseCaches(false);
		// 连接IE
		conn .setRequestProperty("User-Agent",  "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)"); 

		return conn;
	}
	
	/**
	 * 读取服务器返回信息
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	private String readServerResponse(InputStream inputStream) throws Exception{
		BufferedReader isr = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		try{
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while((line = isr.readLine()) != null){
				strBuffer.append(line);
			}
			
			try{
				JSONObject jsonObj = new JSONObject(strBuffer.toString());
				String rescode = jsonObj.optString("rescode");
				String resmsg = jsonObj.optString("resmsg");
				if(rescode != null && rescode.equals("fail"))
					throw new Exception(resmsg);
				return resmsg;
			} catch (JSONException ex){
				return strBuffer.toString();
			}
		} finally {
			isr.close();
		}
	}
	
	/**
	 * 浮点数转换为字符串。 0不显示， 例如 233340.21231 显示为233,340.21
	 * @param value
	 * @return
	 */
	private String DoubleAsString(double value){
		return value == 0?"":String.format("%1$,.2f", value);  
	}
	
	public Map<String, String> queryYonyouMembers(String searchKey) throws Exception{
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		JSONObject localJSONObject1 = new JSONObject();
	    localJSONObject1.put("method", "SearchPerson");
	    JSONObject localJSONObject2 = new JSONObject();
	    localJSONObject2.put("cursize", 0);
	    localJSONObject2.put("requestsize", 10);
	    localJSONObject2.put("searchkey", searchKey);
	    JSONObject localJSONObject3 = new JSONObject();
	    localJSONObject3.put("serverid", localJSONObject1);
	    localJSONObject3.put("param", localJSONObject2);
	    
	    String jsonString = localJSONObject3.toString();
	    byte[] arrayOfByte = MessageUtil.Encrypt(jsonString.getBytes("UTF-8"));
	    
		HttpURLConnection conn = null;
		try{
			conn = (HttpURLConnection)new URL("http://l.yonyou.com/MobileService.ashx").openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(arrayOfByte.length));
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(4000);
			conn.setReadTimeout(8000);
			conn.connect();
			
			OutputStream out = conn.getOutputStream();
		    out.write(arrayOfByte);
		    out.flush();
		    
		    int i = conn.getResponseCode();
		    
		    if (200 == i)
		    {
		      InputStream inputStream = conn.getInputStream();
		      resultMap.put("errno", "1");
		      resultMap.put("result", MessageUtil.DesEncrypt(inputStream));
		      inputStream.close();
		    }
		      return resultMap;
		} catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("errno", "0");
	        resultMap.put("result", e.toString());
	        return resultMap;     
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}	
	
	/**
	 * 下载图片
	 * @param imgPath
	 * @return
	 * @throws Exception
	 */
	public File downloadImage(String imgPath, File localFile) throws Exception {
		String strUrl = SERVLET_DOWNLOADIMAGE + "?FilePath=" + imgPath;
		HttpURLConnection conn =  openGETConnection(strUrl); 
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try{
			conn.connect();
			input = new BufferedInputStream(conn.getInputStream());
	    	output = new BufferedOutputStream(new FileOutputStream(localFile));
	    	int count = 0; 
	    	byte[] buffer = new byte[1024];
	    	while((count = input.read(buffer)) != -1){
	    		output.write(buffer, 0, count);
	    	}
	    	output.flush();
	    	return localFile;
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
			if(output != null)
				output.close();
			if(input != null)
				input.close();
		}
	}
	
	/**
	 * 下载图片
	 * @param imgPath
	 * @return
	 * @throws Exception
	 */
	public byte[] downloadImage(String imgPath) throws Exception {
		String strUrl = SERVLET_DOWNLOADIMAGE + "?FilePath=" + imgPath;
		HttpURLConnection conn =  openGETConnection(strUrl); 
		BufferedInputStream input = null;
		ByteArrayOutputStream output = null;
		try{
			conn.connect();
	    	input = new BufferedInputStream(conn.getInputStream());
	    	output = new ByteArrayOutputStream();
	    	int count = 0; 
	    	byte[] buffer = new byte[1024];
	    	while((count = input.read(buffer)) != -1){
	    		output.write(buffer, 0, count);
	    	}
	    	output.flush();
	    	return output.toByteArray();
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
			if(output != null)
				output.close();
			if(input != null)
				input.close();
		}
	}
	
	/**
	 * 标记重拍消息已读
	 * @param messageKeys
	 * @param username
	 * @throws Exception
	 */
	public void markImgMessageReaded(String[] messageKeys, String username) throws Exception{
		if(messageKeys == null || messageKeys.length == 0) return;
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i< messageKeys.length; i++){
			buffer.append(messageKeys[i]);
			if(i<messageKeys.length-1)
				buffer.append(",");
		}
		
		String strUrl = SERVLET_MARKPHOTOMESSAGE + "?imageMsgkeys=" + buffer.toString() +
				"&username=" + username;
		HttpURLConnection conn =  openGETConnection(strUrl); 
		try{
			conn.connect();
			readServerResponse(conn.getInputStream());
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}				
	}
	
	private String JsonArrayToString(String json) throws JSONException{
		if(json == null || json.equals("")) return "";
		JSONArray jsonArray = new JSONArray(json);
		if(jsonArray.length() == 0) return "";
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i<jsonArray.length(); i++){
			buffer.append(jsonArray.optString(i));
			if(i<jsonArray.length() - 1)
				buffer.append(",");
		}
		return buffer.toString();
	}
	
	/**
	 * 查询重拍图片消息
	 * @param corpname
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public ImgMessageBean[] queryImgMessage(String corpname, String username) throws Exception{
		String strUrl = SERVLET_GETPHOTOMESSAGE + "?corpname=" + 
				URLEncoder.encode(corpname, "UTF-8") + // 如果是中文必须做转化，否则服务器端获取的是乱码
				"&username=" + username;
		
		HttpURLConnection conn =  openGETConnection(strUrl); 
		try{
			conn.connect();
			String resmsg = readServerResponse(conn.getInputStream());
			if(resmsg == null || resmsg.equals("")) return null;
			JSONArray jsonArray = new JSONArray(resmsg);
			if(jsonArray.length() == 0) return null;
			ImgMessageBean[] results = new ImgMessageBean[jsonArray.length()];
			JSONObject json = null;
			ImgMessageBean bean = null;
			for(int i = 0; i<jsonArray.length(); i++){
				json = jsonArray.getJSONObject(i);
				bean = new ImgMessageBean();
				bean.setContent(json.optString("message"));
				bean.setIsread(false);
				bean.setIsupload(false);
				bean.setSendtime(json.optString("dealtime"));
				bean.setGrouppaths(JsonArrayToString(json.optString("groupImagePaths")));
				bean.setBadpaths(JsonArrayToString(json.optString("badImagePaths")));
				bean.setImgmsgkey(json.optString("key"));
				results[i] = bean;
			}
			
			return results;
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}			
	}
	
	/**
	 * 查询系统公告
	 * @return
	 * @throws Exception
	 */
	public SysMessageBean[] querySysMessage() throws Exception{
		HttpURLConnection conn =  openGETConnection(SERVLET_GETPUBMESSAGE); 
		try{
			conn.connect();
			String resmsg = readServerResponse(conn.getInputStream());
			if(resmsg == null || resmsg.equals("")) return null;
			JSONArray jsonArray = new JSONArray(resmsg);
			if(jsonArray.length() == 0) return null;
			SysMessageBean[] results = new SysMessageBean[jsonArray.length()];
			JSONObject json = null;
			SysMessageBean bean = null;
			for(int i = 0; i<jsonArray.length(); i++){
				json = jsonArray.getJSONObject(i);
				bean = new SysMessageBean();
				bean.setContent(json.optString("content"));
				bean.setIsread(false);
				bean.setSendtime(json.optString("ts"));
				bean.setSubject(json.getString("title"));
				bean.setSysmsgkey(json.optString("id"));
				results[i] = bean;
			}
			
			return results;
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}		
	}
	
	/**
	 * 查询明细账
	 * @param corpname
	 * @param beginPeriod
	 * @param endPeriod
	 * @param subjAttr
	 * @return
	 * @throws Exception
	 */
	public ReportMxbBean[] queryReportForMXB(String corpname, String beginPeriod, String endPeriod, String subjAttr) throws Exception{
		String strUrl = SERVLET_QUERYMXZB + "?corpname=" + 
				URLEncoder.encode(corpname, "UTF-8") + // 如果是中文必须做转化，否则服务器端获取的是乱码
				"&beginperiod=" + beginPeriod +
				"&endperiod=" + endPeriod + 
				"&kmsx=" + subjAttr;
		HttpURLConnection conn =  openGETConnection(strUrl); 
		try{
			conn.connect();
			String resmsg = readServerResponse(conn.getInputStream());
			if(resmsg == null || resmsg.equals("")) return null;
			JSONArray jsonArray = new JSONArray(resmsg);
			if(jsonArray.length() == 0) return null;
			ReportMxbBean[] results = new ReportMxbBean[jsonArray.length()];
			JSONObject json = null;
			ReportMxbBean bean = null;
			for(int i = 0; i<jsonArray.length(); i++){
				json = jsonArray.getJSONObject(i);
				bean = new ReportMxbBean();
				bean.setRq(json.optString("rq"));
				bean.setKm(json.optString("km"));
				bean.setZy(json.optString("zy"));
				bean.setJfmny(DoubleAsString(json.optDouble("jfmny")));
				bean.setDfmny(DoubleAsString(json.optDouble("dfmny")));
				bean.setYe(DoubleAsString(json.optDouble("ye")));
				results[i] = bean;
			}
			
			return results;
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}		
	}
	
	/**
	 * 查询现金流量表
	 * @param corpname
	 * @param period
	 * @throws Exception
	 */
	public ReportXjllBean[] queryReportForXjll(String corpname, String period) throws Exception{
		String strUrl = SERVLET_QUERYXJLLB + "?corpname=" + 
				URLEncoder.encode(corpname, "UTF-8") + // 如果是中文必须做转化，否则服务器端获取的是乱码
				"&period=" + period;
		HttpURLConnection conn =  openGETConnection(strUrl); 
		try{
			conn.connect();
			String resmsg = readServerResponse(conn.getInputStream());
			if(resmsg == null || resmsg.equals("")) return null;
			JSONArray jsonArray = new JSONArray(resmsg);
			if(jsonArray.length() == 0) return null;
			ReportXjllBean[] results = new ReportXjllBean[jsonArray.length()];
			JSONObject json = null;
			ReportXjllBean bean = null;
			for(int i = 0; i<jsonArray.length(); i++){
				json = jsonArray.getJSONObject(i);
				bean = new ReportXjllBean();
				bean.setProjectname(json.optString("projectname"));
				bean.setBnmny(DoubleAsString(json.optDouble("bnmny")));
				results[i] = bean;
			}
			
			return results;
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}		
	}
	
	/**
	 * 查询利润表
	 * @param corpname
	 * @param period
	 * @throws Exception
	 */
	public ReportLrbBean[] queryReportForLRB(String corpname, String period) throws Exception{
		String strUrl = SERVLET_QUERYLRB + "?corpname=" + 
				URLEncoder.encode(corpname, "UTF-8") + // 如果是中文必须做转化，否则服务器端获取的是乱码
				"&period=" + period;
		HttpURLConnection conn =  openGETConnection(strUrl); 
		try{
			conn.connect();
			String resmsg = readServerResponse(conn.getInputStream());
			if(resmsg == null || resmsg.equals("")) return null;
			JSONArray jsonArray = new JSONArray(resmsg);
			if(jsonArray.length() == 0) return null;
			ReportLrbBean[] results = new ReportLrbBean[jsonArray.length()];
			JSONObject json = null;
			ReportLrbBean bean = null;
			for(int i = 0; i<jsonArray.length(); i++){
				json = jsonArray.getJSONObject(i);
				bean = new ReportLrbBean();
				bean.setProjectname(json.optString("projectname"));
				bean.setBqmny(DoubleAsString(json.optDouble("bqmny")));
				bean.setBnmny(DoubleAsString(json.optDouble("bnmny")));
				results[i] = bean;
			}
			
			return results;
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}		
	}
	
	/**
	 * 查询资产负债表
	 * @param corpname
	 * @param period
	 * @throws Exception
	 */
	public ReportZcfzbBean[] queryReportForZCFZ(String corpname, String period) throws Exception{
		String strUrl = SERVLET_QUERYZCFZB + "?corpname=" + 
				URLEncoder.encode(corpname, "UTF-8") + // 如果是中文必须做转化，否则服务器端获取的是乱码
				"&period=" + period;
		HttpURLConnection conn =  openGETConnection(strUrl); 
		try{
			conn.connect();
			String resmsg = readServerResponse(conn.getInputStream());
			if(resmsg == null || resmsg.equals("")) return null;
			JSONArray jsonArray = new JSONArray(resmsg);
			if(jsonArray.length() == 0) return null;
			ReportZcfzbBean[] results = new ReportZcfzbBean[jsonArray.length()];
			JSONObject json = null;
			ReportZcfzbBean bean = null;
			for(int i = 0; i<jsonArray.length(); i++){
				json = jsonArray.getJSONObject(i);
				bean = new ReportZcfzbBean();
				bean.setProjectname(json.optString("projectname"));
				bean.setQmmny(DoubleAsString(json.optDouble("qmmny")));
				bean.setNcmny(DoubleAsString(json.optDouble("ncmny")));
				results[i] = bean;
			}
			
			return results;
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}		
	}
	
	/**
	 * 上传文件
	 * @param zipfile
	 * @param corpname
	 * @param username
	 * @throws Exception
	 */
	public void uploadImages(File zipfile, String corpname, String username) throws Exception{
		HttpURLConnection conn = openPOSTConnection(SERVLET_UPLOADIMAGE);
		conn.setRequestProperty("Connection", "Keep-Alive");
		// multipart/form-data提高二进制的传输效率
		conn.setRequestProperty("Content-Type",  "multipart/form-data, boundary=" + BOUNDARY);
		try{
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			Map<String, String> paramMap = new HashMap<String, String>();
			// 公司名称
			paramMap.put("corpname", corpname);
			// 上传图片用户名称
			paramMap.put("username", username);
			// 参数写入流
			writeParamsToStream(out, paramMap);
			// 把文件写入流
			writeFileToStream(out, zipfile);
			
			out.flush();  
	        out.close(); 
	        
	        readServerResponse(conn.getInputStream());
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
	
	/**
	 * 把上传文件写入到流中
	 * @param out
	 * @param file
	 * @throws Exception 
	 */
	private static void writeFileToStream(OutputStream out, File file) throws Exception{
        String filename = file.getName();  
        String contentType = "image/jpg";  
        
        StringBuffer strBuf = new StringBuffer();  
        strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
        strBuf.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\"" + filename  + "\"\r\n");  
        strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
        out.write(strBuf.toString().getBytes());  
        
        DataInputStream in = new DataInputStream(new FileInputStream(file));  
        int bytes = 0;  
        byte[] bufferOut = new byte[1024];  
        while ((bytes = in.read(bufferOut)) != -1) {  
            out.write(bufferOut, 0, bytes);  
        }  
        in.close(); 
        
        byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
        out.write(endData);  
	}
	
	/**
	 * 把参数作为form表单字段写入流
	 * @param out
	 * @param paramMap
	 * @throws Exception 
	 */
	private static void writeParamsToStream(OutputStream out, Map<String, String> paramMap) throws Exception{
		if(paramMap == null || paramMap.size() == 0) return;
		StringBuffer strBuf = new StringBuffer(); 
		for(String key:paramMap.keySet()){
	        strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
	        strBuf.append(String.format("Content-Disposition: form-data; name=\"%s\"\r\n\r\n", key));  
	        strBuf.append(paramMap.get(key));
		}
        out.write(strBuf.toString().getBytes()); 
	}
	
	/**
	 * 注册用户
	 * @param user
	 * @param password
	 * @param name
	 * @param company
	 * @return
	 * @throws Exception
	 */
	public boolean register(String user, String password, String name, String company) throws Exception{
		HttpURLConnection conn = openPOSTConnection(SERVLET_REGISTER);
		// 设置请求的内容类型和编码方式，服务器端默认根据该编码方式读取内容，当然服务器端可以通过request.setCharacterEncoding设置编码方式
		conn.setRequestProperty("Content-Type", "application/json;charset=utf-8"); 
		OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
		try{
			JSONObject json = new JSONObject();
			json.put("email", user);
			json.put("password", password);
			json.put("username", name);
			json.put("corpname", company);
			osw.write(json.toString());
			osw.flush();
			
			readServerResponse(conn.getInputStream());
			return true;
		} finally {
			osw.close();
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
	
	/**
	 * 获取验证码
	 * @param corpname
	 * @return
	 * @throws Exception
	 */
	public String getValidcode(String corpname) throws Exception {
		String strUrl = SERVLET_GETIDENTIFY + "?corpname=" + 
				URLEncoder.encode(corpname, "UTF-8"); // 如果是中文必须做转化，否则服务器端获取的是乱码
		HttpURLConnection conn =  openGETConnection(strUrl); 
		try{
			conn.connect();
			String validcode = readServerResponse(conn.getInputStream());
			return validcode.replace("短信发出成功，短信码：", "");
		} finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
	
	/**
	 * 登陆系统
	 * @param user
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean login(String user, String password) throws Exception{
		HttpURLConnection conn = openPOSTConnection(SERVLET_LOGIN);
		OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
		try{
			JSONObject json = new JSONObject();
			json.put("username", user);
			json.put("password", password);
			osw.write(json.toString());
			osw.flush();
			
			readServerResponse(conn.getInputStream());
			return true;
		} finally {
			osw.close();
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
}
