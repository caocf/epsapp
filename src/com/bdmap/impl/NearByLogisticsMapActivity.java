package com.bdmap.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bdmap.BaseMapActivity;
import com.bdmap.MapUtils;
import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationHolder;
import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.view.CommonListDialog;
import com.epeisong.base.view.CommonListDialog.CommonViewHolder;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.NearByLogisticsDataHolder;
import com.epeisong.model.NearByLogisticsDataHolder.ChooseTabViewHolder;
import com.epeisong.model.NearByLogisticsDataHolder.PoiTitleViewHolder;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.NearByLogisticsListActivity;
import com.epeisong.ui.view.ChooseLineSmallLayout;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout.LogisticsType;
import com.epeisong.ui.view.ChooseTabLayout;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ShapeUtils.ShapeParams;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 附近物流服务
 * @author poet
 *
 */
@SuppressLint("UseSparseArrays")
public class NearByLogisticsMapActivity extends BaseMapActivity implements OnMarkerClickListener {

    public static final String EXTRA_DEFAULT_LOGISTICS_TYPE = "default_logistics_type";
    public static final String EXTRA_DATA_HOLDER = "data_holder";

    Map<Integer, BitmapDescriptor> mBitMapDescriptorMap;
    List<Marker> mMarkerList;

    NearByLogisticsDataHolder mDataHolder;

    EditText mPoiTitleEt;
    ListView mPoiTitleLv;

    ChooseTabLayout mChooseTabLayout;
    ChooseLogisticsTypeLayout mChooseLogisticsTypeLayout;
    ChooseLineSmallLayout mChooseLineSmallLayout;

    ImageView mCenterView;

