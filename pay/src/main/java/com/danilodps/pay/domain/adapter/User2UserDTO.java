package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.UserDTO;
import com.danilodps.pay.domain.model.User;

public class User2UserDTO {

    private User2UserDTO() {}

    public static UserDTO convert(User user){
        return new UserDTO(user.getUserId(),  user.getUsername(), user.getCpf(), user.getEmail(), user.getPassword(), user.getRole(), user.getBalance());
    }
}
