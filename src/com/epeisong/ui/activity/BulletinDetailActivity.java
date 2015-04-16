package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.MainActivity;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.BulletinDao;
import com.epeisong.data.layer02.BulletinDetailProvider;
import com.epeisong.model.Bulletin;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.android.AsyncTask;

/**
 * 查看公告
 * 
 * @author poet
 *
 */
public class BulletinDetailActivity extends BaseActivity implements OnClickListener {

    public static final String EXTRA_BULLETIN_ID = "bulletin_id";
    public static final String EXTRA_FINISH_TO_MAIN = "finish_to_main";

    boolean mFinishToMain;
    
    private String mBulletinId;

    private ImageView iv_head;
    private TextView tv_name;
    private TextView tv_content;
    private TextView tv_time;
    private Button btn_del;

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        BulletinDetailProvider bdprovider = new BulletinDetailProvider();
        bdprovider.delById(mBulletinId); // 删除公告
        this.finish();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "公告详情", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBulletinId = getIntent().getStringExtra(EXTRA_BULLETIN_ID);
        mFinishToMain = getIntent().getBooleanExtra(EXTRA_FINISH_TO_MAIN, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_detail);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        btn_del = (Button) findViewById(R.id.btn_del);

        btn_del.setOnClickListener(this);

        AsyncTask<Void, Void, Bulletin> task = new AsyncTask<Void, Void, Bulletin>() {
            @Override
            protected Bulletin doInBackground(Void... arg0) {
                return BulletinDao.getInstance().queryById(mBulletinId);
            }

            @Override
            protected void onPostExecute(Bulletin result) {
                if (result != null) {
                    tv_name.setText(result.getSender_name());
                    tv_content.setText(result.getContent());
                    tv_time.setText(DateUtil.long2YMDHM(result.getUpdate_time()));
                }
            };
        };
        task.execute();
    }

    @Override
    public void finish() {
        super.finish();
        if (mFinishToMain) {
       	 MainActivity.setTabPos(1,true);
         MainActivity.launchAndClear(this, 1);
       }
    }
 
}
