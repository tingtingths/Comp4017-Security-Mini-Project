package keyman;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * Created by ting on 16年2月21日.
 */
public class SymKeyUtil {

	private final static byte ALGO_AES = 0x1;
	private final static byte ALGO_DES = ALGO_AES + 0x1;
	private final static byte ALGO_3DES = ALGO_DES + 0x1;

	public enum SymKeyAlgo {
		AES, DES, DESede;
	}


	public static SecretKey gen(SymKeyAlgo algo, int size) throws Exception {
		String strAlgo = "";
		if (algo == SymKeyAlgo.AES) strAlgo = "AES";
		if (algo == SymKeyAlgo.DES) strAlgo = "DES";
		if (algo == SymKeyAlgo.DESede) strAlgo = "DESede";
		KeyGenerator kgen = KeyGenerator.getInstance(strAlgo);
		kgen.init(size);
		return kgen.generateKey();
	}

	/* Encrypt symmetric key with public key
	 *
	 * The encrypted symmetric key is in following format:
	 * byte[0], algorithm identifier
	 * byte[1] -> byte[n], encrypted key, where n = the RSA key size in byte
	 */
	public static byte[] encryptKey(SecretKey key, PublicKey pubKey) throws Exception {
		byte algoB = 0x0;
		byte[] b = KeyPairUtil.encryptSmallBytes(pubKey, key.getEncoded());
		byte[] out = new byte[b.length + 1];
		if (key.getAlgorithm().equalsIgnoreCase("aes")) algoB = ALGO_AES;
		else if (key.getAlgorithm().equalsIgnoreCase("des")) algoB = ALGO_DES;
		else if (key.getAlgorithm().equalsIgnoreCase("desede")) algoB = ALGO_3DES;
		else throw new UnknowSymKeyAlgorithm("Unknown algorithm");

		// use first byte to store algorithm type
		System.arraycopy(new byte[]{algoB}, 0, out, 0, 1);
		System.arraycopy(b, 0, out, 1, b.length);
		return out;
	}

	// decrypt symmetric key with private key
	public static SecretKey decryptKey(byte[] b, PrivateKey privKey) throws Exception {
		String algo = "";
		byte algoB = b[0];
		byte[] decB = KeyPairUtil.decryptSmallBytes(privKey, Arrays.copyOfRange(b, 1, b.length));

		// read first byte for algorithm
		if (algoB == ALGO_AES) algo = "AES";
		else if (algoB == ALGO_DES) algo = "DES";
		else if (algoB == ALGO_3DES) algo = "DESede";
		else throw new UnknowSymKeyAlgorithm("Unknown algorithm");

		return new SecretKeySpec(decB, 0, decB.length, algo);
	}

	static class UnknowSymKeyAlgorithm extends Exception {
		public UnknowSymKeyAlgorithm(String msg) {
			super(msg);
		}
	}
}
