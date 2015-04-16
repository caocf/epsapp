package com.epeisong.model;

import java.io.Serializable;

import com.epeisong.logistics.common.Properties;

import android.R.integer;
import android.R.string;

public class Guarantee implements Serializable, Cloneable {
    //public static final int STATUS_VALID = Properties.FREIGHT_STATUS_VALID;
    //public static final int STATUS_INVALID = Properties.FREIGHT_STATUS_INVALID; // 删除和无效都显示已过期
    //public static final int STATUS_DELETED = Properties.FREIGHT_STATUS_DELETED;
    
	private String id;
	private String publisher;
    private String name;
    private int type;
    private String typename;
    private int account;
    private String introduce;
    
    private String mark_url1;
    private String mark_url2;
    
    private int guatype;
    private int status;
    private int customerstatus;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String GetPublisher() {
		return publisher;
	}
    
    public void setPublisher(String publisher) {
    	this.publisher = publisher;
    }
    
    public int getGuaType() {
    	return guatype;
    }
    
    public void setGuaType(int guatype) {
    	this.guatype = guatype;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public String getTypeName() {
        return typename;
    }

    public void setTypeName(String typename) {
        this.typename = typename;
    }
    
    public int getAccount() {
    	return account;
    }
    
    public void setAccount(int account) {
    	this.account = account;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
    
    public String getMark_url1() {
        return mark_url1;
    }

    public void setMark_url1(String mark_url1) {
        this.mark_url1 = mark_url1;
    }
    
    public String getMark_url2() {
        return mark_url2;
    }

    public void setMark_url2(String mark_url2) {
        this.mark_url2 = mark_url2;
    }
    
    public int getStatus() {
    	return status;
    }
    
    public void setStatus(int status) {
    	this.status = status;
    }
    
    public int getCustomerStatus() {
    	return customerstatus;
    }
    
    public void setCustomerStatus(int customerstatus) {
    	this.customerstatus = customerstatus;
    }
    
    
}
