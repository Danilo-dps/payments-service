package pay.domain.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pay.domain.model.enums.ERole;
import pay.domain.model.Role;
import pay.domain.repository.RoleRepository;

import java.util.Arrays;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            log.info("Nenhum perfil encontrado, cadastrando perfis padrão...");

            Arrays.stream(ERole.values()).forEach(roleEnum -> {
                Role newRole = new Role(roleEnum);
                roleRepository.save(newRole);
            });

            log.info("Perfis cadastrados com sucesso!");
        } else {
            log.info("Perfis já cadastrados no banco de dados.");
        }
    }
}