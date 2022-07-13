package network.platon.pid.contract.client;

import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.enums.Web3jProtocolEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

/**
 * @Auther: Chendongming
 * @Date: 2019/9/9 20:29
 * @Description:
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ClientTest {

    /**
     * 测试开始前，设置相关行为属性
     * @throws IOException
     */
    @Before
    public void setup() {
    }
    @Test
    public void test(){
        RetryableClient retryableClient = new RetryableClient();
        PidConfig.setWeb3jProtocolEnum(Web3jProtocolEnum.HTTP);
        PidConfig.setPLATON_URL("10.10.1.5:6789");
        retryableClient.init();
        Web3jWrapper web3jWrapper = retryableClient.getWeb3jWrapper();
        RetryableClient.setWeb3j(web3jWrapper.getWeb3j());
        web3jWrapper.setAddress(web3jWrapper.getAddress());
        web3jWrapper.setWeb3jService(web3jWrapper.getWeb3jService());
        web3jWrapper.setWeb3j(web3jWrapper.getWeb3j());
    }

}
