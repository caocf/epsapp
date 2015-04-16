package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.bdmap.epsloc.EpsLocation;
import com.bdmap.epsloc.EpsLocationHolder;
import com.bdmap.epsloc.EpsLocationRequestor;
import com.bdmap.epsloc.EpsLocationRequestor.OnEpsLocationListener;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;

/**
 * 地址搜索（基于LBS POI）
 * @author poet
 *
 */
public class PoiSearchActivity extends BaseActivity implements OnClickListener, OnEditorActionListener,
        OnGetPoiSearchResultListener, OnItemClickListener {

    public static final String EXTRA_OUT_EPS_POI = "out_eps_poi";

    EpsLocation mEpsLocation;

    EditText mEditText;
    ListView mListView;
    MyAdapter mAdapter;

    PoiSearch mPoiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_search);
        findViewById(R.id.iv_back).setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.et_key);
        mEditText.setOnEditorActionListener(this);
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(this);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        mEpsLocation = EpsLocationHolder.getEpsLocation();
        if (mEpsLocation == null) {
            requestLocation();
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_back:
            finish();
            break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search(mEditText.getText().toString());
            return true;
        }
        return false;
    }

    void requestLocation() {
        showPendingDialog(null);
        EpsLocationRequestor requestor = new EpsLocationRequestor();
        requestor.requestEpsLocation(new OnEpsLocationListener() {
            @Override
            public void onEpsLocation(EpsLocation epsLocation) {
                dismissPendingDialog();
                mEpsLocation = epsLocation;
                EpsLocationHolder.setEpsLocation(epsLocation);
            }
        });
    }

    void search(String key) {
        if (mEpsLocation == null) {
            requestLocation();
            return;
        }
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mPoiSearch.searchInCity(new PoiCitySearchOption().city(mEpsLocation.getCityName()).keyword(key));
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            ToastUtils.showToast("未找到结果");
            return;
        }
        List<PoiInfo> allPoi = result.getAllPoi();
        mAdapter.replaceAll(allPoi);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult arg0) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo info = mAdapter.getItem(position);
        LatLng loc = info.location;
        EpsPoiInfo epsPoiInfo = new EpsPoiInfo();
        epsPoiInfo.addressName = info.address;
        epsPoiInfo.latitude = loc.latitude;
        epsPoiInfo.longitude = loc.longitude;
        Intent data = new Intent();
        data.putExtra(EXTRA_OUT_EPS_POI, epsPoiInfo);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    class MyAdapter extends HoldDataBaseAdapter<PoiInfo> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = holder.createView();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_address;

        View createView() {
            LinearLayout ll = new LinearLayout(getApplicationContext());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setBackgroundResource(R.drawable.selector_item_white_gray);
            int p = DimensionUtls.getPixelFromDpInt(10);
            ll.setPadding(p, p, p, p);
            tv_name = new TextView(getApplicationContext());
            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tv_name.setTextColor(Color.BLACK);
            ll.addView(tv_name);
            tv_address = new TextView(getApplicationContext());
            tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv_address.setTextColor(Color.GRAY);
            ll.addView(tv_address);
            return ll;
        }

        void fillData(PoiInfo info) {
            tv_name.setText(info.name);
            tv_address.setText(info.address);
        }
    }

    public static class EpsPoiInfo implements Serializable {
        private static final long serialVersionUID = 6107898641985996896L;

        public String addressName;
        public double latitude;
        public double longitude;
    }

    public static void startForResult(Activity a, int requestCode) {
        Intent intent = new Intent(a, PoiSearchActivity.class);
        a.startActivityForResult(intent, requestCode);
    }
}
