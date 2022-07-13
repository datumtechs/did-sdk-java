package network.platon.pid.csies.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.platon.crypto.Hash;
import com.platon.utils.Numeric;
import org.apache.commons.lang3.RandomStringUtils;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.utils.PropertyUtils;

@Slf4j
public class ConverDataUtils {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private static final String DEFAULT_SALT_LENGTH = "4";
	
	private static final String KEY_FROM_TOJSON = "$from";
	
	static {
        // sort by letter
        OBJECT_MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        // when map is serialization, sort by key
        OBJECT_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // ignore mismatched fields
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        // use field for serialize and deSerialize
        OBJECT_MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

	}
	
	public static byte[] sha3(byte[] input) {
        return Hash.sha3(input, 0, input.length);
    }

    public static String getHash(String hexInput) {
        return sha3(hexInput);
    }
    
    public static String sha3(String utfString) {
        return Numeric.toHexString(sha3(utfString.getBytes(StandardCharsets.UTF_8)));
    }
    
    public static <T> String serialize(T object) {
        Writer write = new StringWriter();
        try {
            OBJECT_MAPPER.writeValue(write, object);
        } catch (JsonGenerationException e) {
            log.error("JsonGenerationException when serialize object to json", e);
        } catch (JsonMappingException e) {
            log.error("JsonMappingException when serialize object to json", e);
        } catch (IOException e) {
            log.error("IOException when serialize object to json", e);
        }
        return write.toString();
    }
    
    public static boolean isValidateJsonVersusSchema(String jsonData, String jsonSchema) {
    	try {
            JsonNode jsonDataNode = loadJsonObject(jsonData);
            JsonNode jsonSchemaNode = loadJsonObject(jsonSchema);
            JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(jsonSchemaNode);

            ProcessingReport report = schema.validate(jsonDataNode);
            if (report.isSuccess()) {
                log.info(report.toString());
                return true;
            } else {
                Iterator<ProcessingMessage> it = report.iterator();
                StringBuilder errorMsg = new StringBuilder();
                while (it.hasNext()) {
                    errorMsg.append(it.next().getMessage());
                }
                log.error("Json schema validator failed, error: {}", errorMsg.toString());
                return false;
            }
        } catch (Exception e) {
        	log.error("Json schema validator failed,exception", e);
			return false;
		}
    }
    
    public static boolean isCptJsonSchemaValid(String cptJsonSchema) throws IOException {
        return StringUtils.isNotEmpty(cptJsonSchema)
            && isValidJsonSchema(cptJsonSchema);
    }
    
    public static JsonNode loadJsonObject(String jsonString) throws IOException {
        return JsonLoader.fromString(jsonString);
    }
    
    public static boolean isValidJsonSchema(String jsonSchema) throws IOException {
        return JsonSchemaFactory
            .byDefault()
            .getSyntaxValidator()
            .schemaIsValid(loadJsonObject(jsonSchema));
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> objToMap(Object object) throws Exception {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(serialize(object));
        return (HashMap<String, Object>)OBJECT_MAPPER.convertValue(jsonNode, HashMap.class);
    }
    
    public static String mapToCompactJson(Map<String, Object> map) throws Exception {
        return OBJECT_MAPPER.readTree(serialize(map)).toString();
    }
    
    @SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(T obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            log.error("clone object has error.", e);
        }
        return clonedObj;
    }
    
    public static String generalUUID() {
    	String uuid = UUID.randomUUID().toString().replaceAll("-","");
    	return uuid;
    }

    public static String getRandomSalt() {
    	/**
    	 * Randomly generate salt according to different lengths
    	 */
        String length = PropertyUtils.getProperty("salt.length", DEFAULT_SALT_LENGTH);
        int saltLength = Integer.valueOf(length);
        String salt = RandomStringUtils.random(saltLength, true, true);
        return salt;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T deserialize(String json, Class<T> clazz) {
        Object object = null;
        if (isValidFromToJson(json)) {
            log.error("this jsonString is converted by toJson(), please use fromJson() to deserialize it");
            throw new RuntimeException("deserialize json to Object error");
        }
        try {
			object = OBJECT_MAPPER.readValue(json, TypeFactory.rawClass(clazz));
		} catch (Exception e) {
			log.error("json readvalue error",e);
			throw new RuntimeException("OBJECT_MAPPER read vale error");
		}
        return (T) object;
    }
    
    public static boolean isValidFromToJson(String json) {
        if (StringUtils.isBlank(json)) {
        	log.error("input json param is null.");
            return false;
        }
        JsonNode jsonObject = null;
        try {
            jsonObject = loadJsonObject(json);
        } catch (IOException e) {
        	log.error("convert jsonString to JSONObject failed." + e);
            return false;
        }
        return jsonObject.has(KEY_FROM_TOJSON);
    }

}
