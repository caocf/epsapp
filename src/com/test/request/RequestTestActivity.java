package com.test.request;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.base.view.viewinject.ViewInject;
import com.epeisong.base.view.viewinject.ViewInjecter;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;

/**
 * 多线程请求测试
 * @author poet
 *
 */
public class RequestTestActivity extends BaseActivity implements OnClickListener {

    @ViewInject(id = R.id.et_thread_count)
    EditText mThreadCountEt;

    @ViewInject(id = R.id.et_request_count)
    EditText mRequestCountEt;

    @ViewInject(id = R.id.tv_file_name)
    TextView mFileNameTv;

    @ViewInject(id = R.id.lv)
    ListView mListView;

    MyAdapter mAdapter;

    List<RequestThread> mRequestThreads = new ArrayList<RequestTestActivity.RequestThread>();

    String mLogFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_test);
        ViewInjecter.inject(this);
        findViewById(R.id.btn_start).setOnClickListener(this);

        mListView.setAdapter(mAdapter = new MyAdapter());
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "多线程请求测试");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (RequestThread t : mRequestThreads) {
            if (t != null) {
                t.stop = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_start:
            String threadCount = mThreadCountEt.getText().toString();
            String requestCount = mRequestCountEt.getText().toString();
            if (TextUtils.isEmpty(threadCount)) {
                ToastUtils.showToast("输入线程个数");
                return;
            }
            if (TextUtils.isEmpty(requestCount)) {
                ToastUtils.showToast("输入单线程请求个数");
                return;
            }
            start(Integer.parseInt(threadCount), Integer.parseInt(requestCount));
            break;
        }
    }

    private void start(int threadCount, int requestCount) {
        if (!mRequestThreads.isEmpty()) {
            ToastUtils.showToast("当前正在执行，" + mRequestThreads.size());
            return;
        }
        Calendar cal = Calendar.getInstance();
        String hms = "";
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            hms += "0";
        }
        hms += hour + "_";
        int minute = cal.get(Calendar.MINUTE);
        if (minute < 10) {
            hms += "0";
        }
        hms += minute + "_";
        int second = cal.get(Calendar.SECOND);
        if (second < 10) {
            hms += "0";
        }
        hms += second;
        mLogFileName = DateUtil.long2YMD(System.currentTimeMillis()) + "_" + hms + ".txt";

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "crash_eps_request_log");
        mFileNameTv.setText("日志文件：" + dir.getAbsolutePath() + "/" + mLogFileName);
        for (int i = 0; i < threadCount; i++) {
            RequestThread t = new RequestThread(requestCount, (i + 1)) {
                @Override
                protected RequestResult request(int requestCount, int index, int requestIndex) {
                    return RequestTest.test(requestCount, index, requestIndex);
                }
            };
            mRequestThreads.add(t);
            t.start();
        }
        mAdapter.replaceAll(mRequestThreads);
        ToastUtils.showToast(threadCount + "个线程启动完毕");
    }

    abstract class RequestThread extends Thread {
        boolean stop;
        int index;
        int requestCount;
        int successCount;
        int failCount;
        long duration;

        public RequestThread(int requestCount, int index) {
            this.requestCount = requestCount;
            this.index = index;
        }

        @Override
        public void run() {
            String threadName = "线程_" + index + ":";
            int count = 0;
            long start = System.currentTimeMillis();
            while (!stop && count++ < requestCount) {
                RequestResult result = request(this.requestCount, this.index, count);
                if (result.success) {
                    successCount++;
                } else {
                    failCount++;
                }
                String content = DateUtil.long2YMDHMSS(System.currentTimeMillis()) + "\n" + threadName + "第" + count
                        + "次请求:\n" + result.log + "\n\n";
                writeLog(mLogFileName, content);
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
            String content = threadName + requestCount + "次请求，" + successCount + "次成功,成功率：" + 100f * successCount
                    / requestCount + "%\n";
            writeLog(mLogFileName, content);
            stop = true;
            mRequestThreads.remove(this);

            duration = System.currentTimeMillis() - start;
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
            String str = threadName + "耗时：" + DateUtil.long2MS_SSS(duration) + "\n\n";
            writeLog(mLogFileName, str);
        }

        protected abstract RequestResult request(int threadRequestCount, int threadIndex, int requestIndex);
    }

    public static class RequestResult {
        public boolean success;
        public String log;
    }

    private void writeLog(String fileName, String content) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "crash_eps_request_log");
        if (!dir.exists()) {
            dir.mkdir();
        }

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(new File(dir, fileName), "rw");
            raf.seek(raf.length());
            raf.write(content.getBytes("GBK"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
            }
        }
    }

    class MyAdapter extends HoldDataBaseAdapter<RequestThread> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RatioView ratioView = null;
            if (convertView == null) {
                ratioView = new RatioView(RequestTestActivity.this);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(-1, DimensionUtls.getPixelFromDpInt(20));
                ratioView.setLayoutParams(params);
                convertView = ratioView;
            } else {
                ratioView = (RatioView) convertView;
            }
            RequestThread item = getItem(position);
            ratioView.setTotal(item.requestCount);
            ratioView.setSuccess(item.successCount);
            ratioView.setFail(item.failCount);
            if (item.stop) {
                ratioView.setDuration(DateUtil.long2MS_SSS(item.duration));
            }
            return convertView;
        }

    }
}
