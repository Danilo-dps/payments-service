package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.model.request.LoginRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;

public interface AuthService<R, S> {

    JwtResponse authenticate(LoginRequest loginRequest);
    R register(S signupRequest);
}