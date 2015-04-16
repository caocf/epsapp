package com.epeisong.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.model.BusinessChatModel;

/**
 * 空Fragment
 * @author poet
 *
 */
public class EmptyFragment extends Fragment {

    private Fragment mChild;
    private String flag;
    private TextView content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Bundle args = getArguments();
    	if (args != null) {
            flag = args.getString("flag");
        }
    	View root = inflater.inflate(R.layout.fragment_empty, null);
    	content = (TextView) root.findViewById(R.id.tv_content);
    	if(!TextUtils.isEmpty(flag)){
    		content.setText("你不能和自己聊天");
    	}
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mChild != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mChild)
                    .commit();
        }
    }

    public void replace(Fragment f) {
        mChild = f;
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mChild)
                    .commit();
        }
    }

}
