package network.platon.did.contract.client;

import com.platon.protocol.Web3j;
import com.platon.protocol.Web3jService;
import com.platon.protocol.http.HttpService;
import com.platon.protocol.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.config.DidConfig;
import network.platon.did.common.enums.Web3jProtocolEnum;
import org.springframework.retry.annotation.Retryable;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 链参数统一配置项
 *
 * @Description:
 */
@Slf4j
public class RetryableClient {
    private static final ReentrantReadWriteLock WEB3J_CONFIG_LOCK = new ReentrantReadWriteLock();
    private List<Web3jWrapper> web3jWrappers = new ArrayList<>();
    private Web3jWrapper currentWeb3jWrapper;
    private static Web3j web3j;

    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public void init() {
        WEB3J_CONFIG_LOCK.writeLock().lock();
        try {
            web3jWrappers.clear();
            Web3jProtocolEnum protocol = DidConfig.getWeb3jProtocolEnum();
            String address = DidConfig.getPLATON_URL();
            this.reload(protocol, address);
            if (web3jWrappers.isEmpty()) throw new RuntimeException("没有可用Web3j实例!");
        } catch (Exception e) {
            log.error("加载Web3j配置错误,将重试:", e);
            throw e;
        } finally {
            WEB3J_CONFIG_LOCK.writeLock().unlock();
        }
    }

    public void reload(Web3jProtocolEnum protocol, String address) {
        Web3jService service = getWeb3Server(protocol, address);
        Web3jWrapper web3j = Web3jWrapper.builder()
                .address(protocol.getHead() + address)
                .web3jService(service)
                .web3j(Web3j.build(service))
                .build();
        currentWeb3jWrapper = web3j;
        RetryableClient.web3j = Web3j.build(service);
        web3jWrappers.add(web3j);
    }

    public static Web3jService getWeb3Server(Web3jProtocolEnum protocol, String address) {
        Web3jService service = null;
        if (protocol == Web3jProtocolEnum.WS) {
            WebSocketService wss = new WebSocketService(protocol.getHead() + address, true);
            try {
                wss.connect();
                service = wss;
            } catch (ConnectException e) {
                log.error("Websocket地址({})无法连通:", protocol.getHead() + address, e);
                return null;
            }
        } else if (protocol == Web3jProtocolEnum.HTTP) {
            service = new HttpService(protocol.getHead() + address);
        } else {
            log.error("Web3j连接协议[{}]不合法!", protocol.getHead());
            System.exit(1);
        }
        return service;
    }

    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public Web3jWrapper getWeb3jWrapper() {
        WEB3J_CONFIG_LOCK.readLock().lock();
        try {
            return currentWeb3jWrapper;
        } catch (Exception e) {
            log.error("加载Web3j配置错误:", e);
        } finally {
            WEB3J_CONFIG_LOCK.readLock().unlock();
        }
        return null;
    }

    public static Web3j getWeb3j() {
        return web3j;
    }

    public static void setWeb3j(Web3j web3j) {
        RetryableClient.web3j = web3j;
    }

}
