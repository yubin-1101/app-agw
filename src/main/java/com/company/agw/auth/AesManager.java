package com.company.agw.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class AesManager {

    private static final String SECRETKEY_16 = "71F0A7FCA12DBFFC";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public String decrypt(String userID) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRETKEY_16.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec iv = new IvParameterSpec(SECRETKEY_16.substring(0, 16).getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] encryptedData = Base64.getDecoder().decode(userID);
            return new String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt userID", e);
        }
    }

    public String decryptWithKey(String encryptedValue, String key) {
        try {
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(encodedKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedData = Base64.getDecoder().decode(encryptedValue);
            return new String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt value", e);
        }
    }
}
