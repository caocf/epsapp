package com.epeisong.utils;

import java.io.File;

import android.content.Context;

import com.epeisong.EpsApplication;

/**
 * 本地缓存清除
 * 
 * @author poet
 * 
 */
public class CacheDataUtils {

	private static Context context = EpsApplication.getInstance();

	public static void clearAppData() {
		clearDBs();
		clearSp();
		SpUtils.clear();
	}

	public static void clearDB(String dbName) {
		context.deleteDatabase(dbName);
	}

	public static void clearDBs() {
		File file = new File("/data/data/"
				+ context.getPackageName() + "/databases/");
		clear(file);
	}

	public static void clearSp() {
		File dir = new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs");
		clear(dir);
	}

	private static void clear(File dir) {
		if (dir != null && dir.exists() && dir.isDirectory()) {
			LogUtils.et("CacheDataUtils.clear:" + dir.getPath());
			for (File item : dir.listFiles()) {
				item.delete();
			}
		}
	}
}
