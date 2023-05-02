package com.github.claudineysilva.crytposerver.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class CryptoSHA256Service {

    @Value("${crypto.sha.salt")
    private String SHASalt;

    @SneakyThrows
    public String hash(String text) {
        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        algorithm.update(SHASalt.getBytes());
        byte messageDigest[] = algorithm.digest(text.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            hexString.append(String.format("%02X", 0xFF & b));
        }
        return hexString.toString();
    }

}
