package com.epeisong.model;

import java.io.Serializable;

public class Courier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private Float CurrentLatitude;
	private Float CurrentLongitude;
	private String ServeRegionName;
	private int SerializedSize;
	private String CurrentRegionName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Float getCurrentLatitude() {
		return CurrentLatitude;
	}

	public void setCurrentLatitude(Float currentLatitude) {
		CurrentLatitude = currentLatitude;
	}

	public Float getCurrentLongitude() {
		return CurrentLongitude;
	}

	public void setCurrentLongitude(Float currentLongitude) {
		CurrentLongitude = currentLongitude;
	}

	public String getServeRegionName() {
		return ServeRegionName;
	}

	public void setServeRegionName(String serveRegionName) {
		ServeRegionName = serveRegionName;
	}

	public int getSerializedSize() {
		return SerializedSize;
	}

	public void setSerializedSize(int serializedSize) {
		SerializedSize = serializedSize;
	}

	public String getCurrentRegionName() {
		return CurrentRegionName;
	}

	public void setCurrentRegionName(String currentRegionName) {
		CurrentRegionName = currentRegionName;
	}

}
