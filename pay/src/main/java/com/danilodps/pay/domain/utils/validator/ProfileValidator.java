package com.danilodps.pay.domain.utils.validator;

import com.danilodps.pay.domain.model.enums.DocumentTypeEnum;
import com.danilodps.pay.domain.utils.validations.CnpjValidator;
import com.danilodps.pay.domain.utils.validations.CpfValidator;
import com.danilodps.pay.domain.utils.validations.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileValidator {

    private final CpfValidator cpfValidator;
    private final CnpjValidator cnpjValidator;
    private final EmailValidator emailValidator;

    public void validate(String email, String documentIdentifier, String document) {
        whichDocument(documentIdentifier, document);
        emailValidator.validate(email);
    }

    public void whichDocument(String documentIdentifier, String document){
        if(documentIdentifier.equals(DocumentTypeEnum.CPF.getShortName())){
            log.info("Validando CPF");
            cpfValidator.validate(document);
        }
        else{
            log.info("Validando CNPJ");
            cnpjValidator.validate(document);
        }
    }

}
