package com.danilodps.pay.domain.model.request.create;

import com.danilodps.pay.domain.model.enums.RoleEnum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SignUpRequest(
        String username,
        String email,
        String password,
        String documentIdentifier,
        String document,
        List<RoleEnum> roleEnum,
        LocalDateTime signupTimestamp){}
