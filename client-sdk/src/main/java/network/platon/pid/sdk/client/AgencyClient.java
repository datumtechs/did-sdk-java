package network.platon.pid.sdk.client;

import network.platon.pid.sdk.req.agency.RevocationAuthorityReq;
import network.platon.pid.sdk.req.agency.SetAuthorityReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.agency.QueryAdminRoleResp;
import network.platon.pid.sdk.resp.agency.QueryAllAuthorityNameResp;
import network.platon.pid.sdk.resp.agency.QueryAuthorityAccumulateResp;
import network.platon.pid.sdk.resp.agency.QueryAuthorityResp;
import network.platon.pid.sdk.resp.agency.SetAuthorityResp;
import network.platon.pid.sdk.service.VoteService;

public class AgencyClient extends BusinessClient implements VoteService {

	@Override
	public BaseResp<QueryAdminRoleResp> queryAdminRole() {
		return getAgencyService().queryAdminRole();
	}

	@Override
	public BaseResp<Boolean> isAuthorityIssuer(String pid) {
		return getAgencyService().isAuthorityIssuer(pid);
	}

	@Override
	public BaseResp<SetAuthorityResp> addAuthorityIssuer(SetAuthorityReq req) {
		return getAgencyService().addAuthorityIssuer(req);
	}

	@Override
	public BaseResp<SetAuthorityResp> updateAuthorityIssuer(SetAuthorityReq req) {
		return getAgencyService().updateAuthorityIssuer(req);
	}

	@Override
	public BaseResp<SetAuthorityResp> removeAuthorityIssuer(RevocationAuthorityReq req) {
		return getAgencyService().removeAuthorityIssuer(req);
	}

	@Override
	public BaseResp<QueryAuthorityResp> getAuthorityIssuerByPid(String pid) {
		return getAgencyService().getAuthorityIssuerByPid(pid);
	}

	@Override
	public BaseResp<QueryAuthorityResp> getAuthorityIssuerByName(String name) {
		return getAgencyService().getAuthorityIssuerByName(name);
	}

	@Override
	public BaseResp<QueryAuthorityAccumulateResp> getAccumulateOfAuthorityIssuer(String pid) {
		return getAgencyService().getAccumulateOfAuthorityIssuer(pid);
	}

	@Override
	public BaseResp<QueryAllAuthorityNameResp> getAllAuthorityIssuerNameList() {
		return getAgencyService().getAllAuthorityIssuerNameList();
	}
	

}
