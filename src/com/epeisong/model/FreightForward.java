package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;

import com.epeisong.data.dao.helper.FreightForwardDaoHelper.t_freight_forward;
import com.epeisong.logistics.common.Properties;

/**
 * 收到别人发来的车源、货源 或自动收到的联系人发布时自动推送过来的车源货源
 * 
 * @author poet
 * 
 */
public class FreightForward implements Serializable, Comparable<FreightForward> {

    private static final long serialVersionUID = -6010622285217216262L;

    public static final int STATUS_NORMAL = Properties.FREIGHT_DELIVERY_RECEIVER_STATUS_NORMAL;
    public static final int STATUS_DELETED = Properties.FREIGHT_DELIVERY_RECEIVER_STATUS_DELETED;

    private String id;
    private long serial;
    private long forward_create_time;
    private long forward_update_time;
    private String user_id; // 转发者的id
    private String user_show_name; // 转发者的名称

    private Freight freight;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Freight getFreight() {
        return freight;
    }

    public void setFreight(Freight freight) {
        this.freight = freight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }

    public long getForward_create_time() {
        return forward_create_time;
    }

    public void setForward_create_time(long forward_create_time) {
        this.forward_create_time = forward_create_time;
    }

    public long getForward_update_time() {
        return forward_update_time;
    }

    public void setForward_update_time(long forward_update_time) {
        this.forward_update_time = forward_update_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_show_name() {
        return user_show_name;
    }

    public void setUser_show_name(String user_show_name) {
        this.user_show_name = user_show_name;
    }

    @Override
    public int compareTo(FreightForward another) {
        if (another == null) {
            return 1;
        }
        int dTime = (int) (this.getForward_create_time() - another.getForward_create_time());
        if (dTime == 0) {
            return (int) (this.getSerial() - another.getSerial());
        }
        return dTime;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_freight_forward.FIELD.ID, this.getId());
        values.put(t_freight_forward.FIELD.SERIAL, this.getSerial());
        values.put(t_freight_forward.FIELD.USER_ID, this.getUser_id());
        values.put(t_freight_forward.FIELD.USER_NAME, this.getUser_show_name());
        values.put(t_freight_forward.FIELD.FORWARD_CREATE_TIME, this.getForward_create_time());
        values.put(t_freight_forward.FIELD.FORWARD_UPDATE_TIME, this.getForward_update_time());
        Freight f = this.getFreight();
        if (f != null) {
            values.put(t_freight_forward.FIELD.FREIGHT_ID, f.getId());
            values.put(t_freight_forward.FIELD.SENDER_ID, f.getUser_id());
            values.put(t_freight_forward.FIELD.CREATE_TIME, f.getCreate_time());
            values.put(t_freight_forward.FIELD.UPDATE_TIME, f.getUpdate_time());
            values.put(t_freight_forward.FIELD.START_REGION, f.getStart_region());
            values.put(t_freight_forward.FIELD.START_REGION_CODE, f.getStart_region_code());
            values.put(t_freight_forward.FIELD.END_REGION, f.getEnd_region());
            values.put(t_freight_forward.FIELD.END_REGION_CODE, f.getEnd_region_code());
            values.put(t_freight_forward.FIELD.TYPE, f.getType());
            values.put(t_freight_forward.FIELD.GOODS_TYPE, f.getGoods_type());
            values.put(t_freight_forward.FIELD.GOODS_TYPE_NAME, f.getGoods_type_name());
            values.put(t_freight_forward.FIELD.GOODS_TON, f.getGoods_ton());
            values.put(t_freight_forward.FIELD.GOODS_SQUARE, f.getGoods_square());
            values.put(t_freight_forward.FIELD.GOODS_EXCEED, f.getGoods_exceed());
            values.put(t_freight_forward.FIELD.TRUCK_TYPE_CODE, f.getTruck_type_code());
            values.put(t_freight_forward.FIELD.TRUCK_TYPE_NAME, f.getTruck_type_name());
            values.put(t_freight_forward.FIELD.TRUCK_LENGTH_CODE, f.getTruck_length_code());
            values.put(t_freight_forward.FIELD.TRUCK_LENGTH_NAME, f.getTruck_length_name());
            values.put(t_freight_forward.FIELD.TRUCK_SPARE_METER, f.getTruck_spare_meter());
            values.put(t_freight_forward.FIELD.STATUS, f.getStatus());
            values.put(t_freight_forward.FIELD.FREIGHT_OWNER_NAME, f.getOwner_name());
            values.put(t_freight_forward.FIELD.INFO_COST, f.getInfo_cost());
        }
        return values;
    }
}
