package KeySystem;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xupengfei on 6/4/2016.
 */
class KeyUtils {

    static void iterateKeyRing() {

    }

    static KeyPair generateRSAKeyPair(int size)
    {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(size);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No way of doing this T.T");
        }
        return null;
    }

    static SecretKey generateSymKey(String algo, int size) {
        String strAlgo = "";
        if (algo == SymKeyAlgo.AES) strAlgo = "AES";
        if (algo == SymKeyAlgo.DES) strAlgo = "DES";
        if (algo == SymKeyAlgo.DESede) strAlgo = "DESede";
        KeyGenerator kgen = KeyGenerator.getInstance(strAlgo);
        kgen.init(size);
        return kgen.generateKey();
    }
}
