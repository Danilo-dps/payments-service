package com.danilodps.pay.application.usecases;

import com.danilodps.pay.adapters.inbound.controller.request.update.ProfileRequestUpdate;
import com.danilodps.pay.adapters.inbound.controller.response.ProfileResponse;

public interface ProfileUseCase {

    ProfileResponse getById(String profileId);
    ProfileResponse getByEmail(String profileEmail);
    ProfileResponse update(String profileId, ProfileRequestUpdate profileRequestUpdate);
    void delete(String profileId);
}
