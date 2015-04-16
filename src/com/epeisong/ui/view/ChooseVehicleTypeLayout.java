package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;

public class ChooseVehicleTypeLayout extends ChoosableListLayout implements Choosable {

    private List<Choosion> mTypes;

    private Choosion mChoosionDefault;
    private OnChooseVehicleTypeListener mListener;

    public ChooseVehicleTypeLayout(Context context) {
        this(context, null);
    }

    public ChooseVehicleTypeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String onGetTitle() {
        return "车型";
    }

    @Override
    public Choosion getDefaultChoosion() {
        if (mChoosionDefault == null) {
            mChoosionDefault = new Choosion(-1, "车型不限");
        }
        return mChoosionDefault;
    }

    @Override
    public View getView() {
        return this;
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
        mTypes = new ArrayList<Choosion>();
        mTypes.add(getDefaultChoosion());
        mTypes.add(new Choosion(1, "平板"));
        mTypes.add(new Choosion(2, "半挂"));
        mTypes.add(new Choosion(3, "高栏"));
        mTypes.add(new Choosion(4, "集装箱"));
        data.addAll(mTypes);
    }

    @Override
    protected void onSelectedItem(Choosion choosion) {
        if (mListener != null) {
            mListener.onChoosedVehicleType(choosion);
        }
    }

    public void setOnChooseVehicleTypeListener(OnChooseVehicleTypeListener listener) {
        mListener = listener;
    }

    public interface OnChooseVehicleTypeListener {
        void onChoosedVehicleType(Choosion choosion);
    }
}
