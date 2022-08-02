package network.platon.did.sdk.service.impl;

import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.constant.ClaimMetaKey;
import network.platon.did.common.constant.VpOrVcPoofKey;
import network.platon.did.common.enums.AlgorithmTypeEnum;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.common.utils.DateUtils;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.sdk.base.dto.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.req.credential.VerifyCredentialReq;
import network.platon.did.sdk.req.presentation.CreatePresetationReq;
import network.platon.did.sdk.req.presentation.VerifyPresetationReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.presentation.CreatePresetationResp;
import network.platon.did.sdk.service.BusinessBaseService;
import network.platon.did.sdk.service.PresentationService;
import network.platon.did.sdk.utils.CredentialsUtils;
import network.platon.did.sdk.utils.DidUtils;
import network.platon.did.sdk.utils.PresentationUtils;
import network.platon.did.sdk.utils.VerifyInputDataUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PresentationServiceImpl extends BusinessBaseService implements PresentationService, Serializable,Cloneable {
    /**
     *
     */
    private static final long serialVersionUID = 5732175037207593062L;

    private static PresentationServiceImpl presentationServiceImpl = new PresentationServiceImpl();

    public static PresentationServiceImpl getInstance(){
        try {
            return (PresentationServiceImpl) presentationServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
            log.error("get instance error.", e);
        }
        return new PresentationServiceImpl();
    }

    @Override
    public BaseResp<CreatePresetationResp> createPresentation(CreatePresetationReq req) {

        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        DidAuthentication didAuthentication = req.getAuthentication();
        BaseResp<DocumentData> getDocResp = getDidContractService().getDocument(DidUtils.convertDidToAddressStr(didAuthentication.getDid()));
        if(!getDocResp.checkSuccess()) {
            log.error("get did document data fail.{}", getDocResp.getErrMsg());
            return BaseResp.build(getDocResp.getCode(),getDocResp.getErrMsg());
        }
        DocumentData documentData = (DocumentData) getDocResp.getData();
        CheckData checkData = new CheckData();
        RetEnum retEnum = VerifyInputDataUtils.checkDocumentData(documentData, didAuthentication.getPublicKeyId(), didAuthentication.getPrivateKey(),checkData);
        if(!RetEnum.isSuccess(retEnum)) {
            return BaseResp.buildError(retEnum);
        }

        /**
         * According to the policy to convert the corresponding credential
         */
        Map<String, ClaimPolicy> claimPolicyMap = req.getPolicy().getPolicys();
        List<Credential> newCredentialList = new ArrayList<>();
        for (Credential credential : req.getCredentials()) {
            ClaimPolicy claimPolicy = claimPolicyMap.get(credential.obtainPctId());
            if (claimPolicy == null) {
                log.error("did in claimMeta is null,did:{}", credential.getClaimMeta().get(ClaimMetaKey.PCTID));
                continue;
            }
            /**
             * Construct selective credential
             */
            BaseResp<Credential> res = this.createSelectiveCredential(credential, claimPolicy, req.getAuthentication());
            if (res.getCode() != RetEnum.RET_SUCCESS.getCode()) {
                return BaseResp.build(res.getCode(), res.getErrMsg());
            }
            newCredentialList.add(res.getData());
        }
        Presentation presentation = new Presentation();
        List<String> type = new ArrayList<>();
        type.add(DidConst.DEFAULT_PRESENTATION_TYPE);
        presentation.setType(type);
        presentation.setVerifiableCredential(newCredentialList);

        this.generatePresentationProof(req.getChallenge(), req.getAuthentication(), presentation);
        CreatePresetationResp createPresetationResp = new CreatePresetationResp();
        createPresetationResp.setPresentation(presentation);
        return BaseResp.buildSuccess(createPresetationResp);
    }

    /**
     * Create Selective Credential
     * @param credential
     * @param claimPolicy
     * @param didAuthentication
     * @return
     */
    @SuppressWarnings("unchecked")
    private BaseResp<Credential> createSelectiveCredential(Credential credential, ClaimPolicy claimPolicy,
                                                           DidAuthentication didAuthentication) {
        Credential newCredential = ConverDataUtils.clone(credential);
        Map<String, Object> disclosureMap;
        try {
            disclosureMap = ConverDataUtils.deserialize(claimPolicy.getDisclosedFieldsJson(), HashMap.class);
        } catch (Exception e) {
            log.error("claimPolicy objToMap error", e);
            return BaseResp.buildError(RetEnum.RET_CREDENTIAL_TRANSFER_MAP_ERROR);
        }
        // Supplement the missing key of policy
        PresentationUtils.addKeyToPolicy(disclosureMap, newCredential.getClaimData());
        // For claims without selective disclosure of data for salt calculation hash
        PresentationUtils.addSelectSalt(disclosureMap, newCredential.obtainSalt(), newCredential.getClaimData());
        Map<String, Object> proofMap = newCredential.getProof();
        proofMap.put(VpOrVcPoofKey.PROOF_DISCLOSURES, disclosureMap);
        return BaseResp.buildSuccess(newCredential);
    }

    /**
     * Initialize proof data
     *
     * @param challenge
     * @param didAuthentication
     * @param presentation
     */
    private void generatePresentationProof(Challenge challenge, DidAuthentication didAuthentication,
                                           Presentation presentation) {
        /**
         * sign presentation data
         */
        String signature = AlgorithmHandler.signMessageStr(presentation.toRawData(), didAuthentication.getPrivateKey());
        Map<String, Object> proofMap = new HashMap<>();
        String proofType = AlgorithmTypeEnum.ECC.getDesc();
        proofMap.put(VpOrVcPoofKey.PROOF_TYPE, proofType);
        Long proofCreated = DateUtils.getCurrentTimeStamp();
        proofMap.put(VpOrVcPoofKey.PROOF_CTEATED, proofCreated);
        String didPublicKeyId = didAuthentication.getPublicKeyId();
        proofMap.put(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD, didPublicKeyId);
        proofMap.put(VpOrVcPoofKey.PROOF_CHALLENGE, challenge.getNonce());
        proofMap.put(VpOrVcPoofKey.PROOF_JWS, signature);
        presentation.setProof(proofMap);
    }

    @Override
    public BaseResp<String> verifyPresentation(VerifyPresetationReq req) {
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }
        if (!req.getPresentation().checkProof()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, "miss proof data");
        }
        Presentation presentation = req.getPresentation();
        PresentationPolicy presentationPolicy = req.getPolicy();
        Map<String, ClaimPolicy> claimPolicyMap = presentationPolicy.getPolicys();
        if (!presentation.obtainNonce().equals(req.getChallenge().getNonce())) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, "nonce not match");
        }
        //Determine whether the document data is consistent
        BaseResp<DocumentData> getDocResp = this.getDidContractService().getDocument(DidUtils.convertDidToAddressStr(req.getDid()));
        if(!getDocResp.checkSuccess()) {
            log.error("get did document data fail.{}", getDocResp.getErrMsg());
            return BaseResp.build(getDocResp.getCode(),getDocResp.getErrMsg());
        }
        DocumentData documentData = (DocumentData) getDocResp.getData();
        CheckData checkData = new CheckData();
        RetEnum verify = VerifyInputDataUtils.checkDocumentData(documentData, presentation.obtainPublicId(), null, checkData);
        if(!RetEnum.isSuccess(verify)) {
            return BaseResp.buildError(verify);
        }
        if(!CredentialsUtils.verifyEccSignature(presentation.toRawData(), presentation.obtainSign(), checkData.getPublicKeyHex())) {
            return BaseResp.buildError(RetEnum.RET_PRESENTATION_VERIFY_ERROR);
        }
        for (Credential credential : presentation.getVerifiableCredential()) {
            ClaimPolicy claimPolicy = claimPolicyMap.get(credential.obtainPctId());
            if (claimPolicy == null) {
                log.error("did in claimMeta is null,did:{}", credential.obtainPctId());
                return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
                        "claimPolicy is null, pctid :" + credential.obtainPctId());
            }
            RetEnum retEnum = PresentationUtils.verifyPolicy(credential, claimPolicy, String.valueOf(credential.obtainPctId()));
            if (retEnum.getCode() != RetEnum.RET_SUCCESS.getCode()) {
                return BaseResp.buildError(retEnum);
            }
            VerifyCredentialReq verifyCredentialReq = VerifyCredentialReq.builder().credential(credential)
                    .build();
            BaseResp<String> resp = getCredentialService().verifyCredential(verifyCredentialReq);
            if(!resp.checkSuccess()) {
                return resp;
            }
        }
        return BaseResp.buildSuccess();
    }
}
