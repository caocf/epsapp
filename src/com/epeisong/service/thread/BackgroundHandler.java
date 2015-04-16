package com.epeisong.service.thread;

import java.util.concurrent.LinkedBlockingQueue;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.logistics.net.Handler;
import com.epeisong.logistics.proto.Eps.UserLoginResp;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ReleaseLog;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 后台网络服务的消息处理者
 * 
 * @author poet
 * 
 */
public class BackgroundHandler implements Handler {

	private static final String TAG = "BackgroundHandler";

	private LinkedBlockingQueue<BuilderPackage> msgQueue;

	public BackgroundHandler(LinkedBlockingQueue<BuilderPackage> msgQueue) {
		this.msgQueue = msgQueue;

	}

	@Override
	public void connected(UserLoginResp.Builder resp) {
		int status = resp.getLoginStatus();// .getLonginStatus();
		LogUtils.e(this.getClass().getSimpleName(), "status:" + status);
		switch (status) {
		case -1: // 账号不存在
		case -2: // 密码错误
		case -3: // 账号被锁定
		case -4: // 时间验证错误
		case -5: // 你的账号在其他客户端登录
			EpsApplication.exit(MainActivity.sMainActivity,
					LoginActivity.class, resp.getDesc());
			break;
		default:
			if (false && status < 0) {
				EpsApplication.exit(MainActivity.sMainActivity,
						LoginActivity.class, resp.getDesc());
			}
			break;
		}
		// EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class,
		// "长时间未登录，请重新登录");
	}

	@Override
	public void debug(String arg0) {
		LogUtils.d(this, "debug:" + arg0);
		ReleaseLog.log(TAG, "debug:" + arg0);
	}

	@Override
	public void disconnected() {
		LogUtils.d(this, "debug:disconnected");
		ReleaseLog.log(TAG, "debug:disconnected");
	}

	@Override
	public void error(String arg0) {
		LogUtils.d(this, "error:" + arg0);
		ReleaseLog.log(TAG, "error:" + arg0);
	}

	@Override
	public void exception(String arg0, Exception arg1) {
		String log = LogUtils.e("BackgroundHandle.ex", arg1);
		ReleaseLog.log(TAG, "ex:" + log);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Builder msg, int command, int sequence) {
		LogUtils.saveLog("handle.debug", "entry handle");

		BuilderPackage builderPackage = new BuilderPackage(msg, command,
				sequence);
		try {
			msgQueue.put(builderPackage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LogUtils.saveLog("handle.debug", "exit handle");
	}

	@Override
	public void info(String arg0) {
		LogUtils.d(this, "info:" + arg0);
		ReleaseLog.log(TAG, "info:" + arg0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Builder newBuilder(int arg0) {
		return ProtoBuilderUtils.newBuilder(arg0);
	}

	@Override
	public void warn(String arg0) {
		LogUtils.d(this, "warn:" + arg0);
		ReleaseLog.log(TAG, "warn:" + arg0);
	}
}
