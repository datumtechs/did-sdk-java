package network.platon.pid.csies.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConverDataUtilTest {

    @Test
    public void test_get() {
        String uuid = ConverDataUtils.generalUUID();
        String hash = ConverDataUtils.getHash(uuid);
        Assert.assertNotNull(hash);
        String salt= ConverDataUtils.getRandomSalt();
        Assert.assertNotNull(salt);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_clone() throws IOException {

        Map<String,String> map = new LinkedHashMap<>();
        map.put("key","1");
        Map<String,String> mapClone = ConverDataUtils.clone((LinkedHashMap)map);
        Assert.assertTrue(mapClone.containsKey("key"));
        String pctJson = "{\"properties\": { \"name\": { \"type\": \"string\" }, \"no\": { \"type\": \"string\" }, \"data\": { \"type\": \"string\" }}}";
        Assert.assertTrue(ConverDataUtils.isCptJsonSchemaValid(pctJson));
        String json = "{\"no\":\"123\",\"data\":\"456\",\"name\":\"zhangsan\"}";
        Assert.assertTrue(ConverDataUtils.isValidateJsonVersusSchema(json, pctJson));

        String jsonFail = "{\"no\":123,\"data\":\"456\",\"name\":\"zhangsan\"}";
        Assert.assertTrue(!ConverDataUtils.isValidateJsonVersusSchema(jsonFail, pctJson));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_serialize() throws Exception {

        Map<String,Object> map = new HashMap<>();
        map.put("key","1");
        String mapClone = ConverDataUtils.serialize((HashMap)map);
        map = ConverDataUtils.deserialize(mapClone,Map.class);
        Assert.assertTrue(map.containsKey("key"));
        ConverDataUtils.mapToCompactJson(map);

    }
}
