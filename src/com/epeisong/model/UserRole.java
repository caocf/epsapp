package com.epeisong.model;

import java.io.Serializable;

public class UserRole implements Serializable, Cloneable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String user_id;
    // 所在地、服务区域
    private int regionCode;
    private String regionName;

    // 当前所在地
    private int currentRegionCode;
    private String currentRegionName;

    // 当前经纬度
    private double current_longitude;
    private double current_latitude;

    // 线路
    private int lineStartCode;
    private String lineStartName;
    private int lineEndCode;
    private String lineEndName;

    // 时效
    private int validityCode;
    private String validityName;

    // 险种
    private int insuranceCode;
    private String insuranceName;

    // 设备类别
    private int deviceCode;
    private String deviceName;

    // 仓库类别
    private int depotCode;
    private String depotName;

    // 包装类别
    private int packCode;
    private String packName;

    // 车长
    private int truckLengthCode;
    private String truckLengthName;

    // 车型
    private int truckTypeCode;
    private String truckTypeName;

    // 荷载
    private float loadTon;

    // 货物类型
    private int goodsTypeCode;
    private String goodsTypeName;

    private int transportTypeCode;
    private String transportTypeName;

    // 权重
    private double weight;

    // 是否满载
    private int is_full_loaded;

    // 赞
    private int RecommendedCount;

    // 不赞
    private int NotRecommendedCount;

    // 派送范围
    private String range_to_delivervarchar;
    private String range_not_to_delivervarchar;

    public void setRecommendedCount(int RecommendedCount) {
        this.RecommendedCount = RecommendedCount;
    }

    public int getRecommendedCount() {
        return RecommendedCount;
    }

    public void setNotRecommendedCount(int NotRecommendedCount) {
        this.NotRecommendedCount = NotRecommendedCount;
    }

    public int getNotRecommendedCount() {
        return NotRecommendedCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setRegionCode(int regionCode) {
        this.regionCode = regionCode;
    }

    public int getRegionCode() {
        return regionCode;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setCurrentRegionCode(int currentRegionCode) {
        this.currentRegionCode = currentRegionCode;
    }

    public int getCurrentRegionCode() {
        return currentRegionCode;
    }

    public void setCurrentRegionName(String currentRegionName) {
        this.currentRegionName = currentRegionName;
    }

    public String getCurrentRegionName() {
        return currentRegionName;
    }

    public void setCurrent_longitude(double current_longitude) {
        this.current_longitude = current_longitude;
    }

    public double getCurrent_longitude() {
        return current_longitude;
    }

    public void setCurrent_latitude(double current_latitude) {
        this.current_latitude = current_latitude;
    }

    public double getCurrent_latitude() {
        return current_latitude;
    }

    public void setLineStartCode(int lineStartCode) {
        this.lineStartCode = lineStartCode;
    }

    public int getLineStartCode() {
        return lineStartCode;
    }

    public void setLineStartName(String lineStartName) {
        this.lineStartName = lineStartName;
    }

    public String getLineStartName() {
        return lineStartName;
    }

    public String getLineEndName() {
        return lineEndName;
    }

    public void setLineEndCode(int lineEndCode) {
        this.lineEndCode = lineEndCode;
    }

    public int getLineEndCode() {
        return lineEndCode;
    }

    public void setLineEndName(String lineEndName) {
        this.lineEndName = lineEndName;
    }

    public void setValidityCode(int validityCode) {
        this.validityCode = validityCode;
    }

    public int getValidityCode() {
        return validityCode;
    }

    public void setValidityName(String validityName) {
        this.validityName = validityName;
    }

    public String getValidityName() {
        return validityName;
    }

    public void setInsuranceCode(int insuranceCode) {
        this.insuranceCode = insuranceCode;
    }

    public int getInsuranceCode() {
        return insuranceCode;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setDeviceCode(int deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDepotCode(int depotCode) {
        this.depotCode = depotCode;
    }

    public int getDepotCode() {
        return depotCode;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setPackCode(int packCode) {
        this.packCode = packCode;
    }

    public int getPackCode() {
        return packCode;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getPackName() {
        return packName;
    }

    public void setTruckLengthCode(int truckLengthCode) {
        this.truckLengthCode = truckLengthCode;
    }

    public int getTruckLengthCode() {
        return truckLengthCode;
    }

    public void setTruckLengthName(String truckLengthName) {
        this.truckLengthName = truckLengthName;
    }

    public String getTruckLengthName() {
        if (truckLengthName == null) {
            return "";
        }
        return truckLengthName;
    }

    public void setTruckTypeCode(int truckTypeCode) {
        this.truckTypeCode = truckTypeCode;
    }

    public int getTruckTypeCode() {
        return truckTypeCode;
    }

    public void setTruckTypeName(String truckTypeName) {
        this.truckTypeName = truckTypeName;
    }

    public String getTruckTypeName() {
        if (truckTypeName == null) {
            return "";
        }
        return truckTypeName;
    }

    public float getLoadTon() {
        return loadTon;
    }

    public void setLoadTon(float loadTon) {
        this.loadTon = loadTon;
    }

    public void setGoodsTypeCode(int goodsTypeCode) {
        this.goodsTypeCode = goodsTypeCode;
    }

    public int getGoodsTypeCode() {
        return goodsTypeCode;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public int getTransportTypeCode() {
        return transportTypeCode;
    }

    public void setTransportTypeCode(int transportTypeCode) {
        this.transportTypeCode = transportTypeCode;
    }

    public String getTransportTypeName() {
        return transportTypeName;
    }

    public void setTransportTypeName(String transportTypeName) {
        this.transportTypeName = transportTypeName;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setrangedeliver(String range_to_delivervarchar) {
        this.range_to_delivervarchar = range_to_delivervarchar;
    }

    public String getrangedeliver() {
        return range_to_delivervarchar;
    }

    public String getRange_not_to_delivervarchar() {
        return range_not_to_delivervarchar;
    }

    public void setRange_not_to_delivervarchar(String range_not_to_delivervarchar) {
        this.range_not_to_delivervarchar = range_not_to_delivervarchar;
    }

    public void setIs_full_loaded(int is_full_loaded) {
        this.is_full_loaded = is_full_loaded;
    }

    public int getIs_full_loaded() {
        return is_full_loaded;
    }

    public void setDictionary(Dictionary d) {
        int code = d.getId();
        String name = d.getName();
        switch (d.getType()) {
        case Dictionary.TYPE_TRUCK_LENGTH:
            setTruckLengthCode(code);
            setTruckLengthName(name);
            break;
        case Dictionary.TYPE_TRUCK_TYPE:
            setTruckTypeCode(code);
            setTruckTypeName(name);
            break;
        default:
            break;
        }
    }

    public String getDesc() {
        StringBuilder sb = new StringBuilder();
        if (regionName != null) {
            sb.append("服务区\\所在地:" + regionName);
        }
        if (lineStartName != null && lineEndName != null) {
            sb.append("线路:" + lineStartName + "-" + lineEndName);
        }
        return sb.toString();
    }

    public String getline() {
        StringBuilder sb = new StringBuilder();

        if (lineStartName != null && lineEndName != null && 0 != lineStartName.trim().length()
                && 0 != lineEndName.trim().length()) {
            if (lineStartName.equals(lineEndName)) {
                return lineStartName;
            }
            sb.append(lineStartName).append("-").append(lineEndName);
        } else {
            return "";
        }
        return sb.toString();
    }

    public UserRole cloneSelf() {
        try {
            return (UserRole) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRegionCalled(int type) {
        switch (type) {
        // case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
        // return "常驻地区";

        default:
            return "地区";// 区域";全部统一为地区，除了附近的
        }
    }
}
