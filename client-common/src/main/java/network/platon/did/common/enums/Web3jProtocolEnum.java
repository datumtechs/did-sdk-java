package network.platon.did.common.enums;

import lombok.Getter;

public enum Web3jProtocolEnum {
    WS("ws://"),HTTP("http://");
    @Getter
    private String head;

    Web3jProtocolEnum(String head){
        this.head = head;
    }

    public static Web3jProtocolEnum findProtocol(String url) {
    	if(url.startsWith(WS.getHead())) return WS;
    	if(url.startsWith(HTTP.getHead())) return HTTP;
    	return null;
    }
}
