package com.ynt.account.request;

public class ReportLrbBean {
	//项目
	private String projectname ;
	//本期金额
	private String bqmny ;
	//本年累计
	private String bnmny ;
	
	public String getBnmny() {
		return bnmny;
	}
	public void setBnmny(String bnmny) {
		this.bnmny = bnmny;
	}
	public String getBqmny() {
		return bqmny;
	}
	public void setBqmny(String bqmny) {
		this.bqmny = bqmny;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
}
