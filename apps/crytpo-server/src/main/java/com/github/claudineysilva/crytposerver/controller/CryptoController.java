package com.github.claudineysilva.crytposerver.controller;

import com.github.claudineysilva.crytposerver.model.CryptoRequest;
import com.github.claudineysilva.crytposerver.service.CryptoAESService;
import com.github.claudineysilva.crytposerver.service.CryptoRSAService;
import com.github.claudineysilva.crytposerver.service.CryptoSHA256Service;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("crypto")
@AllArgsConstructor
public class CryptoController {

    private final CryptoRSAService cryptoRSAService;
    private final CryptoAESService cryptoAESService;
    private final CryptoSHA256Service cryptoSHA256Service;

    @PostMapping("rsa/encrypt")
    public Mono<String> rsaEncrypt(@RequestBody CryptoRequest cryptoRequest) {
        return Mono.just(cryptoRSAService.encrypt(cryptoRequest.getText()));
    }

    @PostMapping("rsa/decrypt")
    public Mono<String> rsaDecrypt(@RequestBody CryptoRequest cryptoRequest) {
        return Mono.just(cryptoRSAService.decrypt(cryptoRequest.getText()));
    }

    @PostMapping("aes/encrypt")
    public Mono<String> aesEncrypt(@RequestBody CryptoRequest cryptoRequest) {
        return Mono.just(cryptoAESService.encrypt(cryptoRequest.getText()));
    }

    @PostMapping("aes/decrypt")
    public Mono<String> aesDecrypt(@RequestBody CryptoRequest cryptoRequest) {
        return Mono.just(cryptoAESService.decrypt(cryptoRequest.getText()));
    }

    @PostMapping("sha/hash")
    public Mono<String> shaHash(@RequestBody CryptoRequest cryptoRequest) {
        return Mono.just(cryptoSHA256Service.hash(cryptoRequest.getText()));
    }
}
