package com.bdmap.view;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class SmallMapView extends FrameLayout {

    MapView mMapView;
    BaiduMap mBaiduMap;

    public SmallMapView(Context context) {
        super(context);
        init(context);
    }

    public SmallMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(Context context) {
        BaiduMapOptions mapOptions = new BaiduMapOptions();
        mapOptions.zoomControlsEnabled(false);
        mapOptions.zoomGesturesEnabled(false);
        mapOptions.scrollGesturesEnabled(false);
        mapOptions.scaleControlEnabled(false);
        mMapView = new MapView(context, mapOptions);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
    }
}
