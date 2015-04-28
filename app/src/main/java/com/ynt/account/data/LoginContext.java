package com.ynt.account.data;

import java.util.Date;

public class LoginContext {
	private String loginUser;
	private String loginCorp;
	private Date loginTime;
	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getLoginCorp() {
		return loginCorp;
	}

	public void setLoginCorp(String loginCorp) {
		this.loginCorp = loginCorp;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	private static LoginContext m_instance = null;
	
	private LoginContext(){}
	
	public synchronized static LoginContext getInstance(){
		if(m_instance == null)
			m_instance = new LoginContext();
		return m_instance;
	}
}
