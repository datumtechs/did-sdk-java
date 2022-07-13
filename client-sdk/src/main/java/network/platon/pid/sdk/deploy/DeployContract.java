package network.platon.pid.sdk.deploy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import com.platon.crypto.Credentials;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.utils.PropertyUtils;
import network.platon.pid.contract.dto.ContractNameValues;
import network.platon.pid.contract.dto.DeployContractData;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.sdk.client.ReloadClient;
import network.platon.pid.sdk.contract.service.ContractService;
import network.platon.pid.sdk.contract.service.impl.VoteContractServiceImpl;
import network.platon.pid.sdk.contract.service.impl.CredentialContractServiceImpl;
import network.platon.pid.sdk.contract.service.impl.PctContractServiceImpl;
import network.platon.pid.sdk.contract.service.impl.PidContracServiceImpl;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;

/**
 * Release contract tools
 * 
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月16日
 * @Description:
 */
@Slf4j
public class DeployContract {

	public static BaseResp<DeployContractData> deployCredentialContract(String privateKey, String roleContractAddress) {
		ContractService credentialContractService = new CredentialContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = credentialContractService.deployContract(credentials,
				roleContractAddress);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	public static BaseResp<DeployContractData> deployRoleContract(String privateKey, String adminAddress) {
		ContractService contractService = new RoleContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = contractService.deployContract(credentials, adminAddress);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	public static BaseResp<DeployContractData> deployAuthorityContract(String privateKey, String roleContractAddress) {
		ContractService contractService = new VoteContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = contractService.deployContract(credentials,
				roleContractAddress);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	public static BaseResp<DeployContractData> deployPidContract(String privateKey, String roleContractAddress) {
		ContractService contractService = new PidContracServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = contractService.deployContract(credentials,
				roleContractAddress);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}

	public static BaseResp<DeployContractData> deployPctContract(String privateKey, String roleContractAddress) {
		ContractService contractService = new PctContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> resp = contractService.deployContract(credentials,
				roleContractAddress);
		if (!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		return BaseResp.buildSuccess(resp.getData().get(0));
	}
	
	public static BaseResp<String> setAuthorityContractAddr(String privateKey, String roleContractAddress,String authorityControllerAddress) {
		RoleContractServiceImpl roleContractService = new RoleContractServiceImpl();
		PidConfig.setROLE_CONTRACT_ADDRESS(roleContractAddress);
		roleContractService.reloadAddress(new InitContractData(privateKey), ContractNameValues.ROLE);
		string string = new string(authorityControllerAddress);
		TransactionResp<Boolean> resp= roleContractService.setAuthorityContractAddr(string);
		if(resp.checkFail()) {
			log.debug("setAuthorityContractAddr error.error:{}",resp.getErrMsg());
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), null);
		}
		log.info("setAuthorityContractAddr success.",JSONObject.toJSONString(resp));
		return BaseResp.buildSuccess();
	}

	/**
	 * Follow the steps to publish the contract 1. deploy the role contract 2.
	 * deploy the authority contract and use the corresponding address of the role
	 * contract 3. deploy the pid contract and use the corresponding address of the
	 * role contract 4. deploy the pct contract and use the corresponding address of
	 * the role contract 5. deploy the credential contract and use the corresponding
	 * address of the role contract
	 * 
	 * @param privateKey
	 * @param adminAddress
	 * @return
	 */
	public static BaseResp<List<DeployContractData>> deployAllContract(String privateKey, String adminAddress) {
		List<DeployContractData> deployContractDatas = new ArrayList<>();
		ContractService contractService = new RoleContractServiceImpl();
		Credentials credentials = Credentials.create(privateKey);
		TransactionResp<List<DeployContractData>> roleResp = contractService.deployContract(credentials, adminAddress);
		if (!roleResp.checkSuccess()) {
			log.debug("deploy error.error:{}",roleResp.getErrMsg());
			return BaseResp.build(roleResp.getCode(), roleResp.getErrMsg(), deployContractDatas);
		}
		String roleAddress = roleResp.getData().get(0).getContractAddress();
		deployContractDatas.addAll(roleResp.getData());

		contractService = new VoteContractServiceImpl();
		TransactionResp<List<DeployContractData>> authorityResp = contractService.deployContract(credentials,
				roleAddress);
		if (!authorityResp.checkSuccess()) {
			log.debug("deploy error.error:{}",authorityResp.getErrMsg());
			return BaseResp.build(authorityResp.getCode(), authorityResp.getErrMsg(), deployContractDatas);
		}
		deployContractDatas.addAll(authorityResp.getData());
		
		contractService = new PidContracServiceImpl();
		TransactionResp<List<DeployContractData>> pidResp = contractService.deployContract(credentials, roleAddress);
		if (!pidResp.checkSuccess()) {
			log.debug("deploy error.error:{}",pidResp.getErrMsg());
			return BaseResp.build(pidResp.getCode(), pidResp.getErrMsg(), deployContractDatas);
		}
		deployContractDatas.addAll(pidResp.getData());

		contractService = new PctContractServiceImpl();
		TransactionResp<List<DeployContractData>> pctResp = contractService.deployContract(credentials, roleAddress);
		if (!pctResp.checkSuccess()) {
			log.debug("deploy error.error:{}",pctResp.getErrMsg());
			return BaseResp.build(pctResp.getCode(), pctResp.getErrMsg(), deployContractDatas);
		}
		deployContractDatas.addAll(pctResp.getData());

		contractService = new CredentialContractServiceImpl();
		TransactionResp<List<DeployContractData>> credentialResp = contractService.deployContract(credentials,
				roleAddress);
		if (!credentialResp.checkSuccess()) {
			log.debug("deploy error.error:{}",credentialResp.getErrMsg());
			return BaseResp.build(credentialResp.getCode(), credentialResp.getErrMsg(), deployContractDatas);
		}
		deployContractDatas.addAll(credentialResp.getData());

		RoleContractServiceImpl roleContractService = new RoleContractServiceImpl();
		PidConfig.setROLE_CONTRACT_ADDRESS(roleAddress);
		roleContractService.reloadAddress(new InitContractData(privateKey), ContractNameValues.ROLE);
		string string = new string(authorityResp.getData().get(1).getContractAddress());
		TransactionResp<Boolean> resp= roleContractService.setAuthorityContractAddr(string);
		if(resp.checkFail()) {
			log.debug("setAuthorityContractAddr error.error:{}",resp.getErrMsg());
			return BaseResp.build(resp.getCode(), resp.getErrMsg(), deployContractDatas);
		}
		log.info("setAuthorityContractAddr success. resp:{}",JSONObject.toJSONString(resp));
		return BaseResp.buildSuccess(deployContractDatas);
	}

	public static void exportDeployContractData(String privateKey, String adminAddress) {
		log.debug("begin deploy.");
		BaseResp<List<DeployContractData>> deBaseResp = deployAllContract(privateKey, adminAddress);
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
					case PID:
						write.append("contract.pid.address=");
						PropertyUtils.setProperty(PidConfig.getPidcontractname(), deployContractData.getContractAddress());
						break;
					case ROLE:
						write.append("contract.role.address=");
						PropertyUtils.setProperty(PidConfig.getRolecontractname(), deployContractData.getContractAddress());
						break;
					case AUTHORITY_CONTROLLER:
						write.append("contract.authoritycontroller.address=");
						PropertyUtils.setProperty(PidConfig.getAuthoritycontrollercontractname(), deployContractData.getContractAddress());
						break;
					case AUTHORITY_DATA:
						write.append("contract.authoritydata.address=");
						PropertyUtils.setProperty(PidConfig.getAuthoritydatacontractname(), deployContractData.getContractAddress());
						break;
					case PCT_CONTROLLER:
						write.append("contract.pctcontroller.address=");
						PropertyUtils.setProperty(PidConfig.getPctcontrollercontractname(), deployContractData.getContractAddress());
						break;
					case PCT_DATA:
						write.append("contract.pctdata.address=");
						PropertyUtils.setProperty(PidConfig.getPctdatacontractname(), deployContractData.getContractAddress());
						break;
					case CREDENTIAL:
						write.append("contract.credential.address=");
						PropertyUtils.setProperty(PidConfig.getCredentialcontractname(), deployContractData.getContractAddress());
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
	
	
	public static BaseResp<List<DeployContractData>>  deployContractData(String privateKey, String adminAddress) {
		log.debug("begin deploy.");
		BaseResp<List<DeployContractData>> deBaseResp = deployAllContract(privateKey, adminAddress);
		if (deBaseResp.checkSuccess()) {
			ReloadClient.deployContractData(deBaseResp.getData());
			ContractService.init();
		} 
		return deBaseResp;
	}
}
