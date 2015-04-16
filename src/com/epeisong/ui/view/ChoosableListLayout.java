package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.ui.view.Choosable.Choosion;
import com.epeisong.utils.DimensionUtls;

public abstract class ChoosableListLayout extends FrameLayout implements OnItemClickListener {

    private MyAdapter mAdapter;
    private List<Choosion> mData;
    private int mSelectedPos;

    public ChoosableListLayout(Context context) {
        this(context, null);
    }

    public ChoosableListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(getContext());
        int p = (int) DimensionUtls.getPixelFromDp(10);
        title.setPadding(p, p, p, p);
        title.setGravity(Gravity.CENTER);
        title.setText(onGetTitle());
        title.setTextColor(Color.BLACK);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        View line = new View(getContext());
        int h = DimensionUtls.getPixelFromDpInt(2);
        line.setLayoutParams(new LinearLayout.LayoutParams(-1, h));
        line.setBackgroundResource(R.color.blue);
        ll.addView(title);
        ll.addView(line);

        mData = new ArrayList<Choosion>();
        onSetData(mData);
        ListView lv = new ListView(context);
        lv.addHeaderView(ll, null, false);
        lv.setAdapter(mAdapter = new MyAdapter());
        mAdapter.replaceAll(mData);
        lv.setOnItemClickListener(this);
        this.setBackgroundColor(Color.WHITE);
        this.addView(lv);
    }

    protected abstract String onGetTitle();

    protected abstract void onSetData(List<Choosion> data);

    protected abstract void onSelectedItem(Choosion choosion);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= 1;
        boolean change = mSelectedPos != position;
        if (change) {
            mSelectedPos = position;
            mAdapter.notifyDataSetChanged();
        }
        onSelectedItem(mAdapter.getItem(position));
    }

    private class MyAdapter extends HoldDataBaseAdapter<Choosion> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RelativeLayout rl = new RelativeLayout(getContext());
            int h = (int) DimensionUtls.getPixelFromDp(50);
            rl.setLayoutParams(new AbsListView.LayoutParams(-1, h));
            TextView tv = new TextView(getContext());
            tv.setText(getItem(position).getName());
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            params.leftMargin = (int) DimensionUtls.getPixelFromDp(10);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            rl.addView(tv, params);
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(R.drawable.selector_common_hook);
            if (mSelectedPos == position) {
                iv.setSelected(true);
            } else {
                iv.setSelected(false);
            }
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(-2, -2);
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params2.addRule(RelativeLayout.CENTER_VERTICAL);
            params2.rightMargin = (int) DimensionUtls.getPixelFromDp(10);
            rl.addView(iv, params2);

            if (position < getCount() - 1) {
                RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(-1,
                        (int) DimensionUtls.getPixelFromDp(1));
                params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                View line = new View(getContext());
                line.setBackgroundResource(R.color.line_contacts_sub);
                rl.addView(line, params3);
            }
            return rl;
        }
    }
}
