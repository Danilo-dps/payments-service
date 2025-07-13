package pay.domain.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pay.domain.model.Store;
import pay.domain.model.User;
import pay.domain.repository.StoreRepository;
import pay.domain.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
  private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

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
    logger.info("Buscando User para: '{}'", trimmedUsername);

    Optional<User> user = userRepository.findByUsername(trimmedUsername);
    if (user.isPresent()) {
      logger.info("User encontrado: {}", user.get().getUsername());
      return new CustomUserDetails(user.get());
    }

    logger.info("Buscando Store para: '{}'", trimmedUsername);
    Optional<Store> store = storeRepository.findByStoreName(trimmedUsername);
    if (store.isPresent()) {
      logger.info("Store encontrada: {}", store.get().getStoreName());
      return new CustomUserDetails(store.get());
    }

    logger.error("NENHUM User ou Store encontrado para: '{}'", trimmedUsername);
    throw new UsernameNotFoundException("Usuário ou Loja não encontrado: " + trimmedUsername);
  }

}