package com.epeisong.model;

import java.io.Serializable;

public class Packaging implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String id;
    private int pack_type_code;
    private String pack_type_name;
    private String ServeRegionName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPack_type_code() {
        return pack_type_code;
    }

    public void setPack_type_code(int pack_type_code) {
        this.pack_type_code = pack_type_code;
    }

    public String getPack_type_name() {
        return pack_type_name;
    }

    public void setPack_type_name(String pack_type_name) {
        this.pack_type_name = pack_type_name;
    }

    public String getServeRegionName() {
        return ServeRegionName;
    }

    public void setServeRegionName(String serveRegionName) {
        ServeRegionName = serveRegionName;
    }

}
