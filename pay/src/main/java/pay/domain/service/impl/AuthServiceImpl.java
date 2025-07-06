package pay.domain.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pay.domain.adapter.User2UserDTO;
import pay.domain.dto.UserDTO;
import pay.domain.model.ERole;
import pay.domain.model.Role;
import pay.domain.model.User;
import pay.domain.payload.request.LoginRequest;
import pay.domain.payload.response.JwtResponse;
import pay.domain.repository.RoleRepository;
import pay.domain.repository.UserRepository;
import pay.domain.security.jwt.JwtUtils;
import pay.domain.service.AuthService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO signUpRequest) {

        // 1. Validações de negócio (continua igual)
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Erro: Nome de usuário já está em uso!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }

        // 2. Cria a entidade User (continua igual)
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setCpf(signUpRequest.getCpf());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // 3. Pega o Set<Role> do DTO (que agora vem preenchido corretamente)
        Set<Role> rolesFromDto = signUpRequest.getRole();
        Set<Role> rolesParaSalvar;

        // 4. A "PONTE" SIMPLIFICADA
        if (rolesFromDto == null || rolesFromDto.isEmpty()) {
            // Se nenhuma role for enviada, atribui a padrão
            rolesParaSalvar = Set.of(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Erro: Perfil padrão ROLE_USER não encontrado.")));
        } else {
            // Se roles foram enviadas, busca as entidades correspondentes no banco
            rolesParaSalvar = rolesFromDto.stream()
                    .map(role -> {
                        // Pega o enum 'name' de dentro do objeto Role que o Jackson criou
                        ERole roleEnum = role.getName();
                        // Busca a entidade Role gerenciada no banco de dados
                        return roleRepository.findByName(roleEnum)
                                .orElseThrow(() -> new RuntimeException("Erro: Perfil " + roleEnum.name() + " não configurado no banco."));
                    })
                    .collect(Collectors.toSet());
        }

        // 5. Associa os perfis e salva o usuário (continua igual)
        user.setRole(rolesParaSalvar);
        User savedUser = userRepository.save(user);
        return User2UserDTO.convert(savedUser);
    }
}
