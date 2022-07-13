package network.platon.pid.common.config;


import com.platon.parameters.NetworkParameters;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import network.platon.pid.common.enums.Web3jProtocolEnum;
import network.platon.pid.common.utils.PropertyUtils;

/**
 * config for pid
 * @Auther: wanghengtao
 * @Date: 2022年7月11日
 * @Description:
 */
@Data
public class PidConfig {
	
	private static Web3jProtocolEnum web3jProtocolEnum;
	
	private static String PLATON_URL;
	
	private final static String PlatONName = "platon.web3j.url";
	
	private final static String ProtocolName = "platon.web3j.protocol";
	
	private static String VERSION;
	
	private final static String VersionName = "version";

	private static String VOTE_CONTRACT_ADDRESS;

	private final static String VoteContractName = "contract.vote.address";

	private static String PID_CONTRACT_ADDRESS;
	
	private final static String PidContractName = "contract.pid.address";

	private static String PCT_CONTRACT_ADDRESS;

	private final static String PctContractName = "contract.pct.address";
	
	private static String CREDENTIAL_CONTRACT_ADDRESS;
	
	private final static String CredentialContractName = "contract.credential.address";

	private static String GAS_PRICE;
	
	private final static String gasPriceName = "platon.web3j.gasPrice";
	
	private static String GAS_LIMIT;
	
	private final static String gasLimitName = "platon.web3j.gasLimit";
	
	private static Long CHAIN_ID;
	
	private final static String ChainIdName = "platon.web3j.chainId";
	
	private static String CONTRACT_PRIVATEKEY;
	
	private final static String ContractPrivateName = "platon.web3j.contractPrivateKey";

	
	static {
		load();
	}
	
	public static void load() {
		NetworkParameters.selectPlatON();
		web3jProtocolEnum = Web3jProtocolEnum.HTTP;
		String protocol = PropertyUtils.getProperty(ProtocolName,"");
		if("ws".equals(protocol)) {
			web3jProtocolEnum = Web3jProtocolEnum.WS;
		}
		PLATON_URL = PropertyUtils.getProperty(PlatONName,"");
		VERSION = PropertyUtils.getProperty(VersionName,"1.0.0");

		VOTE_CONTRACT_ADDRESS = PropertyUtils.getProperty(VoteContractName,"");
		PID_CONTRACT_ADDRESS = PropertyUtils.getProperty(PidContractName,"");
		PCT_CONTRACT_ADDRESS = PropertyUtils.getProperty(PctContractName,"");
		CREDENTIAL_CONTRACT_ADDRESS = PropertyUtils.getProperty(CredentialContractName,"");

		GAS_PRICE = PropertyUtils.getProperty(gasPriceName,"");
		if(StringUtils.isBlank(GAS_PRICE)) {
			GAS_PRICE = "10000000000";
		}

		GAS_LIMIT = PropertyUtils.getProperty(gasLimitName,"");
		if(StringUtils.isBlank(GAS_LIMIT)) {
			GAS_PRICE = "210000";
		}

		CHAIN_ID = Long.valueOf(PropertyUtils.getProperty(ChainIdName,"120"));
		CONTRACT_PRIVATEKEY = PropertyUtils.getProperty(ContractPrivateName,"");
	}
	
	public static Web3jProtocolEnum getWeb3jProtocolEnum() {
		return web3jProtocolEnum;
	}

	public static void setWeb3jProtocolEnum(Web3jProtocolEnum web3jProtocolEnum) {
		PidConfig.web3jProtocolEnum = web3jProtocolEnum;
	}

	public static String getPLATON_URL() {
		return PLATON_URL;
	}

	public static void setPLATON_URL(String pLATON_URL) {
		PLATON_URL = pLATON_URL;
	}

	public static String getVERSION() {
		return VERSION;
	}

	public static void setVERSION(String vERSION) {
		VERSION = vERSION;
	}

	public static String getVOTE_CONTRACT_ADDRESS() {
		return VOTE_CONTRACT_ADDRESS;
	}

	public static void setVOTE_CONTRACT_ADDRESS(String vOTE_CONTRACT_ADDRESS) {
		VOTE_CONTRACT_ADDRESS = vOTE_CONTRACT_ADDRESS;
	}

	public static String getPID_CONTRACT_ADDRESS() {
		return PID_CONTRACT_ADDRESS;
	}

	public static void setPID_CONTRACT_ADDRESS(String pID_CONTRACT_ADDRESS) {
		PID_CONTRACT_ADDRESS = pID_CONTRACT_ADDRESS;
	}

	public static String getPCT_CONTRACT_ADDRESS() {
		return PCT_CONTRACT_ADDRESS;
	}

	public static void setPCT_CONTRACT_ADDRESS(String pCT_CONTRACT_ADDRESS) {
		PCT_CONTRACT_ADDRESS = pCT_CONTRACT_ADDRESS;
	}

	public static String getCREDENTIAL_CONTRACT_ADDRESS() {
		return CREDENTIAL_CONTRACT_ADDRESS;
	}

	public static void setCREDENTIAL_CONTRACT_ADDRESS(String cREDENTIAL_CONTRACT_ADDRESS) {
		CREDENTIAL_CONTRACT_ADDRESS = cREDENTIAL_CONTRACT_ADDRESS;
	}

	public static String getGAS_PRICE() {
		return GAS_PRICE;
	}

	public static void setGAS_PRICE(String gAS_PRICE) {
		GAS_PRICE = gAS_PRICE;
	}

	public static String getGAS_LIMIT() {
		return GAS_LIMIT;
	}

	public static void setGAS_LIMIT(String gAS_LIMIT) {
		GAS_LIMIT = gAS_LIMIT;
	}

	public static Long getCHAIN_ID() {
		return CHAIN_ID;
	}

	public static void setCHAIN_ID(Long cHAIN_ID) {
		CHAIN_ID = cHAIN_ID;
	}

	public static String getCONTRACT_PRIVATEKEY() {
		return CONTRACT_PRIVATEKEY;
	}

	public static void setCONTRACT_PRIVATEKEY(String cONTRACT_PRIVATEKEY) {
		CONTRACT_PRIVATEKEY = cONTRACT_PRIVATEKEY;
	}

	public static String getPlatonename() {
		return PlatONName;
	}

	public static String getProtocolname() {
		return ProtocolName;
	}

	public static String getVersionname() {
		return VersionName;
	}

	public static String getPidcontractname() {
		return PidContractName;
	}

	public static String getVotecontractname() {
		return VoteContractName;
	}

	public static String getPctcontractname() {
		return PctContractName;
	}

	public static String getCredentialcontractname() {
		return CredentialContractName;
	}

	public static String getGaspricename() {
		return gasPriceName;
	}

	public static String getGaslimitname() {
		return gasLimitName;
	}

	public static String getChainidname() {
		return ChainIdName;
	}

	public static String getContractprivatename() {
		return ContractPrivateName;
	}
}
