package helper;

import keyman.SymKeyUtil;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by ting on 16年2月21日.
 * Encrypt/Decrypt file.
 */
public class SymmetricEncrypter {

	private PublicKey pubKey = null;
	private SecretKey symKey = null;
	private String symAlgo = "";
	private Cipher cipher = null;

	public SymmetricEncrypter(PublicKey pubKey, SecretKey symKey) throws Exception {
		this.pubKey = pubKey;
		this.symKey = symKey;
		symAlgo = symKey.getAlgorithm();
		cipher = Cipher.getInstance(symAlgo);
		cipher.init(Cipher.ENCRYPT_MODE, symKey);
	}

	public byte[] encrypt(byte[] b) throws Exception {
		return cipher.doFinal(b);
	}

	public byte[] encrypt(byte[] b, int offset, int len) throws Exception {
		return cipher.doFinal(b, offset, len);
	}

	/* Encrypt file with symmetric key
	 *
	 * The encrypted file is in the following format:
	 * byte[0] -> byte[1], a short for storing the length of the encrypted symmetric key attached
	 * byte[2] -> byte[x], the encrypted AES key (refer to the format defined in SymKeyUtil), where x = (2 + the short value above) - 1
	 * byte[x+1] -> byte[n], encrypted file data
	 */
	public boolean encrypt(File inF, File outF) throws Exception {
		// key
		byte[] encSymKeyB = SymKeyUtil.encryptKey(symKey, pubKey);
		// key len
		short symKeylen = (short) encSymKeyB.length;
		byte[] symKeyLenB = new byte[]{
				(byte) (symKeylen & 0xff),
				(byte) ((symKeylen >> 8) & 0xff)
		};

		try {
			// streams
			FileOutputStream fos = new FileOutputStream(outF);
			FileInputStream fis = new FileInputStream(inF);

			// write metadata
			fos.write(symKeyLenB);
			fos.write(encSymKeyB);
			//write data
			int bufSize = 8192;
			byte[] buf = new byte[bufSize];
			int k = 0;
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);

			while ((k = fis.read(buf, 0, bufSize)) != -1) {
				cos.write(buf, 0, k);
			}

			cos.close();
			fos.close();
			fis.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
