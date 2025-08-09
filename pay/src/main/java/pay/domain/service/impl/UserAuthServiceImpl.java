package pay.domain.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pay.domain.adapter.User2UserDTO;
import pay.domain.dto.UserDTO;
import pay.domain.model.Role;
import pay.domain.model.User;
import pay.domain.model.enums.ERole;
import pay.domain.repository.RoleRepository;
import pay.domain.repository.UserRepository;
import pay.domain.security.jwt.JwtUtils;
import pay.domain.service.AbstractAuthService;
import pay.domain.utils.validator.UserValidator;

import java.util.Set;
import java.util.stream.Collectors;

@Service("userAuthService")
public class UserAuthServiceImpl extends AbstractAuthService<UserDTO, UserDTO> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public UserAuthServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                               UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserValidator userValidator) {
        super(authenticationManager, jwtUtils);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }

    @Override
    @Transactional
    public UserDTO register(UserDTO signUpRequest) {

        userValidator.validate(signUpRequest);
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setCpf(signUpRequest.getCpf());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

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

        user.setRole(rolesParaSalvar);
        User savedUser = userRepository.save(user);
        return User2UserDTO.convert(savedUser);
    }

}