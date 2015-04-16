package com.epeisong.utils;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.util.Log;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.ui.activity.user.LoadingActivity;

/**
 * Android 系统工具类
 * 
 * @author poet
 * 
 */
public class SysUtils {

	private static Context context = EpsApplication.getInstance();

	/**
	 * 发送短信
	 * 
	 * @param phone
	 * @param message
	 */
	public static void sendSms(Activity activity, String phone, String message) {
		Uri uri = Uri.parse("smsto:" + phone);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", message);
		activity.startActivity(intent);
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return info != null && info.isAvailable();
	}

	/**
	 * 获取网络连接类型
	 * 
	 * @return -1 代表无连接；其他，ConnectivityManager.TYPE_**
	 */
	public static int getNetworkType() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return -1;
		}
		return info.getType();
	}

	public static void vibrate(long[] pattern) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, -1);
	}

	public static String getProcessName() {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	public static PackageInfo getPackageInfo() {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void queryContacts() {
		// 获取用来操作数据的类的对象，对联系人的基本操作都是使用这个对象
		ContentResolver cr = EpsApplication.getInstance().getContentResolver();
		// 查询contacts表的所有记录
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		// 如果记录不为空
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String rawContactId = "";
				// 从Contacts表当中取得ContactId
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				Cursor rawContactCur = cr.query(RawContacts.CONTENT_URI, null,
						RawContacts._ID + "=?", new String[] { id }, null);
				// 该查询结果一般只返回一条记录，所以我们直接让游标指向第一条记录
				if (rawContactCur.moveToFirst()) {
					// 读取第一条记录的RawContacts._ID列的值
					rawContactId = rawContactCur.getString(rawContactCur
							.getColumnIndex(RawContacts._ID));
					Log.v("rawContactID", rawContactId);

				}
				// 关闭游标
				rawContactCur.close();
				// 读取号码
				if (Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					// 根据查询RAW_CONTACT_ID查询该联系人的号码
					Cursor phoneCur = cr
							.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
									null,
									ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
											+ "=?",
									new String[] { rawContactId }, null);

					// 一个联系人可能有多个号码，需要遍历
					while (phoneCur.moveToNext()) {
						// 获取号码
						String number = phoneCur
								.getString(phoneCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Log.v("number", number);
						// 获取号码类型
						String type = phoneCur
								.getString(phoneCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						Log.v("type", type);

					}
					phoneCur.close();

				}
			}
			cursor.close();
		}
	}

	public static void addContactss() {
		for (int i = 0; i < 100; i++) {
			long cur = System.currentTimeMillis() + i;
			String phoneNum = "1" + String.valueOf(cur).substring(3);
			SysUtils.addContacts(DateUtil.long2MS_SSS(cur), phoneNum);
		}
		return;
	}

	public static void addContacts(String name, String phoneNum) {
		ContentValues values = new ContentValues();
		Uri rawContactUri = EpsApplication.getInstance().getContentResolver()
				.insert(RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);
		// 向data表插入数据
		if (name != "") {
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			values.put(StructuredName.GIVEN_NAME, name);
			EpsApplication.getInstance().getContentResolver()
					.insert(ContactsContract.Data.CONTENT_URI, values);
		}
		// 向data表插入电话号码
		if (phoneNum != "") {
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			values.put(Phone.NUMBER, phoneNum);
			values.put(Phone.TYPE, Phone.TYPE_MOBILE);
			EpsApplication.getInstance().getContentResolver()
					.insert(ContactsContract.Data.CONTENT_URI, values);
		}
	}

	private static String getAuthorityFromPermission(String permission) {
		if (permission == null)
			return null;
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(PackageManager.GET_PROVIDERS);
		if (packs != null) {
			for (PackageInfo pack : packs) {
				ProviderInfo[] providers = pack.providers;
				if (providers != null) {
					for (ProviderInfo provider : providers) {
						if (permission.equals(provider.readPermission))
							return provider.authority;
						if (permission.equals(provider.writePermission))
							return provider.authority;
					}
				}
			}
		}
		return null;
	}

	public static boolean hasShortcut() {
		try {
			String authority = getAuthorityFromPermission("com.android.launcher.permission.READ_SETTINGS");
			String url = "content://" + authority + "/favorites?notify=true";
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver
					.query(Uri.parse(url),
							null,
							"title=?",
							new String[] { context.getString(R.string.app_name) },
							null);

			if (cursor != null && cursor.moveToFirst()) {
				cursor.close();
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void installShortcut() {
		Intent shortCutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
				R.drawable.ic_launcher); // 获取快捷键的图标
		shortCutIntent.putExtra("duplicate", false);
		Intent intent = new Intent(context, LoadingActivity.class);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				context.getString(R.string.app_name));// 快捷方式的标题
		shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
		shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);// 快捷方式的动作
		context.sendBroadcast(shortCutIntent);
	}
}
