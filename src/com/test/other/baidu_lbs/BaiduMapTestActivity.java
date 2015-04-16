package com.test.other.baidu_lbs;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.ToastUtils;

public class BaiduMapTestActivity extends BaseActivity implements OnMarkerClickListener {

    MapView mMapView;
    BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = new MapView(this);
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMarkerClickListener(this);

        requestLocation();

    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "百度地图");
    }

    void requestLocation() {
        showPendingDialog("定位中...");
        EpsLocationRequestor requestor = new EpsLocationRequestor();
        requestor.requestEpsLocation(new OnEpsLocationListener() {
            @Override
            public void onEpsLocation(EpsLocation epsLocation) {
                dismissPendingDialog();
                if (epsLocation == null || TextUtils.isEmpty(epsLocation.getCityName())) {
                    ToastUtils.showToast("定位失败");
                } else {
                    MyLocationData data = new MyLocationData.Builder().longitude(epsLocation.getLongitude())
                            .latitude(epsLocation.getLatitude()).build();
                    mBaiduMap.setMyLocationData(data);

                    LatLng ll = new LatLng(epsLocation.getLatitude(), epsLocation.getLongitude());
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(u);

                    // 标记、标注
                    BitmapDescriptor desc = BitmapDescriptorFactory.fromResource(R.drawable.home_location);
                    OverlayOptions marker = new MarkerOptions()
                            .position(new LatLng(epsLocation.getLatitude() + 0.01, epsLocation.getLongitude() + 0.01))
                            .icon(desc).title("marker");
                    mBaiduMap.addOverlay(marker);

                    // 弧形
                    LatLng ll1 = new LatLng(epsLocation.getLatitude() + 0.02, epsLocation.getLongitude() + 0.02);
                    LatLng ll2 = new LatLng(epsLocation.getLatitude() + 0.03, epsLocation.getLongitude() + 0.03);
                    LatLng ll3 = new LatLng(epsLocation.getLatitude() + 0.04, epsLocation.getLongitude() + 0.01);
                    OverlayOptions arc = new ArcOptions().points(ll1, ll2, ll3).color(Color.RED).width(10);
                    mBaiduMap.addOverlay(arc);

                    // 园、环状
                    OverlayOptions circle = new CircleOptions()
                            .center(new LatLng(epsLocation.getLatitude(), epsLocation.getLongitude())).radius(5 * 1000)
                            .stroke(new Stroke(1, Color.BLUE)).fillColor(Color.argb(0x11, 0x00, 0x00, 0xaa));
                    mBaiduMap.addOverlay(circle);

                    // 点、小圆点
                    OverlayOptions dot = new DotOptions()
                            .center(new LatLng(epsLocation.getLatitude() + 0.02, epsLocation.getLongitude() + 0.01))
                            .color(Color.RED).radius(10);
                    mBaiduMap.addOverlay(dot);

                    // ground
                    OverlayOptions ground = new GroundOverlayOptions()
                            .position(new LatLng(epsLocation.getLatitude() - 0.01, epsLocation.getLongitude() - 0.01))
                            .image(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)).dimensions(1000);
                    mBaiduMap.addOverlay(ground);

                    // PolygonOptions 多边形

                    // PolygonOptions 折线

                    OverlayOptions text = new TextOptions()
                            .position(new LatLng(epsLocation.getLatitude() - 0.01, epsLocation.getLongitude() + 0.01))
                            .text("text").bgColor(Color.argb(0x44, 0xcc, 0xcc, 0xcc)).fontColor(Color.BLACK)
                            .fontSize(17);
                    mBaiduMap.addOverlay(text);
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        ToastUtils.showToast("onMarkerClick");
        return true;
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        super.onDestroy();
    }
}