    Runnable mHidePopRunnable = new Runnable() {
        @Override
        public void run() {
            hideInfoWindow();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDataHolder = (NearByLogisticsDataHolder) getIntent().getSerializableExtra(EXTRA_DATA_HOLDER);
        if (mDataHolder == null) {
            mDataHolder = new NearByLogisticsDataHolder();
        }
        LogisticsType type = (LogisticsType) getIntent().getSerializableExtra(EXTRA_DEFAULT_LOGISTICS_TYPE);
        if (type != null) {
            mDataHolder.logisticsType = type;
        }
        super.onCreate(savedInstanceState);
        mBaiduMap.setOnMarkerClickListener(this);

        mMarkerList = new ArrayList<Marker>();
        mBitMapDescriptorMap = new HashMap<Integer, BitmapDescriptor>();
        mBitMapDescriptorMap.put(-1, BitmapDescriptorFactory.fromResource(R.drawable.icon_map_default));
        // 物流园
        mBitMapDescriptorMap.put(Properties.LOGISTIC_TYPE_LOGISTICS_PARK,
                BitmapDescriptorFactory.fromResource(R.drawable.icon_map_logistics_park));
        // 快递
        mBitMapDescriptorMap.put(Properties.LOGISTIC_TYPE_COURIER,
                BitmapDescriptorFactory.fromResource(R.drawable.icon_map_courier));
        // 整车
        mBitMapDescriptorMap.put(Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE,
                BitmapDescriptorFactory.fromResource(R.drawable.icon_map_entire_vehicle));
        // 驳货
        mBitMapDescriptorMap.put(Properties.LOGISTIC_TYPE_TRANSHIP_GOODS,
                BitmapDescriptorFactory.fromResource(R.drawable.icon_map_tranship_goods));

        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, false,
                BitmapDescriptorFactory.fromResource(R.drawable.home_location)));
        mBaiduMap.setOnMyLocationClickListener(new OnMyLocationClickListener() {
            @Override
            public boolean onMyLocationClick() {
                mBaiduMap.setMyLocationEnabled(false);
                HandlerUtils.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBaiduMap.setMyLocationEnabled(true);
                    }
                }, 800);
                return true;
            }
        });
        mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent ev) {
                int action = ev.getAction();
                if (action == MotionEvent.ACTION_UP) {
                    final LatLng center = mBaiduMap.getMapStatus().target;
                    if (DistanceUtil.getDistance(
                            new LatLng(mDataHolder.searchLoc.getLatitude(), mDataHolder.searchLoc.getLongitude()),
                            center) > mDataHolder.reSerchDistance) {
                        if ("".equals("haha")) {
                            TextView tv = new TextView(getApplicationContext());
                            int p = DimensionUtls.getPixelFromDpInt(10);
                            tv.setPadding(p, p, p, p);
                            tv.setBackgroundColor(Color.argb(0x44, 0x00, 0x00, 0x00));
                            tv.setText("点击搜索");
                            tv.setTextColor(Color.WHITE);
                            tv.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    NearByLogisticsDataHolder.dataReverseGeoCode(NearByLogisticsMapActivity.this,
                                            mDataHolder, center);
                                }
                            });
                            showInfoWindow(new InfoWindow(tv, center, -30));
                        } else {
                            // NearByLogisticsDataHolder.dataReverseGeoCode(NearByLogisticsMapActivity.this,
                            // mDataHolder,
                            // center);
                            mDataHolder.searchLoc.setLatitude(center.latitude);
                            mDataHolder.searchLoc.setLongitude(center.longitude);
                            requestData();
                        }
                    } else {
                        hideInfoWindow();
                    }
                } else if (action == MotionEvent.ACTION_MOVE) {
                    if (mCenterView.getVisibility() != View.VISIBLE)
                        mCenterView.setVisibility(View.VISIBLE);
                }
            }
        });

        addGoBackView();
        addCenterView();

        PoiTitleViewHolder poiTitle = NearByLogisticsDataHolder.viewCustomPoiTitle(this, mDataHolder);
        mPoiTitleEt = poiTitle.et;
        mPoiTitleLv = poiTitle.lv;

        if (EpsApplication.DEBUGGING) {
            mCustomTitle.getHomeActionContainer().setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    } else {
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    }
                    return true;
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    void addGoBackView() {
        ImageView iv = new ImageView(this);
        iv.setBackgroundDrawable(ShapeUtils.getShape(new ShapeParams().setBgColor(Color.WHITE).setCorner(3)
                .setStrokeWidth(0)));
        iv.setScaleType(ScaleType.CENTER_INSIDE);
        iv.setImageResource(R.drawable.icon_map_anchor);
        int w = DimensionUtls.getPixelFromDpInt(35);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, w);
        params.gravity = Gravity.LEFT | Gravity.BOTTOM;
        params.bottomMargin = (int) (w * 2);
        params.leftMargin = w / 3;
        addContentView(iv, params);
        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTo(mDataHolder.anchorLoc.getLatitude(), mDataHolder.anchorLoc.getLongitude(), mDataHolder.zoomTo);
                if (!mDataHolder.searchLoc.equals(mDataHolder.anchorLoc)) {
                    mDataHolder.searchLoc = mDataHolder.anchorLoc.cloneSelf();
                    setTitleByCity();
                    requestData();
                }
                mCenterView.setVisibility(View.GONE);
            }
        });
    }

    void addCenterView() {
        mCenterView = new ImageView(this);
        mCenterView.setImageResource(R.drawable.icon_map_center);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mMapViewContainer.addView(mCenterView, params);
        if (mDataHolder.anchorLoc == null || mDataHolder.searchLoc == null
                || mDataHolder.anchorLoc.equals(mDataHolder.searchLoc)) {
            mCenterView.setVisibility(View.GONE);
        }
    }

    void setTitleByCity() {
        setTitleText(NearByLogisticsDataHolder.dataTitleText(mDataHolder));
    }

    @Override
    protected TitleParams getTitleParams() {
        Action action = new ActionImpl() {
            @Override
            public View getView() {
                ImageView iv = new ImageView(getApplicationContext());
                iv.setImageResource(R.drawable.icon_nearby_list);
                return iv;
            }

            @Override
            public void doAction(View v) {
                Intent intent = new Intent(getApplicationContext(), NearByLogisticsListActivity.class);
                intent.putExtra(EXTRA_DATA_HOLDER, mDataHolder);
                startActivity(intent);
                finish();
            }
        };
        return new TitleParams(getDefaultHomeAction(), "").setAction(action);
    }

    @Override
    protected LatLng onGetInitLoc() {
        EpsLocation loc = EpsLocationHolder.getEpsLocation();
        if (loc != null && loc.getLatitude() > 0) {
            return new LatLng(loc.getLatitude(), loc.getLongitude());
        }
        return null;
    }

    @Override
    protected View onCreateTopView() {
        ChooseTabViewHolder viewHolder = NearByLogisticsDataHolder.viewCreateChooseTab(this, mDataHolder);
        mChooseTabLayout = viewHolder.chooseTabLayout;
        mChooseLogisticsTypeLayout = viewHolder.chooseLogisticsTypeLayout;
        mChooseLineSmallLayout = viewHolder.chooseLineSmallLayout;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
        params.topMargin = DimensionUtls.getPixelFromDpInt(90);
        addContentViewSuper(mChooseLogisticsTypeLayout, params);
        addContentViewSuper(mChooseLineSmallLayout, params);
        return mChooseTabLayout;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (NearByLogisticsDataHolder.viewDispatchTouchEvent(this, ev, mPoiTitleEt, mPoiTitleLv)) {
                return super.dispatchTouchEvent(ev);
            }
            if (cancelPoiTitle()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (mChooseLogisticsTypeLayout.getVisibility() == View.VISIBLE) {
            mChooseLogisticsTypeLayout.setVisibility(View.GONE);
            mChooseTabLayout.cancelAll();
            return;
        }
        if (mChooseLineSmallLayout.getVisibility() == View.VISIBLE) {
            mChooseLineSmallLayout.setVisibility(View.GONE);
            mChooseTabLayout.cancelAll();
            return;
        }
        if (cancelPoiTitle()) {
            return;
        }
        super.onBackPressed();
    }

    boolean cancelPoiTitle() {
        if (mPoiTitleEt != null && mPoiTitleEt.getVisibility() == View.VISIBLE) {
            SystemUtils.hideInputMethod(mPoiTitleEt);
            mPoiTitleEt.setVisibility(View.GONE);
            getTitleContainer().getChildAt(0).setVisibility(View.VISIBLE);
            if (mPoiTitleLv != null && mPoiTitleLv.getVisibility() == View.VISIBLE) {
                mPoiTitleLv.setVisibility(View.GONE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus status) {
        mDataHolder.zoomTo = status.zoom;
    }

    @Override
    public void onMapLoaded() {
        super.onMapLoaded();
        if (mDataHolder.searchLoc == null) {
            NearByLogisticsDataHolder.dataRequestLocation(this, mDataHolder);
        } else {
            setTitleByCity();
            MyLocationData locData = new MyLocationData.Builder().direction(100)
                    .latitude(mDataHolder.anchorLoc.getLatitude()).longitude(mDataHolder.anchorLoc.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
            moveTo(mDataHolder.searchLoc.getLatitude(), mDataHolder.searchLoc.getLongitude(), mDataHolder.zoomTo);

            if (mDataHolder.userList == null) {
                requestData();
            } else {
                onPostData(NearByLogisticsDataHolder.POST_DATA_TYPE_USER_LIST);
            }
        }
    }

    private void requestData() {
        if (mMapView == null) {
            return;
        }
        try {
            mBaiduMap.clear();
            if (mDataHolder.searchLoc == null) {
                return;
            }
            NearByLogisticsDataHolder.dataRequestData(this, mDataHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostData(Serializable seri) {
        if (mIsDestroy) {
            return;
        }
        if (seri != null && seri instanceof Integer) {
            int type = (Integer) seri;
            switch (type) {
            case NearByLogisticsDataHolder.POST_DATA_TYPE_LOCATION:
                mDataHolder.searchLoc = mDataHolder.anchorLoc.cloneSelf();
                if (mDataHolder.anchorLoc == null) {
                    ToastUtils.showToast("定位失败");
                    finish();
                } else {
                    setTitleByCity();
                    initLoc(mDataHolder.anchorLoc.getLatitude(), mDataHolder.anchorLoc.getLongitude(),
                            mDataHolder.zoomTo);

                    requestData();
                }
                break;
            case NearByLogisticsDataHolder.POST_DATA_TYPE_USER_LIST:
                if (mDataHolder.userList != null) {
                    showLogistic(mDataHolder.userList);
                    User user = null;
                    if (mDataHolder.userList.size() > 60) {
                        user = mDataHolder.userList.get(59);
                    } else if (mDataHolder.userList.size() > 0) {
                        user = mDataHolder.userList.get(mDataHolder.userList.size() - 1);
                    }
                    int zoomTo;
                    if (user != null && user.getUserRole().getCurrent_latitude() > 0
                            && user.getUserRole().getCurrent_longitude() > 0) {
                        double distance = DistanceUtil.getDistance(new LatLng(mDataHolder.searchLoc.getLatitude(),
                                mDataHolder.searchLoc.getLongitude()), new LatLng(user.getUserRole()
                                .getCurrent_latitude(), user.getUserRole().getCurrent_longitude()));
                        zoomTo = MapUtils.getZoomTo(distance * 2 / 1.2);
                    } else {
                        zoomTo = MapUtils.getZoomTo(200d);
                    }
                    if (zoomTo < mDataHolder.zoomTo) {
                        mDataHolder.zoomTo = zoomTo;
                    }
                } else {
                    ToastUtils.showToast("请求失败");
                }
                OverlayOptions circle = new CircleOptions()
                        .center(new LatLng(mDataHolder.searchLoc.getLatitude(), mDataHolder.searchLoc.getLongitude()))
                        .radius((int) mDataHolder.outerRadius).stroke(new Stroke(1, Color.BLUE))
                        .fillColor(Color.argb(0x11, 0x00, 0x00, 0xaa));
                mBaiduMap.addOverlay(circle);

                moveTo(mDataHolder.searchLoc.getLatitude(), mDataHolder.searchLoc.getLongitude(), mDataHolder.zoomTo);
                break;
            case NearByLogisticsDataHolder.POST_DATA_REVERSE_GEO_ERROR:
                ToastUtils.showToast("获取地理信息失败");
                break;
            case NearByLogisticsDataHolder.POST_DATA_REVERSE_GEO_SUCCESS:
                // setTitleByCity();
                requestData();
                break;
            case NearByLogisticsDataHolder.POST_DATA_POI_SUCCESS:
                cancelPoiTitle();
                // setTitleByCity();
                requestData();
                break;
            case NearByLogisticsDataHolder.POST_DATA_CHOOSED_LOGISTICS_TYPE:
            case NearByLogisticsDataHolder.POST_DATA_CHOOSED_LINE:
                requestData();
                break;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mChooseLineSmallLayout.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    private void showLogistic(ArrayList<User> list) {
        if (list == null) {
            return;
        }
        if (list.isEmpty()) {
            ToastUtils.showToast("附近没有可见的物流服务");
            return;
        }
        for (User u : list) {
            double longitude = u.getUserRole().getCurrent_longitude();
            double latitude = u.getUserRole().getCurrent_latitude();
            if (longitude > 0 && latitude > 0) {
                LatLng ll = new LatLng(latitude, longitude);
                int z = 9;
                if (u.getUser_type_code() == Properties.LOGISTIC_TYPE_LOGISTICS_PARK) {
                    z = 10;
                }
                OverlayOptions ooA = new MarkerOptions().position(ll).icon(getBitmapDescriptor(u.getUser_type_code()))
                        .zIndex(z).draggable(false);
                Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", u);
                marker.setExtraInfo(bundle);
                mMarkerList.add(marker);
            }
        }
    }

    private BitmapDescriptor getBitmapDescriptor(int userTypeCode) {
        BitmapDescriptor result = mBitMapDescriptorMap.get(userTypeCode);
        if (result == null) {
            result = mBitMapDescriptorMap.get(-1);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.clear();
        if (mBitMapDescriptorMap != null) {
            Set<Entry<Integer, BitmapDescriptor>> set = mBitMapDescriptorMap.entrySet();
            for (Entry<Integer, BitmapDescriptor> entry : set) {
                entry.getValue().recycle();
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // marker.getPosition();
        final int zoomTo = (int) mBaiduMap.getMapStatus().zoom;
        Bundle bundle = marker.getExtraInfo();
        if (bundle != null) {
            Serializable seri = bundle.getSerializable("user");
            if (seri != null) {
                if (seri instanceof User) {
                    final User user = (User) seri;
                    computeDistance(user, zoomTo);
                }
            }
        }
        return true;
    }

    void computeDistance(final User user, final int zoomTo) {
        showPendingDialog("数据获取中...");
        final LatLng ll = new LatLng(user.getUserRole().getCurrent_latitude(), user.getUserRole()
                .getCurrent_longitude());
        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {
                final List<User> temp = new ArrayList<User>();
                temp.add(user);
                double d = DimensionUtls.getDensity() / 0.0254d / MapUtils.getScale(zoomTo);
                for (User u : mDataHolder.userList) {
                    if (!u.getId().equals(user.getId())) {
                        double distance = DistanceUtil.getDistance(ll, new LatLng(
                                u.getUserRole().getCurrent_latitude(), u.getUserRole().getCurrent_longitude()));
                        double px = d * distance;
                        LogUtils.e(null, "px:" + px + "---- zoom:" + zoomTo);
                        if (px < 52) {
                            temp.add(u);
                        }
                    }
                }
                return temp;
            }

            @Override
            protected void onPostExecute(List<User> result) {
                dismissPendingDialog();
                if (result.size() == 1) {
                    ViewHolder holder = new ViewHolder();
                    holder.setResId(R.layout.activity_nearbylogistics_map_single_view);
                    View infoWindowView = holder.onCreateView(getApplicationContext());
                    holder.onFillData(user);
                    infoWindowView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoUserActivity(user);
                            v.removeCallbacks(mHidePopRunnable);
                            hideInfoWindow();
                        }
                    });
                    InfoWindow infoWindow = new InfoWindow(infoWindowView, ll, -45);
                    showInfoWindow(infoWindow);
                    HandlerUtils.remove(mHidePopRunnable);
                    HandlerUtils.postDelayed(mHidePopRunnable, 6 * 1000);
                } else {
                    CommonListDialog<User> dialog = new CommonListDialog<User>(NearByLogisticsMapActivity.this) {
                        @Override
                        protected CommonViewHolder<User> onCreateViewHolder() {
                            return new ViewHolder();
                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            gotoUserActivity(mCommonAdapter.getItem(position));
                        }
                    };
                    dialog.showAndSetData(result);
                }
            }
        };
        task.execute();
    }

    class ViewHolder implements CommonViewHolder<User> {
        ImageView iv_logo;
        TextView tv_name;
        TextView tv_logistic_type_name;
        TextView tv_phone;

        int resId = R.layout.activity_nearbylogistics_map_list_item;

        public void setResId(int resId) {
            this.resId = resId;
        }

        public View onCreateView(Context context) {
            View v = SystemUtils.inflate(resId);
            iv_logo = (ImageView) v.findViewById(R.id.iv_logo);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_logistic_type_name = (TextView) v.findViewById(R.id.tv_logistic_type_name);
            tv_phone = (TextView) v.findViewById(R.id.tv_phone);
            return v;
        }

        public void onFillData(User u) {
            iv_logo.setImageResource(getLogo(u.getUser_type_code()));
            tv_name.setText(u.getShow_name());
            tv_logistic_type_name.setText(u.getUser_type_name());
            String call = u.getContacts_phone();
            if (TextUtils.isEmpty(call)) {
                call = u.getContacts_telephone();
            }
            tv_phone.setText(call);
        }

        int getLogo(int type) {
            switch (type) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:// 整车运输
                return R.drawable.icon_map_logo_driver;
            case Properties.LOGISTIC_TYPE_EXPRESS: // 快递
            case Properties.LOGISTIC_TYPE_COURIER: // 快递员
            case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION: // 同城 配送
            case Properties.LOGISTIC_TYPE_PICK_UP_POINT: // 收发网点
                return R.drawable.icon_map_logo_courier;
            case Properties.LOGISTIC_TYPE_PARKING_LOT: // 停车场、配货市场
                return R.drawable.icon_map_logo_parking;
            case Properties.LOGISTIC_TYPE_VEHICLE_REPAIR: // 汽车修理
                return R.drawable.icon_map_logo_truck_repair;
            case Properties.LOGISTIC_TYPE_GAS_STATION: // 加油站
                return R.drawable.icon_map_logo_attendant;
            default:
                return R.drawable.icon_map_logo_driver;
            }
        }
    }

    void gotoUserActivity(User u) {
        Intent intent = new Intent();
        intent.setClass(NearByLogisticsMapActivity.this, ContactsDetailActivity.class);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, u);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, u.getId());
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, u.getUser_type_code());
        startActivity(intent);
    }
}
