package com.danilodps.pay.domain.service.impl;

import com.danilodps.commons.domain.model.response.SignInResponse;
import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.commons.domain.validation.ValidatorComponent;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignInRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.SignUpRequest;
import com.danilodps.pay.adapters.inbound.controller.response.JwtResponse;
import com.danilodps.pay.application.service.impl.ProfileAuthServiceImpl;
import com.danilodps.pay.domain.mappers.entities.core.ProfileEntity2JpaProfileEntity;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.ProfileEntityRepository;
import com.danilodps.pay.domain.model.RoleEntity;
import com.danilodps.pay.domain.model.enums.DocumentTypeEnum;
import com.danilodps.pay.infrastrucure.config.KafkaEventProducer;
import com.danilodps.pay.infrastrucure.security.jwt.JwtTokenGenerator;
import com.danilodps.pay.infrastrucure.spring.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileAuthServiceImpl Tests")
class ProfileAuthServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidatorComponent profileValidator;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ProfileEntityRepository profileEntityRepository;

    @InjectMocks
    private ProfileAuthServiceImpl profileAuthService;

    private SignUpRequest validSignUpRequest;
    private SignInRequest validSignInRequest;
    private ProfileEntity mockProfileEntity;
    private UserDetailsImpl mockUserDetails;
    private Authentication mockAuthentication;
    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final String encodedPassword = "encodedPassword123";
    private final String testUsername = "Test User";
    private final String testDocumentIdentifier = "CPF";
    private final String testDocument = "123.456.789-00";
    private final String testProfileId = "f755df70-c0dc-45ea-88c4-0d2bf2d99397";
    private final String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    private final String documentIdentifier = DocumentTypeEnum.CPF.getShortName();

    @BeforeEach
    void setUp() {
        validSignUpRequest = SignUpRequest.builder()
                .username(testUsername)
                .userEmail(testEmail)
                .password(testPassword)
                .documentIdentifier(testDocumentIdentifier)
                .document(testDocument)
                .build();

        validSignInRequest = SignInRequest.builder()
                .userEmail(testEmail)
                .password(testPassword)
                .build();

        RoleEntity mockRoleEntity = new RoleEntity(1L, "CPF","ROLE_USER", "User role" );

        String validEmail = "test@example.com";
        String username = "Test User";
        String document = "063.546.880-87";
        String profileId = "f755df70-c0dc-45ea-88c4-0d2bf2d99397";
        mockProfileEntity = new ProfileEntity(
                profileId,
                username,
                documentIdentifier,
                document,
                validEmail,
                encodedPassword,
                new BigDecimal("12"),
                Collections.singletonList(mockRoleEntity),
                LocalDateTime.now(),
                LocalDateTime.now());

        mockUserDetails = new UserDetailsImpl(ProfileEntity2JpaProfileEntity.convert(mockProfileEntity));
        mockAuthentication = mock(Authentication.class);
    }

    @Nested
    @DisplayName("register() Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register a new user")
        void shouldSuccessfullyRegisterNewUser() {
            // Given
            doNothing().when(profileValidator).validate(
                    validSignUpRequest.userEmail(),
                    validSignUpRequest.documentIdentifier(),
                    validSignUpRequest.document()
            );
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);
            doNothing().when(kafkaEventProducer).publishSignUpNotification(any(SignUpResponse.class));

            // When
            SignUpResponse response = profileAuthService.register(validSignUpRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.username()).isEqualTo(testUsername);
            assertThat(response.email()).isEqualTo(testEmail);
            assertThat(response.id()).isNotNull();

            verify(profileValidator, times(1)).validate(
                    validSignUpRequest.userEmail(),
                    validSignUpRequest.documentIdentifier(),
                    validSignUpRequest.document()
            );
            verify(passwordEncoder, times(1)).encode(testPassword);
            verify(profileEntityRepository, times(1)).save(any(ProfileEntity.class));
            verify(kafkaEventProducer, times(1)).publishSignUpNotification(any(SignUpResponse.class));
        }

        @Test
        @DisplayName("Should create ProfileEntity with correct values")
        void shouldCreateProfileEntityWithCorrectValues() {
            // Given
            doNothing().when(profileValidator).validate(anyString(), anyString(), anyString());
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(kafkaEventProducer).publishSignUpNotification(any(SignUpResponse.class));

            ArgumentCaptor<ProfileEntity> profileCaptor = ArgumentCaptor.forClass(ProfileEntity.class);

            // When
            profileAuthService.register(validSignUpRequest);

            // Then
            verify(profileEntityRepository).save(profileCaptor.capture());
            ProfileEntity capturedProfile = profileCaptor.getValue();

            assertThat(capturedProfile.getUsername()).isEqualTo(testUsername);
            assertThat(capturedProfile.getProfileEmail()).isEqualTo(testEmail);
            assertThat(capturedProfile.getDocumentIdentifier()).isEqualTo(testDocumentIdentifier);
            assertThat(capturedProfile.getDocument()).isEqualTo(testDocument);
            assertThat(capturedProfile.getPassword()).isEqualTo(encodedPassword);
            assertThat(capturedProfile.getProfileId()).isNotNull();
            assertThat(capturedProfile.getCreatedAt()).isNotNull();
            assertThat(capturedProfile.getRoles()).isNotEmpty();
        }

        @Test
        @DisplayName("Should call validator with correct parameters")
        void shouldCallValidatorWithCorrectParameters() {
            // Given
            doNothing().when(profileValidator).validate(anyString(), anyString(), anyString());
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);
            doNothing().when(kafkaEventProducer).publishSignUpNotification(any(SignUpResponse.class));

            ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> docIdCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> docCaptor = ArgumentCaptor.forClass(String.class);

            // When
            profileAuthService.register(validSignUpRequest);

            // Then
            verify(profileValidator).validate(emailCaptor.capture(), docIdCaptor.capture(), docCaptor.capture());
            assertThat(emailCaptor.getValue()).isEqualTo(testEmail);
            assertThat(docIdCaptor.getValue()).isEqualTo(testDocumentIdentifier);
            assertThat(docCaptor.getValue()).isEqualTo(testDocument);
        }

        @Test
        @DisplayName("Should send Kafka notification after successful registration")
        void shouldSendKafkaNotificationAfterSuccessfulRegistration() {
            // Given
            doNothing().when(profileValidator).validate(anyString(), anyString(), anyString());
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);
            ArgumentCaptor<SignUpResponse> kafkaCaptor = ArgumentCaptor.forClass(SignUpResponse.class);

            // When
            profileAuthService.register(validSignUpRequest);

            // Then
            verify(kafkaEventProducer).publishSignUpNotification(kafkaCaptor.capture());
            SignUpResponse kafkaResponse = kafkaCaptor.getValue();

            assertThat(kafkaResponse.username()).isEqualTo(testUsername);
            assertThat(kafkaResponse.email()).isEqualTo(testEmail);
            assertThat(kafkaResponse.signupTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when validator throws exception")
        void shouldThrowExceptionWhenValidatorThrowsException() {
            // Given
            doThrow(new RuntimeException("Validation failed"))
                    .when(profileValidator).validate(anyString(), anyString(), anyString());

            // When & Then
            assertThatThrownBy(() -> profileAuthService.register(validSignUpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Validation failed");

            verify(passwordEncoder, never()).encode(anyString());
            verify(profileEntityRepository, never()).save(any());
            verify(kafkaEventProducer, never()).publishSignUpNotification(any());
        }

        @Test
        @DisplayName("Should encode password before saving")
        void shouldEncodePasswordBeforeSaving() {
            // Given
            doNothing().when(profileValidator).validate(anyString(), anyString(), anyString());
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);
            doNothing().when(kafkaEventProducer).publishSignUpNotification(any(SignUpResponse.class));

            // When
            profileAuthService.register(validSignUpRequest);

            // Then
            verify(passwordEncoder, times(1)).encode(testPassword);
        }
    }

    @Nested
    @DisplayName("authenticate() Tests")
    class AuthenticateTests {

        @Test
        @DisplayName("Should successfully authenticate user and return JWT")
        void shouldSuccessfullyAuthenticateUserAndReturnJwt() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
            when(jwtTokenGenerator.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);
            doNothing().when(kafkaEventProducer).publishSignInNotification(any(SignInResponse.class));

            // When
            JwtResponse response = profileAuthService.authenticate(validSignInRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo(jwtToken);
            assertThat(response.profileId()).isEqualTo(testProfileId);
            assertThat(response.username()).isEqualTo(testUsername);
            assertThat(response.profileEmail()).isEqualTo(testEmail);
            assertThat(response.roles()).containsExactly("ROLE_USER");

            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenGenerator, times(1)).generateJwtToken(mockAuthentication);
            verify(kafkaEventProducer, times(1)).publishSignInNotification(any(SignInResponse.class));
        }

        @Test
        @DisplayName("Should create authentication with correct credentials")
        void shouldCreateAuthenticationWithCorrectCredentials() {
            // Given
            ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

            when(authenticationManager.authenticate(authCaptor.capture()))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
            when(jwtTokenGenerator.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);
            doNothing().when(kafkaEventProducer).publishSignInNotification(any(SignInResponse.class));

            // When
            profileAuthService.authenticate(validSignInRequest);

            // Then
            UsernamePasswordAuthenticationToken capturedAuth = authCaptor.getValue();
            assertThat(capturedAuth.getPrincipal()).isEqualTo(testEmail);
            assertThat(capturedAuth.getCredentials()).isEqualTo(testPassword);
        }

        @Test
        @DisplayName("Should set authentication in SecurityContextHolder")
        void shouldSetAuthenticationInSecurityContextHolder() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
            when(jwtTokenGenerator.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);
            doNothing().when(kafkaEventProducer).publishSignInNotification(any(SignInResponse.class));

            // When
            profileAuthService.authenticate(validSignInRequest);

            // Then
            verify(mockAuthentication, times(1)).getPrincipal();
        }

        @Test
        @DisplayName("Should extract correct roles from user details")
        void shouldExtractCorrectRolesFromUserDetails() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
            when(jwtTokenGenerator.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);
            doNothing().when(kafkaEventProducer).publishSignInNotification(any(SignInResponse.class));

            // When
            JwtResponse response = profileAuthService.authenticate(validSignInRequest);

            // Then
            assertThat(response.roles()).hasSize(1);
            assertThat(response.roles()).contains("ROLE_USER");
        }

        @Test
        @DisplayName("Should send Kafka notification after successful authentication")
        void shouldSendKafkaNotificationAfterSuccessfulAuthentication() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
            when(jwtTokenGenerator.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);

            ArgumentCaptor<SignInResponse> kafkaCaptor = ArgumentCaptor.forClass(SignInResponse.class);

            // When
            profileAuthService.authenticate(validSignInRequest);

            // Then
            verify(kafkaEventProducer).publishSignInNotification(kafkaCaptor.capture());
            SignInResponse kafkaResponse = kafkaCaptor.getValue();

            assertThat(kafkaResponse.id()).isEqualTo(testProfileId);
            assertThat(kafkaResponse.username()).isEqualTo(testUsername);
            assertThat(kafkaResponse.email()).isEqualTo(testEmail);
            assertThat(kafkaResponse.signinTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when authentication fails - invalid credentials")
        void shouldThrowExceptionWhenAuthenticationFailsInvalidCredentials() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("Bad credentials"));

            // When & Then
            assertThatThrownBy(() -> profileAuthService.authenticate(validSignInRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Bad credentials");

            verify(jwtTokenGenerator, never()).generateJwtToken(any());
            verify(kafkaEventProducer, never()).publishSignInNotification(any());
        }

        @Test
        @DisplayName("Should throw exception when user is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("User not found"));

            // When & Then
            assertThatThrownBy(() -> profileAuthService.authenticate(validSignInRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should generate JWT with correct user details")
        void shouldGenerateJwtWithCorrectUserDetails() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
            when(jwtTokenGenerator.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);
            doNothing().when(kafkaEventProducer).publishSignInNotification(any(SignInResponse.class));

            // When
            JwtResponse response = profileAuthService.authenticate(validSignInRequest);

            // Then
            verify(jwtTokenGenerator, times(1)).generateJwtToken(mockAuthentication);
            assertThat(response.accessToken()).isEqualTo(jwtToken);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle user with multiple roles")
        void shouldHandleUserWithMultipleRoles() {

            mockProfileEntity.setRoles(List.of(
                                    new RoleEntity(1L, documentIdentifier, "ROLE_USER", "User role"),
                                    new RoleEntity(1L, documentIdentifier, "ROLE_ADMIN", "Admin role")
            ));

            UserDetailsImpl multiRoleUserDetails = new UserDetailsImpl(ProfileEntity2JpaProfileEntity.convert(mockProfileEntity));

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(multiRoleUserDetails);
            when(jwtTokenGenerator.generateJwtToken(mockAuthentication)).thenReturn(jwtToken);
            doNothing().when(kafkaEventProducer).publishSignInNotification(any(SignInResponse.class));

            // When
            JwtResponse response = profileAuthService.authenticate(validSignInRequest);

            // Then
            assertThat(response.roles()).hasSize(2);
            assertThat(response.roles()).contains("ROLE_USER", "ROLE_ADMIN");
        }

        @Test
        @DisplayName("Should preserve registration timestamp when creating user")
        void shouldPreserveRegistrationTimestampWhenCreatingUser() {
            // Given
            doNothing().when(profileValidator).validate(anyString(), anyString(), anyString());
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(kafkaEventProducer).publishSignUpNotification(any(SignUpResponse.class));

            ArgumentCaptor<ProfileEntity> profileCaptor = ArgumentCaptor.forClass(ProfileEntity.class);
            LocalDateTime beforeTest = LocalDateTime.now();

            // When
            profileAuthService.register(validSignUpRequest);

            // Then
            verify(profileEntityRepository).save(profileCaptor.capture());
            ProfileEntity capturedProfile = profileCaptor.getValue();

            assertThat(capturedProfile.getCreatedAt()).isNotNull();
            assertThat(capturedProfile.getCreatedAt()).isAfterOrEqualTo(beforeTest);
        }
    }

}