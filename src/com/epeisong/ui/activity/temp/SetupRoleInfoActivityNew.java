package com.epeisong.ui.activity.temp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.EpsApplication;
import com.epeisong.EpsNetConfig;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.Dictionary;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.model.UserRole;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.ui.activity.temp.ChooseRoleActivity.Role;
import com.epeisong.ui.activity.temp.LoginActivity.OnLoginListener;
import com.epeisong.ui.activity.user.UserLoginedInitActivity;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;
import com.google.protobuf.TextFormat;

/**
 * 设置角色信息（新）
 * @author poet
 *
 */
public class SetupRoleInfoActivityNew extends BaseActivity implements OnClickListener {

    public static final String EXTRA_ROLE = "role";

    private static final int REQUEST_CODE_REGION = 100;
    private static final int REQUEST_CODE_LINE_START = 200;
    private static final int REQUEST_CODE_LINE_END = 201;
    private static final int REQUEST_CODE_USER_REGION = 202;

    private static final int status_create_logi_fail = 1;
    private static final int status_login_fail = 2;
    private static final int status_login_success = 3;
    private static final int status_login_success_need_create_logi = 4;

    private EditText mShowNameEt;
    private TextView mRegionTv;
    private View mLineContainer;
    private TextView mRegionStartTv;
    private TextView mRegionEndTv;
    private LinearLayout mChoosionContainer;
    private View viewLine;
    private TextView mUserRegionTv;
    private EditText mUserAddressEt;
    private RegionResult mUserRegionResult;

    private Role mRole;

    private UserRole mUserRole = new UserRole();

    private String mPhone;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRole = (Role) getIntent().getSerializableExtra(EXTRA_ROLE);
        super.onCreate(savedInstanceState);
        if (mRole == null) {
            ToastUtils.showToast("参数错误");
            finish();
        }
        setContentView(R.layout.activity_setup_role_info_new);
        mShowNameEt = (EditText) findViewById(R.id.et_show_name);
        mRegionTv = (TextView) findViewById(R.id.tv_region);
        mRegionTv.setOnClickListener(this);
        mLineContainer = findViewById(R.id.ll_line);
        viewLine = findViewById(R.id.setup_view);
        mRegionStartTv = (TextView) findViewById(R.id.tv_region_start);
        mRegionStartTv.setOnClickListener(this);
        mRegionEndTv = (TextView) findViewById(R.id.tv_region_end);
        mRegionEndTv.setOnClickListener(this);
        mChoosionContainer = (LinearLayout) findViewById(R.id.ll_choosion_container);
        findViewById(R.id.btn_next).setOnClickListener(this);

        initViewByRoleType(mRole.getCode());

