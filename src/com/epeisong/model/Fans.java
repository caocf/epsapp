package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;

import com.epeisong.data.dao.helper.FansDaoHelper.t_fans;

/**
 * 新联系人（添加联系人、接受联系人邀请）
 * 
 * @author poet
 * 
 */
public class Fans implements Serializable {

	private static final long serialVersionUID = -6548815462647476585L;

	public static final int source_phone = 1; // 通过手机号码查找添加
	public static final int source_scan = 2; // 通过扫一扫添加

	public static final int status_un_read = 0;
	public static final int status_read = 1;
	public static final int status_added = 2;

	private String id;
	private String name;
	private int source;
	private int status;
	private long time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(t_fans.FIELD.ID, id);
		values.put(t_fans.FIELD.NAME, name);
		values.put(t_fans.FIELD.SOURCE, source);
		values.put(t_fans.FIELD.STATUS, status);
		values.put(t_fans.FIELD.TIME, time);
		return values;
	}
}
