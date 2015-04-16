package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;
import android.text.TextUtils;

import com.epeisong.data.dao.helper.FreightDaoHelper.t_freight;
import com.epeisong.logistics.common.Properties;

/**
 * 车源、货源
 * 
 * @author poet
 * 
 */
public class Freight implements Serializable, Comparable<Freight> {

    private static final long serialVersionUID = 1L;

    public static final int TYPE_GOODS = Properties.FREIGHT_TYPE_GOODS;
    public static final int TYPE_TRUCK = Properties.FREIGHT_TYPE_VEHICLE;
    public static final int TYPE_ALL = Properties.FREIGHT_TYPE_ALL;

    public static final int FORWARD_TO_BLACKBOARD_NOT = 0;
    public static final int FORWARD_TO_BLACKBOARD_ALREADY = 1;

    public final int MESSAGE_HIDE = Properties.MARKET_SCREEN_NOT_ALLOW_TO_SHOW;
    public final int MESSAGE_SHOW = Properties.MARKET_SCREEN_ALLOW_TO_SHOW;

    private String id;
    private long serial; // 序列号，服务器针对每个用户能看到的车源、货源进行连续的编号，用于同步
    private String user_id; // 发布者
    private String owner_name; // 发布者对外显示的名字
    private long create_time;
    private long update_time;
    private String start_region;
    private int start_region_code;
    private String end_region;
    private int end_region_code;
    private int type; // 车源、货源
    private int goods_type; // 货源类型
    private String goods_type_name;
    private double goods_ton; // 货源吨数
    private int goods_square; // 货源方数
    private String goods_exceed; // 货源三不超信息
    private int truck_type_code; // 车源类型
    private String truck_type_name;
    private int truck_length_code;
    private String truck_length_name;
    private int truck_spare_meter; // 车源剩余米数
    private int info_cost; // 信息费，单位：分
    private int freight_cost; // 运费，单位：分
    private int distribution_market;// 是否发布到配货市场
    private int market_screen_freight_id;
    private int status;
    private int order_status;

    private long pushTime; // 该车源货源推送过来的时间，本地消息列表排序用

    private int forward_to_blacklist;

    private User user;

    private String desc;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public int getDistribution_market() {
        return distribution_market;
    }

