package com.epeisong.model;

import com.epeisong.data.dao.helper.ContactsTagDaoHelper.t_contacts_tag;

import android.content.ContentValues;

/**
 * 联系人标签
 * @author poet
 *
 */
public class ContactsTag {

    private int id;
    private String contacts_id;
    private int tag_id;
    private long update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContacts_id() {
        return contacts_id;
    }

    public void setContacts_id(String contacts_id) {
        this.contacts_id = contacts_id;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_contacts_tag.FIELD.CONTACTS_ID, contacts_id);
        values.put(t_contacts_tag.FIELD.TAG_ID, tag_id);
        values.put(t_contacts_tag.FIELD.UPDATE_TIME, update_time);
        return values;
    }
}
