package com.epeisong.model;

import java.io.Serializable;

public class EquipmentLeasing implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String id;
    private int equipment_type_code;
    private String equipment_type_name;
    private String ServeRegionName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEquipment_type_code() {
        return equipment_type_code;
    }

    public void setEquipment_type_code(int equipment_type_code) {
        this.equipment_type_code = equipment_type_code;
    }

    public String getEquipment_type_name() {
        return equipment_type_name;
    }

    public void setEquipment_type_name(String equipment_type_name) {
        this.equipment_type_name = equipment_type_name;
    }

    public String getServeRegionName() {
        return ServeRegionName;
    }

    public void setServeRegionName(String serveRegionName) {
        ServeRegionName = serveRegionName;
    }

}
