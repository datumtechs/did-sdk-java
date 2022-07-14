package network.platon.pid.contract.dto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class CredentialEvidence {

	private String hash;
	
	private String signaturedata;
	
	private String signer;

	private String create;
	
	public enum TypeEnum {
		
		SIGNER("0"),SIGNATUREDATA("1");
		
		private String name;
		TypeEnum(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
		
		public static TypeEnum findType(String name) {
	    	return ENUMS.get(name);
	    }
	    private static final Map <String, TypeEnum> ENUMS = new HashMap <>();
	    static {
	        Arrays.asList(TypeEnum.values()).forEach(en -> ENUMS.put(en.getName(), en));
	    } 
	}
	
}


