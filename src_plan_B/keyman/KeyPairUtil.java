package keyman;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * Created by ting on 16年2月15日.
 */
public class KeyPairUtil {

	// Generate KeyPair, in RSA algorithm
	public static KeyPair gen(int size) {
		try {
			// Generate keys
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(size);
			KeyPair kp = kpg.genKeyPair();
			return kp;
		} catch (Exception e) {
			return null;
		}
	}

	// Encode the key to Base64 String
	public static <T> String encode(T key) throws Exception {
		String begining = "";
		String ending = "";
		byte[] b = new byte[0];

		if (key instanceof PrivateKey) {
			b = ((PrivateKey) key).getEncoded();
		} else if (key instanceof PublicKey) {
			b = ((PublicKey) key).getEncoded();
		} else if (key instanceof EncryptedPrivateKeyInfo) {
			b = ((EncryptedPrivateKeyInfo) key).getEncoded();
		} else {
			throw new UnknownKeyTypeException("Parameter not recognized");
		}

		return begining + Base64.getEncoder().encodeToString(b) + ending;
	}

	public static EncryptedPrivateKeyInfo encryptKey(PrivateKey privKey, String passphrase) throws Exception {
		String pbeAlgo = "PBEWithSHA1AndDESede";

		// Preparing PBE
		SecureRandom rand = SecureRandom.getInstanceStrong();
		byte[] salt = new byte[8];
		rand.nextBytes(salt);

		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 50);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());

		SecretKeyFactory skf = SecretKeyFactory.getInstance(pbeAlgo);
		SecretKey skey = skf.generateSecret(pbeKeySpec);

		Cipher cipher = Cipher.getInstance(pbeAlgo);
		cipher.init(Cipher.ENCRYPT_MODE, skey, pbeParamSpec);

		// encrypt private key
		byte[] b = cipher.doFinal(privKey.getEncoded());
		// construct PKCS8 obj
		AlgorithmParameters algoParam = AlgorithmParameters.getInstance(pbeAlgo);
		algoParam.init(pbeParamSpec);
		return new EncryptedPrivateKeyInfo(algoParam, b);
	}

	public static byte[] encryptSmallBytes(PublicKey pubKey, byte[] b) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return cipher.doFinal(b);
	}

	public static byte[] decryptSmallBytes(PrivateKey privKey, byte[] b) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privKey);
		return cipher.doFinal(b);
	}

	public static PrivateKey decryptKey(EncryptedPrivateKeyInfo encKey, String passphrase) throws Exception {
		PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray());
		SecretKeyFactory skf = SecretKeyFactory.getInstance(encKey.getAlgName());
		SecretKey skey = skf.generateSecret(pbeKeySpec);

		AlgorithmParameters algoParam = encKey.getAlgParameters();
		Cipher cipher = Cipher.getInstance(encKey.getAlgName());
		cipher.init(Cipher.DECRYPT_MODE, skey, algoParam);

		// decrypt
		KeySpec keySpec = encKey.getKeySpec(cipher);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		// get private key
		PrivateKey privKey = kf.generatePrivate(keySpec);
		//PublicKey pubKey = kf.generatePublic(keySpec);

		return privKey;
	}

	public static KeyPair getKeyPair(PrivateKey privKey) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		// get public
		RSAPrivateKeySpec privKeySpec = kf.getKeySpec(privKey, RSAPrivateKeySpec.class);
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(privKeySpec.getModulus(), BigInteger.valueOf(65537));
		PublicKey pubKey = kf.generatePublic(pubKeySpec);

		return new KeyPair(pubKey, privKey);
	}

	// return private/public key with the private key byte encoded data
	public static <T> T decode(byte[] encodedKey, T inputKeyClass) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		if (inputKeyClass instanceof PrivateKey) {
			// get private
			PKCS8EncodedKeySpec pkcs = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedKey));
			PrivateKey privKey = kf.generatePrivate(pkcs);
			return (T) privKey;
		}
		if (inputKeyClass instanceof PublicKey) {
			X509EncodedKeySpec x509 = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedKey));
			PublicKey pubKey = kf.generatePublic(x509);
			return (T) pubKey;
		}

		throw new UnknownKeyTypeException("Unsupported input key.");
	}

	// ------
	static class UnknownKeyTypeException extends Exception {
		public UnknownKeyTypeException(String msg) {
			super(msg);
		}
	}
}
