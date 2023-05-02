package com.github.claudineysilva.crytposerver.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class CryptoServiceRSAOld {

    public static final String ALGORITHM = "RSA";

    /**
     * Local da chave privada no sistema de arquivos.
     */
    public static final String PATH_PUBLIC_KEY = "./keys/private.key";

    /**
     * Local da chave pública no sistema de arquivos.
     */
    public static final String PATH_PRIVATE_KEY = "./keys/public.key";

    /**
     * Gera a chave que contém um par de chave Privada e Pública usando 2048 bytes.
     * Armazena o conjunto de chaves nos arquivos private.key e public.key
     */
    @SneakyThrows
    public static void generateKey() {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        final KeyPair key = keyGen.generateKeyPair();

        File privateKeyFile = new File(PATH_PUBLIC_KEY);
        File publicKeyFile = new File(PATH_PRIVATE_KEY);

        // Cria os arquivos para armazenar a chave Privada e a chave Publica
        if (privateKeyFile.getParentFile() != null) {
            privateKeyFile.getParentFile().mkdirs();
        }
        privateKeyFile.createNewFile();

        if (publicKeyFile.getParentFile() != null) {
            publicKeyFile.getParentFile().mkdirs();
        }
        publicKeyFile.createNewFile();

        // Salva a Chave Pública no arquivo
        ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                new FileOutputStream(publicKeyFile));
        publicKeyOS.writeObject(key.getPublic());
        publicKeyOS.close();

        // Salva a Chave Privada no arquivo
        ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                new FileOutputStream(privateKeyFile));
        privateKeyOS.writeObject(key.getPrivate());
        privateKeyOS.close();
    }

    /**
     * Verifica se o par de chaves Pública e Privada já foram geradas.
     */
    public static boolean hasKeyFiles() {

        File privateKeyFile = new File(PATH_PUBLIC_KEY);
        File publicKeyFile = new File(PATH_PRIVATE_KEY);

        return privateKeyFile.exists() && publicKeyFile.exists();
    }

    /**
     * Criptografa o texto puro usando chave pública.
     */
    @SneakyThrows
    public static byte[] encrypt(String text, PublicKey publicKey) {
        byte[] cipherText = null;

        final Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Criptografa o texto puro usando a chave Púlica
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipherText = cipher.doFinal(text.getBytes());

        return cipherText;
    }

    /**
     * Decriptografa o texto puro usando chave privada.
     */
    @SneakyThrows
    public static String decrypt(byte[] text, PrivateKey privateKey) {
        byte[] dectyptedText = null;

        final Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Decriptografa o texto puro usando a chave Privada
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        dectyptedText = cipher.doFinal(text);

        return new String(dectyptedText);
    }

    /**
     * Testa o Algoritmo
     */
    public static void main(String[] args) {

        try {

            // Verifica se já existe um par de chaves, caso contrário gera-se as chaves..
            if (!hasKeyFiles()) {
                // Método responsável por gerar um par de chaves usando o algoritmo RSA e
                // armazena as chaves nos seus respectivos arquivos.
                generateKey();
            }

            final String msgOriginal = "Text do encrypt";
            ObjectInputStream inputStream = null;

            // Criptografa a Mensagem usando a Chave Pública
            inputStream = new ObjectInputStream(new FileInputStream(PATH_PRIVATE_KEY));
            final PublicKey chavePublica = (PublicKey) inputStream.readObject();
            final byte[] textoCriptografado = encrypt(msgOriginal, chavePublica);

            // Decriptografa a Mensagem usando a Chave Pirvada
            inputStream = new ObjectInputStream(new FileInputStream(PATH_PUBLIC_KEY));
            final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
            final String textoPuro = decrypt(textoCriptografado, chavePrivada);

            // Imprime o texto original, o texto criptografado e
            // o texto descriptografado.
            System.out.println("Mensagem Original: " + msgOriginal);
            System.out.println("Mensagem Criptografada: " +textoCriptografado.toString());
            System.out.println("Mensagem Decriptografada: " + textoPuro);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
