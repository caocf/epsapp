package com.epeisong.base.activity;

import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.DimensionUtls;

/**
 * 列表PopupWindow界面
 * @author poet
 *
 */
public abstract class ListPopupActivity extends BaseActivity implements OnItemClickListener {

    ListView mListView;

    int mSelectedPos = -1;

    PopupWindow mPopupWindow;
    HoldDataBaseAdapter<String> mPopupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = new ListView(this);
        setContentView(mListView);
        mListView.setOnItemClickListener(this);
        initPopupWindow();
    }

    protected abstract List<String> onShowPopup(int position);

    protected abstract void onClickPopup(int listPos, int popupPos);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<String> data = onShowPopup(position);
        if (data != null && !data.isEmpty()) {
            mSelectedPos = position;
            mPopupAdapter.replaceAll(data);
            showPopupWindow(view);
        }
    }

    void showPopupWindow(View v) {
        mPopupWindow.showAsDropDown(v);
    }

    void initPopupWindow() {
        mPopupWindow = new PopupWindow(getApplicationContext());
        mPopupAdapter = new HoldDataBaseAdapter<String>() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = new TextView(getApplicationContext());
                int p = DimensionUtls.getPixelFromDpInt(10);
                tv.setPadding(p, p, p, p);
                tv.setText(getItem(position));
                return tv;
            }
        };
        ListView lv = new ListView(getApplicationContext());
        lv.setAdapter(mPopupAdapter);
        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
        mPopupWindow.setContentView(lv);
        mPopupWindow.setWidth(EpsApplication.getScreenWidth() / 2 - 50);
        mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindow.dismiss();
                onClickPopup(mSelectedPos, position);
                mSelectedPos = -1;
            }
        });
    }
}
