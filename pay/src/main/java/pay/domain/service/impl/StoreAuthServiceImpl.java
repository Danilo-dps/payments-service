package pay.domain.service.impl;


import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pay.domain.adapter.Store2StoreDTO;
import pay.domain.dto.StoreDTO;
import pay.domain.model.Role;
import pay.domain.model.Store;
import pay.domain.model.enums.ERole;
import pay.domain.repository.RoleRepository;
import pay.domain.repository.StoreRepository;
import pay.domain.security.jwt.JwtUtils;
import pay.domain.service.AbstractAuthService;

import java.util.Set;
import java.util.stream.Collectors;

@Service("storeAuthService")
public class StoreAuthServiceImpl extends AbstractAuthService<StoreDTO, StoreDTO> {

    private final StoreRepository storeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreAuthServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                                StoreRepository storeRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        super(authenticationManager, jwtUtils);
        this.storeRepository = storeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public StoreDTO register(StoreDTO signUpRequest) {

        Store store = new Store();
        store.setStoreName(signUpRequest.getStoreName());
        store.setCnpj(signUpRequest.getCnpj());
        store.setStoreEmail(signUpRequest.getStoreEmail());
        store.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Set<Role> rolesFromDto = signUpRequest.getRole();
        Set<Role> rolesParaSalvar;

        if (rolesFromDto == null || rolesFromDto.isEmpty()) {
            rolesParaSalvar = Set.of(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Erro: Perfil padrão ROLE_USER não encontrado.")));
        } else {
            rolesParaSalvar = rolesFromDto.stream()
                    .map(role -> {
                        ERole roleEnum = role.getName();
                        return roleRepository.findByName(roleEnum)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil " + roleEnum.name() + " não configurado no banco."));
                    })
                    .collect(Collectors.toSet());
        }

        store.setRole(rolesParaSalvar);
        Store savedStore = storeRepository.save(store);
        return Store2StoreDTO.convert(savedStore);
    }

}