package keyman;

import helper.HashHelper;

import javax.crypto.EncryptedPrivateKeyInfo;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by ting on 16年2月16日.
 */
public class KeyWrapper {

	private String comment = "";
	private String passphrase;
	private PrivateKey privateKey = null;
	private PublicKey publicKey = null;

	public KeyWrapper(String comment, String passphrase, PrivateKey privateKey, PublicKey publicKey) {
		this.comment = comment;
		this.passphrase = passphrase;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public KeyWrapper(String comment, String passphrase, PrivateKey privateKey) throws Exception {
		this.comment = comment;
		this.passphrase = passphrase;
		this.privateKey = privateKey;
		this.publicKey = KeyPairUtil.getKeyPair(privateKey).getPublic();
	}

	public KeyWrapper(String comment, String passphrase, PublicKey publicKey) {
		this.comment = comment;
		this.passphrase = passphrase;
		this.publicKey = publicKey;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getPassphrase() { return passphrase; }

	public EncryptedPrivateKeyInfo getEncryptedPrivateKey() throws Exception {
		return KeyPairUtil.encryptKey(privateKey, passphrase);
	}

	public PrivateKey getPrivateKey() throws Exception {
		if (privateKey == null)
			throw new Exception("No private key supplied.");
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public KeyPair getKeyPair() throws Exception {
		if (privateKey == null)
			throw new Exception("No private key supplied.");
		if (publicKey == null)
			return KeyPairUtil.getKeyPair(privateKey);
		return new KeyPair(publicKey, privateKey);
	}

	public String getFingerprint() {
		try {
			return HashHelper.sha256(publicKey.getEncoded());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "hash error";
	}
}
