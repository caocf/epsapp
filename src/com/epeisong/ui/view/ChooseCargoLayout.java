package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;

public class ChooseCargoLayout extends ChoosableListLayout implements Choosable {

    private OnChooseCargoListener mListener;
    private List<Choosion> mData;
    private Choosion mChoosionDefault;

    public ChooseCargoLayout(Context context) {
        this(context, null);
    }

    public ChooseCargoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String onGetTitle() {
        return "货物类型";
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
    public Choosion getDefaultChoosion() {
        if (mChoosionDefault == null) {
            mChoosionDefault = new Choosion(1, "普通货物");
        }
        return mChoosionDefault;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    protected void onSetData(List<Choosion> data) {
        mData = new ArrayList<Choosion>();
        mData.add(getDefaultChoosion());
        mData.add(new Choosion(2, "冷藏"));
        mData.add(new Choosion(3, "机械"));
        mData.add(new Choosion(4, "食品"));
        mData.add(new Choosion(5, "危险品"));
        data.addAll(mData);
    }

    @Override
    protected void onSelectedItem(Choosion choosion) {
        if (mListener != null) {
            mListener.onChoosedCargo(choosion);
        }
    }

    public void setOnChooseCargoListener(OnChooseCargoListener listener) {
        mListener = listener;
    }

    public interface OnChooseCargoListener {
        void onChoosedCargo(Choosion choosion);
    }
}
