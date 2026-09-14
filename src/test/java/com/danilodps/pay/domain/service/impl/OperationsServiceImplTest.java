package com.danilodps.pay.domain.service.impl;

import com.danilodps.commons.application.exceptions.InsufficientBalanceException;
import com.danilodps.commons.application.exceptions.InvalidValueException;
import com.danilodps.commons.application.exceptions.NotFoundException;
import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import com.danilodps.pay.domain.model.*;
import com.danilodps.pay.infrastrucure.config.KafkaEventProducer;
import com.danilodps.pay.application.service.impl.OperationsServiceImpl;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.DepositRequest;
import com.danilodps.pay.adapters.inbound.controller.request.create.operations.TransactionRequest;
import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;
import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OperationsServiceImpl Tests")
class OperationsServiceImplTest {

    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @Mock
    private ProfileEntityRepository profileEntityRepository;

    @Mock
    private DepositEntityRepository depositEntityRepository;

    @Mock
    private TransactionEntityRepository transactionEntityRepository;

    @InjectMocks
    private OperationsServiceImpl operationsService;

    private ProfileEntity mockSenderProfile;
    private ProfileEntity mockReceiverProfile;
    private final String senderEmail = "sender@example.com";
    private final String receiverEmail = "receiver@example.com";
    private final String senderProfileId = "sender-123";
    private final String receiverProfileId = "receiver-456";
    private final BigDecimal senderInitialBalance = BigDecimal.valueOf(1500.00);
    private final BigDecimal receiverInitialBalance = BigDecimal.valueOf(500.00);
    private final BigDecimal transferAmount = BigDecimal.valueOf(200.00);
    private final BigDecimal depositAmount = BigDecimal.valueOf(500.00);
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {

        RoleEntity mockRoleEntity = new RoleEntity(1L, "ROLE_USER", "User role", "documentIdentifier");
        mockSenderProfile = new ProfileEntity(
                senderProfileId,
                "Sender User",
                "CPF",
                "",
                senderEmail,
                "password",
                senderInitialBalance,
                Collections.singletonList(mockRoleEntity),
                now.minusDays(30),
                now.minusDays(1));

        mockReceiverProfile = new ProfileEntity(
                receiverProfileId,
                "Receiver User",
                "CPF",
                "",
                receiverEmail,
                "encodedPassword",
                receiverInitialBalance,
                Collections.singletonList(mockRoleEntity),
                now.minusDays(30),
                now.minusDays(1));
    }

    @Nested
    @DisplayName("deposit() Tests")
    class DepositTests {

        @Test
        @DisplayName("Should successfully deposit amount to valid user")
        void shouldSuccessfullyDepositAmountToValidUser() {
            // Given
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(depositAmount)
                    .userEmail(senderEmail)
                    .build();

            DepositEntity mockDepositEntity = new DepositEntity(
                    UUID.randomUUID().toString(),
                    now,
                    depositAmount,
                    mockSenderProfile);

            when(profileEntityRepository.findByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(depositEntityRepository.save(any(DepositEntity.class)))
                    .thenReturn(mockDepositEntity);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile);
            doNothing().when(kafkaEventProducer).publishDepositEventNotification(any(DepositResponse.class));

            // When
            DepositResponse response = operationsService.deposit(depositRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.amount()).isEqualByComparingTo(depositAmount);
            assertThat(response.userEmail()).isEqualTo(senderEmail);
            assertThat(response.username()).isEqualTo("Sender User");
            assertThat(response.depositId()).isNotNull();
            assertThat(response.depositTimestamp()).isNotNull();

            assertThat(mockSenderProfile.getBalance())
                    .isEqualByComparingTo(senderInitialBalance.add(depositAmount));

            verify(profileEntityRepository, times(1)).findByProfileEmail(senderEmail);
            verify(depositEntityRepository, times(1)).save(any(DepositEntity.class));
            verify(profileEntityRepository, times(1)).save(mockSenderProfile);
            verify(kafkaEventProducer, times(1)).publishDepositEventNotification(any(DepositResponse.class));
        }

