package com.epeisong.ui.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.FreightResp.Builder;
import com.epeisong.model.Dictionary;
import com.epeisong.model.Freight;
import com.epeisong.model.RegionResult;
import com.epeisong.net.request.NetPublishFreight;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.ui.view.SwitchButton;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 发布车源
 * 
 * @author poet
 * 
 */
public class PublishTruckActivity extends BaseActivity implements OnItemSelectedListener, OnClickListener {

    private class MyWatcher implements TextWatcher {
        private int watchedId;

        public MyWatcher(int id) {
            this.watchedId = id;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (watchedId == R.id.et_start_place || watchedId == R.id.et_end_place) {
                mIsPlaceOk = previewPlace();
            } else if (watchedId == R.id.et_spare_meter || watchedId == R.id.et_ton || watchedId == R.id.et_square) {
                mIsDescOk = previewDesc();
            }
            // else if(watchedId == R.id.spinner_truck_meter || watchedId ==
            // R.id.spinner_truck_type){
            // mIsDescOk = previewDesc();
            // }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private TextView mStartPlaceEt;
    private TextView mEndPlaceEt;
    private TextView mTruckMeterSp;
    private TextView mTruckTypeSp;
    private EditText mSpareMeterEt;
    private EditText mTonEt;
    private EditText mSquareEt;

    private TextView mTimeTv;
    private TextView mPlaceTv;
    private TextView mDescTv;
    private TextView mContactTv;
    private TextView mContactPhone;
    private TextView mPublicUser;

    private TextView mContactTel;
    private Button mPublishBtn;

    private boolean mIsPlaceOk;
    private boolean mIsDescOk;

    private int mStartRegionCode;
    private int mEndRegionCode;
    private SwitchButton switch_button;

    private String mTruckMeter;
    private String mTruckType;
    private int mTruckTypeCode;
    private int mTruckMeterCode;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.et_start_place:
            Intent start = new Intent(getApplicationContext(), ChooseRegionActivity.class);
            start.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
            start.putExtra(ChooseRegionActivity.EXTRA_IS_SHOW_COUNTRY, false);
            startActivityForResult(start, 100);
            break;
        case R.id.et_end_place:
            Intent end = new Intent(getApplicationContext(), ChooseRegionActivity.class);
            end.putExtra(ChooseRegionActivity.EXTRA_IN_FILTER, ChooseRegionActivity.FILTER_0_3);
            startActivityForResult(end, 200);
            break;
        case R.id.root:
            SystemUtils.hideInputMethod(mTonEt);
            break;
        default:
            break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mIsDescOk = previewDesc();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initView() {
        switch_button = (SwitchButton) findViewById(R.id.switch_button);
        switch_button.setSwitchText("是", "否", true);

        mStartPlaceEt = (TextView) findViewById(R.id.et_start_place);
        mStartPlaceEt.setOnClickListener(this);
        mEndPlaceEt = (TextView) findViewById(R.id.et_end_place);
        mEndPlaceEt.setOnClickListener(this);
        mTruckMeterSp = (TextView) findViewById(R.id.spinner_truck_meter);
        mTruckTypeSp = (TextView) findViewById(R.id.spinner_truck_type);
        mSpareMeterEt = (EditText) findViewById(R.id.et_spare_meter);
        mTonEt = (EditText) findViewById(R.id.et_ton);
        mSquareEt = (EditText) findViewById(R.id.et_square);
        mTimeTv = (TextView) findViewById(R.id.tv_public_time);
        mContactPhone = (TextView) findViewById(R.id.tv_contact_phone);
        mContactTel = (TextView) findViewById(R.id.tv_contact_tel);
        mPublicUser = (TextView) findViewById(R.id.tv_public_user);
        // mTruckDefault.setId(-1);
        // mTruckDefault.setName("各种车型");
        // mTruckTypeCode = mTruckDefault.getId();
        // mTruckType = mTruckDefault.getName();
        // mTruckTypeSp.setText(mTruckType);
        // mTruckMeterDefault.setId(0);
        // mTruckMeterDefault.setName("3");
        // mTruckMeterCode = mTruckMeterDefault.getId();
        // mTruckMeter = mTruckMeterDefault.getName();
        // mTruckMeterSp.setText(mTruckMeter);
        // mIsDescOk = previewDesc();
        mTruckMeterSp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                List<Dictionary> data = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_LENGTH);
                // Dictionary dict = new Dictionary();
                // dict.setId(-1);
                // dict.setName("各种车长");
                // data.add(0, dict);
                showDictionaryListDialog("选择车长", data, new OnChooseDictionaryListener() {
                    @Override
                    public void onChoosedDictionary(Dictionary item) {
                        // ToastUtils.showToast(item.getName() + " - " +
                        // item.getId());
                        mTruckMeterSp.setText(item.getName());
                        mTruckMeter = item.getName();
                        mTruckMeterCode = item.getId();
                        mIsDescOk = previewDesc();
                    }
                });
            }
        });
        mTruckTypeSp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                List<Dictionary> data = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_TYPE);
                Dictionary dict = new Dictionary();
                // dict.setId(-1);
                // dict.setName("各种车型");
                // data.add(0, dict);
                showDictionaryListDialog("选择车型", data, new OnChooseDictionaryListener() {
                    @Override
                    public void onChoosedDictionary(Dictionary item) {
                        // ToastUtils.showToast(item.getName() + " - " +
                        // item.getId());
                        mTruckTypeSp.setText(item.getName());
                        mTruckType = item.getName();
                        mTruckTypeCode = item.getId();
                        mIsDescOk = previewDesc();
                    }
                });
            }
        });
        mStartPlaceEt.addTextChangedListener(new MyWatcher(R.id.et_start_place));
        mEndPlaceEt.addTextChangedListener(new MyWatcher(R.id.et_end_place));
        mSpareMeterEt.addTextChangedListener(new MyWatcher(R.id.et_spare_meter));
        mTonEt.addTextChangedListener(new MyWatcher(R.id.et_ton));
        mSquareEt.addTextChangedListener(new MyWatcher(R.id.et_square));
        // mTruckMeterSp.addTextChangedListener(new
        // MyWatcher(R.id.spinner_truck_meter));
        // mTruckTypeSp.addTextChangedListener(new
        // MyWatcher(R.id.spinner_truck_type));

        mPlaceTv = (TextView) findViewById(R.id.tv_place);
        mDescTv = (TextView) findViewById(R.id.tv_desc);
        mContactTv = (TextView) findViewById(R.id.tv_contact);

        mTimeTv.setText(DateUtil.long2YMDHM(System.currentTimeMillis()));
        mPublicUser.setText(UserDao.getInstance().getUser().getShow_name());
        mContactTv.setText(UserDao.getInstance().getUser().getContacts_name());
        mContactPhone.setText(UserDao.getInstance().getUser().getContacts_phone());
        mContactTel.setText(UserDao.getInstance().getUser().getContacts_telephone());

        ImageView iv = (ImageView) findViewById(R.id.iv_type);
        iv.setImageResource(R.drawable.black_board_truck);
        mPublishBtn = (Button) findViewById(R.id.btn_publish);
        mPublishBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = mStartPlaceEt.getText().toString();
                String end = mEndPlaceEt.getText().toString();
                String spareMeter = mSpareMeterEt.getText().toString();
                String ton = mTonEt.getText().toString();
                String square = mSquareEt.getText().toString();
                // if (mIsPlaceOk && mIsDescOk) {
                if (!TextUtils.isEmpty(start)
                        && !TextUtils.isEmpty(end)
                        && (!TextUtils.isEmpty(mTruckMeter) || !TextUtils.isEmpty(spareMeter)
                                || !TextUtils.isEmpty(ton) || !TextUtils.isEmpty(square))) {
                    final Freight d = new Freight();
                    d.setUser_id(UserDao.getInstance().getUser().getId());
                    d.setUser(UserDao.getInstance().getUser());
                    d.setType(Freight.TYPE_TRUCK);
                    d.setCreate_time(System.currentTimeMillis());
                    d.setStart_region(mStartPlaceEt.getText().toString());
                    d.setStart_region_code(mStartRegionCode);
                    d.setEnd_region(mEndPlaceEt.getText().toString());
                    d.setEnd_region_code(mEndRegionCode);
                    d.setTruck_type_name(mTruckType);
                    d.setTruck_type_code(mTruckTypeCode);

                    boolean bOn = switch_button.isOpen();
                    int distribution_market = bOn ? Properties.FREIGHT_POST_TO_MARKET_SCREEN
                            : Properties.FREIGHT_NOT_POST_TO_MARKET_SCREEN;
                    d.setDistribution_market(distribution_market);
                    // TODO truck_type_code
                    if (TextUtils.isEmpty(ton)) {
                        d.setGoods_ton(0);
                    } else {
                        d.setGoods_ton(Float.parseFloat(ton));
                    }
                    if (TextUtils.isEmpty(square)) {
                        d.setGoods_square(0);
                    } else {
                        d.setGoods_square((int) Float.parseFloat(square));
                    }
                    if (TextUtils.isEmpty(mTruckMeter)) {
                        d.setTruck_length_code(0);
                        d.setTruck_length_name("");
                    } else {
                        d.setTruck_length_code(mTruckMeterCode);
                        d.setTruck_length_name(mTruckMeter);
                    }
                    if (TextUtils.isEmpty(spareMeter)) {
                        d.setTruck_spare_meter(0);
                    } else {
                        d.setTruck_spare_meter((int) (Float.parseFloat(spareMeter)*10));
                    }
                    if (TextUtils.isEmpty(mTruckType)) {
                        d.setTruck_type_code(0);
                        d.setTruck_type_name("");
                    } else {
                        d.setTruck_type_name(mTruckType);
                        d.setTruck_type_code(mTruckTypeCode);
                    }
                    NetPublishFreight net = new NetPublishFreight(PublishTruckActivity.this, d);
                    net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
                        @Override
                        public void onSuccess(Builder response) {
                            d.setId(String.valueOf(response.getFreightId()));
                            d.setCreate_time(response.getCreateDate());
                            d.setStatus(Properties.FREIGHT_STATUS_NO_PROCESSED);
                            d.setOwner_name(UserDao.getInstance().getUser().getShow_name());
                            // DispatchManager.getInstance().insert(d);
                            ToastUtils.showToast("发布成功");

                            Intent data = new Intent();
                            data.putExtra("todayCount", response.getFreightCountOfTodatyOnBlackBoard());
                            data.putExtra("totalCount", response.getFreightCountOnBlackBoard());
                            data.putExtra(BlackBoardActivity.EXTRA_PUBLISH_DISPATCH, d);
                            setResult(Activity.RESULT_OK, data);
                            finish();
                        }
                    });
                } else {
                    ToastUtils.showToast("请填写完整信息！");
                }
            }
        });
    }

    private boolean previewDesc() {
        String truckMeter = mTruckMeter;
        String truckType = mTruckType;
        String spareMeter = mSpareMeterEt.getText().toString();
        String ton = mTonEt.getText().toString();
        String square = mSquareEt.getText().toString();
        String desc = "";

        if (!TextUtils.isEmpty(truckMeter)) {
            desc = truckMeter + " ";
        }
        if (!TextUtils.isEmpty(truckType)) {
            desc += truckType + " ";
        }
        if (!TextUtils.isEmpty(spareMeter)) {
            desc += "还空" + spareMeter + "米位 ";
        }
        if (!TextUtils.isEmpty(ton)) {
            desc += "需" + ton + "吨 ";
        }
        if (!TextUtils.isEmpty(square)) {
            if (!TextUtils.isEmpty(ton)) {
                desc += square + "方";
            } else {
                desc += "需" + square + "方";
            }
        }
        mDescTv.setText(desc);
        mDescTv.setTextColor(getResources().getColor(R.color.light_gray));
        return true;
    }

    private boolean previewPlace() {
        String start = mStartPlaceEt.getText().toString();
        String end = mEndPlaceEt.getText().toString();
        if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            mPlaceTv.setText("地址填写不完整");
            mPlaceTv.setTextColor(Color.RED);
            return false;
        } else {
            mPlaceTv.setText(start + " - " + end);
            mPlaceTv.setTextColor(Color.BLACK);
            return true;
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "发布车源", null).setShowLogo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
            if (requestCode == 100) {
                mStartPlaceEt.setText(result.getShortNameFromDistrict());
                mStartRegionCode = result.getFullCode();
            } else if (requestCode == 200) {
                mEndPlaceEt.setText(result.getShortNameFromDistrict());
                mEndRegionCode = result.getFullCode();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_truck);
        findViewById(R.id.root).setOnClickListener(this);
        initView();
    }

}
