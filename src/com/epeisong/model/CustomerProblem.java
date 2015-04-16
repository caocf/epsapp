package com.epeisong.model;

import java.io.Serializable;

public class CustomerProblem implements Serializable {

	private static final long serialVersionUID = 1L;

	private User user;
	private String content;
	private String problemType;
	private int problemCode;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	public int getProblemCode() {
		return problemCode;
	}

	public void setProblemCode(int problemCode) {
		this.problemCode = problemCode;
	}

}
