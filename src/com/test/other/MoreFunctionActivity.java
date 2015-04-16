package com.test.other;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase.Mode;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.epeisong.base.activity.PullToRefreshListViewActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.ui.adapter.SettingAdapter;
import com.epeisong.ui.adapter.SettingItem;
import com.test.other.baidu_lbs.BaiduLocTestActivity;
import com.test.other.baidu_lbs.BaiduMapTestActivity;
import com.test.other.baidu_lbs.BaiduPoiTestActivity;

public class MoreFunctionActivity extends PullToRefreshListViewActivity {

    SettingAdapter mSettingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMode(Mode.DISABLED);
        mSettingAdapter.addAll(createData());
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "更多功能");
    }

    @Override
    protected ListAdapter onCreateAdapter() {
        return mSettingAdapter = new SettingAdapter(this);
    }

    public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
        position -= mListView.getHeaderViewsCount();
        SettingItem item = mSettingAdapter.getItem(position);
        if (item.getRunnable() != null) {
            parent.post(item.getRunnable());
        }
    };

    List<SettingItem> createData() {
        List<SettingItem> items = new ArrayList<SettingItem>();
        items.add(new SettingItem().setName("功能测试"));
        items.add(new SettingItem(0, "百度定位", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), BaiduLocTestActivity.class);
                startActivity(intent);
            }
        }));
        items.add(new SettingItem(0, "百度地图", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), BaiduMapTestActivity.class);
                startActivity(intent);
            }
        }));
        items.add(new SettingItem(0, "百度POI", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), BaiduPoiTestActivity.class);
                startActivity(intent);
            }
        }));
        return items;
    }
}
