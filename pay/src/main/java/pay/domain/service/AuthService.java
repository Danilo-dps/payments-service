package pay.domain.service;

import pay.domain.dto.UserDTO;
import pay.domain.payload.request.LoginRequest;
import pay.domain.payload.response.JwtResponse;

public interface AuthService<R, S> {

    JwtResponse authenticate(LoginRequest loginRequest);
    R register(S signupRequest);
}

//public interface AuthService {
//
//    JwtResponse authenticateUser(LoginRequest loginRequest);
//    UserDTO registerUser(UserDTO signupRequest);
//}
