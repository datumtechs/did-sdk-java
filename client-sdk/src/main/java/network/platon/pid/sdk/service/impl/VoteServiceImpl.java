package network.platon.pid.sdk.service.impl;

import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.req.agency.RevocationAuthorityReq;
import network.platon.pid.sdk.req.agency.SetAuthorityReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.agency.*;
import network.platon.pid.sdk.service.VoteService;
import network.platon.pid.sdk.service.BusinessBaseService;
import network.platon.pid.sdk.utils.PidUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-09 18:04
 */
@Slf4j
public class VoteServiceImpl extends BusinessBaseService implements VoteService,Serializable,Cloneable {

	private static final long serialVersionUID = 5732175037207593062L;
	
	private static VoteServiceImpl agencyService = new VoteServiceImpl();
	
	public static VoteServiceImpl getInstance(){
        try {
            return (VoteServiceImpl) agencyService.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new VoteServiceImpl();
    }

	@Override
    public BaseResp<QueryAdminRoleResp> queryAdminRole() {
        BaseResp<string> resp = this.getRoleContractService().getAdminAddr();
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        QueryAdminRoleResp result = new QueryAdminRoleResp();
        result.setAdmin(resp.getData());
        return BaseResp.buildSuccess(result);
    }

    @Override
    public BaseResp<Boolean> isAuthorityIssuer(String pid) {
        if (!PidUtils.isValidPid(pid) ) {
            log.error(
                    "Failed to call `isAuthorityIssuer()`: the addr convert base on `pid` is illegal, pid: {}",
                    pid
            );
            return BaseResp.build(RetEnum.RET_PID_INVALID);
        }
        String identity = PidUtils.convertPidToAddressStr(pid);

        // check the authrityInfo is a valid identity, first?
        BaseResp<Boolean> pidResp = this.getPidContractService().isValidIdentity(identity);
        if (pidResp.checkFail()) {
            return BaseResp.build(pidResp.getCode(), pidResp.getErrMsg());
        }
        // check the identity is a authority, second?
        BaseResp<Boolean> resp = this.getAuthorityContractService().isAuthority(identity);
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        return resp;
    }

    @Override
    public BaseResp<SetAuthorityResp> addAuthorityIssuer(SetAuthorityReq req) {
    	BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}

        if (!PidUtils.verifySetAuthorityArg(req)) {
            log.error("Failed to call `addAuthorityIssuer()`: the `SetAuthorityReq` is illegal");
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
        }
        String pid = req.getAuthority().getPid();
        if (!PidUtils.isValidPid(pid)) {
            log.error("Failed to call `addAuthorityIssuer()`: the pid is invalid on req, pid: {}", pid);
            return BaseResp.build(RetEnum.RET_PID_INVALID);
        }

        // check the authrityInfo is a valid identity, first?
        BaseResp<Boolean> pidResp = this.getPidContractService().isValidIdentity(PidUtils.convertPidToAddressStr(pid));
        if (pidResp.checkFail()) {
            return BaseResp.build(pidResp.getCode(), pidResp.getErrMsg());
        }
        // Verify if the authority details already exist, second?
        BaseResp<Boolean> isExistResp = this.getAuthorityContractService().isAuthority(PidUtils.convertPidToAddressStr(pid));
        if (isExistResp.checkFail()) {
            return BaseResp.build(isExistResp.getCode(), isExistResp.getErrMsg());
        }
        if (isExistResp.getData()) {
            return TransactionResp.build(RetEnum.RET_AGENCY_AUTHORITY_ISSUER_ALREADY_EXIST);
        }
        // Verify if the authority name already exist, third?
        BaseResp<AuthorityInfo> queryResp = this.getAuthorityContractService().getAuthorityInfoByName(req.getAuthority().getName());
        if (queryResp.checkFail() && queryResp.getCode() != RetEnum.RET_AGENCY_AUTHORITY_ISSUER_NOTEXIST.getCode()) {
            return BaseResp.build(queryResp.getCode(), queryResp.getErrMsg());
        }
        // the authority is already exist!
        if (!PidUtils.isEmptyAuthorityInfo(queryResp.getData())) {
            return BaseResp.build(RetEnum.RET_AGENCY_AUTHORITY_ISSUER_ALREADY_EXIST);
        }

        // do add authority
        TransactionResp<Boolean> resp =
                this.getAuthorityContractService(new InitContractData(req.getPrivateKey()))
                        .addAuthority(req.getAuthority());
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        SetAuthorityResp result = new SetAuthorityResp();
        result.setStatus(resp.getData());
        result.setTransactionInfo(resp.getTransactionInfo());
        return BaseResp.buildSuccess(result);
    }

