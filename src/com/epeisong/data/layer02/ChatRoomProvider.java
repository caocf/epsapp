package com.epeisong.data.layer02;

import java.util.List;

import com.epeisong.data.dao.ChatRoomDao;
import com.epeisong.model.ChatRoom;

public class ChatRoomProvider {

	public List<ChatRoom> provideFirst(int size) {
		return ChatRoomDao.getInstance().queryFirst(size);
	}

	public List<ChatRoom> provideOlder(long last_time, int size) {
		return ChatRoomDao.getInstance().queryOlder(last_time, size);
	}

	public List<ChatRoom> provideNewer(long last_time, int size) {
		return ChatRoomDao.getInstance().queryNewer(last_time, size);
	}
}
