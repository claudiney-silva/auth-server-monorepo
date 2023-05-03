package com.github.claudineysilva.crytposerver.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    private final String privateKey = "qxBEEQv7E8aviX1KUcdOiF5ve5COUPAr";

    @SneakyThrows
    public String createJwt(String username) {
        SignedJWT signedJWT = createSignedJWT(username);
        System.out.println(signedJWT.serialize());

        String encryptedToken = encryptToken(signedJWT);
        return encryptedToken;
    }

    @SneakyThrows
    private SignedJWT createSignedJWT(String username) {
        JWTClaimsSet jwtClaimsSet = createJWTClainSet(username);
        KeyPair keyPair = generateKeyPair();

        JWK jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).keyID(UUID.randomUUID().toString()).build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .jwk(jwk) // informando a chave pública, que será usada para validar o token assinado no client
                .type(JOSEObjectType.JWT)
                .customParam("custom", "teste value")
                .build(), jwtClaimsSet);

        // Assinando o token
        RSASSASigner signer = new RSASSASigner(keyPair.getPrivate());
        signedJWT.sign(signer);
        return signedJWT;
    }

    private JWTClaimsSet createJWTClainSet(String username) {
        return new JWTClaimsSet.Builder()
                .subject(username)
                .claim("name", "Teste "+username)
                .claim("email", "Fulano@email")
                .claim("authorities", "SCOPE_profile")
                .issuer("https://127.0.0.1:9000")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + (3600 * 1000)))
                .build();
    }

    @SneakyThrows
    private KeyPair generateKeyPair() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.genKeyPair();
    }

    private String encryptToken(SignedJWT signedJWT) throws JOSEException {

        // gerando token criptografado
        DirectEncrypter directEncrypter = new DirectEncrypter(privateKey.getBytes());

        JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT")
                .build(), new Payload(signedJWT));

        jweObject.encrypt(directEncrypter);

        return jweObject.serialize();

    }

    @SneakyThrows
    public SignedJWT validateJwt(String encryptedToken) {
        String decryptedToken = decryptToken(encryptedToken);
        SignedJWT signedJWT = validateTokenSignature(decryptedToken);
        return signedJWT;
    }

    private String decryptToken(String encryptedToken) throws ParseException, JOSEException {
        JWEObject jweObject = JWEObject.parse(encryptedToken);
        DirectDecrypter directDecrypter = new DirectDecrypter(privateKey.getBytes());
        jweObject.decrypt(directDecrypter);
        return  jweObject.getPayload().toSignedJWT().serialize();
    }

    @SneakyThrows
    private SignedJWT validateTokenSignature(String signedToken) {
        SignedJWT signedJWT = SignedJWT.parse(signedToken);
        RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());

        if (!signedJWT.verify(new RSASSAVerifier(publicKey)))
            throw new AccessDeniedException("Token inválido");

        return signedJWT;
    }
}
