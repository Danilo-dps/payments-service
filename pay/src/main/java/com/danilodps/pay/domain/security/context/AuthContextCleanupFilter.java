package com.danilodps.pay.domain.security.context;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuthContextCleanupFilter implements Filter {

    private final ClientContext clientContext;

    public AuthContextCleanupFilter(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            clientContext.clear();
        }
    }
}