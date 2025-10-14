package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.User;
import com.danilodps.pay.domain.model.response.UserResponse;

public class User2UserResponse {

    private User2UserResponse() {}

    public static UserResponse convert(User user){
        return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getBalance());
    }
}
