package com.danilodps.pay.application.controller;

import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.dto.UserDTO;
import com.danilodps.pay.domain.model.Role;
import com.danilodps.pay.domain.model.enums.ERole;
import com.danilodps.pay.domain.model.request.LoginRequest;
import com.danilodps.pay.domain.model.request.SignupRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;
import com.danilodps.pay.domain.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    @Qualifier("userAuthService")
    AuthService<SignupRequest, UserDTO> userAuthService;

    @Mock
    @Qualifier("storeAuthService")
    AuthService<SignupRequest, StoreDTO> storeAuthService;

    @InjectMocks
    AuthController authController;

    LoginRequest loginRequest;
    UserDTO userDTO;
    StoreDTO storeDTO;
    JwtResponse jwtResponse;
    SignupRequest signupRequest;
    String uuid = "67c6e748-aa01-4fb3-b665-9bfaecfb179e";
    Role userRole;
    Role storeRole;

    @BeforeEach
    void setUp() {
        authController = new AuthController(userAuthService, storeAuthService);

        userRole = new Role();
        userRole.setName(ERole.ROLE_USER);

        storeRole = new Role();
        storeRole.setName(ERole.ROLE_STORE);

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        userDTO = UserDTO.builder()
                .userId(uuid)
                .username("testuser")
                .email("user@test.com")
                .cpf("333.629.770-22")
                .password("password123")
                .role(Set.of(userRole))
                .build();

        storeDTO = StoreDTO.builder()
                .storeId(uuid)
                .storeName("Test Store")
                .storeEmail("store@test.com")
                .cnpj("75.161.045/0001-47")
                .password("password123")
                .role(Set.of(storeRole))
                .build();

        jwtResponse = JwtResponse.builder()
                .username("testuser")
                .email("user@test.com")
                .build();

        signupRequest = SignupRequest.builder()
                .id(uuid)
                .username("testuser")
                .email("user@test.com")
                .build();
    }

    @Test
    void login_WithValidLoginRequest_ShouldReturnJwtResponse() {
        when(userAuthService.authenticate(loginRequest)).thenReturn(jwtResponse);

        ResponseEntity<JwtResponse> response = authController.login(loginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());

        JwtResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(jwtResponse.getAccessToken(), responseBody.getAccessToken());
        assertEquals(jwtResponse.getUsername(), responseBody.getUsername());
        assertEquals(jwtResponse.getEmail(), responseBody.getEmail());

        verify(userAuthService).authenticate(loginRequest);
    }

    @Test
    void login_ShouldCallUserAuthServiceAuthenticate() {
        when(userAuthService.authenticate(loginRequest)).thenReturn(jwtResponse);

        authController.login(loginRequest);

        verify(userAuthService).authenticate(loginRequest);
    }

    @Test
    void signupUser_WithValidUserDTO_ShouldReturnSignupRequest() {
        when(userAuthService.register(userDTO)).thenReturn(signupRequest);

        ResponseEntity<SignupRequest> response = authController.signupUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());

        SignupRequest responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(signupRequest.id(), responseBody.id());
        assertEquals(signupRequest.username(), responseBody.username());
        assertEquals(signupRequest.email(), responseBody.email());

        verify(userAuthService).register(userDTO);
    }

    @Test
    void signupUser_ShouldCallUserAuthServiceRegister() {
        when(userAuthService.register(userDTO)).thenReturn(signupRequest);

        authController.signupUser(userDTO);

        verify(userAuthService).register(userDTO);
    }

    @Test
    void signupStore_WithValidStoreDTO_ShouldReturnSignupRequest() {
        when(storeAuthService.register(storeDTO)).thenReturn(signupRequest);

        ResponseEntity<SignupRequest> response = authController.signupStore(storeDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());

        SignupRequest responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(signupRequest.id(), responseBody.id());
        assertEquals(signupRequest.username(), responseBody.username());
        assertEquals(signupRequest.email(), responseBody.email());

        verify(storeAuthService).register(storeDTO);
    }

    @Test
    void signupStore_ShouldCallStoreAuthServiceRegister() {
        when(storeAuthService.register(storeDTO)).thenReturn(signupRequest);

        authController.signupStore(storeDTO);

        verify(storeAuthService).register(storeDTO);
    }

    @Test
    void login_WithDifferentLoginRequest_ShouldPassCorrectDataToService() {
        LoginRequest customLoginRequest = LoginRequest.builder()
                .username("customuser")
                .password("custompass")
                .build();

        JwtResponse customJwtResponse = JwtResponse.builder()
                .username("customuser")
                .email("custom@test.com")
                .build();

        when(userAuthService.authenticate(customLoginRequest)).thenReturn(customJwtResponse);

        ResponseEntity<JwtResponse> response = authController.login(customLoginRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("customuser", responseBody.getUsername());

        verify(userAuthService).authenticate(customLoginRequest);
    }

    @Test
    void signupUser_WithDifferentUserDTO_ShouldPassCorrectDataToService() {
        UserDTO customUserDTO = UserDTO.builder()
                .username("customuser")
                .email("custom@test.com")
                .password("custompass")
                .build();

        SignupRequest customSignupRequest = SignupRequest.builder()
                .id(uuid)
                .username("customuser")
                .email("custom@test.com")
                .build();

        when(userAuthService.register(customUserDTO)).thenReturn(customSignupRequest);

        ResponseEntity<SignupRequest> response = authController.signupUser(customUserDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SignupRequest responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(uuid, responseBody.id());
        assertEquals("customuser", responseBody.username());

        verify(userAuthService).register(customUserDTO);
    }

    @Test
    void signupStore_WithDifferentStoreDTO_ShouldPassCorrectDataToService() {
        StoreDTO customStoreDTO = StoreDTO.builder()
                .storeName("Custom Store")
                .storeEmail("customstore@test.com")
                .cnpj("98765432000199")
                .password("storepass")
                .build();

        SignupRequest customSignupRequest = SignupRequest.builder()
                .id(uuid)
                .username("Custom Store")
                .email("customstore@test.com")
                .build();

        when(storeAuthService.register(customStoreDTO)).thenReturn(customSignupRequest);

        ResponseEntity<SignupRequest> response = authController.signupStore(customStoreDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SignupRequest responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(uuid, responseBody.id());
        assertEquals("Custom Store", responseBody.username());

        verify(storeAuthService).register(customStoreDTO);
    }

    @Test
    void constructor_ShouldInitializeServicesCorrectly() {
        assertNotNull(authController);
    }
}