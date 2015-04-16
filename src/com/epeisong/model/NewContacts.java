package com.epeisong.model;

import android.content.ContentValues;

/**
 * 新联系人（添加联系人、接受联系人邀请）
 * 
 * @author poet
 * 
 */
public class NewContacts  {

	public static final int source_phone = 1; // 通过手机号码查找添加
	public static final int source_scan = 2; // 通过扫一扫添加

	public static final int type_mine_request = 1; // 我发出的邀请
	public static final int type_remote_request = 2; // 别人添加我

	public static final int status_pending = 1; // 等待验证（接受）
	public static final int status_complete = 2; // 已通过验证（已接受）

	public static final int _status_un_read = 0;
	public static final int _status_read = 1;

	private String contacts_id;
	private int source;
	private int type;
	private int status; // 是否完成添加
	private int _status; // 已读、未读
	private String message; // 别人请求添加自己时填写的信息

	private Contacts contacts;

	public String getContacts_id() {
		return contacts_id;
	}

	public void setContacts_id(String contacts_id) {
		this.contacts_id = contacts_id;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int get_status() {
		return _status;
	}

	public void set_status(int _status) {
		this._status = _status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Contacts getContacts() {
		return contacts;
	}

	public void setContacts(Contacts contacts) {
		this.contacts = contacts;
	}

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		return values;
	}
}
