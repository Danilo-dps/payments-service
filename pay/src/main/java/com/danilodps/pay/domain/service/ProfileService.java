package com.danilodps.pay.domain.service;

import com.danilodps.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.request.update.ProfileRequestUpdate;
import com.danilodps.pay.domain.model.response.ProfileResponse;

import java.util.List;

public interface ProfileService {

    ProfileResponse getById(String profileId);
    ProfileResponse getByEmail(String profileEmail);
    ProfileResponse update(String profileId, ProfileRequestUpdate profileRequestUpdate);
    void delete(String profileId);
    List<DepositResponse> getAllDeposits(String profileId);
}
