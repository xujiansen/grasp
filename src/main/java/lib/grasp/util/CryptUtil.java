package lib.grasp.util;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密
 */
public class CryptUtil {

    private static final String CIPHER_AES = "AES/CBC/PKCS7Padding";

    /**
     * AES 加密
     */
    public static byte[] aesEncrypt(byte[] aesKey, byte[] aesIv, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesIv);
            Cipher cipherAes = Cipher.getInstance(CIPHER_AES, "BC");
            cipherAes.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipherAes.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchProviderException e) {
        } catch (NoSuchPaddingException e) {
        } catch (InvalidKeyException e) {
        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (InvalidAlgorithmParameterException e) {
        }
        return null;
    }

    /**
     * AES 解密
     */
    public static byte[] aesDecrypt(byte[] aesKey, byte[] aesIv, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesIv);
            Cipher cipherAes = Cipher.getInstance(CIPHER_AES, "BC");
            cipherAes.init(Cipher.DECRYPT_MODE, key, iv);
            return cipherAes.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchProviderException e) {
        } catch (NoSuchPaddingException e) {
        } catch (InvalidKeyException e) {
        } catch (BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (InvalidAlgorithmParameterException e) {
        }
        return null;
    }

    // 加密
    public static String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        return new String(Base64.encode(plaintext.getBytes(), Base64.DEFAULT));
    }

    // 解密
    public static String decrypt(String ciphertext) {
        if (ciphertext == null) {
            return null;
        }

        byte[] rt = null;
        try {
            rt = Base64.decode(ciphertext.getBytes(), Base64.DEFAULT);
        } catch (Exception e) {
        }

        if (rt == null) {
            return "";
        }
        return new String(rt);
    }

}
