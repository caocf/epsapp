package com.test.other.baidu_lbs;

import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.java.JavaUtils;

public class BaiduPoiTestActivity extends BaseActivity implements OnGetPoiSearchResultListener, OnClickListener {

    PoiSearch mPoiSearch;

    MyAdapter mAdapter;
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_baidu_poi);
        ListView lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(mAdapter = new MyAdapter());
        mEditText = (EditText) findViewById(R.id.et_key);
        findViewById(R.id.btn_search).setOnClickListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "百度POI测试");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_search:
            String key = mEditText.getText().toString();
            if (!TextUtils.isEmpty(key)) {
                searchInCity("南京市", key);
            }
            break;

        default:
            break;
        }
    }

    void searchInCity(String city, String key) {
        showPendingDialog(null);
        mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(key));
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        dismissPendingDialog();
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            ToastUtils.showToast("未找到结果");
            return;
        }
        List<PoiInfo> allPoi = poiResult.getAllPoi();
        mAdapter.replaceAll(allPoi);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        dismissPendingDialog();
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtils.showToast("未找到结果");
        } else {
            ToastUtils.showToast(poiDetailResult.getName() + ":" + poiDetailResult.getAddress());
        }
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
        TextView tv;

        public View createView() {
            tv = new TextView(getApplicationContext());
            return tv;
        }

        public void fillData(PoiInfo info) {
            tv.setText(JavaUtils.getString(info));
        }
    }
}
