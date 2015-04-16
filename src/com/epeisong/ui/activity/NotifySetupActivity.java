package com.epeisong.ui.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.speech.tts.TTSServiceFactory;
import com.epeisong.ui.view.SwitchButton;
import com.epeisong.ui.view.SwitchButton.OnSwitchListener;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.SpUtilsCur.KEYS_NOTIFY;

/**
 * 消息通知设置
 * 
 * @author poet
 * 
 */
public class NotifySetupActivity extends BaseActivity implements OnItemClickListener, OnSwitchListener, OnClickListener {

    private SwitchButton switch_button1;
    private SwitchButton switch_button2;
    private SwitchButton switch_button3;
    private SwitchButton switch_button4;
    private SwitchButton switch_button5;
    private RelativeLayout rl_start_time;
    private TextView tv_start_time = null;
    private RelativeLayout rl_end_time;
    private TextView tv_end_time = null;

    private long mStartTimeCur, mEndTimeCur;
    private long mStartTimeDefault, mEndTimeDefault;

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "消息提醒", null).setShowLogo(false);
    }

    DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
    final Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
    final Calendar dateEndTime = Calendar.getInstance(Locale.CHINA);

    TimePickerDialog.OnTimeSetListener startListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.YEAR, 0);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_YEAR, 0);
            long start = cal.getTimeInMillis();
            SpUtilsCur.put(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME, start);
            changeStartEndTime();
        }
    };
    TimePickerDialog.OnTimeSetListener endListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.YEAR, 0);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_YEAR, 0);
            long end = cal.getTimeInMillis();
            SpUtilsCur.put(SpUtilsCur.KEYS_NOTIFY.LONG_END_TIME, end);
            changeStartEndTime();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifysetup);
        switch_button1 = (SwitchButton) findViewById(R.id.switch_button1);
        switch_button1.setOnSwitchListener(NotifySetupActivity.this);
        switch_button2 = (SwitchButton) findViewById(R.id.switch_button2);
        switch_button2.setOnSwitchListener(NotifySetupActivity.this);
        switch_button3 = (SwitchButton) findViewById(R.id.switch_button3);
        switch_button3.setOnSwitchListener(NotifySetupActivity.this);
        switch_button4 = (SwitchButton) findViewById(R.id.switch_button4);
        switch_button4.setOnSwitchListener(NotifySetupActivity.this);
        switch_button5 = (SwitchButton) findViewById(R.id.switch_button5);
        switch_button5.setOnSwitchListener(NotifySetupActivity.this);
        rl_start_time = (RelativeLayout) findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(this);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        rl_end_time = (RelativeLayout) findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(this);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.HOUR_OF_DAY, 22);
        mStartTimeDefault = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 6);
        mEndTimeDefault = cal.getTimeInMillis();

        changeStartEndTime();

        boolean on;
        on = SpUtilsCur.getBoolean(KEYS_NOTIFY.BOOL_NOTIFY_NEWS_SOUND, true);
        switch_button1.setSwitch(on);
        on = SpUtilsCur.getBoolean(KEYS_NOTIFY.BOOL_NOTIFY_NEWS_SHAKE, true);
        switch_button2.setSwitch(on);
        on = SpUtilsCur.getBoolean(KEYS_NOTIFY.BOOL_NOTIFY_TASK_SOUND, true);
        switch_button3.setSwitch(on);
        on = SpUtilsCur.getBoolean(KEYS_NOTIFY.BOOL_NOTIFY_TASK_SHAKE, true);
        switch_button4.setSwitch(on);
        on = SpUtilsCur.getBoolean(KEYS_NOTIFY.BOOL_NO_DISTURB, false);
        switch_button5.setSwitch(on);
        if (on) {
            rl_start_time.setVisibility(View.VISIBLE);
            rl_end_time.setVisibility(View.VISIBLE);
        } else {
            rl_start_time.setVisibility(View.GONE);
            rl_end_time.setVisibility(View.GONE);
        }

        SwitchButton sb_tts = (SwitchButton) findViewById(R.id.sb_tts);
        sb_tts.setSwitch(SpUtilsCur.getBoolean(SpUtilsCur.KEYS_NOTIFY.BOOL_OPEN_TTS, true));
        sb_tts.setOnSwitchListener(this);
    }

    private void changeStartEndTime() {
        mStartTimeCur = SpUtilsCur.getLong(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME, mStartTimeDefault);
        tv_start_time.setText(DateUtil.long2HM(mStartTimeCur));

        mEndTimeCur = SpUtilsCur.getLong(SpUtilsCur.KEYS_NOTIFY.LONG_END_TIME, mEndTimeDefault);
        tv_end_time.setText(DateUtil.long2HM(mEndTimeCur));
    }

    @Override
    public void onSwitch(SwitchButton btn, boolean on) {
        switch (btn.getId()) {
        case R.id.switch_button1:
            SpUtilsCur.put(KEYS_NOTIFY.BOOL_NOTIFY_NEWS_SOUND, on);
            btn.setSwitch(on);
            break;
        case R.id.switch_button2:
            SpUtilsCur.put(KEYS_NOTIFY.BOOL_NOTIFY_NEWS_SHAKE, on);
            btn.setSwitch(on);
            break;

        case R.id.switch_button3:
            SpUtilsCur.put(KEYS_NOTIFY.BOOL_NOTIFY_TASK_SOUND, on);
            btn.setSwitch(on);
            break;
        case R.id.switch_button4:
            SpUtilsCur.put(KEYS_NOTIFY.BOOL_NOTIFY_TASK_SHAKE, on);
            btn.setSwitch(on);
            break;
        case R.id.switch_button5:
            SpUtilsCur.put(KEYS_NOTIFY.BOOL_NO_DISTURB, on);
            btn.setSwitch(on);
            if (SpUtilsCur.getBoolean(KEYS_NOTIFY.BOOL_NO_DISTURB, true)) {
                rl_start_time.setVisibility(View.VISIBLE);
                rl_end_time.setVisibility(View.VISIBLE);
                if (SpUtilsCur.getLong(KEYS_NOTIFY.LONG_START_TIME, 0) == 0) {
                    SpUtilsCur.put(KEYS_NOTIFY.LONG_START_TIME, mStartTimeDefault);
                    SpUtilsCur.put(KEYS_NOTIFY.LONG_END_TIME, mEndTimeDefault);
                }
            } else {
                rl_start_time.setVisibility(View.GONE);
                rl_end_time.setVisibility(View.GONE);
            }
            break;
        case R.id.sb_tts:
            SpUtilsCur.put(KEYS_NOTIFY.BOOL_OPEN_TTS, on);
            btn.setSwitch(on);
            if (!on) {
                TTSServiceFactory.getInstance().clear();
            }
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.rl_start_time:
            // SpUtilsCur.getLong(SpUtilsCur.KEYS_NOTIFY.LONG_START_TIME, def);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mStartTimeCur);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            new TimePickerDialog(NotifySetupActivity.this, startListener, hour, minute, true).show();
            break;
        case R.id.rl_end_time:
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTimeInMillis(mEndTimeCur);
            int hourEnd = calEnd.get(Calendar.HOUR_OF_DAY);
            int minuteEnd = calEnd.get(Calendar.MINUTE);
            new TimePickerDialog(NotifySetupActivity.this, endListener, hourEnd, minuteEnd, true).show();
            break;
        }

    }
}
