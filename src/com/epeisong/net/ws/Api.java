package com.epeisong.net.ws;

import com.epeisong.net.ws.utils.BankCardResp;
import com.epeisong.net.ws.utils.BondWalletResp;
import com.epeisong.net.ws.utils.ComplainTaskResp;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.TaskResp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.net.ws.utils.WithdrawTaskResp;

/**
 * WebService 提供的操作接口
 * @author poet
 *
 */
public interface Api {

    Resp createAccountAndLogisticByPlatformManager(String mobileToCreate, int logisticType, String logisticTypeName,
            String name, int serveRegionCode, String serveRegionName, String mobileOfManager, String loginPwdOfManager,
            String payMentPwdOfManager, int operationType, int clientType, String passWord, 
            String contactName, String contactTelephone, String contactMobile, String selfIntroduction, int regionCode,
			String regionName, String address, double currentLongitude, double currentLatitude, int routeCodeA,
			String routeNameA, int routeCodeB, String routeNameB, int goodsTypeCode, String goodsTypeName,
			int transportTypeCode, String transportTypeName) throws Exception;

    Resp createAccount(String mobile, String password, String code) throws Exception;

    Resp forgetPassword(String mobile, String verifyCode, String newPwd) throws Exception;

    Resp updatePassword(String mobile, String oldPwd, String newPwd) throws Exception;

    //jack
    WalletResp frozenWallet(String uname, String upwd, String realname, String identifyNo) throws Exception;
    WalletResp thawWallet(String uname, String upwd, String realname, String identifyNo) throws Exception;
    
    WalletResp changeMobile(String loginName, String loginPwd, String code, int codeType, String payPwd,
            String oldMobile, String newMobile) throws Exception;

    WalletResp openWallet(String loginName, String loginPwd, String mobile, String code, int codeType, String realName,
            int idType, String idNum, String payPwd) throws Exception;

    WalletResp getWallet(String loginName, String loginPwd) throws Exception;

    WalletResp listDetail(String uname, String upwd, int detailId, int type, int count, int year, int month, int date)
            throws Exception;

    WalletResp withdrawToBankCard(String loginName, String loginPwd, long amount, String paymentPwd, int payeeType,
            String payerName, int payerLogisticsType, int bankCardId) throws Exception;
    
    /**
     * 其他问题
     */
    Integer createTask(String userAccount, String userName, String contactTel, int type , String detail) throws Exception;

    /**
     * 提现
     */
    WalletResp withdraw(String loginName, String loginPwd, long amount, String paymentPwd, int payeeType,
            String openBankName, String bankCode, String bankName, int bankRegionCode, String bankRegionName,
            String payeeName, String payeeAccount) throws Exception;

    /**
     * 修改支付密码<br>
     * 目前mobile、code、codeType3个字段可以随意填写，不取其值，只做占位
     * @param loginName：登录名
     * @param loginPwd：密码
     * @param mobile：手机号
     * @param code：验证码
     * @param codeType：验证码类型，Properties.VERIFICATION_CODE_PURPOSE_XXX
     * @param newPaymentPwd：新支付密码
     * @param oldPaymentPwd：原支付密码
     * @param bigClientType：客户端大类型，Properties.APP_CLIENT_BIG_TYPE_PHONE
     * @return
     * @throws Exception
     */
    WalletResp changePaymentPwd(String loginName, String loginPwd, String mobile, String code, int codeType,
            String newPaymentPwd, String oldPaymentPwd) throws Exception;

    /**
     * 忘记支付密码
     * @param loginName：登录名
     * @param loginPwd：密码
     * @param mobile：手机号
     * @param code：验证码
     * @param codeType：验证码类型，Properties.VERIFICATION_CODE_PURPOSE_XXX
     * @param idNum：身份证号
     * @param paymentPwd：支付密码
     * @param bigClientType：客户端大类型，Properties.APP_CLIENT_BIG_TYPE_PHONE
     * @return
     */
    WalletResp forgetPaymentPwd(String loginName, String loginPwd, String mobile, String code, int codeType,
            String idNum, String paymentPwd) throws Exception;

    WithdrawTaskResp execWithdrawTask(String uname, String upwd, int taskId, int status, int subStatus,
            String serialNumber, String note) throws Exception;

    WithdrawTaskResp listWithdrawTask(String loginName, String loginPwd, int taskId, int status, int type, int count)
            throws Exception;

    WithdrawTaskResp updateSerialNumber(String loginName, String loginPwd, int taskId, String serialNumber)
            throws Exception;

    WithdrawTaskResp cancelWithdrawTask(String uname, String upwd, String taskId, String note, String paymentPwd)
            throws Exception;
    
    WithdrawTaskResp getWithdrawTask(String uname, String upwd, String taskId) throws Exception;

    BankCardResp createBankCard(String uname, String upwd, String paymentPwd, String bankCode, String bankName,
            String cardNumber, String realName, String idType, String identityNumber, String openBankName,
            String regionCode, String regionName) throws Exception;

    BankCardResp deleteBankCard(String uname, String upwd, String paymentPwd, String bankCardId) throws Exception;

    BankCardResp listBankCard(String uname, String upwd) throws Exception;

    /**
     * 获取保证金账户
     * @param loginName
     * @param loginPwd
     * @return
     * @throws Exception
     */
    BondWalletResp getBondWallet(String loginName, String loginPwd) throws Exception;

    /**
     * 给担保账户充值（从钱包账户）
     */
    BondWalletResp deposit2BondWalletFromWallet(String loginName, String loginPwd, long amount, String paymentPwd)
            throws Exception;

    /**
     * 从担保账户提现（到钱包账户）
     * @return
     */
    BondWalletResp withdraw2WalletFromBondWallet(String loginName, String loginPwd, long amount, String paymentPwd)
            throws Exception;

    /**
     * 冻结担保账户金额
     */
    BondWalletResp frozenBondWallet(String loginName, String loginPwd, long amount, String paymentPwd) throws Exception;

    /**
     * 解冻担保账户金额
     */
    BondWalletResp thawBondWallet(String loginName, String loginPwd, long amount, String paymentPwd) throws Exception;

    /**
     * 赔付
     */
    BondWalletResp compensateByGuarantee(String loginName, String loginPwd, int payerWalletId, int payeeWalletId,
            long amount, int dealBillType, int dealBillId, String paymentPwd) throws Exception;

    /**
     * 赔付
     */
    BondWalletResp compensateByUser(String loginName, String loginPwd, int payeeWalletId, long amount,
            int dealBillType, int dealBillId, String paymentPwd) throws Exception;

    BondWalletResp BondlistDetail(String uname, String upwd, int detailId, int type, int count, int year, int month,
            int date) throws Exception;

    TaskResp startGuaranteeTask(String loginName, String loginPwd, String paymentPwd, int walletType) throws Exception;

    TaskResp stopGuaranteeTask(String loginName, String loginPwd, String paymentPwd, int walletType) throws Exception;

    TaskResp checkIsAuto(String loginName, String loginPwd, int walletType) throws Exception;

    TaskResp listGuaranteeTask(String loginName, String loginPwd, int status, int taskId, int count, int type,
            int walletType) throws Exception;
    
    ComplainTaskResp execComplainTask(String uname, String upwd, int taskId, int payerGuaranteeAmountResultType, 
    		int payerGuaranteeAmountResultId, int payeeGuaranteeAmountResultType, int payeeGuaranteeAmountResultId,
    		int infoFeeResultType, int infoFeeResultId, int dealBillType, String dealBillId, String note, String paymentPwd) throws Exception;

}
