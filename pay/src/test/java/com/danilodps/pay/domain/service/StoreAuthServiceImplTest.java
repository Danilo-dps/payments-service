package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.Role;
import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.enums.ERole;
import com.danilodps.pay.domain.model.request.SignupRequest;
import com.danilodps.pay.domain.repository.RoleRepository;
import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.security.jwt.JwtUtils;
import com.danilodps.pay.domain.service.impl.StoreAuthServiceImpl;
import com.danilodps.pay.domain.utils.validator.StoreValidator;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreAuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StoreValidator storeValidator;

    @Captor
    private ArgumentCaptor<Store> storeCaptor;

    @Captor
    private ArgumentCaptor<SignupRequest> signupRequestCaptor;

    private StoreAuthServiceImpl storeAuthService;

    private StoreDTO validStoreDTO;
    private Role userRole;
    private Role storeRole;

    String uuid = "67c6e748-aa01-4fb3-b665-9bfaecfb179e";

    @BeforeEach
    void setUp() {
        storeAuthService = new StoreAuthServiceImpl(
                authenticationManager, kafkaEventProducer, jwtUtils,
                storeRepository, roleRepository, passwordEncoder, storeValidator
        );

        userRole = new Role();
        userRole.setName(ERole.ROLE_USER);

        storeRole = new Role();
        storeRole.setName(ERole.ROLE_STORE);

        validStoreDTO = StoreDTO.builder()
                .storeId(uuid)
                .storeName("Test Store")
                .cnpj("12345678000195")
                .storeEmail("test@store.com")
                .password("password123")
                .build();
    }

    @Test
    void register_WithValidStoreDTOAndNoRoles_ShouldRegisterStoreWithDefaultRole() {
        validStoreDTO.setRole(null);
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode(validStoreDTO.getPassword())).thenReturn(encodedPassword);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        Store savedStore = new Store();
        savedStore.setStoreId(validStoreDTO.getStoreId());
        savedStore.setStoreName(validStoreDTO.getStoreName());
        savedStore.setStoreEmail(validStoreDTO.getStoreEmail());
        when(storeRepository.saveAndFlush(any(Store.class))).thenReturn(savedStore);

        SignupRequest result = storeAuthService.register(validStoreDTO);

        verify(storeValidator).validate(validStoreDTO);
        verify(passwordEncoder).encode(validStoreDTO.getPassword());
        verify(roleRepository).findByName(ERole.ROLE_USER);

        verify(storeRepository).saveAndFlush(storeCaptor.capture());
        Store capturedStore = storeCaptor.getValue();

        assertEquals(validStoreDTO.getStoreName(), capturedStore.getStoreName());
        assertEquals(validStoreDTO.getCnpj(), capturedStore.getCnpj());
        assertEquals(validStoreDTO.getStoreEmail(), capturedStore.getStoreEmail());
        assertEquals(encodedPassword, capturedStore.getPassword());
        assertEquals(Set.of(userRole), capturedStore.getRole());

        verify(kafkaEventProducer).publishKafkaSignUpNotification(signupRequestCaptor.capture());

        assertEquals(savedStore.getStoreName(), result.username());
        assertEquals(savedStore.getStoreEmail(), result.email());
        assertNotNull(result.now());
    }

    @Test
    void register_WhenDefaultRoleNotFound_ShouldThrowException() {
        validStoreDTO.setRole(null);

        when(passwordEncoder.encode(validStoreDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> storeAuthService.register(validStoreDTO));

        assertEquals("Erro: Perfil padrão ROLE_USER não encontrado.", exception.getMessage());

        verify(storeRepository, never()).saveAndFlush(any(Store.class));
        verify(kafkaEventProducer, never()).publishKafkaSignUpNotification(any(SignupRequest.class));
    }

    @Test
    void register_WhenCustomRoleNotFound_ShouldThrowException() {
        Set<Role> customRoles = Set.of(storeRole);
        validStoreDTO.setRole(customRoles);

        when(passwordEncoder.encode(validStoreDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_STORE)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> storeAuthService.register(validStoreDTO));

        assertEquals("Erro: Perfil ROLE_STORE não configurado no banco.", exception.getMessage());

        verify(storeRepository, never()).saveAndFlush(any(Store.class));
        verify(kafkaEventProducer, never()).publishKafkaSignUpNotification(any(SignupRequest.class));
    }

    @Test
    void register_WhenStoreValidatorThrowsException_ShouldPropagateException() {
        String validationError = "CNPJ inválido";
        doThrow(new RuntimeException(validationError)).when(storeValidator).validate(validStoreDTO);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> storeAuthService.register(validStoreDTO));

        assertEquals(validationError, exception.getMessage());

        verify(storeRepository, never()).saveAndFlush(any(Store.class));
        verify(kafkaEventProducer, never()).publishKafkaSignUpNotification(any(SignupRequest.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_ShouldEncodePasswordBeforeSaving() {
        validStoreDTO.setPassword("plainPassword");
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(storeRepository.saveAndFlush(any(Store.class))).thenReturn(new Store());

        storeAuthService.register(validStoreDTO);

        verify(passwordEncoder).encode("plainPassword");
        verify(storeRepository).saveAndFlush(storeCaptor.capture());

        assertEquals(encodedPassword, storeCaptor.getValue().getPassword());
    }

}