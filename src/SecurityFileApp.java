import helper.SymmetricDecrypter;
import helper.SymmetricEncrypter;
import keyman.KeyStoreManager;
import keyman.KeyWrapper;
import keyman.SymKeyUtil;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;

/**
 * Created by ting on 16年4月18日.
 */
public class SecurityFileApp {

    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            SecurityFileApp app = new SecurityFileApp();
            KeyStoreManager ksm = app.prepare();
            app.run(ksm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SecurityFileApp() {

    }

    public KeyStoreManager prepare() {
        try {
            System.out.println("Path for keystore file (will create one if keystore not exist): ");
            String path = scanner.nextLine();
            File keystoreF = new File(path);

            System.out.println("Please input the passphrase for this keystore: ");
            String passphrase = scanner.nextLine();
            return new KeyStoreManager(keystoreF, passphrase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printKeyPairs(KeyStoreManager ksm) {
        for (int i = 0; i < ksm.getKeys().length; i++) {
            System.out.println((i + 1) + "> " + ksm.getKeys()[i].getComment());
        }
    }

    public void run(KeyStoreManager ksm) throws Exception {
        boolean running = true;
        String userInput;
        SymmetricEncrypter symEnc;
        SymmetricDecrypter symDec;
        KeyWrapper keyPair = null;

        if (ksm.getKeys().length == 0) {
            System.out.println("The Keystore does not contain any RSA key pair...");
            System.out.println("Generating one...");
            System.out.println("Key comment: ");
            String comment = scanner.nextLine();
            System.out.println("Key size (1024, 2048, 4096 ...etc.)");
            int size = Integer.valueOf(scanner.nextLine());
            keyPair = ksm.createKey(comment, size);
        } if (ksm.getKeys().length == 1) {
          keyPair = ksm.getKeys()[0];
        } else {
            printKeyPairs(ksm);
            System.out.println("key number:");
            int keyNum = Integer.valueOf(scanner.nextLine());
            keyPair = ksm.getKeys()[keyNum-1];
        }

        while (running) {
            System.out.println("Active key pair - " + keyPair.getComment());
            System.out.println("Choose what you want to do: ");
            System.out.println("1: Encrypt File");
            System.out.println("2: Decrypt File");
            System.out.println("3: Generate Asymmetric Key pair");
            System.out.println("4: Export Public Key");
            System.out.println("5: Export Private Key");
            System.out.println("6: Import Public Key");
            System.out.println("7: Import Private Key");
            System.out.println("8: Change Key description");
            //System.out.println("9: Change password");
            System.out.println("9: Set active Key pair:");
            System.out.println("0: quit");
            userInput = scanner.nextLine();

            String inPath;
            String outPath;
            PrivateKey tmpPriv = null;
            PublicKey tmpPub = null;
            KeyWrapper tmpKey;

            switch (userInput) {
                case "0":
                    running = false;
                    break;
                case "1":
                    System.out.println("File to be encrypted: ");
                    inPath = scanner.nextLine();
                    System.out.println("File to be outputed: ");
                    outPath = scanner.nextLine();

                    SecretKey symKey = null;
                    boolean valid = false;
                    while (!valid) {
                        valid = true;
                        System.out.println("Algorithm, 1>AES, 2>DES, 3>3DES");
                        int algo = Integer.valueOf(scanner.nextLine());
                        if (algo == 1) symKey = SymKeyUtil.gen(SymKeyUtil.SymKeyAlgo.AES, 128);
                        else if (algo == 2) symKey = SymKeyUtil.gen(SymKeyUtil.SymKeyAlgo.DES, 112);
                        else if (algo == 3) symKey = SymKeyUtil.gen(SymKeyUtil.SymKeyAlgo.DESede, 112);
                        else valid = false;
                    }

                    symEnc = new SymmetricEncrypter(keyPair.getPublicKey(), symKey);
                    symEnc.encrypt(new File(inPath), new File(outPath));
                    break;
                case "2":
                    System.out.println("File to be decrypted: ");
                    inPath = scanner.nextLine();
                    System.out.println("File to be outputed: ");
                    outPath = scanner.nextLine();

                    if (keyPair.getPrivateKey() != null) {
                        symDec = new SymmetricDecrypter(keyPair.getPrivateKey());
                        symDec.decrypt(new File(inPath), new File(outPath));
                    } else {
                        System.out.println("No private key found in active keypair...");
                    }
                    break;
                case "3":
                    System.out.println("Key comment: ");
                    String comment = scanner.nextLine();
                    System.out.println("Key size (1024, 2048, 4096 ...etc.)");
                    int size = Integer.valueOf(scanner.nextLine());
                    ksm.createKey(comment, size);
                    break;
                case "4":
                    System.out.println("Export path:");
                    outPath = scanner.nextLine();
                    ksm.writeKey(new File(outPath), keyPair, KeyStoreManager.KeyType.PUBLIC);
                    break;
                case "5":
                    try {
                        keyPair.getPrivateKey();
                        System.out.println("Export path:");
                        outPath = scanner.nextLine();
                        ksm.writeKey(new File(outPath), keyPair, KeyStoreManager.KeyType.PRIVATE);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "6":
                    System.out.println("Import path:");
                    inPath = scanner.nextLine();
                    ksm.importKey(new File(inPath));
                    break;
                case "7":
                    System.out.println("Import path:");
                    inPath = scanner.nextLine();
                    System.out.println("Passphrase:");
                    String passphrase = scanner.nextLine();
                    ksm.importKey(new File(inPath), passphrase);
                    break;
                case "8":
                    System.out.println("Current comment: " + keyPair.getComment());
                    System.out.println("New comment: ");
                    String newComment = scanner.nextLine();

                    tmpPriv = null;
                    tmpPub = null;
                    try {
                        tmpPriv = keyPair.getPrivateKey();
                        tmpPub = keyPair.getPublicKey();
                    } catch (Exception e) {}
                    tmpKey = new KeyWrapper(newComment, keyPair.getPassphrase(), tmpPriv, tmpPub);
                    ksm.removeKey(keyPair);
                    ksm.storeKey(tmpKey, KeyStoreManager.KeyType.PRIVATE);
                    keyPair = tmpKey;
                    break;
                /*
                case "9":
                    System.out.println("Old passphrase:");
                    if (scanner.nextLine().equals(keyPair.getPassphrase())) {
                        System.out.println("New passphrase: ");
                        String newPass = scanner.nextLine();

                        for (KeyWrapper k : ksm.getKeys()) {
                            tmpPriv = null;
                            tmpPub = null;
                            try {
                                tmpPriv = k.getPrivateKey();
                                tmpPub = k.getPublicKey();
                            } catch (Exception e) {
                            }
                            tmpKey = new KeyWrapper(k.getComment(), newPass, tmpPriv, tmpPub);
                            ksm.removeKey(k);
                            ksm.storeKey(tmpKey, KeyStoreManager.KeyType.PRIVATE);
                        }
                    } else {
                        System.out.println("Passphrase mismatch");
                    }
                    break;
                */
                case "9":
                    printKeyPairs(ksm);
                    System.out.println("key number:");
                    int keyNum = Integer.valueOf(scanner.nextLine());
                    keyPair = ksm.getKeys()[keyNum-1];
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
    }
}
