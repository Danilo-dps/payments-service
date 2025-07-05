package pay.domain.utils.validator;

import org.springframework.stereotype.Component;
import pay.domain.dto.UserDTO;
import pay.domain.utils.validations.CpfValidator;
import pay.domain.utils.validations.EmailValidator;

@Component
public class UserValidator {

    private final CpfValidator cpfValidator;
    private final EmailValidator emailValidator;

    public UserValidator(CpfValidator cpfValidator, EmailValidator emailValidator) {
        this.cpfValidator = cpfValidator;
        this.emailValidator = emailValidator;
    }

    public void validate(UserDTO userDTO) {
        cpfValidator.validate(userDTO.getCpf());
        emailValidator.validate(userDTO.getEmail());
    }
}
