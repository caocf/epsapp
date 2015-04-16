package com.epeisong.net.request;

import com.google.protobuf.GeneratedMessage;

/**
 * 网络请求监听接口
 * @author poet
 *
 * @param <RESP>
 */
public interface OnNetRequestListener<RESP extends GeneratedMessage.Builder<?>> {
	void onSuccess(RESP response);

	void onFail(String msg);

	void onError();
}
