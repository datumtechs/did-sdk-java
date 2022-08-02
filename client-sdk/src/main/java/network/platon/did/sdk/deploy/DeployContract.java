package network.platon.did.sdk.deploy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.platon.crypto.Credentials;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.config.DidConfig;
import network.platon.did.common.utils.PropertyUtils;
import network.platon.did.contract.dto.DeployContractData;
import network.platon.did.sdk.client.ReloadClient;
import network.platon.did.sdk.contract.service.ContractService;
import network.platon.did.sdk.contract.service.impl.VoteContractServiceImpl;
import network.platon.did.sdk.contract.service.impl.CredentialContractServiceImpl;
import network.platon.did.sdk.contract.service.impl.PctContractServiceImpl;
import network.platon.did.sdk.contract.service.impl.DidContracServiceImpl;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;

/**
 * Release contract tools
 * 
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月16日
 * @Description:
 */
@Slf4j
public class DeployContract {

	public static BaseResp<DeployContractData> deployCredentialContract(String privateKey, String voteContractAddress) {
		CredentialContractServiceImpl credentialContractService = new CredentialContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = credentialContractService.deployContract(credentials, voteContractAddress);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	public static BaseResp<DeployContractData> deployVoteContract(String privateKey, String adminAddress, String serviceUrl) {
		VoteContractServiceImpl voteContractService = new VoteContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = voteContractService.deployContract(credentials, adminAddress, serviceUrl);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	public static BaseResp<DeployContractData> deployDidContract(String privateKey) {
		DidContracServiceImpl contractService = new DidContracServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = contractService.deployContract(credentials);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	public static BaseResp<DeployContractData> deployPctContract(String privateKey, String voteContractAddress) {
		PctContractServiceImpl contractService = new PctContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = contractService.deployContract(credentials, voteContractAddress);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	/**
	 * Follow the steps to publish the contract 1. deploy the role contract 2.
	 * deploy the authority contract and use the corresponding address of the role
	 * contract 3. deploy the did contract and use the corresponding address of the
	 * role contract 4. deploy the pct contract and use the corresponding address of
	 * the role contract 5. deploy the credential contract and use the corresponding
	 * address of the role contract
	 * 
	 * @param privateKey
	 * @param adminAddress
	 * @return
	 */
	public static BaseResp<List<DeployContractData>> deployAllContract(String privateKey, String adminAddress, String serviceUrl) {
		List<DeployContractData> deployContractDatas = new ArrayList<>();
		VoteContractServiceImpl voteContractService = new VoteContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> voteResp = voteContractService.deployContract(credentials, adminAddress, serviceUrl);
		if (!voteResp.checkSuccess()) {
			log.debug("deploy error.error:{}",voteResp.getErrMsg());
			return BaseResp.build(voteResp.getCode(), voteResp.getErrMsg(), deployContractDatas);
		}
		String voteAddress = voteResp.getData().get(0).getContractAddress();
		deployContractDatas.addAll(voteResp.getData());

		DidContracServiceImpl didContractService = new DidContracServiceImpl();
		TransactionResp<List<DeployContractData>> didResp = didContractService.deployContract(credentials);
		if (!didResp.checkSuccess()) {
			log.debug("deploy error.error:{}",didResp.getErrMsg());
			return BaseResp.build(didResp.getCode(), didResp.getErrMsg(), deployContractDatas);
		}
		deployContractDatas.addAll(didResp.getData());

		PctContractServiceImpl pctContractService = new PctContractServiceImpl();
		TransactionResp<List<DeployContractData>> pctResp = pctContractService.deployContract(credentials, voteAddress);
		if (!pctResp.checkSuccess()) {
			log.debug("deploy error.error:{}",pctResp.getErrMsg());
			return BaseResp.build(pctResp.getCode(), pctResp.getErrMsg(), deployContractDatas);
		}
		deployContractDatas.addAll(pctResp.getData());

		CredentialContractServiceImpl credentialContractService = new CredentialContractServiceImpl();
		TransactionResp<List<DeployContractData>> credentialResp = credentialContractService.deployContract(credentials, voteAddress);
		if (!credentialResp.checkSuccess()) {
			log.debug("deploy error.error:{}",credentialResp.getErrMsg());
			return BaseResp.build(credentialResp.getCode(), credentialResp.getErrMsg(), deployContractDatas);
		}
		deployContractDatas.addAll(credentialResp.getData());

		return BaseResp.buildSuccess(deployContractDatas);
	}

	public static void exportDeployContractData(String privateKey, String adminAddress, String serviceUrl) {
		log.debug("begin deploy.");
		BaseResp<List<DeployContractData>> deBaseResp = deployAllContract(privateKey, adminAddress, serviceUrl);
		if (deBaseResp.checkSuccess()) {
			FileOutputStream outSTr = null;
			BufferedOutputStream Buff = null;
			String enter = "\r\n";
			StringBuilder write = new StringBuilder();
			try {
				outSTr = new FileOutputStream(new File("/contract.txt"));
				Buff = new BufferedOutputStream(outSTr);
				for (DeployContractData deployContractData : deBaseResp.getData()) {
					write = new StringBuilder();
					switch (deployContractData.getContractNameValues()) {
					case DID:
						write.append("contract.did.address=");
						PropertyUtils.setProperty(DidConfig.getDidcontractname(), deployContractData.getContractAddress());
						break;
					case VOTE:
						write.append("contract.role.address=");
						PropertyUtils.setProperty(DidConfig.getVotecontractname(), deployContractData.getContractAddress());
						break;
					case PCT:
						write.append("contract.pctdata.address=");
						PropertyUtils.setProperty(DidConfig.getPctcontractname(), deployContractData.getContractAddress());
						break;
					case CREDENTIAL:
						write.append("contract.credential.address=");
						PropertyUtils.setProperty(DidConfig.getCredentialcontractname(), deployContractData.getContractAddress());
						break;
					default:
						break;
					}
					write.append(deployContractData.getContractAddress());
					write.append(enter);
					Buff.write(write.toString().getBytes("UTF-8"));
				}
				Buff.flush();
				Buff.close();
			} catch (Exception e) {
				log.error("export error", e);
			} finally {
				try {
					Buff.close();
					outSTr.close();
				} catch (Exception e) {
					log.error("colse file error", e);
				}
			}
			ReloadClient.deployContractData(deBaseResp.getData());
			ContractService.init();
		}
		
	}
	
	
	public static BaseResp<List<DeployContractData>>  deployContractData(String privateKey, String adminAddress, String serviceUrl) {
		log.debug("begin deploy.");
		BaseResp<List<DeployContractData>> deBaseResp = deployAllContract(privateKey, adminAddress, serviceUrl);
		if (deBaseResp.checkSuccess()) {
			ReloadClient.deployContractData(deBaseResp.getData());
			ContractService.init();
		} 
		return deBaseResp;
	}
}
