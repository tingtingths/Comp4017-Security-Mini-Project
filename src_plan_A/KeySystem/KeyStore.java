package KeySystem;

import java.io.Serializable;
import java.security.KeyPair;
import java.util.LinkedList;

/**
 * Created by xupengfei on 13/4/2016.
 */
public class KeyStore implements Serializable {
    public final static String KEY_STORE_PATH = "./.keyStore.ser";
    public LinkedList<SecurityKey> privateKeyRing;
    public LinkedList<SecurityKey> publicKeyRing;

    public void createKeyStore(String keyDesc) {
        privateKeyRing = new LinkedList<>();
        publicKeyRing = new LinkedList<>();
        KeyPair keyPair = KeyUtils.generateRSAKeyPair(2048);
        addPrivateKeyToRing(new SecurityKey(keyPair.getPublic(),keyPair.getPrivate(),keyDesc));
    }

    public boolean addPublicKeyToRing(SecurityKey PublicKey) {
        if (!PublicKey.isPublicKey()) return false;
        this.publicKeyRing.add(PublicKey);
        return true;
    }

    public boolean addPrivateKeyToRing(SecurityKey Private) {
        if (Private.isPublicKey()) return false;
        this.privateKeyRing.add(Private);
        return true;
    }

}
