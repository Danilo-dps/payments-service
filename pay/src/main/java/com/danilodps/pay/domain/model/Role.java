package com.danilodps.pay.domain.model;

import com.danilodps.pay.domain.model.enums.ERole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Entity
@Table(name = "tb_roles")
public class Role implements Serializable{
  @Serial
  private static final long serialVersionUID = 1L;

  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;

  public Role() {}

  @JsonCreator
  public static Role fromString(String value) {
    if (value == null) {
      return null;
    }
    return new Role(ERole.valueOf(value.toUpperCase()));
  }

  public Role(ERole name) {
    this.name = name;
  }

  @JsonValue
  public ERole getName() {
    return name;
  }

}