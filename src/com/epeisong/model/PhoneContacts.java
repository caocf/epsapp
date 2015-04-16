package com.epeisong.model;

import android.content.ContentValues;

import com.epeisong.data.dao.helper.PhoneContactsDaoHelper.t_phonecontacts;

/**
 * 手机联系人
 * 
 * @author poet
 * 
 */
public class PhoneContacts {

    public static final int STATUS_UN_SPECIFIED = 0; // 未指定
    public static final int STATUS_UN_USE = 1; // 没有使用该应用
    public static final int STATUS_UN_ADD = 2; // 使用应用，不是联系人
    public static final int STATUS_ADDED = 3; // 已经添加为联系人

    private String id; // 系统DB中的id
    private int contacts_id; // 手机通讯录中的id
    private String sort_key;
    private String name;
    private String pinyin;
    private String phone_num;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getContacts_id() {
        return contacts_id;
    }

    public void setContacts_id(int contacts_id) {
        this.contacts_id = contacts_id;
    }

    public String getSort_key() {
        return sort_key;
    }

    public void setSort_key(String sort_key) {
        this.sort_key = sort_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_phonecontacts.FIELD.ID, id);
        values.put(t_phonecontacts.FIELD.SORT_KEY, sort_key);
        values.put(t_phonecontacts.FIELD.NAME, name);
        values.put(t_phonecontacts.FIELD.PINYIN, pinyin);
        values.put(t_phonecontacts.FIELD.PHONE_NUM, phone_num);
        values.put(t_phonecontacts.FIELD.STATUS, status);
        return values;
    }
}
