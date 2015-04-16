package com.bdmap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.epeisong.EpsApplication;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.model.Region;

/**
 * 定位管理
 * 
 * @author poet
 * @date 2014-11-24 下午10:45:16
 */
@Deprecated
public class LocManager implements BDLocationListener {

    public static LocManager single;

    private List<WeakReference<RegionObserver>> mRegionObserverRefs;

    private List<WeakReference<LocObserver>> mBdLocObserverRefs;

    private LocationClient mLocationClient;
    private BDLocation mLocation;
    private Region mRegion;

    private LocManager() {
        mRegion = RegionDao.getInstance().queryByCityName("南京市");
        if (mRegion == null) {
            mRegion = new Region();
            mRegion.setCode(3201);
            mRegion.setName("南京市");
        }

        mRegionObserverRefs = new ArrayList<WeakReference<RegionObserver>>();
        mBdLocObserverRefs = new ArrayList<WeakReference<LocObserver>>();

        mLocationClient = new LocationClient(EpsApplication.getInstance());
        mLocationClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        // option.setCoorType("gcj02");
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public static LocManager getInstance() {
        if (single == null) {
            synchronized (LocManager.class) {
                if (single == null) {
                    single = new LocManager();
                }
            }
        }
        return single;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLoc) {
        if (bdLoc != null) {
            mLocation = null;
            mLocation = bdLoc;
        }
        boolean change = false;
        if (bdLoc != null && !TextUtils.isEmpty(bdLoc.getCity())) {
            Region region = RegionDao.getInstance().queryByCityName(bdLoc.getCity());
            if (region != null) {
                mRegion = region;
                change = true;
            }
        }
        if (change) {
            Iterator<WeakReference<RegionObserver>> iterator = mRegionObserverRefs.iterator();
            while (iterator.hasNext()) {
                WeakReference<RegionObserver> next = iterator.next();
                RegionObserver ob = next.get();
                if (ob == null) {
                    iterator.remove();
                } else {
                    ob.onRegionChange(mRegion);
                }
            }
        }
        if (!mBdLocObserverRefs.isEmpty()) {
            Iterator<WeakReference<LocObserver>> it = mBdLocObserverRefs.iterator();
            while (it.hasNext()) {
                WeakReference<LocObserver> next = it.next();
                if (next.get() == null) {
                    it.remove();
                } else {
                    next.get().onBdLocChange(bdLoc);
                }
            }
        }
        mLocationClient.stop();
    }

    public Region getRegion(RegionObserver ob) {
        if (ob != null) {
            mRegionObserverRefs.add(new WeakReference<RegionObserver>(ob));
        }
        return mRegion;
    }

    public BDLocation getBdLocation(LocObserver ob) {
        if (ob != null) {
            mBdLocObserverRefs.add(new WeakReference<LocManager.LocObserver>(ob));
        }
        if (mLocation == null && ob != null) {
            refreshBdLoc();
        }
        return mLocation;
    }

    public void refreshBdLoc() {
        mLocationClient.stop();
        mLocationClient.start();
    }

    public void stop() {
        mLocationClient.stop();
    }

    public void removeRegionObserver(RegionObserver ob) {
        Iterator<WeakReference<RegionObserver>> iterator = mRegionObserverRefs.iterator();
        while (iterator.hasNext()) {
            WeakReference<RegionObserver> next = iterator.next();
            if (next.get() == null || next.get() == ob) {
                iterator.remove();
                if (next.get() == ob) {
                    break;
                }
            }
        }
    }

    public void removeBdLocObserver(LocObserver ob) {
        Iterator<WeakReference<LocObserver>> it = mBdLocObserverRefs.iterator();
        while (it.hasNext()) {
            WeakReference<LocObserver> next = it.next();
            if (next.get() == null || next.get() == ob) {
                it.remove();
                if (next.get() == ob) {
                    break;
                }
            }
        }
    }

    public interface RegionObserver {
        void onRegionChange(Region region);
    }

    public interface LocObserver {
        void onBdLocChange(BDLocation loc);
    }

}
