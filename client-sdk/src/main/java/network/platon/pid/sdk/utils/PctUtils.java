package network.platon.pid.sdk.utils;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import network.platon.pid.csies.utils.ConverDataUtils;


@Slf4j
public final class PctUtils {

    /**
     * claim : { "name": "bob", “age": 10 }
     * schema:
     * {
     *     "properties": {
     *             "name": {
     *                 "type": "string"
     *             },
     *             "age": {
     *                 "type": "integer",
     *             }
     *     }
     * }
     * @param jsonSchema
     * @return boolean
     */
    public static boolean isPctJsonSchemaValid(String jsonSchema) {
        try {
            return ConverDataUtils.isCptJsonSchemaValid(jsonSchema);
        } catch (Exception e) {
            log.error("schema error ",e);
            return false;
        }
    }

	public static boolean verifyPctFormat(String jsonSchema, Map<String, Object> claim) {
        try {
            String claimStr = ConverDataUtils.serialize(claim);
            String cptJsonSchema = ConverDataUtils.serialize(jsonSchema);

            if (!ConverDataUtils.isCptJsonSchemaValid(cptJsonSchema)) {
            	log.error("verifyPctFormat cptJsonSchema is not vaild");
                return false;
            }
            if (!ConverDataUtils.isValidateJsonVersusSchema(claimStr, cptJsonSchema)) {
            	log.error("verifyPctFormat claimStr and cptJsonSchema is not vaild");
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("pctId verifyPctFormat fail " , e);
            return false;
        }
    }
}
