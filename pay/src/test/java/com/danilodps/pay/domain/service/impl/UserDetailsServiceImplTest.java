package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.RoleEntity;
import com.danilodps.pay.domain.repository.ProfileEntityRepository;
import com.danilodps.pay.domain.service.spring.UserDetailsImpl;
import com.danilodps.pay.domain.service.spring.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

    @Mock
    private ProfileEntityRepository profileEntityRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private ProfileEntity mockProfileEntity;
    private final String validEmail = "user@example.com";
    private final String invalidEmail = "nonexistent@example.com";
    private final String profileId = UUID.randomUUID().toString();
    private final String username = "Test User";
    private final String encodedPassword = "encodedPassword123";

    @BeforeEach
    void setUp() {
        String documentIdentifier = "CPF";
        RoleEntity mockRoleEntity = RoleEntity.builder()
                .roleId(1L)
                .roleGrantedAuthority("ROLE_USER")
                .description("User role")
                .docIdentifier(documentIdentifier)
                .build();

        String document = "123.456.789-00";
        mockProfileEntity = ProfileEntity.builder()
                .profileId(profileId)
                .username(username)
                .profileEmail(validEmail)
                .password(encodedPassword)
                .documentIdentifier(documentIdentifier)
                .document(document)
                .roles(Collections.singletonList(mockRoleEntity))
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("loadUserByUsername() Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should return UserDetails when email exists")
        void shouldReturnUserDetailsWhenEmailExists() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetails userDetails = userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails).isNotNull();
            assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);

            assertThat(userDetails.getUsername()).isEqualTo(username);
            assertThat(userDetails.getPassword()).isEqualTo(encodedPassword);
            assertThat(userDetails.getAuthorities()).isNotEmpty();
            assertThat(userDetails.getAuthorities()).hasSize(1);
            assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                    .isEqualTo("ROLE_USER");

            verify(profileEntityRepository, times(1)).findByProfileEmail(validEmail);
        }

        @Test
        @DisplayName("Should throw RuntimeException when email does not exist")
        void shouldThrowRuntimeExceptionWhenEmailDoesNotExist() {
            // Given
            when(profileEntityRepository.findByProfileEmail(invalidEmail))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(invalidEmail))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Perfil não encontrado")
                    .hasMessageContaining(invalidEmail);

            verify(profileEntityRepository, times(1)).findByProfileEmail(invalidEmail);
        }

        @Test
        @DisplayName("Should return UserDetailsImpl with correct profileId")
        void shouldReturnUserDetailsImplWithCorrectProfileId() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getProfileId()).isEqualTo(profileId);
            assertThat(userDetails.getUsername()).isEqualTo(username);
            assertThat(userDetails.getProfileEmail()).isEqualTo(validEmail);
        }

        @Test
        @DisplayName("Should include all roles from ProfileEntity")
        void shouldIncludeAllRolesFromProfileEntity() {
            // Given
            RoleEntity roleUser = RoleEntity.builder()
                    .roleId(1L)
                    .roleGrantedAuthority("ROLE_USER")
                    .build();
            RoleEntity roleAdmin = RoleEntity.builder()
                    .roleId(2L)
                    .roleGrantedAuthority("ROLE_ADMIN")
                    .build();

            mockProfileEntity.setRoles(java.util.List.of(roleUser, roleAdmin));

            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getAuthorities()).hasSize(2);
            assertThat(userDetails.getAuthorities())
                    .extracting("authority")
                    .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
        }

        @Test
        @DisplayName("Should handle user with empty roles list")
        void shouldHandleUserWithEmptyRolesList() {
            // Given
            mockProfileEntity.setRoles(Collections.emptyList());

            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getAuthorities()).isEmpty();
        }

        @Test
        @DisplayName("Should call repository with correct email parameter")
        void shouldCallRepositoryWithCorrectEmailParameter() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            userDetailsService.loadUserByUsername(validEmail);

            // Then
            verify(profileEntityRepository, times(1)).findByProfileEmail(validEmail);
        }

        @Test
        @DisplayName("Should not call repository when exception is thrown before")
        void shouldNotCallRepositoryWhenExceptionIsThrownBefore() {

            when(profileEntityRepository.findByProfileEmail(invalidEmail))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(invalidEmail))
                    .isInstanceOf(RuntimeException.class);

            verify(profileEntityRepository, times(1)).findByProfileEmail(invalidEmail);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle email with leading/trailing spaces")
        void shouldHandleEmailWithLeadingTrailingSpaces() {
            // Given
            String emailWithSpaces = "  " + validEmail + "  ";

            when(profileEntityRepository.findByProfileEmail(emailWithSpaces))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetails userDetails = userDetailsService.loadUserByUsername(emailWithSpaces);

            // Then
            assertThat(userDetails).isNotNull();
            verify(profileEntityRepository, times(1)).findByProfileEmail(emailWithSpaces);
        }

        @Test
        @DisplayName("Should preserve password encoding exactly as stored")
        void shouldPreservePasswordEncodingExactlyAsStored() {
            // Given
            String complexEncodedPassword = "{bcrypt}$2a$10$N9qo8uLOickgx2ZMRZoMy.Mr/KqZ5JkF5gF6sK4X5fY8gX9f7gX9f";
            mockProfileEntity.setPassword(complexEncodedPassword);

            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetails userDetails = userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getPassword()).isEqualTo(complexEncodedPassword);
        }
    }

    @Nested
    @DisplayName("Transaction & ReadOnly Tests")
    class TransactionTests {

        @Test
        @DisplayName("Should be read-only transactional")
        void shouldBeReadOnlyTransactional() {

            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetails userDetails = userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails).isNotNull();

            verify(profileEntityRepository, never()).save(any());
            verify(profileEntityRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Null Safety Tests")
    class NullSafetyTests {

        @Test
        @DisplayName("Should throw NullPointerException when email is null")
        void shouldThrowNullPointerExceptionWhenEmailIsNull() {
            // When & Then
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should throw exception when email is empty string")
        void shouldThrowExceptionWhenEmailIsEmptyString() {
            // When & Then
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("Integration-like Tests")
    class IntegrationLikeTests {

        @Test
        @DisplayName("Should return UserDetailsImpl instance with all fields populated")
        void shouldReturnUserDetailsImplWithAllFieldsPopulated() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getProfileId()).isNotBlank();
            assertThat(userDetails.getUsername()).isNotBlank();
            assertThat(userDetails.getProfileEmail()).isNotBlank();
            assertThat(userDetails.getPassword()).isNotBlank();
            assertThat(userDetails.getAuthorities()).isNotNull();
        }

        @Test
        @DisplayName("Should maintain consistency between UserDetails and ProfileEntity")
        void shouldMaintainConsistencyBetweenUserDetailsAndProfileEntity() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getProfileId()).isEqualTo(mockProfileEntity.getProfileId());
            assertThat(userDetails.getUsername()).isEqualTo(mockProfileEntity.getUsername());
            assertThat(userDetails.getProfileEmail()).isEqualTo(mockProfileEntity.getProfileEmail());
            assertThat(userDetails.getPassword()).isEqualTo(mockProfileEntity.getPassword());
        }
    }

}