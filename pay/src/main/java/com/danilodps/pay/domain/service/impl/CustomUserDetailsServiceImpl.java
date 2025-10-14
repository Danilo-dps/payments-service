package com.danilodps.pay.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.User;
import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;
  private final StoreRepository storeRepository;

  public CustomUserDetailsServiceImpl(UserRepository userRepository, StoreRepository storeRepository) {
    this.userRepository = userRepository;
    this.storeRepository = storeRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String trimmedUsername = username.trim();
    log.info("Buscando User para: '{}'", trimmedUsername);

    Optional<User> user = userRepository.findByUsername(trimmedUsername);
    if (user.isPresent()) {
      log.info("User encontrado: {}", user.get().getUsername());
      return new CustomUserDetails(user.get());
    }

    log.info("Buscando Store para: '{}'", trimmedUsername);
    Optional<Store> store = storeRepository.findByStoreName(trimmedUsername);
    if (store.isPresent()) {
      log.info("Store encontrada: {}", store.get().getStoreName());
      return new CustomUserDetails(store.get());
    }

    log.error("NENHUM User ou Store encontrado para: '{}'", trimmedUsername);
    throw new UsernameNotFoundException("Usuário ou Loja não encontrado: " + trimmedUsername);
  }

}