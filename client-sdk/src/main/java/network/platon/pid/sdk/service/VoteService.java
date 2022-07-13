package network.platon.pid.sdk.service;

import network.platon.pid.sdk.req.agency.RevocationAuthorityReq;
import network.platon.pid.sdk.req.agency.SetAuthorityReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.agency.*;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-09 18:04
 */
public interface VoteService {

    /**
     * Query current Admin account address
     * @return
     */
    BaseResp<QueryAdminRoleResp> queryAdminRole();

    /**
     * Determine whether the current pid is an authority issuer
     * @param pid
     * @return
     */
    BaseResp<Boolean> isAuthorityIssuer(String pid);

    /**
     * Add an authority issuer
     * @param req
     * @return
     */
    BaseResp<SetAuthorityResp> addAuthorityIssuer(SetAuthorityReq req);

    /**
     * Update an authority issuer
     * @param req
     * @return
     */
    BaseResp<SetAuthorityResp> updateAuthorityIssuer(SetAuthorityReq req);

    /**
     * Remove an authority issuer
     * @param req
     * @return
     */
    BaseResp<SetAuthorityResp> removeAuthorityIssuer(RevocationAuthorityReq req);

    /**
     * Extract the corresponding authority issuer details according to `pid`
     * @param pid
     * @return
     */
    BaseResp<QueryAuthorityResp> getAuthorityIssuerByPid(String pid);

    /**
     * Extract the corresponding authority issuer details according to `authority name`
     * @param name
     * @return
     */
    BaseResp<QueryAuthorityResp> getAuthorityIssuerByName(String name);

    /**
     * Extract `accumulate` of the corresponding authority issuer according to `pid`
     * @param pid
     * @return
     */
    BaseResp<QueryAuthorityAccumulateResp> getAccumulateOfAuthorityIssuer(String pid);


    /**
     * Query the names of all authorities in the authority collection
     * @return
     */
    BaseResp<QueryAllAuthorityNameResp> getAllAuthorityIssuerNameList();

}
