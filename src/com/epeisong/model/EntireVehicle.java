package com.epeisong.model;

import java.io.Serializable;

import com.epeisong.logistics.common.Properties;

@Deprecated
public class EntireVehicle implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final int IS_FULL_LOADED_FULL = Properties.ENTIRE_VEHICLE_FULL_LOADED;
    public static final int IS_FULL_LOADED_HALF = Properties.ENTIRE_VEHICLE_HALF_LOADED;
    public static final int IS_FULL_LOADED_EMPTY = Properties.ENTIRE_VEHICLE_EMPTY;

    private String id;
    private String user_id;
    private int server_region_code;
    private String server_region_name;
    private int current_region_code;
    private String current_region_name;
    private double current_longitude;
    private double current_latitude;
    private int vehicle_length_code;
    private String vehicle_lenght_name;
    private int vehicle_type_code;
    private String vehicle_type_name;
    private int route_a_code;
    private String route_a_name;
    private int route_b_code;
    private String route_b_name;
    private int is_full_loaded;
    
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getServer_region_code() {
        return server_region_code;
    }

    public void setServer_region_code(int server_region_code) {
        this.server_region_code = server_region_code;
    }

    public String getServer_region_name() {
        return server_region_name;
    }

    public void setServer_region_name(String server_region_name) {
        this.server_region_name = server_region_name;
    }

    public int getCurrent_region_code() {
        return current_region_code;
    }

    public void setCurrent_region_code(int current_region_code) {
        this.current_region_code = current_region_code;
    }

    public String getCurrent_region_name() {
        return current_region_name;
    }

    public void setCurrent_region_name(String current_region_name) {
        this.current_region_name = current_region_name;
    }

    public double getCurrent_longitude() {
        return current_longitude;
    }

    public void setCurrent_longitude(double current_longitude) {
        this.current_longitude = current_longitude;
    }

    public double getCurrent_latitude() {
        return current_latitude;
    }

    public void setCurrent_latitude(double current_latitude) {
        this.current_latitude = current_latitude;
    }

    public int getVehicle_length_code() {
        return vehicle_length_code;
    }

    public void setVehicle_length_code(int vehicle_length_code) {
        this.vehicle_length_code = vehicle_length_code;
    }

    public String getVehicle_lenght_name() {
        return vehicle_lenght_name;
    }

    public void setVehicle_lenght_name(String vehicle_lenght_name) {
        this.vehicle_lenght_name = vehicle_lenght_name;
    }

    public int getVehicle_type_code() {
        return vehicle_type_code;
    }

    public void setVehicle_type_code(int vehicle_type_code) {
        this.vehicle_type_code = vehicle_type_code;
    }

    public String getVehicle_type_name() {
        return vehicle_type_name;
    }

    public void setVehicle_type_name(String vehicle_type_name) {
        this.vehicle_type_name = vehicle_type_name;
    }

    public int getRoute_a_code() {
        return route_a_code;
    }

    public void setRoute_a_code(int route_a_code) {
        this.route_a_code = route_a_code;
    }

    public String getRoute_a_name() {
        return route_a_name;
    }

    public void setRoute_a_name(String route_a_name) {
        this.route_a_name = route_a_name;
    }

    public int getRoute_b_code() {
        return route_b_code;
    }

    public void setRoute_b_code(int route_b_code) {
        this.route_b_code = route_b_code;
    }

    public String getRoute_b_name() {
        return route_b_name;
    }

    public void setRoute_b_name(String route_b_name) {
        this.route_b_name = route_b_name;
    }

    public int getIs_full_loaded() {
        return is_full_loaded;
    }

    public void setIs_full_loaded(int is_full_loaded) {
        this.is_full_loaded = is_full_loaded;
    }

}
