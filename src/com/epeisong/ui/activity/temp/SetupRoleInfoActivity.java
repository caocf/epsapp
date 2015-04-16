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

import com.epeisong.R;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.Dictionary;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.net.request.NetCreateRole;
import com.epeisong.net.request.OnNetRequestListener;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.ui.activity.temp.ChooseRoleActivity.SetupModel;
import com.epeisong.ui.activity.user.TempActivity;
import com.epeisong.ui.activity.user.UserLoginedInitActivity;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 注册-设置业务
 * 
 * @author poet
 * 
 */
public class SetupRoleInfoActivity extends TempActivity implements OnClickListener {

    private static final int REQUEST_CODE_REGION = 100;
    private static final int REQUEST_CODE_LINE_START = 200;
    private static final int REQUEST_CODE_LINE_END = 201;

    private TextView mRegionTabTv;
    private TextView mRegionTv;
    private View mLineContainer;
    private TextView mRegionStartTv;
    private TextView mRegionEndTv;
    private LinearLayout mChoosionContainer;
    private List<TextView> mChoosionValueViews = new ArrayList<TextView>();
    private EditText mIntroEt;

    private SetupModel mSetupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSetupModel = ChooseRoleActivity.sSetupModel;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_role_info);
        mRegionTabTv = (TextView) findViewById(R.id.tv_region_tab);
        mRegionTv = (TextView) findViewById(R.id.tv_region);
        mRegionTv.setOnClickListener(this);
        mLineContainer = findViewById(R.id.ll_line);
        mRegionStartTv = (TextView) findViewById(R.id.tv_region_start);
        mRegionStartTv.setOnClickListener(this);
        mRegionEndTv = (TextView) findViewById(R.id.tv_region_end);
        mRegionEndTv.setOnClickListener(this);
        mChoosionContainer = (LinearLayout) findViewById(R.id.ll_choosion_container);
        mIntroEt = (EditText) findViewById(R.id.et_intro);
        findViewById(R.id.btn_next).setOnClickListener(this);

        if (mSetupModel == null || mSetupModel.getRole() == null) {
            finish();
            return;
        }
        initViewByRoleType(mSetupModel.getRole().getCode());

        if (mSetupModel.getRoleRegion() != null) {
            mRegionTv.setText(mSetupModel.getRoleRegion().getGeneralName());
        }
        if (!TextUtils.isEmpty(mSetupModel.getRoleIntro())) {
            mIntroEt.setText(mSetupModel.getRoleIntro());
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        String name = "";
        if (mSetupModel != null && mSetupModel.getRole() != null) {
            name = mSetupModel.getRole().getName();
        }
        String title = "设置业务  [" + name + "]";
        return new TitleParams(getDefaultHomeAction(), title, null).setShowLogo(false);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
        case R.id.tv_region:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_2, REQUEST_CODE_REGION);
            return;
        case R.id.tv_region_start:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_2, REQUEST_CODE_LINE_START);
            return;
        case R.id.tv_region_end:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_2, REQUEST_CODE_LINE_END);
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
                    mSetupModel.getRoleAttrMap().put(dictType, dict);
                    ((TextView) v).setText(dict.getName());
                }
            });
        }
    }

    private void next() {
        if (mSetupModel.getRoleRegion() == null) {
            ToastUtils.showToast("请选择[" + "" + "]");
            return;
        }
        NetCreateRole net = new NetCreateRole(this) {
            @Override
            protected boolean onSetRequest(LogisticsReq.Builder req) {
                String accountName = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
                LogUtils.d(SetupRoleInfoActivity.this, "accountName:" + accountName);
                if (accountName == null) {
                    return false;
                }
                ProtoEBizLogistics.Builder logi = ProtoEBizLogistics.newBuilder();
                logi.setAccountName(accountName);
                logi.setIsHide(mSetupModel.getIsHide());
                logi.setName(mSetupModel.getShowName());
                logi.setLogisticsType(mSetupModel.getRole().getCode());
                logi.setLogisticsTypeName(mSetupModel.getRole().getName());
                if (mSetupModel.getUserRegion() != null) {
                    logi.setRegionCode(mSetupModel.getUserRegion().getFullCode());
                    logi.setRegionName(mSetupModel.getUserRegion().getGeneralName());
                }
                if (mSetupModel.getUserRegionInfo() != null) {
                    logi.setAddress(mSetupModel.getUserRegionInfo());
                }
                if (mSetupModel.getContactsName() != null) {
                    logi.setContact(mSetupModel.getContactsName());
                }
                if (mSetupModel.getContactsPhone() != null) {
                    logi.setMobile1(mSetupModel.getContactsPhone());
                }
                if (mSetupModel.getContactsTelephone() != null) {
                    logi.setTelephone1(mSetupModel.getContactsTelephone());
                }
                if (mSetupModel.getRoleIntro() != null) {
                    logi.setSelfIntroduction(mSetupModel.getRoleIntro());
                }
                req.setBizLogistics(logi);
                return setRequest(req);
            }
        };
        net.request(new OnNetRequestListener<CommonLogisticsResp.Builder>() {
            @Override
            public void onSuccess(CommonLogisticsResp.Builder response) {
                ToastUtils.showToast("创建成功!");
                User user = UserParser.parseSingleUser(response);
                if (user != null) {
                    if (TextUtils.isEmpty(user.getId())) {
                        ToastUtils.showToast("id is empty!");
                    }
                    UserDao.getInstance().replace(user);
                }
                Intent intent = new Intent(getApplicationContext(), UserLoginedInitActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFail(String msg) {
                ToastUtils.showToast(msg);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void initViewByRoleType(int type) {
        mRegionTabTv.setText(getRegionTabName(type));
        if (showLine(type)) {
            mLineContainer.setVisibility(View.VISIBLE);
        }
        List<Integer> dictTypes = getDictionaryType(type);
        for (int dictType : dictTypes) {
            View item = SystemUtils.inflate(R.layout.activity_setup_role_info_item);
            mChoosionContainer.addView(item);
            TextView tv_key = (TextView) item.findViewById(R.id.tv_item_key);
            TextView tv_value = (TextView) item.findViewById(R.id.tv_item_value);
            tv_key.setText(Dictionary.getDictName(dictType));
            tv_value.setOnClickListener(this);
            tv_value.setTag(dictType);
            mChoosionValueViews.add(tv_value);
        }
    }

    private String getRegionTabName(int type) {
        switch (type) {
        case Properties.LOGISTIC_TYPE_EXPRESS:
        case Properties.LOGISTIC_TYPE_COURIER:
        case Properties.LOGISTIC_TYPE_CITY_DISTRIBUTION:
        case Properties.LOGISTIC_TYPE_INSURANCE:
        case Properties.LOGISTIC_TYPE_MOVE_HOUSE:
            return "服务区域";
        default:
            return "所在地";
        }
    }

    private boolean showLine(int type) {
        switch (type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            return true;
        default:
            return false;
        }
    }

    private List<Integer> getDictionaryType(int type) {
        List<Integer> dictTypes = new ArrayList<Integer>();
        switch (type) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            dictTypes.add(Dictionary.TYPE_TRUCK_LENGTH);
            dictTypes.add(Dictionary.TYPE_TRUCK_TYPE);
            break;
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            dictTypes.add(Dictionary.TYPE_VALIDITY);
            break;
        case Properties.LOGISTIC_TYPE_INSURANCE:
            dictTypes.add(Dictionary.TYPE_INSURANCE_TYPE);
            break;
        case Properties.LOGISTIC_TYPE_PACKAGING:
            dictTypes.add(Dictionary.TYPE_PACK_TYPE);
            break;
        case Properties.LOGISTIC_TYPE_STORAGE:
            dictTypes.add(Dictionary.TYPE_DEPOT_TYPE);
            break;
        }

        return dictTypes;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Serializable extra = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (extra != null && extra instanceof RegionResult) {
                RegionResult result = (RegionResult) extra;
                if (requestCode == REQUEST_CODE_REGION) {
                    mSetupModel.setRoleRegion(result);
                    mRegionTv.setText(result.getGeneralName());
                } else if (requestCode == REQUEST_CODE_LINE_START) {
                    mSetupModel.setRoleLineStart(result);
                    mRegionStartTv.setText(result.getGeneralName());
                } else if (requestCode == REQUEST_CODE_LINE_END) {
                    mSetupModel.setRoleLineEnd(result);
                    mRegionEndTv.setText(result.getGeneralName());
                }
            }
        }
    }

    private boolean setRequest(LogisticsReq.Builder req) {
        ProtoEBizLogistics.Builder logi = req.getBizLogisticsBuilder();
        switch (mSetupModel.getRole().getCode()) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            if (mSetupModel.getRoleLineStart() != null) {
                logi.setRouteCodeA(mSetupModel.getRoleLineStart().getFullCode());
                logi.setRouteNameA(mSetupModel.getRoleLineStart().getGeneralName());
            }
            if (mSetupModel.getRoleLineEnd() != null) {
                logi.setRouteCodeB(mSetupModel.getRoleLineEnd().getFullCode());
                logi.setRouteNameB(mSetupModel.getRoleLineEnd().getGeneralName());
            }
            if (mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_TRUCK_LENGTH) != null) {
                logi.setVehicleLengthCode(mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_TRUCK_LENGTH).getId());
                logi.setVehicleLengthName(mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_TRUCK_LENGTH).getName());
            }
            if (mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_TRUCK_TYPE) != null) {
                logi.setVehicleType(mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_TRUCK_TYPE).getId());
                logi.setVehicleTypeName(mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_TRUCK_TYPE).getName());
            }
            return true;
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
            if (mSetupModel.getRoleLineStart() != null) {
                logi.setRouteCodeA(mSetupModel.getRoleLineStart().getFullCode());
                logi.setRouteNameA(mSetupModel.getRoleLineStart().getGeneralName());
            }
            if (mSetupModel.getRoleLineEnd() != null) {
                logi.setRouteCodeB(mSetupModel.getRoleLineEnd().getFullCode());
                logi.setRouteNameB(mSetupModel.getRoleLineEnd().getGeneralName());
            }
            if (mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_VALIDITY) != null) {
                logi.setPeriodOfValidity(mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_VALIDITY).getId());
                logi.setPeriodOfValidityDesc(mSetupModel.getRoleAttrMap().get(Dictionary.TYPE_VALIDITY).getName());
            }
            return true;
        default:
            // ToastUtils.showToastInThread("不支持该类型角色！");
            return true;
        }
    }

}
