package com.epeisong.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationHolder;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldViewAdapter;
import com.epeisong.base.view.PwdInputView.SimpleTextWatcher;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetRequestor;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.ui.view.ChooseLineSmallLayout;
import com.epeisong.ui.view.ChooseLineSmallLayout.ChooseLineResult;
import com.epeisong.ui.view.ChooseLineSmallLayout.OnChooseLineSmallListener;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout.LogisticsType;
import com.epeisong.ui.view.ChooseLogisticsTypeLayout.OnChooseLogisticsTypeListener;
import com.epeisong.ui.view.ChooseTabLayout;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ShapeUtils.ShapeParams;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.ViewUtils;
import com.epeisong.utils.android.AsyncTask;
import com.google.protobuf.GeneratedMessage;

public class NearByLogisticsDataHolder implements Serializable {

    private static final long serialVersionUID = 1225985900972640058L;

    public static final int POST_DATA_TYPE_LOCATION = 1;
    public static final int POST_DATA_TYPE_USER_LIST = 2;
    public static final int POST_DATA_REVERSE_GEO_ERROR = 3;
    public static final int POST_DATA_REVERSE_GEO_SUCCESS = 4;
    public static final int POST_DATA_POI_SUCCESS = 5;
    public static final int POST_DATA_CHOOSED_LOGISTICS_TYPE = 6; // 选择了角色类型
    public static final int POST_DATA_CHOOSED_LOGISTICS_TYPE_NONE = 7; // 未选择角色类型，点击空白处，选择界面消失
    public static final int POST_DATA_CHOOSED_LINE = 8; // 选择了线路，不限或具体线路
    public static final int POST_DATA_CHOOSED_LINE_NONE = 9; // 未选择线路，点击空白处

    public EpsLocation anchorLoc; // 定位点（我的位置）
    public EpsLocation searchLoc; // 搜索点（地图选点）

    public LogisticsType logisticsType;
    public ChooseLineResult chooseLineResult;

    public float zoomTo = 15;
    public double outerRadius; // 搜索外半径
    public double innerRadius; // 搜索内半径
    public ArrayList<User> userList; // 搜索结果（由近到远排序）
    public Map<User, Double> distanceAnchorMap;
    public double reSerchDistance; // 搜索点偏移中心点多少，可以重新搜索

    public static String dataTitleText(NearByLogisticsDataHolder holder) {
        if (holder.anchorLoc != null) {
            return holder.anchorLoc.getAddressName();
        }
        if (holder.searchLoc == null) {
            return null;
        }
        String title = holder.searchLoc.getAddressName();
        if (holder.anchorLoc != null && holder.anchorLoc.getCityCode() != holder.searchLoc.getCityCode()) {
            title = holder.searchLoc.getCityName() + title;
        }
        return title;
    }

    public static void dataRequestLocation(final XBaseActivity a, final NearByLogisticsDataHolder dataHolder) {
        EpsLocationRequestor requestor = new EpsLocationRequestor();
        requestor.requestEpsLocation(new OnEpsLocationListener() {
            @Override
            public void onEpsLocation(EpsLocation epsLocation) {
                a.dismissPendingDialog();
                dataHolder.anchorLoc = epsLocation;
                if (epsLocation != null && !TextUtils.isEmpty(epsLocation.getCityName())) {
                    EpsLocationHolder.setEpsLocation(epsLocation);
                }
                a.onPostData(POST_DATA_TYPE_LOCATION);
            }
        });
        a.showPendingDialog("定位中...");
    }

