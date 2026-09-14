package com.danilodps.pay.application.usecases;

import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignInRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignUpRequest;
import com.danilodps.pay.adapters.inbound.controller.response.JwtResponse;

public interface ProfileAuthUseCase {

    SignUpResponse register(SignUpRequest signUpRequest);
    JwtResponse authenticate(SignInRequest loginRequest);
}