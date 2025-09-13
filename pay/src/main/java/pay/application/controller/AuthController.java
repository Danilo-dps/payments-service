package pay.application.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.domain.dto.StoreDTO;
import pay.domain.dto.UserDTO;
import pay.domain.payload.request.LoginRequest;
import pay.domain.payload.response.JwtResponse;
import pay.domain.payload.request.SignupRequest;
import pay.domain.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService<SignupRequest, UserDTO> userAuthService;
    private final AuthService<SignupRequest, StoreDTO> storeAuthService;

    public AuthController(@Qualifier("userAuthService") AuthService<SignupRequest, UserDTO> userAuthService,
                          @Qualifier("storeAuthService") AuthService<SignupRequest, StoreDTO> storeAuthService) {
        this.userAuthService = userAuthService;
        this.storeAuthService = storeAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest){
        JwtResponse jwtResponse = userAuthService.authenticate(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup/user")
    public ResponseEntity<SignupRequest> signupUser(@RequestBody UserDTO signUpRequest){
        SignupRequest registeredUser = userAuthService.register(signUpRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/signup/store")
    public ResponseEntity<SignupRequest> signupStore(@RequestBody StoreDTO signUpRequest){
        SignupRequest registeredStore = storeAuthService.register(signUpRequest);
        return ResponseEntity.ok(registeredStore);
    }
}
