package com.test.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SimpleListActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.LogUtils.LogListener;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.test.log.LogcatActivity.Log;

public class LogcatActivity extends SimpleListActivity<Log> implements
		LogListener {

	public static final String EXTRA_DATA = "data";

	private int mLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<Log> logs = (ArrayList<Log>) getIntent()
				.getSerializableExtra(EXTRA_DATA);
		mAdapter.setMaxSize(1000);
		if (logs != null && !logs.isEmpty()) {
			mAdapter.addAll(logs);
		}
		LogUtils.addLogListener(this);
	}

	@Override
	protected void onDestroy() {
		LogUtils.removeLogListener(this);
		Intent service = new Intent(this, LogcatService.class);
		startService(service);
		super.onDestroy();
	}

	@Override
	public void onLog(Log log) {
		boolean bLast = mListView.getLastVisiblePosition() >= mAdapter
				.getCount() - 3;
		mAdapter.addItem(log);
		if (bLast) {
			mListView.setSelection(mAdapter.getCount() - 1);
		}
	}

	@Override
	protected TitleParams getTitleParams() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(createAction());
		return new TitleParams(getDefaultHomeAction(), "LogCat", actions)
				.setShowLogo(false);
	}

	private Action createAction() {
		return new ActionImpl() {
			@Override
			public View getView() {
				Spinner sp = new Spinner(getApplicationContext());
				List<String> data = new ArrayList<String>();
				data.add("verbose");
				data.add("debug");
				data.add("info");
				data.add("warn");
				data.add("error");
				sp.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
						android.R.layout.simple_list_item_1, data));
				sp.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (mLevel != position) {
							mLevel = position;
							mAdapter.notifyDataSetChanged();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
				return sp;
			}
		};
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String tag = mAdapter.getItem(position).getTag();
		ToastUtils.showToast(tag);
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		Log item = mAdapter.getItem(position);
		if (item.getLevel() < mLevel) {
			return new View(getApplicationContext());
		}
		ViewHolder holder = null;
		if (convertView == null || !(convertView instanceof RelativeLayout)) {
			convertView = SystemUtils
					.inflate(R.layout.activity_logcat_list_view_item);
			holder = new ViewHolder();
			holder.findView(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.fillData(item);
		return convertView;
	}

	private class ViewHolder {
		TextView tv_time;
		TextView tv_log;

		public void findView(View v) {
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			tv_log = (TextView) v.findViewById(R.id.tv_log);
		}

		public void fillData(Log log) {
			tv_time.setText(DateUtil.long2HMSS(log.getTime()));
			tv_log.setText(log.getContent());
			if (log.getLevel() == LogListener.error) {
				tv_log.setTextColor(Color.RED);
			} else if (log.getLevel() == LogListener.warn) {
				tv_log.setTextColor(Color.argb(0x88, 0xAA, 0x00, 0x00));
			} else if (log.getLevel() == LogListener.debug) {
				tv_log.setTextColor(Color.BLACK);
			} else {
				tv_log.setTextColor(Color.GREEN);
			}
		}
	}

	public static class Log implements Serializable {
		private int level;
		private long time;
		private String tag;
		private String content;

		public Log(int level, long time, String tag, String content) {
			super();
			this.level = level;
			this.time = time;
			this.tag = tag;
			this.content = content;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
}
