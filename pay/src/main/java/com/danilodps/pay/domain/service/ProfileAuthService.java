package com.danilodps.pay.domain.service;

import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.pay.domain.model.request.create.SignInRequest;
import com.danilodps.pay.domain.model.request.create.SignUpRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;

public interface ProfileAuthService {

    SignUpResponse register(SignUpRequest signUpRequest);
    JwtResponse authenticate(SignInRequest loginRequest);
}