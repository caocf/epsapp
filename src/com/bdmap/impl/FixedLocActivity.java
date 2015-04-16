package com.bdmap.impl;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;

/**
 * 根据经纬度显示地图
 * @author poet
 *
 */
public class FixedLocActivity extends BaseActivity {

    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_DESC = "desc";

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;

    private double mLongitude, mLatitude;
    private String mDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLongitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
        mLatitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        mDesc = getIntent().getStringExtra(EXTRA_DESC);
        if (TextUtils.isEmpty(mDesc)) {
            mDesc = "我在这儿";
        }
        BaiduMapOptions mapOptions = new BaiduMapOptions();
        mapOptions.zoomControlsEnabled(false);
        mapOptions.zoomGesturesEnabled(false);
        mMapView = new MapView(this, mapOptions);
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();

        LatLng ll = new LatLng(mLatitude, mLongitude);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        TextView tv = new TextView(this);
        tv.setBackgroundResource(R.drawable.bg_more_menu);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tv.setText(mDesc);
        layout.addView(tv);
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.home_location);
        layout.addView(iv);
        mInfoWindow = new InfoWindow(layout, ll, 0);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "位置", null).setShowLogo(false);
    }

    private void resetLoc() {
        MyLocationData locData = new MyLocationData.Builder()
        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(mLatitude).longitude(mLongitude).build();
        mBaiduMap.setMyLocationData(locData);

        LatLng ll = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);

        MapStatusUpdate zoomTo = MapStatusUpdateFactory.zoomTo(8); //16
        mBaiduMap.animateMapStatus(zoomTo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        resetLoc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
}
