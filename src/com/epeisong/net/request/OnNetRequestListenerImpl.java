package com.epeisong.net.request;

import com.epeisong.utils.ToastUtils;
import com.google.protobuf.GeneratedMessage;

/**
 * 网络请求监听接口简单实现类
 * @author poet
 *
 * @param <RESP>
 */
public abstract class OnNetRequestListenerImpl<RESP extends GeneratedMessage.Builder<?>>
		implements OnNetRequestListener<RESP> {

	@Override
	public void onFail(String msg) {
		ToastUtils.showToast(msg);
	}
	
	@Override
	public void onError() {
	}
}
