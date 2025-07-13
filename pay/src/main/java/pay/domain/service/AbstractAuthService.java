package pay.domain.service;

// package pay.domain.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pay.domain.payload.request.LoginRequest;
import pay.domain.payload.response.JwtResponse;
import pay.domain.security.jwt.JwtUtils;
import pay.domain.service.AuthService;
import pay.domain.service.impl.CustomUserDetails;

import java.util.List;
import java.util.stream.Collectors;

// A classe agora é abstrata
public abstract class AbstractAuthService<R, S> implements AuthService<R, S> {

    // Dependências comuns
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // Construtor para as classes filhas chamarem
    protected AbstractAuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Este método é comum e pode ser implementado aqui uma única vez.
     */
    @Override
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    /**
     * Forçamos as classes filhas a implementar sua própria lógica de registro.
     */
//    @Override
//    public abstract R register(S signupRequest);
}