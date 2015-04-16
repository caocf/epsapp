package com.epeisong.data.dao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.data.dao.helper.ChatMsgDaoHelper;
import com.epeisong.data.dao.helper.ChatMsgDaoHelper.t_chatmsg;
import com.epeisong.data.model.CommonMsg;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.model.Point.PointCode;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.NotificationUtils;

public class ChatMsgDao {

	private static ChatMsgDao dao = new ChatMsgDao();

	private ChatMsgDaoHelper mDaoHelper;

	private Map<String, WeakReference<ChatMsgObserver>> mMap;

	private ChatMsgDao() {
		mDaoHelper = new ChatMsgDaoHelper(EpsApplication.getInstance(), null,
				null, 0);
	}

	public static ChatMsgDao getInstance() {
		return dao;
	}

	public synchronized boolean checkTable(String table) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		checkTable(db, table);
		db.close();
		return true;
	}

	private boolean replace(SQLiteDatabase db, ChatMsg msg) {
		String tableName = ChatUtils.getChatMsgTableName(msg);
		checkTable(db, tableName);
		Cursor c = db.query(tableName, null, t_chatmsg.FIELD.ID + "=?",
				new String[] { msg.getId() }, null, null, null);
		boolean bReplace = false;
		if (c.getCount() <= 0) {
			long _id = db.insert(tableName, null, msg.getContentValues());
			if (_id > 0) {
				bReplace = true;
			}
		} else {
			int count = db.update(
					tableName,
					msg.getContentValues(),
					t_chatmsg.FIELD.ID + "=? and " + t_chatmsg.FIELD.SEND_TIME
							+ "<?",
					new String[] { msg.getId(),
							String.valueOf(msg.getSend_time()) });
			if (count > 0) {
				bReplace = true;
			}
		}
		c.close();
		return bReplace;
	}

	public synchronized boolean replaceAll(String tableName, List<ChatMsg> msgs) {
		if (msgs == null || msgs.isEmpty()) {
			return false;
		}
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		checkTable(db, tableName);
		boolean result = true;
		for (ChatMsg msg : msgs) {
			long _id = db.replace(tableName, null, msg.getContentValues());
			if (_id <= 0) {
				result = false;
				break;
			}
		}
		// 拉取消息后，更改ChatRoom
		ChatMsg newest = getNewestNormalStatus(tableName, db);
		if (newest != null) {
			ChatRoom room = ChatRoomDao.getInstance().queryById(
					ChatUtils.getChatMsgTableName(newest));
			if (room != null) {
				effectRoomList(newest, true);
			}
		}
		db.close();
		return result;
	}

	public boolean replace(ChatMsg msg) {
		return replace(msg, true);
	}

	public boolean replace(ChatMsg msg, boolean bNotify) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		boolean bReplace = replace(db, msg);
		if (bReplace) {
			boolean bRead = !bNotify || notifyObserver(msg);
			ChatMsg newest = getNewestNormalStatus(
					ChatUtils.getChatMsgTableName(msg), db);
			if (newest != null && msg.getRemote_logistic_type_code() > 0) {
				newest.setRemote_logistic_type_code(msg
						.getRemote_logistic_type_code());
				newest.setRemote_logistic_type_name(msg
						.getRemote_logistic_type_name());
				newest.setRemote_name(msg.getRemote_name());
			}
			effectRoomList(newest, bRead);
			if (bNotify) {
				if (!bRead
						&& MainActivity.sCurPagePos != MainActivity.MESSAGE_POS) {
					PointDao.getInstance().show(PointCode.Code_Message);
				}
				if (!BaseActivity.isTop()) {
					NotificationUtils.notify(msg);
				}
			}
		}
		db.close();
		return bReplace;
	}

	private ChatMsg getNewestNormalStatus(String table, SQLiteDatabase db) {
		Cursor c = db.query(table, null, t_chatmsg.FIELD.REMOTE_STATUS + "="
				+ Properties.CHAT_STATUS_NORMAL, null, null, null,
				t_chatmsg.FIELD.SERIAL + " desc limit 0,1");
		ChatMsg msg = null;
		if (c.moveToFirst()) {
			msg = parseCursor(c);
		}
		c.close();
		return msg;
	}

	private void effectRoomList(ChatMsg newest, boolean bRead,
			String... tableName) {
		if (newest != null) {
			ChatRoom room = ChatRoomDao.getInstance().queryById(
					ChatUtils.getChatMsgTableName(newest));
			if (room == null) {
				room = ChatRoom.createFromChatMsg(newest);
				if (bRead) {
					room.setStatus(CommonMsg.STATUS_READED);
				} else {
					room.setStatus(CommonMsg.STATUS_UNREAD);
					room.setNew_msg_count(1);
				}
				ChatRoomDao.getInstance().insert(room);
			} else if (newest.getSend_time() > room.getUpdate_time() || true) { // 发送前按本地时间保存后，发送成功后返回服务器时间小
				room.setUpdate_time(newest.getSend_time());
				room.setLast_msg(ChatRoom.getMsgFromWho(newest)
						+ "："
						+ ChatMsg.getMsgByType(newest.getType(),
								newest.getType_data()));
				// if (!newest.isSelf()) {
				// room.setRemote_logistic_type_code(newest.getSender_logistic_type_code());
				// room.setRemote_logistic_type_name(newest.getSender_logistic_type_name());
				// room.setRemote_name(newest.getSender_name());
				// } else {
				// room.setRemote_name(newest.getRemote_name());
				// }
				if (!bRead) {
					room.setNew_msg_count(room.getNew_msg_count() + 1);
				}
				ChatRoomDao.getInstance().update(room);
			}
		} else if (tableName != null && tableName.length > 0) {
			ChatRoom room = ChatRoomDao.getInstance().queryById(tableName[0]);
			if (room != null) {
				room.setLast_msg("");
				ChatRoomDao.getInstance().update(room);
			}
		}
	}

	public synchronized boolean insertAll(String tableName, List<ChatMsg> list) {
		if (list == null || list.isEmpty()) {
			return false;
		}
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		checkTable(db, tableName);
		db.beginTransaction();
		for (ChatMsg msg : list) {
			long _id = db.insert(tableName, null, msg.getContentValues());
			if (_id == -1) {
				db.endTransaction();
				db.close();
				return false;
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return true;
	}

	public synchronized void delete(String tableName, ChatMsg msg) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		int count = db.delete(tableName, t_chatmsg.FIELD.ID + "=?",
				new String[] { msg.getId() });
		if (count > 0) {
			ChatMsg newest = getNewestNormalStatus(tableName, db);
			if (newest != null) {
				effectRoomList(newest, true);
			} else {
				effectRoomList(newest, true, tableName);
			}
		}
		db.close();
	}

	public synchronized void clear(String tableName) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		db.delete(tableName, null, null);
		db.close();
	}

	public synchronized boolean update(String tableName, ChatMsg msg,
			String uuid) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		int count = 0;
		try {
			count = db.update(tableName, msg.getContentValues(),
					t_chatmsg.FIELD.ID + "=?", new String[] { uuid });
			if (count > 0) {
				ChatMsg newest = getNewestNormalStatus(tableName, db);
				if (newest != null && msg.getRemote_logistic_type_code() > 0) {
					newest.setRemote_logistic_type_code(msg
							.getRemote_logistic_type_code());
					newest.setRemote_logistic_type_name(msg
							.getRemote_logistic_type_name());
					newest.setRemote_name(msg.getRemote_name());
				}
				effectRoomList(newest, true);
			}
		} catch (SQLiteConstraintException e) {
			db.delete(tableName, null, null);
			db.insert(tableName, null, msg.getContentValues());
		}
		db.close();
		return count > 0;
	}

	public synchronized void saveBytes(String tableName, String uuid,
			byte[] data) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(t_chatmsg.FIELD.BYTES, data);
		db.update(tableName, values, t_chatmsg.FIELD.ID + "=?",
				new String[] { uuid });
		db.close();
	}

	public synchronized byte[] queryBytes(String tableName, String id) {
		SQLiteDatabase db = mDaoHelper.getReadableDatabase();
		Cursor c = db.query(tableName, new String[] { t_chatmsg.FIELD.BYTES },
				t_chatmsg.FIELD.ID + "=?", new String[] { id }, null, null,
				null);
		byte[] data = null;
		if (c.moveToFirst()) {
			data = c.getBlob(c.getColumnIndex(t_chatmsg.FIELD.BYTES));
		}
		c.close();
		db.close();
		return data;
	}

	public synchronized void insertUnSend(String tableName, String text) {
		SQLiteDatabase db = mDaoHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(t_chatmsg.FIELD.LOCAL_STATUS, ChatMsg.local_stauts_un_send);
		values.put(t_chatmsg.FIELD.TYPE_DATA, text);
		db.insert(tableName, null, values);
		db.close();
	}

	public synchronized String queryUnSend(String tableName) {
		SQLiteDatabase db = mDaoHelper.getReadableDatabase();
		checkTable(db, tableName);
		Cursor c = db.query(tableName, null, t_chatmsg.FIELD.LOCAL_STATUS + "="
				+ ChatMsg.local_stauts_un_send, null, null, null, null);
		String result = null;
		if (c.moveToNext()) {
			result = parseCursor(c).getType_data();
			db.delete(tableName, t_chatmsg.FIELD.LOCAL_STATUS + "="
					+ ChatMsg.local_stauts_un_send, null);
		}
		c.close();
		db.close();
		return result;
	}

	public synchronized ChatMsg queryNewest(String tableName) {
		SQLiteDatabase db = mDaoHelper.getReadableDatabase();
		Cursor c = db.query(tableName, null, t_chatmsg.FIELD.LOCAL_STATUS
				+ "!=" + ChatMsg.local_stauts_un_send, null, null, null,
				t_chatmsg.FIELD.SEND_TIME + " desc limit 0,1");
		ChatMsg msg = null;
		if (c.moveToNext()) {
			msg = parseCursor(c);
		}
		c.close();
		db.close();
		return msg;
	}

	public synchronized List<ChatMsg> queryFirst(String tableName, int size) {
		SQLiteDatabase db = mDaoHelper.getReadableDatabase();
		Cursor c = db.query(tableName, null, t_chatmsg.FIELD.LOCAL_STATUS
				+ "!=" + ChatMsg.local_stauts_un_send, null, null, null,
				t_chatmsg.FIELD.SEND_TIME + " desc , " + t_chatmsg.FIELD.SERIAL
						+ " desc limit 0," + size);
		List<ChatMsg> result = new ArrayList<ChatMsg>();
		if (c != null) {
			while (c.moveToNext()) {
				result.add(parseCursor(c));
			}
			c.close();
		}
		db.close();
		Collections.sort(result);
		return result;
	}

	public synchronized List<ChatMsg> queryNewer(String tableName,
			long edgeTime, long edgeSerial, int size) {
		List<ChatMsg> result = new ArrayList<ChatMsg>();
		SQLiteDatabase db = mDaoHelper.getReadableDatabase();
		Cursor c = db.query(tableName, null, t_chatmsg.FIELD.LOCAL_STATUS
				+ "!=? and " + t_chatmsg.FIELD.SEND_TIME + ">=? and ("
				+ t_chatmsg.FIELD.SERIAL + ">? or " + t_chatmsg.FIELD.SERIAL
				+ "=0 )",
				new String[] { String.valueOf(ChatMsg.local_stauts_un_send),
						String.valueOf(edgeTime), String.valueOf(edgeSerial) },
				null, null, t_chatmsg.FIELD.SERIAL + " limit 0," + size);
		while (c.moveToNext()) {
			result.add(parseCursor(c));
		}
		c.close();
		db.close();
		return result;
	}

	public synchronized List<ChatMsg> queryOlder(String tableName,
			long edgeTime, long edgeSerial, int size) {
		List<ChatMsg> result = new ArrayList<ChatMsg>();
		SQLiteDatabase db = mDaoHelper.getReadableDatabase();
		Cursor c = db.query(tableName, null, t_chatmsg.FIELD.LOCAL_STATUS
				+ "!=? and " + t_chatmsg.FIELD.SEND_TIME + "<=? and "
				+ t_chatmsg.FIELD.SERIAL + "<?",
				new String[] { String.valueOf(ChatMsg.local_stauts_un_send),
						String.valueOf(edgeTime), String.valueOf(edgeSerial) },
				null, null, t_chatmsg.FIELD.SEND_TIME + " desc , "
						+ t_chatmsg.FIELD.SERIAL + " desc limit 0," + size);
		while (c.moveToNext()) {
			result.add(parseCursor(c));
		}
		c.close();
		db.close();
		return result;
	}

	public synchronized void deleteTable(String tableName) {
		try {
			SQLiteDatabase db = mDaoHelper.getWritableDatabase();
			db.execSQL("drop table " + tableName);
			db.close();
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}

	private void checkTable(SQLiteDatabase db, String tableName) {
		if (!isTableExist(db, tableName)) {
			String sql = t_chatmsg.getSql(tableName);
			db.execSQL(sql);
			LogUtils.d("ChatMsgDao", "checkTable and Create:" + sql);
		}
	}

	private boolean isTableExist(SQLiteDatabase db, String tableName) {
		boolean exist = false;
		String sql = "SELECT COUNT(*) FROM sqlite_master where type='table' and name='"
				+ tableName + "'";
		Cursor c = null;
		try {
			c = db.rawQuery(sql, null);
			if (c.moveToNext()) {
				if (c.getInt(0) > 0) {
					exist = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return exist;
	}

	private boolean notifyObserver(final ChatMsg msg) {
		boolean notify = false;
		if (mMap != null && !mMap.isEmpty()) {
			String tableId = ChatUtils.getChatMsgTableName(msg);
			Iterator<Entry<String, WeakReference<ChatMsgObserver>>> it = mMap
					.entrySet().iterator();
			while (it.hasNext()) {
				final Entry<String, WeakReference<ChatMsgObserver>> next = it
						.next();
				if (next.getValue().get() == null) {
					it.remove();
				} else {
					if (next.getKey().equals(tableId)) {
						HandlerUtils.post(new Runnable() {
							@Override
							public void run() {
								next.getValue().get().onNewMsg(msg);
							}
						});
						notify = true;
						break;
					}
				}
			}
		}
		return notify;
	}

	private ChatMsg parseCursor(Cursor c) {
		ChatMsg msg = new ChatMsg();
		String id = c.getString(c.getColumnIndex(t_chatmsg.FIELD.ID));
		msg.setId(id);
		int business_type = c.getInt(c
				.getColumnIndex(t_chatmsg.FIELD.BUSINESS_TYPE));
		msg.setBusiness_type(business_type);
		String business_id = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.BUSINESS_ID));
		msg.setBusiness_id(business_id);
		String business_owner_id = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.BUSINESS_OWNER_ID));
		msg.setBusiness_owner_id(business_owner_id);
		String business_desc = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.BUSINESS_DESC));
		msg.setBusiness_desc(business_desc);
		String business_extra = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.BUSINESS_EXTRA));
		msg.setBusiness_extra(business_extra);
		long serial = c.getLong(c.getColumnIndex(t_chatmsg.FIELD.SERIAL));
		msg.setSerial(serial);
		String sender_id = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.SENDER_ID));
		msg.setSender_id(sender_id);
		String sender_name = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.SENDER_NAME));
		msg.setSender_name(sender_name);
		int sender_logistic_type_code = c.getInt(c
				.getColumnIndex(t_chatmsg.FIELD.SENDER_LOGISTIC_TYPE_CODE));
		msg.setSender_logistic_type_code(sender_logistic_type_code);
		String sender_logistic_type_name = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.SENDER_LOGISTIC_TYPE_NAME));
		msg.setSender_logistic_type_name(sender_logistic_type_name);
		String receiver_id = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.RECEIVER_ID));
		msg.setReceiver_id(receiver_id);
		long send_time = c.getLong(c.getColumnIndex(t_chatmsg.FIELD.SEND_TIME));
		msg.setSend_time(send_time);
		int type = c.getInt(c.getColumnIndex(t_chatmsg.FIELD.TYPE));
		msg.setType(type);
		String type_data = c.getString(c
				.getColumnIndex(t_chatmsg.FIELD.TYPE_DATA));
		msg.setType_data(type_data);
		int local_status = c.getInt(c
				.getColumnIndex(t_chatmsg.FIELD.LOCAL_STATUS));
		msg.setLocal_status(local_status);
		int remote_status = c.getInt(c
				.getColumnIndex(t_chatmsg.FIELD.REMOTE_STATUS));
		msg.setRemote_status(remote_status);

		int conversationA = c.getInt(c
				.getColumnIndex(t_chatmsg.FIELD.CONVERSATION_A));
		msg.setConversationA(conversationA);
		int conversationB = c.getInt(c
				.getColumnIndex(t_chatmsg.FIELD.CONVERSATION_B));
		msg.setConversationB(conversationB);

		return msg;
	}

	public void addObserver(ChatMsgObserver observer, String table) {
		if (mMap == null) {
			mMap = new HashMap<String, WeakReference<ChatMsgObserver>>();
		}
		if (observer != null && !TextUtils.isEmpty(table)) {
			mMap.put(table, new WeakReference<ChatMsgDao.ChatMsgObserver>(
					observer));
		}
	}

	public void removeObserver(String table) {
		if (mMap != null) {
			mMap.remove(table);
		}
	}

	public interface ChatMsgObserver {
		void onNewMsg(ChatMsg msg);

		void onClear();
	}
}
