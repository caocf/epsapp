package com.epeisong.ui.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.RegionDao;
import com.epeisong.model.Region;
import com.epeisong.utils.SystemUtils;

public class ChooseRegionSearchActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private class MyAdapter extends HoldDataBaseAdapter<Region> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_choose_region_search_item);
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

    class ViewHolder {
        TextView tv_region;
        TextView tv_note;

        public void fillData(Region region) {
            tv_region.setText(region.getName());
            tv_note.setText(region.getNote());
        }

        public void findView(View v) {
            tv_region = (TextView) v.findViewById(R.id.tv_region_name);
            tv_note = (TextView) v.findViewById(R.id.tv_region_note);
        }
    }

    private int mFilter;
    private EditText mEditText;
    private View mClearView;
    private ListView mListView;
    private View mLvContainer;

    private View mSearchNothingView;

    private MyAdapter mAdapter;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_clear:
            mEditText.setText("");
            mClearView.setVisibility(View.INVISIBLE);
            break;
        case R.id.btn_cancel:
            finish();
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Region region = mAdapter.getItem(position);
        Intent data = new Intent();
        data.putExtra(ChooseRegionActivity.EXTRA_OUT_REGION, region);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    protected TitleParams getTitleParams() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFilter = getIntent().getIntExtra(ChooseRegionActivity.EXTRA_IN_FILTER, -1);
        if (mFilter == -1) {
            throw new RuntimeException("必须指定显示地区，传递：EXTRA_IN_FILTER。");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_region_search);
        mEditText = (EditText) findViewById(R.id.et_search);
        mClearView = findViewById(R.id.iv_clear);
        mClearView.setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv);
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(this);

        mLvContainer = findViewById(R.id.ll_lv_container);
        mSearchNothingView = findViewById(R.id.tv_search_nothing);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                	//TODO
                    mAdapter.clear();
                    mLvContainer.setVisibility(View.GONE);
                    mSearchNothingView.setVisibility(View.GONE);
                    return;
                }
                mClearView.setVisibility(View.VISIBLE);
                List<Region> data = RegionDao.getInstance().query(mFilter, s.toString());
                mAdapter.replaceAll(data);
                mLvContainer.setVisibility(View.VISIBLE);
                if (mAdapter.isEmpty()) {
                    mSearchNothingView.setVisibility(View.VISIBLE);
                } else {
                    mSearchNothingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditText.requestFocus();
                SystemUtils.showInputMethod(mEditText);
            }
        }, 300);
    }
}
