package com.ynt.account.task;

import java.util.ArrayList;

import com.ynt.account.base.BaseActivity;
import com.ynt.account.data.ImgMessageBean;
import com.ynt.account.data.SqlMessagePersistence;
import com.ynt.account.data.SysMessageBean;
import com.ynt.account.request.ServerRequest;

import android.content.Context;
import android.os.AsyncTask;

public class MessageTask {
	private SqlMessagePersistence persistence;
	
	public MessageTask(Context context){
		persistence = new SqlMessagePersistence(context);
	}

	/**
	 * 从服务器上下载系统公告，并更新到本地系统中
	 * @return
	 * @throws Exception
	 */
	public Boolean refreshSysMessage() throws Exception {
		// 首先通过网络下载系统公告
		SysMessageBean[] remoteBeans = ServerRequest.getInstance().querySysMessage();
		if(remoteBeans == null || remoteBeans.length == 0) return false;
		
		// 查找本app的所有系统公告
		SysMessageBean[] localBeans = persistence.readSysMessages(0, Integer.MAX_VALUE, true);
		// 对比网络下载的公告与本地公告，找出差异的公告
		ArrayList<SysMessageBean> diffBeans = new ArrayList<SysMessageBean>();
		for(SysMessageBean remoteBean: remoteBeans){
			if(localBeans == null || localBeans.length == 0){
				diffBeans.add(remoteBean);
			} else {
				boolean isfinded = false;
				for(SysMessageBean localBean: localBeans){
					if(localBean.getSysmsgkey().equals(remoteBean.getSysmsgkey())){
						isfinded = true;
						break;
					}
				}
				if(!isfinded){
					diffBeans.add(remoteBean);
				}
			}
		}
		// 保存差异化公告到本地系统中
		if(diffBeans.size() > 0){
			persistence.InsertSysMessages(diffBeans.toArray(new SysMessageBean[diffBeans.size()]));
			return true;
		} 
		
		return false;
	}
	 
	/**
	 * 刷新重拍图片消息
	 * @param corpname
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public Boolean refreshImgMessage(String corpname, String username) throws Exception {
		// 首先通过网络下载图片重拍消息
		ImgMessageBean[] remoteBeans = ServerRequest.getInstance().queryImgMessage(corpname, username);
		if(remoteBeans == null || remoteBeans.length == 0) return false;
		// 保存到本地数据库
		persistence.InsertImgMessages(remoteBeans);
		// 标记重拍图片消息已读
		String[] messageKeys = new String[remoteBeans.length];
		for(int i = 0; i< remoteBeans.length; i++)
			messageKeys[i] = remoteBeans[i].getImgmsgkey();
		
		ServerRequest.getInstance().markImgMessageReaded(messageKeys, username);
		return true;
	}
}
