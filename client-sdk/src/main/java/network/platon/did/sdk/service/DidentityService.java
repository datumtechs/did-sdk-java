package network.platon.did.sdk.service;

import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.req.did.*;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.did.*;

/**
 * Did (PlatON Identity Document) related method interface class
 * @Auther: Zhangrj
 * @Date: 2020年5月28日
 * @Description:
 */
public interface DidentityService {


	BaseResp<CreateDidResp> createDid(CreateDidReq req);

	/**
	 * Query the document info
	 * @return
	 */
	BaseResp<QueryDidDocumentResp> queryDidDocument(QueryDidDocumentReq req);


	/**
	 * Query the documentData info
	 * @return
	 */
	BaseResp<QueryDidDocumentDataResp> queryDidDocumentData (QueryDidDocumentReq req);

	/**
	 * Set the corresponding publickey parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetDidAttrResp> addPublicKey(AddPublicKeyReq req);

	/**
	 * Set the corresponding publickey parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetDidAttrResp> updatePublicKey(UpdatePublicKeyReq req);

	/**
	 * Revocation the corresponding publickey parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetDidAttrResp> revocationPublicKey(RevocationPublicKeyReq req);

	/**
	 * Set the corresponding service parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetDidAttrResp> setService(SetServiceReq req);

	/**
	 * Revocation the corresponding service parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetDidAttrResp> revocationService(SetServiceReq req);


	BaseResp<ChangeDocumentStatusResp> changeDocumentStatus(ChangeDocumentStatusReq req);

	BaseResp<DidConst.DocumentStatus> getDocumentStatus(String did);

	/**
	 * Determine whether the current did already exists
	 * @param did
	 * @return
	 */
	BaseResp<Boolean> isDidExist(String did);

}
