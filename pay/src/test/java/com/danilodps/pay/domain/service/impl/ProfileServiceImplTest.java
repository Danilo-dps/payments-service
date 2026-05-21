package com.danilodps.pay.domain.service.impl;

import com.danilodps.commons.application.exceptions.DuplicateEmailException;
import com.danilodps.commons.application.exceptions.NotFoundException;
import com.danilodps.commons.domain.validation.EmailValidator;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.request.update.ProfileRequestUpdate;
import com.danilodps.pay.domain.model.response.ProfileResponse;
import com.danilodps.pay.domain.repository.ProfileEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileServiceImpl Tests")
class ProfileServiceImplTest {

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProfileEntityRepository profileEntityRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ProfileEntity mockProfileEntity;
    private final String validProfileId = "profile-123";
    private final String validEmail = "user@example.com";
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockProfileEntity = ProfileEntity.builder()
                .profileId(validProfileId)
                .username("Test User")
                .profileEmail(validEmail)
                .balance(BigDecimal.valueOf(1000.50))
                .password("encodedOldPassword")
                .createdAt(now.minusDays(30))
                .lastUpdated(now.minusDays(1))
                .build();
    }

    @Nested
    @DisplayName("getById() Tests")
    class GetByIdTests {

        @Test
        @DisplayName("Should return ProfileResponse when profile exists")
        void shouldReturnProfileResponseWhenProfileExists() {
            // Given
            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            ProfileResponse response = profileService.getById(validProfileId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.profileId()).isEqualTo(validProfileId);
            assertThat(response.username()).isEqualTo("Test User");
            assertThat(response.profileEmail()).isEqualTo(validEmail);
            assertThat(response.balance()).isEqualByComparingTo("1000.50");

            verify(profileEntityRepository, times(1)).findById(validProfileId);
        }

        @Test
        @DisplayName("Should throw NotFoundException when profile does not exist")
        void shouldThrowNotFoundExceptionWhenProfileDoesNotExist() {
            // Given
            String nonExistentId = "non-existent-id";
            when(profileEntityRepository.findById(nonExistentId))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> profileService.getById(nonExistentId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(nonExistentId);

            verify(profileEntityRepository, times(1)).findById(nonExistentId);
        }

        @Test
        @DisplayName("Should throw NullPointerException when profileId is null")
        void shouldThrowNullPointerExceptionWhenProfileIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> profileService.getById(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("User ID não pode ser null");

            verify(profileEntityRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("getByEmail() Tests")
    class GetByEmailTests {

        @Test
        @DisplayName("Should return ProfileResponse when email exists")
        void shouldReturnProfileResponseWhenEmailExists() {
            // Given
            when(profileEntityRepository.findByProfileEmail(validEmail))
                    .thenReturn(Optional.of(mockProfileEntity));

            // When
            ProfileResponse response = profileService.getByEmail(validEmail);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.profileEmail()).isEqualTo(validEmail);
            assertThat(response.profileId()).isEqualTo(validProfileId);

            verify(profileEntityRepository, times(1)).findByProfileEmail(validEmail);
        }

        @Test
        @DisplayName("Should throw NotFoundException when email does not exist")
        void shouldThrowNotFoundExceptionWhenEmailDoesNotExist() {
            // Given
            String nonExistentEmail = "nonexistent@example.com";
            when(profileEntityRepository.findByProfileEmail(nonExistentEmail))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> profileService.getByEmail(nonExistentEmail))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(nonExistentEmail);

            verify(profileEntityRepository, times(1)).findByProfileEmail(nonExistentEmail);
        }

        @Test
        @DisplayName("Should throw NullPointerException when email is null")
        void shouldThrowNullPointerExceptionWhenEmailIsNull() {
            // When & Then
            assertThatThrownBy(() -> profileService.getByEmail(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Email não pode ser null");

            verify(profileEntityRepository, never()).findByProfileEmail(any());
        }
    }

    @Nested
    @DisplayName("update() Tests")
    class UpdateTests {

        private ProfileRequestUpdate createUpdateRequest(String newEmail, String newPassword) {
            return ProfileRequestUpdate.builder()
                    .currentEmail(validEmail)
                    .newEmail(newEmail)
                    .currentPassword("oldPass")
                    .newPassword(newPassword)
                    .build();
        }

        @Test
        @DisplayName("Should update email successfully when new email is different and valid")
        void shouldUpdateEmailSuccessfullyWhenNewEmailIsDifferentAndValid() {
            // Given
            String newEmail = "newemail@example.com";
            ProfileRequestUpdate updateRequest = createUpdateRequest(newEmail, null);

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.findByProfileEmail(newEmail))
                    .thenReturn(Optional.empty());
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);

            // When
            ProfileResponse response = profileService.update(validProfileId, updateRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(mockProfileEntity.getProfileEmail()).isEqualTo(newEmail);
            assertThat(mockProfileEntity.getLastUpdated()).isNotNull();

            verify(emailValidator, times(1)).validate(newEmail);
            verify(profileEntityRepository, times(1)).findByProfileEmail(newEmail);
            verify(profileEntityRepository, times(1)).saveAndFlush(mockProfileEntity);
        }

        @Test
        @DisplayName("Should not update email when new email is same as current email")
        void shouldNotUpdateEmailWhenNewEmailIsSameAsCurrent() {
            // Given
            ProfileRequestUpdate updateRequest = createUpdateRequest(validEmail, null);

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);

            // When
            profileService.update(validProfileId, updateRequest);

            // Then
            assertThat(mockProfileEntity.getProfileEmail()).isEqualTo(validEmail);
            verify(emailValidator, never()).validate(anyString());
            verify(profileEntityRepository, never()).findByProfileEmail(anyString());
            verify(profileEntityRepository, times(1)).saveAndFlush(mockProfileEntity);
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when new email already exists")
        void shouldThrowDuplicateEmailExceptionWhenNewEmailAlreadyExists() {
            // Given
            String existingEmail = "existing@example.com";
            ProfileRequestUpdate updateRequest = createUpdateRequest(existingEmail, null);
            ProfileEntity existingProfileWithEmail = mockProfileEntity;

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.findByProfileEmail(existingEmail))
                    .thenReturn(Optional.of(existingProfileWithEmail));

            // When & Then
            assertThatThrownBy(() -> profileService.update(validProfileId, updateRequest))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining(existingEmail);

            verify(emailValidator, times(1)).validate(existingEmail);
            verify(profileEntityRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Should update password successfully when new password is provided")
        void shouldUpdatePasswordSuccessfullyWhenNewPasswordIsProvided() {
            // Given
            String newPassword = "newStrongPassword123";
            ProfileRequestUpdate updateRequest = createUpdateRequest(null, newPassword);
            String encodedPassword = "encodedNewPassword";

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);

            // When
            profileService.update(validProfileId, updateRequest);

            // Then
            assertThat(mockProfileEntity.getPassword()).isEqualTo(encodedPassword);
            assertThat(mockProfileEntity.getLastUpdated()).isNotNull();

            verify(passwordEncoder, times(1)).encode(newPassword);
            verify(profileEntityRepository, times(1)).saveAndFlush(mockProfileEntity);
        }

        @Test
        @DisplayName("Should update both email and password when both are provided")
        void shouldUpdateBothEmailAndPasswordWhenBothAreProvided() {
            // Given
            String newEmail = "newemail@example.com";
            String newPassword = "newPassword123";
            ProfileRequestUpdate updateRequest = createUpdateRequest(newEmail, newPassword);
            String encodedPassword = "encodedNewPassword";

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.findByProfileEmail(newEmail))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);

            // When
            profileService.update(validProfileId, updateRequest);

            // Then
            assertThat(mockProfileEntity.getProfileEmail()).isEqualTo(newEmail);
            assertThat(mockProfileEntity.getPassword()).isEqualTo(encodedPassword);

            verify(emailValidator).validate(newEmail);
            verify(passwordEncoder).encode(newPassword);
            verify(profileEntityRepository).saveAndFlush(mockProfileEntity);
        }

        @Test
        @DisplayName("Should throw NotFoundException when trying to update non-existent profile")
        void shouldThrowNotFoundExceptionWhenUpdatingNonExistentProfile() {
            // Given
            String nonExistentId = "non-existent-id";
            ProfileRequestUpdate updateRequest = createUpdateRequest("email@test.com", "password");

            when(profileEntityRepository.findById(nonExistentId))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> profileService.update(nonExistentId, updateRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(nonExistentId);

            verify(profileEntityRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Should not update email when newEmail is blank")
        void shouldNotUpdateEmailWhenNewEmailIsBlank() {
            // Given
            ProfileRequestUpdate updateRequest = createUpdateRequest("", null);

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);

            // When
            profileService.update(validProfileId, updateRequest);

            // Then
            assertThat(mockProfileEntity.getProfileEmail()).isEqualTo(validEmail);
            verify(emailValidator, never()).validate(anyString());
            verify(profileEntityRepository, never()).findByProfileEmail(anyString());
        }

        @Test
        @DisplayName("Should not update password when newPassword is blank")
        void shouldNotUpdatePasswordWhenNewPasswordIsBlank() {
            // Given
            ProfileRequestUpdate updateRequest = createUpdateRequest(null, "   ");

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);

            // When
            profileService.update(validProfileId, updateRequest);

            // Then
            verify(passwordEncoder, never()).encode(anyString());
            assertThat(mockProfileEntity.getPassword()).isEqualTo("encodedOldPassword");
        }

        @Test
        @DisplayName("Should call emailValidator.validate when email is being updated")
        void shouldCallEmailValidatorWhenEmailIsBeingUpdated() {
            // Given
            String newEmail = "validemail@example.com";
            ProfileRequestUpdate updateRequest = createUpdateRequest(newEmail, null);

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.findByProfileEmail(newEmail))
                    .thenReturn(Optional.empty());
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenReturn(mockProfileEntity);

            // When
            profileService.update(validProfileId, updateRequest);

            // Then
            verify(emailValidator, times(1)).validate(newEmail);
        }
    }

    @Nested
    @DisplayName("delete() Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete profile successfully when profile exists")
        void shouldDeleteProfileSuccessfullyWhenProfileExists() {
            // Given
            when(profileEntityRepository.existsById(validProfileId))
                    .thenReturn(true);
            doNothing().when(profileEntityRepository).deleteById(validProfileId);

            // When
            profileService.delete(validProfileId);

            // Then
            verify(profileEntityRepository, times(1)).existsById(validProfileId);
            verify(profileEntityRepository, times(1)).deleteById(validProfileId);
        }

        @Test
        @DisplayName("Should throw NotFoundException when trying to delete non-existent profile")
        void shouldThrowNotFoundExceptionWhenDeletingNonExistentProfile() {
            // Given
            String nonExistentId = "non-existent-id";
            when(profileEntityRepository.existsById(nonExistentId))
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> profileService.delete(nonExistentId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(nonExistentId);

            verify(profileEntityRepository, times(1)).existsById(nonExistentId);
            verify(profileEntityRepository, never()).deleteById(anyString());
        }
    }

    @Nested
    @DisplayName("Transaction and Integration Behavior Tests")
    class TransactionBehaviorTests {

        @Test
        @DisplayName("Should update lastUpdated timestamp when profile is modified")
        void shouldUpdateLastUpdatedTimestampWhenProfileIsModified() {
            // Given
            String newEmail = "updated@example.com";
            ProfileRequestUpdate updateRequest = ProfileRequestUpdate.builder()
                    .newEmail(newEmail)
                    .build();
            LocalDateTime previousLastUpdated = mockProfileEntity.getLastUpdated();

            when(profileEntityRepository.findById(validProfileId))
                    .thenReturn(Optional.of(mockProfileEntity));
            when(profileEntityRepository.findByProfileEmail(newEmail))
                    .thenReturn(Optional.empty());
            when(profileEntityRepository.saveAndFlush(any(ProfileEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            profileService.update(validProfileId, updateRequest);

            // Then
            ArgumentCaptor<ProfileEntity> entityCaptor = ArgumentCaptor.forClass(ProfileEntity.class);
            verify(profileEntityRepository).saveAndFlush(entityCaptor.capture());

            ProfileEntity savedEntity = entityCaptor.getValue();
            assertThat(savedEntity.getLastUpdated()).isAfter(previousLastUpdated);
        }
    }
}