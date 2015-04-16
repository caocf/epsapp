package com.epeisong.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.SpUtils;

/**
 * 
 * @author 孙灵洁 版本更新
 * 
 */
public class TheNewVersionActivity extends BaseActivity {

    @Override
    protected TitleParams getTitleParams() {
        // TODO Auto-generated method stub
        return new TitleParams(getDefaultHomeAction(), "版本更新", null).setShowLogo(false);
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_version);

        boolean hasNewVersion = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION, false);
        if (hasNewVersion == true) {
            String url = SpUtils.getString(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL, null);
            if (url != null) {
                Dialog(url);
            }
        }

    }

    public void Dialog(final String url) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("软件升级");
        alert.setMessage("发现有新版本，建议立即更新使用");
        alert.setPositiveButton("更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        alert.create().show();

    }

}
