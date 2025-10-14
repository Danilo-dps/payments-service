package com.danilodps.pay.domain.service.impl;

import com.danilodps.pay.domain.model.Role;
import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final String id;
    private final String username;
    @Getter
    private final String email;

    @JsonIgnore
    private String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        for (Role role : user.getRole()) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getName().name());
            list.add(simpleGrantedAuthority);
        }
        this.authorities = list;
    }

    public CustomUserDetails(Store store) {
        this.id = store.getStoreId();
        this.username = store.getStoreName();
        this.email = store.getStoreEmail();
        this.password = store.getPassword();
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        for (Role role : store.getRole()) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getName().name());
            list.add(simpleGrantedAuthority);
        }
        this.authorities = list;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}