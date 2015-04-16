package com.epeisong.model;

import java.io.Serializable;

import android.content.ContentValues;

/**
 * 消息：公告消息、会话信息、咨询的会话信息
 * 
 * @author poet
 * 
 */
public class Msg implements Serializable, Comparable<Msg> {

	private static final long serialVersionUID = 3286504916427058177L;

	protected String id;
	protected int type;
	protected String type_extra_data;
	protected String remote_id; // 该消息和我的直接关系人的id
	protected String sender_id;
	protected long create_time;
	protected long update_time;
	protected String content;
	protected int content_type;
	protected int status;
	protected String extra_01;
	protected String extra_02;
	protected String extra_03;
	protected String extra_04;
	protected User remote;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getType_extra_data() {
		return type_extra_data;
	}

	public void setType_extra_data(String type_extra_data) {
		this.type_extra_data = type_extra_data;
	}

	public String getRemote_id() {
		return remote_id;
	}

	public void setRemote_id(String remote_id) {
		this.remote_id = remote_id;
	}

	public User getRemote() {
		return remote;
	}

	public void setRemote(User remote) {
		this.remote = remote;
	}

	public String getSender_id() {
		return sender_id;
	}

	public void setSender_id(String sender_id) {
		this.sender_id = sender_id;
	}

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		this.update_time = update_time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getContent_type() {
		return content_type;
	}

	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getExtra_01() {
		return extra_01;
	}

	public void setExtra_01(String extra_01) {
		this.extra_01 = extra_01;
	}

	public String getExtra_02() {
		return extra_02;
	}

	public void setExtra_02(String extra_02) {
		this.extra_02 = extra_02;
	}

	public String getExtra_03() {
		return extra_03;
	}

	public void setExtra_03(String extra_03) {
		this.extra_03 = extra_03;
	}

	public String getExtra_04() {
		return extra_04;
	}

	public void setExtra_04(String extra_04) {
		this.extra_04 = extra_04;
	}

	public boolean isSelf() {
		return false;
	}

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		
		return values;
	}

	@Override
	public int compareTo(Msg another) {
		if (another == null) {
			return -1;
		}
		return (int) (another.getUpdate_time() - update_time);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o != null && o instanceof Msg) {
			Msg msg = (Msg) o;
			return this.getId().equals(msg.getId());
		}
		return false;
	}
}
