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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final ProfileEntityRepository profileEntityRepository;

  @Override
  @NullMarked
  public UserDetails loadUserByUsername(String profileEmail) throws UsernameNotFoundException {
      ProfileEntity profileEntity = profileEntityRepository.findByProfileEmail(profileEmail)
              .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + profileEmail));

      log.info("Usuário encontrado: {}", profileEntity.getProfileEmail());
      return new UserDetailsImpl(profileEntity);
  }

}