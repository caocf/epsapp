package com.epeisong.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.DateTimePickDialogUtil;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.ToastUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 交易费管理
 * @author Jack
 *
 */
public class TradeManaActivity extends BaseActivity implements OnClickListener {

    private TextView tv_select_time;
    private LinearLayout rl_time;
    private ImageView iv_search;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trade_manage);
		
        tv_select_time = (TextView) findViewById(R.id.tv_select_time);
        tv_select_time.setText(new SimpleDateFormat("yyyy.MM.dd").format(Calendar.getInstance().getTime()));
        
        rl_time = (LinearLayout) findViewById(R.id.rl_time);
        rl_time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						TradeManaActivity.this);
				dateTimePicKDialog.dateTimePicKDialog(tv_select_time);

			}
		});
        
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
	}
    
    protected TitleParams getTitleParams() {
    	return new TitleParams(getDefaultHomeAction(), "交易费管理", null).setShowLogo(false);
    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        switch (id) {
        case R.id.btn_publish:
        	ToastUtils.showToast("发布");
        	break;
        }
    }
}
