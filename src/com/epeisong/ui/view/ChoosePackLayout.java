package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;

public class ChoosePackLayout extends ChoosableListLayout implements Choosable {
    private OnChoosePackListener mListener;
    private List<Choosion> mData;
    private Choosion mChoosionDefault;

    public ChoosePackLayout(Context context) {
        this(context, null);
    }

    public ChoosePackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String onGetTitle() {
        return "包装类别";
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public Choosion getDefaultChoosion() {
        if (mChoosionDefault == null) {
            mChoosionDefault = new Choosion(-1, "包装不限");
        }
        return mChoosionDefault;
    }
    @Override
    public int getChooseDictionaryType() {
    	// TODO Auto-generated method stub
    	return 0;
    }@Override
    public String getChooseTitle() {
    	// TODO Auto-generated method stub
    	return null;
    }@Override
    public OnChooseDictionaryListener getOnChooseDictionaryListener() {
    	// TODO Auto-generated method stub
    	return null;
    }

    @Override
    protected void onSetData(List<Choosion> data) {
        mData = new ArrayList<Choosion>();
        mData.add(getDefaultChoosion());
        mData.add(new Choosion(1, "纸箱"));
        mData.add(new Choosion(2, "木箱"));
        mData.add(new Choosion(3, "防震"));
        mData.add(new Choosion(4, "防水"));
        mData.add(new Choosion(5, "电子产品包装"));
        data.addAll(mData);
    }

    @Override
    protected void onSelectedItem(Choosion choosion) {
        if (mListener != null) {
            mListener.onChoosedPack(choosion);
        }
    }

    public void setOnChoosePackListener(OnChoosePackListener listener) {
        mListener = listener;
    }

    public interface OnChoosePackListener {
        void onChoosedPack(Choosion choosion);
    }

}
