package com.danilodps.pay.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class RoleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private String docIdentifier;

    private String roleGrantedAuthority;

    private String description;

    public RoleEntity() {
    }

    public RoleEntity(Long roleId, String docIdentifier, String roleGrantedAuthority, String description) {
        this.roleId = roleId;
        this.docIdentifier = docIdentifier;
        this.roleGrantedAuthority = roleGrantedAuthority;
        this.description = description;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getDocIdentifier() {
        return docIdentifier;
    }

    public void setDocIdentifier(String docIdentifier) {
        this.docIdentifier = docIdentifier;
    }

    public String getRoleGrantedAuthority() {
        return roleGrantedAuthority;
    }

    public void setRoleGrantedAuthority(String roleGrantedAuthority) {
        this.roleGrantedAuthority = roleGrantedAuthority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoleEntity that)) return false;
        return Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roleId);
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "roleId=" + roleId +
                ", docIdentifier='" + docIdentifier + '\'' +
                ", roleGrantedAuthority='" + roleGrantedAuthority + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}