package com.danilodps.pay.domain.model;

public interface RoleEntityRepository {

    RoleEntity findByRoleId(Long roleId);
}
