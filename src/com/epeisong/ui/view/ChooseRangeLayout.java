package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.DimensionUtls;

public class ChooseRangeLayout extends RelativeLayout {

    int selectedColor = Color.argb(0xff, 0xf8, 0xf8, 0xf8);

    List<Integer> mRangeList;

    ListView mListView;
    MyAdapter mAdapter;

    OnChooseRangeListener mListener;
    int mSelectedPos;

    public ChooseRangeLayout(Context context) {
        super(context);
        init();
    }

    public ChooseRangeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setBackgroundColor(Color.argb(0x88, 0, 0, 0));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callListener(null);
            }
        });

        mRangeList = new ArrayList<Integer>();
        mRangeList.add(1000);
        mRangeList.add(1500);
        mRangeList.add(2000);
        mRangeList.add(3000);
        mRangeList.add(4000);
        mRangeList.add(5000);

        mListView = new ListView(getContext());
        mListView.setSelector(R.drawable.selector_item_white_gray);
        mListView.setBackgroundColor(Color.WHITE);
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPos = position;
                callListener(mRangeList.get(position));
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.replaceAll(mRangeList);
        this.addView(mListView);
    }

    void callListener(Integer range) {
        if (mListener != null) {
            mListener.onChoosedRange(range);
        }
    }

    public void setListener(OnChooseRangeListener l) {
        mListener = l;
    }

    public interface OnChooseRangeListener {
        void onChoosedRange(Integer range);
    }

    class MyAdapter extends HoldDataBaseAdapter<Integer> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = holder.createView(getContext());
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(this, position);
            return convertView;
        }
    }

    class ViewHolder {
        LinearLayout root;
        View v;
        TextView tv;

        int p10 = DimensionUtls.getPixelFromDpInt(10);

        View createView(Context context) {
            root = new LinearLayout(context);
            root.setOrientation(LinearLayout.HORIZONTAL);
            v = new View(context);
            v.setBackgroundColor(Color.argb(0xff, 0xff, 0x80, 0x00));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(p10 / 5, -1);
            p.topMargin = p.bottomMargin = p10 / 2;
            p.leftMargin = 1;
            root.addView(v, p);
            tv = new TextView(context);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(p10, p10, p10, p10);
            root.addView(tv);
            return root;
        }

        void fillData(MyAdapter adapter, int pos) {
            root.setBackgroundColor(Color.TRANSPARENT);
            v.setVisibility(View.INVISIBLE);
            tv.setText(adapter.getItem(pos) + "米");
            if (pos == mSelectedPos) {
                root.setBackgroundColor(selectedColor);
                v.setVisibility(View.VISIBLE);
            }
        }
    }
}
