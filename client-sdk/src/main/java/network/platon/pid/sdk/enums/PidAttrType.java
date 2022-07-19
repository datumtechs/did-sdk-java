package network.platon.pid.sdk.enums;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The `key` is PlatON DID Document field (attribute) stored in the contract
 */
public enum  PidAttrType {


    CREATED(BigInteger.valueOf(0), "created"),

    PUBLICKEY(BigInteger.valueOf(1), "publicKey"),

    SERVICE(BigInteger.valueOf(2), "service"),
    ;

    /**
     * type code.
     */
    private BigInteger code;
    /**
     * type name.
     */
    private String name;


    PidAttrType(BigInteger code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * get type code.
     *
     * @return type code
     */
    public BigInteger getCode() {
        return this.code;
    }

    /**
     * get type name.
     *
     * @return type name
     */
    public String getName() {
        return this.name;
    }

    public static PidAttrType findPidAttr(BigInteger code) {
        return ENUMS.get(code);
    }
    private static final Map<BigInteger, PidAttrType> ENUMS = new HashMap<>();
    static {
        Arrays.asList(PidAttrType.values()).forEach(en -> ENUMS.put(en.getCode(), en));
    }
}
