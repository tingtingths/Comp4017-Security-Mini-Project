package helper;

import keyman.SymKeyUtil;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PrivateKey;

/**
 * Created by ting on 2016/2/22.
 */
public class SymmetricDecrypter {

	private PrivateKey privKey = null;

	public SymmetricDecrypter(PrivateKey privKey) {
		this.privKey = privKey;
	}

	public byte[] decrypt(byte[] b, SecretKey symKey) throws Exception {
		Cipher cipher = Cipher.getInstance(symKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(symKey.getEncoded(), symKey.getAlgorithm()));

		return cipher.doFinal(b);
	}

	public byte[] decrypt(byte[] b, int offset, int len, SecretKey symKey) throws Exception {
		Cipher cipher = Cipher.getInstance(symKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(symKey.getEncoded(), symKey.getAlgorithm()));

		return cipher.doFinal(b, offset, len);
	}

	public boolean decrypt(File inF, File outF) throws Exception {
		try {
			int bufSize = 8192;
			byte[] buf = new byte[bufSize];
			int k = 0;

			// streams
			FileInputStream fis = new FileInputStream(inF);
			FileOutputStream fos = new FileOutputStream(outF);

			// read key len
			if ((k = fis.read(buf, 0, 2)) == 2) {
				ByteBuffer bb = ByteBuffer.allocate(2);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				bb.put(buf[0]);
				bb.put(buf[1]);
				short symKeyLen = bb.getShort(0);

				// read key
				byte[] encSymKeyB = new byte[symKeyLen];
				int left = symKeyLen;
				while (left > 0) {
					k = fis.read(buf, 0, symKeyLen);
					System.arraycopy(buf, 0, encSymKeyB, symKeyLen - left, left);
					left -= k;
				}
				// decrypt sym key from bytes
				SecretKey symKey = SymKeyUtil.decryptKey(encSymKeyB, privKey);
				Cipher cipher = Cipher.getInstance(symKey.getAlgorithm());
				cipher.init(Cipher.DECRYPT_MODE, symKey);

				CipherInputStream cis = new CipherInputStream(fis, cipher);

				// decrypt and write
				while ((k = cis.read(buf, 0, bufSize)) != -1) {
					fos.write(buf, 0, k);
				}

				cis.close();
				fos.close();
				fis.close();
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}
