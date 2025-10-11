package pay.domain.adapter;

import pay.domain.model.User;
import pay.domain.model.response.UserResponse;

public class User2UserResponse {

    private User2UserResponse() {}

    public static UserResponse convert(User user){
        return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getBalance());
    }
}