        @Test
        @DisplayName("Should throw InvalidValueException when amount is null")
        void shouldThrowInvalidValueExceptionWhenAmountIsNull() {
            // Given
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(null)
                    .userEmail(senderEmail)
                    .build();

            // When & Then
            assertThatThrownBy(() -> operationsService.deposit(depositRequest))
                    .isInstanceOf(InvalidValueException.class);

            verify(profileEntityRepository, never()).findByProfileEmail(anyString());
            verify(depositEntityRepository, never()).save(any());
            verify(kafkaEventProducer, never()).publishDepositEventNotification(any());
        }

        @Test
        @DisplayName("Should throw InvalidValueException when amount is zero")
        void shouldThrowInvalidValueExceptionWhenAmountIsZero() {
            // Given
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(BigDecimal.ZERO)
                    .userEmail(senderEmail)
                    .build();

            // When & Then
            assertThatThrownBy(() -> operationsService.deposit(depositRequest))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        @DisplayName("Should throw InvalidValueException when amount is negative")
        void shouldThrowInvalidValueExceptionWhenAmountIsNegative() {
            // Given
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(BigDecimal.valueOf(-100))
                    .userEmail(senderEmail)
                    .build();

            // When & Then
            assertThatThrownBy(() -> operationsService.deposit(depositRequest))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user email does not exist")
        void shouldThrowNotFoundExceptionWhenUserEmailDoesNotExist() {
            // Given
            String nonExistentEmail = "nonexistent@example.com";
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(depositAmount)
                    .userEmail(nonExistentEmail)
                    .build();

            when(profileEntityRepository.findByProfileEmail(nonExistentEmail))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> operationsService.deposit(depositRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(nonExistentEmail);

            verify(profileEntityRepository, times(1)).findByProfileEmail(nonExistentEmail);
            verify(depositEntityRepository, never()).save(any());
            verify(profileEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create DepositEntity with correct values")
        void shouldCreateDepositEntityWithCorrectValues() {
            // Given
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(depositAmount)
                    .userEmail(senderEmail)
                    .build();

            when(profileEntityRepository.findByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(depositEntityRepository.save(any(DepositEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile);

            // When
            operationsService.deposit(depositRequest);

            // Then
            ArgumentCaptor<DepositEntity> depositCaptor = ArgumentCaptor.forClass(DepositEntity.class);
            verify(depositEntityRepository).save(depositCaptor.capture());

            DepositEntity capturedDeposit = depositCaptor.getValue();
            assertThat(capturedDeposit.getAmount()).isEqualByComparingTo(depositAmount);
            assertThat(capturedDeposit.getProfileEntity()).isEqualTo(mockSenderProfile);
            assertThat(capturedDeposit.getDepositId()).isNotNull();
            assertThat(capturedDeposit.getDepositAt()).isNotNull();
        }

        @Test
        @DisplayName("Should send Kafka event with correct DepositResponse")
        void shouldSendKafkaEventWithCorrectDepositResponse() {
            // Given
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(depositAmount)
                    .userEmail(senderEmail)
                    .build();

            DepositEntity mockDepositEntity = new DepositEntity(
                    "deposit-123",
                    now,
                    depositAmount,
                    mockSenderProfile);

            when(profileEntityRepository.findByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(depositEntityRepository.save(any(DepositEntity.class)))
                    .thenReturn(mockDepositEntity);
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile);

            ArgumentCaptor<DepositResponse> kafkaCaptor = ArgumentCaptor.forClass(DepositResponse.class);

            // When
            operationsService.deposit(depositRequest);

            // Then
            verify(kafkaEventProducer).publishDepositEventNotification(kafkaCaptor.capture());
            DepositResponse kafkaResponse = kafkaCaptor.getValue();

            assertThat(kafkaResponse.amount()).isEqualByComparingTo(depositAmount);
            assertThat(kafkaResponse.userEmail()).isEqualTo(senderEmail);
            assertThat(kafkaResponse.username()).isEqualTo("Sender User");
        }
    }

    @Nested
    @DisplayName("transfer() Tests")
    class TransferTests {

        @Test
        @DisplayName("Should successfully transfer amount between valid users")
        void shouldSuccessfullyTransferAmountBetweenValidUsers() {
            // Given
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(transferAmount)
                    .build();

            TransactionEntity mockTransactionEntity = new TransactionEntity(
                    UUID.randomUUID().toString(),
                    transferAmount,
                    now,
                    mockSenderProfile,
                    mockReceiverProfile);

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));
            when(profileEntityRepository.save(mockSenderProfile))
                    .thenReturn(mockSenderProfile);
            when(profileEntityRepository.save(mockReceiverProfile))
                    .thenReturn(mockReceiverProfile);
            when(transactionEntityRepository.save(any(TransactionEntity.class)))
                    .thenReturn(mockTransactionEntity);
            doNothing().when(kafkaEventProducer).publishTransferEventNotification(any(TransactionResponse.class));

            // When
            TransactionResponse response = operationsService.transfer(transactionRequest);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.amount()).isEqualByComparingTo(transferAmount);
            assertThat(response.senderEmail()).isEqualTo(senderEmail);
            assertThat(response.receiverEmail()).isEqualTo(receiverEmail);
            assertThat(response.transactionId()).isNotNull();
            assertThat(response.transactionTimestamp()).isNotNull();

            assertThat(mockSenderProfile.getBalance())
                    .isEqualByComparingTo(senderInitialBalance.subtract(transferAmount));
            assertThat(mockReceiverProfile.getBalance())
                    .isEqualByComparingTo(receiverInitialBalance.add(transferAmount));

            verify(profileEntityRepository, times(1)).findAndLockByProfileEmail(senderEmail);
            verify(profileEntityRepository, times(1)).findAndLockByProfileEmail(receiverEmail);
            verify(profileEntityRepository, times(1)).save(mockSenderProfile);
            verify(profileEntityRepository, times(1)).save(mockReceiverProfile);
            verify(transactionEntityRepository, times(1)).save(any(TransactionEntity.class));
            verify(kafkaEventProducer, times(1)).publishTransferEventNotification(any(TransactionResponse.class));
        }

        @Test
        @DisplayName("Should throw InvalidValueException when transfer amount is zero")
        void shouldThrowInvalidValueExceptionWhenTransferAmountIsZero() {
            // Given
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(BigDecimal.ZERO)
                    .build();

            // When & Then
            assertThatThrownBy(() -> operationsService.transfer(transactionRequest))
                    .isInstanceOf(InvalidValueException.class);

            verify(profileEntityRepository, never()).findAndLockByProfileEmail(anyString());
        }

        @Test
        @DisplayName("Should throw InvalidValueException when transfer amount is negative")
        void shouldThrowInvalidValueExceptionWhenTransferAmountIsNegative() {
            // Given
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(BigDecimal.valueOf(-100))
                    .build();

            // When & Then
            assertThatThrownBy(() -> operationsService.transfer(transactionRequest))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        @DisplayName("Should throw NotFoundException when sender does not exist")
        void shouldThrowNotFoundExceptionWhenSenderDoesNotExist() {
            // Given
            String nonExistentSender = "nonexistent@example.com";
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(nonExistentSender)
                    .receiverEmail(receiverEmail)
                    .amount(transferAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(nonExistentSender))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> operationsService.transfer(transactionRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("remetente")
                    .hasMessageContaining(nonExistentSender);

            verify(profileEntityRepository, times(1)).findAndLockByProfileEmail(nonExistentSender);
            verify(transactionEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw NotFoundException when receiver does not exist")
        void shouldThrowNotFoundExceptionWhenReceiverDoesNotExist() {
            // Given
            String nonExistentReceiver = "nonexistent@example.com";
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(nonExistentReceiver)
                    .amount(transferAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(nonExistentReceiver))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> operationsService.transfer(transactionRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("remetente")
                    .hasMessageContaining(nonExistentReceiver);

            verify(profileEntityRepository, times(1)).findAndLockByProfileEmail(senderEmail);
            verify(profileEntityRepository, times(1)).findAndLockByProfileEmail(nonExistentReceiver);
            verify(transactionEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InsufficientBalanceException when sender has insufficient balance")
        void shouldThrowInsufficientBalanceExceptionWhenSenderHasInsufficientBalance() {
            // Given
            BigDecimal largeAmount = BigDecimal.valueOf(2000.00);
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(largeAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));

            // When & Then
            assertThatThrownBy(() -> operationsService.transfer(transactionRequest))
                    .isInstanceOf(InsufficientBalanceException.class);

            verify(profileEntityRepository, never()).save(any());
            verify(transactionEntityRepository, never()).save(any());
            verify(kafkaEventProducer, never()).publishTransferEventNotification(any());
        }

        @Test
        @DisplayName("Should create TransactionEntity with correct values")
        void shouldCreateTransactionEntityWithCorrectValues() {
            // Given
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(transferAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile, mockReceiverProfile);
            when(transactionEntityRepository.save(any(TransactionEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            operationsService.transfer(transactionRequest);

            // Then
            ArgumentCaptor<TransactionEntity> transactionCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
            verify(transactionEntityRepository).save(transactionCaptor.capture());

            TransactionEntity capturedTransaction = transactionCaptor.getValue();
            assertThat(capturedTransaction.getAmount()).isEqualByComparingTo(transferAmount);
            assertThat(capturedTransaction.getProfileSender()).isEqualTo(mockSenderProfile);
            assertThat(capturedTransaction.getProfileReceiver()).isEqualTo(mockReceiverProfile);
            assertThat(capturedTransaction.getTransactionId()).isNotNull();
            assertThat(capturedTransaction.getTransactionAt()).isNotNull();
        }

        @Test
        @DisplayName("Should use pessimistic lock for sender when transferring")
        void shouldUsePessimisticLockForSenderWhenTransferring() {
            // Given
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(transferAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile, mockReceiverProfile);
            when(transactionEntityRepository.save(any(TransactionEntity.class)))
                    .thenReturn(mock(TransactionEntity.class));

            // When
            operationsService.transfer(transactionRequest);

            // Then
            verify(profileEntityRepository, atLeastOnce()).findAndLockByProfileEmail(senderEmail);
        }

        @Test
        @DisplayName("Should send Kafka event with correct TransactionResponse")
        void shouldSendKafkaEventWithCorrectTransactionResponse() {
            // Given
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(transferAmount)
                    .build();

            TransactionEntity mockTransactionEntity = new TransactionEntity(
                    "d3464bd7-9010-42aa-aa87-b085bcf0c117",
                    transferAmount,
                    now,
                    mockSenderProfile,
                   mockReceiverProfile);

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile, mockReceiverProfile);
            when(transactionEntityRepository.save(any(TransactionEntity.class)))
                    .thenReturn(mockTransactionEntity);

            ArgumentCaptor<TransactionResponse> kafkaCaptor = ArgumentCaptor.forClass(TransactionResponse.class);

            // When
            operationsService.transfer(transactionRequest);

            // Then
            verify(kafkaEventProducer).publishTransferEventNotification(kafkaCaptor.capture());
            TransactionResponse kafkaResponse = kafkaCaptor.getValue();

            assertThat(kafkaResponse.amount()).isEqualByComparingTo(transferAmount);
            assertThat(kafkaResponse.senderEmail()).isEqualTo(senderEmail);
            assertThat(kafkaResponse.receiverEmail()).isEqualTo(receiverEmail);
        }
    }

    @Nested
    @DisplayName("getAllDeposits() Tests")
    class GetAllDepositsTests {

        @Test
        @DisplayName("Should return list of deposits for valid profileId")
        void shouldReturnListOfDepositsForValidProfileId() {
            // Given
            String profileId = senderProfileId;
            DepositProjection mockProjection = mock(DepositProjection.class);
            List<DepositProjection> mockDeposits = List.of(mockProjection);

            when(depositEntityRepository.findDepositsByProfileId(profileId))
                    .thenReturn(mockDeposits);

            // When
            List<DepositProjection> result = operationsService.getAllDeposits(profileId);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(mockDeposits);
            verify(depositEntityRepository, times(1)).findDepositsByProfileId(profileId);
        }

        @Test
        @DisplayName("Should return empty list when profile has no deposits")
        void shouldReturnEmptyListWhenProfileHasNoDeposits() {
            // Given
            String profileId = senderProfileId;
            when(depositEntityRepository.findDepositsByProfileId(profileId))
                    .thenReturn(List.of());

            // When
            List<DepositProjection> result = operationsService.getAllDeposits(profileId);

            // Then
            assertThat(result).isEmpty();
            verify(depositEntityRepository, times(1)).findDepositsByProfileId(profileId);
        }

        @Test
        @DisplayName("Should pass correct profileId to repositories")
        void shouldPassCorrectProfileIdToRepository() {
            // Given
            String profileId = senderProfileId;
            when(depositEntityRepository.findDepositsByProfileId(profileId))
                    .thenReturn(List.of());

            // When
            operationsService.getAllDeposits(profileId);

            // Then
            verify(depositEntityRepository, times(1)).findDepositsByProfileId(profileId);
        }

        @Test
        @DisplayName("Should return DepositProjection with correct fields")
        void shouldReturnDepositProjectionWithCorrectFields() {
            // Given
            String profileId = senderProfileId;
            String depositId = "deposit-123";
            BigDecimal amount = BigDecimal.valueOf(500);
            LocalDateTime depositAt = now;

            DepositProjection mockProjection = new DepositProjection() {
                @Override public String getDepositId() { return depositId; }
                @Override public LocalDateTime getDepositAt() { return depositAt; }
                @Override public BigDecimal getAmount() { return amount; }
            };

            when(depositEntityRepository.findDepositsByProfileId(profileId))
                    .thenReturn(List.of(mockProjection));

            // When
            List<DepositProjection> result = operationsService.getAllDeposits(profileId);

            // Then
            assertThat(result).hasSize(1);
            DepositProjection projection = result.get(0);
            assertThat(projection.getDepositId()).isEqualTo(depositId);
            assertThat(projection.getAmount()).isEqualByComparingTo(amount);
            assertThat(projection.getDepositAt()).isEqualTo(depositAt);
        }
    }

    @Nested
    @DisplayName("getAllTransactions() Tests")
    class GetAllTransactionsTests {

        @Test
        @DisplayName("Should return list of transactions for valid profileId")
        void shouldReturnListOfTransactionsForValidProfileId() {
            // Given
            String profileId = senderProfileId;
            TransactionProjection mockProjection = mock(TransactionProjection.class);
            List<TransactionProjection> mockTransactions = List.of(mockProjection);

            when(transactionEntityRepository.findAll(profileId))
                    .thenReturn(mockTransactions);

            // When
            List<TransactionProjection> result = operationsService.getAllTransactions(profileId);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(mockTransactions);
            verify(transactionEntityRepository, times(1)).findAll(profileId);
        }

        @Test
        @DisplayName("Should return empty list when profile has no transactions")
        void shouldReturnEmptyListWhenProfileHasNoTransactions() {
            // Given
            String profileId = senderProfileId;
            when(transactionEntityRepository.findAll(profileId))
                    .thenReturn(List.of());

            // When
            List<TransactionProjection> result = operationsService.getAllTransactions(profileId);

            // Then
            assertThat(result).isEmpty();
            verify(transactionEntityRepository, times(1)).findAll(profileId);
        }

        @Test
        @DisplayName("Should pass correct profileId to repositories")
        void shouldPassCorrectProfileIdToRepository() {
            // Given
            String profileId = senderProfileId;
            when(transactionEntityRepository.findAll(profileId))
                    .thenReturn(List.of());

            // When
            operationsService.getAllTransactions(profileId);

            // Then
            verify(transactionEntityRepository, times(1)).findAll(profileId);
        }

        @Test
        @DisplayName("Should return TransactionProjection with correct fields")
        void shouldReturnTransactionProjectionWithCorrectFields() {
            // Given
            String profileId = senderProfileId;
            String transactionId = "transaction-123";
            String receiverId = "receiver-456";
            BigDecimal amount = BigDecimal.valueOf(200);
            LocalDateTime transactionAt = now;

            TransactionProjection mockProjection = new TransactionProjection() {
                @Override public String getTransactionId() { return transactionId; }
                @Override public String getProfileReceiver() { return receiverId; }
                @Override public LocalDateTime getTransactionAt() { return transactionAt; }
                @Override public BigDecimal getAmount() { return amount; }
            };

            when(transactionEntityRepository.findAll(profileId))
                    .thenReturn(List.of(mockProjection));

            // When
            List<TransactionProjection> result = operationsService.getAllTransactions(profileId);

            // Then
            assertThat(result).hasSize(1);
            TransactionProjection projection = result.get(0);
            assertThat(projection.getTransactionId()).isEqualTo(transactionId);
            assertThat(projection.getProfileReceiver()).isEqualTo(receiverId);
            assertThat(projection.getAmount()).isEqualByComparingTo(amount);
            assertThat(projection.getTransactionAt()).isEqualTo(transactionAt);
        }
    }

    @Nested
    @DisplayName("Transaction & Integration Tests")
    class TransactionIntegrationTests {

        @Test
        @DisplayName("Should update both sender and receiver balances atomically")
        void shouldUpdateBothSenderAndReceiverBalancesAtomically() {
            // Given
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(transferAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile, mockReceiverProfile);
            when(transactionEntityRepository.save(any(TransactionEntity.class)))
                    .thenReturn(mock(TransactionEntity.class));

            // When
            operationsService.transfer(transactionRequest);

            // Then
            assertThat(mockSenderProfile.getBalance())
                    .isEqualByComparingTo(senderInitialBalance.subtract(transferAmount));
            assertThat(mockReceiverProfile.getBalance())
                    .isEqualByComparingTo(receiverInitialBalance.add(transferAmount));

            verify(profileEntityRepository, times(1)).save(mockSenderProfile);
            verify(profileEntityRepository, times(1)).save(mockReceiverProfile);
        }

        @Test
        @DisplayName("Should not update any balance when transfer fails")
        void shouldNotUpdateAnyBalanceWhenTransferFails() {
            // Given
            BigDecimal largeAmount = BigDecimal.valueOf(2000.00);
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(largeAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));

            BigDecimal senderBalanceBefore = mockSenderProfile.getBalance();
            BigDecimal receiverBalanceBefore = mockReceiverProfile.getBalance();

            // When & Then
            assertThatThrownBy(() -> operationsService.transfer(transactionRequest))
                    .isInstanceOf(InsufficientBalanceException.class);

            assertThat(mockSenderProfile.getBalance()).isEqualByComparingTo(senderBalanceBefore);
            assertThat(mockReceiverProfile.getBalance()).isEqualByComparingTo(receiverBalanceBefore);

            verify(profileEntityRepository, never()).save(any());
            verify(transactionEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should rollback transaction when exception occurs during deposit")
        void shouldRollbackTransactionWhenExceptionOccursDuringDeposit() {
            // Given
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(depositAmount)
                    .userEmail(senderEmail)
                    .build();

            when(profileEntityRepository.findByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));

            // Simula exceção no save do depósito
            when(depositEntityRepository.save(any(DepositEntity.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatThrownBy(() -> operationsService.deposit(depositRequest))
                    .isInstanceOf(RuntimeException.class);

            verify(profileEntityRepository, never()).save(any());

            // para testar o rollback real, precisa de um teste de integração com banco H2
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle exact balance transfer correctly")
        void shouldHandleExactBalanceTransferCorrectly() {
            // Given
            BigDecimal exactAmount = senderInitialBalance;
            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .senderEmail(senderEmail)
                    .receiverEmail(receiverEmail)
                    .amount(exactAmount)
                    .build();

            when(profileEntityRepository.findAndLockByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(profileEntityRepository.findAndLockByProfileEmail(receiverEmail))
                    .thenReturn(Optional.of(mockReceiverProfile));
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile, mockReceiverProfile);
            when(transactionEntityRepository.save(any(TransactionEntity.class)))
                    .thenReturn(mock(TransactionEntity.class));

            // When
            operationsService.transfer(transactionRequest);

            // Then
            assertThat(mockSenderProfile.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(mockReceiverProfile.getBalance())
                    .isEqualByComparingTo(receiverInitialBalance.add(exactAmount));
        }

        @Test
        @DisplayName("Should handle deposit of very large amount")
        void shouldHandleDepositOfVeryLargeAmount() {
            // Given
            BigDecimal largeAmount = new BigDecimal("999999999999.99");
            DepositRequest depositRequest = DepositRequest.builder()
                    .amount(largeAmount)
                    .userEmail(senderEmail)
                    .build();

            when(profileEntityRepository.findByProfileEmail(senderEmail))
                    .thenReturn(Optional.of(mockSenderProfile));
            when(depositEntityRepository.save(any(DepositEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(profileEntityRepository.save(any(ProfileEntity.class)))
                    .thenReturn(mockSenderProfile);

            // When
            operationsService.deposit(depositRequest);

            // Then
            assertThat(mockSenderProfile.getBalance())
                    .isEqualByComparingTo(senderInitialBalance.add(largeAmount));
        }
    }
}