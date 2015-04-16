package com.epeisong.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.epeisong.R;

/**
 * 流程执行
 * 
 * @author Jack
 * 
 */

public class FlowExeFragment extends Fragment {
	private ListView mListView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(getActivity());
        mListView.setBackgroundResource(R.color.page_bg);
        return mListView;
    }

}
