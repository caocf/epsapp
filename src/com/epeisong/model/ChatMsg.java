package com.epeisong.model;

import java.io.File;
import java.io.Serializable;

import android.content.ContentValues;
import android.text.TextUtils;

import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.helper.ChatMsgDaoHelper.t_chatmsg;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.common.TableConstants;
import com.epeisong.utils.EncodeUtils;
import com.epeisong.utils.FileUtils;

/**
 * 聊天消息
 * 
 * @author poet
 * 
 */
public class ChatMsg implements Serializable, Comparable<ChatMsg> {

    private static final long serialVersionUID = -8624714340690310413L;

    public static final int local_status_normal = 0;
    public static final int local_status_sending = 1;
    public static final int local_status_fail = 2;
    public static final int local_stauts_un_send = 3;

    public static final int type_text = Properties.CHAT_TYPE_TEXT;
    public static final int type_voice = Properties.CHAT_TYPE_VOICE;
    public static final int type_pic = Properties.CHAT_TYPE_PICTURE;
    public static final int type_vedio = Properties.CHAT_TYPE_VEDIO;
    public static final int type_location = Properties.CHAT_TYPE_POSITION;

    public static final int business_type_normal = TableConstants.E_CHAT;
    public static final int business_type_freight = TableConstants.E_FREIGHT;
    public static final int business_type_info_fee = TableConstants.E_INFO_FEE;
    public static final int business_type_withdraw = TableConstants.WITHDRAW;
    public static final int business_type_complaint = TableConstants.COMPLAINT;

    private String id;
    private int business_type; // 该消息针对的业务类型：车源货源的咨询、运单的咨询等。
    private String business_id; // 对应的业务id
    private String business_owner_id; // 业务所属id
    private String business_desc;
    private String business_extra;
    private long serial; // 序列号：针对每个聊天内容，服务器对每条消息生成连续的id
    private String sender_id;
    private String sender_name;
    private int sender_logistic_type_code;
    private String sender_logistic_type_name;
    private String receiver_id;
    private long send_time;
    private int type;
    private String type_data;
    private int local_status;
    private int remote_status;

    private int conversationA;
    private int conversationB;

    private String remote_name;
    private int remote_logistic_type_code;
    private String remote_logistic_type_name;

    private byte[] temp_data;

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

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
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

    public int getSender_logistic_type_code() {
        return sender_logistic_type_code;
    }

    public void setSender_logistic_type_code(int sender_logistic_type_code) {
        this.sender_logistic_type_code = sender_logistic_type_code;
    }

    public String getSender_logistic_type_name() {
        return sender_logistic_type_name;
    }

    public void setSender_logistic_type_name(String sender_logistic_type_name) {
        this.sender_logistic_type_name = sender_logistic_type_name;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }

    public String getType_data() {
        return type_data;
    }

    public void setType_data(String type_data) {
        this.type_data = type_data;
    }

    public int getLocal_status() {
        return local_status;
    }

    public void setLocal_status(int local_status) {
        this.local_status = local_status;
    }

    public int getRemote_status() {
        return remote_status;
    }

    public void setRemote_status(int remote_status) {
        this.remote_status = remote_status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemote_name() {
        if (remote_name == null) {
            if (!UserDao.getInstance().getUser().getId().equals(sender_id)) {
                return sender_name;
            }
        }
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

    public int getConversationA() {
        return conversationA;
    }

    public void setConversationA(int conversationA) {
        this.conversationA = conversationA;
    }

    public int getConversationB() {
        return conversationB;
    }

    public void setConversationB(int conversationB) {
        this.conversationB = conversationB;
    }

    public byte[] getTemp_data() {
        return temp_data;
    }

    public void setTemp_data(byte[] temp_data) {
        this.temp_data = temp_data;
    }

    public boolean isSelf() {
        return UserDao.getInstance().getUser().getId().equals(sender_id);
    }

    public String getRemoteId() {
        if (isSelf()) {
            return receiver_id;
        } else {
            return sender_id;
        }
    }

    public String getVoiceLocalPath() {
        if (type != type_voice || TextUtils.isEmpty(type_data)) {
            return null;
        }
        File file = new File(FileUtils.getChatVoiceFileDir() + File.separator + EncodeUtils.md5base64(type_data));
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(t_chatmsg.FIELD.ID, id);
        values.put(t_chatmsg.FIELD.BUSINESS_TYPE, business_type);
        values.put(t_chatmsg.FIELD.BUSINESS_ID, business_id);
        values.put(t_chatmsg.FIELD.BUSINESS_OWNER_ID, business_owner_id);
        values.put(t_chatmsg.FIELD.BUSINESS_DESC, business_desc);
        values.put(t_chatmsg.FIELD.BUSINESS_EXTRA, business_extra);
        values.put(t_chatmsg.FIELD.SERIAL, serial);
        values.put(t_chatmsg.FIELD.CHAT_ROOM_ID, ChatUtils.getChatMsgTableName(this));
        values.put(t_chatmsg.FIELD.SENDER_ID, sender_id);
        values.put(t_chatmsg.FIELD.SENDER_NAME, sender_name);
        values.put(t_chatmsg.FIELD.SENDER_LOGISTIC_TYPE_CODE, sender_logistic_type_code);
        values.put(t_chatmsg.FIELD.SENDER_LOGISTIC_TYPE_NAME, sender_logistic_type_name);
        values.put(t_chatmsg.FIELD.RECEIVER_ID, receiver_id);
        values.put(t_chatmsg.FIELD.SEND_TIME, send_time);
        values.put(t_chatmsg.FIELD.TYPE, type);
        values.put(t_chatmsg.FIELD.TYPE_DATA, type_data);
        values.put(t_chatmsg.FIELD.LOCAL_STATUS, local_status);
        values.put(t_chatmsg.FIELD.REMOTE_STATUS, remote_status);
        return values;
    }

    @Override
    public int compareTo(ChatMsg another) {
        if (another == null) {
            return 1;
        }
        long dTime = (this.getSend_time() - another.getSend_time());
        if (dTime > 0) {
            return 1;
        } else if (dTime < 0) {
            return -1;
        }
        return (int) (this.serial - another.getSerial());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof ChatMsg)) {
            return false;
        }
        ChatMsg msg = (ChatMsg) o;
        return this.id.equals(msg.getId());
    }

    public String getShowContent() {
        switch (type) {
        case type_text:
            return type_data;
        case type_voice:
            return "[语音]";
        case type_pic:
            return "[图片]";
        case type_location:
            return "[位置]";
        default:
            return "";
        }
    }

    public static String getMsgByType(int type, String data) {
        switch (type) {
        case type_text:
            return data;
        case type_voice:
            return "[语音]";
        case type_pic:
            return "[图片]";
        case type_location:
            return "[位置]";
        default:
            break;
        }
        return null;
    }
}
