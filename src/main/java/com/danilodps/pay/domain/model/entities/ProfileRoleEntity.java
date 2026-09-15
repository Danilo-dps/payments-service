package com.danilodps.pay.domain.model.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_PROFILE_ROLES")
@EqualsAndHashCode(of = "id")
public class ProfileRoleEntity {

    @EmbeddedId
    private ProfileRoleId id;

}