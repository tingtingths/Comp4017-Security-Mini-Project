package KeySystem;

import com.sun.istack.internal.Nullable;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;

/**
 * Created by xupengfei on 6/4/2016.
 */
public class KeyManager {

    public LinkedList<SecurityKey> privateKeyRing = new LinkedList<>();
    public LinkedList<SecurityKey> publicKeyRing = new LinkedList<>();


    public KeyManager() {

    }

    public boolean isSystemInit() {
        return false;
    }

    public void initKeyStore() {

    }

    public boolean checkPassord(String passord) {
        return true;
    }

    public void setPassword(String password) {

    }

    public String[] getPublicKeyDescriptions() {
        String[] debugStr = {"sff", "gee", "qqq"};
        return debugStr;
    }

    public String[] getPrivateKeyDescriptions() {
        String[] debugStr = {"bbb", "www", "lll"};
        return debugStr;
    }

    public void updatePublicKeyDescription(int index, String desc) {

    }

    public void updatePrivateKeyDescription(int index, String desc) {

    }

    public SecurityKey generatePrivateKeyPair(@Nullable SecurityKey PrivateKey) {
        if (PrivateKey == null) {
            //TODO create a new pair of key
        } else {
            if (PrivateKey.isPublicKey()) return null;
            PrivateKey privateKey = PrivateKey.getPrivateKey();
            //TODO generate public key from privateKey
        }

        return null; //TODO return a SecurityKey instance with is a private Key and public key is store in this instance
    }

    public void readInKeyStore(String filePath) {

    }

    public void exportPublicKey(String filePath) {

    }

    public void exportPrivateKey(String filePath) {

    }

    public void importPublicKey(String filePath) {

    }

    public void importPrivateKey(String filePath) {

    }

    public void removePublicKey() {

    }

    public void removePrivateKey() {

    }

    public boolean addPublicKeyToRing(SecurityKey PublicKey) {
        if (!PublicKey.isPublicKey()) return false;

        return true;
    }

    public boolean addPrivateKeyToRing(SecurityKey Private) {
        if (Private.isPublicKey()) return false;

        return true;
    }


    public void changePassword(String newPassword) {

    }

    public void changeKeyDescription() {

    }

}
