package com.epeisong.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.PointDao;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.model.Point.PointCode;
import com.epeisong.ui.fragment.ContactsFragment;
 
import com.epeisong.utils.LogUtils;

/**
 * 联系人
 * 
 * @author poet
 * 
 */
public class ContactsActivity extends BaseActivity  {
	ContactsFragment fragment;
    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "联系人", null).setShowLogo(false);
    }

    private boolean mUseAnim = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mUseAnim) {
            overridePendingTransition(R.anim.menu_in, 0);
        }
        LogUtils.et("oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        fragment = new ContactsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
       // PointDao.getInstance().hide(PointCode.Code_Contacts);
    }

    @Override
    public void finish() {
        super.finish();
        if (mUseAnim) {
            overridePendingTransition(0, R.anim.menu_out);
        }
    }

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];
		switch(type) {
		case CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ: //有人关注
			int show = (Integer)param[1];
			fragment.refreshPoint(show);
			 
			break;
		}
	}

 
    
}
