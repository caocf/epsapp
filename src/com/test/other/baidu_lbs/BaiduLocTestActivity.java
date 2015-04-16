package com.test.other.baidu_lbs;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.java.JavaUtils;

/**
 * 百度定位测试
 * @author poet
 *
 */
public class BaiduLocTestActivity extends BaseActivity implements OnClickListener {

    TextView mLoc1Tv;
    EditText mSpanEt;
    TextView mLoc2Tv;

    EpsLocationRequestor mRequestor, mRequestor2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_baidu_loc);
        findViewById(R.id.btn_request_loc).setOnClickListener(this);
        findViewById(R.id.btn_request_loc_2).setOnClickListener(this);
        mLoc1Tv = (TextView) findViewById(R.id.tv_loc);
        mLoc2Tv = (TextView) findViewById(R.id.tv_loc_2);
        mSpanEt = (EditText) findViewById(R.id.et_span);

        mRequestor = new EpsLocationRequestor();
        mRequestor2 = new EpsLocationRequestor();
    }

    OnEpsLocationListener mEpsLocationListener1 = new OnEpsLocationListener() {
        @Override
        public void onEpsLocation(EpsLocation epsLocation) {
            mLoc1Tv.setText(JavaUtils.getString(epsLocation));
        }
    };

    OnEpsLocationListener mEpsLocationListener2 = new OnEpsLocationListener() {
        @Override
        public void onEpsLocation(EpsLocation epsLocation) {
            mLoc2Tv.setText(JavaUtils.getString(epsLocation));
        }
    };

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "百度定位测试");
    }

    @Override
    protected void onDestroy() {
        mRequestor2.stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_request_loc:
            mRequestor.requestEpsLocation(mEpsLocationListener1);
            break;
        case R.id.btn_request_loc_2:
            int span = 2000;
            String spanStr = mSpanEt.getText().toString();
            try {
                int i = Integer.parseInt(spanStr);
                if (i > 2) {
                    span = i * 1000;
                }
            } catch (Exception e) {

            }
            mRequestor2.requestEpsLocation(mEpsLocationListener2, span);
            break;
        }
    }
}
