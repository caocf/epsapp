package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;
import android.text.TextUtils;

import com.epeisong.data.dao.helper.ContactsDaoHelper.t_contacts;
import com.epeisong.logistics.common.Properties;

/**
 * 联系人（黑名单）
 * 
 * @author poet
 * 
 */
public class Contacts implements Serializable, Comparable<Contacts> {

    private static final long serialVersionUID = 2563524144837271216L;

    public static final int STATUS_BLACKLIST = Properties.CONTACT_STATUS_ISBLACK;
    public static final int STATUS_NORNAL = Properties.CONTACT_STATUS_NORMAL;
    public static final int STATUS_DELETED = Properties.CONTACT_STATUS_DELETED;

    private String id;
    private String phone;
    private String logo_url;
    private String show_name;
    private String pinyin;
    private String jianpin;
    private String contacts_name;
    private String contacts_phone;
    private String contacts_telephone;
    private String address;
    private String qq;
    private String email;
    private String wechat;
    private int star_level;
    private int logistic_type_code;
    private String logistic_type_name;

    private UserRole userRole;

    private String relation_id;
    private long relation_time; // 该联系人的添加时间
    private long update_time; // 该联系人的更新时间
    private String remark;
    private String remark_pinyin;
    private String remark_desc; // 备注描述
    private int status; // 联系人、黑名单

    private int market_banned_id;

    public int getMarket_banned_id() {
        return market_banned_id;
    }

