package com.danilodps.pay.domain.security.context;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class ClientContext {
    private String username;
    private String token;
    @Getter
    private boolean authenticated;

    public void setCurrentUser(String username, String token) {
        this.username = username;
        this.token = token;
        this.authenticated = true;
    }

    public String getCurrentUsername() {
        if (!authenticated) {
            throw new IllegalStateException("No user authenticated in current context");
        }
        return username;
    }

    public String getCurrentToken() {
        if (!authenticated) {
            throw new IllegalStateException("No token available in current context");
        }
        return token;
    }

    public void clear() {
        this.username = null;
        this.token = null;
        this.authenticated = false;
    }
}