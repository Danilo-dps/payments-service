package com.danilodps.pay.domain.service.spring;

import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.repository.ProfileEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

  private final ProfileEntityRepository profileEntityRepository;

  @Override
  @NullMarked
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Optional<ProfileEntity> profileEntity = Optional.ofNullable(profileEntityRepository.findByUsername(username)
              .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + username)));

      log.info("Usuário encontrado: {}", profileEntity.get().getUsername());
      return new CustomUserDetails(profileEntity.get());
  }

}