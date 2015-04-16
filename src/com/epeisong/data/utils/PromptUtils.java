/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.transaction.util.PromptUtils.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2015年3月5日下午2:06:19
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.data.utils;

public class PromptUtils {

    public static final int SUCC = 1;// 成功
    public static final int LOGISTICS_ID_ERROR = 2;// logisticsId 不正确
    public static final int DEAL_BILL_NULL = 3;// 没有找到订单
    public static final int INFO_FEE_AMOUNT_LESS_THAN_ZORE = 4;// 信息费小于0
    public static final int CREATE_DEAL_BILL_ERROR = 5;// 下订单失败
    public static final int UPDATE_INFO_FEE_AMOUNT_ERROR = 6;// 更新信息费失败
    public static final int UPDATE_INFO_FEE_STATUS_ERROR = 7;// 更新交易状态失败，状态已经被更改
    public static final int PAYER_FROZEN_ERROR_BALANCE_LOW = 8;// 付款方冻结担保金账户余额不足
    public static final int PAYER_FROZEN_ERROR = 9;// 付款方冻结担保金失败
    public static final int PAYEE_FROZEN_ERROR_BALANCE_LOW = 10;// 收款方冻结担保金账户余额不足
    public static final int PAYEE_FROZEN_ERROR = 11;// 收款方冻结担保金失败
    public static final int PAYER_THAW_ERROR_BALANCE_LOW = 12;// 付款方解冻担保账户余额不足
    public static final int PAYER_THAW_ERROR = 13;// 付款方解冻担保金失败
    public static final int PAYEE_THAW_ERROR_BALANCE_LOW = 14;// 收款方解冻担保账户余额不足
    public static final int PAYEE_THAW_ERROR = 15;// 收款方解冻担保金失败
    public static final int PAYER_WALLET_IS_NULL = 16;// 付款方钱包没有绑定
    public static final int PAYEE_WALLET_IS_NULL = 17;// 收款方钱包没有绑定
    public static final int INFO_FEE_AMOUNT_INCONSISTENT = 18;// 信息费前后不一致
    public static final int PAYMENT_PWD_ERROR = 19;// 支付密码不正确
    public static final int PAY_TO_PLATFORM_EXIST = 20;// 付款到平台，交易记录已经存在
    public static final int PAYER_WALLET_BALANCE_LOW = 21;// 付款方钱包余额不足
    public static final int PAY_TO_PLATFORM_ERROR = 22;// 付款到平台失败
    public static final int CONFIRM_PAY_ERROR = 23;// 确认支付失败
    public static final int CONFIRM_PAY_PAYMENT = 24;// 确认支付,已经付款
    public static final int INF0_FEE_COMPENSATE_ERROR = 25;// 信息费赔付失败
    public static final int INF0_FEE_COMPENSATE_IS_ALREADY = 26;// 信息费已经赔付
    public static final int PAYER_GUARANTEE_AMOUNT_COMPENSATE_ERROR = 27;// 付款方赔付担保金失败
    public static final int PAYER_GUARANTEE_AMOUNT_COMPENSATE_BALANCE_LOW = 28;// 付款方赔付担保金账户余额不足
    public static final int PAYEE_GUARANTEE_AMOUNT_COMPENSATE_ERROR = 29;// 收款方赔付担保金失败
    public static final int PAYEE_GUARANTEE_AMOUNT_COMPENSATE_BALANCE_LOW = 30;// 收款方赔付担保金账户余额不足
    public static final int INF0_FEE_REFUND_ERROR = 31;// 信息费退款失败
    public static final int INF0_FEE_REFUND_IS_ALREADY = 32;// 信息费已经退款
    public static final int COMPLAIN_TASK_NULL = 33;// 没有找到投诉任务
    public static final int UNAME_OR_PWD_ERROR = 34;// 登录用户名密码不正确
    public static final int EXEC_COMPLAIN_TASK_ERROR = 35;// 处理投诉任务失败
    public static final int COMPLAIN_TASK_UPDATE_INFO_FEE_STATUS_ERROR = 36;// 处理投诉任务中，更改信息费状态失败
    public static final int PAYER_GUARANTEE_AMOUNT_EXPROPRIATE_ERROR = 37;// 付款方被没收担保金失败
    public static final int PAYER_GUARANTEE_AMOUNT_EXPROPRIATE_BALANCE_LOW = 38;// 付款方被没收担保金账户余额不足
    public static final int PAYEE_GUARANTEE_AMOUNT_EXPROPRIATE_ERROR = 39;// 收款方被没收担保金失败
    public static final int PAYEE_GUARANTEE_AMOUNT_EXPROPRIATE_BALANCE_LOW = 40;// 收款方被没收担保金账户余额不足
    public static final int INF0_FEE_EXPROPRIATE_ERROR = 41;// 信息费没收失败
    public static final int INF0_FEE_EXPROPRIATE_IS_ALREADY = 42;// 信息费已经被没收
    public static final int FREIGHT_IS_ALREADY_ORDERED = 43;// 车源货源已经被预定
    public static final int IDENTITY_NO_IS_ERROR = 44;// 身份证号码不正确
    public static final int REAL_NAME_IS_ERROR = 45;// 姓名不正确
    public static final int FROZEN_WALLET_FAIL = 46;// 冻结钱包失败
    public static final int THAW_WALLET_FAIL = 47;// 解冻钱包失败
    public static final int WALLET_IS_NOT_AVAILABLE = 48;// 钱包未开通
    public static final int WALLET_IS_FROZEN = 49;// 钱包已冻结
    public static final int WALLET_IS_DISABLE = 50;// 钱包已停用

