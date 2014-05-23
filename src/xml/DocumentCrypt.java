package xml;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

/**
 * decryption and encryption
 * Created by gloria_z on 14-5-21.
 */
public class DocumentCrypt {
    private String algorithm = "RSA";      //algorithm
    private int KEY_SIZE = 1024;            //size of key
    private KeyPairGenerator keyPairGen;
    private KeyFactory keyFactory;
    private KeyPair keyPair;
    public final String PRIVATE_KEY = "private_key";
    public final String PUBLIC_KEY = "public_key";

    public DocumentCrypt() {}

    public DocumentCrypt(String algorithm, int size) {
        this.algorithm = algorithm;
        KEY_SIZE = size;
    }

    public void changeAlgorithmAndKeySize(String algorithm, int size) {
        this.algorithm = algorithm;
        KEY_SIZE = size;
    }

    //initialize key factory, key pair, key generator and so on
    public void initialize() {
        try {
            keyPairGen = KeyPairGenerator.getInstance(algorithm);
            keyPairGen.initialize(KEY_SIZE);
            keyFactory = KeyFactory.getInstance(algorithm);
            keyPair = keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //decrypt via base64
    public byte[] decryptBASE64(String key) {
        try {
            return new BASE64Decoder().decodeBuffer(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //encrypt via base64
    public String encryptBASE64(byte[] key) {
        return new BASE64Encoder().encode(key);
    }

    //save key pair to file
    public boolean saveKeys(String fileName) {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String privateKeyString = encryptBASE64(privateKey.getEncoded());
        String publicKeyString = encryptBASE64(publicKey.getEncoded());

        try {
            DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(fileName)));
            out.writeUTF(privateKeyString);
            out.writeUTF(publicKeyString);
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //get key pair from file
    public HashMap<String, String> fetchKey(String fileName) {
        try {
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(fileName)));
            String privateKeyString = in.readUTF();
            String publicKeyString = in.readUTF();
            HashMap<String, String> map = new HashMap<String, String>(2);
            map.put(PUBLIC_KEY, publicKeyString);
            map.put(PRIVATE_KEY, privateKeyString);
            return map;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encrypt(byte[] data) {
        PublicKey key = keyPair.getPublic();
        return doFinal(data, Cipher.ENCRYPT_MODE, key);
    }

    public byte[] encrypt(String publicKey, byte[] data) {
        try {
            byte[] keyBytes = decryptBASE64(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            PublicKey key = keyFactory.generatePublic(keySpec);
            return doFinal(data, Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] data) {
        PrivateKey key = keyPair.getPrivate();
        return doFinal(data, Cipher.DECRYPT_MODE, key);
    }

    public byte[] decrypt(String privateKey, byte[] data) {
        try {
            byte[] keyBytes = decryptBASE64(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey key = keyFactory.generatePrivate(keySpec);
            return doFinal(data, Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Break Encryption or Decryption
    private byte[] doFinal(byte[] data, int mode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(mode, key);
            int SIZE_FOR_EACH;
            if (mode == Cipher.ENCRYPT_MODE)
                SIZE_FOR_EACH = KEY_SIZE / 8 - 11;
            else
                SIZE_FOR_EACH = KEY_SIZE / 8;

            int blocks = data.length / SIZE_FOR_EACH;
            int LAST_BLOCK_SIZE = data.length % SIZE_FOR_EACH;
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            for (int i = 0; i < blocks; i++) {
                out.write(
                        cipher.doFinal(data, i * SIZE_FOR_EACH, SIZE_FOR_EACH));
            }
            if (LAST_BLOCK_SIZE != 0)
                out.write(cipher.doFinal(data, blocks * SIZE_FOR_EACH,
                        LAST_BLOCK_SIZE));
            out.close();
            return out.toByteArray();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}