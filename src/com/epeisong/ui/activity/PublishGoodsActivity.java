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
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.Properties;
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
 * 发布货源 TODO canPostScreen
 * 
 * @author poet
 * 
 */
public class PublishGoodsActivity extends BaseActivity implements OnItemSelectedListener, OnClickListener {

    private class MyWatcher implements TextWatcher {
        private int watchedId;

        public MyWatcher(int id) {
            this.watchedId = id;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (watchedId == R.id.et_start_place || watchedId == R.id.et_end_place) {
                mIsPlaceOk = previewPlace();
            } else if (watchedId == R.id.et_ton || watchedId == R.id.et_square) {
                // TODO
                mIsDescOk = previewDesc();
            }
            // else if(watchedId == R.id.spinner_truck_meter || watchedId ==
            // R.id.spinner_truck_type || watchedId == R.id.spinner_goods_exceed
            // || watchedId == R.id.spinner_goods_type){
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
    private EditText mTonEt;
    private EditText mSquareEt;
    private TextView mGoodsExceedSp;
    private TextView mGoodsTypeSp;
    private TextView mTruckMeterSp;
    private TextView mTruckTypeSp;

    private String mGoodsExceed;
    private String mTruckMeter;
    private int mTruckMeterCode;
    private String mTruckType;
    private int mTruckTypeCode;
    private String mGoodsType;
    private int mGoodsTypeCode;
    private int mGoodsExceedCode;
    private Dictionary mTruckDefault = new Dictionary();
    private Dictionary mGoodsDefault = new Dictionary();
    private Dictionary mTruckMeterDefault = new Dictionary();
    private Dictionary mGoodsExceedDefault = new Dictionary();

    private TextView mTimeTv;
    private TextView mPlaceTv;
    private TextView mDescTv;
    private TextView mContactTv;
    private TextView mContactPhone;
    private TextView mPublicUser;
    private EditText mInfoFee;

    private TextView mContactTel;
    private Button mPublishBtn;

    private boolean mIsPlaceOk;
    private boolean mIsDescOk;

    private int mStartRegionCode;
    private int mEndRegionCode;
    private SwitchButton switch_button;

    @Override
    public void onClick(View v) {
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
            SystemUtils.hideInputMethod(mSquareEt);
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
        mTonEt = (EditText) findViewById(R.id.et_ton);
        mSquareEt = (EditText) findViewById(R.id.et_square);
        mInfoFee = (EditText) findViewById(R.id.et_info_fee);
        mGoodsExceedSp = (TextView) findViewById(R.id.spinner_goods_exceed);
        mGoodsTypeSp = (TextView) findViewById(R.id.spinner_goods_type);
        mTruckMeterSp = (TextView) findViewById(R.id.spinner_truck_meter);
        mTruckTypeSp = (TextView) findViewById(R.id.spinner_truck_type);
        // mTruckDefault.setId(-1);
        // mTruckDefault.setName("各种车型");
        // mTruckTypeCode = mTruckDefault.getId();
        // mTruckType = mTruckDefault.getName();
        // mTruckTypeSp.setText(mTruckType);
        mGoodsDefault.setId(1);
        mGoodsDefault.setName("普通货物");
        mGoodsTypeCode = mGoodsDefault.getId();
        mGoodsType = mGoodsDefault.getName();
        mGoodsTypeSp.setText(mGoodsType);
        // mTruckMeterDefault.setId(0);
        // mTruckMeterDefault.setName("3");
        // mTruckMeterCode = mTruckMeterDefault.getId();
        // mTruckMeter = mTruckMeterDefault.getName();
        // mTruckMeterSp.setText(mTruckMeter);
        mGoodsExceedDefault.setId(0);
        mGoodsExceedDefault.setName("三不超");
        mGoodsExceedCode = mGoodsExceedDefault.getId();
        mGoodsExceed = mGoodsExceedDefault.getName();
        mGoodsExceedSp.setText(mGoodsExceed);

        mTimeTv = (TextView) findViewById(R.id.tv_public_time);
        mContactPhone = (TextView) findViewById(R.id.tv_contact_phone);
        mContactTel = (TextView) findViewById(R.id.tv_contact_tel);
        mPublicUser = (TextView) findViewById(R.id.tv_public_user);

        mStartPlaceEt.addTextChangedListener(new MyWatcher(R.id.et_start_place));
        mEndPlaceEt.addTextChangedListener(new MyWatcher(R.id.et_end_place));
        mTonEt.addTextChangedListener(new MyWatcher(R.id.et_ton));
        mSquareEt.addTextChangedListener(new MyWatcher(R.id.et_square));
        // mTruckMeterSp.addTextChangedListener(new
        // MyWatcher(R.id.spinner_truck_meter));
        // mTruckTypeSp.addTextChangedListener(new
        // MyWatcher(R.id.spinner_truck_type));
        // mGoodsExceedSp.addTextChangedListener(new
        // MyWatcher(R.id.spinner_goods_exceed));
        // mGoodsTypeSp.addTextChangedListener(new
        // MyWatcher(R.id.spinner_goods_type));

        mGoodsExceedSp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // List<Dictionary> data =
                // DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_TYPE);
                List<Dictionary> data = DictionaryDao.getInstance().queryByType(0);
                // List<Dictionary> data = null;
                Dictionary dict = new Dictionary();
                dict.setId(0);
                dict.setName("三不超");
                data.add(0, dict);

                Dictionary dict1 = new Dictionary();
                dict1.setId(1);
                dict1.setName("超长");
                data.add(1, dict1);

                Dictionary dict2 = new Dictionary();
                dict2.setId(2);
                dict2.setName("超高");
                data.add(2, dict2);

                Dictionary dict3 = new Dictionary();
                dict3.setId(3);
                dict3.setName("超宽");
                data.add(3, dict3);

                showDictionaryListDialog("选择类型", data, new OnChooseDictionaryListener() {
                    @Override
                    public void onChoosedDictionary(Dictionary item) {
                        // ToastUtils.showToast(item.getName() + " - " +
                        // item.getId());
                        mGoodsExceedSp.setText(item.getName());
                        mGoodsExceed = item.getName();
                        mIsDescOk = previewDesc();
                    }
                });
            }
        });
        mGoodsTypeSp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                List<Dictionary> data = DictionaryDao.getInstance().queryByType(
                        Dictionary.PUBLISH_GOODSSOURCES_GOODSTYPE);
                // Dictionary dict = new Dictionary();
                // dict.setId(-1);
                // dict.setName("货物类型不限");
                // data.add(0, dict);
                showDictionaryListDialog("选择货物类型", data, new OnChooseDictionaryListener() {
                    @Override
                    public void onChoosedDictionary(Dictionary item) {
                        // ToastUtils.showToast(item.getName() + " - " +
                        // item.getId());
                        mGoodsTypeSp.setText(item.getName());
                        mGoodsType = item.getName();
                        mGoodsTypeCode = item.getId();
                        mIsDescOk = previewDesc();
                    }
                });
            }
        });
        mTruckMeterSp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                List<Dictionary> data = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_LENGTH);
                Dictionary dict = new Dictionary();
                dict.setId(-1);
                dict.setName("车长不限");
                data.add(0, dict);
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
                dict.setId(-1);
                dict.setName("车型不限");
                data.add(0, dict);
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

        mPlaceTv = (TextView) findViewById(R.id.tv_place);
        mDescTv = (TextView) findViewById(R.id.tv_desc);
        mContactTv = (TextView) findViewById(R.id.tv_contact);

        mTimeTv.setText(DateUtil.long2YMDHM(System.currentTimeMillis()));
        mPublicUser.setText(UserDao.getInstance().getUser().getShow_name());
        mContactTv.setText(UserDao.getInstance().getUser().getContacts_name());
        mContactPhone.setText(UserDao.getInstance().getUser().getContacts_phone());

        mContactTel.setText(UserDao.getInstance().getUser().getContacts_telephone());

        ImageView iv = (ImageView) findViewById(R.id.iv_type);
        iv.setImageResource(R.drawable.black_board_goods);

        mPublishBtn = (Button) findViewById(R.id.btn_publish);
        mPublishBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = mStartPlaceEt.getText().toString();
                String end = mEndPlaceEt.getText().toString();
                String goodsTon = mTonEt.getText().toString();
                String goodsSquare = mSquareEt.getText().toString();
                String infoFree = mInfoFee.getText().toString();
                if (!TextUtils.isEmpty(start)
                        && !TextUtils.isEmpty(end)
                        && (!TextUtils.isEmpty(goodsTon) || !TextUtils.isEmpty(goodsSquare) || !TextUtils
                                .isEmpty(mTruckMeter))) {
                    // if (mIsPlaceOk && mIsDescOk) {
                    final Freight d = new Freight();
                    d.setUser_id(UserDao.getInstance().getUser().getId());
                    d.setUser(UserDao.getInstance().getUser());
                    d.setType(Freight.TYPE_GOODS);
                    d.setStart_region(mStartPlaceEt.getText().toString());
                    d.setStart_region_code(mStartRegionCode);
                    d.setEnd_region(mEndPlaceEt.getText().toString());
                    d.setEnd_region_code(mEndRegionCode);
                    d.setCreate_time(System.currentTimeMillis());
                    boolean bOn = switch_button.isOpen();
                    int distribution_market = bOn ? Properties.FREIGHT_POST_TO_MARKET_SCREEN
                            : Properties.FREIGHT_NOT_POST_TO_MARKET_SCREEN;
                    d.setDistribution_market(distribution_market);
                    if (TextUtils.isEmpty(goodsTon)) {
                        d.setGoods_ton(0);
                    } else {
                        d.setGoods_ton(Float.parseFloat(goodsTon));
                    }
                    if (TextUtils.isEmpty(goodsSquare)) {
                        d.setGoods_square(0);
                    } else {
                        d.setGoods_square((int) Float.parseFloat(goodsSquare));
                    }
                    // 车长的name和code
                    if (TextUtils.isEmpty(mTruckMeter)) {
                        d.setTruck_length_code(0);
                        d.setTruck_length_name("");
                    } else {
                        d.setTruck_length_code(mTruckMeterCode);
                        d.setTruck_length_name(mTruckMeter);
                    }
                    // 车型的name和code
                    if (TextUtils.isEmpty(mTruckType)) {
                        d.setTruck_type_code(0);
                        d.setTruck_type_name("");
                    } else {
                        d.setTruck_type_name(mTruckType);
                        d.setTruck_type_code(mTruckTypeCode);
                    }
                    d.setGoods_exceed(mGoodsExceed);
                    d.setGoods_type_name(mGoodsType);
                    // d.setTruck_type_name(mTruckType);
                    // d.setTruck_type(mTruckTypeCode);
                    d.setGoods_type(mGoodsTypeCode);
                    // TODO truck_type_code
                    if (TextUtils.isEmpty(infoFree)) {
                        d.setInfo_cost(0);
                    } else {
                        d.setInfo_cost((int) Float.parseFloat(infoFree));
                    }

                    NetPublishFreight net = new NetPublishFreight(PublishGoodsActivity.this, d);
                    net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
                        @Override
                        public void onSuccess(FreightResp.Builder response) {
                            d.setId(String.valueOf(response.getFreightId()));
                            d.setCreate_time(response.getCreateDate());
                            d.setStatus(Properties.FREIGHT_STATUS_NO_PROCESSED);
                            d.setOwner_name(UserDao.getInstance().getUser().getShow_name());
                            // DispatchManager.getInstance().insert(d);
                            ToastUtils.showToast("发布成功");

                            // FreightDao.getInstance().insert(d);
                            Intent data = new Intent();

                            data.putExtra(BlackBoardActivity.EXTRA_PUBLISH_DISPATCH, d);
                            data.putExtra("todayCount", response.getFreightCountOfTodatyOnBlackBoard());
                            data.putExtra("totalCount", response.getFreightCountOnBlackBoard());
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
        String goodsTon = mTonEt.getText().toString();
        String goodsSquare = mSquareEt.getText().toString();
        // String goodsExceed = mGoodsExceed;
        String goodsType = mGoodsType;
        String truckMeter = mTruckMeter;
        String truckType = mTruckType;

        String exceed = mGoodsExceed;
        String desc = "";

        if (!TextUtils.isEmpty(goodsTon)) {
            desc = goodsTon + "吨 ";
        }
        if (!TextUtils.isEmpty(goodsSquare)) {
            desc += goodsSquare + "方 ";
        }
        if (!TextUtils.isEmpty(exceed)) {
            desc += exceed + " ";
        }
        if (!TextUtils.isEmpty(goodsType)) {
            desc += goodsType + " ";
        }
        if (!TextUtils.isEmpty(truckMeter)) {
            desc += truckMeter + " ";
        }
        if (!TextUtils.isEmpty(truckType)) {
            desc += truckType;
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
        return new TitleParams(getDefaultHomeAction(), "发布货源", null).setShowLogo(false);
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
        setContentView(R.layout.activity_publish_goods);
        findViewById(R.id.root).setOnClickListener(this);
        initView();
    }
}
