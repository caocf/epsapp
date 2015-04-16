package com.bdmap.epsloc;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.epeisong.EpsApplication;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.model.Region;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class EpsLocationRequestor implements BDLocationListener {

    private LocationClient mLocationClient;
    private EpsLocation mEpsLocation;
    private OnEpsLocationListener mEpsLocationListener;

    private boolean mIsOnce = true;

    public EpsLocationRequestor() {
        if (Thread.currentThread() != EpsApplication.getInstance().getMainLooper().getThread()) {
            ToastUtils.showToastInThread("EpsLocationRequestor必须在主线程使用");
            return;
        }
        mLocationClient = new LocationClient(EpsApplication.getInstance());
        mLocationClient.registerLocationListener(this);
    }

    public void stop() {
        mLocationClient.stop();
        mEpsLocationListener = null;
    }

    public void requestEpsLocation(OnEpsLocationListener l) {
        requestEpsLocation(l, 0);
    }

    public void requestEpsLocation(OnEpsLocationListener l, int spanInMs) {
        if (mLocationClient == null) {
            return;
        }
        mEpsLocationListener = l;
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        if (spanInMs >= 1000) {
            option.setScanSpan(spanInMs);
            mIsOnce = false;
        }
        mLocationClient.setLocOption(option);

        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mLocationClient.start();
        int requestCode = mLocationClient.requestLocation();
        LogUtils.d(this, "requestCode:" + requestCode);
    }

    @Override
    public void onReceiveLocation(final BDLocation loc) {
        if (loc == null || loc.getTime() == null) {
            callListener(null);
            return;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                setEpsLocation(loc);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                callListener(mEpsLocation);
            }
        };
        task.execute();
    }

    void setEpsLocation(BDLocation loc) {
        if (mEpsLocation == null) {
            mEpsLocation = new EpsLocation();
        }
        mEpsLocation.setLongitude(loc.getLongitude());
        mEpsLocation.setLatitude(loc.getLatitude());
        Region city = RegionDao.getInstance().queryByCityName(loc.getCity());
        if (city != null) {
            mEpsLocation.setCityCode(city.getCode());
            mEpsLocation.setCityName(city.getName());
            Region province = RegionDao.getInstance().queryByCode(city.getCode() / 100);
            if (province != null) {
                mEpsLocation.setProvinceCode(province.getCode());
                mEpsLocation.setProvinceName(province.getName());
            }
            Region district = RegionDao.getInstance().queryByDistrictNameAndCityCode(loc.getDistrict(), city.getCode());
            if (district != null) {
                mEpsLocation.setDistrictCode(district.getCode());
                mEpsLocation.setDistrictName(district.getName());
            }
            String address = "";
            if (loc.getStreet() != null) {
                address += loc.getStreet();
            }
            if (loc.getStreetNumber() != null) {
                address += loc.getStreetNumber();
            }
            mEpsLocation.setAddressName(address);
            mEpsLocation.setUpdateTime(System.currentTimeMillis());
            mEpsLocation.setTime(loc.getTime());
        }
    }

    void callListener(EpsLocation epsLoc) {
        if (mEpsLocationListener != null) {
            mEpsLocationListener.onEpsLocation(epsLoc);
        }
        if (mIsOnce) {
            mLocationClient.stop();
            mEpsLocationListener = null;
        }
    }

    public interface OnEpsLocationListener {
        void onEpsLocation(EpsLocation epsLocation);
    }
}
