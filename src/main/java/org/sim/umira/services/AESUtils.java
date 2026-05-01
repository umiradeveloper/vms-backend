package org.sim.umira.services;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    private static final String SECRET = "k7mX2qPvR9nZsL4w"; // 16 char key

    public static String encrypt(String strToEncrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // return Base64.getEncoder()
            //         .encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(cipher.doFinal(strToEncrypt.getBytes()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new String(cipher.doFinal(
            Base64.getUrlDecoder().decode(strToDecrypt)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
