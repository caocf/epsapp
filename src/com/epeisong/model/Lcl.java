package com.epeisong.model;

import java.io.Serializable;

/**
 * 零担专线
 * 
 * @author poet
 * 
 */
public class Lcl implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String id;
    private String RouteNameA;
    private String RouteNameB;
    private int load_type_code;
    private String load_type_name;
    private int PeriodOfValidity;
    private String ServeRegionName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteNameA() {
        return RouteNameA;
    }

    public void setRouteNameA(String routeNameA) {
        RouteNameA = routeNameA;
    }

    public String getRouteNameB() {
        return RouteNameB;
    }

    public void setRouteNameB(String routeNameB) {
        RouteNameB = routeNameB;
    }

    public int getLoad_type_code() {
        return load_type_code;
    }

    public void setLoad_type_code(int load_type_code) {
        this.load_type_code = load_type_code;
    }

    public String getLoad_type_name() {
        return load_type_name;
    }

    public void setLoad_type_name(String load_type_name) {
        this.load_type_name = load_type_name;
    }

    public int getPeriodOfValidity() {
        return PeriodOfValidity;
    }

    public void setPeriodOfValidity(int periodOfValidity) {
        PeriodOfValidity = periodOfValidity;
    }

    public String getServeRegionName() {
        return ServeRegionName;
    }

    public void setServeRegionName(String serveRegionName) {
        ServeRegionName = serveRegionName;
    }

}
