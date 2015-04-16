package com.epeisong.base.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;

/**
 * 简单的ListActivity
 * 
 * @author poet
 * 
 * @param <E>
 */
public abstract class SimpleListActivity<E> extends BaseActivity implements OnItemClickListener,
        OnItemLongClickListener, OnLoadMoreListener {

    protected FrameLayout mFrameLayoutTop;
    protected ListView mListView;
    protected MyAdapter mAdapter;
    protected EndlessAdapter mEndlessAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_listview);
        mFrameLayoutTop = (FrameLayout) findViewById(R.id.fl_top);
        mListView = (ListView) findViewById(R.id.lv);
        onAddHeaderOrFooter(mListView);
        mAdapter = new MyAdapter();
        if (isUseEndlessAdapter()) {
            mEndlessAdapter = new EndlessAdapter(this, mAdapter);
            mEndlessAdapter.setIsAutoLoad(false);
            mEndlessAdapter.setOnLoadMoreListener(this);
            mListView.setAdapter(mEndlessAdapter);
        } else {
            mListView.setAdapter(mAdapter);
        }

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        View emptyView = getEmptyView();
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
            addContentView(emptyView, new LayoutParams(-1, -1));
            mListView.setEmptyView(emptyView);
        }
    }

    protected void onAddHeaderOrFooter(ListView listView) {
    }

    protected abstract View getItemView(int position, View convertView, ViewGroup parent);

    protected boolean isUseEndlessAdapter() {
        return false;
    }

    protected View getEmptyView() {
        TextView tv = new TextView(getApplicationContext());
        tv.setText("没有数据");
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    protected Integer getAdapterViewTypeCount() {
        return null;
    }

    protected Integer getAdapterViewType(int position) {
        return null;
    }

    protected Boolean isAdapterItemEnabled(int position) {
        return null;
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
    }

    public class MyAdapter extends HoldDataBaseAdapter<E> {
        @Override
        public int getItemViewType(int position) {
            Integer type = getAdapterViewType(position);
            if (type == null) {
                return super.getItemViewType(position);
            }
            return type;
        }

        @Override
        public int getViewTypeCount() {
            Integer count = getAdapterViewTypeCount();
            if (count == null) {
                return super.getViewTypeCount();
            }
            return count;
        }

        @Override
        public boolean isEnabled(int position) {
            Boolean enabled = isAdapterItemEnabled(position);
            if (enabled == null) {
                return super.isEnabled(position);
            }
            return enabled;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItemView(position, convertView, parent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

}
