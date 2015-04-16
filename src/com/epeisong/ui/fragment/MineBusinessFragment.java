package com.epeisong.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.dialog.ChooseLineActivity;
import com.epeisong.base.fragment.PendingFragment;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.UserDao.UserObserver;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsUpdate;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.Dictionary;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.ui.activity.EditAddressActivity;
import com.epeisong.ui.activity.SimpleEditActivity;
import com.epeisong.ui.view.SwitchButton;
import com.epeisong.ui.view.SwitchButton.OnSwitchListener;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 我的基本信息-业务
 * @author poet
 *
 */
public class MineBusinessFragment extends PendingFragment implements OnItemClickListener, OnClickListener,
        OnChooseDictionaryListener, OnSwitchListener, UserObserver {

    private static final int REQUEST_CODE_CHOOSE_REGION = 101;
    private static final int REQUEST_CODE_CHOOSE_LINE = 102;

    private MyAdapter mAdapter;

    private ListView mListView;
    private TextView mIntroTv;
    private TextView mContactsNameTv;
    private TextView mContactsPhoneTv;
    private TextView mContactsTelephoneTv;
    private View mContactsAddressContainer;
    private TextView mContactsAddressTv;
    private TextView mContactsAddressInfoTv;
    private View mProviderLocContainer;
    private SwitchButton mSwitchButton;
    private TextView mProviedLocInfoTv;

    private User mUser;

    private XBaseActivity mXBaseActivity;

    @Override
    protected View onCreateContentView(LayoutInflater inflater) {
        mListView = new ListView(getActivity());
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setOnItemClickListener(this);
        View bottom = SystemUtils.inflate(R.layout.fragment_mine_business_bottom);
        mListView.addFooterView(bottom, null, false);
        bottom.findViewById(R.id.rl_desc).setOnClickListener(this);
        bottom.findViewById(R.id.rl_contacts_name).setOnClickListener(this);
        bottom.findViewById(R.id.rl_contacts_phone).setOnClickListener(this);
        bottom.findViewById(R.id.rl_contacts_telephone).setOnClickListener(this);
        mContactsAddressContainer = bottom.findViewById(R.id.rl_contacts_address);
        mContactsAddressContainer.setOnClickListener(this);
        mIntroTv = (TextView) bottom.findViewById(R.id.tv_desc);
        mContactsNameTv = (TextView) bottom.findViewById(R.id.tv_contacts_name);
        mContactsPhoneTv = (TextView) bottom.findViewById(R.id.tv_contacts_phone);
        mContactsTelephoneTv = (TextView) bottom.findViewById(R.id.tv_contacts_telephone);
        mContactsAddressTv = (TextView) bottom.findViewById(R.id.tv_contacts_address);
        mContactsAddressInfoTv = (TextView) bottom.findViewById(R.id.tv_contacts_address_info);
        mProviderLocContainer = bottom.findViewById(R.id.rl_provider_loc);
        mProviedLocInfoTv = (TextView) bottom.findViewById(R.id.tv_provided_loc);
        mSwitchButton = (SwitchButton) bottom.findViewById(R.id.sb);
        mSwitchButton.setSwitchText("工作", "休息", false);
        mSwitchButton.setOnSwitchListener(this);
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
        UserDao.getInstance().addObserver(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mXBaseActivity = (XBaseActivity) activity;
    }

    @Override
    public void onUserChange(User user) {
        mUser = user;
        fillData(mUser);
    }

    @Override
    public void onSwitch(SwitchButton btn, final boolean on) {
        if (on) {
            SpUtilsCur.put(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 1);
            mSwitchButton.setSwitch(true);
            // EpsLocationRequestor requestor = new EpsLocationRequestor();
            // requestor.requestEpsLocation(new OnEpsLocationListener() {
            // @Override
            // public void onEpsLocation(EpsLocation epsLocation) {
            // if (epsLocation == null) {
            // ToastUtils.showToast("定位失败，请连接网络");
            // } else {
            // uploadLocation(epsLocation);
            // }
            // }
            // });
        } else {
            SpUtilsCur.put(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 2);
            mSwitchButton.setSwitch(false);
            if (false) {
                mXBaseActivity.showPendingDialog(null);
                AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        NetLogisticsUpdate net = new NetLogisticsUpdate() {
                            @Override
                            protected int getCommandCode() {
                                return CommandConstants.CLEAR_CURRENT_LOCATION_INFO_REQ;
                            }

                            @Override
                            protected boolean onSetRequest(LogisticsReq.Builder req) {
                                req.setLogisticsId(Integer.parseInt(mUser.getId()));
                                return true;
                            }
                        };
                        try {
                            Builder resp = net.request();
                            return net.isSuccess(resp);
                        } catch (NetGetException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        mXBaseActivity.dismissPendingDialog();
                        if (result) {
                            SpUtilsCur.put(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 2);
                            mUser.setRegion(null);
                            mUser.setAddress(null);
                            mUser.getUserRole().setCurrent_longitude(0);
                            mUser.getUserRole().setCurrent_latitude(0);
                            UserDao.getInstance().replace(mUser);
                        } else {
                            ToastUtils.showToast("操作失败");
                        }
                    }
                };
                task.execute();
            }
        }
    }

    void uploadLocation(final EpsLocation loc) {
        mXBaseActivity.showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Builder resp = NetLogisticsUpdate.updateLocation(loc, Properties.IS_REALTIME_COURIER);
                return resp != null && resp.getResult().equals(Constants.SUCC);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mXBaseActivity.dismissPendingDialog();
                if (!result) {
                    ToastUtils.showToast("操作失败");
                }
            }
        };
        task.execute();
    }

    private void loadData() {
        AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                return UserDao.getInstance().getUser();
            }

            @Override
            protected void onPostExecute(User result) {
                if (result != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", result);
                    endPending(true, bundle, null);
                } else {
                    endPending(false, null, new OnPendingAgainListener() {
                        @Override
                        public void onPendingAgain() {
                            loadData();
                        }
                    });
                }
            }
        };
        task.execute();
    }

    @Override
    protected void onPendingSuccess(Bundle bundle) {
        User user = (User) bundle.getSerializable("user");
        mUser = user;
        fillData(user);
    }

    private void fillData(User user) {
        List<Attr> attrs = new ArrayList<MineBusinessFragment.Attr>();
        attrs.add(new Attr(Attr.type_name, "名称", user.getShow_name()));
        attrs.add(new Attr(Attr.type_role, "类别", user.getUser_type_name()));
        UserRole role = user.getUserRole();
        String regionName = "";
        if (role != null) {
            regionName = role.getRegionName();
        }
        attrs.add(new Attr(Attr.type_location, UserRole.getRegionCalled(user.getUser_type_code()), regionName));
        if (role != null) {
            if (isShowLine(user.getUser_type_code())) {
                attrs.add(new Attr(Attr.type_line, "线路", role.getline()));
            }
            switch (user.getUser_type_code()) {
            case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
            case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
                attrs.add(new Attr(Attr.type_truck_type, "车型", role.getTruckTypeName()));
                attrs.add(new Attr(Attr.type_truck_length, "车长", role.getTruckLengthName()));
                attrs.add(new Attr(Attr.type_load_ton, "核载", role.getLoadTon() > 0 ? (role.getLoadTon() + "吨") : ""));
                attrs.add(new Attr(Attr.type_goods_type, "货物类型", role.getGoodsTypeName()));
                break;
            case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT:
                break;
            case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
                attrs.add(new Attr(Attr.type_validity, "时效", role.getValidityName()));
                attrs.add(new Attr(Attr.type_goods_type, "货物类型", role.getGoodsTypeName()));
                break;
            case Properties.LOGISTIC_TYPE_INSURANCE:
                attrs.add(new Attr(Attr.type_insurance_type, "险种", role.getInsuranceName()));
                break;
            case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
                attrs.add(new Attr(Attr.type_device_type, "设备类别", role.getDeviceName()));
                break;
            default:
                break;
            }
        }

        if (mAdapter == null) {
            mAdapter = new MyAdapter();
            mListView.setAdapter(mAdapter);
        }
        mAdapter.replaceAll(attrs);

        String intro = user.getSelf_intro();
        mIntroTv.setText(intro);
        mContactsNameTv.setText(user.getContacts_name());
        mContactsPhoneTv.setText(user.getContacts_phone());
        mContactsTelephoneTv.setText(user.getContacts_telephone());

        switch (user.getUser_type_code()) {
        case Properties.LOGISTIC_TYPE_COURIER: // 快递员
            // break;
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE: // 整车运输
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS: // 驳货
            mProviderLocContainer.setVisibility(View.VISIBLE);
            if (SpUtilsCur.getInt(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 0) == 1) {
                mSwitchButton.setSwitch(true);
                // mProviedLocInfoTv.setVisibility(View.VISIBLE);
                // mProviedLocInfoTv.setText(user.getRegion() +
                // user.getAddress());
            } else {
                mSwitchButton.setSwitch(false);
                mProviedLocInfoTv.setVisibility(View.GONE);
            }
            break;
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING: // 设备租赁
        default:
            mContactsAddressContainer.setVisibility(View.VISIBLE);
            mContactsAddressTv.setText(user.getRegion());
            mContactsAddressInfoTv.setText(user.getAddress());
            break;
        }
    }

    private boolean isShowLine(int roleType) {
        switch (roleType) {
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_desc:
            simpleEdit(SimpleEditActivity.WHAT_INTRODUCTION, mUser.getSelf_intro());
            break;
        case R.id.rl_contacts_name:
            simpleEdit(SimpleEditActivity.WHAT_CONTACTS_NAME, mUser.getContacts_name());
            break;
        case R.id.rl_contacts_phone:
            simpleEdit(SimpleEditActivity.WHAT_CONTACTS_PHONE, mUser.getContacts_phone());
            break;
        case R.id.rl_contacts_telephone:
            simpleEdit(SimpleEditActivity.WHAT_CONTACTS_TELEPHONE, mUser.getContacts_telephone());
            break;
        case R.id.rl_contacts_address:
            Intent intent = new Intent(getActivity(), EditAddressActivity.class);
            intent.putExtra(EditAddressActivity.EXTRA_ORIGINAL_REGION_CODE, mUser.getRegion_code());
            intent.putExtra(EditAddressActivity.EXTRA_ORIGINAL_REGION_NAME, mUser.getRegion());
            intent.putExtra(EditAddressActivity.EXTRA_ORIGINAL_ADDRESS, mUser.getAddress());
            intent.putExtra("user", mUser);
            startActivity(intent);
            break;
        }
    }

    private void simpleEdit(int extra_what, String extra_what_data) {
        Intent intent = new Intent(getActivity(), SimpleEditActivity.class);
        intent.putExtra(SimpleEditActivity.EXTRA_WHAT, extra_what);
        intent.putExtra(SimpleEditActivity.EXTRA_WHAT_DATA, extra_what_data);
        intent.putExtra("user", mUser);
        startActivity(intent);
    }

    private void updateRole(final int attrType, final UserRole role) {
        if (mXBaseActivity == null) {
            mXBaseActivity = (XBaseActivity) getActivity();
        }
        mXBaseActivity.showPendingDialog(null);
        final UserRole temp = mUser.getUserRole().cloneSelf();
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetLogisticsUpdate net = new NetLogisticsUpdate() {
                    @Override
                    protected boolean onSetRequestParams(ProtoEBizLogistics.Builder logi) {
                        logi.setLoadType(mUser.getUser_type_code());
                        switch (attrType) {
                        case Attr.type_location:
                            logi.setServeRegionCode(role.getRegionCode());
                            logi.setServeRegionName(role.getRegionName());
                            temp.setRegionCode(role.getRegionCode());
                            temp.setRegionName(role.getRegionName());
                            break;
                        case Attr.type_line:
                            logi.setRouteCodeA(role.getLineStartCode());
                            logi.setRouteNameA(role.getLineStartName());
                            logi.setRouteCodeB(role.getLineEndCode());
                            logi.setRouteNameB(role.getLineEndName());
                            temp.setLineStartCode(role.getLineStartCode());
                            temp.setLineStartName(role.getLineStartName());
                            temp.setLineEndCode(role.getLineEndCode());
                            temp.setLineEndName(role.getLineEndName());
                            break;
                        case Attr.type_truck_length:
                            logi.setVehicleLengthCode(role.getTruckLengthCode());
                            logi.setVehicleLengthName(role.getTruckLengthName());
                            temp.setTruckLengthCode(role.getTruckLengthCode());
                            temp.setTruckLengthName(role.getTruckLengthName());
                            break;
                        case Attr.type_truck_type:
                            logi.setVehicleType(role.getTruckTypeCode());
                            logi.setVehicleTypeName(role.getTruckTypeName());
                            temp.setTruckTypeCode(role.getTruckTypeCode());
                            temp.setTruckTypeName(role.getTruckTypeName());
                            break;
                        case Attr.type_goods_type:
                            logi.setGoodsType(role.getGoodsTypeCode());
                            logi.setGoodsTypeName(role.getGoodsTypeName());
                            temp.setGoodsTypeCode(role.getGoodsTypeCode());
                            temp.setGoodsTypeName(role.getGoodsTypeName());
                            break;
                        case Attr.type_load_ton:
                            logi.setMaxKilogram((int) (role.getLoadTon() * 1000));
                            temp.setLoadTon(role.getLoadTon());
                            break;
                        case Attr.type_validity:
                            logi.setPeriodOfValidity(role.getValidityCode());
                            logi.setPeriodOfValidityDesc(role.getValidityName());
                            temp.setValidityCode(role.getValidityCode());
                            temp.setValidityName(role.getValidityName());
                            break;
                        case Attr.type_insurance_type:
                            logi.setInsuranceType(role.getInsuranceCode());
                            logi.setInsuranceTypeName(role.getInsuranceName());
                            temp.setInsuranceCode(role.getInsuranceCode());
                            temp.setInsuranceName(role.getInsuranceName());
                            break;
                        case Attr.type_device_type:
                            logi.setEquipmentType(role.getDeviceCode());
                            logi.setEquipmentTypeName(role.getDeviceName());
                            temp.setDeviceCode(role.getDeviceCode());
                            temp.setDeviceName(role.getDeviceName());
                            break;
                        case Attr.type_depot_type:
                            logi.setStorageType(role.getDepotCode());
                            logi.setStorageTypeName(role.getDepotName());
                            temp.setDepotCode(role.getDepotCode());
                            temp.setDepotName(role.getDepotName());
                            break;
                        case Attr.type_pack_type:
                            logi.setPackageType(role.getPackCode());
                            logi.setPackageTypeName(role.getPackName());
                            temp.setPackCode(role.getPackCode());
                            temp.setPackName(role.getPackName());
                            break;
                        default:
                            return false;
                        }
                        return true;

                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    return net.isSuccess(resp);
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mXBaseActivity.dismissPendingDialog();
                if (result != null) {
                    if (result) {
                        mUser.setUserRole(temp);
                        UserDao.getInstance().replace(mUser);
                        fillData(mUser);
                        ToastUtils.showToast("修改成功");
                    }
                }
            }
        };
        task.execute();
    }

    @Override
    public void onChoosedDictionary(Dictionary dict) {
        int attrType = -1;
        UserRole role = new UserRole();
        int code = dict.getId();
        String name = dict.getName();
        switch (dict.getType()) {
        case Dictionary.TYPE_TRUCK_TYPE:
            attrType = Attr.type_truck_type;
            role.setTruckTypeCode(code);
            role.setTruckTypeName(name);
            break;
        case Dictionary.TYPE_TRUCK_LENGTH:
            attrType = Attr.type_truck_length;
            role.setTruckLengthCode(code);
            role.setTruckLengthName(name);
            break;
        case Dictionary.TYPE_GOODS_TYPE:
            attrType = Attr.type_goods_type;
            role.setGoodsTypeCode(code);
            role.setGoodsTypeName(name);
            break;
        case Dictionary.TYPE_VALIDITY:
            attrType = Attr.type_validity;
            role.setValidityCode(code);
            role.setValidityName(name);
            break;
        case Dictionary.TYPE_INSURANCE_TYPE:
            attrType = Attr.type_insurance_type;
            role.setInsuranceCode(code);
            role.setInsuranceName(name);
            break;
        case Dictionary.TYPE_DEVICE_TYPE:
            attrType = Attr.type_device_type;
            role.setDeviceCode(code);
            role.setDeviceName(name);
            break;
        default:
            return;
        }
        if (attrType == -1) {
            return;
        }
        updateRole(attrType, role);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (REQUEST_CODE_CHOOSE_REGION == requestCode) {
                Serializable extra = data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                if (extra == null || !(extra instanceof RegionResult)) {
                    return;
                }
                final RegionResult regionResult = (RegionResult) extra;
                UserRole role = new UserRole();
                role.setRegionCode(regionResult.getFullCode());
                role.setRegionName(regionResult.getShortNameFromDistrict());
                updateRole(Attr.type_location, role);
            } else if (REQUEST_CODE_CHOOSE_LINE == requestCode) {
                Serializable extra1 = data.getSerializableExtra(ChooseLineActivity.EXTRA_START_REGION);
                Serializable extra2 = data.getSerializableExtra(ChooseLineActivity.EXTRA_END_REGION);
                if (extra1 != null && extra2 != null) {
                    RegionResult start = (RegionResult) extra1;
                    RegionResult end = (RegionResult) extra2;
                    UserRole role = new UserRole();
                    role.setLineStartCode(start.getFullCode());
                    role.setLineStartName(start.getShortNameFromDistrict());
                    role.setLineEndCode(end.getFullCode());
                    role.setLineEndName(end.getShortNameFromDistrict());
                    updateRole(Attr.type_line, role);
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Attr attr = mAdapter.getItem(position);
        switch (attr.type) {
        case Attr.type_name:
            simpleEdit(SimpleEditActivity.WHAT_SHOW_NAME, mUser.getShow_name());
            break;
        case Attr.type_location:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_CHOOSE_REGION);
            break;
        case Attr.type_line:
            Bundle extrasForChooseRegion = new Bundle();
            extrasForChooseRegion.putInt(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
            if (mUser.getUser_type_code() == Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE) {
                extrasForChooseRegion.putBoolean(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY, false);
            }
            ChooseLineActivity.launch(this, REQUEST_CODE_CHOOSE_LINE, true, extrasForChooseRegion);
            break;
        case Attr.type_truck_length:
            List<Dictionary> data = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_LENGTH);
            int truckLengthCode = mUser.getUserRole().getTruckLengthCode();
            ((XBaseActivity) getActivity()).showDictionaryListDialogMulti("选择车长", data, truckLengthCode, this);
            break;
        case Attr.type_truck_type:
            List<Dictionary> truckType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_TYPE);
            int truckTypeCode = mUser.getUserRole().getTruckTypeCode();
            ((XBaseActivity) getActivity()).showDictionaryListDialogMulti("选择车型", truckType, truckTypeCode, this);
            break;
        case Attr.type_goods_type:
            List<Dictionary> goodType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_GOODS_TYPE);
            int goodsTypeCode = mUser.getUserRole().getGoodsTypeCode();
            ((XBaseActivity) getActivity()).showDictionaryListDialogMulti("选择货物类型", goodType, goodsTypeCode, this);
            break;
        case Attr.type_load_ton:
            final EditText et = new EditText(mXBaseActivity);
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setBackgroundResource(R.drawable.shape_content_white);
            et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            et.setTextColor(Color.BLACK);
            if (mUser.getUserRole().getLoadTon() != 0) {
                et.setText(String.valueOf(mUser.getUserRole().getLoadTon()));
                et.setSelectAllOnFocus(true);
            }
            int p = DimensionUtls.getPixelFromDpInt(10);
            FrameLayout fl = new FrameLayout(mXBaseActivity);
            fl.setPadding(p, p, p, p);
            fl.addView(et);
            mXBaseActivity.showYesNoDialog("修改核载：单位吨", fl, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        String s = et.getText().toString();
                        if (TextUtils.isEmpty(s)) {
                            ToastUtils.showToast("请输入核载");
                        } else {
                            UserRole role = new UserRole();
                            role.setLoadTon(Float.parseFloat(s));
                            updateRole(Attr.type_load_ton, role);
                        }
                    }
                }
            });
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SystemUtils.showInputMethod(et);
                }
            }, 300);
            break;
        case Attr.type_validity:
            List<Dictionary> validityData = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_VALIDITY);
            ((XBaseActivity) getActivity()).showDictionaryListDialog("选择时效", validityData, this);
            break;
        case Attr.type_insurance_type:
            List<Dictionary> insuranceData = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_INSURANCE_TYPE);
            ((XBaseActivity) getActivity()).showDictionaryListDialog("选择险种", insuranceData, this);
            break;
        case Attr.type_device_type:
            List<Dictionary> deviceData = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_DEVICE_TYPE);
            ((XBaseActivity) getActivity()).showDictionaryListDialog("选择设备类别", deviceData, this);
            break;
        default:
            break;
        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<Attr> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                // TODO
                convertView = SystemUtils.inflate(R.layout.fragment_mine_business_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_value;

        public void findView(View v) {
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_value = (TextView) v.findViewById(R.id.tv_value);
        }

        public void fillData(Attr attr) {
            if (attr.type == Attr.type_role) {
                tv_name.setTextColor(Color.argb(0xff, 0xb2, 0xb9, 0xc3));
                tv_value.setTextColor(Color.argb(0xff, 0xb2, 0xb9, 0xc3));
            } else {
                tv_name.setTextColor(Color.BLACK);
                tv_value.setTextColor(Color.argb(0xff, 0x89, 0x8e, 0x96));
            }
            tv_name.setText(attr.getName());
            tv_value.setText(attr.getValue());
        }
    }

    public static class Attr {

        public static final int type_name = 1; // 角色名称
        public static final int type_role = 2; // 角色类别
        public static final int type_location = 3; // 所在地
        public static final int type_line = 4; // 线路
        public static final int type_validity = 5; // 时效
        public static final int type_goods_type = 6; // 货物类型
        public static final int type_truck_type = 7; // 车类型
        public static final int type_truck_length = 8; // 车长
        public static final int type_insurance_type = 9; // 险种
        public static final int type_device_type = 10; // 设备类别
        public static final int type_depot_type = 11; // 仓库类别
        public static final int type_pack_type = 12; // 包装类别
        public static final int type_load_ton = 13; // 荷载

        private int type;
        private String name;
        private String value;

        public Attr() {

        }

        public Attr(int type, String name, String value) {
            super();
            this.type = type;
            this.name = name;
            this.value = value;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
