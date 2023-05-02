package com.github.claudineysilva.crytposerver.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;
import java.util.Base64;

@Service
public class CryptoAESService {

    public static final String ALGORITHM = "AES";


    @Value("${crypto.aes.key}")
    private String AESKey;

    @Value("${crypto.aes.iv}")
    private String AESIV;

    @SneakyThrows
    public String encrypt(String text) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(AESKey.getBytes("UTF-8"), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(AESIV.getBytes("UTF-8")));
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF-8")));
    }

    @SneakyThrows
    public String decrypt(String text) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(AESKey.getBytes("UTF-8"), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(AESIV.getBytes("UTF-8")));
        return new String(cipher.doFinal(Base64.getDecoder().decode(text.getBytes())),"UTF-8");
    }
}
