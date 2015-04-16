package com.epeisong.service.thread;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationHolder;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.NetLogisticsUpdate;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.android.AsyncTask;

public class RefreshEpsLocationThread extends Thread implements OnEpsLocationListener {

    static final long EPS_LOCATION_HOLDER_SET_DURATION = 1000 * 60 * 10;

    private boolean stop;

    private EpsLocationRequestor mRequestor;

    private long mLastSetTime; // 上一次设置全局EpsLocation的时间

    private long mDuration; // 线程执行间隔

    private boolean mIsRefreshLoc;

    public RefreshEpsLocationThread() {
        mRequestor = new EpsLocationRequestor();
        mDuration = getDuration();
        if (mDuration < 5000) {
            mDuration = 5000;
        }
        mIsRefreshLoc = SpUtilsCur.getInt(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 0) == 1;
    }

    @Override
    public void run() {
        mRequestor.requestEpsLocation(this);
        while (!stop) {
            try {
                Thread.sleep(mDuration);
            } catch (InterruptedException e) {
                stop = true;
                LogUtils.e(this.getClass().getName(), e);
                break;
            }
            mIsRefreshLoc = SpUtilsCur.getInt(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 0) == 1;
            if ((System.currentTimeMillis() - mLastSetTime) > EPS_LOCATION_HOLDER_SET_DURATION || mIsRefreshLoc) {
                mRequestor.requestEpsLocation(this);
            }
        }
    }

    @Override
    public void onEpsLocation(EpsLocation epsLocation) {
        if (System.currentTimeMillis() - mLastSetTime > EPS_LOCATION_HOLDER_SET_DURATION) {
            EpsLocationHolder.setEpsLocation(epsLocation);
            mLastSetTime = System.currentTimeMillis();
        }

        mIsRefreshLoc = SpUtilsCur.getInt(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 0) == 1;
        if (mIsRefreshLoc) {
            uploadLocation(epsLocation);
        }
    }

    void uploadLocation(final EpsLocation loc) {
        if (loc == null) {
            return;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                NetLogisticsUpdate.updateLocation(loc, Properties.IS_REALTIME_COURIER);
                return null;
            }
        };
        task.execute();
    }

    public void shutdown() {
        if (!stop) {
            stop = true;
            interrupt();
        }
    }

    long getDuration() {
        User user = UserDao.getInstance().getUser();
        switch (user.getUser_type_code()) {
        case Properties.LOGISTIC_TYPE_COURIER:
            return 1000 * 19;
        default:
            return 1000 * 60 * 10;
        }
    }
}