    @Override
    public BaseResp<SetAuthorityResp> updateAuthorityIssuer(SetAuthorityReq req) {
    	BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
        if (!PidUtils.verifySetAuthorityArg(req)) {
            log.error("Failed to call `updateAuthorityIssuer()`: the `SetAuthorityReq` is illegal");
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
        }
        String pid = req.getAuthority().getPid();
        if (!PidUtils.isValidPid(pid)) {
            log.error("Failed to call `updateAuthorityIssuer()`: the pid is invalid on req, pid: {}", pid);
            return BaseResp.build(RetEnum.RET_PID_INVALID);
        }

        // check the authrityInfo is a authority, first?
        BaseResp<Boolean> isExistResp = this.getAuthorityContractService().isAuthority(PidUtils.convertPidToAddressStr(pid));
        if (isExistResp.checkFail()) {
            return BaseResp.build(isExistResp.getCode(), isExistResp.getErrMsg());
        }
        if (!isExistResp.getData()) {
            return TransactionResp.build(RetEnum.RET_AGENCY_AUTHORITY_ISSUER_NOTEXIST);
        }

        TransactionResp<Boolean> resp =
                this.getAuthorityContractService(new InitContractData(req.getPrivateKey()))
                        .updateAuthority(req.getAuthority());
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        SetAuthorityResp result = new SetAuthorityResp();
        result.setStatus(resp.getData());
        result.setTransactionInfo(resp.getTransactionInfo());
        return BaseResp.buildSuccess(result);
    }

    @Override
    public BaseResp<SetAuthorityResp> removeAuthorityIssuer(RevocationAuthorityReq req) {
    	BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
        if (StringUtils.isBlank(req.getPid()) && StringUtils.isBlank(req.getPrivateKey()) && !PidUtils.isPrivateKeyValid(req.getPrivateKey())) {
            log.error("Failed to call `removeAuthorityIssuer()`: the `RevocationAuthorityReq` is illegal");
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
        }

        String pid = req.getPid();
        if (!PidUtils.isValidPid(pid)) {
            log.error("Failed to call `removeAuthorityIssuer()`: the pid is invalid, pid: {}", pid);
            return BaseResp.build(RetEnum.RET_PID_INVALID);
        }

        // check the authrityInfo is a authority, first?
        BaseResp<Boolean> isExistResp = this.getAuthorityContractService().isAuthority(PidUtils.convertPidToAddressStr(pid));
        if (isExistResp.checkFail()) {
            return BaseResp.build(isExistResp.getCode(), isExistResp.getErrMsg());
        }
        if (!isExistResp.getData()) {
            return TransactionResp.build(RetEnum.RET_AGENCY_AUTHORITY_ISSUER_NOTEXIST);
        }

        TransactionResp<Boolean> resp =
                this.getAuthorityContractService(new InitContractData(req.getPrivateKey()))
                        .removeAuthority(PidUtils.convertPidToAddressStr(pid));
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        SetAuthorityResp result = new SetAuthorityResp();
        result.setStatus(resp.getData());
        result.setTransactionInfo(resp.getTransactionInfo());
        return BaseResp.buildSuccess(result);
    }

    @Override
    public BaseResp<QueryAuthorityResp> getAuthorityIssuerByPid(String pid) {
        if (!PidUtils.isValidPid(pid)) {
            log.error("Failed to call `getAuthorityIssuerByAddr()`: the pid is invalid, pid: {}", pid);
            return BaseResp.build(RetEnum.RET_PID_INVALID);
        }
        BaseResp<AuthorityInfo> resp = this.getAuthorityContractService().getAuthorityInfoByIdentity(PidUtils.convertPidToAddressStr(pid));
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        QueryAuthorityResp result = new QueryAuthorityResp();
        if (null != resp.getData()) {
            result.setStatus(true);
        }
        result.setAuthorityInfo(resp.getData());
        return BaseResp.buildSuccess(result);
    }

    @Override
    public BaseResp<QueryAuthorityResp> getAuthorityIssuerByName(String name) {
        if (StringUtils.isBlank(name)) {
            log.error("Failed to call `getAuthorityIssuerByName()`: the authority name is empty on input params");
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
        }
        BaseResp<AuthorityInfo> resp = this.getAuthorityContractService().getAuthorityInfoByName(name);
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        QueryAuthorityResp result = new QueryAuthorityResp();
        if (null != resp.getData()) {
            result.setStatus(true);
        }
        result.setAuthorityInfo(resp.getData());
        return BaseResp.buildSuccess(result);
    }

    @Override
    public BaseResp<QueryAuthorityAccumulateResp> getAccumulateOfAuthorityIssuer(String pid) {
        if (!PidUtils.isValidPid(pid)) {
            log.error("Failed to call `getAccumulateOfAuthorityIssuer()`: the pid is invalid, pid: {}", pid);
            return BaseResp.build(RetEnum.RET_PID_INVALID);
        }
        string identity = PidUtils.convertPidToAddressStr(pid);
        BaseResp<BigInteger> resp = this.getAuthorityContractService().getAccumulate(identity);
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        QueryAuthorityAccumulateResp result = new QueryAuthorityAccumulateResp();
        result.setAccumulate(resp.getData());
        return BaseResp.buildSuccess(result);
    }

    @Override
    public BaseResp<QueryAllAuthorityNameResp> getAllAuthorityIssuerNameList() {
        BaseResp<List<String>> resp = this.getAuthorityContractService().getAllNameList();
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }
        QueryAllAuthorityNameResp result = new QueryAllAuthorityNameResp();
        result.setNames(resp.getData());
        return BaseResp.buildSuccess(result);
    }
}
