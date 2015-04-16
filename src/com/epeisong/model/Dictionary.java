package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;

import com.epeisong.data.dao.helper.DictionaryDaoHelper.t_sys_dictionary;

/**
 * 系统字典对象
 * 
 * @author poet
 * 
 */
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 9156317324184945486L;

    /**
     * 角色类型
     */
    public static final int TYPE_ROLE = 1;
    /**
     * 车长
     */
    public static final int TYPE_TRUCK_LENGTH = 2;
    /**
     * 车型
     */
    public static final int TYPE_TRUCK_TYPE = 3;
    /**
     * 计量单位
     */
    public static final int TYPE_UNITS = 4;
    /**
     * 货物类型
     */
    public static final int TYPE_GOODS_TYPE = 5;
    /**
     * 运输方式
     */
    public static final int TYPE_TRANSPORT_MODE = 6;
    /**
     * 送货方式
     */
    public static final int TYPE_DELIVERY_MODE = 7;
    /**
     * 报价类别
     */
    public static final int TYPE_PRICE_CATEGORY = 8;
    /**
     * 计价方式
     */
    public static final int TYPE_VALUATION_MODE = 9;
    /**
     * 时效
     */
    public static final int TYPE_VALIDITY = 10;
    /**
     * 险种
     */
    public static final int TYPE_INSURANCE_TYPE = 11;
    /**
     * 设备种类
     */
    public static final int TYPE_DEVICE_TYPE = 12;
    /**
     * 起重吨位
     */
    public static final int TYPE_JACK_UP_TON = 13;
    /**
     * 荷载吨位
     */
    public static final int TYPE_LOAD_TON = 14;
    /**
     * 仓库类别
     */
    public static final int TYPE_DEPOT_TYPE = 15;
    /**
     * 包装类别
     */
    public static final int TYPE_PACK_TYPE = 16;

    public static final int PUBLISH_GOODSSOURCES_GOODSTYPE = 18;
    
    public static final int TYPE_BANK_TYPE = 19;
    
    /**
     * 问题类型
     */
    public static final int TYPE_QUESTIONS = 20;
    
    

    private int id;
    private String name;
    private int sort_order; // 排序字段
    private int type; // 字典类型：车长、车型等

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort_order() {
        return sort_order;
    }

    public void setSort_order(int sort_order) {
        this.sort_order = sort_order;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Dictionary)) {
            return false;
        }
        Dictionary d = (Dictionary) o;
        return getId() == d.getId() && getType() == d.getType();
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_sys_dictionary.FIELD.ID, id);
        values.put(t_sys_dictionary.FIELD.NAME, name);
        values.put(t_sys_dictionary.FIELD.SORT_ORDER, sort_order);
        values.put(t_sys_dictionary.FIELD.TYPE, type);
        return values;
    }

    public static String getDictName(int type) {
        String name = "";
        switch (type) {
        case TYPE_ROLE:
            name = "角色";
            break;
        case TYPE_TRUCK_LENGTH:
            name = "车长";
            break;
        case TYPE_TRUCK_TYPE:
            name = "车型";
            break;
        case TYPE_UNITS:
            name = "计量单位";
            break;
        case TYPE_GOODS_TYPE:
            name = "货物类型";
            break;
        case TYPE_TRANSPORT_MODE:
            name = "运输方式";
            break;
        case TYPE_DELIVERY_MODE:
            name = "送货方式";
            break;
        case TYPE_PRICE_CATEGORY:
            name = "报价类别";
            break;
        case TYPE_VALUATION_MODE:
            name = "计价方式";
            break;
        case TYPE_VALIDITY:
            name = "时效";
            break;
        case TYPE_INSURANCE_TYPE:
            name = "险种";
            break;
        case TYPE_DEVICE_TYPE:
            name = "设备种类";
            break;
        case TYPE_JACK_UP_TON:
            name = "起重吨位";
            break;
        case TYPE_LOAD_TON:
            name = "荷载吨位";
            break;
        case TYPE_DEPOT_TYPE:
            name = "仓库类别";
            break;
        case TYPE_PACK_TYPE:
            name = "包装类别";
            break;
        case PUBLISH_GOODSSOURCES_GOODSTYPE: // 发布货源
            name = "货物类型";
            break;
        }
        return name;
    }
}
