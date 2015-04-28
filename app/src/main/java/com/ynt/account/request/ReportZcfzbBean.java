package com.ynt.account.request;

public class ReportZcfzbBean {
	//项目
	private String projectname ;
	//期末余额
	private String qmmny;
	//年初余额
	private String ncmny;
	
	public String getNcmny() {
		return ncmny;
	}
	public void setNcmny(String ncmny) {
		this.ncmny = ncmny;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public String getQmmny() {
		return qmmny;
	}
	public void setQmmny(String qmmny) {
		this.qmmny = qmmny;
	}
}
