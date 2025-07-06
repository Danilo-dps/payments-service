package pay.domain.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pay.domain.model.enums.ERole;
import pay.domain.model.Role;
import pay.domain.repository.RoleRepository;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verifica se a tabela de roles já tem dados para não inserir repetido
        if (roleRepository.count() == 0) {
            System.out.println("Nenhum perfil encontrado, cadastrando perfis padrão...");

            Arrays.stream(ERole.values()).forEach(roleEnum -> {
                Role newRole = new Role(roleEnum);
                roleRepository.save(newRole);
            });

            System.out.println("Perfis cadastrados com sucesso!");
        } else {
            System.out.println("Perfis já cadastrados no banco de dados.");
        }
    }
}