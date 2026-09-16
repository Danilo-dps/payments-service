package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.domain.model.ProfileEntityRepository;
import com.danilodps.pay.domain.model.RoleEntityRepository;
import com.danilodps.pay.domain.model.entities.ProfileEntity;
import com.danilodps.pay.domain.model.entities.RoleEntity;
import com.danilodps.pay.infrastrucure.spring.UserDetailsImpl;
import com.danilodps.pay.infrastrucure.spring.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

    @Mock
    private ProfileEntityRepository profileEntityRepository;

    @Mock
    private RoleEntityRepository roleEntityRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private ProfileEntity mockProfileEntity;
    private RoleEntity mockRoleEntity;
    private final String validEmail = "user@example.com";
    private final String invalidEmail = "nonexistent@example.com";
    private final String profileId = "fd6ce5f9-0cb4-4f2b-8523-59404c65f040";
    private final String username = "Test User";
    private final String encodedPassword = "encodedPassword123";
    public static final ZoneId SAO_PAULO_ZONE = ZoneId.of("America/Sao_Paulo");

    @BeforeEach
    void setUp() {
        String documentIdentifier = "CPF";
        mockRoleEntity = new RoleEntity(1L, documentIdentifier, "ROLE_USER", "User role");

        String document = "123.456.789-00";
        mockProfileEntity = new ProfileEntity(
                profileId,
                username,
                documentIdentifier,
                document,
                validEmail,
                encodedPassword,
                new BigDecimal("1200"),
                LocalDateTime.now(SAO_PAULO_ZONE),
                LocalDateTime.now(SAO_PAULO_ZONE));
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
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

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
            verify(roleEntityRepository, times(1)).findRolesByProfileId(profileId);
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
            verify(roleEntityRepository, never()).findRolesByProfileId(any());
        }

        @Test
        @DisplayName("Should return UserDetailsImpl with correct profileId")
        void shouldReturnUserDetailsImplWithCorrectProfileId() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

            // When
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getProfileId()).isEqualTo(profileId);
            assertThat(userDetails.getUsername()).isEqualTo(username);
            assertThat(userDetails.getProfileEmail()).isEqualTo(validEmail);
        }

        @Test
        @DisplayName("Should include all roles returned by RoleEntityRepository")
        void shouldIncludeAllRolesFromProfileEntity() {
            // Given
            RoleEntity roleUser = new RoleEntity(1L, "CPF", "ROLE_USER", "User role");
            RoleEntity roleAdmin = new RoleEntity(2L, "CPF", "ROLE_ADMIN", "User role");

            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(roleUser, roleAdmin));

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
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(Collections.emptyList());

            // When
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails.getAuthorities()).isEmpty();
        }

        @Test
        @DisplayName("Should call repositories with correct email/profileId parameter")
        void shouldCallRepositoryWithCorrectEmailParameter() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

            // When
            userDetailsService.loadUserByUsername(validEmail);

            // Then
            verify(profileEntityRepository, times(1)).findByProfileEmail(validEmail);
            verify(roleEntityRepository, times(1)).findRolesByProfileId(profileId);
        }

        @Test
        @DisplayName("Should not call repositories when exception is thrown before")
        void shouldNotCallRepositoryWhenExceptionIsThrownBefore() {

            when(profileEntityRepository.findByProfileEmail(invalidEmail))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(invalidEmail))
                    .isInstanceOf(RuntimeException.class);

            verify(profileEntityRepository, times(1)).findByProfileEmail(invalidEmail);
            verify(roleEntityRepository, never()).findRolesByProfileId(any());
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
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

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
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

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
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

            // When
            UserDetails userDetails = userDetailsService.loadUserByUsername(validEmail);

            // Then
            assertThat(userDetails).isNotNull();

            verify(profileEntityRepository, never()).save(any());
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
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

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
            when(roleEntityRepository.findRolesByProfileId(profileId))
                    .thenReturn(List.of(mockRoleEntity));

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