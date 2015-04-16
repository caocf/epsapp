package com.epeisong.ui.activity;

import android.os.Bundle;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.ui.fragment.SearchFreightFragment;

public class SearchFreightActivity extends BaseActivity {

    SearchFreightFragment mSearchFreightFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mSearchFreightFragment = new SearchFreightFragment();
        Bundle extras = getIntent().getExtras();
        mSearchFreightFragment.setArguments(extras);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, mSearchFreightFragment).commit();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "搜索车源货源");
    }

    @Override
    public void onBackPressed() {
        if (mSearchFreightFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
