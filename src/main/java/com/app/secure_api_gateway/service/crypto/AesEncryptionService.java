package com.app.secure_api_gateway.service.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AesEncryptionService {

    private static final String ALGO = "AES/CBC/PKCS5Padding";

    @Value("${crypto.aes.key}")
    private String aesKey; // 32 bytes for AES-256

    @Value("${crypto.aes.key}")
    private String aesIv; // 16 bytes for IV

    //ENCRYPT WITH FIXED IV
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES"),
                new IvParameterSpec(aesIv.getBytes(StandardCharsets.UTF_8))
        );

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    //DECRYPT
    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES"),
                new IvParameterSpec(aesIv.getBytes(StandardCharsets.UTF_8))
        );

        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}