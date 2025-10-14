package com.danilodps.pay.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ERole {
    ROLE_USER(1, "user"),
    ROLE_STORE(2, "store");

    @Getter
    private final int id;
    private final String shortName;

    ERole(int id, String shortName) {
        this.id = id;
        this.shortName = shortName;
    }

    @JsonValue
    public String getShortName() {
        return this.shortName;
    }

    @JsonCreator
    public static ERole fromShortName(String text) {
        if (text == null) {
            throw new IllegalArgumentException("O nome do perfil não pode ser nulo.");
        }

        return Arrays.stream(ERole.values())
                .filter(role -> role.getShortName().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum perfil encontrado para o texto: " + text));
    }

    private static final Map<Integer, ERole> BY_ID =
            Stream.of(values()).collect(Collectors.toMap(ERole::getId, e -> e));

    public static ERole fromId(int id) {
        ERole role = BY_ID.get(id);
        if (role == null) {
            throw new IllegalArgumentException("Nenhum perfil encontrado para o ID: " + id);
        }
        return role;
    }
}