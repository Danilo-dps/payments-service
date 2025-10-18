package com.danilodps.pay.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.model.request.LoginRequest;
import com.danilodps.pay.domain.model.response.JwtResponse;
import com.danilodps.pay.domain.model.request.SigninResponse;
import com.danilodps.pay.domain.security.jwt.JwtUtils;
import com.danilodps.pay.domain.service.impl.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public abstract class AbstractAuthService<R, S> implements AuthService<R, S> {

    private final AuthenticationManager authenticationManager;
    protected final KafkaEventProducer kafkaEventProducer;
    private final JwtUtils jwtUtils;

    protected AbstractAuthService(AuthenticationManager authenticationManager, KafkaEventProducer kafkaEventProducer, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.kafkaEventProducer = kafkaEventProducer;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        kafkaEventProducer.publishKafkaSignInNotification(SigninResponse.builder().id(userDetails.getId()).username(userDetails.getUsername()).email(userDetails.getEmail()).signinTimestamp(LocalDateTime.now()).build());
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

}