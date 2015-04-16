package com.epeisong.ui.activity;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.InfoScreenFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/**
 * 信息电子屏activity
 * @author Jack
 *
 */
public class InfoScreenActivity extends BaseActivity {
	private InfoScreenFragment infoScreenFragment;
	
    public static void launch(Context context, User user) {
        Intent intent = new Intent(context, InfoScreenActivity.class);
        intent.putExtra(ContactsDetailActivity.EXTRA_USER, user);
        
        context.startActivity(intent);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "信息电子屏", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	User mUser = (User) getIntent().getSerializableExtra(ContactsDetailActivity.EXTRA_USER);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        infoScreenFragment = new InfoScreenFragment();
        Bundle args = new Bundle();
        args.putSerializable(InfoScreenFragment.EXTRA_MARKET, mUser);
        infoScreenFragment.setArguments(args);
        
        getSupportFragmentManager().beginTransaction().add(R.id.frame, infoScreenFragment).commit();
    }
    
    @Override
    public void onBackPressed() {
    	if (infoScreenFragment.onBackPressed()) {
            return;
        }
    	super.onBackPressed();
    }

}
