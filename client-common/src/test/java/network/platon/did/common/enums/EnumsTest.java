package network.platon.did.common.enums;

import network.platon.did.common.utils.ClassUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EnumsTest {

    private List<Class<?>> target = new ArrayList<>();
    /**
     * 测试开始前，设置相关行为属性
     * @throws IOException
     */
    @Before
    public void setup() {
        String packageName= EnumsTest.class.getPackage().getName();
        Set<Class<?>> classSet = ClassUtil.getClasses(packageName);
        classSet.stream().filter(clazz->!clazz.getName().contains("Test")).forEach(target::add);
    }
    @Test
    public void test() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        for (Class<?> clazz:target){
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String descriptor = Modifier.toString(field.getModifiers());//获得其属性的修饰
                if(descriptor.contains("public static final")){
                    Object object = field.get(clazz);
                    System.out.println(object);
                }
            }
        }
        assertTrue(true);
    }
}
