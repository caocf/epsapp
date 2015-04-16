package com.bdmap.impl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.ToastUtils;

/**
 * 显示地图位置
 * @author poet
 *
 */
public class MyLocActivity extends BaseActivity implements OnEpsLocationListener {

    public static final String EXTRA_OUT_EPS_LOCATION = "out_location";

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    EpsLocation mEpsLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
            mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(p).build()));
        } else {
            mMapView = new MapView(this, new BaiduMapOptions());
        }
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        EpsLocationRequestor requestor = new EpsLocationRequestor();
        requestor.requestEpsLocation(this);
    }

    @Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ActionImpl() {

            @Override
            public View getView() {
                return getRightTextView("发送", R.drawable.shape_content_blue);
            }

            @Override
            public void doAction(View v) {
                if (mEpsLocation != null) {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_OUT_EPS_LOCATION, mEpsLocation);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    ToastUtils.showToast("位置获取失败");
                }
            }
        });
        return new TitleParams(getDefaultHomeAction(), "我的位置", actions).setShowLogo(false);
    }

    @Override
    public void onEpsLocation(EpsLocation epsLocation) {
        mEpsLocation = epsLocation;
        resetLoc(epsLocation);
    }

    private void resetLoc(EpsLocation loc) {
        dismissPendingDialog();
        // MyLocationData locData = new
        // MyLocationData.Builder().accuracy(loc.getRadius())
        MyLocationData locData = new MyLocationData.Builder()
        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(loc.getLatitude()).longitude(loc.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);

        LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);

        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                MapStatusUpdate zoomTo = MapStatusUpdateFactory.zoomTo(16);
                mBaiduMap.animateMapStatus(zoomTo);
            }
        }, 400);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
        showPendingDialog("正在定位...");
        if (mEpsLocation != null) {
            resetLoc(mEpsLocation);
        }
    }

    @Override
    protected void onDestroy() {
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
        super.onDestroy();
    }
}
