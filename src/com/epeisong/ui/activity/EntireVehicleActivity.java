package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.epeisong.LogisticsProducts;
import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsSearch;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.Dictionary;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.ui.view.Choosable;
import com.epeisong.ui.view.Choosable.Choosion;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.ui.view.ChooseServeRegionLayout;
import com.epeisong.ui.view.ChooseServeRegionLayout.OnChooseServeRegionListener;
import com.epeisong.ui.view.ChooseValidityLayout.OnChooseValidityListener;
import com.epeisong.ui.view.ChooseVehicleLenghtLayout.OnChooseVehicleLenghtListener;
import com.epeisong.ui.view.ChooseVehicleTypeLayout.OnChooseVehicleTypeListener;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 易配送统一搜索界面
 * @author Jack
 *
 */

public class EntireVehicleActivity extends VerticalFilterActivity implements OnChooseVehicleLenghtListener,
        OnChooseVehicleTypeListener, OnChooseLineListener, OnChooseServeRegionListener, OnChooseValidityListener {

    public static final String EXTRA_REGION_RESULT = "region_result";

    private static final int LOAD_SIZE_FIRST = 10;
    public static final int LAOD_SIZE_MORE = 10;

    private ChooseServeRegionLayout mChooseServeRegionLayout;
    private ChooseLineLayout mChooseLineLayout;
    private int mServeRegionCode;
    private int mVehicleLenghtCode;
    private int mVehicleTypeCode;
    private int mStartRegionCode;
    private int mEndRegionCode;
    private int mValidity;
    private int load_type;
    private int Logistic_type;
    private int Product_type;
    private int mInsuranceType;
    private int mEquipmentType;
    private int mDepotType;
    private int mPackType;
    private double weightScore;

    private int RegionCode;

    @Override
    public void onChoosedServeRegion(RegionResult result) {
        int oldcode = mServeRegionCode;
        mServeRegionCode = result.getCode();
        setFilterValue(0, result.getShortNameFromDistrict());
        hideChoosableView(0);
        switch (Logistic_type) {
        case Properties.LOGISTIC_TYPE_PARKING_LOT:
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
        case Properties.LOGISTIC_TYPE_MARKET:
            if (oldcode != mServeRegionCode)
                loadData(LOAD_SIZE_FIRST, "0", 0, true);
            break;
        default:
            if (oldcode != mServeRegionCode)
                loadData(LOAD_SIZE_FIRST, "0", 0, true);
            break;
        }
    }

    @Override
    public void onChoosedServeRegionDefault(Choosion choosion) {
        int oldcode = mServeRegionCode;
        mServeRegionCode = 0;

        setFilterValue(0, choosion.getName());
        hideChoosableView(0);
        switch (Logistic_type) {
        case Properties.LOGISTIC_TYPE_PARKING_LOT:
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
        case Properties.LOGISTIC_TYPE_MARKET:
            if (oldcode != mServeRegionCode)
                loadData(LOAD_SIZE_FIRST, "0", 0, true);
            break;
        default:
            if (oldcode != mServeRegionCode)
                loadData(LOAD_SIZE_FIRST, "0", 0, true);
            break;
        }
    }

    @Override
    public void onChoosedLine(RegionResult start, RegionResult end) {
        int lineid;
        int startnum = mStartRegionCode, endnum = mEndRegionCode;
        switch (Logistic_type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            lineid = 3;
            break;
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            lineid = 1;
            break;
        default:
            lineid = 1;
        }
        if (start != null && end != null) {
            setFilterValue(lineid, start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
            mStartRegionCode = start.getCode();
            mEndRegionCode = end.getCode();
        } else {
            setFilterValue(lineid, mChooseLineLayout.getDefaultChoosion().getName());
            mStartRegionCode = -1;
            mEndRegionCode = -1;
        }
        hideChoosableView(lineid);
        if (startnum == mStartRegionCode && endnum == mEndRegionCode)
            ;
        else
            loadData(LOAD_SIZE_FIRST, "0", 0, true);
    }

    public void onChoosedCargo(Choosion choosion) {
        /*
         * setFilterValue(3, choosion.getName()); mCargo =
         * String.valueOf(choosion.getCode()); hideChoosableView(3);
         */
    }

    @Override
    public void onChoosedVehicleLenght(Choosion choosion) {
        /*
         * String name = choosion.getName(); setFilterValue(1, name); if
         * (name.equals("车长不限")) { mVehicleLenghtCode = -1; } else if
         * (name.equals("13.5米")) { mVehicleLenghtCode = 512; } else {
         * mVehicleLenghtCode = 2048; // mVehicleLength = Double //
         * .parseDouble(name.substring(0, name.length() - 1)); }
         * mVehicleLengthName = name; hideChoosableView(1);
         */
    }

    @Override
    public void onChoosedVehicleType(Choosion choosion) {
        // setFilterValue(1, choosion.getName());
        // mVehicleType = String.valueOf(choosion.getCode());
        // hideChoosableView(1);
    }

    public void onChoosedValidity(Choosion choosion) {
        int numnum = mValidity;
        setFilterValue(2, choosion.getName());
        mValidity = choosion.getCode();
        hideChoosableView(2);
        if (mValidity != numnum)
            loadData(LOAD_SIZE_FIRST, "0", 0, true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // int iit = mListView.getHeaderViewsCount();
        User mUser = mAdapter.getItem(position - 1);
        // ToastUtils.showToast(mUser.getIs_hide()+"");
        Intent intent = new Intent();
        intent.setClass(this, ContactsDetailActivity.class);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, mUser);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUser.getId());
        intent.putExtra(ContactsDetailActivity.EXTRA_TAGNODIS_STRING, 1);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER_TYPEID, mUser.getUser_type_code());
        intent.putExtra(String.valueOf(R.string.producttypenum), Product_type);
        startActivity(intent);
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        String edge_id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        weightScore = mAdapter.getItem(mAdapter.getCount() - 1).getUserRole().getWeight();
        loadData(LAOD_SIZE_MORE, edge_id, weightScore, false);// remember to add
                                                              // weight;
    }

    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {
        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {
                NetLogisticsSearch net = new NetLogisticsSearch() {
                    @Override
                    protected int getCommandCode() {
                        switch (Product_type) {
                        case LogisticsProducts.PRODUCTS_DANGEROUS:// 8
                        case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:// 32
                        case LogisticsProducts.PRODUCTS_LARGETRANSPORT:// 4
                        case LogisticsProducts.PRODUCTS_REFRIGERATED:// 16
                            return CommandConstants.SEARCH_GOODS_TYPE_OF_LOGISTIC_REQ;
                        }

                        switch (Logistic_type) {
                        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
                            return CommandConstants.SEARCH_ENTIRE_VEHICLE_TRANSHIP_GOODS_REQ;
                        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
                            return CommandConstants.SEARCH_LESS_THAN_TRUCK_LOAD_AND_LINE_REQ;
                            // 快递员
                        case Properties.LOGISTIC_TYPE_COURIER:
                            return CommandConstants.SEARCH_COURIER_REQ;
                            // 同城配送、快递公司、收发网点
                        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
                        case Properties.LOGISTIC_TYPE_EXPRESS:
                            return CommandConstants.SEARCH_PICKUP_POINT_EXPRESS_CITY_DISTRIBUTION_REQ;// SEARCH_COURIER_REQ;
                            // 第三方物流
                        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
                            return CommandConstants.SEARCH_THIRD_PART_LOGISTIC_REQ;
                        case Properties.LOGISTIC_TYPE_INSURANCE:
                            return CommandConstants.SEARCH_INSURANCE_REQ;
                        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
                            return CommandConstants.SEARCH_STOWAGE_INFORMATION_DEPARTMENT_REQ;
                        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
                            return CommandConstants.SEARCH_EQUIPMENT_LEASING_REQ;
                        case Properties.LOGISTIC_TYPE_STORAGE:
                            return CommandConstants.SEARCH_STORAGE_REQ;

                        case Properties.LOGISTIC_TYPE_PACKAGING:
                            return CommandConstants.SEARCH_PACKAGING_REQ;
                        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
                            return CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_REQ;
                        case Properties.LOGISTIC_TYPE_PARKING_LOT:
                            return CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_REQ;
                        case Properties.LOGISTIC_TYPE_MARKET:
                            return CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_REQ;
                        case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
                            return CommandConstants.SEARCH_MOVE_HOUSE_REQ;
                        default:
                            break;
                        }
                        return -1;
                    }

                    @Override
                    protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder req) {

                        req.setLimitCount(size);
                        int id = 0;
                        try {
                            if (edge_id != null) {
                                id = Integer.parseInt(edge_id);
                            }
                        } catch (Exception e) {
                            id = 0;
                        }
                        req.setId(id);
                        // ToastUtils.showToast(String.valueOf(weight));
                        req.setWeightScore(weight);
                        req.setLogisticTypeCode(Logistic_type);// 搜索类型条件，req统一的，可以再分开

                        switch (Product_type) {
                        case LogisticsProducts.PRODUCTS_DANGEROUS:// 8
                        case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:// 32
                        case LogisticsProducts.PRODUCTS_LARGETRANSPORT:// 4
                        case LogisticsProducts.PRODUCTS_REFRIGERATED:// 16
                            req.setServeRegionCode(mServeRegionCode);
                            req.setGoodsType(Product_type);
                            if (mStartRegionCode > 0 && mEndRegionCode > 0) {
                                req.setRouteCodeA(mStartRegionCode);
                                req.setRouteCodeB(mEndRegionCode);
                            }
                            return true;
                        }

                        switch (Logistic_type) {
                        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            if (mVehicleLenghtCode > 0) {
                                req.setVehicleLengthCode(mVehicleLenghtCode);
                            }
                            if (mVehicleTypeCode > 0) {
                                req.setVehicleType(mVehicleTypeCode);
                            }
                            if (mStartRegionCode > 0 && mEndRegionCode > 0) {
                                req.setRouteCodeA(mStartRegionCode);
                                req.setRouteCodeB(mEndRegionCode);
                            }
                            break;
                        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:

                            req.setPeriodOfValidity(mValidity);

                            if (mStartRegionCode > 0 && mEndRegionCode > 0) {
                                req.setRouteCodeA(mStartRegionCode);
                                req.setRouteCodeB(mEndRegionCode);
                            }
                            if (load_type > 0) {
                                req.setLoadType(load_type);
                            }
                            req.setServeRegionCode(mServeRegionCode);
                            break;
                        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
                        case Properties.LOGISTIC_TYPE_PARKING_LOT:
                        case Properties.LOGISTIC_TYPE_MARKET:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            break;
                        case Properties.LOGISTIC_TYPE_COURIER:
                        case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
                        case Properties.LOGISTIC_TYPE_EXPRESS:
                        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:

                        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            if (mStartRegionCode > 0 && mEndRegionCode > 0) {
                                req.setRouteCodeA(mStartRegionCode);
                                req.setRouteCodeB(mEndRegionCode);
                            }
                            break;
                        case Properties.LOGISTIC_TYPE_INSURANCE:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            if (mInsuranceType > 0)
                                req.setInsuranceType(mInsuranceType);
                            break;
                        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            if (mStartRegionCode > 0 && mEndRegionCode > 0) {
                                req.setRouteCodeA(mStartRegionCode);
                                req.setRouteCodeB(mEndRegionCode);
                            }
                            break;
                        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            if (mEquipmentType > 0)
                                req.setEquipmentType(mEquipmentType);
                            break;
                        case Properties.LOGISTIC_TYPE_STORAGE:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            if (mDepotType > 0)
                                req.setEquipmentType(mDepotType);

                            break;
                        case Properties.LOGISTIC_TYPE_PACKAGING:
                            if (mServeRegionCode > 0) {
                                req.setServeRegionCode(mServeRegionCode);
                            }
                            if (mPackType > 0)
                                req.setEquipmentType(mPackType);
                            // if (mStartRegionCode > 0 && mEndRegionCode > 0) {
                            // req.setRouteCodeA(mStartRegionCode);
                            // req.setRouteCodeB(mEndRegionCode);
                            // }
                            break;
                        }
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (resp == null) {
                        return null;
                    }
                    return UserParser.parse(resp);
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> result) {
                if (bFirst) {
                    dismissPendingDialog();
                }
                if (mFilterContainer.getVisibility() != View.VISIBLE) {
                    mFilterContainer.setVisibility(View.VISIBLE);
                }
                if (result != null) {
                    if (result.isEmpty()) {
                        mEndlessAdapter.setHasMore(false);
                        if (bFirst) {
                            ToastUtils.showToast("没有数据");
                            mAdapter.clear();
                        } else {
                            mEndlessAdapter.endLoad(true);
                        }
                    } else {
                        mEndlessAdapter.setHasMore(result.size() >= size);
                        if (bFirst) {
                            mAdapter.replaceAll(result);
                        } else {
                            mAdapter.addAll(result);
                            mEndlessAdapter.endLoad(true);
                        }
                    }
                } else {
                    if (!bFirst) {
                        mEndlessAdapter.endLoad(false);
                    } else
                        mAdapter.clear();
                }
            }
        };
        if (bFirst) {
            showPendingDialog(null);
        }
        task.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent mIntent = getIntent();
        Bundle b = mIntent.getExtras();
        Logistic_type = b.getInt(String.valueOf(R.string.usertypenum));
        Product_type = b.getInt(String.valueOf(R.string.producttypenum));

        b.putSerializable("Logistic_type", Logistic_type);
        super.onCreate(savedInstanceState);

        Serializable extra = mIntent.getSerializableExtra(EXTRA_REGION_RESULT);
        if (extra != null && extra instanceof RegionResult) {
            RegionResult result = (RegionResult) extra;
            mServeRegionCode = result.getCode();
            setFilterValue(0, result.getShortNameFromDistrict());
        }

        mFilterContainer.setVisibility(View.GONE);

        switch (Product_type) {
        case LogisticsProducts.PRODUCTS_DANGEROUS:
        case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:
        case LogisticsProducts.PRODUCTS_LARGETRANSPORT:
        case LogisticsProducts.PRODUCTS_REFRIGERATED:
            hideSearchBtn();
            loadData(LOAD_SIZE_FIRST, "0", 0, true);
            return;
        }

        switch (Logistic_type) {
        case Properties.LOGISTIC_TYPE_PARKING_LOT:
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
        case Properties.LOGISTIC_TYPE_MARKET:
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:

            Region region = (Region) getIntent().getSerializableExtra(NearbyMarketActivity.EXTRA_DEFAULT_REGION);
            if (region != null) {
                RegionCode = region.getFullCode();// b.getInt(EntireVehicleActivity.EXTRA_REGIONCODE);
                mServeRegionCode = RegionCode;
                setFilterValue(0, region.getName());
            }
            hideSearchBtn();

            loadData(LOAD_SIZE_FIRST, "0", 0, true);
            break;
        case Properties.LOGISTIC_TYPE_EXPRESS:
        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
        case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
        case Properties.LOGISTIC_TYPE_COURIER:
            hideSearchBtn();
            loadData(LOAD_SIZE_FIRST, "0", 0, true);
            break;
        default:
            hideSearchBtn();
            loadData(LOAD_SIZE_FIRST, "0", 0, true);
            break;
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        switch (Product_type) {
        case LogisticsProducts.PRODUCTS_DANGEROUS:
            return new TitleParams(getDefaultHomeAction(), "危险品", null).setShowLogo(false);
        case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:
            return new TitleParams(getDefaultHomeAction(), "鲜活易腐", null).setShowLogo(false);
        case LogisticsProducts.PRODUCTS_LARGETRANSPORT:
            return new TitleParams(getDefaultHomeAction(), "大件运输", null).setShowLogo(false);
        case LogisticsProducts.PRODUCTS_REFRIGERATED:
            return new TitleParams(getDefaultHomeAction(), "冷藏品", null).setShowLogo(false);
        }
        switch (Logistic_type) {
        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
            return new TitleParams(getDefaultHomeAction(), "第三方物流").setShowLogo(false);
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            return new TitleParams(getDefaultHomeAction(), "整车运输", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            return new TitleParams(getDefaultHomeAction(), "零担专线", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_EXPRESS:
            return new TitleParams(getDefaultHomeAction(), "快递", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_COURIER:
            return new TitleParams(getDefaultHomeAction(), "快递员", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_INSURANCE:
            return new TitleParams(getDefaultHomeAction(), "保险", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
            return new TitleParams(getDefaultHomeAction(), "配载信息部", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
            return new TitleParams(getDefaultHomeAction(), "设备租赁", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_STORAGE:
            return new TitleParams(getDefaultHomeAction(), "仓储", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_PACKAGING:
            return new TitleParams(getDefaultHomeAction(), "包装", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
            return new TitleParams(getDefaultHomeAction(), "搬家", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
            return new TitleParams(getDefaultHomeAction(), "同城配送", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
            return new TitleParams(getDefaultHomeAction(), "物流园", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_PARKING_LOT:
            return new TitleParams(getDefaultHomeAction(), "停车场", null).setShowLogo(false);
        case Properties.LOGISTIC_TYPE_MARKET:
            return new TitleParams(getDefaultHomeAction(), "配货市场", null).setShowLogo(false);
        default:
            return new TitleParams(getDefaultHomeAction(), "搜索", null).setShowLogo(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (mChooseServeRegionLayout.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
            if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
    }

    @Override
    protected synchronized void onClickSearchBtn() {
        // 搜索按钮
        loadData(LOAD_SIZE_FIRST, "0", 0, true);
    }

    private OnChooseDictionaryListener mChooseTruckLengthListener = new OnChooseDictionaryListener() {

        @Override
        public void onChoosedDictionary(Dictionary dict) {
            setFilterValue(1, dict.getName());
            if (mVehicleLenghtCode != dict.getId()) {
                mVehicleLenghtCode = dict.getId();
                onClickSearchBtn();
            }
        }
    };

    private OnChooseDictionaryListener mChooseTruckTypeListener = new OnChooseDictionaryListener() {

        @Override
        public void onChoosedDictionary(Dictionary dict) {
            setFilterValue(2, dict.getName());
            if (mVehicleTypeCode != dict.getId()) {
                mVehicleTypeCode = dict.getId();
                onClickSearchBtn();
            }
        }
    };

    private OnChooseDictionaryListener mChooseValidityListener = new OnChooseDictionaryListener() {

        @Override
        public void onChoosedDictionary(Dictionary dict) {
            setFilterValue(2, dict.getName());
            if (mValidity != dict.getId()) {
                mValidity = dict.getId();
                onClickSearchBtn();
            }
        }
    };

    private OnChooseDictionaryListener mChooseInsuranceTypeListener = new OnChooseDictionaryListener() {

        @Override
        public void onChoosedDictionary(Dictionary dict) {
            setFilterValue(1, dict.getName());
            if (mInsuranceType != dict.getId()) {
                mInsuranceType = dict.getId();
                onClickSearchBtn();
            }
        }
    };

    private OnChooseDictionaryListener mChooseDeviceTypeListener = new OnChooseDictionaryListener() {

        @Override
        public void onChoosedDictionary(Dictionary dict) {
            setFilterValue(1, dict.getName());
            if (mEquipmentType != dict.getId()) {
                mEquipmentType = dict.getId();
                onClickSearchBtn();
            }
        }
    };
    private OnChooseDictionaryListener mChooseDepotTypeListener = new OnChooseDictionaryListener() {

        @Override
        public void onChoosedDictionary(Dictionary dict) {
            setFilterValue(1, dict.getName());
            if (mDepotType != dict.getId()) {
                mDepotType = dict.getId();
                onClickSearchBtn();
            }
        }
    };
    private OnChooseDictionaryListener mChoosePackTypeListener = new OnChooseDictionaryListener() {

        @Override
        public void onChoosedDictionary(Dictionary dict) {
            setFilterValue(1, dict.getName());
            if (mPackType != dict.getId()) {

                mPackType = dict.getId();
                onClickSearchBtn();
            }
        }
    };

    @Override
    protected Map<String, Choosable> onCreateFilterView() {

        Map<String, Choosable> map = new LinkedHashMap<String, Choosable>();

        switch (Product_type) {
        case LogisticsProducts.PRODUCTS_DANGEROUS:
        case LogisticsProducts.PRODUCTS_FRESHPERISHABLE:
        case LogisticsProducts.PRODUCTS_LARGETRANSPORT:
        case LogisticsProducts.PRODUCTS_REFRIGERATED:
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));

            // map.put("服务区域", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(getApplicationContext()));
            map.put("线路", mChooseLineLayout = new ChooseLineLayout(getApplicationContext()));
            // mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            mChooseLineLayout.setActivity(this);
            mChooseLineLayout.setOnChooseLineListener(this);
            return map;
        }
        switch (Logistic_type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            // map.put("常驻地区", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(getApplicationContext()));
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));
            map.put("车长", new Choosable() {
                @Override
                public String getChooseTitle() {
                    return "选择车长";
                }

                @Override
                public int getChooseDictionaryType() {
                    return Dictionary.TYPE_TRUCK_LENGTH;
                }

                @Override
                public OnChooseDictionaryListener getOnChooseDictionaryListener() {
                    return mChooseTruckLengthListener;
                }

                @Override
                public Choosion getDefaultChoosion() {
                    Choosion choosion = new Choosion(-1, "车长不限");
                    return choosion;
                }

                @Override
                public View getView() {
                    return null;
                }
            });
            map.put("车型", new Choosable() {
                @Override
                public String getChooseTitle() {
                    return "选择车型";
                }

                @Override
                public int getChooseDictionaryType() {
                    return Dictionary.TYPE_TRUCK_TYPE;
                }

                @Override
                public OnChooseDictionaryListener getOnChooseDictionaryListener() {
                    return mChooseTruckTypeListener;
                }

                @Override
                public Choosion getDefaultChoosion() {
                    Choosion choosion = new Choosion(-1, "车型不限");
                    return choosion;
                }

                @Override
                public View getView() {
                    return null;
                }
            });
            map.put("线路", mChooseLineLayout = new ChooseLineLayout(getApplicationContext()));
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            mChooseServeRegionLayout.setActivity(this);
            // mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
            mChooseLineLayout.setActivity(this);
            mChooseLineLayout.setOnChooseLineListener(this);
            mServeRegionCode = mChooseServeRegionLayout.getDefaultChoosion().getCode();
            break;
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));

            // map.put("所在地", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(this));
            map.put("线路", mChooseLineLayout = new ChooseLineLayout(getApplicationContext()));
            // mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
            map.put("时效", new Choosable() {
                @Override
                public String getChooseTitle() {
                    return "选择时效";
                }

                @Override
                public int getChooseDictionaryType() {
                    return Dictionary.TYPE_VALIDITY;
                }

                @Override
                public OnChooseDictionaryListener getOnChooseDictionaryListener() {
                    return mChooseValidityListener;
                }

                @Override
                public Choosion getDefaultChoosion() {
                    Choosion choosion = new Choosion(-1, "时效不限");
                    return choosion;
                }

                @Override
                public View getView() {
                    return null;
                }
            });

            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            mChooseLineLayout.setActivity(this);
            mChooseLineLayout.setOnChooseLineListener(this);
            break;
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
        case Properties.LOGISTIC_TYPE_PARKING_LOT:
        case Properties.LOGISTIC_TYPE_MARKET:
            String startName1 = "地区";// "服务局域";
            if (// Logistic_type == Properties.LOGISTIC_TYPE_EXPRESS ||
            Logistic_type == Properties.LOGISTIC_TYPE_PARKING_LOT || Logistic_type == Properties.LOGISTIC_TYPE_MARKET
                    || Logistic_type == Properties.LOGISTIC_TYPE_LOGISTICS_PARK) {
                startName1 = "选择地区";
            }
            map.put(startName1, mChooseServeRegionLayout = new ChooseServeRegionLayout(this));
            // 2015/1/7
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            break;
        case Properties.LOGISTIC_TYPE_EXPRESS:
        case Properties.LOGISTIC_TYPE_COURIER:
        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
        case Properties.LOGISTIC_TYPE_MOVE_HOUSE:

        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
            String startName = "地区";// "服务局域";
            if (// Logistic_type == Properties.LOGISTIC_TYPE_EXPRESS ||
            Logistic_type == Properties.LOGISTIC_TYPE_PARKING_LOT || Logistic_type == Properties.LOGISTIC_TYPE_MARKET
                    || Logistic_type == Properties.LOGISTIC_TYPE_LOGISTICS_PARK) {
                startName = "选择地区";
            }
            map.put(startName, mChooseServeRegionLayout = new ChooseServeRegionLayout(this));
            // 2015/1/7
            map.put("线路", mChooseLineLayout = new ChooseLineLayout(getApplicationContext()));
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            mChooseLineLayout.setActivity(this);
            mChooseLineLayout.setOnChooseLineListener(this);
            break;
        case Properties.LOGISTIC_TYPE_INSURANCE:
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));

            // map.put("服务区域", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(this));
            map.put("险种", new Choosable() {
                @Override
                public String getChooseTitle() {
                    return "选择险种";
                }

                @Override
                public int getChooseDictionaryType() {
                    return Dictionary.TYPE_INSURANCE_TYPE;
                }

                @Override
                public OnChooseDictionaryListener getOnChooseDictionaryListener() {
                    return mChooseInsuranceTypeListener;
                }

                @Override
                public Choosion getDefaultChoosion() {
                    Choosion choosion = new Choosion(-1, "险种不限");
                    return choosion;
                }

                @Override
                public View getView() {
                    return null;
                }
            });
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            break;
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));

            // map.put("所在地", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(this));
            map.put("配载线路", mChooseLineLayout = new ChooseLineLayout(getApplicationContext()));
            // mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            mChooseLineLayout.setActivity(this);
            mChooseLineLayout.setOnChooseLineListener(this);
            break;
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));

            // map.put("所在地", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(this));
            map.put("设备类别", new Choosable() {
                @Override
                public String getChooseTitle() {
                    return "选择设备类别";
                }

                @Override
                public int getChooseDictionaryType() {
                    return Dictionary.TYPE_DEVICE_TYPE;
                }

                @Override
                public OnChooseDictionaryListener getOnChooseDictionaryListener() {
                    return mChooseDeviceTypeListener;
                }

                @Override
                public Choosion getDefaultChoosion() {
                    Choosion choosion = new Choosion(-1, "设备类别不限");
                    return choosion;
                }

                @Override
                public View getView() {
                    return null;
                }
            });
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            break;
        case Properties.LOGISTIC_TYPE_STORAGE:
            // map.put("所在地", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(this));
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));

            map.put("仓库类别", new Choosable() {
                @Override
                public String getChooseTitle() {
                    return "选择仓库类别";
                }

                @Override
                public int getChooseDictionaryType() {
                    return Dictionary.TYPE_DEPOT_TYPE;
                }

                @Override
                public OnChooseDictionaryListener getOnChooseDictionaryListener() {
                    return mChooseDepotTypeListener;
                }

                @Override
                public Choosion getDefaultChoosion() {
                    Choosion choosion = new Choosion(-1, "仓库类别不限");
                    return choosion;
                }

                @Override
                public View getView() {
                    return null;
                }
            });
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            break;
        case Properties.LOGISTIC_TYPE_PACKAGING:
            map.put("地区", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));

            // map.put("所在地", mChooseServeRegionLayout = new
            // ChooseServeRegionLayout(this));
            map.put("包装类别", new Choosable() {
                @Override
                public String getChooseTitle() {
                    return "选择包装类别";
                }

                @Override
                public int getChooseDictionaryType() {
                    return Dictionary.TYPE_PACK_TYPE;
                }

                @Override
                public OnChooseDictionaryListener getOnChooseDictionaryListener() {
                    return mChoosePackTypeListener;
                }

                @Override
                public Choosion getDefaultChoosion() {
                    Choosion choosion = new Choosion(-1, "包装类别不限");
                    return choosion;
                }

                @Override
                public View getView() {
                    return null;
                }
            });
            // map.put("线路", mChooseLineLayout = new
            // ChooseLineLayout(getApplicationContext()));
            // mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
            mChooseServeRegionLayout.setActivity(this);
            mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
            // mChooseLineLayout.setActivity(this);
            // mChooseLineLayout.setOnChooseLineListener(this);
            break;
        }
        return map;
    }
}
