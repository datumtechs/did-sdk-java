package network.platon.did.sdk.base.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import network.platon.did.common.constant.VpOrVcPoofKey;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.annoation.CustomSize;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description: The base data structure of PlatON DID Presentation  info
 * @Author: Gavin
 * @Date: 2020-06-03 18:24
 */
@Data
public class Presentation implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Required: The @context.
     */
    @JSONField(name="@context")
    private List<String> context;

    @CustomNotNull
    private List<String> type;

    @CustomNotNull
	@CustomSize(min = 1)
    private List<Credential> verifiableCredential;

    @CustomNotNull
    private Map<String, Object> proof;
    
    public String toRawData() {
    	Presentation presentation = ConverDataUtils.clone(this);
    	presentation.setProof(null);
        return ConverDataUtils.serialize(presentation);
    }
    
	public boolean checkProof() {
		if(proof.containsKey(VpOrVcPoofKey.PROOF_TYPE) && proof.containsKey(VpOrVcPoofKey.PROOF_CTEATED)
				&& proof.containsKey(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD) && proof.containsKey(VpOrVcPoofKey.PROOF_CHALLENGE) 
				&& proof.containsKey(VpOrVcPoofKey.PROOF_JWS)) return true;
		return false;
	}
	
	public String obtainNonce() {
		Map<String, Object> proofMap = this.getProof();
		return (String) proofMap.get(VpOrVcPoofKey.PROOF_CHALLENGE);
	}
	
	public String obtainSign() {
		Map<String, Object> proofMap = this.getProof();
		return (String) proofMap.get(VpOrVcPoofKey.PROOF_JWS);
	}
	
	public String obtainPublicId() {
		Map<String, Object> proofMap = this.getProof();
		return (String) proofMap.get(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD);
	}
}
