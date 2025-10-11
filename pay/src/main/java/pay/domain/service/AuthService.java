package pay.domain.service;

import pay.domain.model.request.LoginRequest;
import pay.domain.model.response.JwtResponse;

public interface AuthService<R, S> {

    JwtResponse authenticate(LoginRequest loginRequest);
    R register(S signupRequest);
}