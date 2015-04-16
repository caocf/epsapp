package com.epeisong.utils.android;

import com.epeisong.utils.LogUtils;

/**
 * 为了同一个页面在一个任务执行时不接受其他任务
 * @author poet
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class AsyncTaskEx<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private OnAsyncTaskListener listener;

    public AsyncTaskEx(OnAsyncTaskListener l) {
        listener = l;
        if (listener == null) {
            throw new IllegalArgumentException("OnAsyncTaskListener can not be null");
        }
    }
    
    public AsyncTaskEx(Class<?> clazz) {
        
    }

    @Override
    protected void onPreExecute() {
        listener.onAsyncTask(true);
    }

    @Override
    protected void onPostExecute(Result result) {
        listener.onAsyncTask(false);
        onPostExecuteEx(result);
    }

    protected abstract void onPostExecuteEx(Result result);

    @Override
    public AsyncTask<Params, Progress, Result> execute(Params... params) {
        if (listener.canRun()) {
            return super.execute(params);
        }
        LogUtils.d("AsyncTaskEx", "一个任务正在执行，放弃当前任务！");
        return this;
    }

    public interface OnAsyncTaskListener {
        void onAsyncTask(boolean isStart);

        boolean canRun();
    }
}
