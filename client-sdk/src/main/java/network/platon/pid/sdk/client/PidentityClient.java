package network.platon.pid.sdk.client;

import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.req.pid.AddPublicKeyReq;
import network.platon.pid.sdk.req.pid.ChangeDocumentStatusReq;
import network.platon.pid.sdk.req.pid.CreatePidReq;
import network.platon.pid.sdk.req.pid.QueryPidDocumentReq;
import network.platon.pid.sdk.req.pid.RevocationPublicKeyReq;
import network.platon.pid.sdk.req.pid.SetPidAuthReq;
import network.platon.pid.sdk.req.pid.SetServiceReq;
import network.platon.pid.sdk.req.pid.UpdatePublicKeyReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pid.*;
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
	public BaseResp<QueryPidDocumentDataResp> queryPidDocumentData (QueryPidDocumentReq req){
		return getPidentityService().queryPidDocumentData(req);
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
