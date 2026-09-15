package com.danilodps.pay.infrastrucure.spring;

import com.danilodps.pay.domain.model.entities.ProfileEntity;
import com.danilodps.pay.domain.model.entities.RoleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String profileId;
    private final String username;
    private final String profileEmail;
    @JsonIgnore
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(ProfileEntity profileEntity, List<RoleEntity> roles) {
        this.profileId = profileEntity.getProfileId();
        this.username = profileEntity.getUsername();
        this.profileEmail = profileEntity.getProfileEmail();
        this.password = profileEntity.getPassword();
        this.authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleGrantedAuthority()))
                .collect(Collectors.toList());
    }

}