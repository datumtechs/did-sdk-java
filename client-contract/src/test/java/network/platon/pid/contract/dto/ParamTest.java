package network.platon.pid.contract.dto;

import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.tx.gas.GasProvider;
import network.platon.pid.common.utils.ClassUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @Auther: Chendongming
 * @Date: 2019/9/9 20:29
 * @Description:
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ParamTest {

    private List<Class<?>> target = new ArrayList<>();
    /**
     * 测试开始前，设置相关行为属性
     * @throws IOException
     */
    @Before
    public void setup() {
        String packageName= ParamTest.class.getPackage().getName();
        Set<Class<?>> classSet = ClassUtil.getClasses(packageName);
        classSet.stream().filter(clazz->!clazz.getName().contains("Test")).forEach(target::add);
    }
    @Test
    public void test() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        for (Class<?> clazz:target){
            if(clazz.isEnum()) {
                if(clazz.getSimpleName().contains("TypeEnum")){
                    CredentialEvidence.TypeEnum typeEnum = CredentialEvidence.TypeEnum.findType("0");
                    typeEnum.getName();
                }
                continue;
            }
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method:methods){
                if(Modifier.isStatic(method.getModifiers())) continue;
                if(Modifier.isProtected(method.getModifiers())) continue;
                if(Modifier.isPrivate(method.getModifiers())) continue;
                if(method.getName().equals("init")) continue;
                Class<?>[] types = method.getParameterTypes();
                Object instance = null;
                if(clazz.getSimpleName().contains("DeployContractData")){
                    Constructor c1=clazz.getDeclaredConstructor(new Class[]{ContractNameValues.class});
                    instance = c1.newInstance(new Object[]{ContractNameValues.AUTHORITY_CONTROLLER});

                    c1=clazz.getDeclaredConstructor(new Class[]{ContractNameValues.class,String.class});
                    instance = c1.newInstance(new Object[]{ContractNameValues.AUTHORITY_CONTROLLER, "123"});

                    c1=clazz.getDeclaredConstructor(new Class[]{ContractNameValues.class,String.class,String.class});
                    instance = c1.newInstance(new Object[]{ContractNameValues.AUTHORITY_CONTROLLER, "123", "123"});
                } else if(clazz.getSimpleName().contains("InitContractData")){
                    Constructor c1=clazz.getDeclaredConstructor(new Class[]{String.class});
                    instance = c1.newInstance(new Object[]{"1212"});
                    c1=clazz.getDeclaredConstructor(new Class[]{String.class,String.class});
                    instance = c1.newInstance(new Object[]{"1212","1212"});
                    c1=clazz.getDeclaredConstructor(new Class[]{String.class,String.class,String.class});
                    instance = c1.newInstance(new Object[]{"1212","1212","1212"});
                    GasProvider gasProvider = new GasProvider() {
                        @Override
                        public BigInteger getGasPrice() {
                            return BigInteger.ONE;
                        }

                        @Override
                        public BigInteger getGasLimit() {
                            return BigInteger.ONE;
                        }
                    };
                    c1=clazz.getDeclaredConstructor(new Class[]{String.class, GasProvider.class});
                    instance = c1.newInstance(new Object[]{"1212",gasProvider});

                    c1=clazz.getDeclaredConstructor(new Class[]{String.class,String.class,String.class, GasProvider.class});
                    instance = c1.newInstance(new Object[]{"1212","1212","1212",gasProvider});
                } else if(clazz.getSimpleName().contains("InitClientDataBuilder")){
                    instance = clazz.newInstance();
                } else if(clazz.getSimpleName().contains("InitClientData")){
                    Constructor c1=clazz.getDeclaredConstructor(new Class[]{String.class,String.class,String.class,String.class,Long.class,List.class});
                    String gasLimit = "";
                    String gasPrice = "";
                    String transPrivateKey = "";
                    String web3Url = "";
                    Long chainId = 108l;
                    List<DeployContractData> deployContractDatas = new ArrayList<>();
                    instance = c1.newInstance(new Object[]{gasLimit,gasPrice,transPrivateKey,web3Url,chainId,deployContractDatas});
                } else if(clazz.getSimpleName().contains("TransactionInfo")){
                    Constructor c1=clazz.getDeclaredConstructor(new Class[]{TransactionReceipt.class});
                    TransactionReceipt transactionReceipt = new TransactionReceipt();
                    transactionReceipt.setBlockHash("0x123");
                    transactionReceipt.setBlockNumber("0x1");
                    transactionReceipt.setTransactionIndex("0x1");
                    instance = c1.newInstance(new Object[]{transactionReceipt});

                    c1=clazz.getDeclaredConstructor(new Class[]{BigInteger.class,String.class,BigInteger.class});
                    instance = c1.newInstance(new Object[]{BigInteger.ONE, "0x", BigInteger.ONE});
                } else {
                    instance = clazz.newInstance();
                }
                if(types.length!=0){
                    Object[] args = new Object[types.length];
                    for (int i=0;i<types.length;i++){
                        if(Boolean.class==types[i]){
                            args[i]=Boolean.TRUE;
                            continue;
                        }
                        if(Double.class==types[i]||"double".equals(types[i].getName())){
                            args[i]=11.3;
                            continue;
                        }
                        if(String.class==types[i]){
                            args[i]="333";
                            continue;
                        }
                        if(Integer.class==types[i]||"int".equals(types[i].getName())){
                            args[i]=333;
                            continue;
                        }
                        if(Long.class==types[i]||"long".equals(types[i].getName())){
                            args[i]=333L;
                            continue;
                        }
                        if(ContractNameValues.class==types[i]){
                            args[i]=ContractNameValues.PID;
                            continue;
                        }
                        try{
                            args[i]=mock(types[i]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    method.invoke(instance,args);
                    continue;
                }
                method.invoke(instance);
            }
        }
        assertTrue(true);
    }
}
