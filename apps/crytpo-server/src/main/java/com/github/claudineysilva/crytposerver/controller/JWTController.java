package com.github.claudineysilva.crytposerver.controller;

import com.github.claudineysilva.crytposerver.service.JWTService;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("jwt")
@RequiredArgsConstructor
public class JWTController {

    private final JWTService jwtService;

    @PostMapping("create")
    public Mono<String> createJwt(@RequestBody String username) {
        return Mono.just(jwtService.createJwt(username));
    }

    @SneakyThrows
    @PostMapping("validate")
    public Mono<String> validateJwt(@RequestBody String token) {
        SignedJWT signedJWT = jwtService.validateJwt(token);
        return Mono.just(signedJWT.getJWTClaimsSet().getClaim("name").toString());
    }
}
