package KeySystem;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.spec.KeySpec;

/**
 * Created by xupengfei on 6/4/2016.
 */
public class KeyUtils {

    public enum SymKeyAlgo {
        AES, DES, DESede;
    }

    public static void iterateKeyRing() {

    }

    public static KeyPair generateRSAKeyPair(int size) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(size);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No way of doing this T.T");
        }
        return null;
    }

    public static SecretKey generateSymKey(SymKeyAlgo algo, int size) {
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

    public static CBCWrapper encryptPBE(SecretKey symKey, byte[] b) {
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

     public static byte[] decryptPBE(SecretKey symKey, CBCWrapper wrapper) {
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

    public static byte[] encrypt(SecretKey symKey, InputStream is) throws Exception {
        return new byte[0];
    }

    public static byte[] encrypt(SecretKey symKey, byte[] b) throws Exception {
        Cipher cipher;
        String symAlgo = symKey.getAlgorithm();
        cipher = Cipher.getInstance(symAlgo);
        cipher.init(Cipher.ENCRYPT_MODE, symKey);

        return cipher.doFinal(b);
    }

    public static byte[] decrypt(SecretKey symKey, byte[] b) throws Exception {
        Cipher cipher;
        String symAlgo = symKey.getAlgorithm();
        cipher = Cipher.getInstance(symAlgo);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(symKey.getEncoded(), symKey.getAlgorithm()));

        return cipher.doFinal(b);
    }

    public static byte[] encryptSmall(Key asmKey, byte[] b) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, asmKey);
        return cipher.doFinal(b);
    }

    public static byte[] decryptSmall(Key asmKey, byte[] b) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, asmKey);
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