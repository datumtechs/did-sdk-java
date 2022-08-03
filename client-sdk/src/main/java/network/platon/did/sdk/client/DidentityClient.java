package network.platon.did.sdk.client;

import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.req.did.*;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.did.ChangeDocumentStatusResp;
import network.platon.did.sdk.resp.did.CreateDidResp;
import network.platon.did.sdk.resp.did.QueryDidDocumentResp;
import network.platon.did.sdk.resp.did.SetDidAttrResp;
import network.platon.did.sdk.service.DidentityService;

public class DidentityClient extends BusinessClient implements DidentityService  {

	@Override
	public BaseResp<CreateDidResp> createDid(CreateDidReq req) {
		return getDidentityService().createDid(req);
	}

	@Override
	public BaseResp<QueryDidDocumentResp> queryDidDocument(QueryDidDocumentReq req) {
		return getDidentityService().queryDidDocument(req);
	}

	@Override
	public BaseResp<SetDidAttrResp> addPublicKey(AddPublicKeyReq req) {
		return getDidentityService().addPublicKey(req);
	}

	@Override
	public BaseResp<SetDidAttrResp> updatePublicKey(UpdatePublicKeyReq req) {
		return getDidentityService().updatePublicKey(req);
	}

	@Override
	public BaseResp<SetDidAttrResp> setService(SetServiceReq req) {
		return getDidentityService().setService(req);
	}

	@Override
	public BaseResp<SetDidAttrResp> revocationPublicKey(RevocationPublicKeyReq req) {
		return getDidentityService().revocationPublicKey(req);
	}

	@Override
	public BaseResp<SetDidAttrResp> revocationService(SetServiceReq req) {
		return getDidentityService().revocationService(req);
	}

	@Override
	public BaseResp<DidConst.DocumentStatus> getDocumentStatus(String did) {
		return getDidentityService().getDocumentStatus(did);
	}

	@Override
	public BaseResp<Boolean> isDidExist(String did) {
		return getDidentityService().isDidExist(did);
	}

	@Override
	public BaseResp<ChangeDocumentStatusResp> changeDocumentStatus(ChangeDocumentStatusReq req) {
		return getDidentityService().changeDocumentStatus(req);
	}


}
