package com.epeisong.data.dao.helper;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.epeisong.EpsApplication;

public class ChatMsgDaoHelper extends SQLiteOpenHelper {

    // private static final int DB_VERSION = 1;

    /**
     * send_status change to local status,add field remote_stauts
     */
    private static final int DB_VERSION = 2;

    private Context mContext;

    public ChatMsgDaoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, getDB_name(), factory, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            mContext.deleteDatabase(getDB_name());
            new ChatMsgDaoHelper(mContext, getDB_name(), null, 0);
        }
    }

    private static String getDB_name() {
        return EpsApplication.getDbName("chatmsg");
    }

    public static class t_chatmsg {

        public static String getSql(String tName) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(FIELD.ID, "TEXT UNIQUE");
            map.put(FIELD.BUSINESS_TYPE, "TEXT");
            map.put(FIELD.BUSINESS_ID, "TEXT");
            map.put(FIELD.BUSINESS_OWNER_ID, "TEXT");
            map.put(FIELD.BUSINESS_DESC, "TEXT");
            map.put(FIELD.BUSINESS_EXTRA, "TEXT");
            map.put(FIELD.SERIAL, "INTEGER");
            map.put(FIELD.CHAT_ROOM_ID, "TEXT");
            map.put(FIELD.SENDER_ID, "TEXT");
            map.put(FIELD.SENDER_NAME, "TEXT");
            map.put(FIELD.SENDER_LOGISTIC_TYPE_CODE, "TEXT");
            map.put(FIELD.SENDER_LOGISTIC_TYPE_NAME, "TEXT");
            map.put(FIELD.RECEIVER_ID, "TEXT");
            map.put(FIELD.SEND_TIME, "TEXT");
            map.put(FIELD.TYPE, "TEXT");
            map.put(FIELD.TYPE_DATA, "TEXT");
            map.put(FIELD.LOCAL_STATUS, "TEXT");
            map.put(FIELD.REMOTE_STATUS, "TEXT");
            map.put(FIELD.BYTES, "BLOB");
            map.put(FIELD.CONVERSATION_A, "TEXT");
            map.put(FIELD.CONVERSATION_B, "TEXT");

            StringBuilder sb = new StringBuilder("CREATE TABLE " + tName + " (");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getKey() + " " + entry.getValue() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.append(" );").toString();
        }

        public static interface FIELD {
            String ID = "id";
            String BUSINESS_TYPE = "business_type";
            String BUSINESS_ID = "business_id";
            String BUSINESS_OWNER_ID = "business_owner_id";
            String BUSINESS_DESC = "business_desc";
            String BUSINESS_EXTRA = "business_extra";
            String SERIAL = "serial";
            String CHAT_ROOM_ID = "chat_room_id";
            String SENDER_ID = "sender_id";
            String SENDER_NAME = "sender_name";
            String SENDER_LOGISTIC_TYPE_CODE = "sender_logistic_type_code";
            String SENDER_LOGISTIC_TYPE_NAME = "sender_logistic_type_name";
            String RECEIVER_ID = "receiver_id";
            String SEND_TIME = "send_time";
            String TYPE = "type"; // 消息类型：文本、语音、图片
            String TYPE_DATA = "type_data"; // 类型对应值：文本内容、语音url、图片url
            String LOCAL_STATUS = "local_status";
            String REMOTE_STATUS = "remote_status";

            String CONVERSATION_A = "conversation_a";
            String CONVERSATION_B = "conversation_b";

            String BYTES = "bytes"; // 保存的二进制数据（对于发送失败的消息，包含二进制数据的，需要保存到数据库，下次重新发送成功后，删除该数据）
        }
    }
}
