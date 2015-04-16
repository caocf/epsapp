package com.epeisong.ui.activity.temp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.view.TitleParams;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.ChooseRegionActivity;
import com.epeisong.ui.activity.temp.ChooseRoleActivity.SetupModel;
import com.epeisong.ui.activity.user.TempActivity;
import com.epeisong.utils.ToastUtils;

/**
 * 注册-设置信息
 * 
 * @author poet
 * 
 */
public class SetupAccountInfoActivity extends TempActivity implements OnClickListener {

    private static final int REQUEST_CODE_REGION = 100;

    private EditText mNameEt;
    private TextView mRegionTv;
    private EditText mRegionInfoEt;
    private EditText mContactsNameEt;
    private EditText mContactsPhoneEt;
    private EditText mContactsTelephoneEt;

    private SetupModel mSetupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSetupModel = ChooseRoleActivity.sSetupModel;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account_info);
        mNameEt = (EditText) findViewById(R.id.et_show_name);
        mRegionTv = (TextView) findViewById(R.id.tv_region);
        mRegionTv.setOnClickListener(this);
        mRegionInfoEt = (EditText) findViewById(R.id.et_region_info);
        mContactsNameEt = (EditText) findViewById(R.id.et_contacts_name);
        mContactsPhoneEt = (EditText) findViewById(R.id.et_contacts_phone);
        mContactsTelephoneEt = (EditText) findViewById(R.id.et_contacts_telephone);
        findViewById(R.id.btn_next).setOnClickListener(this);

        if (mSetupModel == null) {
            finish();
        }

        if (!TextUtils.isEmpty(mSetupModel.getShowName())) {
            mNameEt.setText(mSetupModel.getShowName());
        }
        if (mSetupModel.getUserRegion() != null) {
            mRegionTv.setText(mSetupModel.getUserRegion().getGeneralName());
        }
        if (!TextUtils.isEmpty(mSetupModel.getUserRegionInfo())) {
            mRegionInfoEt.setText(mSetupModel.getUserRegionInfo());
        }
        if (!TextUtils.isEmpty(mSetupModel.getContactsName())) {
            mContactsNameEt.setText(mSetupModel.getContactsName());
        }
        if (!TextUtils.isEmpty(mSetupModel.getContactsPhone())) {
            mContactsPhoneEt.setText(mSetupModel.getContactsPhone());
        }
        if (!TextUtils.isEmpty(mSetupModel.getContactsTelephone())) {
            mContactsTelephoneEt.setText(mSetupModel.getContactsTelephone());
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "设置信息", null).setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_region:
            ChooseRegionActivity.launch(this, ChooseRegionActivity.FILTER_0_3, REQUEST_CODE_REGION);
            break;
        case R.id.btn_next:
            String showName = mNameEt.getText().toString().trim();
            if (TextUtils.isEmpty(showName)) {
                ToastUtils.showToast("必须填写[名称]!");
                return;
            }
            mSetupModel.setShowName(showName);

            String userRegionInfo = mRegionInfoEt.getText().toString().trim();
            if (!TextUtils.isEmpty(userRegionInfo)) {
                mSetupModel.setUserRegionInfo(userRegionInfo);
            }
            String contactsName = mContactsNameEt.getText().toString().trim();
            if (!TextUtils.isEmpty(contactsName)) {
                mSetupModel.setContactsName(contactsName);
            }
            String contactsPhone = mContactsPhoneEt.getText().toString().trim();
            if (!TextUtils.isEmpty(contactsPhone)) {
                mSetupModel.setContactsPhone(contactsPhone);
            }
            String contactsTelephone = mContactsTelephoneEt.getText().toString().trim();
            if (!TextUtils.isEmpty(contactsTelephone)) {
                mSetupModel.setContactsTelephone(contactsTelephone);
            }

            Intent intent = new Intent(this, SetupRoleInfoActivity.class);
            startActivity(intent);
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_REGION) {
                RegionResult result = (RegionResult) data.getSerializableExtra(ChooseRegionActivity.EXTRA_OUT_REGION);
                mSetupModel.setUserRegion(result);
                mRegionTv.setText(result.getGeneralName());
            }
        }
    }
}