        mPhone = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
        mPassword = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);

        if (mPhone == null || mPassword == null) {
            ToastUtils.showToast("参数错误");
            finish();
        }
    }

    private void initViewByRoleType(int type) {
        if (isShowLine(type)) {
            mLineContainer.setVisibility(View.VISIBLE);
        }
        List<Integer> dictTypes = getDictionaryTypes(type);
        for (int dictType : dictTypes) {
            View item = SystemUtils.inflate(R.layout.activity_setup_role_info_item);
            mChoosionContainer.addView(item);
            viewLine.setVisibility(View.VISIBLE);
            TextView tv_key = (TextView) item.findViewById(R.id.tv_item_key);
            TextView tv_value = (TextView) item.findViewById(R.id.tv_item_value);
            tv_key.setText(Dictionary.getDictName(dictType));
            tv_value.setOnClickListener(this);
            tv_value.setTag(dictType);
        }
        if (canSetRegion(type)) {
            View regionView = SystemUtils.inflate(R.layout.activity_setup_role_info_region);
            mUserRegionTv = (TextView) regionView.findViewById(R.id.tv_user_region);
            mUserAddressEt = (EditText) regionView.findViewById(R.id.et_user_address);
            mUserRegionTv.setOnClickListener(this);
            regionView.findViewById(R.id.btn_loc).setOnClickListener(this);
            mChoosionContainer.addView(regionView);
        }
    }

    private List<Integer> getDictionaryTypes(int type) {
        List<Integer> dictTypes = new ArrayList<Integer>();
        switch (type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
            dictTypes.add(Dictionary.TYPE_TRUCK_LENGTH);
            dictTypes.add(Dictionary.TYPE_TRUCK_TYPE);
            break;
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
            dictTypes.add(Dictionary.TYPE_DEVICE_TYPE);
            break;
        }
        return dictTypes;
    }

    private boolean isShowLine(int type) {
        switch (type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
        case Properties.LOGISTIC_TYPE_MARKET:
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
            return true;
        default:
            return false;
        }
    }

    private boolean canSetRegion(int type) {
        switch (type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
        case Properties.LOGISTIC_TYPE_COURIER:
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
            return false;
        }
        return true;
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), mRole.getName()).setShowLogo(false);
    }

    void onReceiveEpsLocation(EpsLocation loc) {
        if (loc != null) {
            mUserRole.setCurrent_longitude(loc.getLongitude());
            mUserRole.setCurrent_latitude(loc.getLatitude());
            Region city = RegionDao.getInstance().queryByCityName(loc.getCityName());
            if (city != null) {
                Region district = RegionDao.getInstance().queryByDistrictNameAndCityCode(loc.getDistrictName(),
                        city.getCode());
                if (district != null) {
                    mUserRegionResult = RegionDao.convertToResult(district);
                    String address = loc.getAddressName();
                    mUserRegionTv.setText(mUserRegionResult.getGeneralName());
                    mUserAddressEt.setText(address);
                    return;
                }
            }
        }
        ToastUtils.showToast("定位失败");
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
        case R.id.tv_region:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_REGION);
            return;
        case R.id.tv_region_start:
            if (mRole.getCode() == Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE) {
                Bundle extras = new Bundle();
                extras.putBoolean(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY, false);
                extras.putInt(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
                ChooseRegionActivity.launch(this, REQUEST_CODE_LINE_START, extras);
            } else {
                ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_LINE_START);
            }
            return;
        case R.id.tv_region_end:
            if (mRole.getCode() == Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE) {
                Bundle extras = new Bundle();
                extras.putBoolean(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY, false);
                extras.putInt(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
                ChooseRegionActivity.launch(this, REQUEST_CODE_LINE_END, extras);
            } else {
                ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_LINE_END);
            }
            return;
        case R.id.tv_user_region:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_1_3, REQUEST_CODE_USER_REGION);
            return;
        case R.id.btn_loc:
            showPendingDialog("定位中...");
            EpsLocationRequestor requestor = new EpsLocationRequestor();
            requestor.requestEpsLocation(new OnEpsLocationListener() {
                @Override
                public void onEpsLocation(EpsLocation epsLocation) {
                    dismissPendingDialog();
                    onReceiveEpsLocation(epsLocation);
                }
            });
            return;
        case R.id.btn_next:
            next();
            return;
        }
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            final int dictType = (Integer) tag;
            String title = "选择" + Dictionary.getDictName(dictType);
            List<Dictionary> data = DictionaryDao.getInstance().queryByType(dictType);
            showDictionaryListDialog(title, data, new OnChooseDictionaryListener() {
                @Override
                public void onChoosedDictionary(Dictionary dict) {
                    mUserRole.setDictionary(dict);
                    ((TextView) v).setText(dict.getName());
                }
            });
        }
    }

    private boolean setRequest(ProtoEBizLogistics.Builder logi) {
        setLine(logi);
        switch (mRole.getCode()) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            if (mUserRole.getTruckLengthCode() > 0) {
                logi.setVehicleLengthCode(mUserRole.getTruckLengthCode());
                logi.setVehicleLengthName(mUserRole.getTruckLengthName());
            }
            if (mUserRole.getTruckTypeCode() > 0) {
                logi.setVehicleType(mUserRole.getTruckTypeCode());
                logi.setVehicleTypeName(mUserRole.getTruckTypeName());
            }
            return true;
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:

            return true;
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:

            return true;
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
            if (mUserRole.getDeviceCode() > 0) {
                logi.setEquipmentType(mUserRole.getDeviceCode());
                logi.setEquipmentTypeName(mUserRole.getDeviceName());
            }
            return true;
        }
        return true;
    }

    private void setLine(ProtoEBizLogistics.Builder logi) {
        switch (mRole.getCode()) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS:
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
        case Properties.LOGISTIC_TYPE_MARKET:
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
            if (mUserRole.getLineStartCode() > 0 && mUserRole.getLineEndCode() > 0) {
                logi.setRouteCodeA(mUserRole.getLineStartCode());
                logi.setRouteNameA(mUserRole.getLineStartName());
                logi.setRouteCodeB(mUserRole.getLineEndCode());
                logi.setRouteNameB(mUserRole.getLineEndName());
            }
            break;
        }
    }

    private void next() {
        final String show_name = mShowNameEt.getText().toString();
        if (TextUtils.isEmpty(show_name)) {
            ToastUtils.showToast("请输入名称");
            return;
        }
        if (mUserRole.getRegionCode() <= 0) {
            ToastUtils.showToast("请选择地区");
            return;
        }
        showPendingDialog(null);
        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                LogisticsReq.Builder req = LogisticsReq.newBuilder();
                long currentTimeMillis = System.currentTimeMillis();
                req.setMobile(mPhone);
                req.setShadowPassword(JavaUtils.getShadowPwd(mPhone, mPassword, currentTimeMillis));
                req.setCurrentTimeMillis(currentTimeMillis);
                req.setClientBigType(Properties.APP_CLIENT_BIG_TYPE_PHONE);
                ProtoEBizLogistics.Builder logi = ProtoEBizLogistics.newBuilder();
                logi.setName(show_name);
                logi.setAccountName(mPhone);
                logi.setLogisticsType(mRole.getCode());
                logi.setLogisticsTypeName(mRole.getName());
                logi.setServeRegionCode(mUserRole.getRegionCode());
                logi.setServeRegionName(mUserRole.getRegionName());
                // 默认数据：运输类型
                logi.setTransportTypeCode(1);
                logi.setTransportTypeName("公路运输");

                if (mUserRole.getCurrent_longitude() > 0) {
                    logi.setCurrentLongitude(mUserRole.getCurrent_longitude());
                    logi.setCurrentLatitude(mUserRole.getCurrent_latitude());
                }
                if (mUserRegionResult != null) {
                    logi.setRegionCode(mUserRegionResult.getFullCode());
                    logi.setRegionName(mUserRegionResult.getGeneralName());
                    String address = mUserAddressEt.getText().toString();
                    if (address != null) {
                        logi.setAddress(address);
                    }
                }

                if (!setRequest(logi)) {
                    return status_create_logi_fail;
                }
                req.setBizLogistics(logi);
                LogUtils.d(SetupRoleInfoActivityNew.this, mPhone + ":" + mPassword);
                LogUtils.d(SetupRoleInfoActivityNew.this, TextFormat.printToString(req));
                try {
                    CommonLogisticsResp.Builder resp = NetServiceFactory.getInstance().createLogistics(
                            EpsNetConfig.getHost(), EpsNetConfig.PORT, req, 15 * 1000);
                    if (resp != null && Constants.SUCC.equals(resp.getResult())) {
                        Boolean loginB = LoginActivity.login(mPhone, mPassword, new OnLoginListener() {
                            @Override
                            public void onNeedCreateLogi() {
                                Intent intent = new Intent(SetupRoleInfoActivityNew.this,
                                        SetupRoleInfoActivityNew.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        if (loginB == null) {
                            return status_login_success_need_create_logi;
                        }
                        if (loginB) {
                            return status_login_success;
                        }
                        return status_login_fail;
                    } else {
                        if (resp != null) {
                            ToastUtils.showToastInThread(resp.getDesc());
                        }
                        return status_create_logi_fail;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return status_create_logi_fail;
            }

            @Override
            protected void onPostExecute(Integer result) {
                dismissPendingDialog();
                LogUtils.d("SetupRoleInfoActivity", "onPostExecute:" + result);
                if (result != null) {
                    switch (result) {
                    case status_create_logi_fail:

                        break;
                    case status_login_fail:
                        EpsApplication.exit(SetupRoleInfoActivityNew.this, LoginActivity.class, (Serializable) null);
                        break;
                    case status_login_success:
                        Intent intent = new Intent(SetupRoleInfoActivityNew.this, UserLoginedInitActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case status_login_success_need_create_logi:

                        break;
                    }
                }
            }
        };
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Serializable extra = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (extra != null && extra instanceof RegionResult) {
                RegionResult result = (RegionResult) extra;
                if (result.getCode() <= 0) {
                    ToastUtils.showToast("地址编码异常");
                    return;
                }
                if (requestCode == REQUEST_CODE_REGION) {
                    mUserRole.setRegionCode(result.getFullCode());
                    mUserRole.setRegionName(result.getShortNameFromDistrict());
                    mRegionTv.setText(result.getShortNameFromDistrict());
                } else if (requestCode == REQUEST_CODE_LINE_START) {
                    mUserRole.setLineStartCode(result.getFullCode());
                    mUserRole.setLineStartName(result.getShortNameFromDistrict());
                    mRegionStartTv.setText(result.getShortNameFromDistrict());
                } else if (requestCode == REQUEST_CODE_LINE_END) {
                    mUserRole.setLineEndCode(result.getFullCode());
                    mUserRole.setLineEndName(result.getShortNameFromDistrict());
                    mRegionEndTv.setText(result.getShortNameFromDistrict());
                } else if (requestCode == REQUEST_CODE_USER_REGION) {
                    mUserRegionResult = result;
                    mUserRegionTv.setText(result.getGeneralName());
                }
            }
        }
    }
}
