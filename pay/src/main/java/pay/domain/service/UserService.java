package pay.domain.service;

import pay.domain.dto.UserDTO;
import pay.domain.record.*;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID userId);
    UserResponse getByEmail(String email);
    UserDTO update(UUID userId, UserResponse userResponse);
    void delete(UUID userId);
    List<DepositResponse> getAllDeposits(UUID userId);
    List<ReceivedTransferResponse> getAllReceivedTransfers(UUID userId);
    List<SentTransferResponse> getAllSentTransfers(UUID userId);
}
