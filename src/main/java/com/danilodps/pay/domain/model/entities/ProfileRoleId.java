package com.danilodps.pay.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ProfileRoleId implements Serializable {

    @Column(name = "PROFILE_ID")
    private String profileId;

    @Column(name = "ROLE_ID")
    private Long roleId;

}