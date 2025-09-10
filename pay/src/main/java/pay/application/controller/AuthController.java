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
import pay.domain.record.SignupResponse;
import pay.domain.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService<SignupResponse, UserDTO> userAuthService;
    private final AuthService<SignupResponse, StoreDTO> storeAuthService;

    public AuthController(@Qualifier("userAuthService") AuthService<SignupResponse, UserDTO> userAuthService,
                          @Qualifier("storeAuthService") AuthService<SignupResponse, StoreDTO> storeAuthService) {
        this.userAuthService = userAuthService;
        this.storeAuthService = storeAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest){
        JwtResponse jwtResponse = userAuthService.authenticate(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup/user")
    public ResponseEntity<SignupResponse> signupUser(@RequestBody UserDTO signUpRequest){
        SignupResponse registeredUser = userAuthService.register(signUpRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/signup/store")
    public ResponseEntity<SignupResponse> signupStore(@RequestBody StoreDTO signUpRequest){
        SignupResponse registeredStore = storeAuthService.register(signUpRequest);
        return ResponseEntity.ok(registeredStore);
    }
}
