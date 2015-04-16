package com.epeisong.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

/**
 * 选择地区返回结果对象
 * 
 * @author poet
 * 
 */
public class RegionResult implements Serializable {

    private static final long serialVersionUID = -2826454401438999978L;

    private int code = 0;

    private int type;

    private String regionName = "";

    private String provinceName = "";

    private String cityName = "";

    private String districtName = "";

    public int getCode() {
        return code;
    }

    public int getFullCode() {
        return getFullCode(code);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        if (regionName != null)
            this.regionName = regionName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        if (provinceName != null)
            this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        if (cityName != null)
            this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        if (districtName != null)
            this.districtName = districtName;
    }

    private int getFullCode(int code) {
        if (code > 0) {
            if (code < 100000) {
                if (code > 1000) {
                    return code * 100;
                } else if (code > 10) {
                    return code * 10000;
                } else {
                    return code * 100000;
                }
            }
        }
        return code;
    }

    public String getGeneralName() {
        String name = provinceName + cityName + districtName;
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        return regionName;
    }

    public String getShortNameFromDistrict() {
        if (!TextUtils.isEmpty(districtName)) {
            return cityName + districtName;
        }
        if (!TextUtils.isEmpty(cityName)) {
            return cityName;
        }
        if (!TextUtils.isEmpty(provinceName)) {
            return provinceName;
        }
        if (!TextUtils.isEmpty(regionName)) {
            return regionName;
        }
        return "";
    }

    public String getFullName() {
        List<String> names = new ArrayList<String>();
        if (!TextUtils.isEmpty(regionName)) {
            names.add(regionName);
        }
        if (!TextUtils.isEmpty(provinceName)) {
            names.add(provinceName);
        }
        if (!TextUtils.isEmpty(cityName)) {
            names.add(cityName);
        }
        if (!TextUtils.isEmpty(districtName)) {
            names.add(districtName);
        }
        if (names.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append(name);
        }
        return sb.toString();
    }

    public String getNameByType() {
        switch (type) {
        case 0:
            return regionName;
        case 1:
            return provinceName;
        case 11:
        case 2:
            return cityName;
        case 3:
            return districtName;
        default:
            return "";
        }
    }
}
