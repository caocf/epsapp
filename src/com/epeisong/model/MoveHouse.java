package com.epeisong.model;

import java.io.Serializable;

public class MoveHouse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String ServeRegionName;
	private Float VehicleLength;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServeRegionName() {
		return ServeRegionName;
	}

	public void setServeRegionName(String serveRegionName) {
		ServeRegionName = serveRegionName;
	}

	public Float getVehicleLength() {
		return VehicleLength;
	}

	public void setVehicleLength(Float vehicleLength) {
		VehicleLength = vehicleLength;
	}

}
