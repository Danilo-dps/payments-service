package com.danilodps.pay.domain.utils.validator;

import org.springframework.stereotype.Component;
import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.utils.validations.CnpjValidator;
import com.danilodps.pay.domain.utils.validations.EmailValidator;

@Component
public class StoreValidator {

    private final CnpjValidator cnpjValidator;
    private final EmailValidator emailValidator;

    public StoreValidator(CnpjValidator cnpjValidator, EmailValidator emailValidator) {
        this.cnpjValidator = cnpjValidator;
        this.emailValidator = emailValidator;
    }

    public void validate(StoreDTO storeDTO) {
        cnpjValidator.validate(storeDTO.getCnpj());
        emailValidator.validate(storeDTO.getStoreEmail());
    }
}
