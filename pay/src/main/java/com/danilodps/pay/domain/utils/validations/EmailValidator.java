package com.danilodps.pay.domain.utils.validations;

import com.danilodps.application.exceptions.EmailEmptyException;
import com.danilodps.application.exceptions.InvalidEmailException;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;
import java.util.regex.Pattern;

@Component
public class EmailValidator {
    private static final Logger logger = Logger.getLogger(EmailValidator.class.getName());

    private static final String ERROR_EMAIL = "Erro. Email inválido";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public void validate(String email) {
        if (isNullOrEmpty(email)) {
            logger.warning(ERROR_EMAIL);
            throw new EmailEmptyException();
        }

        if (hasInvalidStructure(email)) {
            logger.warning(ERROR_EMAIL);
            throw new InvalidEmailException(email);
        }

        if (!matchesPattern(email)) {
            logger.warning(ERROR_EMAIL);
            throw new InvalidEmailException(email);
        }
    }

    private boolean isNullOrEmpty(String email) {
        return email == null || email.trim().isEmpty();
    }

    private boolean hasInvalidStructure(String email) {
        return email.contains("@.") ||
                email.endsWith(".") ||
                email.startsWith(".") ||
                email.indexOf('@') < 1;
    }

    private boolean matchesPattern(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
