import helper.SymmetricDecrypter;
import helper.SymmetricEncrypter;
import keyman.KeyStoreManager;
import keyman.KeyWrapper;
import keyman.SymKeyUtil;

import javax.crypto.SecretKey;
import java.io.File;

/**
 * Created by ting on 16年2月15日.
 */
public class Main {

	public Main() {
		File keyStoreFile = new File("/home/ting/keystore");
		String passwd = "123456789";

		try {
			KeyStoreManager ksm = new KeyStoreManager(keyStoreFile, passwd);
			KeyWrapper wrapper = ksm.createKey("", 1024);
			byte[] b = "Testing string".getBytes("UTF-8");

			// sym key test
			SecretKey symKey = SymKeyUtil.gen(SymKeyUtil.SymKeyAlgo.AES, 112);

			// encrypt file
			File oriF = new File("/home/ting/orifile");
			File encF = new File("/home/ting/encfile");
			File outF = new File("/home/ting/decfile");

			SymmetricEncrypter encrypter = new SymmetricEncrypter(wrapper.getPublicKey(), symKey);
			SymmetricDecrypter decrypter = new SymmetricDecrypter(wrapper.getPrivateKey());

			encrypter.encrypt(oriF, encF);
			System.out.println("encrypted");
			decrypter.decrypt(encF, outF);
			System.out.println("decrypted");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
