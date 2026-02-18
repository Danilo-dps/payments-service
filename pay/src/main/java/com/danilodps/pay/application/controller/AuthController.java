package com.danilodps.pay.application.controller;

import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.pay.domain.model.request.create.SignInRequest;
import com.danilodps.pay.domain.model.request.create.SignUpRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;
import com.danilodps.pay.domain.service.ProfileAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ProfileAuthService profileAuthService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody SignInRequest signInRequest){
        JwtResponse jwtResponse = profileAuthService.authenticate(signInRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signupUser(@RequestBody SignUpRequest signUpRequest){
        SignUpResponse registeredUser = profileAuthService.register(signUpRequest);
        return ResponseEntity.ok(registeredUser);
    }

}
