package com.danilodps.pay.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_ROLES")
public class RoleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_DOC", length = 10)
    private String docIdentifier;

    @Column(name = "ROLE_NAME", length = 10)
    private String roleGrantedAuthority;

    @Column(name = "ROLE_DESCRIPTION", length = 25)
    private String description;

}