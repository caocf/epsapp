package com.epeisong.net.ws;

import com.epeisong.net.ws.utils.BankCardResp;
import com.epeisong.net.ws.utils.BondWalletResp;
import com.epeisong.net.ws.utils.ComplainTaskResp;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.net.ws.utils.TaskResp;
import com.epeisong.net.ws.utils.WalletResp;
import com.epeisong.net.ws.utils.WithdrawTaskResp;

/**
 * WebService 请求执行者
 * @author poet
 *
 */
public class ApiExecutor implements Api {

    public static Api sApi = new ApiImpl();

    @Override
    public Resp createAccountAndLogisticByPlatformManager(String mobileToCreate, int logisticType,
            String logisticTypeName, String name, int serveRegionCode, String serveRegionName, String mobileOfManager,
            String loginPwdOfManager, String payMentPwdOfManager, int operationType, int clientType, String passWord, 
            String contactName, String contactTelephone, String contactMobile, String selfIntroduction, int regionCode,
			String regionName, String address, double currentLongitude, double currentLatitude, int routeCodeA,
			String routeNameA, int routeCodeB, String routeNameB, int goodsTypeCode, String goodsTypeName,
			int transportTypeCode, String transportTypeName)
            throws Exception {
        return sApi.createAccountAndLogisticByPlatformManager(mobileToCreate, logisticType, logisticTypeName, name,
                serveRegionCode, serveRegionName, mobileOfManager, loginPwdOfManager, payMentPwdOfManager,
                operationType, clientType, passWord, contactName, contactTelephone, contactMobile, selfIntroduction,
	    		regionCode, regionName, address, currentLongitude, currentLatitude,
	    		 
	    		routeCodeA, routeNameA, routeCodeB, routeNameB, 
	    		goodsTypeCode, goodsTypeName, transportTypeCode, transportTypeName);
    }

    @Override
    public Resp createAccount(String mobile, String password, String code) throws Exception {
        return sApi.createAccount(mobile, password, code);
    }

    @Override
    public Resp forgetPassword(String mobile, String verifyCode, String newPwd) throws Exception {
        return sApi.forgetPassword(mobile, verifyCode, newPwd);
    }

    @Override
    public Resp updatePassword(String mobile, String oldPwd, String newPwd) throws Exception {
        return sApi.updatePassword(mobile, oldPwd, newPwd);
    }
    
    @Override
    public WalletResp frozenWallet(String uname, String upwd, String realname, String identifyNo) throws Exception {
    	return sApi.frozenWallet(uname, upwd, realname, identifyNo);
    }
    
    @Override
    public WalletResp thawWallet(String uname, String upwd, String realname, String identifyNo) throws Exception {
    	return sApi.thawWallet(uname, upwd, realname, identifyNo);
    }
    
    @Override
    public WalletResp changeMobile(String loginName, String loginPwd, String code, int codeType, String payPwd,
            String oldMobile, String newMobile) throws Exception {
        return sApi.changeMobile(loginName, loginPwd, code, codeType, payPwd, oldMobile, newMobile);
    }

    @Override
    public WalletResp openWallet(String loginName, String loginPwd, String mobile, String code, int codeType,
            String realName, int idType, String idNum, String payPwd) throws Exception {
        return sApi.openWallet(loginName, loginPwd, mobile, code, codeType, realName, idType, idNum, payPwd);
    }

    @Override
    public WalletResp getWallet(String loginName, String loginPwd) throws Exception {
        return sApi.getWallet(loginName, loginPwd);
    }

    @Override
    public WalletResp listDetail(String uname, String upwd, int detailId, int type, int count, int year, int month,
            int date) throws Exception {
        return sApi.listDetail(uname, upwd, detailId, type, count, year, month, date);
    }

    @Override
    public WalletResp withdraw(String loginName, String loginPwd, long amount, String paymentPwd, int payeeType,
            String openBankName, String bankCode, String bankName, int bankRegionCode, String bankRegionName,
            String payeeName, String payeeAccount) throws Exception {
        return sApi.withdraw(loginName, loginPwd, amount, paymentPwd, payeeType, openBankName, bankCode, bankName,
                bankRegionCode, bankRegionName, payeeName, payeeAccount);
    }

    @Override
    public WalletResp withdrawToBankCard(String loginName, String loginPwd, long amount, String paymentPwd,
            int payeeType, String payerName, int payerLogisticsType, int bankCardId) throws Exception {
        return sApi.withdrawToBankCard(loginName, loginPwd, amount, paymentPwd, payeeType, payerName, payerLogisticsType, bankCardId);
    }

    @Override
    public WalletResp changePaymentPwd(String loginName, String loginPwd, String mobile, String code, int codeType,
            String newPaymentPwd, String oldPaymentPwd) throws Exception {
        return sApi.changePaymentPwd(loginName, loginPwd, mobile, code, codeType, newPaymentPwd, oldPaymentPwd);
    }

    @Override
    public WalletResp forgetPaymentPwd(String loginName, String loginPwd, String mobile, String code, int codeType,
            String idNum, String paymentPwd) throws Exception {
        return sApi.forgetPaymentPwd(loginName, loginPwd, mobile, code, codeType, idNum, paymentPwd);
    }

    @Override
    public WithdrawTaskResp execWithdrawTask(String uname, String upwd, int taskId, int status, int subStatus,
            String serialNumber, String note) throws Exception {
        return sApi.execWithdrawTask(uname, upwd, taskId, status, subStatus, serialNumber, note);
    }

    @Override
    public WithdrawTaskResp listWithdrawTask(String loginName, String loginPwd, int taskId, int status, int type,
            int count) throws Exception {
        return sApi.listWithdrawTask(loginName, loginPwd, taskId, status, type, count);
    }

