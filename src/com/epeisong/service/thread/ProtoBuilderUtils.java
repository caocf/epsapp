package com.epeisong.service.thread;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskReq;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskResp;
import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskReq;
import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskResp;
import com.epeisong.logistics.proto.Eps.AccountReq;
import com.epeisong.logistics.proto.Eps.AccountResp;
import com.epeisong.logistics.proto.Eps.AddContactServerPushReq;
import com.epeisong.logistics.proto.Eps.AuthenticationReq;
import com.epeisong.logistics.proto.Eps.AuthenticationResp;
import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.logistics.proto.Eps.BulletinServerPushReq;
import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.logistics.proto.Eps.ChatSendMultiTypeServerPushReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.logistics.proto.Eps.DeliverFreightServerPushReq;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.FreightServerPushReq;
import com.epeisong.logistics.proto.Eps.GeneralResp;
import com.epeisong.logistics.proto.Eps.GetMediaBytesReq;
import com.epeisong.logistics.proto.Eps.GetMediaBytesResp;
import com.epeisong.logistics.proto.Eps.GuaranteeReq;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.epeisong.logistics.proto.Eps.KickReq;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.logistics.proto.Eps.QRCodeReq;
import com.epeisong.logistics.proto.Eps.QRCodeResp;
import com.epeisong.logistics.proto.Eps.QuestionReq;
import com.epeisong.logistics.proto.Eps.QuestionResp;
import com.epeisong.logistics.proto.Eps.RecommendReq;
import com.epeisong.logistics.proto.Eps.RecommendResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeReq;
import com.epeisong.logistics.proto.Eps.SysDictionaryReq;
import com.epeisong.logistics.proto.Eps.SysDictionaryResp;
import com.epeisong.logistics.proto.Eps.SystemNoticeReq;
import com.epeisong.logistics.proto.Eps.SystemNoticeResp;
import com.epeisong.logistics.proto.Eps.UserLoginReq;
import com.epeisong.logistics.proto.Eps.UserLoginResp;
import com.epeisong.logistics.proto.Eps.UserQuitReq;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq;
import com.epeisong.logistics.proto.InfoFee.InfoFeeResp;
import com.epeisong.logistics.proto.Wallet.WalletReq;
import com.epeisong.logistics.proto.Wallet.WalletResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * @author cngaohk
 * @since Jun 20, 2014
 */
