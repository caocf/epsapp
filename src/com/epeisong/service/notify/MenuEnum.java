package com.epeisong.service.notify;

import android.util.Log;

 

 

/**
 * 菜单枚举常量
 * @author chenchong
 *
 */
public enum MenuEnum {
	
    HomeMenu("首页","home",0,"epsapp","MainActivity"),
    MsgMenu("消息","msg",0,"epsapp","MainActivity"),
    ContactsMenu("联系人","contact",0,"epsapp","MainActivity"),
    OrderMenu("订单","order",0,"epsapp","MainActivity"),
    MineMenu("我的","mine",0,"epsapp","MainActivity"),
    
    ContList("联系人列表","contlist",0,"contact","ContactsActivity"),
    
    ContFans("关注人页面","contfans",0,"contlist","FansActivity"),
    
    OrderPeihuo("订车配货","orderpeihuo",0,"order","MainActivity"),
    OrderList("订单列表","orderlist",0,"order","InfoFeeListActivity"),
    OrderListExe("订单执行中","orderexe",0,"orderlist","InfoFeeListActivity"),
    OrderListDone("订单已完成","orderdone",0,"orderlist","InfoFeeListActivity"),
    OrderListCancel("订单已取消","ordercancel",0,"orderlist","InfoFeeListActivity");
    
    
    private String menuName;
    private String menuCode;
    private int isShow;//是否显示小红点；1=是 0=否，默认为0
    private String parentCode; //父级代码
    private String actName;//当前菜单对应ACTIVITYname
    
	private MenuEnum(String menuName,String menuCode,int isShow,
			String parentCode, String actName) {
		this.menuName = menuName;
		this.menuCode = menuCode;
		this.isShow = isShow;
		this.parentCode = parentCode;
		this.actName = actName;
	 
	}
	
	public static int getEnumNum() {
		return values().length;
	}
	
	  public static MenuEnum getMenuBean(int ordinal ) {
		  if (ordinal < 0 || ordinal >= values().length) {
			      Log.e("BODYTYPE","获取枚举的索引大小不正确");
			  }
        return values()[ordinal];
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

	

}
