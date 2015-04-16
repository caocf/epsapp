package com.epeisong.service.notify;

import java.io.Serializable;

//显示菜单bean
public class MenuBean implements Serializable{
  
    
    private String menuName;
    private String menuCode;
    private int isShow;//是否显示小红点；1=是 0=否，默认为0
    private String parentCode; //父级代码
    private String actName;//当前菜单对应ACTIVITYname
    private String curLoginPhone;//当前登录用户名
    
    public MenuBean() {
    	
    }
    
   public MenuBean(String menuName,String menuCode,int isShow,
		   String parentCode,String actName,String loginPhone) {
    	this.menuCode = menuCode;
    	this.menuName = menuName;
    	this.isShow = isShow;
    	this.parentCode = parentCode;
    	this.actName = actName;
    	this.curLoginPhone = loginPhone;
    }
    
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public int getIsShow() {
		return isShow;
	}
	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}

	public String getCurLoginPhone() {
		return curLoginPhone;
	}

	public void setCurLoginPhone(String curLoginPhone) {
		this.curLoginPhone = curLoginPhone;
	}
   

}
