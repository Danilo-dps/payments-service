package com.danilodps.pay.domain.service.spring;

import com.danilodps.pay.domain.model.ProfileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
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

    public UserDetailsImpl(ProfileEntity profileEntity) {
        this.profileId = profileEntity.getProfileId();
        this.username = profileEntity.getUsername();
        this.profileEmail = profileEntity.getProfileEmail();
        this.password = profileEntity.getPassword();
        this.authorities = profileEntity.getRoles().stream()
                .map(roleEntity -> new SimpleGrantedAuthority(roleEntity.getRoleGrantedAuthority()))
                .collect(Collectors.toList());
    }

}