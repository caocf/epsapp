package com.epeisong.data.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epeisong.EpsApplication;
import com.epeisong.data.dao.helper.ChatRoomDaoHelper;
import com.epeisong.data.dao.helper.ChatRoomDaoHelper.t_chatroom;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.data.model.CommonMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.utils.HandlerUtils;

public class ChatRoomDao {

    private static ChatRoomDao dao = new ChatRoomDao();

    private ChatRoomDaoHelper mDaoHelper;
    private String mTableName;

    private List<ChatRoomObserver> mObservers;

    private ChatRoomDao() {
        mDaoHelper = new ChatRoomDaoHelper(EpsApplication.getInstance(), null, null, 0);
        mTableName = mDaoHelper.getTableName();
    }

    public static ChatRoomDao getInstance() {
        return dao;
    }

    public synchronized boolean insert(ChatRoom room) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.insert(mTableName, null, room.getContentValues());
        db.close();
        if (_id > 0) {
            notifyObserver(room, CRUD.CREATE);
            return true;
        }
        return false;
    }

    public synchronized boolean delete(ChatRoom room) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        int count = db.delete(mTableName, t_chatroom.FIELD.ID + "=?", new String[] { room.getId() });
        db.close();
        if (count > 0) {
            notifyObserver(room, CRUD.DELETE);
            return true;
        }
        return false;
    }

    public synchronized boolean read(ChatRoom room) {
        room.setStatus(CommonMsg.STATUS_READED);
        room.setNew_msg_count(0);
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.update(mTableName, room.getContentValues(), t_chatroom.FIELD.ID + "=?",
                new String[] { room.getId() });
        db.close();
        if (_id > 0) {
            notifyObserver(room, CRUD.READ);
            return true;
        }
        return false;
    }

    public synchronized boolean update(ChatRoom room) {
        SQLiteDatabase db = mDaoHelper.getWritableDatabase();
        long _id = db.update(mTableName, room.getContentValues(), t_chatroom.FIELD.ID + "=?",
                new String[] { room.getId() });
        db.close();
        if (_id > 0) {
            notifyObserver(room, CRUD.UPDATE);
            return true;
        }
        return false;
    }

    public synchronized ChatRoom queryById(String id) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, "id=?", new String[] { id }, null, null, null);
        ChatRoom room = null;
        if (c != null) {
            if (c.moveToNext()) {
                room = parseCursor(c);
            }
            c.close();
        }
        db.close();
        return room;
    }

    public synchronized List<ChatRoom> queryFirst(int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, null, null, null, null, t_chatroom.FIELD.UPDATE_TIME + " desc limit 0,"
                + size);
        List<ChatRoom> result = new ArrayList<ChatRoom>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    public synchronized List<ChatRoom> queryNewer(long last_time, int size) {

        List<ChatRoom> result = new ArrayList<ChatRoom>();

        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_chatroom.FIELD.UPDATE_TIME + ">?",
                new String[] { String.valueOf(last_time) }, null, null, t_chatroom.FIELD.UPDATE_TIME + " limit 0,"
                        + size);
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<ChatRoom> queryOlder(long last_time, int size) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_chatroom.FIELD.UPDATE_TIME + "<?",
                new String[] { String.valueOf(last_time) }, null, null, t_chatroom.FIELD.UPDATE_TIME + " desc limit 0,"
                        + size);
        List<ChatRoom> result = new ArrayList<ChatRoom>();
        if (c != null) {
            while (c.moveToNext()) {
                result.add(parseCursor(c));
            }
            c.close();
        }
        db.close();
        return result;
    }

    public synchronized List<ChatRoom> queryFreightAdvisoryList(String freight_id) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_chatroom.FIELD.ID + " like ?",
                new String[] { "%_freight_" + freight_id }, null, null, t_chatroom.FIELD.UPDATE_TIME);
        List<ChatRoom> result = new ArrayList<ChatRoom>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }
    
    public synchronized List<ChatRoom> queryComplaintAdvisoryList(String complaint_id) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_chatroom.FIELD.ID + " like ?",
                new String[] { "%_complaint_" + complaint_id }, null, null, t_chatroom.FIELD.UPDATE_TIME);
        List<ChatRoom> result = new ArrayList<ChatRoom>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized List<ChatRoom> queryInfoFeeAdvisoryList(String infoFeeId) {
        SQLiteDatabase db = mDaoHelper.getReadableDatabase();
        Cursor c = db.query(mTableName, null, t_chatroom.FIELD.ID + " like ?",
                new String[] { "%_info_fee_" + infoFeeId }, null, null, t_chatroom.FIELD.UPDATE_TIME + " desc");
        List<ChatRoom> result = new ArrayList<ChatRoom>();
        while (c.moveToNext()) {
            result.add(parseCursor(c));
        }
        c.close();
        db.close();
        return result;
    }

    private ChatRoom parseCursor(Cursor c) {
        ChatRoom room = new ChatRoom();
        String id = c.getString(c.getColumnIndex(t_chatroom.FIELD.ID));
        room.setId(id);
        int business_type = c.getInt(c.getColumnIndex(t_chatroom.FIELD.BUSINESS_TYPE));
        room.setBusiness_type(business_type);
        String business_id = c.getString(c.getColumnIndex(t_chatroom.FIELD.BUSINESS_ID));
        room.setBusiness_id(business_id);
        String business_owner_id = c.getString(c.getColumnIndex(t_chatroom.FIELD.BUSINESS_OWNER_ID));
        room.setBusiness_owner_id(business_owner_id);
        String business_desc = c.getString(c.getColumnIndex(t_chatroom.FIELD.BUSINESS_DESC));
        room.setBusiness_desc(business_desc);
        String business_extra = c.getString(c.getColumnIndex(t_chatroom.FIELD.BUSINESS_EXTRA));
        room.setBusiness_extra(business_extra);
        String remote_id = c.getString(c.getColumnIndex(t_chatroom.FIELD.REMOTE_ID));
        room.setRemote_id(remote_id);
        String remote_name = c.getString(c.getColumnIndex(t_chatroom.FIELD.REMOTE_NAME));
        room.setRemote_name(remote_name);
        int remote_logistic_type_code = c.getInt(c.getColumnIndex(t_chatroom.FIELD.REMOTE_LOGISTIC_TYPE_CODE));
        room.setRemote_logistic_type_code(remote_logistic_type_code);
        String remote_logistic_type_name = c.getString(c.getColumnIndex(t_chatroom.FIELD.REMOTE_LOGISTIC_TYPE_NAME));
        room.setRemote_logistic_type_name(remote_logistic_type_name);
        long update_time = c.getLong(c.getColumnIndex(t_chatroom.FIELD.UPDATE_TIME));
        room.setUpdate_time(update_time);
        String last_msg = c.getString(c.getColumnIndex(t_chatroom.FIELD.LAST_MSG));
        room.setLast_msg(last_msg);
        int new_msg_count = c.getInt(c.getColumnIndex(t_chatroom.FIELD.NEW_MSG_COUNT));
        room.setNew_msg_count(new_msg_count);
        int status = c.getInt(c.getColumnIndex(t_chatroom.FIELD.STATUS));
        room.setStatus(status);
        return room;
    }

    private void notifyObserver(final ChatRoom room, final CRUD crud) {
        if (mObservers != null) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (ChatRoomObserver ob : mObservers) {
                        ob.onChatRoomChange(room, crud);
                    }
                }
            });

        }
    }

    public void addObserver(ChatRoomObserver ob) {
        if (ob == null) {
            return;
        }
        if (mObservers == null) {
            mObservers = new ArrayList<ChatRoomDao.ChatRoomObserver>();
        }
        mObservers.add(ob);
    }

    public void removeObserver(ChatRoomObserver ob) {
        if (mObservers != null && mObservers.size() > 0 && ob != null) {
            mObservers.remove(ob);
        }
    }

    public interface ChatRoomObserver {
        void onChatRoomChange(ChatRoom room, CRUD curd);
    }
}
