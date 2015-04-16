package com.epeisong.data.layer02;

import java.util.List;

import com.epeisong.data.dao.BulletinDao;
import com.epeisong.model.Bulletin;

public class BulletinProvider {

	public List<Bulletin> provideFirst(int size) {
		return BulletinDao.getInstance().queryFirst(size);
	}

	public List<Bulletin> provideOlder(long last_time, int size) {
		return BulletinDao.getInstance().queryOlder(last_time, size);
	}

	public List<Bulletin> provideNewer(long last_time, int size) {
		return BulletinDao.getInstance().queryNewer(last_time, size);
	}
}
