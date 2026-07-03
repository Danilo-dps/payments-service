package com.danilodps.pay.adapters.inbound.controller;

import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignInRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignUpRequest;
import com.danilodps.pay.adapters.inbound.controller.response.JwtResponse;
import com.danilodps.pay.application.usecases.ProfileAuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController {

    private final ProfileAuthUseCase profileAuthUseCase;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody SignInRequest signInRequest){
        JwtResponse jwtResponse = profileAuthUseCase.authenticate(signInRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signupUser(@RequestBody SignUpRequest signUpRequest){
        SignUpResponse registeredUser = profileAuthUseCase.register(signUpRequest);
        return ResponseEntity.ok(registeredUser);
    }

}
