package com.epeisong.ui.activity;

import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
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
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsUpdate;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics.Builder;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 编辑地址Activity
 * @author Jack
 *
 */
public class EditOtherAddressActivity extends BaseActivity implements OnClickListener {

    public static final String EXTRA_ORIGINAL_REGION_CODE = "original_region_code";
    public static final String EXTRA_ORIGINAL_REGION_NAME = "original_region_name";
    public static final String EXTRA_ORIGINAL_ADDRESS = "original address";

    private TextView mRegionTv;
    private EditText mAddressEt;
    private RegionResult mRegionResult;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private User mUser;
    // private int serviceupdate;
    private boolean addressBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        String address = getIntent().getStringExtra(EXTRA_ORIGINAL_ADDRESS);
        String regionName = getIntent().getStringExtra(EXTRA_ORIGINAL_REGION_NAME);
        if (TextUtils.isEmpty(address))
            addressBoolean = false;
        else {
            addressBoolean = true;
        }

        // addressBoolean = false;
        if (addressBoolean) {
            mRegionTv = (TextView) findViewById(R.id.tv_region);
            // mRegionTv.setOnClickListener(this);
            mRegionTv.setText(regionName);

            mAddressEt = (EditText) findViewById(R.id.et_address);
            mAddressEt.setText(address);
            mAddressEt.setEnabled(false);
            findViewById(R.id.btn_ok).setVisibility(View.GONE);

        } else {
            mRegionTv = (TextView) findViewById(R.id.tv_region);
            mRegionTv.setOnClickListener(this);
            mAddressEt = (EditText) findViewById(R.id.et_address);
            findViewById(R.id.btn_ok).setOnClickListener(this);
            mRegionTv.setText(regionName);

            mAddressEt.setText(address);

        }
        mUser = (User) getIntent().getSerializableExtra("user");
        // serviceupdate = getIntent().getIntExtra("service_update", 0);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mapViewContainer);
        BaiduMapOptions mapOptions = new BaiduMapOptions();
        mapOptions.zoomControlsEnabled(false);
        mapOptions.zoomGesturesEnabled(false);
        mapOptions.scrollGesturesEnabled(false);
        mapOptions.scaleControlEnabled(false);
        mMapView = new MapView(this, mapOptions);
        frameLayout.addView(mMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        findViewById(R.id.btn_change_loc).setOnClickListener(this);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "更改地址", null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        String addressSave = mUser.getAddress();
        if (!addressBoolean && !TextUtils.isEmpty(addressSave))
            intent.putExtra("address", addressSave);
        // intent.putExtra("wallet", mWallet);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        resetLoc();
        super.onResume();
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

    private void resetLoc() {
        double longitude = mUser.getUserRole().getCurrent_longitude();
        double latitude = mUser.getUserRole().getCurrent_latitude();

        setMap(mBaiduMap, longitude, latitude);
    }

    private void setMap(BaiduMap map, double longitude, double latitude) {
        MyLocationData locData = new MyLocationData.Builder().direction(100).latitude(latitude).longitude(longitude)
                .build();
        mBaiduMap.setMyLocationData(locData);

        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        map.animateMapStatus(u);

        MapStatusUpdate zoomTo = MapStatusUpdateFactory.zoomTo(16);
        map.animateMapStatus(zoomTo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_region:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, 100);
            break;
        case R.id.btn_ok:
            final String address = mAddressEt.getText().toString();
            if (!addressBoolean)
                updateAddress(address);
            break;
        case R.id.btn_change_loc:
            reqquestLocation();
            break;
        default:
            break;
        }
    }

    void reqquestLocation() {
        showPendingDialog("获取地理信息...");
        EpsLocationRequestor requestor = new EpsLocationRequestor();
        requestor.requestEpsLocation(new OnEpsLocationListener() {
            @Override
            public void onEpsLocation(final EpsLocation epsLocation) {
                dismissPendingDialog();
                if (epsLocation == null || TextUtils.isEmpty(epsLocation.getCityName())) {
                    ToastUtils.showToast("获取地理信息失败");
                    return;
                }
                BaiduMapOptions mapOptions = new BaiduMapOptions();
                mapOptions.zoomControlsEnabled(false);
                mapOptions.zoomGesturesEnabled(false);
                mapOptions.scrollGesturesEnabled(false);
                mapOptions.scaleControlEnabled(false);
                MapView view = new MapView(EditOtherAddressActivity.this, mapOptions);
                BaiduMap map = view.getMap();
                map.setMyLocationEnabled(true);
                setMap(map, epsLocation.getLongitude(), epsLocation.getLatitude());
                AlertDialog.Builder b = new AlertDialog.Builder(EditOtherAddressActivity.this);
                FrameLayout frameLayout = new FrameLayout(getApplicationContext());
                frameLayout.addView(view, new FrameLayout.LayoutParams(-1, DimensionUtls.getPixelFromDpInt(200)));
                view.onResume();
                b.setView(frameLayout);
                b.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateLoc(epsLocation);
                    }
                });
                AlertDialog dialog = b.create();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.show();
            }
        });
    }

    private void updateLoc(final EpsLocation loc) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetLogisticsUpdate net = new NetLogisticsUpdate() {
                    @Override
                    protected boolean onSetRequest(LogisticsReq.Builder req) {
                        req.setIsUpdateOthersInfo(true);
                        req.setLogisticsId(Integer.valueOf(mUser.getId()));
                        return false;
                    }

                    @Override
                    protected boolean onSetRequestParams(Builder logi) {
                        logi.setCurrentLongitude(loc.getLongitude());
                        logi.setCurrentLatitude(loc.getLatitude());
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        mUser.getUserRole().setCurrent_longitude(loc.getLongitude());
                        mUser.getUserRole().setCurrent_latitude(loc.getLatitude());

                        // if(!addressBoolean)
                        // UserDao.getInstance().replace(mUser);
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    if (!addressBoolean) {
                        mRegionResult = loc.convertToResult();
                        String address = loc.getAddressName();
                        mRegionTv.setText(mRegionResult.getGeneralName());
                        mAddressEt.setText(address);
                    }

                    ToastUtils.showToast("修改成功");
                    resetLoc();
                }
            }
        };
        task.execute();
    }

    private void updateAddress(final String address) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetLogisticsUpdate net = new NetLogisticsUpdate() {
                    @Override
                    protected boolean onSetRequest(LogisticsReq.Builder req) {
                        req.setIsUpdateOthersInfo(true);
                        req.setLogisticsId(Integer.valueOf(mUser.getId()));
                        req.setIsRealTime(Properties.IS_REALTIME_STATIC);
                        return false;
                    }

                    @Override
                    protected boolean onSetRequestParams(Builder logi) {
                        if (!TextUtils.isEmpty(address)) {
                            logi.setAddress(address);
                        }
                        if (mRegionResult != null) {
                            logi.setRegionCode(mRegionResult.getFullCode());
                            logi.setRegionName(mRegionResult.getGeneralName());
                        }
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        if (mRegionResult != null) {
                            mUser.setRegion(mRegionResult.getGeneralName());
                            mUser.setRegion_code(mRegionResult.getFullCode());
                        }
                        mUser.setAddress(address);
                        // UserDao.getInstance().replace(mUser);
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    ToastUtils.showToast("修改成功");
                }
            }
        };
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            Serializable extra = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (extra != null && extra instanceof RegionResult) {
                mRegionResult = (RegionResult) extra;
                mRegionTv.setText(mRegionResult.getGeneralName());
            }
        }
    }
}
