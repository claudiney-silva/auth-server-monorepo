package com.github.claudineysilva.otpserver;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

import java.io.FileOutputStream;

@RestController
@RequestMapping("otp")
public class OtpController {

    private static final int[] DIGITS_POWER = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };

    private final static String SEED = seed();

    @GetMapping
    public Mono<String> otp() throws NoSuchAlgorithmException, InvalidKeyException {

        String counter = moviment();

        byte[] keyBytes = hexStr2Bytes(SEED);
        byte[] text = hexStr2Bytes(counter);

        byte[] hash = hash(keyBytes, text);
        String otp = truncateOTP(hash);
        String uri = qrcodeURI();
        qrcodeImage(uri);

        return Mono.just("OTP: %s e a URI: %s".formatted(otp, uri));
    }

    private static String seed() {
        SecureRandom random = new SecureRandom();
        byte[] seed20 = new byte[20];
        random.nextBytes(seed20);
        String seed20HexKey = Hex.encodeHexString(seed20);
        return seed20HexKey;
    }

    private static String moviment() {
        long unixTimestamp = Instant.now().getEpochSecond();
        long sync = 0;
        long period = 30;
        long time = (unixTimestamp - sync) / period;

        String counter = Long.toHexString(time).toUpperCase();
        while (counter.length() < 16)
            counter = "0" + counter;

        return counter;
    }

    private static byte[] hexStr2Bytes(String hex) {
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        byte[] bytes = new byte[bArray.length - 1];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = bArray[i + 1];
        return bytes;
    }

    @SneakyThrows
    private static byte[] hash(byte[] keyBytes, byte[] text) {
        Mac hmac;
        hmac = Mac.getInstance("HmacSHA1");
        SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
        hmac.init(macKey);
        byte[] hash = hmac.doFinal(text);

        System.out.println(new String(hash));

        return hash;
    }

    private static String truncateOTP(byte[] hash) {
        int offset = hash[hash.length - 1] & 0xf;
        int binary = ((hash[offset] & 0x7f) << 24)
                | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8)
                | (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[6];

        var result = Integer.toString(otp);
        while (result.length() < 6)
            result = "0" + result;

        return result;
    }

    private static String qrcodeURI() {
        String accountName = "(teste@account.name)";
        String issuer = "Sample Teste";
        String hashAlgorithm = "SHA1";
        String seedBase32 = new Base32().encodeToString(SEED.getBytes());
        String digits = "6";
        String period = "30";
        StringBuilder path = new StringBuilder();
        path.append("/");
        path.append(issuer);
        path.append(" ");
        path.append(accountName);
        URIBuilder uri = new URIBuilder();
        uri.setScheme("otpauth");
        String method = "totp";
        uri.setHost(method);
        uri.setPath(path.toString());
        uri.setParameter("secret", seedBase32);
        uri.setParameter("algorithm", hashAlgorithm);
        uri.setParameter("digits", digits);
        //if (method.equals("hotp"))
        //    uri.setParameter("counter", counter);
        if (method.equals("totp"))
            uri.setParameter("period", period);
        String dataUri = uri.toString();
        return dataUri;
    }

    @SneakyThrows
    private static void qrcodeImage(String qrCodeData) {
        String filePath = "qrcode.png";
        int height = 340;
        int width = 340;
        BitMatrix matrix = new MultiFormatWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToFile(
                matrix,
                filePath.substring(filePath.lastIndexOf('.') + 1),
                new File(filePath));

    }

}
