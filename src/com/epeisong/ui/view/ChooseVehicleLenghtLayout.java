package com.epeisong.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.epeisong.base.activity.XBaseActivity.OnChooseDictionaryListener;

public class ChooseVehicleLenghtLayout extends ChoosableListLayout implements Choosable {
    private List<Choosion> mLenghts;

	private OnChooseVehicleLenghtListener mListener;

	public ChooseVehicleLenghtLayout(Context context) {
		this(context, null);
	}
    private Choosion mChoosionDefault;

	public ChooseVehicleLenghtLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    public Choosion getDefaultChoosion() {
        if (mChoosionDefault == null) {
            mChoosionDefault = new Choosion(-1, "车长不限");
        }
        return mChoosionDefault;
    }

	@Override
	protected String onGetTitle() {
		// TODO Auto-generated method stub
		return "车长";
	}
    @Override
    protected void onSetData(List<Choosion> data) {
        mLenghts = new ArrayList<Choosion>();
        mLenghts.add(getDefaultChoosion());
        mLenghts.add(new Choosion(1, "13.5米"));
        mLenghts.add(new Choosion(2, "17.5米"));
        data.addAll(mLenghts);
    }

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this;
	}
	
	@Override
	public int getChooseDictionaryType() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getChooseTitle() {
		// TODO Auto-generated method stub
		return null;
	}@Override
	public OnChooseDictionaryListener getOnChooseDictionaryListener() {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    protected void onSelectedItem(Choosion choosion) {
        if (mListener != null) {
            mListener.onChoosedVehicleLenght(choosion);
        }
    }
    public void setOnChooseVehicleLenghtListener(OnChooseVehicleLenghtListener listener) {
        mListener = listener;
    }

    public interface OnChooseVehicleLenghtListener {
        void onChoosedVehicleLenght(Choosion choosion);
    }
}
