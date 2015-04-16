package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;

import com.epeisong.data.dao.helper.BulletinDaoHelper.t_bulletin;
import com.epeisong.logistics.common.Properties;

/**
 * 公告消息
 * 
 * @author poet
 * 
 */
public class Bulletin implements Serializable {

    private static final long serialVersionUID = -4946661911033283236L;

    public static final int status_web_normal = Properties.BULLETIN_STATUS_NORMAL;
    public static final int status_web_deleted = Properties.BULLETIN_STATUS_DELETED;

    public static final int status_unread = 0;
    public static final int status_readed = 1;

    private String id;
    private String sender_id;
    private String sender_name;
    private long create_time;
    private long update_time;
    private String content;
    private int content_type;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContent_type() {
        return content_type;
    }

    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_bulletin.FIELD.ID, id);
        values.put(t_bulletin.FIELD.SENDER_ID, sender_id);
        values.put(t_bulletin.FIELD.SENDER_NAME, sender_name);
        values.put(t_bulletin.FIELD.CREATE_TIME, create_time);
        values.put(t_bulletin.FIELD.UPDATE_TIME, update_time);
        values.put(t_bulletin.FIELD.CONTENT, content);
        values.put(t_bulletin.FIELD.CONTENT_TYPE, content_type);
        values.put(t_bulletin.FIELD.STATUS, status);
        return values;
    }
}