    public static String getPrompt(int resultError, boolean bPayer) {
        switch (resultError) {
        case LOGISTICS_ID_ERROR:
            return "用户ID错误";
        case DEAL_BILL_NULL:
            return "未找到订单";
        case INFO_FEE_AMOUNT_LESS_THAN_ZORE:
            return "信息费异常";
        case CREATE_DEAL_BILL_ERROR:
            return "下订单失败";
        case UPDATE_INFO_FEE_AMOUNT_ERROR:
            return "信息费修改失败";
        case UPDATE_INFO_FEE_STATUS_ERROR:
            return "操作失败，订单状态已变化";
        case PAYER_FROZEN_ERROR_BALANCE_LOW:
            if (bPayer) {
                return "您的保证金账户余额不足";
            } else {
                return "对方保证金账户余额不足";
            }
        case PAYER_FROZEN_ERROR:
            return "冻结保证金失败";
        case PAYEE_FROZEN_ERROR_BALANCE_LOW:
            if (bPayer) {
                return "对方保证金账户余额不足";
            } else {
                return "您的保证金账户余额不足";
            }
        case PAYEE_FROZEN_ERROR:
            return "冻结保证金失败";
        case PAYER_THAW_ERROR_BALANCE_LOW:
            if (bPayer) {
                return "解冻保证金失败";
            } else {
                return "解冻对方保证金失败";
            }
        case PAYER_THAW_ERROR:
            return "解冻保证金失败";
        case PAYEE_THAW_ERROR_BALANCE_LOW:
            if (bPayer) {
                return "解冻对方保证金失败";
            } else {
                return "解冻保证金失败";
            }
        case PAYEE_THAW_ERROR:
            return "解冻保证金失败";
        case PAYER_WALLET_IS_NULL:
            if (bPayer) {
                return "您还没有绑定钱包";
            } else {
                return "对方没有绑定钱包";
            }
        case PAYEE_WALLET_IS_NULL:
            if (bPayer) {
                return "对方没有绑定钱包";
            } else {
                return "您还没有绑定钱包";
            }
        case PAYMENT_PWD_ERROR:
            return "支付密码不正确";
        case PAY_TO_PLATFORM_EXIST:
            return "付款失败，记录已存在";
        case PAYER_WALLET_BALANCE_LOW:
            return "钱包余额不足";
        case PAY_TO_PLATFORM_ERROR:
            return "付款失败";
        case CONFIRM_PAY_ERROR:
            return "确认付款失败";
        case CONFIRM_PAY_PAYMENT:
            return "确认付款失败，订单已支付";
        case INF0_FEE_COMPENSATE_ERROR:
            return "赔付失败";
        case INF0_FEE_COMPENSATE_IS_ALREADY:
            return "已经赔付";
        case PAYER_GUARANTEE_AMOUNT_COMPENSATE_ERROR:// 付款方赔付保证金失败
            return "赔付保证金失败";
        case PAYER_GUARANTEE_AMOUNT_COMPENSATE_BALANCE_LOW:// 付款方赔付保证金账户余额不足
            return "保证金余额不足，赔付失败";
        case PAYEE_GUARANTEE_AMOUNT_COMPENSATE_ERROR:// 收款方赔付保证金失败
            return "赔付保证金失败";
        case PAYEE_GUARANTEE_AMOUNT_COMPENSATE_BALANCE_LOW:// 收款方赔付保证金账户余额不足
            return "保证金余额不足，赔付失败";
        case INF0_FEE_REFUND_ERROR:// 退款失败
            return "退款失败";
        case INF0_FEE_REFUND_IS_ALREADY:// 已经退款
            return "已经退款";
        case COMPLAIN_TASK_NULL:// 没有找到投诉任务
            return "没有找到投诉任务";
        case UNAME_OR_PWD_ERROR:// 登录用户名密码不正确
            return "登录用户名密码不正确";
        case EXEC_COMPLAIN_TASK_ERROR:// 处理投诉任务失败
            return "处理投诉任务失败";
        case COMPLAIN_TASK_UPDATE_INFO_FEE_STATUS_ERROR:// 处理投诉任务中，更改信息费状态失败
            return "处理投诉任务中";
        case PAYER_GUARANTEE_AMOUNT_EXPROPRIATE_ERROR:// 付款方被没收担保金失败
            if (bPayer) {
                return "没收担保金失败";
            }
            return "没收对方担保金失败";
        case PAYER_GUARANTEE_AMOUNT_EXPROPRIATE_BALANCE_LOW:// 付款方被没收担保金账户余额不足
            if (bPayer) {
                return "没收担保金账户余额不足";
            }
            return "没收对方担保金账户余额不足";
        case PAYEE_GUARANTEE_AMOUNT_EXPROPRIATE_ERROR:// 收款方被没收担保金失败
            if (bPayer) {
                return "没收对方担保金失败";
            }
            return "没收担保金失败";
        case PAYEE_GUARANTEE_AMOUNT_EXPROPRIATE_BALANCE_LOW:// 收款方被没收担保金账户余额不足
            if (bPayer) {
                return "没收对方担保金账户余额不足";
            }
            return "没收担保金账户余额不足";
        case INF0_FEE_EXPROPRIATE_ERROR:// 信息费没收失败
            return "信息费没收失败";
        case INF0_FEE_EXPROPRIATE_IS_ALREADY:// 信息费已经被没收
            return "信息费已经被没收";
        case FREIGHT_IS_ALREADY_ORDERED:// 车源货源已经被预定
            return "车源货源已经被预定";
        case IDENTITY_NO_IS_ERROR: //
            return "身份证号码不正确";
        case REAL_NAME_IS_ERROR:// 姓名不正确
            return "姓名不正确";
        case FROZEN_WALLET_FAIL: // 冻结钱包失败
            return "冻结钱包失败";
        case THAW_WALLET_FAIL: // 解冻钱包失败
            return "解冻钱包失败";
        case WALLET_IS_NOT_AVAILABLE: // 钱包未开通
            return "钱包未激活，请先激活钱包";
        case WALLET_IS_FROZEN:// 钱包已冻结
            return "钱包已冻结";
        case WALLET_IS_DISABLE:// 钱包已停用
            return "钱包已停用";
        default:
            return "未知错误:" + resultError;
        }
    }
}