public class ProtoBuilderUtils {
    public static Builder newBuilder(int command) {
        Builder builder = null;
        {
            switch (command) {
            case CommandConstants.KICK_REQ:
                builder = KickReq.newBuilder();
                break;
            case CommandConstants.CREATE_ACCOUNT_REQ:
                builder = AccountReq.newBuilder();
                break;
            case CommandConstants.CREATE_ACCOUNT_RESP:
                builder = AccountResp.newBuilder();
                break;
            case CommandConstants.CREATE_GUARANTEE_PRODUCT_REQ:
            case CommandConstants.CREATE_GUARANTEE_PRODUCT_ORDER_REQ:
                builder = GuaranteeReq.newBuilder();
                break;
            case CommandConstants.CREATE_GUARANTEE_PRODUCT_RESP:
            case CommandConstants.CREATE_GUARANTEE_PRODUCT_ORDER_RESP:
                builder = GuaranteeResp.newBuilder();
                break;
            case CommandConstants.LIST_GUARANTEE_PRODUCT_REQ:
            case CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_REQ:
            case CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_CONTACT_REQ:
                builder = GuaranteeReq.newBuilder();
                break;
            case CommandConstants.LIST_GUARANTEE_PRODUCT_RESP:
            case CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_RESP:
            case CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_CONTACT_RESP:
                builder = GuaranteeResp.newBuilder();
                break;
            case CommandConstants.UPDATE_GUARANTEE_PRODUCT_STATUS_REQ:
                builder = GuaranteeReq.newBuilder();
                break;
            case CommandConstants.UPDATE_GUARANTEE_PRODUCT_STATUS_RESP:
                builder = GuaranteeResp.newBuilder();
                break;
            case CommandConstants.CREATE_SUB_ROLE_BY_PLAT_FORM_CUSTOMER_SERVICE_REQ:
                builder = LogisticsReq.newBuilder();
                break;
            case CommandConstants.CREATE_SUB_ROLE_BY_PLAT_FORM_CUSTOMER_SERVICE_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            case CommandConstants.CREATE_FREIGHT_REQ:
                builder = FreightReq.newBuilder();
                break;
            case CommandConstants.CREATE_FREIGHT_RESP:
                builder = FreightResp.newBuilder();
                break;
            case CommandConstants.CREATE_LOGISTICS_REQ:
                builder = LogisticsReq.newBuilder();
                break;
            case CommandConstants.CREATE_LOGISTICS_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            // 多媒体二进制文件获取
            case CommandConstants.GET_MEDIA_BYTES_REQ:
                builder = GetMediaBytesReq.newBuilder();
                break;
            case CommandConstants.GET_MEDIA_BYTES_RESP:
                builder = GetMediaBytesResp.newBuilder();
                break;
            // 系统字典更新
            case CommandConstants.CHECK_SYS_DICTIONARY_NEW_VERSOIN_FOR_UPDATE_REQ:
                builder = SysDictionaryReq.newBuilder();
                break;
            case CommandConstants.CHECK_SYS_DICTIONARY_NEW_VERSOIN_FOR_UPDATE_RESP:
                builder = SysDictionaryResp.newBuilder();
                break;
            // 发送验证码
            case CommandConstants.SEND_VERIFICATION_CODE_REQ:
                builder = SendVerificationCodeReq.newBuilder();
                break;
            case CommandConstants.SEND_VERIFICATION_CODE_RESP:
                builder = GeneralResp.newBuilder();
                break;
            // 获得、更新推荐数
            case CommandConstants.CREATE_OR_UPDATE_RECOMMEND_STATUS_REQ:
                builder = RecommendReq.newBuilder();
                break;
            case CommandConstants.CREATE_OR_UPDATE_RECOMMEND_STATUS_RESP:
                builder = RecommendResp.newBuilder();
                break;
            case CommandConstants.GET_SOMEONES_RECOMMEND_COUNT_FROM_MY_FRIENDS_REQ:
                builder = RecommendReq.newBuilder();
                break;
            case CommandConstants.GET_SOMEONES_RECOMMEND_COUNT_FROM_MY_FRIENDS_RESP:
                builder = RecommendResp.newBuilder();
                break;
            // 认证
            case CommandConstants.AUTHENTICATE_SOME_ONE_REQ:
                builder = AuthenticationReq.newBuilder();
                break;
            case CommandConstants.AUTHENTICATE_SOME_ONE_RESP:
                builder = AuthenticationResp.newBuilder();
                break;
            // 登录(被踢下来暂时使用此命令字，而builder 是UserLoginResp)
            case CommandConstants.USER_LOGIN_REQ:
                builder = UserLoginReq.newBuilder();
                break;
            case CommandConstants.USER_LOGIN_RESP:
                builder = UserLoginResp.newBuilder();
                break;
            // 退出登录
            case CommandConstants.USER_QUIT_REQ:
                builder = UserQuitReq.newBuilder();
                break;
            case CommandConstants.USER_QUIT_RESP:
                builder = GeneralResp.newBuilder();
                break;
            // 收到别人转发（通知）的车源货源
            case CommandConstants.DELIVER_FREIGHT_SERVER_PUSH_REQ:
                builder = DeliverFreightServerPushReq.newBuilder();
                break;
            // 收到联系人发的公告
            case CommandConstants.BULLETIN_SERVER_PUSH_REQ:
                builder = BulletinServerPushReq.newBuilder();
                break;
            // 关注我的人
            case CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ:
                builder = AddContactServerPushReq.newBuilder();
                break;
            // 公告
            case CommandConstants.CREATE_BULLETIN_REQ:
            case CommandConstants.LIST_OWN_BULLETINS_REQ:
            case CommandConstants.UPDATE_BULLETIN_STATUS_REQ:
            case CommandConstants.LIST_BULLETINS_REQ:
                builder = BulletinReq.newBuilder();
                break;
            case CommandConstants.CREATE_BULLETIN_RESP:
            case CommandConstants.LIST_OWN_BULLETINS_RESP:
            case CommandConstants.UPDATE_BULLETIN_STATUS_RESP:
            case CommandConstants.LIST_BULLETINS_RESP:
                builder = BulletinResp.newBuilder();
                break;
                //用户问题
            case CommandConstants.LIST_CUSTOM_SERVICE_TASK_REQ:
            case CommandConstants.GET_CUSTOM_SERVICE_TASK_REQ:
            case CommandConstants.UPDATE_CUSTOM_SERVICE_TASK_REQ:
            	builder = CustomServiceTaskReq.newBuilder();
            	break;
            case CommandConstants.LIST_CUSTOM_SERVICE_TASK_RESP:
            case CommandConstants.GET_CUSTOM_SERVICE_TASK_RESP:
            case CommandConstants.UPDATE_CUSTOM_SERVICE_TASK_RESP:
            	builder = CustomServiceTaskResp.newBuilder();
            	break;
            // 发布小黑板条数
            case CommandConstants.GET_FREIGHT_COUNT_ON_BLACK_BOARD_REQ:
                builder = FreightReq.newBuilder();
                break;
            case CommandConstants.GET_FREIGHT_COUNT_ON_BLACK_BOARD_RESP:
                builder = FreightResp.newBuilder();
                break;
            // 联系人
            case CommandConstants.DELETE_CONTACT_REQ:
            case CommandConstants.LIST_CONTACTS_REQ:
            case CommandConstants.SEARCH_CONTACT_REQ:
            case CommandConstants.ADD_CONTACT_REQ:
            case CommandConstants.SYNC_CONTACT_REQ:
            case CommandConstants.CHECK_MOBILE_ADDRESS_BOOK_REQ:// 检测手机通讯录的号码是否在平台注册
            case CommandConstants.UPDATE_CONTACT_STATUS_REQ:
                builder = ContactReq.newBuilder();
                break;
            case CommandConstants.DELETE_CONTACT_RESP:
            case CommandConstants.LIST_CONTACTS_RESP:
            case CommandConstants.SEARCH_CONTACT_RESP:
            case CommandConstants.ADD_CONTACT_RESP:
            case CommandConstants.SYNC_CONTACT_RESP:
            case CommandConstants.CHECK_MOBILE_ADDRESS_BOOK_RESP:
            case CommandConstants.UPDATE_CONTACT_STATUS_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            // account
            case CommandConstants.UPDATE_PASSWORD_REQ:
            case CommandConstants.CHANGE_MOBLE_REQ:
            case CommandConstants.FORGET_PASSWORD_REQ:
                builder = AccountReq.newBuilder();
                break;
            case CommandConstants.UPDATE_PASSWORD_RESP:
            case CommandConstants.CHANGE_MOBLE_RESP:
            case CommandConstants.FORGET_PASSWORD_RESP:
                builder = AccountResp.newBuilder();
                break;

            // logistics
            case CommandConstants.GET_LOGISTICS_REQ:
            case CommandConstants.GET_TOTAL_LOGISTIC_INFO_REQ:
            case CommandConstants.UPDATE_LOGISTICS_INFO_REQ:
            case CommandConstants.UPDATE_LOGISTIC_SUBSCRIBE_REQ:
            case CommandConstants.CLEAR_CURRENT_LOCATION_INFO_REQ:
            case CommandConstants.COMPLAIN_REQ:
            case CommandConstants.LIST_COMPLAINTS_REQ:
            case CommandConstants.UPDATE_COMPLAINT_STATUS_REQ:
            case CommandConstants.GET_COUNT_OF_HAS_BEEN_COMPLAINED_REQ:
            case CommandConstants.LIST_PEOPLE_THAT_COMPLAINS_YOU_REQ:
            case CommandConstants.GET_COMPLAINT_BY_PHONE_NUMBER_REQ:
            case CommandConstants.GET_COMPLAINT_BY_ID_REQ:
                builder = LogisticsReq.newBuilder();
                break;
            case CommandConstants.GET_LOGISTICS_RESP:
            case CommandConstants.GET_TOTAL_LOGISTIC_INFO_RESP:
            case CommandConstants.UPDATE_LOGISTICS_INFO_RESP:
            case CommandConstants.UPDATE_LOGISTIC_SUBSCRIBE_RESP:
            case CommandConstants.UPDATE_SUB_LOGISTICS_INFO_RESP:
            case CommandConstants.CLEAR_CURRENT_LOCATION_INFO_RESP:
            case CommandConstants.COMPLAIN_RESP:
            case CommandConstants.LIST_COMPLAINTS_RESP:
            case CommandConstants.UPDATE_COMPLAINT_STATUS_RESP:
            case CommandConstants.GET_COUNT_OF_HAS_BEEN_COMPLAINED_RESP:
            case CommandConstants.LIST_PEOPLE_THAT_COMPLAINS_YOU_RESP:
            case CommandConstants.GET_COMPLAINT_BY_PHONE_NUMBER_RESP:
            case CommandConstants.GET_COMPLAINT_BY_ID_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            // 车源货源
            case CommandConstants.GET_DEPENDENT_FREIGHT_DELIVERY_REQ:
            case CommandConstants.GET_FREIGHT_DELIVERY_REQ:
            case CommandConstants.GET_FREIGHT_REQ:
            case CommandConstants.GET_OWN_FREIGHT_REQ:
            case CommandConstants.DELIVER_FREIGHT_REQ: // 通知、转发给联系人
            case CommandConstants.REPASTE_FREIGHT_REQ: // 转发到小黑板
            case CommandConstants.LIST_LATEST_FREIGHT_DELIVERY_REQ:
            case CommandConstants.LIST_OLD_FREIGHT_DELIVERY_ADJOIN_LOCAL_REQ:
            case CommandConstants.LIST_NEW_FREIGHT_DELIVERY_ADJOIN_LOCAL_REQ:
            case CommandConstants.LIST_MISSED_FREIGHT_DELIVERY_REQ:
            case CommandConstants.LIST_FREIGHTS_ON_BLACK_BOARD_BY_CREATE_TIME_REQ:// LIST_OWN_FREIGHT_ON_BLACK_BOARD_BY_CREATE_TIME_REQ:
            case CommandConstants.SEARCH_FREIGHT_BY_LOCATION_REQ:
            case CommandConstants.UPDATE_FREIGHT_STATUS_REQ:
            case CommandConstants.UPDATE_FREIGHT_DELIVERY_RECEIVER_STATUS_REQ:// 更新朋友发来的车源货源状态
            case CommandConstants.UPDATE_FREIGHT_WHETHER_POST_TO_MARKET_SCREEN_STATUS_REQ:
            case CommandConstants.DELETE_ALL_INVALID_FREIGHTS_ON_BLACK_BOARD_REQ:// 删除所有无效的车源货源
                builder = FreightReq.newBuilder();
                break;
            case CommandConstants.GET_DEPENDENT_FREIGHT_DELIVERY_RESP:
            case CommandConstants.GET_FREIGHT_DELIVERY_RESP:
            case CommandConstants.GET_FREIGHT_RESP:
            case CommandConstants.GET_OWN_FREIGHT_RESP:
            case CommandConstants.DELIVER_FREIGHT_RESP:
            case CommandConstants.REPASTE_FREIGHT_RESP:
            case CommandConstants.LIST_LATEST_FREIGHT_DELIVERY_RESP:
            case CommandConstants.LIST_OLD_FREIGHT_DELIVERY_ADJOIN_LOCAL_RESP:
            case CommandConstants.LIST_NEW_FREIGHT_DELIVERY_ADJOIN_LOCAL_RESP:
            case CommandConstants.LIST_MISSED_FREIGHT_DELIVERY_RESP:
            case CommandConstants.LIST_FREIGHTS_ON_BLACK_BOARD_BY_CREATE_TIME_RESP:// LIST_OWN_FREIGHT_ON_BLACK_BOARD_BY_CREATE_TIME_RESP:
            case CommandConstants.SEARCH_FREIGHT_BY_LOCATION_RESP:
            case CommandConstants.UPDATE_FREIGHT_STATUS_RESP:
            case CommandConstants.UPDATE_FREIGHT_DELIVERY_RECEIVER_STATUS_RESP:
            case CommandConstants.UPDATE_FREIGHT_WHETHER_POST_TO_MARKET_SCREEN_STATUS_RESP:
            case CommandConstants.DELETE_ALL_INVALID_FREIGHTS_ON_BLACK_BOARD_RESP:
                builder = FreightResp.newBuilder();
                break;
            // 聊天
            case CommandConstants.CHAT_SEND_MULTI_TYPE_REQ:
            case CommandConstants.LIST_MISSED_CHATS_REQ:
            case CommandConstants.LIST_OLD_CHATS_ADJOIN_LOCAL_REQ:
            case CommandConstants.LIST_NEW_CHATS_ADJOIN_LOCAL_REQ:
            case CommandConstants.LIST_EARLIEST_CHATS_REQ:
            case CommandConstants.LIST_LATEST_CHATS_REQ:
            case CommandConstants.CHAT_UPDATE_REQ:
                builder = ChatReq.newBuilder();
                break;
            case CommandConstants.CHAT_SEND_MULTI_TYPE_RESP:
            case CommandConstants.LIST_MISSED_CHATS_RESP:
            case CommandConstants.LIST_OLD_CHATS_ADJOIN_LOCAL_RESP:
            case CommandConstants.LIST_NEW_CHATS_ADJOIN_LOCAL_RESP:
            case CommandConstants.LIST_EARLIEST_CHATS_RESP:
            case CommandConstants.LIST_LATEST_CHATS_RESP:
            case CommandConstants.CHAT_UPDATE_RESP:
                builder = ChatResp.newBuilder();
                break;
            // 收到聊天
            case CommandConstants.CHAT_SEND_MULTI_TYPE_SERVER_PUSH_REQ:
                builder = ChatSendMultiTypeServerPushReq.newBuilder();
                break;
            // 配货市场
            // case CommandConstants.SEARCH_MARKETS_BY_LOCATION_REQ:
            case CommandConstants.LIST_LATEST_FREIGHTS_ON_MARKET_SCREEN_REQ:
            case CommandConstants.LIST_OLDER_FREIGHTS_ADJOIN_LOCAL_FROM_MARKET_SCREEN_REQ:
            case CommandConstants.LIST_NEWER_FREIGHTS_ON_MARKET_SCREEN_BY_EPS_REQ:
            case CommandConstants.LIST_REGIONS_MARKET_CONTAINED_REQ:
            case CommandConstants.ADD_BANNED_LOGISTIC_REQ:
            case CommandConstants.DELETE_BANNED_LOGISTIC_REQ:
            case CommandConstants.GET_MARKET_SCREEN_BANNED_LIST_REQ:
            case CommandConstants.UPDATE_IS_ALLOW_TO_SHOW_STATUS_ON_MARKET_SCREEN_REQ:
            case CommandConstants.GET_MEMBERS_REQ:
            case CommandConstants.UPDATE_MEMBER_STATUS_REQ:
            case CommandConstants.ADD_MEMBER_REQ:
            case CommandConstants.DELETE_MEMBER_REQ:
            case CommandConstants.MANAGE_MEMBERS_REQ:
            case CommandConstants.ADD_REGION_TO_MARKET_REQ:
            case CommandConstants.DELETE_REGION_FROM_MARKET_REQ:
                builder = SearchCommonLogisticsReq.newBuilder();
                break;
            case CommandConstants.SEARCH_MARKETS_BY_LOCATION_RESP:
            case CommandConstants.LIST_LATEST_FREIGHTS_ON_MARKET_SCREEN_RESP:
            case CommandConstants.LIST_OLDER_FREIGHTS_ADJOIN_LOCAL_FROM_MARKET_SCREEN_RESP:
            case CommandConstants.LIST_NEWER_FREIGHTS_ON_MARKET_SCREEN_BY_EPS_RESP:
            case CommandConstants.LIST_REGIONS_MARKET_CONTAINED_RESP:
            case CommandConstants.ADD_BANNED_LOGISTIC_RESP:
            case CommandConstants.DELETE_BANNED_LOGISTIC_RESP:
            case CommandConstants.GET_MARKET_SCREEN_BANNED_LIST_RESP:
            case CommandConstants.UPDATE_IS_ALLOW_TO_SHOW_STATUS_ON_MARKET_SCREEN_RESP:
            case CommandConstants.GET_MEMBERS_RESP:
            case CommandConstants.UPDATE_MEMBER_STATUS_RESP:
            case CommandConstants.ADD_MEMBER_RESP:
            case CommandConstants.DELETE_MEMBER_RESP:
            case CommandConstants.MANAGE_MEMBERS_RESP:
            case CommandConstants.ADD_REGION_TO_MARKET_RESP:
            case CommandConstants.DELETE_REGION_FROM_MARKET_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            // 整车运输
            case CommandConstants.SEARCH_ENTIRE_VEHICLE_TRANSHIP_GOODS_REQ:
            case CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_REQ:
            case CommandConstants.SEARCH_LESS_THAN_TRUCK_LOAD_AND_LINE_REQ:
            case CommandConstants.SEARCH_INSURANCE_REQ:
            case CommandConstants.SEARCH_COURIER_REQ:
            case CommandConstants.SEARCH_STOWAGE_INFORMATION_DEPARTMENT_REQ:
            case CommandConstants.SEARCH_EQUIPMENT_LEASING_REQ:
            case CommandConstants.SEARCH_GOODS_TYPE_OF_LOGISTIC_REQ:
            case CommandConstants.SEARCH_STORAGE_REQ:
            case CommandConstants.SEARCH_PACKAGING_REQ:
            case CommandConstants.SEARCH_MOVE_HOUSE_REQ:
                // case CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_REQ:
            case CommandConstants.SEARCH_MARKETS_BY_LOCATION_REQ:
            case CommandConstants.SEARCH_PICKUP_POINT_EXPRESS_CITY_DISTRIBUTION_REQ:
            case CommandConstants.SEARCH_THIRD_PART_LOGISTIC_REQ:
                builder = SearchCommonLogisticsReq.newBuilder();
                break;
            case CommandConstants.SEARCH_ENTIRE_VEHICLE_TRANSHIP_GOODS_RESP:
            case CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_RESP:
            case CommandConstants.SEARCH_LESS_THAN_TRUCK_LOAD_AND_LINE_RESP:
            case CommandConstants.SEARCH_INSURANCE_RESP:
            case CommandConstants.SEARCH_COURIER_RESP:
            case CommandConstants.SEARCH_STOWAGE_INFORMATION_DEPARTMENT_RESP:
            case CommandConstants.SEARCH_EQUIPMENT_LEASING_RESP:
            case CommandConstants.SEARCH_GOODS_TYPE_OF_LOGISTIC_RESP:
            case CommandConstants.SEARCH_STORAGE_RESP:
            case CommandConstants.SEARCH_PACKAGING_RESP:
            case CommandConstants.SEARCH_MOVE_HOUSE_RESP:
            case CommandConstants.SEARCH_PICKUP_POINT_EXPRESS_CITY_DISTRIBUTION_RESP:
            case CommandConstants.SEARCH_THIRD_PART_LOGISTIC_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;

            // complain
            case CommandConstants.LIST_COMPLAIN_TASK_REQ:
            case CommandConstants.GET_COMPLAIN_TASK_REQ:
                builder = ComplainTaskReq.newBuilder();
                break;
            case CommandConstants.LIST_COMPLAIN_TASK_RESP:
            case CommandConstants.GET_COMPLAIN_TASK_RESP:
                builder = ComplainTaskResp.newBuilder();
                break;
            // info fee start
            case CommandConstants.GET_INFO_FEE_REQ:
            case CommandConstants.CREATE_INFO_FEE_REQ:
            case CommandConstants.LIST_INFO_FEE_REQ:
            case CommandConstants.UPDATE_INFO_FEE_FLOW_STATUS_REQ:
            case CommandConstants.UPDATE_DRAWBACK_STATUS_REQ:
            case CommandConstants.UPDATE_INFO_FEE_STATUS_REQ:
            case CommandConstants.UPDATE_INFO_FEE_AMOUNT_REQ:
            case CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ:
                builder = InfoFeeReq.newBuilder();
                break;
            case CommandConstants.GET_INFO_FEE_RESP:
            case CommandConstants.LIST_INFO_FEE_RESP:
            case CommandConstants.UPDATE_INFO_FEE_FLOW_STATUS_RESP:
            case CommandConstants.UPDATE_DRAWBACK_STATUS_RESP:
            case CommandConstants.UPDATE_INFO_FEE_STATUS_RESP:
            case CommandConstants.UPDATE_INFO_FEE_AMOUNT_RESP:
                builder = InfoFeeResp.newBuilder();
                break;
            // 创建订单返回
            case CommandConstants.CREATE_INFO_FEE_RESP:
                builder = FreightResp.newBuilder();
                break;
            // 隐私
            case CommandConstants.UPDATE_PRIVACY_REQ:
            case CommandConstants.GET_WHETHER_CAN_DO_STATUS_REQ:
            case CommandConstants.GET_PRIVACY_REQ:
                builder = LogisticsReq.newBuilder();
                break;
            case CommandConstants.UPDATE_PRIVACY_RESP:
            case CommandConstants.GET_WHETHER_CAN_DO_STATUS_RESP:
            case CommandConstants.GET_PRIVACY_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            // 标签
            case CommandConstants.LIST_SOME_ONES_OWN_ETAGS_REQ:
            case CommandConstants.TAG_SOME_ONE_REQ:
            case CommandConstants.CREATE_ETAG_REQ:
            case CommandConstants.DO_NOT_TAG_SOME_ONE_REQ:
                builder = ContactReq.newBuilder();
                break;
            case CommandConstants.LIST_SOME_ONES_OWN_ETAGS_RESP:
            case CommandConstants.TAG_SOME_ONE_RESP:
            case CommandConstants.CREATE_ETAG_RESP:
            case CommandConstants.DO_NOT_TAG_SOME_ONE_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;

            // 咨询帮助
            case CommandConstants.LIST_QUESTIONS_REQ:
            case CommandConstants.GET_CUSTOMER_SERVICE_REQ:
                builder = QuestionReq.newBuilder();
                break;
            case CommandConstants.LIST_QUESTIONS_RESP:
            case CommandConstants.GET_CUSTOMER_SERVICE_RESP:
                builder = QuestionResp.newBuilder();
                break;

            // 钱包
            case CommandConstants.CREATE_RLOGISTICS_WALLET_REQ:
            case CommandConstants.GET_WALLET_REQ:
                builder = WalletReq.newBuilder();
                break;
            case CommandConstants.CREATE_RLOGISTICS_WALLET_RESP:
            case CommandConstants.GET_WALLET_RESP:
                builder = WalletResp.newBuilder();
                break;
            // 车源货源order status
            case CommandConstants.FREIGHT_SERVER_ORDER_STATUS_PUSH_REQ:
                builder = FreightServerPushReq.newBuilder();
                break;
            // 生成二维码url
            case CommandConstants.GENERATE_QR_CODE_TO_ADD_CONTACT_REQ:
                builder = QRCodeReq.newBuilder();
                break;
            case CommandConstants.GENERATE_QR_CODE_TO_ADD_CONTACT_RESP:
                builder = QRCodeResp.newBuilder();
                break;
            // 首页找车找货
            case CommandConstants.LIST_LATEST_FREIGHTS_ON_MARKET_SCREEN_BY_EPS_REQ:
                builder = SearchCommonLogisticsReq.newBuilder();
                break;
            case CommandConstants.LIST_LATEST_FREIGHTS_ON_MARKET_SCREEN_BY_EPS_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            case CommandConstants.LIST_LOGISTICS_BY_LOCALE_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            case CommandConstants.SYSTEM_NOTICE_SERVER_PUSH_REQ:
                builder = SystemNoticeReq.newBuilder();
                break;
            case CommandConstants.SYSTEM_NOTICE_SERVER_PUSH_RESP:
                builder = SystemNoticeResp.newBuilder();
                break;
            case CommandConstants.LIST_LOGISTICS_AROUND_A_CENTER_REQ:
                builder = LogisticsReq.newBuilder();
                break;
            case CommandConstants.LIST_LOGISTICS_AROUND_A_CENTER_RESP:
                builder = CommonLogisticsResp.newBuilder();
                break;
            }

        }

        return builder;
    }
}
