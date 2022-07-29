package network.platon.pid.sdk.client;

import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.req.pid.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pid.ChangeDocumentStatusResp;
import network.platon.pid.sdk.resp.pid.CreatePidResp;
import network.platon.pid.sdk.resp.pid.QueryPidDocumentResp;
import network.platon.pid.sdk.resp.pid.SetPidAttrResp;
import network.platon.pid.sdk.service.PidentityService;

public class PidentityClient extends BusinessClient implements PidentityService  {

	@Override
	public BaseResp<CreatePidResp> createPid(CreatePidReq req) {
		return getPidentityService().createPid(req);
	}

	@Override
	public BaseResp<QueryPidDocumentResp> queryPidDocument(QueryPidDocumentReq req) {
		return getPidentityService().queryPidDocument(req);
	}

	@Override
	public BaseResp<SetPidAttrResp> addPublicKey(AddPublicKeyReq req) {
		return getPidentityService().addPublicKey(req);
	}

	@Override
	public BaseResp<SetPidAttrResp> updatePublicKey(UpdatePublicKeyReq req) {
		return getPidentityService().updatePublicKey(req);
	}

	@Override
	public BaseResp<SetPidAttrResp> setService(SetServiceReq req) {
		return getPidentityService().setService(req);
	}

	@Override
	public BaseResp<SetPidAttrResp> revocationPublicKey(RevocationPublicKeyReq req) {
		return getPidentityService().revocationPublicKey(req);
	}

	@Override
	public BaseResp<SetPidAttrResp> revocationService(SetServiceReq req) {
		return getPidentityService().revocationService(req);
	}

	@Override
	public BaseResp<PidConst.DocumentStatus> getDocumentStatus(String pid) {
		return getPidentityService().getDocumentStatus(pid);
	}

	@Override
	public BaseResp<Boolean> isPidExist(String pid) {
		return getPidentityService().isPidExist(pid);
	}

	@Override
	public BaseResp<ChangeDocumentStatusResp> changeDocumentStatus(ChangeDocumentStatusReq req) {
		return getPidentityService().changeDocumentStatus(req);
	}


}