    public void setMarket_banned_id(int market_banned_id) {
        this.market_banned_id = market_banned_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getContacts_name() {
        return contacts_name;
    }

    public void setContacts_name(String contacts_name) {
        this.contacts_name = contacts_name;
    }

    public String getContacts_phone() {
        return contacts_phone;
    }

    public void setContacts_phone(String contacts_phone) {
        this.contacts_phone = contacts_phone;
    }

    public String getContacts_telephone() {
        return contacts_telephone;
    }

    public void setContacts_telephone(String contacts_telephone) {
        this.contacts_telephone = contacts_telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public int getStar_level() {
        return star_level;
    }

    public void setStar_level(int star_level) {
        this.star_level = star_level;
    }

    public int getLogistic_type_code() {
        return logistic_type_code;
    }

    public void setLogistic_type_code(int logistic_type_code) {
        this.logistic_type_code = logistic_type_code;
    }

    public String getLogistic_type_name() {
        return logistic_type_name;
    }

    public void setLogistic_type_name(String logistic_type_name) {
        this.logistic_type_name = logistic_type_name;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(String relation_id) {
        this.relation_id = relation_id;
    }

    public long getRelation_time() {
        return relation_time;
    }

    public void setRelation_time(long relation_time) {
        this.relation_time = relation_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark_desc() {
        return remark_desc;
    }

    public void setRemark_desc(String remark_desc) {
        this.remark_desc = remark_desc;
    }

    public String getRemark_pinyin() {
        return remark_pinyin;
    }

    public void setRemark_pinyin(String remark_pinyin) {
        this.remark_pinyin = remark_pinyin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_contacts.FIELD.ID, id);
        values.put(t_contacts.FIELD.PHONE, phone);
        values.put(t_contacts.FIELD.LOGO_URL, logo_url);
        values.put(t_contacts.FIELD.SHOW_NAME, show_name);
        values.put(t_contacts.FIELD.PINYIN, pinyin);
        values.put(t_contacts.FIELD.CONTACT_NAME, contacts_name);
        values.put(t_contacts.FIELD.CONTACT_PHONE, contacts_phone);
        values.put(t_contacts.FIELD.CONTACT_TELEPHONE, contacts_telephone);
        values.put(t_contacts.FIELD.ADDRESS, address);
        values.put(t_contacts.FIELD.QQ, qq);
        values.put(t_contacts.FIELD.EMAIL, email);
        values.put(t_contacts.FIELD.WECHAT, wechat);
        values.put(t_contacts.FIELD.STAR_LEVEL, star_level);
        values.put(t_contacts.FIELD.LOGISTIC_TYPE_CODE, logistic_type_code);
        values.put(t_contacts.FIELD.LOGISTIC_TYPE_NAME, logistic_type_name);

        if (userRole != null) {
            values.put(t_contacts.FIELD.role_region_code, userRole.getRegionCode());
            values.put(t_contacts.FIELD.role_region_name, userRole.getRegionName());
            values.put(t_contacts.FIELD.role_cur_region_code, userRole.getCurrentRegionCode());
            values.put(t_contacts.FIELD.role_cur_region_name, userRole.getCurrentRegionName());
            values.put(t_contacts.FIELD.role_cur_longitude, userRole.getCurrent_longitude());
            values.put(t_contacts.FIELD.role_cur_latitude, userRole.getCurrent_latitude());
            values.put(t_contacts.FIELD.role_line_start_code, userRole.getLineStartCode());
            values.put(t_contacts.FIELD.role_line_start_name, userRole.getLineStartName());
            values.put(t_contacts.FIELD.role_line_end_code, userRole.getLineEndCode());
            values.put(t_contacts.FIELD.role_line_end_name, userRole.getLineEndName());
            values.put(t_contacts.FIELD.role_validity_code, userRole.getValidityCode());
            values.put(t_contacts.FIELD.role_validity_name, userRole.getValidityName());
            values.put(t_contacts.FIELD.role_insurance_code, userRole.getInsuranceCode());
            values.put(t_contacts.FIELD.role_insurance_name, userRole.getInsuranceName());
            values.put(t_contacts.FIELD.role_device_code, userRole.getDeviceCode());
            values.put(t_contacts.FIELD.role_device_name, userRole.getDeviceName());
            values.put(t_contacts.FIELD.role_depot_code, userRole.getDepotCode());
            values.put(t_contacts.FIELD.role_depot_name, userRole.getDepotName());
            values.put(t_contacts.FIELD.role_pack_code, userRole.getPackCode());
            values.put(t_contacts.FIELD.role_pack_name, userRole.getPackName());
            values.put(t_contacts.FIELD.role_truck_len_code, userRole.getTruckLengthCode());
            values.put(t_contacts.FIELD.role_truck_len_name, userRole.getTruckLengthName());
            values.put(t_contacts.FIELD.role_truck_type_code, userRole.getTruckTypeCode());
            values.put(t_contacts.FIELD.role_truck_type_name, userRole.getTruckTypeName());
            values.put(t_contacts.FIELD.role_load_ton, userRole.getLoadTon());
            values.put(t_contacts.FIELD.role_goods_type_code, userRole.getGoodsTypeCode());
            values.put(t_contacts.FIELD.role_goods_type_name, userRole.getGoodsTypeName());
            values.put(t_contacts.FIELD.role_weight, userRole.getWeight());
            values.put(t_contacts.FIELD.role_is_full_loaded, userRole.getIs_full_loaded());
        }

        values.put(t_contacts.FIELD.RELATION_ID, relation_id);
        values.put(t_contacts.FIELD.RELATION_TIME, relation_time);
        values.put(t_contacts.FIELD.UPDATE_TIME, update_time);
        values.put(t_contacts.FIELD.REMARK, remark);
        values.put(t_contacts.FIELD.REMARK_PINYIN, remark_pinyin);
        values.put(t_contacts.FIELD.REMARK_DESC, remark_desc);
        values.put(t_contacts.FIELD.STATUS, status);
        return values;
    }

    @Override
    public int compareTo(Contacts another) {
        String anPinyin = null;
        if (another != null) {
            anPinyin = another.getRemark_pinyin();
            if (TextUtils.isEmpty(anPinyin)) {
                anPinyin = another.getPinyin();
            }
        }
        if (TextUtils.isEmpty(anPinyin)) {
            return 1;
        }

        String mPinyin = getRemark_pinyin();
        if (TextUtils.isEmpty(mPinyin)) {
            mPinyin = getPinyin();
        }

        if (TextUtils.isEmpty(mPinyin)) {
            return -1;
        }
        return mPinyin.toUpperCase().compareTo(anPinyin.toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Contacts)) {
            return false;
        }
        Contacts c = (Contacts) o;
        return this.id.equals(c.getId());
    }

    public User convertToUser() {
        User u = new User();
        u.setId(id);
        u.setAddress(address);
        u.setContacts_name(contacts_name);
        u.setContacts_phone(contacts_phone);
        u.setContacts_telephone(contacts_telephone);
        u.setEmail(email);
        u.setLogo_url(logo_url);
        u.setPhone(phone);
        u.setPinyin(pinyin);
        u.setShow_name(show_name);
        u.setStar_level(star_level);
        u.setUser_type_code(logistic_type_code);
        u.setUser_type_name(logistic_type_name);
        return u;
    }

    public static Contacts convertFromUser(User u) {
        Contacts c = new Contacts();
        c.setId(u.getId());
        c.setAddress(u.getAddress());
        c.setContacts_name(u.getContacts_name());
        c.setContacts_phone(u.getContacts_phone());
        c.setContacts_telephone(u.getContacts_telephone());
        c.setEmail(u.getEmail());
        c.setLogistic_type_code(u.getUser_type_code());
        c.setLogistic_type_name(u.getUser_type_name());
        c.setLogo_url(u.getLogo_url());
        c.setPinyin(u.getPinyin());
        c.setQq(u.getQq());
        c.setShow_name(u.getShow_name());
        c.setStar_level(u.getStar_level());
        c.setStatus(Contacts.STATUS_NORNAL);
        return c;
    }

    @Override
    public String toString() {
        return show_name + ":" + phone + "\r\n";
    }
}
