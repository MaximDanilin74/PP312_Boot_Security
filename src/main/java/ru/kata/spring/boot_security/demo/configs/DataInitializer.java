package ru.kata.spring.boot_security.demo.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    return roleRepository.save(role);
                });

        if (userRepository.findByUsernameWithRoles("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRoles(Set.of(roleUser));
            userRepository.save(user);
        }

        if (userRepository.findByUsernameWithRoles("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(roleAdmin, roleUser));
            userRepository.save(admin);
        }
    }
}