package com.epeisong.model;

import java.io.Serializable;

public class Storage implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String id;
    private int goods_type_code;
    private String goods_type_name;
    private String ServeRegionName;
    private int StorageCapacity;
    private int storage_type_code;
    private String storage_type_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGoods_type_code() {
        return goods_type_code;
    }

    public void setGoods_type_code(int goods_type_code) {
        this.goods_type_code = goods_type_code;
    }

    public String getGoods_type_name() {
        return goods_type_name;
    }

    public void setGoods_type_name(String goods_type_name) {
        this.goods_type_name = goods_type_name;
    }

    public String getServeRegionName() {
        return ServeRegionName;
    }

    public void setServeRegionName(String serveRegionName) {
        ServeRegionName = serveRegionName;
    }

    public int getStorageCapacity() {
        return StorageCapacity;
    }

    public void setStorageCapacity(int storageCapacity) {
        StorageCapacity = storageCapacity;
    }

    public int getStorage_type_code() {
        return storage_type_code;
    }

    public void setStorage_type_code(int storage_type_code) {
        this.storage_type_code = storage_type_code;
    }

    public String getStorage_type_name() {
        return storage_type_name;
    }

    public void setStorage_type_name(String storage_type_name) {
        this.storage_type_name = storage_type_name;
    }

}
