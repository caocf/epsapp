package com.epeisong.model;

import android.text.TextUtils;

import com.epeisong.logistics.common.Properties;

/**
 * 用户的业务属性
 * 
 * @author poet
 * 
 */
public class UserBusiness {

    private String name;
    private String called;
    private String location;
    private String line;
    private String validity;
    private String goods_type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalled() {
        return called;
    }

    public void setCalled(String called) {
        this.called = called;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public void copyFromUser(User user) {
        setName(user.getUser_type_name());
        switch (user.getUser_type_code()) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            setCalled("整车运输");

            break;

        default:
            break;
        }
    }
}
