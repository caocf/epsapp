package com.epeisong.model;

import java.io.Serializable;

/**
 * 申诉 - 
 * 
 * @author Jack
 * 
 */

public class GuaComplainTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InfoFee infoFee;
	private ComplainTask complainTask;
	
	public void setInfoFee(InfoFee infoFee) {
		this.infoFee = infoFee;
	}
	
	public void setComplainTask(ComplainTask complainTask) {
		this.complainTask = complainTask;
	}
	
	public InfoFee getInfoFee() {
		return this.infoFee;
	}
	
	public ComplainTask getComplainTask() {
		return this.complainTask;
	}
	
}
