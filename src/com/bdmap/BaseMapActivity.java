package com.bdmap;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.utils.DimensionUtls;

/**
 * 地图基础Activity
 * @author poet
 *
 */
public abstract class BaseMapActivity extends BaseActivity implements OnMapLoadedCallback, OnMapStatusChangeListener,
        OnMapClickListener {

    protected RelativeLayout mMapViewContainer;
    protected MapView mMapView;
    protected BaiduMap mBaiduMap;

    protected boolean mIsInfoWindowShowing;
    protected boolean mIsDestroy;

    RoomToRunnable mRoomToRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        View topView = onCreateTopView();
        if (topView != null) {
            root.addView(topView);
        }
        mMapViewContainer = new RelativeLayout(this);
        mMapView = new MapView(this);
        mMapView.removeViewAt(2);
        mMapViewContainer.addView(mMapView);
        root.addView(mMapViewContainer);
        setContentView(root);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.setOnMapStatusChangeListener(this);
        mBaiduMap.setOnMapClickListener(this);

        LatLng ll = onGetInitLoc();
        if (ll != null) {
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }

        customZoomControls();
    }

    protected View onCreateTopView() {
        return null;
    }

    protected LatLng onGetInitLoc() {
        return null;
    }

    void customZoomControls() {
        int size = DimensionUtls.getPixelFromDpInt(40);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ImageView iv_plus = new ImageView(this);
        iv_plus.setScaleType(ScaleType.FIT_XY);
        iv_plus.setImageResource(R.drawable.icon_map_zoom_plus);
        ll.addView(iv_plus, new LinearLayout.LayoutParams(size, size));
        ImageView iv_minus = new ImageView(this);
        iv_minus.setImageResource(R.drawable.icon_map_zoom_minus);
        iv_minus.setScaleType(ScaleType.FIT_XY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.topMargin = DimensionUtls.getPixelFromDpInt(10);
        ll.addView(iv_minus, params);

        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(-2, -2, Gravity.RIGHT | Gravity.BOTTOM);
        fl.rightMargin = fl.bottomMargin = DimensionUtls.getPixelFromDpInt(10);
        addContentView(ll, fl);

        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float zoom = mBaiduMap.getMapStatus().zoom;
                if (zoom < mBaiduMap.getMaxZoomLevel()) {
                    zoom++;
                    MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(zoom);
                    mBaiduMap.animateMapStatus(status);
                }
            }
        });
        iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float zoom = mBaiduMap.getMapStatus().zoom;
                if (zoom > mBaiduMap.getMinZoomLevel()) {
                    zoom--;
                    MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(zoom);
                    mBaiduMap.animateMapStatus(status);
                }
            }
        });
    }

    protected void initLoc(double latitude, double longitude, final float zoomTo) {
        if (isFinishing()) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder().direction(100).latitude(latitude).longitude(longitude)
                .build();
        mBaiduMap.setMyLocationData(locData);

        moveTo(latitude, longitude, zoomTo);
    }

    protected void moveTo(double latitude, double longitude, final float zoomTo) {
        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);

        if (mRoomToRunnable == null) {
            mRoomToRunnable = new RoomToRunnable(zoomTo);
        } else {
            mRoomToRunnable.setZoomTo(zoomTo);
        }
        mMapView.postDelayed(mRoomToRunnable, 400);
    }

    protected void showInfoWindow(InfoWindow infoWindow) {
        mBaiduMap.showInfoWindow(infoWindow);
        mIsInfoWindowShowing = true;
    }

    protected void hideInfoWindow() {
        mBaiduMap.hideInfoWindow();
        mIsInfoWindowShowing = false;
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onMapStatusChangeStart(MapStatus arg0) {
    }

    @Override
    public void onMapStatusChange(MapStatus arg0) {
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus arg0) {
    }

    @Override
    public void onMapClick(LatLng arg0) {
        hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi arg0) {
        return false;
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
    protected void onStop() {
        mMapView.removeCallbacks(mRoomToRunnable);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mIsInfoWindowShowing) {
            hideInfoWindow();
            return;
        }
        super.onBackPressed();
    }

    class RoomToRunnable implements Runnable {
        float zoomTo;

        RoomToRunnable(float zoomTo) {
            this.zoomTo = zoomTo;
        }

        RoomToRunnable setZoomTo(float zoomTo) {
            this.zoomTo = zoomTo;
            return this;
        }

        @Override
        public void run() {
            MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(zoomTo);
            mBaiduMap.animateMapStatus(status);
        }
    }
}
