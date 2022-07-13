

package network.platon.pid.common.enums;


import lombok.Getter;

public enum PctTypeEnum {

    /**
     * original type, used to create original type credential.
     */
    ORIGINAL(0, "original"),

    /**
     * zkp type, used to create zkp type credential.
     */
    ZKP(1, "zkp");

    /**
     * type code.
     */
    @Getter
    private Integer code;
    /**
     * type name.
     */
    @Getter
    private String name;

    /**
     * constructor.
     *
     * @param code cpt type code
     * @param name cpt type name
     */
    PctTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
