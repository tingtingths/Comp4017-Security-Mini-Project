package FileSystem;

import KeySystem.KeyUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

/**
 * Created by xupengfei on 6/4/2016.
 */
public class FileManager {
    public FileManager() {
    }

    public void encryptFile(String filePath, Key asmKey) {
        try {
            SecretKey symKey = KeySystem.KeyUtils.generateSymKey(KeySystem.KeyUtils.SymKeyAlgo.AES, 128);
            byte[] symKeyB = symKey.getEncoded();
            byte[] encSymKeyB = KeyUtils.encryptSmall(asmKey, symKeyB);
            int symKeySize = encSymKeyB.length;

            Cipher cipher = Cipher.getInstance(symKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, symKey);

            File inF = new File(filePath);
            FileInputStream fis = new FileInputStream(inF);
            File outF = new File(filePath + ".enc");
            FileOutputStream fos = new FileOutputStream(outF);

            // write metadata
            fos.write(symKeySize);
            fos.write(encSymKeyB);
            // write payload
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decryptFile(String filePath) {

    }

}
