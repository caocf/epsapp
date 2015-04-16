package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.dialog.ChooseLineActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.data.net.NetCreateRoleByCostomerService;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.logistics.proto.Eps.LogisticsReq.Builder;
import com.epeisong.model.Dictionary;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.model.UserRole;
import com.epeisong.ui.activity.temp.ChooseRoleActivity.Role;
import com.epeisong.utils.ReleaseLog;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.java.JavaUtils;

/**
 * 客服添加用户
 * @author gnn
 *
 */
public class CustomerAddUserActivity extends BaseActivity implements OnClickListener {
    private static final int REQUEST_CODE_CHOOSE_REGION = 100;
    private static final int REQUEST_CODE_CHOOSE_LINE = 102;
    private ImageView iv_area_info;
    private TextView tv_area_info;
    private double longitude;
    private double latitude;
    private String address;
    private String generalName;
    private int areaRegionCode;
    private String areaRegionName;
    private RegionResult mUserRegionResult;
    private int serveRegionCode;
    private int logisticType;
    private int carLengthCode;
    private int carTypeCode;
    private int transportTypeCode;
    private int packTypeCode; // 包装类型
    private int wareTypeCode; // 仓库类型
    private int goodsTypeCode;
    private int equipmentTypeCode;
    private int validityTypeCode; // 时效
    private int tonTypeCode;
    // private ChooseLineLayout mChooseLineLayout;
    private TextView mRegionTv;
    private EditText mAddressEt;
    private RegionResult mRegionResult;
    private RegionResult start;
    private RegionResult end;

    private TextView mSelectPlace;
    private LinearLayout mLayoutType;
    private TextView mTvTypeName;
    private EditText et_phone;
    private EditText et_password;
    private EditText et_name;
    private EditText et_contact;
    private EditText et_contact_tel;
    private EditText et_contact_mobile;
    private EditText et_intro_content;
    private EditText et_ton;
    private TextView tv_line;
    private TextView tv_car_length;
    private TextView tv_car_type;
    private TextView tv_transport_type;
    // private EditText et_ton;
    private TextView tv_packaging_type;
    private TextView tv_warehouse_type; // 仓库
    private TextView tv_ton_type;
    private TextView tv_goods_type;
    private TextView tv_equipment_type;
    private TextView tv_validity_type; // 时效
    private TextView tv_area; // 地区
    private Button btn_obtain;
    private Button btn_clear;

    private LinearLayout ll_line;
    private LinearLayout ll_car_length;
    private LinearLayout ll_car_type;
    private LinearLayout ll_transport_type;
    private LinearLayout ll_ton;
    private LinearLayout ll_packaging_type;
    private LinearLayout ll_warehouse_type;
    private LinearLayout ll_goods_type;
    private LinearLayout ll_equipment_type;
    private LinearLayout ll_validity_type;
    private LinearLayout ll_area;

