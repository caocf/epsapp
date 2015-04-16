package com.epeisong.data.layer02.abs;

import java.util.List;

import com.epeisong.model.ChatMsg;

/**
 * 抽取聊天消息的提供者接口，分裂不同实现算法
 * @author poet
 *
 */
public interface ChatMsgProvider {

    public List<ChatMsg> providerFirst(final String remote_id, final int size, final int business_type,
            final String business_id);

    public List<ChatMsg> provideNewer(final String remote_id, final int size, final int business_type,
            final String business_id, long last_time, long last_serial);

    public List<ChatMsg> provideOlder(final String remote_id, final int size, final int business_type,
            final String business_id, long last_time, long last_serial);
}
