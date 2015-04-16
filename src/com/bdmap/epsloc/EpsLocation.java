package com.bdmap.epsloc;

import java.io.Serializable;

import com.epeisong.data.dao.RegionDao;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;

/**
 * Eps地理服务对象
 * @author poet
 *
 */
public class EpsLocation implements Serializable, Cloneable {

    private static final long serialVersionUID = 4598720119707554359L;

    private long updateTime;
    private String time;

    private int districtCode = -1;
    private String districtName = "";
    private int cityCode = -1;
    private String cityName = "";
    private int provinceCode = -1;
    private String provinceName = "";
    private int addressCode = -1;
    private String addressName = "";
    private double longitude = -1;
    private double latitude = -1;

    private int countryCode = -1;
    private String countryName = "";

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(int districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(int addressCode) {
        this.addressCode = addressCode;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        EpsLocation loc = (EpsLocation) o;
        return this.getLatitude() == loc.getLatitude() && this.longitude == loc.getLongitude();
    }

    public EpsLocation cloneSelf() {
        try {
            return (EpsLocation) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RegionResult convertToResult() {
        Region region = RegionDao.getInstance().queryByCityName(getCityName());
        if (region != null) {
            Region distict = RegionDao.getInstance().queryByDistrictNameAndCityCode(districtName, cityCode);
            return RegionDao.convertToResult(distict);
        }
        return RegionDao.convertToResult(region);
    }

    public Region convertToRegion() {
        return RegionDao.getInstance().queryByCityName(getCityName());
    }
}
