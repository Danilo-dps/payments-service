package pay.domain.utils.validations;

import org.springframework.stereotype.Component;
import pay.application.exceptions.InvalidCNPJException;
import pay.application.exceptions.StoreCNPJEmptyException;

import java.util.logging.Logger;

@Component
public class CnpjValidator {

    private static final Logger logger = Logger.getLogger(CnpjValidator.class.getName());

    private static final int CNPJ_LENGTH = 14;
    private static final String NON_DIGIT_REGEX = "[^0-9]";
    private static final String ERROR_CNPJ = "Erro. CNPJ inválido";

    public void validate(String cnpj) {
        if (isNullOrEmpty(cnpj)) {
            logger.warning(ERROR_CNPJ);
            throw new StoreCNPJEmptyException();
        }

        String cleanedCNPJ = cleanCNPJ(cnpj);

        if (!hasValidLength(cleanedCNPJ)) {
            logger.warning(ERROR_CNPJ);
            throw new InvalidCNPJException(cnpj);
        }

        if (areAllDigitsIdentical(cleanedCNPJ)) {
            logger.warning(ERROR_CNPJ);
            throw new InvalidCNPJException(cnpj);
        }

        if (!hasValidDigits(cleanedCNPJ)) {
            logger.warning(ERROR_CNPJ);
            throw new InvalidCNPJException(cnpj);
        }
    }

    private boolean isNullOrEmpty(String cnpj) {
        return cnpj == null || cnpj.trim().isEmpty();
    }

    private String cleanCNPJ(String cnpj) {
        return cnpj.replaceAll(NON_DIGIT_REGEX, "");
    }

    private boolean hasValidLength(String cnpj) {
        return cnpj.length() == CNPJ_LENGTH;
    }

    private boolean areAllDigitsIdentical(String cnpj) {
        if (cnpj.length() < 1) {
            return false;
        }
        char firstDigit = cnpj.charAt(0);
        for (int i = 1; i < cnpj.length(); i++) {
            if (cnpj.charAt(i) != firstDigit) {
                return false;
            }
        }
        return true;
    }

    private boolean hasValidDigits(String cnpj) {
        try {
            int[] digits = convertToDigitArray(cnpj);
            return validateVerifierDigits(digits);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int[] convertToDigitArray(String cnpj) {
        return cnpj.chars()
                .map(Character::getNumericValue)
                .toArray();
    }

    private boolean validateVerifierDigits(int[] digits) {
        int firstExpectedDigit = calculateFirstVerifierDigit(digits);
        if (firstExpectedDigit != digits[12]) {
            return false;
        }

        int secondExpectedDigit = calculateSecondVerifierDigit(digits);
        return secondExpectedDigit == digits[13];
    }

    private int calculateFirstVerifierDigit(int[] digits) {
        int[] weights = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return calculateVerifierDigit(digits, weights, 12);
    }

    private int calculateSecondVerifierDigit(int[] digits) {
        int[] weights = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return calculateVerifierDigit(digits, weights, 13);
    }

    private int calculateVerifierDigit(int[] digits, int[] weights, int digitPosition) {
        int sum = 0;

        for (int i = 0; i < weights.length; i++) {
            sum += digits[i] * weights[i];
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}



