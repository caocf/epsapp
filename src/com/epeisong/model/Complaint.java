package com.epeisong.model;

import java.io.Serializable;

/**
 * 投诉对象
 * 
 * @author gnn
 * 
 */
public class Complaint implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private int count;
	private int status;
	private String owner_id;
	private String content; // 投诉内容
	private String nameId; // 投诉方的ID
	private String name; // 投诉方
	private String phone; // 投诉方电话
	private String byName; // 被投诉方
	private String byPhone; // 被投诉方电话
	private String byNameId; // 被投诉方的ID
	private String result; // 投诉结果

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getByName() {
		return byName;
	}

	public void setByName(String byName) {
		this.byName = byName;
	}

	public String getByPhone() {
		return byPhone;
	}

	public void setByPhone(String byPhone) {
		this.byPhone = byPhone;
	}

	public String getNameId() {
		return nameId;
	}

	public void setNameId(String nameId) {
		this.nameId = nameId;
	}

	public String getByNameId() {
		return byNameId;
	}

	public void setByNameId(String byNameId) {
		this.byNameId = byNameId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
