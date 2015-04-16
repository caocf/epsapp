package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;

import com.epeisong.data.dao.helper.ChatRoomDaoHelper.t_chatroom;
import com.epeisong.data.utils.ChatUtils;

/**
 * 会话
 * 
 * @author poet
 * 
 */
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = -1486775058720023258L;

    private String id;
    private int business_type; // 业务类型
    private String business_id; // 业务id
    private String business_owner_id; // 业务所属id
    private String business_desc; // 业务描述
    private String business_extra; // 业务扩展字段
    private String remote_id;
    private String remote_name;
    private int remote_logistic_type_code;
    private String remote_logistic_type_name;
    private long update_time;
    private String last_msg;
    private int new_msg_count;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(int business_type) {
        this.business_type = business_type;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getBusiness_owner_id() {
        return business_owner_id;
    }

    public void setBusiness_owner_id(String business_owner_id) {
        this.business_owner_id = business_owner_id;
    }

    public String getBusiness_desc() {
        return business_desc;
    }

    public void setBusiness_desc(String business_desc) {
        this.business_desc = business_desc;
    }

    public String getBusiness_extra() {
        return business_extra;
    }

    public void setBusiness_extra(String business_extra) {
        this.business_extra = business_extra;
    }

    public String getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(String remote_id) {
        this.remote_id = remote_id;
    }

    public String getRemote_name() {
        return remote_name;
    }

    public void setRemote_name(String remote_name) {
        this.remote_name = remote_name;
    }

    public int getRemote_logistic_type_code() {
        return remote_logistic_type_code;
    }

    public void setRemote_logistic_type_code(int remote_logistic_type_code) {
        this.remote_logistic_type_code = remote_logistic_type_code;
    }

    public String getRemote_logistic_type_name() {
        return remote_logistic_type_name;
    }

    public void setRemote_logistic_type_name(String remote_logistic_type_name) {
        this.remote_logistic_type_name = remote_logistic_type_name;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public int getNew_msg_count() {
        return new_msg_count;
    }

    public void setNew_msg_count(int new_msg_count) {
        this.new_msg_count = new_msg_count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_chatroom.FIELD.ID, id);
        values.put(t_chatroom.FIELD.BUSINESS_TYPE, business_type);
        values.put(t_chatroom.FIELD.BUSINESS_ID, business_id);
        values.put(t_chatroom.FIELD.BUSINESS_OWNER_ID, business_owner_id);
        values.put(t_chatroom.FIELD.BUSINESS_DESC, business_desc);
        values.put(t_chatroom.FIELD.BUSINESS_EXTRA, business_extra);
        values.put(t_chatroom.FIELD.REMOTE_ID, remote_id);
        values.put(t_chatroom.FIELD.REMOTE_NAME, remote_name);
        values.put(t_chatroom.FIELD.REMOTE_LOGISTIC_TYPE_CODE, remote_logistic_type_code);
        values.put(t_chatroom.FIELD.REMOTE_LOGISTIC_TYPE_NAME, remote_logistic_type_name);
        values.put(t_chatroom.FIELD.UPDATE_TIME, update_time);
        values.put(t_chatroom.FIELD.LAST_MSG, last_msg);
        values.put(t_chatroom.FIELD.NEW_MSG_COUNT, new_msg_count);
        values.put(t_chatroom.FIELD.STATUS, status);
        return values;
    }

    public static ChatRoom createFromChatMsg(ChatMsg msg) {
        ChatRoom room = new ChatRoom();
        room.id = ChatUtils.getChatMsgTableName(msg);
        room.business_type = msg.getBusiness_type();
        room.business_id = msg.getBusiness_id();
        room.business_owner_id = msg.getBusiness_owner_id();
        room.business_desc = msg.getBusiness_desc();
        room.business_extra = msg.getBusiness_extra();
        room.remote_id = msg.getRemoteId();
        room.remote_name = msg.getRemote_name();
        if (!msg.isSelf()) {
            room.remote_logistic_type_code = msg.getSender_logistic_type_code();
            room.remote_logistic_type_name = msg.getSender_logistic_type_name();
        } else {
            room.remote_logistic_type_code = msg.getRemote_logistic_type_code();
            room.setRemote_logistic_type_name(msg.getRemote_logistic_type_name());
        }
        room.update_time = msg.getSend_time();
        String who = getMsgFromWho(msg);
        room.last_msg = who + "：" + ChatMsg.getMsgByType(msg.getType(), msg.getType_data());
        return room;
    }

    public static String getMsgFromWho(ChatMsg msg) {
        String who = msg.isSelf() ? "我" : msg.getSender_name();
        if (who.length() > 7) {
            who = who.substring(0, 6) + "...";
        }
        return who;
    }

    public void copy(ChatRoom room) {
        if (room == null || !room.getId().equals(this.getId())) {
            return;
        }
        this.update_time = room.getUpdate_time();
        setLast_msg(room.getLast_msg());
        setNew_msg_count(room.getNew_msg_count());
        this.status = room.getStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (getId() == null || o == null || !(o instanceof ChatRoom)) {
            return false;
        }
        return getId().equals(((ChatRoom) o).getId());
    }
}
