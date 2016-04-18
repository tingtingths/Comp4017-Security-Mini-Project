package KeySystem;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by xupengfei on 6/4/2016.
 */
public class SecurityKey implements Serializable{

    private PublicKey publicKey;


    private PrivateKey privateKey;
    private String keyDescription;
    private boolean isPublicKey;

    public SecurityKey(PublicKey PublicKey, PrivateKey PrivateKey, String KeyDescription) {
        this.isPublicKey = false;
        this.publicKey = PublicKey;
        this.privateKey = PrivateKey;
        this.keyDescription = KeyDescription;
    }

    public SecurityKey(PublicKey PublicKey, String KeyDescription) {
        this.isPublicKey = true;
        this.publicKey = null;
        this.publicKey = PublicKey;
        this.keyDescription = KeyDescription;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setKeyDescription(String keyDescription) {
        this.keyDescription = keyDescription;
    }

    public String getKeyDescription() {
        return keyDescription;
    }

    public boolean isPublicKey() {
        return isPublicKey;
    }
}