    public void setDistribution_market(int distribution_market) {
        this.distribution_market = distribution_market;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        if (update_time == 0) {
            return create_time;
        }
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public String getStart_region() {
        return start_region;
    }

    public void setStart_region(String start_region) {
        this.start_region = start_region;
    }

    public int getStart_region_code() {
        return start_region_code;
    }

    public void setStart_region_code(int start_region_code) {
        this.start_region_code = start_region_code;
    }

    public String getEnd_region() {
        return end_region;
    }

    public void setEnd_region(String end_region) {
        this.end_region = end_region;
    }

    public int getEnd_region_code() {
        return end_region_code;
    }

    public void setEnd_region_code(int end_region_code) {
        this.end_region_code = end_region_code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGoods_type_name() {
        return goods_type_name;
    }

    public int getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(int igoods_type) {
        this.goods_type = igoods_type;
    }

    public void setGoods_type_name(String goods_type) {
        this.goods_type_name = goods_type;
    }

    public double getGoods_ton() {
        return goods_ton;
    }

    public void setGoods_ton(double goods_ton) {
        this.goods_ton = goods_ton;
    }

    public int getGoods_square() {
        return goods_square;
    }

    public void setGoods_square(int goods_square) {
        this.goods_square = goods_square;
    }

    public String getGoods_exceed() {
        return goods_exceed;
    }

    public void setGoods_exceed(String goods_exceed) {
        this.goods_exceed = goods_exceed;
    }

    public String getTruck_type_name() {
        return truck_type_name;
    }

    public void setTruck_type_name(String truck_type) {
        this.truck_type_name = truck_type;
    }

    public int getTruck_type_code() {
        return truck_type_code;
    }

    public void setTruck_type_code(int itruck_type_code) {
        this.truck_type_code = itruck_type_code;
    }

    public int getTruck_length_code() {
        return truck_length_code;
    }

    public void setTruck_length_code(int truck_length_code) {
        this.truck_length_code = truck_length_code;
    }

    public String getTruck_length_name() {
        return truck_length_name;
    }

    public void setTruck_length_name(String truck_length_name) {
        this.truck_length_name = truck_length_name;
    }

    public int getTruck_spare_meter() {
        return truck_spare_meter;
    }

    public void setTruck_spare_meter(int truck_spare_meter) {
        this.truck_spare_meter = truck_spare_meter;
    }

    public int getInfo_cost() {
        return info_cost;
    }

    public void setInfo_cost(int info_cost) {
        this.info_cost = info_cost;
    }

    public int getFreight_cost() {
        return freight_cost;
    }

    public void setFreight_cost(int freight_cost) {
        this.freight_cost = freight_cost;
    }

    public int getStatus() {
        return status;
    }

    public int getMarket_screen_freight_id() {
        return market_screen_freight_id;
    }

    public void setMarket_screen_freight_id(int market_screen_freight_id) {
        this.market_screen_freight_id = market_screen_freight_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static int getTypeGoods() {
        return TYPE_GOODS;
    }

    public static int getTypeTruck() {
        return TYPE_TRUCK;
    }

    public boolean isSelf() {
        return false;
    }

    public String getRegion() {
        return start_region + " - " + end_region;
    }

    public int getForward_to_blacklist() {
        return forward_to_blacklist;
    }

    public void setForward_to_blacklist(int forward_to_blacklist) {
        this.forward_to_blacklist = forward_to_blacklist;
    }

    public long getPushTime() {
        return pushTime;
    }

    public void setPushTime(long pushTime) {
        this.pushTime = pushTime;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        if (desc != null && desc.length() > 0) {
            return desc;
        }
        String str_Result = "";
        if (type == TYPE_GOODS) {
            if (goods_ton > 0.0) {
                str_Result += goods_ton + "吨 ";
            }
            if (goods_square > 0) {
                str_Result += goods_square + "方 ";
            }
            if (!TextUtils.isEmpty(goods_type_name)) {
                str_Result += goods_type_name + " ";
            }
            if (!TextUtils.isEmpty(goods_exceed)) {
                str_Result += goods_exceed + " ";
            }
            if (!TextUtils.isEmpty(truck_length_name)) {
                str_Result += "需车长" + truck_length_name + " ";
            } else {
                // str_Result += "车长不限 ";
            }
            if (!TextUtils.isEmpty(truck_type_name)) {
                str_Result += truck_type_name;
            }
            return str_Result;
        } else {
            // if(truck_length_code > 0){
            // str_Result += "有车长" + truck_length_name + " ";
            // }
            // if(truck_type_code > 0){
            // str_Result += truck_type_name + " ";
            // }
            if (!TextUtils.isEmpty(truck_length_name)) {
                str_Result += "有车长" + truck_length_name + " ";
            }
            if (!TextUtils.isEmpty(truck_type_name)) {
                str_Result += truck_type_name + " ";
            }
            if (truck_spare_meter > 0) {
                str_Result += "还空" + truck_spare_meter / 10.0 + "米位 ";
            }

            if (goods_ton > 0.0) {
                str_Result += "需" + goods_ton + "吨 ";
            }
            if (goods_square > 0) {
                if (goods_ton > 0.0) {
                    str_Result += goods_square + "方";
                } else {
                    str_Result += "需" + goods_square + "方";
                }
            }

            return str_Result;
        }
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_freight.FIELD.ID, id);
        values.put(t_freight.FIELD.SENDER_ID, user_id);
        values.put(t_freight.FIELD.SENDER_NAME, owner_name);
        values.put(t_freight.FIELD.CREATE_TIME, this.getCreate_time());
        values.put(t_freight.FIELD.UPDATE_TIME, this.getUpdate_time());
        values.put(t_freight.FIELD.START_REGION, this.getStart_region());
        values.put(t_freight.FIELD.START_REGION_CODE, this.getStart_region_code());
        values.put(t_freight.FIELD.END_REGION, this.getEnd_region());
        values.put(t_freight.FIELD.END_REGION_CODE, this.getEnd_region_code());
        values.put(t_freight.FIELD.TYPE, this.getType());
        values.put(t_freight.FIELD.GOODS_TYPE, this.getGoods_type_name());
        values.put(t_freight.FIELD.GOODS_TON, this.getGoods_ton());
        values.put(t_freight.FIELD.GOODS_SQUARE, this.getGoods_square());
        values.put(t_freight.FIELD.GOODS_EXCEED, this.getGoods_exceed());
        values.put(t_freight.FIELD.TRUCK_TYPE_CODE, this.getTruck_type_code());
        values.put(t_freight.FIELD.TRUCK_TYPE_NAME, this.getTruck_type_name());
        values.put(t_freight.FIELD.TRUCK_LENGTH_CODE, this.getTruck_length_code());
        values.put(t_freight.FIELD.TRUCK_LENGTH_NAME, this.getTruck_length_name());
        values.put(t_freight.FIELD.STATUS, status);
        values.put(t_freight.FIELD.FREIGHT_OWNER_NAME, this.getOwner_name());
        values.put(t_freight.FIELD.INFO_COST, this.getInfo_cost());
        values.put(t_freight.FIELD.FREIGHT_COST, this.getFreight_cost());
        values.put(t_freight.FIELD.FORWARD_TO_BLACK_LIST, this.getForward_to_blacklist());
        values.put(t_freight.FIELD.PUSH_TIME, pushTime);
        return values;
    }

    @Override
    public int compareTo(Freight another) {
        if (another == null) {
            return -1;
        }
        return (int) (another.getCreate_time() - getCreate_time());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Freight)) {
            return false;
        }
        Freight f = (Freight) o;
        return getId().equals(f.getId());
    }
}
