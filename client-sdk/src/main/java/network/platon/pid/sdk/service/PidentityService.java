package network.platon.pid.sdk.service;

import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.req.pid.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pid.*;

/**
 * Pid (PlatON Identity Document) related method interface class
 * @Auther: Zhangrj
 * @Date: 2020年5月28日
 * @Description:
 */
public interface PidentityService {


	BaseResp<CreatePidResp> createPid(CreatePidReq req);

	/**
	 * Query the document info
	 * @return
	 */
	BaseResp<QueryPidDocumentResp> queryPidDocument(QueryPidDocumentReq req);


	/**
	 * Query the documentData info
	 * @return
	 */
	BaseResp<QueryPidDocumentDataResp> queryPidDocumentData (QueryPidDocumentReq req);

	/**
	 * Set the corresponding publickey parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetPidAttrResp> addPublicKey(AddPublicKeyReq req);

	/**
	 * Set the corresponding publickey parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetPidAttrResp> updatePublicKey(UpdatePublicKeyReq req);

	/**
	 * Revocation the corresponding publickey parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetPidAttrResp> revocationPublicKey(RevocationPublicKeyReq req);

	/**
	 * Set the corresponding service parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetPidAttrResp> setService(SetServiceReq req);

	/**
	 * Revocation the corresponding service parameter according to PId
	 * @param req
	 * @return
	 */
	BaseResp<SetPidAttrResp> revocationService(SetServiceReq req);


	BaseResp<ChangeDocumentStatusResp> changeDocumentStatus(ChangeDocumentStatusReq req);

	BaseResp<PidConst.DocumentStatus> getDocumentStatus(String pid);

	/**
	 * Determine whether the current pid already exists
	 * @param pid
	 * @return
	 */
	BaseResp<Boolean> isPidExist(String pid);

}
