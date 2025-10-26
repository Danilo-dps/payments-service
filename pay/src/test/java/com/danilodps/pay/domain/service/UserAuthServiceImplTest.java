package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.dto.UserDTO;
import com.danilodps.pay.domain.model.Role;
import com.danilodps.pay.domain.model.User;
import com.danilodps.pay.domain.model.enums.ERole;
import com.danilodps.pay.domain.model.request.SignupRequest;
import com.danilodps.pay.domain.repository.RoleRepository;
import com.danilodps.pay.domain.repository.UserRepository;
import com.danilodps.pay.domain.security.jwt.JwtUtils;
import com.danilodps.pay.domain.service.impl.UserAuthServiceImpl;
import com.danilodps.pay.domain.utils.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<SignupRequest> signupRequestCaptor;

    private UserAuthServiceImpl userAuthService;

    private UserDTO validUserDTO;
    private Role userRole;

    String uuid = "67c6e748-aa01-4fb3-b665-9bfaecfb179e";

    @BeforeEach
    void setUp() {
        userAuthService = new UserAuthServiceImpl(
                authenticationManager, kafkaEventProducer, jwtUtils,
                userRepository, roleRepository, passwordEncoder, userValidator
        );

        userRole = new Role();
        userRole.setName(ERole.ROLE_USER);


        validUserDTO = UserDTO.builder()
                .userId(UUID.fromString(uuid))
                .username("Test User")
                .cpf("12345678000195")
                .email("test@store.com")
                .password("password123")
                .build();
    }

    @Test
    void register_WithValidStoreDTOAndNoRoles_ShouldRegisterStoreWithDefaultRole() {
        validUserDTO.setRole(null);
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode(validUserDTO.getPassword())).thenReturn(encodedPassword);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        User savedUser = new User();
        savedUser.setUserId(validUserDTO.getUserId());
        savedUser.setUsername(validUserDTO.getUsername());
        savedUser.setEmail(validUserDTO.getEmail());
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        SignupRequest result = userAuthService.register(validUserDTO);

        verify(userValidator).validate(validUserDTO);
        verify(passwordEncoder).encode(validUserDTO.getPassword());
        verify(roleRepository).findByName(ERole.ROLE_USER);

        verify(userRepository).saveAndFlush(userArgumentCaptor.capture());
        User capturedStore = userArgumentCaptor.getValue();

        assertEquals(validUserDTO.getUsername(), capturedStore.getUsername());
        assertEquals(validUserDTO.getCpf(), capturedStore.getCpf());
        assertEquals(validUserDTO.getEmail(), capturedStore.getEmail());
        assertEquals(encodedPassword, capturedStore.getPassword());
        assertEquals(Set.of(userRole), capturedStore.getRole());

        verify(kafkaEventProducer).publishKafkaSignUpNotification(signupRequestCaptor.capture());

        assertEquals(savedUser.getUsername(), result.username());
        assertEquals(savedUser.getEmail(), result.email());
        assertNotNull(result.signupTimestamp());
    }

    @Test
    void register_WhenDefaultRoleNotFound_ShouldThrowException() {
        validUserDTO.setRole(null);

        when(passwordEncoder.encode(validUserDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userAuthService.register(validUserDTO));

        assertEquals("Erro: Perfil padrão ROLE_USER não encontrado.", exception.getMessage());

        verify(userRepository, never()).saveAndFlush(any(User.class));
        verify(kafkaEventProducer, never()).publishKafkaSignUpNotification(any(SignupRequest.class));
    }

    @Test
    void register_WhenCustomRoleNotFound_ShouldThrowException() {
        Set<Role> customRoles = Set.of(userRole);
        validUserDTO.setRole(customRoles);

        when(passwordEncoder.encode(validUserDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userAuthService.register(validUserDTO));

        assertEquals("Erro: Perfil ROLE_USER não configurado no banco.", exception.getMessage());

        verify(userRepository, never()).saveAndFlush(any(User.class));
        verify(kafkaEventProducer, never()).publishKafkaSignUpNotification(any(SignupRequest.class));
    }

    @Test
    void register_WhenStoreValidatorThrowsException_ShouldPropagateException() {
        String validationError = "CNPJ inválido";
        doThrow(new RuntimeException(validationError)).when(userValidator).validate(validUserDTO);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userAuthService.register(validUserDTO));

        assertEquals(validationError, exception.getMessage());

        verify(userRepository, never()).saveAndFlush(any(User.class));
        verify(kafkaEventProducer, never()).publishKafkaSignUpNotification(any(SignupRequest.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_ShouldEncodePasswordBeforeSaving() {
        validUserDTO.setPassword("plainPassword");
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(new User());

        userAuthService.register(validUserDTO);

        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).saveAndFlush(userArgumentCaptor.capture());

        assertEquals(encodedPassword, userArgumentCaptor.getValue().getPassword());
    }
    
}