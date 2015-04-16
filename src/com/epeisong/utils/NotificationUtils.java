package com.epeisong.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.RemoteViews;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils.InfoFeeStatusParams;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils.InfoFeeStatusResult;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Bulletin;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Freight;
import com.epeisong.model.InfoFee;
import com.epeisong.ui.activity.BulletinDetailActivity;
import com.epeisong.ui.activity.ChatRoomActivity;
import com.epeisong.ui.activity.ComplaintDealDetailActivity;
import com.epeisong.ui.activity.FreightDetailActivity;
import com.epeisong.ui.activity.InfoFeeDetailActivity;
import com.epeisong.ui.activity.TransferWithdrawalDetailActivity;

/**
 * 通知栏工具类
 * @author poet
 *
 */
@SuppressWarnings("deprecation")
public class NotificationUtils {

    private static final int ID_CHAT_MSG = 100;
    private static final int ID_BULLETIN = 101;
    private static final int ID_FREIGHT = 102;

    private static Context context = EpsApplication.getInstance();

    public static void notify(ChatMsg msg) {
        Intent intent;
        if (msg.getBusiness_type() == ChatMsg.business_type_freight) {
            intent = new Intent(context, FreightDetailActivity.class);
            BusinessChatModel model = BusinessChatModel.getFromChatMsg(msg);
            intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(FreightDetailActivity.EXTRA_SHOW_ADVISORY_FIRST, true);
            intent.putExtra(FreightDetailActivity.EXTRA_FINISH_TO_MAIN, true);
            if (UserDao.getInstance().getUser().getId().equals(model.getBusiness_owner_id())) {
                intent.putExtra(FreightDetailActivity.EXTRA_ADVISORYER_ID, msg.getRemoteId());
            }
        }else if(msg.getBusiness_type() == ChatMsg.business_type_complaint){
        	intent = new Intent(context, ComplaintDealDetailActivity.class);
            BusinessChatModel model = BusinessChatModel.getFromChatMsg(msg);
            intent.putExtra(ComplaintDealDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(ComplaintDealDetailActivity.EXTRA_SHOW_ADVISORY_FIRST, true);
            intent.putExtra(ComplaintDealDetailActivity.EXTRA_FINISH_TO_MAIN, true);
            if (UserDao.getInstance().getUser().getId().equals(model.getBusiness_owner_id())) {
                intent.putExtra(ComplaintDealDetailActivity.EXTRA_ADVISORYER_ID, msg.getRemoteId());
            }
        } else if (msg.getBusiness_type() == ChatMsg.business_type_info_fee) {
            intent = new Intent(context, InfoFeeDetailActivity.class);
            BusinessChatModel model = BusinessChatModel.getFromChatMsg(msg);
            intent.putExtra(InfoFeeDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(InfoFeeDetailActivity.EXTRA_SHOW_CHAT_FIRST, true);
            intent.putExtra(InfoFeeDetailActivity.EXTRA_REMOTE_ID, msg.getRemoteId());
            intent.putExtra(InfoFeeDetailActivity.EXTRA_FINISH_TO_MAIN, true);
        } else if (msg.getBusiness_type() == ChatMsg.business_type_normal) {
            intent = new Intent(context, ChatRoomActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, msg.getRemoteId());
            intent.putExtra(ChatRoomActivity.EXTRA_FINISH_TO_MAIN, true);
        }else if(msg.getBusiness_type() == ChatMsg.business_type_withdraw){
        	intent = new Intent(context, TransferWithdrawalDetailActivity.class);
            BusinessChatModel model = BusinessChatModel.getFromChatMsg(msg);
            intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(TransferWithdrawalDetailActivity.EXTRA_REMOTE_ID, msg.getRemoteId());
        } else {
            return;
        }
        int statusIconId = R.drawable.ic_notication;
        String statusText = msg.getSender_name() + "：" + msg.getShowContent();
        int iconId = R.drawable.main_bottom_message_selected;
        String contentTitle = msg.getSender_name();
        String contentText = msg.getShowContent();
        notify(ID_CHAT_MSG, statusIconId, statusText, intent, iconId, contentTitle, contentText);
    }

    public static void notify(Bulletin bulletin) {
        Intent intent = new Intent(context, BulletinDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BulletinDetailActivity.EXTRA_BULLETIN_ID, bulletin.getId());
        intent.putExtra(BulletinDetailActivity.EXTRA_FINISH_TO_MAIN, true);
        int statusIconId = R.drawable.ic_notication;
        String statusText = "朋友发布了一条新公告";
        int iconId = R.drawable.message_bulletin;
        String contentTitle = "公告";
        String contentText = bulletin.getContent();
        notify(ID_BULLETIN, statusIconId, statusText, intent, iconId, contentTitle, contentText);
    }

    public static void notify(Freight f) {
        Intent intent = new Intent(context, FreightDetailActivity.class);
        intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, BusinessChatModel.getFromFreight(f));
        intent.putExtra(FreightDetailActivity.EXTRA_FINISH_TO_MAIN, true);
        int statusIconId = R.drawable.ic_notication;
        String statusText;
        int iconId;
        if (f.getType() == Freight.TYPE_GOODS) {
            statusText = "收到一条朋友发来的货源";
            iconId = R.drawable.icon_freight_goods;
        } else {
            statusText = "收到一条朋友发来的车源";
            iconId = R.drawable.icon_freight_truck;
        }
        String contentTitle = f.getStart_region() + "-" + f.getEnd_region();
        String contentText = f.getDesc();
        notify(ID_FREIGHT, statusIconId, statusText, intent, iconId, contentTitle, contentText);
    }
    
    public static void notify(InfoFee infoFee,boolean isNewOrder) {
        Intent intent = new Intent(context, InfoFeeDetailActivity.class);
        BusinessChatModel model = BusinessChatModel.getFromInfoFee(infoFee);
        intent.putExtra(InfoFeeDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
        intent.putExtra(InfoFeeDetailActivity.EXTRA_INFO_FEE, infoFee);
        intent.putExtra(InfoFeeDetailActivity.EXTRA_FINISH_TO_MAIN, true);
        int statusIconId = R.drawable.ic_notication;
        String statusText = "您收到一条新的订单动态";
        if(isNewOrder ) {
          statusText = "您有一条新的订单";
        }  
 
        
        int iconId = R.drawable.icon_common_msg_info_fee;
        
        
        StringBuilder sb = new StringBuilder();
        String mineId = UserDao.getInstance().getUser().getId();
        InfoFeeStatusResult result = InfoFeeStatusUtils.getResult(new InfoFeeStatusParams(mineId, infoFee));
        String str = sb.append(result.source+"：" + result.sourceName +" 状态："+ result.getStatus()).toString();
        
   
        String contentTitle = infoFee.getFreightAddr();
        String contentText = str;//infoFee.getFreightInfo();
        notify(0, statusIconId, statusText, intent, iconId, contentTitle, contentText);
    }

    private static void notify(int id, int statusIconId, String statusText, Intent intent, int iconId,
            String contentTitle, String contentText) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        opts.inPurgeable = true;
        BitmapFactory.decodeResource(context.getResources(), iconId, opts);
        if (opts.outWidth > 36) {
            opts.inSampleSize = Math.round(opts.outWidth / 36);
        }
        opts.inJustDecodeBounds = false;
        Bitmap iconBmp = BitmapFactory.decodeResource(context.getResources(), iconId, opts);
        notify(iconId, statusIconId, statusText, intent, iconBmp, contentTitle, contentText);
    }

    private static void notify(int id, int statusIconId, String statusText, Intent intent, Bitmap iconBmp,
            String contentTitle, String contentText) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(statusIconId, statusText, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.when = System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
        notification.contentView.setImageViewBitmap(android.R.id.icon, iconBmp);
        manager.notify(0, notification);
    }

    @SuppressWarnings("unused")
    private static void notifyCustomView(int id, int statusIconId, String statusText, Intent intent, int iconId,
            String contentTitle, String contentText) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.title, contentTitle);
        remoteViews.setTextViewText(R.id.text, contentText);
        remoteViews.setImageViewResource(R.id.icon, iconId);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(statusIconId, statusText, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
        notification.contentView = remoteViews;
        manager.notify(0, notification);
    }

    public static void cancel(int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (id == -1) {
            manager.cancelAll();
        } else {
            manager.cancel(id);
        }
    }
}
