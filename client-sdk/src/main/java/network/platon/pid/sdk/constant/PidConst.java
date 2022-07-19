package network.platon.pid.sdk.constant;

import com.platon.rlp.wasm.datatypes.Uint8;
import com.platon.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Storage variable
 * @Auther: Rongjin Zhang
 * @Date: 2020年5月29日
 * @Description:
 */
public class PidConst {

	public static final String PID_PREFIX = "did:pid:";

	public static final String PID_EVENT_ATTRIBUTE_CHANGE_STR = "PIDAttributeChanged";
	public static final String PID_EVENT_ATTRIBUTE_CHANGE_RLP = Numeric.toHexStringWithPrefixZeroPadded(Numeric.toBigInt(
			PID_EVENT_ATTRIBUTE_CHANGE_STR.getBytes(StandardCharsets.UTF_8)), 64);

	public static final String PLATONE_PID_PATTERN = "did:pid:[a-zA-Z0-9]{42}";

	public static final String PLATONE_ADDRESS_PATTERN = "[a-zA-Z0-9]{42}";

	public static final String PLATONE_PRIVATE_KEY_PATTERN = "0x[a-fA-f0-9]{60,64}|[a-fA-f0-9]{60,64}";
	public static final String PLATONE_PUBLICK_KEY_PATTERN = "0x[a-fA-f0-9]{120,128}|[a-fA-f0-9]{120,128}";

	public static final int ADDRESS_LENGTH_IN_HEX = 42;
	public static final int MAX_AUTHORITY_ISSUER_NAME_LENGTH = 32;

	// TODO
	public static final String PID_DEFAULT_CONTEXT = "https://w3id.org/did/v1";


	public static enum DocumentAttrStatus {

		PID_PUBLICKEY_INVALID(Uint8.of(1), "1"),
		PID_PUBLICKEY_VALID(Uint8.of(0), "0"),

		PID_SERVICE_INVALID(Uint8.of(1), "1"),
		PID_SERVICE_VALID(Uint8.of(0), "0");

		/**
		 * The Attr status code on Document
		 */
		private Uint8 status;

		/**
		 * The Attr status string on Document
		 */
		private String tag;


		/**
		 * Constructor.
		 */
		DocumentAttrStatus(Uint8 status, String tag) {
			this.status = status;
			this.tag = tag;
		}

		public Uint8 getStatus() {
			return status;
		}

		public String getTag() {
			return tag;
		}

	}

	public static enum DocumentStatus {
		ACTIVATION(BigInteger.valueOf(0), "activation"),
		DEACTIVATION(BigInteger.valueOf(1), "deactivation");


		/**
		 * The Code of the Document status.
		 */
		private BigInteger code;

		/**
		 * The Tag Name of the Document status.
		 */
		private String tag;

		/**
		 * Constructor.
		 */
		DocumentStatus(BigInteger code, String tag) {
			this.code = code;
			this.tag = tag;
		}

		public BigInteger getCode() {
			return this.code;
		}
		public String getTag() {
			return this.tag;
		}


		private static final Map<BigInteger, DocumentStatus> ENUMS = new HashMap<BigInteger, DocumentStatus>();
		static {
			Arrays.asList(DocumentStatus.values()).forEach(en -> ENUMS.put(en.getCode(), en));
		}

		public static DocumentStatus findStatus(BigInteger code) {
			return ENUMS.get(code);
		}
	}

	/**
	 * the document attr value member length
	 */
	public static final int PID_SERVICE_VALUE_MEM_LEN = 4;
	public static final int PID_PUBLICKEY_VALUE_MEM_LEN = 5;
	public static final int PID_AUTH_VALUE_MEM_LEN = 3;


	/**
     * CredentialService related param names.
     */
    public static final String CLAIM = "claimData";

    public static final String DEFAULT_PRESENTATION_TYPE = "VerifiablePresentation";



	public static enum PublicKeyType {
		RSA("RSA"),
		SECP256K1("Secp256k1");

		/**
		 * The Type Name of the Credential Proof.
		 */
		private String typeName;

		/**
		 * Constructor.
		 */
		PublicKeyType(String typeName) {
			this.typeName = typeName;
		}

		/**
		 * Getter.
		 *
		 * @return typeName
		 */
		public String getTypeName() {
			return typeName;
		}

		private static final Map<String, PublicKeyType> ENUMS = new HashMap<String, PublicKeyType>();
		static {
			Arrays.asList(PublicKeyType.values()).forEach(en -> ENUMS.put(en.getTypeName(), en));
		}
		public static PublicKeyType findStatus(String typeName) {
			return ENUMS.get(typeName);
		}
	}
}

