package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.model.request.update.ProfileRequestUpdate;
import com.danilodps.pay.domain.model.response.operations.DepositResponse;
import com.danilodps.pay.domain.model.response.ProfileResponse;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    ProfileResponse getById(UUID profileId);
    ProfileResponse getByEmail(String profileEmail);
    ProfileResponse update(UUID profileId, ProfileRequestUpdate profileRequestUpdate);
    void delete(UUID profileId);
    List<DepositResponse> getAllDeposits(UUID profileId);
}
