package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.dto.UserDTO;
import com.danilodps.pay.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getById(String userId);
    UserResponse getByEmail(String email);
    UserDTO update(String userId, UserResponse userResponse);
    void delete(String userId);
    List<DepositResponse> getAllDeposits(String userId);
}
