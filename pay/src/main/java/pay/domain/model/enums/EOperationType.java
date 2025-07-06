package pay.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EOperationType {
    DEPOSIT("deposit"),
    TRANSFER("transfer");

    private final String shortName;

    EOperationType(String shortName) { this.shortName = shortName;}

    @JsonValue
    public String getShortName() { return this.shortName;}

    @JsonCreator
    public static EOperationType fromShortName(String text) {
        if (text == null) {
            throw new IllegalArgumentException("O nome do perfil não pode ser nulo.");
        }

        return Arrays.stream(EOperationType.values())
                .filter(op -> op.getShortName().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum perfil encontrado para o texto: " + text));
    }
}