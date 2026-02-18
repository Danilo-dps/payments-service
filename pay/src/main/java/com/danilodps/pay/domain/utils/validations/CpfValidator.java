package com.danilodps.pay.domain.utils.validations;

import com.danilodps.commons.application.exceptions.InvalidCPFException;
import com.danilodps.commons.application.exceptions.UserCPFEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class CpfValidator {

    private static final int CPF_LENGTH = 11;
    private static final String NON_DIGIT_REGEX = "[^0-9]";
    private static final String ERROR_CPF = "Erro. CPF inválido";

    public void validate(String cpf) {
        if (isNullOrEmpty(cpf)) {
            log.warn("Erro. Nome está vazio");
            throw new UserCPFEmptyException();
        }

        String cleanedCpf = cleanCpf(cpf);

        if (!hasValidLength(cleanedCpf)) {
            log.warn(ERROR_CPF);
            throw new InvalidCPFException(cpf);
        }

        if (areAllDigitsIdentical(cleanedCpf)) {
            log.warn(ERROR_CPF);
            throw new InvalidCPFException(cpf);
        }

        if (!hasValidDigits(cleanedCpf)) {
            log.warn(ERROR_CPF);
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
        if (cpf.length() < 11) {
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