    public static void dataRequestData(final XBaseActivity a, final NearByLogisticsDataHolder dataHolder) {
        a.showPendingDialog(null);
        if (dataHolder.distanceAnchorMap == null) {
            dataHolder.distanceAnchorMap = new HashMap<User, Double>();
        } else {
            dataHolder.distanceAnchorMap.clear();
        }
        dataHolder.reSerchDistance = 1000;
        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {
                NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> net = new NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder>() {
                    @Override
                    protected String getResult(Builder resp) {
                        return resp.getResult();
                    }

                    @Override
                    protected String getDesc(Builder resp) {
                        return resp.getDesc();
                    }

                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.LIST_LOGISTICS_AROUND_A_CENTER_REQ;
                    }

                    @Override
                    protected GeneratedMessage.Builder<LogisticsReq.Builder> getRequest() {
                        LogisticsReq.Builder req = LogisticsReq.newBuilder();
                        if (dataHolder.logisticsType != null) {
                            if (dataHolder.logisticsType.goods_type > 0) {
                                req.setGoodsType(dataHolder.logisticsType.goods_type);
                            } else if (dataHolder.logisticsType.role_type != 0) {
                                req.setLogisticTypeCode(dataHolder.logisticsType.role_type);
                            }
                        }
                        if (dataHolder.chooseLineResult != null && dataHolder.chooseLineResult.start != null
                                && dataHolder.chooseLineResult.end != null) {
                            req.setRouteCodeA(dataHolder.chooseLineResult.start.getFullCode());
                            req.setRouteCodeB(dataHolder.chooseLineResult.end.getFullCode());
                        }
                        req.setLongitude(dataHolder.searchLoc.getLongitude());
                        req.setLatitude(dataHolder.searchLoc.getLatitude());
                        req.setMinRadiusInMeters(0d);
                        return req;
                    }
                };
                try {
                    Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        dataHolder.innerRadius = resp.getInnerRadiusInMeters();
                        dataHolder.outerRadius = resp.getOuterRadiusInMeters();
                        if (dataHolder.outerRadius <= 0) {
                            dataHolder.outerRadius = 20 * 1000;
                        }
                        List<User> list = UserParser.parse(resp);
                        if (list != null && list.size() > 0) {
                            for (User user : list) {
                                if (user.getIs_hide() == Properties.LOGISTIC_NOT_HIDE
                                        && user.getUserRole().getCurrent_latitude() > 0) {
                                    double distance2Anchor = DistanceUtil.getDistance(
                                            new LatLng(user.getUserRole().getCurrent_latitude(), user.getUserRole()
                                                    .getCurrent_longitude()),
                                            new LatLng(dataHolder.anchorLoc.getLatitude(), dataHolder.anchorLoc
                                                    .getLongitude()));
                                    dataHolder.distanceAnchorMap.put(user, distance2Anchor);

                                    double distance2Search = DistanceUtil.getDistance(
                                            new LatLng(user.getUserRole().getCurrent_latitude(), user.getUserRole()
                                                    .getCurrent_longitude()),
                                            new LatLng(dataHolder.searchLoc.getLatitude(), dataHolder.searchLoc
                                                    .getLongitude()));
                                    if (distance2Search > dataHolder.reSerchDistance) {
                                        dataHolder.reSerchDistance = distance2Search;
                                    }
                                }
                            }
                            Collections.sort(list, new Comparator<User>() {
                                @Override
                                public int compare(User lhs, User rhs) {
                                    double d = dataHolder.distanceAnchorMap.get(lhs)
                                            - dataHolder.distanceAnchorMap.get(rhs);
                                    if (d == 0) {
                                        return 0;
                                    }
                                    return d > 0 ? 1 : -1;
                                }
                            });
                        }
                        return list;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> result) {
                a.dismissPendingDialog();
                if (dataHolder.userList != null) {
                    dataHolder.userList.clear();
                }
                dataHolder.userList = (ArrayList<User>) result;
                a.onPostData(POST_DATA_TYPE_USER_LIST);
            }
        };
        task.execute();
    }

    public static void dataReverseGeoCode(final XBaseActivity a, final NearByLogisticsDataHolder holder, final LatLng ll) {
        a.showPendingDialog(null);
        GeoCoder coder = GeoCoder.newInstance();
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseResult) {
                a.dismissPendingDialog();
                if (reverseResult == null || reverseResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    a.onPostData(POST_DATA_REVERSE_GEO_ERROR);
                } else {
                    holder.searchLoc.setAddressName(reverseResult.getAddress());
                    holder.searchLoc.setLatitude(ll.latitude);
                    holder.searchLoc.setLongitude(ll.longitude);
                    a.onPostData(POST_DATA_REVERSE_GEO_SUCCESS);
                }
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {
            }
        });
        coder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
    }

    public static boolean viewDispatchTouchEvent(XBaseActivity a, MotionEvent ev, EditText et, ListView lv) {
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        return ViewUtils.isPointOnView(a, et, x, y) || ViewUtils.isPointOnView(a, lv, x, y);
    }

    @SuppressWarnings("deprecation")
    public static PoiTitleViewHolder viewCustomPoiTitle(final BaseActivity a, final NearByLogisticsDataHolder holder) {
        final RelativeLayout titleContainer = a.getTitleContainer();
        titleContainer.getLayoutParams().height = DimensionUtls.getPixelFromDpInt(35);
        titleContainer.setBackgroundDrawable(ShapeUtils.getShape(new ShapeParams()
                .setBgColor(Color.argb(0x11, 0xff, 0xff, 0xff)).setCorner(DimensionUtls.getPixelFromDp(5))
                .setStrokeWidth(0)));
        final EditText et = new EditText(a);
        et.setBackgroundDrawable(null);
        et.setTextColor(Color.WHITE);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        et.setVisibility(View.GONE);
        titleContainer.addView(et, -1, -1);
        final TextView title = (TextView) titleContainer.getChildAt(0);
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setVisibility(View.GONE);
                et.setVisibility(View.VISIBLE);
                et.requestFocus();
                String key = et.getText().toString();
                if (TextUtils.isEmpty(key)) {
                    SystemUtils.showInputMethod(et);
                } else {
                    et.setText(key);
                }
            }
        });
        final HoldViewAdapter<PoiInfo> adapter = new HoldViewAdapter<PoiInfo>() {
            @Override
            protected ViewHolder<PoiInfo> onCreateViewHolder() {
                return new ViewHolder<PoiInfo>() {
                    TextView tv_name;
                    TextView tv_address;

                    @Override
                    public View createView(Context context) {
                        tv_name = new TextView(context);
                        tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                        tv_name.setTextColor(Color.BLACK);
                        tv_address = new TextView(context);
                        tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        tv_address.setTextColor(Color.GRAY);
                        LinearLayout ll = new LinearLayout(context);
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.addView(tv_name);
                        ll.addView(tv_address);
                        int p = DimensionUtls.getPixelFromDpInt(10);
                        ll.setPadding(p, p / 2, p, p / 2);
                        return ll;
                    }

                    @Override
                    public void fillData(PoiInfo t) {
                        tv_name.setText(t.name);
                        tv_address.setText(t.address);
                    }
                };
            }
        };
        final ListView lv = new ListView(a);
        lv.setBackgroundResource(R.drawable.common_bg_rect_gray);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo item = adapter.getItem(position);
                holder.searchLoc.setAddressName(item.address);
                holder.searchLoc.setLatitude(item.location.latitude);
                holder.searchLoc.setLongitude(item.location.longitude);
                a.onPostData(POST_DATA_POI_SUCCESS);
                et.setText("");
            }
        });
        lv.setCacheColorHint(Color.TRANSPARENT);

        final PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiDetailResult(PoiDetailResult arg0) {
            }

            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    ToastUtils.showToast("未找到结果");
                    adapter.clear();
                    return;
                }
                if (lv.getParent() == null) {
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(titleContainer.getWidth(), -2);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    params.topMargin = DimensionUtls.getPixelFromDpInt(46);
                    a.addContentView(lv, params);
                } else if (lv.getVisibility() != View.VISIBLE) {
                    lv.setVisibility(View.VISIBLE);
                }
                List<PoiInfo> allPoi = result.getAllPoi();
                adapter.replaceAll(allPoi);
            }
        });
        et.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString();
                if (TextUtils.isEmpty(key)) {
                    adapter.clear();
                } else {
                    poiSearch.searchInCity(new PoiCitySearchOption().city(holder.anchorLoc.getCityName()).keyword(key));
                }
            }
        });
        PoiTitleViewHolder poiTitle = new PoiTitleViewHolder();
        poiTitle.et = et;
        poiTitle.lv = lv;
        return poiTitle;
    }

    public static class PoiTitleViewHolder {
        public EditText et;
        public ListView lv;
    }

    public static ChooseTabViewHolder viewCreateChooseTab(final XBaseActivity a,
            final NearByLogisticsDataHolder dataHolder) {

        final ChooseTabLayout chooseTabLayout = new ChooseTabLayout(a);
        chooseTabLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, DimensionUtls.getPixelFromDpInt(45)));
        chooseTabLayout.setBackgroundColor(Color.WHITE);
        final ChooseLogisticsTypeLayout chooseLogisticsTypeLayout = new ChooseLogisticsTypeLayout(a)
                .setChoosedType(dataHolder.logisticsType);
        final ChooseLineSmallLayout chooseLineSmallLayout = new ChooseLineSmallLayout(a,
                EpsApplication.getScreenWidth() / 2).setChoosedLine(dataHolder.chooseLineResult);

        chooseLogisticsTypeLayout.setVisibility(View.GONE);
        chooseLogisticsTypeLayout.setListener(new OnChooseLogisticsTypeListener() {
            @Override
            public void onChoosedLogisticsType(LogisticsType type) {
                chooseTabLayout.cancelAll();
                chooseLogisticsTypeLayout.setVisibility(View.GONE);
                if (type != null) {
                    dataHolder.logisticsType = type;
                    chooseTabLayout.setTabText(0, dataHolder.logisticsType.name);
                    if (type.hasLine()) {
                        chooseTabLayout.setTabEnable(1, true);
                    } else {
                        chooseTabLayout.setTabEnable(1, false);
                        chooseTabLayout.setTabText(1, "筛选");
                        dataHolder.chooseLineResult = null;

                    }
                    a.onPostData(POST_DATA_CHOOSED_LOGISTICS_TYPE);
                } else {
                    a.onPostData(POST_DATA_CHOOSED_LOGISTICS_TYPE_NONE);
                }
            }
        });

        chooseLineSmallLayout.setVisibility(View.GONE);
        chooseLineSmallLayout.setListener(new OnChooseLineSmallListener() {
            @Override
            public void onChoosedLine(ChooseLineResult result) {
                chooseTabLayout.cancelAll();
                chooseLineSmallLayout.setVisibility(View.GONE);
                if (result != null) {
                    dataHolder.chooseLineResult = result;
                    if (result.start == null || result.end == null) {
                        chooseTabLayout.setTabText(1, "线路不限");
                    } else {
                        chooseTabLayout.setTabText(1,
                                result.start.getShortNameFromDistrict() + "-" + result.end.getShortNameFromDistrict());
                    }
                    a.onPostData(POST_DATA_CHOOSED_LINE);
                } else {
                    a.onPostData(POST_DATA_CHOOSED_LINE_NONE);
                }
            }
        });

        String tab = "全部";
        if (dataHolder.logisticsType != null) {
            tab = dataHolder.logisticsType.name;
        }
        chooseTabLayout.addTab(tab, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseLogisticsTypeLayout.getVisibility() == View.VISIBLE) {
                    chooseLogisticsTypeLayout.setVisibility(View.GONE);
                    chooseTabLayout.cancelAll();
                } else {
                    chooseLogisticsTypeLayout.setVisibility(View.VISIBLE);
                    chooseLineSmallLayout.setVisibility(View.GONE);
                    chooseTabLayout.setSelected(0);
                }
            }
        });
        String tab2 = "筛选";
        if (dataHolder.chooseLineResult != null) {
            ChooseLineResult result = dataHolder.chooseLineResult;
            if (result.start != null && result.end != null) {
                tab2 = result.start.getShortNameFromDistrict() + "-" + result.end.getShortNameFromDistrict();
            } else {
                tab2 = "线路不限";
            }
        }
        chooseTabLayout.addTab(tab2, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseLineSmallLayout.getVisibility() == View.VISIBLE) {
                    chooseLineSmallLayout.setVisibility(View.GONE);
                    chooseTabLayout.cancelAll();
                } else {
                    chooseLineSmallLayout.setVisibility(View.VISIBLE);
                    chooseLogisticsTypeLayout.setVisibility(View.GONE);
                    chooseTabLayout.setSelected(1);
                }
            }
        });
        boolean enable = dataHolder.logisticsType == null ? false : dataHolder.logisticsType.hasLine();
        chooseTabLayout.setTabEnable(1, enable);

        ChooseTabViewHolder holder = new ChooseTabViewHolder();
        holder.chooseTabLayout = chooseTabLayout;
        holder.chooseLogisticsTypeLayout = chooseLogisticsTypeLayout;
        holder.chooseLineSmallLayout = chooseLineSmallLayout;
        return holder;
    }

    public static class ChooseTabViewHolder {
        public ChooseTabLayout chooseTabLayout;
        public ChooseLogisticsTypeLayout chooseLogisticsTypeLayout;
        public ChooseLineSmallLayout chooseLineSmallLayout;
    }
}
