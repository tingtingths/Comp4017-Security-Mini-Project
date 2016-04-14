package KeySystem;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.KeySpec;

/**
 * Created by xupengfei on 6/4/2016.
 */
class KeyUtils {

    public enum SymKeyAlgo {
        AES, DES, DESede;
    }

    static void iterateKeyRing() {

    }

    static KeyPair generateRSAKeyPair(int size) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(size);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No way of doing this T.T");
        }
        return null;
    }

    static SecretKey generateSymKey(SymKeyAlgo algo, int size) {
        try {
            String strAlgo = "";
            if (algo == SymKeyAlgo.AES) strAlgo = "AES";
            if (algo == SymKeyAlgo.DES) strAlgo = "DES";
            if (algo == SymKeyAlgo.DESede) strAlgo = "DESede";
            KeyGenerator kgen = KeyGenerator.getInstance(strAlgo);
            kgen.init(size);

            return kgen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static SecretKey generatePBESymKey(char[] passphrase, byte[] salt) {
        try {
            String pbeAlgo = "PBKDF2WithHmacSHA256";
            SecretKeyFactory factory = SecretKeyFactory.getInstance(pbeAlgo);
            KeySpec spec = new PBEKeySpec(passphrase, salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static CBCWrapper encryptPBE(SecretKey symKey, byte[] b) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, symKey);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] encByte = cipher.doFinal(b);
            return new CBCWrapper(iv, encByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static byte[] decryptPBE(SecretKey symKey, CBCWrapper wrapper) {
        try {
            byte[] iv = wrapper.getIv();
            byte[] b = wrapper.getData();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, symKey, new IvParameterSpec(iv));
            return cipher.doFinal(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static byte[] encrypt(SecretKey symKey, byte[] b) throws Exception {
        Cipher cipher;
        String symAlgo = symKey.getAlgorithm();
        cipher = Cipher.getInstance(symAlgo);
        cipher.init(Cipher.ENCRYPT_MODE, symKey);

        return cipher.doFinal(b);
    }

    static byte[] decrypt(SecretKey symKey, byte[] b) throws Exception {
        Cipher cipher;
        String symAlgo = symKey.getAlgorithm();
        cipher = Cipher.getInstance(symAlgo);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(symKey.getEncoded(), symKey.getAlgorithm()));

        return cipher.doFinal(b);
    }
}

class CBCWrapper {
    private byte[] iv;
    private byte[] data;

    CBCWrapper(byte[] iv, byte[] data) {
        this.iv = iv;
        this.data = data;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getData() {
        return data;
    }
}