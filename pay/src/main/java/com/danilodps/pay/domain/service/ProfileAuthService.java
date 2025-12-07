package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.model.request.create.SignInRequest;
import com.danilodps.pay.domain.model.request.create.SignUpRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;
import com.danilodps.pay.domain.model.response.SignUpResponse;

public interface ProfileAuthService {

    SignUpResponse register(SignUpRequest signUpRequest);
    JwtResponse authenticate(SignInRequest loginRequest);
}