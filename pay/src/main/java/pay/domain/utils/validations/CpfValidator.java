package pay.domain.utils.validations;

import org.springframework.stereotype.Component;
import pay.application.exceptions.InvalidCPFException;
import pay.application.exceptions.UserCPFEmptyException;

import java.util.logging.Logger;

@Component
public class CpfValidator {
    private static final Logger logger = Logger.getLogger(CpfValidator.class.getName());

    private static final int CPF_LENGTH = 11;
    private static final String NON_DIGIT_REGEX = "[^0-9]";
    private static final String ERROR_CPF = "Erro. CPF inválido";

    public void validate(String cpf) {
        if (isNullOrEmpty(cpf)) {
            logger.warning("Erro. Nome está vazio");
            throw new UserCPFEmptyException();
        }

        String cleanedCpf = cleanCpf(cpf);

        if (!hasValidLength(cleanedCpf)) {
            logger.warning(ERROR_CPF);
            throw new InvalidCPFException(cpf);
        }

        if (areAllDigitsIdentical(cleanedCpf)) {
            logger.warning(ERROR_CPF);
            throw new InvalidCPFException(cpf);
        }

        if (!hasValidDigits(cleanedCpf)) {
            logger.warning(ERROR_CPF);
            throw new InvalidCPFException(cpf);
        }
    }

    private boolean isNullOrEmpty(String cpf) {
        return cpf == null || cpf.trim().isEmpty();
    }

    private String cleanCpf(String cpf) {
        return cpf.replaceAll(NON_DIGIT_REGEX, "");
    }

    private boolean hasValidLength(String cpf) {
        return cpf.length() == CPF_LENGTH;
    }

    private boolean areAllDigitsIdentical(String cpf) {
        if (cpf.length() < 1) {
            return false;
        }
        char firstDigit = cpf.charAt(0);
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != firstDigit) {
                return false;
            }
        }
        return true;
    }

    private boolean hasValidDigits(String cpf) {
        try {
            int[] digits = convertToDigitArray(cpf);
            return validateVerifierDigits(digits);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int[] convertToDigitArray(String cpf) {
        return cpf.chars()
                .map(Character::getNumericValue)
                .toArray();
    }

    private boolean validateVerifierDigits(int[] digits) {
        int firstExpectedDigit = calculateVerifierDigit(digits, 9);
        if (firstExpectedDigit != digits[9]) {
            return false;
        }

        int secondExpectedDigit = calculateVerifierDigit(digits, 10);
        return secondExpectedDigit == digits[10];
    }

    private int calculateVerifierDigit(int[] digits, int limit) {
        int sum = calculateWeightedSum(digits, limit);
        int remainder = calculateRemainder(sum);
        return determineVerifierDigit(remainder);
    }

    private int calculateWeightedSum(int[] digits, int limit) {
        int sum = 0;
        int weight = limit + 1;

        for (int i = 0; i < limit; i++) {
            sum += digits[i] * weight--;
        }
        return sum;
    }

    private int calculateRemainder(int sum) {
        return sum % 11;
    }

    private int determineVerifierDigit(int remainder) {
        return remainder < 2 ? 0 : 11 - remainder;
    }
}