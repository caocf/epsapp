package com.epeisong.model;

import java.io.Serializable;

public class RegionArea implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private int regionCode;
	private String regionName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

}
