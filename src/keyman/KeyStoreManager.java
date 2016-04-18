package keyman;

import helper.FileHelper;

import javax.crypto.EncryptedPrivateKeyInfo;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ting on 16年2月16日.
 */
public class KeyStoreManager {

	private String headerStr = "------ KEY";
	private String footerStr = "------ END";
	private String commentStr = "comment : ";
	private String keyTypeStr = "type : ";
	private String fingerprintStr = "sha256 : ";
	private File keystoreFile = null;
	private String passphrase = "";

	public KeyStoreManager(File keystoreFile, String passphrase) throws IOException {
		this.keystoreFile = keystoreFile;
		this.passphrase = passphrase;
		FileHelper.createEmptyZip(keystoreFile);
	}

	// create new key pair and put to keystore file
	public KeyWrapper createKey(String comment, int size) {
		KeyPair kp = KeyPairUtil.gen(size);
		KeyWrapper wrapper = new KeyWrapper(comment, passphrase, kp.getPrivate(), kp.getPublic());
		try {
			storeKey(wrapper, KeyType.PRIVATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wrapper;
	}

	public KeyWrapper[] getKeys() {
		if (keystoreFile.exists()) {
			try {
				ArrayList<KeyWrapper> keys = new ArrayList<>();
				for (String name : FileHelper.getZipEntriesName(keystoreFile)) {
					byte[] b = FileHelper.getZipEntryByte(keystoreFile, name);
					keys.add(readKey(b, passphrase));
				}
				return keys.toArray(new KeyWrapper[keys.size()]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<KeyWrapper>().toArray(new KeyWrapper[0]);
	}

	public boolean importKey(File f, String passphrase) {
		try {
			return importKey(FileHelper.readBytes(f), passphrase);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean importKey(byte[] b, String passphrase) {
		try {
			KeyWrapper wrapper = readKey(b, passphrase);
			storeKey(wrapper, KeyType.PRIVATE);
			System.out.println("import key " + wrapper.getFingerprint() + " success");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    public boolean importKey(File f) throws Exception {
        return importKey(FileHelper.readBytes(f));
    }

    public boolean importKey(byte[] b) {
        try {
            KeyWrapper wrapper = readKey(b, passphrase);
            storeKey(wrapper, KeyType.PUBLIC);
            System.out.println("import key " + wrapper.getFingerprint() + " success");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

	public boolean removeKey(KeyWrapper wrapper) {
		try {
			FileHelper.removeZipEntry(keystoreFile, wrapper.getFingerprint());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// read a single key
	private KeyWrapper readKey(byte[] b, String passphrase) throws Exception {
		String content = new String(b);
		//System.out.println(content);

		Pattern p = Pattern.compile("(.*)\\n" + commentStr + "(.*)\\n" + keyTypeStr + "(.*)\\n" + fingerprintStr + "(.*)\\n(.*?)\\n(.*)");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			if (m.groupCount() != 6) throw new KeyFileParseException("Error parsing key file.");
			String header = m.group(1);
			String comment = m.group(2);
			String type = m.group(3);
			String fingerprint = m.group(4);
			String encodedKey = m.group(5);
			String footer = m.group(6);

            //System.out.println(header);

			// check parsed value
			if (!header.equals(headerStr) || !footer.equals(footerStr))
				throw new KeyFileParseException("Error parsing key file.");

			if (type.equalsIgnoreCase("PRIVATE")) {
				EncryptedPrivateKeyInfo encKey = new EncryptedPrivateKeyInfo(Base64.getDecoder().decode(encodedKey));
				PrivateKey key = KeyPairUtil.decryptKey(encKey, passphrase);

				return new KeyWrapper(comment, passphrase, key);
			} else if (type.equalsIgnoreCase("PUBLIC")) {
				PublicKey key = (PublicKey) KeyPairUtil.decode(encodedKey.getBytes(), KeyPairUtil.Type.PUBLIC);
				return new KeyWrapper(comment, passphrase, key);
			} else {
				throw new KeyFileParseException("Error parsing key file.");
			}
		} else {
			throw new KeyFileParseException("Error parsing key file.");
		}
	}

	// write key to keystore
	public void storeKey(KeyWrapper wrapper, KeyType t) throws Exception {
		File tmpF = File.createTempFile("tempkey", "ksm");
		writeKey(tmpF, wrapper, t);
		FileHelper.putZipEntry(keystoreFile, tmpF, wrapper.getFingerprint());
		tmpF.delete();
	}

	// write single key to file
	public void writeKey(File f, KeyWrapper keyWrapper, KeyType keyType) throws Exception {
		String comment = commentStr + keyWrapper.getComment() + "\n";
		String type = keyTypeStr + keyType + "\n";
		String fingerprint = fingerprintStr + keyWrapper.getFingerprint() + "\n";
		String encoded = "";
		if (keyType == KeyType.PUBLIC) {
			encoded = KeyPairUtil.encode(keyWrapper.getPublicKey()) + "\n";
		}
		if (keyType == KeyType.PRIVATE) {
			encoded = KeyPairUtil.encode(keyWrapper.getEncryptedPrivateKey()) + "\n";
		}
		String out = headerStr + "\n" + comment + type + fingerprint + encoded + footerStr;
		FileHelper.writeBytes(f, out.getBytes());
	}

	public enum KeyType {
		PUBLIC, PRIVATE
	}

	class KeyFileParseException extends Exception {
		public KeyFileParseException(String msg) {
			super(msg);
		}
	}

	class MissingKeyStoreException extends Exception {
		public MissingKeyStoreException(String msg) {
			super(msg);
		}
	}
}
