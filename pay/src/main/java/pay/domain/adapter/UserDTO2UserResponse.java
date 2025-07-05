package pay.domain.adapter;

import pay.domain.dto.UserDTO;
import pay.domain.record.UserResponse;

public class UserDTO2UserResponse {

    private UserDTO2UserResponse() {}

    public static UserResponse convert(UserDTO userDTO){
        return new UserResponse(userDTO.getUserId(), userDTO.getUsername(), userDTO.getEmail(), userDTO.getBalance());
    }
}
