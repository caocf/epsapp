package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;

public class ChooseValidityLayout extends ChoosableListLayout implements Choosable {

    private OnChooseValidityListener mListener;

    private List<Choosion> mData;

    private Choosion mChoosionDefault;

    public ChooseValidityLayout(Context context) {
        this(context, null);
    }

    public ChooseValidityLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String onGetTitle() {
        return "时效";
    }

    @Override
    public Choosion getDefaultChoosion() {
        if (mChoosionDefault == null) {
            mChoosionDefault = new Choosion(-1, "时效不限");
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
        mData.add(new Choosion(1, "1天"));
        mData.add(new Choosion(2, "2天"));
        mData.add(new Choosion(3, "3天"));
        mData.add(new Choosion(4, "4天"));
        mData.add(new Choosion(5, "5天"));
        mData.add(new Choosion(6, "6天"));
        mData.add(new Choosion(7, "7天"));
        mData.add(new Choosion(8, "7天以上"));
        data.addAll(mData);
    }

    @Override
    protected void onSelectedItem(Choosion choosion) {
        if (mListener != null) {
            mListener.onChoosedValidity(choosion);
        }
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

    public void setOnChooseValidityListener(OnChooseValidityListener listener) {
        mListener = listener;
    }

    public interface OnChooseValidityListener {
        void onChoosedValidity(Choosion choosion);
    }
}
