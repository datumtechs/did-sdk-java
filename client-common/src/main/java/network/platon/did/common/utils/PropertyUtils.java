package network.platon.did.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);
    private static final String DID_PROP_NAME = "didentity.properties";
    private static Properties prop = new Properties();

    static {
        load();
    }

    /**
     * load properties from specific config file.
     *
     * @param filePath properties config file.
     */
    private static void loadProperties(String filePath) throws IOException {

        InputStream in;
        in = PropertyUtils.class.getClassLoader().getResourceAsStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        prop.load(br);
        br.close();
        in.close();
    }

    /**
     * get property value by specific key.
     *
     * @param key property key
     * @return value of the key
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    /**
     * get property value by specific key.
     *
     * @param key property keys
     * @param defaultValue default value
     * @return value of the key
     */
    public static String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }
    
    /**
     * get the all key from Properties.
     * 
     * @return value of the key Set
     */
    public static Set<Object> getAllPropertyKey() {
        return prop.keySet();
    }
    
    /**
     * load the properties.
     */
    private static void load() {
        
        try {
            loadProperties(DID_PROP_NAME);
        } catch (IOException e) {
            logger.error("[PropertyUtils] Load DIdentity.properties file failed.", e);
        }
    }
    
    /**
     * reload the properties.
     * 
     */
    public static void reload() {
        load();
    }
    
    public static void setProperty(String key, String value) throws Exception {
        //String profilepath = PropertyUtils.class.getResource("/").getPath()+DID_PROP_NAME;

        String profilepath = PropertyUtils.class.getClassLoader().getResource(DID_PROP_NAME).getPath();
        // String profilepath = profileURI.substring(1);
    	PropertiesConfiguration config  = new PropertiesConfiguration(profilepath);
        config.setAutoSave(true);
        config.setProperty(key, value);
    }
}
