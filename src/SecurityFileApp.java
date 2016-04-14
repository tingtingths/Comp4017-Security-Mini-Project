import FileSystem.FileManager;
import KeySystem.KeyManager;

import java.util.Scanner;

/**
 * Created by xupengfei on 6/4/2016.
 */
public class SecurityFileApp {
    boolean running = true;
    Scanner scanner = new Scanner(System.in);
    String userInput = "";
    FileManager fileManager;
    KeyManager keyManager;


    public SecurityFileApp() {
        fileManager = new FileManager();
        keyManager = new KeyManager();
    }

    private void initSystem() {
        if (keyManager.initKeyStore()) {
            //TODO load the keyStore to memory
        }else {
            System.out.println("/**** Your are new to the system, init the system first ****/");
            if (!keyManager.isSystemInit()) {
                System.out.println("Generating the first key... Please input the key description");
                String firstDesc = scanner.nextLine();
                keyManager.createKeyStore(firstDesc);
                System.out.println("Please input your new password:");
                userInput = scanner.nextLine();
                keyManager.setPassword(userInput);
            }
        }
    }

    private boolean checkPassword() {
        System.out.println("Please input your password: ");
        userInput = scanner.nextLine();
        return keyManager.checkPassord(userInput);
    }

    private void encryptFile() {
        System.out.println("Please input the file path:");
        userInput = scanner.nextLine();
        fileManager.encryptFile(userInput);
    }

    private void decryptFile() {
        System.out.println("Please input the file path:");
        userInput = scanner.nextLine();
        fileManager.decryptFile(userInput);
    }

    private void generateAsymmetricKeyPair() {
        System.out.println("Generating keys...");
        keyManager.generatePrivateKeyPair(null);
        System.out.println("Done with generating keys!");
    }

    private void exportKey(boolean isPublic) {
        System.out.println("Please type in the file path where you want to put the file");
        userInput = scanner.nextLine();
        if (isPublic) {
            System.out.println("Exporting public key...");
            keyManager.exportPrivateKey(userInput);
        } else {
            keyManager.exportPrivateKey(userInput);
            System.out.println("Exporting private key...");
        }
        System.out.println("Exporting key to: " + userInput);
    }

    private void importKey(boolean isPublic) {
        System.out.println("Please type in the file path of your key file: ");
        userInput = scanner.nextLine();
        if (isPublic) {
            System.out.println("Importing public key...");
            keyManager.importPublicKey(userInput);
        } else {
            System.out.println("Importing private key...");
            keyManager.importPrivateKey(userInput);
        }
        System.out.println("Importing done!");
    }

    private void changeKeyDescription() {
        System.out.println("What kind of key you'd like to access? ");
        System.out.println("1. Public key ring");
        System.out.println("2. Private key ring");
        userInput = scanner.nextLine();
        if (userInput.equals("1")) {
            String selectIndex = "";
            int keySize = 0;
            do {
                System.out.println("Choose the public key you'd like to change: ");
                String[] keyDescription = keyManager.getPublicKeyDescriptions();
                keySize = keyDescription.length;
                printKeyDescriptions(keyDescription);
                selectIndex = scanner.nextLine();
            } while (Integer.parseInt(selectIndex) > keySize);


            System.out.println("Please type in your new description: ");
            String description = scanner.nextLine();
            keyManager.updatePublicKeyDescription(Integer.parseInt(selectIndex), description);
        } else if (userInput.equals("2")) {
            String selectIndex = "";
            int keySize = 0;
            do {
                System.out.println("Choose the private key you'd like to change: ");
                String[] keyDescription = keyManager.getPrivateKeyDescriptions();
                keySize = keyDescription.length;
                printKeyDescriptions(keyDescription);
                selectIndex = scanner.nextLine();
            } while (Integer.parseInt(selectIndex) > keySize);

            System.out.println("Please type in your new description: ");
            String description = scanner.nextLine();
            keyManager.updatePrivateKeyDescription(Integer.parseInt(selectIndex), description);
        } else {
            System.out.println("Invalid input...");
            return;
        }
        System.out.println("Key description changed! ");
    }

    private void printKeyDescriptions(String[] keyDescriptions) {
        int i = 0;
        for (String keyDescription : keyDescriptions) {
            System.out.println(Integer.toString(i) + ". " + keyDescription);
            i++;
        }
    }

    private void changePassword() {
        System.out.println("Please type in your old password: ");
        if (keyManager.checkPassord(scanner.nextLine())) {
            System.out.println("Please type in your new password: ");
            keyManager.changePassword(scanner.nextLine());
        } else {
            System.out.println("Wrong password!");
        }
    }

    private void mainProcess() {
        while (running) {
            System.out.println("\n\nChoose what you want to do: ");
            System.out.println("1: Encrypt File");
            System.out.println("2: Decrypt File");
            System.out.println("3: Generate Asymmetric Key pair");
            System.out.println("4: Export Public Key");
            System.out.println("5: Export Private Key");
            System.out.println("6: Import Public Key");
            System.out.println("7: Import Private Key");
            System.out.println("8: Change Key description");
            System.out.println("9: Change password");
            System.out.println("0: quit");
            userInput = scanner.nextLine();

            switch (userInput) {
                case "0":
                    running = false;
                    break;
                case "1":
                    encryptFile();
                    break;
                case "2":
                    decryptFile();
                    break;
                case "3":
                    generateAsymmetricKeyPair();
                    break;
                case "4":
                    exportKey(true);
                    break;
                case "5":
                    exportKey(false);
                    break;
                case "6":
                    importKey(true);
                    break;
                case "7":
                    importKey(false);
                    break;
                case "8":
                    changeKeyDescription();
                    break;
                case "9":
                    changePassword();
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SecurityFileApp app = new SecurityFileApp();

        System.out.println("/---------Welcome---------/");
        app.initSystem();
        if (!app.checkPassword()) {
            System.out.println("Wrong password!");
        } else {
            app.mainProcess();
        }
        System.out.println("Bye Bye!");
    }
}