    private MyAdapter mAdapter;
    private String resultContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customer_add_user);
        mSelectPlace = (TextView) findViewById(R.id.tv_select_place);
        mSelectPlace.setOnClickListener(this);
        mLayoutType = (LinearLayout) findViewById(R.id.ll_type);
        mLayoutType.setOnClickListener(this);
        mTvTypeName = (TextView) findViewById(R.id.tv_typename);
        findViewById(R.id.bt_public_bulletin).setOnClickListener(this);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_contact = (EditText) findViewById(R.id.et_contact);
        et_contact_tel = (EditText) findViewById(R.id.et_contact_tel);
        et_contact_mobile = (EditText) findViewById(R.id.et_contact_mobile);
        et_intro_content = (EditText) findViewById(R.id.et_intro_content);
        et_ton = (EditText) findViewById(R.id.et_ton);
        tv_line = (TextView) findViewById(R.id.tv_line);
        tv_line.setOnClickListener(this);
        tv_car_length = (TextView) findViewById(R.id.tv_car_length);
        tv_car_length.setOnClickListener(this);
        tv_car_type = (TextView) findViewById(R.id.tv_car_type);
        tv_car_type.setOnClickListener(this);
        tv_transport_type = (TextView) findViewById(R.id.tv_transport_type);
        tv_transport_type.setOnClickListener(this);
        // et_ton = (EditText) findViewById(R.id.et_ton);
        tv_packaging_type = (TextView) findViewById(R.id.tv_packaging_type);
        tv_packaging_type.setOnClickListener(this);
        tv_warehouse_type = (TextView) findViewById(R.id.tv_warehouse_type);
        tv_warehouse_type.setOnClickListener(this);
        tv_ton_type = (TextView) findViewById(R.id.tv_ton_type);
        tv_ton_type.setOnClickListener(this);
        tv_goods_type = (TextView) findViewById(R.id.tv_goods_type);
        tv_goods_type.setOnClickListener(this);
        tv_equipment_type = (TextView) findViewById(R.id.tv_equipment_type);
        tv_equipment_type.setOnClickListener(this);
        tv_validity_type = (TextView) findViewById(R.id.tv_validity_type);
        tv_validity_type.setOnClickListener(this);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_area.setOnClickListener(this);

        ll_line = (LinearLayout) findViewById(R.id.ll_line);
        ll_car_length = (LinearLayout) findViewById(R.id.ll_car_length);
        ll_car_type = (LinearLayout) findViewById(R.id.ll_car_type);
        ll_transport_type = (LinearLayout) findViewById(R.id.ll_transport_type);
        ll_ton = (LinearLayout) findViewById(R.id.ll_ton);
        ll_packaging_type = (LinearLayout) findViewById(R.id.ll_packaging_type);
        ll_warehouse_type = (LinearLayout) findViewById(R.id.ll_warehouse_type);
        ll_goods_type = (LinearLayout) findViewById(R.id.ll_goods_type);
        ll_equipment_type = (LinearLayout) findViewById(R.id.ll_equipment_type);
        ll_validity_type = (LinearLayout) findViewById(R.id.ll_validity_type);
        ll_area = (LinearLayout) findViewById(R.id.ll_area);
        tv_transport_type.setText("公路运输");
        transportTypeCode = 1;
        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                // et_contact_tel.setText(s.toString());
                et_contact_mobile.setText(s.toString());
            }
        });
        et_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // et_contact.setText(s.toString());
            }
        });
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "添加用户", null).setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ll_type:
            GridView mGv;
            final List<String> arrayFruit = new ArrayList<String>();
            final List<Integer> arraytype = new ArrayList<Integer>();
            final List<Dictionary> RoleType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_ROLE);
            List<Dictionary> dicts = new ArrayList<Dictionary>();
            final List<Role> list = new ArrayList<Role>();
            for (Dictionary dict : RoleType) {
                // 8:快递 10:同城配送
                if (dict.getId() != -1 && dict.getId() != 3 && dict.getId() != 6 && dict.getId() != 8
                        && dict.getId() != 10 && dict.getId() != 21 && dict.getId() != 22 && dict.getId() != 30
                        && dict.getId() != 31 && dict.getId() != 32 && dict.getId() != 33) {
                    dicts.add(dict);
                }
                // if(dict.getId() != -1 && dict.getId() != 3 && dict.getId() !=
                // 6 && dict.getId() != 21 &&dict.getId() != 22 &&dict.getId()
                // != 30 &&dict.getId() != 31 &&dict.getId() != 32
                // &&dict.getId() != 33){
                // dicts.add(dict);
                // }
            }
            for (Dictionary dict : dicts) {
                list.add(new Role().setCode(dict.getType()).setName(dict.getName()));
                arrayFruit.add(dict.getName());
                arraytype.add(dict.getId());
            }
            if (list.size() % 2 != 0) {
                list.add(new Role());
                arrayFruit.add("");
                arraytype.add(-1);
            }
            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
            dialog.show();
            Window window = dialog.getWindow();
            window.setContentView(R.layout.activity_customer_add_user_dialog);
            mGv = (GridView) window.findViewById(R.id.gridview);
            mGv.setAdapter(mAdapter = new MyAdapter());
            mGv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!TextUtils.isEmpty(arrayFruit.get(position))) {
                        mTvTypeName.setText(arrayFruit.get(position));
                        logisticType = arraytype.get(position);
                        showOrHide(logisticType);
                        dialog.dismiss();
                    }
                }
            });

            mAdapter.addAll(list);

            break;
        case R.id.tv_select_place:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_CHOOSE_REGION);
            break;
        case R.id.tv_line:
            Bundle extrasForChooseRegion = new Bundle();
            extrasForChooseRegion.putInt(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
            // if (mUser.getUser_type_code() ==
            // Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE) {
            // extrasForChooseRegion.putBoolean(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY,
            // false);
            // }
            ChooseLineActivity.launch(this, REQUEST_CODE_CHOOSE_LINE, true, extrasForChooseRegion);
            // ChooseLineActivity.launch(this, REQUEST_CODE_CHOOSE_LINE);
            break;
        case R.id.tv_car_length:
            List<Dictionary> dataLength = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_LENGTH);
            showDictionaryListDialog("选择车长", dataLength, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_car_length.setText(item.getName());
                    carLengthCode = item.getId();

                }
            });
            break;
        case R.id.tv_car_type:
            List<Dictionary> dataType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_TYPE);
            showDictionaryListDialog("选择车型", dataType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_car_type.setText(item.getName());
                    carTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_transport_type:
            List<Dictionary> transportType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRANSPORT_MODE);
            showDictionaryListDialog("选择运输方式", transportType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_transport_type.setText(item.getName());
                    transportTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_ton_type:
            List<Dictionary> dataTon = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_LOAD_TON);
            showDictionaryListDialog("选择吨位", dataTon, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_ton_type.setText(item.getName());
                    tonTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_packaging_type:
            List<Dictionary> packType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_PACK_TYPE);
            showDictionaryListDialog("选择包装类型", packType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_packaging_type.setText(item.getName());
                    packTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_warehouse_type:
            List<Dictionary> wareType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_DEPOT_TYPE);
            showDictionaryListDialog("选择仓库类型", wareType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_warehouse_type.setText(item.getName());
                    wareTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_goods_type:
            List<Dictionary> goodsType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_GOODS_TYPE);
            showDictionaryListDialog("选择货物类型", goodsType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_goods_type.setText(item.getName());
                    goodsTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_equipment_type:
            List<Dictionary> equipmentType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_DEVICE_TYPE);
            showDictionaryListDialog("选择设备类型", equipmentType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_equipment_type.setText(item.getName());
                    equipmentTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_validity_type:
            List<Dictionary> validityType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_VALIDITY);
            showDictionaryListDialog("选择时效", validityType, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary item) {
                    tv_validity_type.setText(item.getName());
                    validityTypeCode = item.getId();
                }
            });
            break;
        case R.id.tv_area:
            // final Dialog area = new AlertDialog.Builder(this).create();
            Button mbtn;

            final Dialog area = new Dialog(this);
            area.setTitle("选择地址");
            area.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
            area.show();
            Window windowArea = area.getWindow();
            windowArea.setContentView(R.layout.activity_add_edit_address);
            mAddressEt = (EditText) windowArea.findViewById(R.id.et_address);
            mRegionTv = (TextView) windowArea.findViewById(R.id.tv_region);
            iv_area_info = (ImageView) windowArea.findViewById(R.id.iv_area_info);
            tv_area_info = (TextView) windowArea.findViewById(R.id.tv_area_info);
            if (!TextUtils.isEmpty(areaRegionName)) {
                mRegionTv.setText(areaRegionName);
            }
            if (!TextUtils.isEmpty(address)) {
                mAddressEt.setText(address);
            }
            mRegionTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChooseRegionActivity.launch(CustomerAddUserActivity.this, ChooseRegionActivity.FILTER_0_3, 105);
                }
            });
            btn_obtain = (Button) windowArea.findViewById(R.id.btn_obtain);
            btn_obtain.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showPendingDialog("定位中...");
                    EpsLocationRequestor requestor = new EpsLocationRequestor();
                    requestor.requestEpsLocation(new OnEpsLocationListener() {
                        @Override
                        public void onEpsLocation(EpsLocation epsLocation) {
                            dismissPendingDialog();
                            onReceiveEpsLocation(epsLocation);
                        }
                    });
                }
            });
            btn_clear = (Button) windowArea.findViewById(R.id.btn_clear);
            btn_clear.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    longitude = 0;
                    latitude = 0;
                    iv_area_info.setVisibility(View.GONE);
                }
            });

            mbtn = (Button) windowArea.findViewById(R.id.btn_ok);
            mbtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (longitude != 0 && latitude != 0 && !TextUtils.isEmpty(generalName)) {
                        areaRegionName = generalName;
                        tv_area.setText(areaRegionName + address);
                        area.dismiss();
                    } else {
                        if (TextUtils.isEmpty(mRegionTv.getText().toString())
                                || TextUtils.isEmpty(mAddressEt.getText().toString())) {
                            ToastUtils.showToast("地址不能为空，请手动填写");
                        } else {
                            address = mAddressEt.getText().toString();
                            areaRegionName = mRegionTv.getText().toString();
                            tv_area.setText(areaRegionName + address);
                            area.dismiss();
                        }
                    }
                    // area.dismiss();
                }
            });
            break;
        case R.id.bt_public_bulletin:
            final String phone = et_phone.getText().toString();
            final String name = et_name.getText().toString();
            if (Validation() < 0) {
                return;
            }
            showPendingDialog(null);
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    NetCreateRoleByCostomerService net = new NetCreateRoleByCostomerService() {

                        @Override
                        protected boolean onSetRequest(Builder req) {
                            long currentTimeMillis = System.currentTimeMillis();
                            req.setMobile(phone);
                            req.setClientBigType(Properties.APP_CLIENT_BIG_TYPE_PHONE);
                            req.setCurrentTimeMillis(currentTimeMillis);
                            // req.setServeRegionCode(serveRegionCode);
                            // req.setServeRegionName(mSelectPlace.getText().toString());
                            ProtoEBizLogistics.Builder logi = ProtoEBizLogistics.newBuilder();
                            logi.setServeRegionCode(serveRegionCode);
                            logi.setServeRegionName(mSelectPlace.getText().toString());
                            logi.setLogisticsType(logisticType);
                            logi.setLogisticsTypeName(mTvTypeName.getText().toString());
                            logi.setAccountName(phone);
                            logi.setName(name);
                            if (TextUtils.isEmpty(et_contact.getText().toString())) {
                                logi.setContact(name);
                            } else {
                                logi.setContact(et_contact.getText().toString());
                            }
                            // 密码
                            if (!TextUtils.isEmpty(et_password.getText().toString())) {
                                String shadow = JavaUtils.getShadowPwd(phone, et_password.getText().toString(),
                                        System.currentTimeMillis());
                                req.setShadowPassword(shadow);
                            }
                            // 联系电话
                            if (!TextUtils.isEmpty(et_contact_tel.getText().toString())) {
                                logi.setTelephone1(et_contact_tel.getText().toString());
                            }
                            // 联系手机
                            logi.setMobile1(et_contact_mobile.getText().toString());
                            logi.setCreateDate(currentTimeMillis);
                            logi.setSelfIntroduction(et_intro_content.getText().toString());
                            req.setBizLogistics(logi);

                            return setRequest(req);
                        }
                    };
                    try {
                        CommonLogisticsResp.Builder resp = net.request();
                        if (resp != null && "SUCC".equals(resp.getResult())) {
                            return true;
                        } else {
                            resultContent = resp.getDesc();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    dismissPendingDialog();
                    if (result) {
                        finish();
                        ToastUtils.showToast("添加成功");
                    } else {
                        ToastUtils.showToast(resultContent);
                    }
                }

            };
            task.execute();
            break;

        default:
            break;
        }
    }

    private int Validation() {
        String account = et_phone.getText().toString();
        String roleType = mTvTypeName.getText().toString();
        String name = et_name.getText().toString();
        String area = mSelectPlace.getText().toString();
        String password = et_password.getText().toString();
        if (!TextUtils.isEmpty(password)) {
            if (password.length() < 6 || password.length() > 20) {
                return -1;
            }
        }
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showToast("用户账号不能为空");
            return -1;
        }
        if (TextUtils.isEmpty(roleType)) {
            ToastUtils.showToast("角色类别不能为空");
            return -1;
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast("用户名称不能为空");
            return -1;
        }
        if (TextUtils.isEmpty(area)) {
            ToastUtils.showToast("地区不能为空");
            return -1;
        }
        // if(ll_line.getVisibility() == View.VISIBLE){
        // if(TextUtils.isEmpty(tv_line.getText().toString())){
        // ToastUtils.showToast("请选择线路");
        // return -1;
        // }
        // }
        return 1;
    }

    private boolean setRequest(LogisticsReq.Builder req) {
        ProtoEBizLogistics.Builder logi = req.getBizLogisticsBuilder();
        if (!TextUtils.isEmpty(tv_area.getText().toString())) {
            logi.setRegionCode(areaRegionCode);
            logi.setRegionName(areaRegionName);
            logi.setCurrentLatitude(latitude);
            logi.setCurrentLongitude(longitude);
        }
        switch (logisticType) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE: // 整车运输
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS: // 驳货
            if (!TextUtils.isEmpty(tv_line.getText().toString())) {
                logi.setRouteCodeA(start.getFullCode());
                logi.setRouteNameA(start.getShortNameFromDistrict());
                logi.setRouteCodeB(end.getFullCode());
                logi.setRouteNameB(end.getShortNameFromDistrict());
            }
            logi.setVehicleLengthCode(carLengthCode);
            logi.setVehicleLengthName(tv_car_length.getText().toString());
            logi.setVehicleType(carTypeCode);
            logi.setVehicleTypeName(tv_car_type.getText().toString());
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            // logi.setMaxKilogram(tonTypeCode);
            if (!TextUtils.isEmpty(et_ton.getText().toString())) {
                logi.setMaxKilogram(Integer.parseInt(et_ton.getText().toString()) * 1000);
            }
            if (TextUtils.isEmpty(tv_transport_type.getText().toString())) {
                // 默认数据：运输类型
                logi.setTransportTypeCode(1);
                logi.setTransportTypeName("公路运输");
            } else {
                logi.setTransportTypeCode(transportTypeCode);
                logi.setTransportTypeName(tv_transport_type.getText().toString());
            }
            return true;
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE: // 专线
            if (!TextUtils.isEmpty(tv_line.getText().toString())) {
                logi.setRouteCodeA(start.getFullCode());
                logi.setRouteNameA(start.getShortNameFromDistrict());
                logi.setRouteCodeB(end.getFullCode());
                logi.setRouteNameB(end.getShortNameFromDistrict());
            }
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            logi.setAddress(address);
            // logi.setRegionCode(areaRegionCode);
            // logi.setRegionName(areaRegionName);
            // logi.setCurrentLatitude(latitude);
            // logi.setCurrentLongitude(longitude);
            logi.setPeriodOfValidity(validityTypeCode);
            logi.setPeriodOfValidityDesc(tv_validity_type.getText().toString());
            if (TextUtils.isEmpty(tv_transport_type.getText().toString())) {
                // 默认数据：运输类型
                logi.setTransportTypeCode(1);
                logi.setTransportTypeName("公路运输");
            } else {
                logi.setTransportTypeCode(transportTypeCode);
                logi.setTransportTypeName(tv_transport_type.getText().toString());
            }
            return true;
        case Properties.LOGISTIC_TYPE_SORTING_TRANSFER: // 分拣中转
        case Properties.LOGISTIC_TYPE_MOVE_HOUSE: // 搬家
            if (!TextUtils.isEmpty(tv_line.getText().toString())) {
                logi.setRouteCodeA(start.getFullCode());
                logi.setRouteNameA(start.getShortNameFromDistrict());
                logi.setRouteCodeB(end.getFullCode());
                logi.setRouteNameB(end.getShortNameFromDistrict());
            }
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            logi.setAddress(address);
            // logi.setRegionCode(areaRegionCode);
            // logi.setRegionName(areaRegionName);
            // logi.setCurrentLatitude(latitude);
            // logi.setCurrentLongitude(longitude);
            return true;
        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS: // 第三方物流
            if (TextUtils.isEmpty(tv_transport_type.getText().toString())) {
                // 默认数据：运输类型
                logi.setTransportTypeCode(1);
                logi.setTransportTypeName("公路运输");
            } else {
                logi.setTransportTypeCode(transportTypeCode);
                logi.setTransportTypeName(tv_transport_type.getText().toString());
            }
            if (!TextUtils.isEmpty(tv_line.getText().toString())) {
                logi.setRouteCodeA(start.getFullCode());
                logi.setRouteNameA(start.getShortNameFromDistrict());
                logi.setRouteCodeB(end.getFullCode());
                logi.setRouteNameB(end.getShortNameFromDistrict());
            }
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            logi.setAddress(address);
            // logi.setRegionCode(areaRegionCode);
            // logi.setRegionName(areaRegionName);
            // logi.setCurrentLatitude(latitude);
            // logi.setCurrentLongitude(longitude);
            return true;
        case Properties.LOGISTIC_TYPE_EXPRESS: // 快递
        case Properties.LOGISTIC_TYPE_COURIER: // 快递员
        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION: // 同城配送
            if (!TextUtils.isEmpty(tv_line.getText().toString())) {
                logi.setRouteCodeA(start.getFullCode());
                logi.setRouteNameA(start.getShortNameFromDistrict());
                logi.setRouteCodeB(end.getFullCode());
                logi.setRouteNameB(end.getShortNameFromDistrict());
            }
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            if (TextUtils.isEmpty(tv_transport_type.getText().toString())) {
                // 默认数据：运输类型
                logi.setTransportTypeCode(1);
                logi.setTransportTypeName("公路运输");
            } else {
                logi.setTransportTypeCode(transportTypeCode);
                logi.setTransportTypeName(tv_transport_type.getText().toString());
            }
            return true;
        case Properties.LOGISTIC_TYPE_STORAGE: // 仓储
            logi.setStorageType(wareTypeCode);
            logi.setStorageTypeName(tv_warehouse_type.getText().toString());
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            logi.setAddress(address);
            // logi.setRegionCode(areaRegionCode);
            // logi.setRegionName(areaRegionName);
            // logi.setCurrentLatitude(latitude);
            // logi.setCurrentLongitude(longitude);
            return true;
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING: // 设备类型
            logi.setEquipmentType(equipmentTypeCode);
            logi.setEquipmentTypeName(tv_equipment_type.getText().toString());
            return true;
        case Properties.LOGISTIC_TYPE_PACKAGING: // 包装
            logi.setPackageType(packTypeCode);
            logi.setPackageTypeName(tv_packaging_type.getText().toString());
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            return true;
        case Properties.LOGISTIC_TYPE_ENTERPRISE_LOGISTICS_DEPARTMENT: // 企业物流部
            logi.setGoodsType(goodsTypeCode);
            logi.setGoodsTypeName(tv_goods_type.getText().toString());
            logi.setAddress(address);
            // logi.setRegionCode(areaRegionCode);
            // logi.setRegionName(areaRegionName);
            // logi.setCurrentLatitude(latitude);
            // logi.setCurrentLongitude(longitude);
            return true;
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT: // 配置信息部
            if (!TextUtils.isEmpty(tv_line.getText().toString())) {
                logi.setRouteCodeA(start.getFullCode());
                logi.setRouteNameA(start.getShortNameFromDistrict());
                logi.setRouteCodeB(end.getFullCode());
                logi.setRouteNameB(end.getShortNameFromDistrict());
            }
            logi.setAddress(address);
            // logi.setRegionCode(areaRegionCode);
            // logi.setRegionName(areaRegionName);
            // logi.setCurrentLatitude(latitude);
            // logi.setCurrentLongitude(longitude);
            return true;

        default:
            return true;
        }
    }

    private void showOrHide(int type) {
        // 整车运输，驳货，快递员 不需要地址
        ll_line.setVisibility(View.GONE);
        ll_car_length.setVisibility(View.GONE);
        ll_car_type.setVisibility(View.GONE);
        ll_transport_type.setVisibility(View.GONE);
        ll_ton.setVisibility(View.GONE);
        ll_packaging_type.setVisibility(View.GONE);
        ll_warehouse_type.setVisibility(View.GONE);
        ll_goods_type.setVisibility(View.GONE);
        ll_equipment_type.setVisibility(View.GONE);
        ll_validity_type.setVisibility(View.GONE);
        ll_area.setVisibility(View.VISIBLE);
        switch (type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE: // 整车运输
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS: // 驳货
            ll_line.setVisibility(View.VISIBLE);
            ll_car_length.setVisibility(View.VISIBLE);
            ll_car_type.setVisibility(View.VISIBLE);
            ll_ton.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            ll_transport_type.setVisibility(View.VISIBLE);
            ll_area.setVisibility(View.GONE);
            break;
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE: // 专线
            ll_line.setVisibility(View.VISIBLE);
            ll_validity_type.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            ll_transport_type.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_SORTING_TRANSFER: // 分拣中转
        case Properties.LOGISTIC_TYPE_MOVE_HOUSE: // 搬家
            ll_line.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS: // 第三方物流
            ll_transport_type.setVisibility(View.VISIBLE);
            ll_line.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_EXPRESS: // 快递
        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION: // 同城配送
            ll_line.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            ll_transport_type.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_COURIER: // 快递员
            ll_line.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            ll_transport_type.setVisibility(View.VISIBLE);
            ll_area.setVisibility(View.GONE);
            break;
        case Properties.LOGISTIC_TYPE_STORAGE: // 仓储
            ll_warehouse_type.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING: // 设备类型
            ll_equipment_type.setVisibility(View.VISIBLE);
            ll_area.setVisibility(View.GONE);
            break;
        case Properties.LOGISTIC_TYPE_PACKAGING: // 包装
            ll_packaging_type.setVisibility(View.VISIBLE);
            ll_goods_type.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_ENTERPRISE_LOGISTICS_DEPARTMENT: // 企业物流部
            ll_goods_type.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT: // 配置信息部
            ll_line.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_REGION) {
                RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                mSelectPlace.setTextColor(Color.BLACK);
                serveRegionCode = result.getFullCode();
                // serveRegionCode = result.getCode();
                mSelectPlace.setText(result.getShortNameFromDistrict());// .getGeneralName());getShortNameFromDistrict
            } else if (REQUEST_CODE_CHOOSE_LINE == requestCode) {
                Serializable extra1 = data.getSerializableExtra(ChooseLineActivity.EXTRA_START_REGION);
                Serializable extra2 = data.getSerializableExtra(ChooseLineActivity.EXTRA_END_REGION);
                if (extra1 != null && extra2 != null) {
                    start = (RegionResult) extra1;
                    end = (RegionResult) extra2;
                    UserRole role = new UserRole();
                    role.setLineStartCode(start.getFullCode());
                    role.setLineStartName(start.getShortNameFromDistrict());
                    role.setLineEndCode(end.getFullCode());
                    role.setLineEndName(end.getShortNameFromDistrict());
                    tv_line.setText(start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
                }
            } else if (requestCode == 105) {
                Serializable extra = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                if (extra != null && extra instanceof RegionResult) {
                    mRegionResult = (RegionResult) extra;
                    areaRegionName = mRegionResult.getGeneralName();
                    mRegionTv.setText(areaRegionName);
                    areaRegionCode = mRegionResult.getFullCode();
                }
            }
        }
    }

    class MyAdapter extends HoldDataBaseAdapter<Role> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ll = new LinearLayout(getApplication());
            // ll.setBackgroundResource(R.color.white);
            ll.setBackgroundResource(R.drawable.selector_gridview_add_user);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(getApplication());
            tv.setTextSize(20);
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setLayoutParams(lp);
            tv.setPadding(15, 15, 0, 15);
            ll.addView(tv);
            Role role = getItem(position);
            tv.setText(role.getName());
            tv.setTag(role);
            return ll;
        }
    }

    void onReceiveEpsLocation(EpsLocation loc) {
        tv_area_info.setText("");
        iv_area_info.setVisibility(View.GONE);
        mRegionTv.setText("");
        mAddressEt.setText("");
        if (loc != null) {
            address = loc.getAddressName();
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();
            Region city = RegionDao.getInstance().queryByCityName(loc.getCityName());
            if (city != null) {
                Region district = RegionDao.getInstance().queryByDistrictNameAndCityCode(loc.getDistrictName(),
                        city.getCode());
                if (district != null) {
                    mUserRegionResult = RegionDao.convertToResult(district);
                    areaRegionCode = mUserRegionResult.getFullCode();
                    areaRegionName = mUserRegionResult.getGeneralName();
                    mRegionTv.setText(areaRegionName);
                    mAddressEt.setText(address);
                    iv_area_info.setVisibility(View.VISIBLE);
                }
            } else {
                tv_area_info.setText("定位失败");
                ReleaseLog.log("customerAddUserActivity", JavaUtils.getString(loc));
            }
        } else {
            tv_area_info.setText("定位失败");
            ReleaseLog.log("customerAddUserActivity", JavaUtils.getString(loc));
        }
    }

}