    @Override
    public WithdrawTaskResp updateSerialNumber(String loginName, String loginPwd, int taskId, String serialNumber)
            throws Exception {
        return sApi.updateSerialNumber(loginName, loginPwd, taskId, serialNumber);
    }

    @Override
    public WithdrawTaskResp cancelWithdrawTask(String uname, String upwd, String taskId, String note, String paymentPwd)
            throws Exception {
        return sApi.cancelWithdrawTask(uname, upwd, taskId, note, paymentPwd);
    }
    
    @Override
	public WithdrawTaskResp getWithdrawTask(String uname, String upwd, String taskId) throws Exception {
		// TODO Auto-generated method stub
		return sApi.getWithdrawTask(uname, upwd, taskId);
	}

    @Override
    public BankCardResp createBankCard(String uname, String upwd, String paymentPwd, String bankCode, String bankName,
            String cardNumber, String realName, String idType, String identityNumber, String openBankName,
            String regionCode, String regionName) throws Exception {
        return sApi.createBankCard(uname, upwd, paymentPwd, bankCode, bankName, cardNumber, realName, idType,
                identityNumber, openBankName, regionCode, regionName);
    }

    @Override
    public BankCardResp deleteBankCard(String uname, String upwd, String paymentPwd, String bankCardId)
            throws Exception {
        return sApi.deleteBankCard(uname, upwd, paymentPwd, bankCardId);
    }

    @Override
    public BankCardResp listBankCard(String uname, String upwd) throws Exception {
        return sApi.listBankCard(uname, upwd);
    }

    @Override
    public BondWalletResp getBondWallet(String loginName, String loginPwd) throws Exception {
        return sApi.getBondWallet(loginName, loginPwd);
    }

    @Override
    public BondWalletResp deposit2BondWalletFromWallet(String loginName, String loginPwd, long amount, String paymentPwd)
            throws Exception {
        return sApi.deposit2BondWalletFromWallet(loginName, loginPwd, amount, paymentPwd);
    }

    @Override
    public BondWalletResp withdraw2WalletFromBondWallet(String loginName, String loginPwd, long amount,
            String paymentPwd) throws Exception {
        return sApi.withdraw2WalletFromBondWallet(loginName, loginPwd, amount, paymentPwd);
    }

    @Override
    public BondWalletResp frozenBondWallet(String loginName, String loginPwd, long amount, String paymentPwd)
            throws Exception {
        return sApi.frozenBondWallet(loginName, loginPwd, amount, paymentPwd);
    }

    @Override
    public BondWalletResp thawBondWallet(String loginName, String loginPwd, long amount, String paymentPwd)
            throws Exception {
        return sApi.thawBondWallet(loginName, loginPwd, amount, paymentPwd);
    }

    @Override
    public BondWalletResp compensateByGuarantee(String loginName, String loginPwd, int payerWalletId,
            int payeeWalletId, long amount, int dealBillType, int dealBillId, String paymentPwd) throws Exception {
        return sApi.compensateByGuarantee(loginName, loginPwd, payerWalletId, payeeWalletId, amount, dealBillType,
                dealBillId, paymentPwd);
    }

    @Override
    public BondWalletResp compensateByUser(String loginName, String loginPwd, int payeeWalletId, long amount,
            int dealBillType, int dealBillId, String paymentPwd) throws Exception {
        return compensateByUser(loginName, loginPwd, payeeWalletId, amount, dealBillType, dealBillId, paymentPwd);
    }

    @Override
    public BondWalletResp BondlistDetail(String uname, String upwd, int detailId, int type, int count, int year,
            int month, int date) throws Exception {
        return sApi.BondlistDetail(uname, upwd, detailId, type, count, year, month, date);
    }

    @Override
    public TaskResp startGuaranteeTask(String loginName, String loginPwd, String paymentPwd, int walletType)
            throws Exception {
        return sApi.startGuaranteeTask(loginName, loginPwd, paymentPwd, walletType);
    }

    @Override
    public TaskResp stopGuaranteeTask(String loginName, String loginPwd, String paymentPwd, int walletType)
            throws Exception {
        return sApi.stopGuaranteeTask(loginName, loginPwd, paymentPwd, walletType);
    }

    @Override
    public TaskResp checkIsAuto(String loginName, String loginPwd, int walletType) throws Exception {
        return sApi.checkIsAuto(loginName, loginPwd, walletType);
    }

    @Override
    public TaskResp listGuaranteeTask(String loginName, String loginPwd, int status, int taskId, int count, int type,
            int walletType) throws Exception {

        return sApi.listGuaranteeTask(loginName, loginPwd, status, taskId, count, type, walletType);
    }
    
    @Override
    public ComplainTaskResp execComplainTask(String uname, String upwd, int taskId, int payerGuaranteeAmountResultType, 
    		int payerGuaranteeAmountResultId, int payeeGuaranteeAmountResultType, int payeeGuaranteeAmountResultId,
    		int infoFeeResultType, int infoFeeResultId, int dealBillType, String dealBillId, String note, String paymentPwd) throws Exception {
    	return sApi.execComplainTask(uname, upwd, taskId, payerGuaranteeAmountResultType, payerGuaranteeAmountResultId, 
    			payeeGuaranteeAmountResultType, payeeGuaranteeAmountResultId, infoFeeResultType, infoFeeResultId, dealBillType, 
    			dealBillId, note, paymentPwd);
    }

	@Override
	public Integer createTask(String userAccount, String userName,
			String contactTel, int type, String detail) throws Exception {
		// TODO Auto-generated method stub
		return sApi.createTask(userAccount, userName, contactTel, type, detail);
	}

}
