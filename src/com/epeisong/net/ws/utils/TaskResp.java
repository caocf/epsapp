package com.epeisong.net.ws.utils;

import java.util.List;

/**
 * @author Roy Lu
 * @since Dec 29, 2014
 * @function
 */
public class TaskResp {
	public static int SUCC = 1;
	public static int FAIL = -1;

	int result = -1;

	String desc = "";

	Task task = null;
	
	List<Task> taskList = null;
	
	int isAuto = -1;
	
	public String getDesc() {
		return desc;
	}

	public int getResult() {
		return result;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setResult(int result) {
		this.result = result;
	}

    public int getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(int isAuto) {
        this.isAuto = isAuto;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
	
}
