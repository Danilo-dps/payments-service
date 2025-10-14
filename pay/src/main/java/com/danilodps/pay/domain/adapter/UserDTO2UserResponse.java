package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.UserDTO;
import com.danilodps.pay.domain.model.response.UserResponse;

public class UserDTO2UserResponse {

    private UserDTO2UserResponse() {}

    public static UserResponse convert(UserDTO userDTO){
        return new UserResponse(userDTO.getUserId(), userDTO.getUsername(), userDTO.getEmail(), userDTO.getBalance());
    }
}
