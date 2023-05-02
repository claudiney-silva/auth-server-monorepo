package com.github.claudineysilva.crytposerver.service;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.util.Base64;


@Service
public class CryptoRSAService {

    public static final String ALGORITHM = "RSA";

    @Value("${crypto.rsa.publicKey}")
    private String publicKeyString;

    @Value("${crypto.rsa.privateKey}")
    private String privateKeyString;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    @SneakyThrows
    private void init() {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(publicKeyString));
        publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(privateKeyString));
        privateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    @SneakyThrows
    public String encrypt(String text) {
        byte[] cipherText = null;

        final Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Criptografa o texto puro usando a chave Púlica
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipherText = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }


    @SneakyThrows
    public String decrypt(String text) {
        byte[] dectyptedText = null;

        final Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Decriptografa o texto puro usando a chave Privada
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        dectyptedText = cipher.doFinal(Base64.getDecoder().decode(text));

        return new String(dectyptedText);
    }
}
