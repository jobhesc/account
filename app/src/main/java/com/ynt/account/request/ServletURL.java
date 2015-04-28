package com.ynt.account.request;

public interface ServletURL {
	/**
	 * 服务器地址
	 */
	public static final String SERVERADDR="http://192.168.1.104:89";
	/**
	 * 登陆的servlet地址
	 */
	public static final String SERVLET_LOGIN= SERVERADDR + "/ynt/loginServlet";
	/**
	 * 注册的servlet地址
	 */
	public static final String SERVLET_REGISTER= SERVERADDR + "/ynt/registerServlet";
	/**
	 * 获取验证码的servlet地址
	 */
	public static final String SERVLET_GETIDENTIFY= SERVERADDR + "/ynt/getIdentifyServlet";
	/**
	 * 查询资产负债表的servlet地址
	 */
	public static final String SERVLET_QUERYZCFZB= SERVERADDR + "/ynt/zcfzbServlet";
	/**
	 * 查询利润表的servlet地址
	 */
	public static final String SERVLET_QUERYLRB= SERVERADDR + "/ynt/lrbServlet";
	/**
	 * 查询现金流量表的servlet地址
	 */
	public static final String SERVLET_QUERYXJLLB= SERVERADDR + "/ynt/xjllbServlet";
	/**
	 * 查询明细账表的servlet地址
	 */
	public static final String SERVLET_QUERYMXZB= SERVERADDR + "/ynt/mxzServlet";
	/**
	 * 图片上传的servlet地址
	 */
	public static final String SERVLET_UPLOADIMAGE= SERVERADDR + "/ynt/ImageUploadServlet";
	/**
	 * 图片下载的servlet地址
	 */
	public static final String SERVLET_DOWNLOADIMAGE= SERVERADDR + "/ynt/ImageDownloadServlet";
	/**
	 * 获取系统公告地址
	 */
	public static final String SERVLET_GETPUBMESSAGE= SERVERADDR + "/ynt/getPublicMessageServlet";
	/**
	 * 获取重拍图片消息地址
	 */
	public static final String SERVLET_GETPHOTOMESSAGE = SERVERADDR + "/ynt/ImageRephotoMsgServlet";
	/**
	 * 标记重拍图片已读地址
	 */
	public static final String SERVLET_MARKPHOTOMESSAGE = SERVERADDR + "/ynt/ImageRephotoMarkServlet";
}
