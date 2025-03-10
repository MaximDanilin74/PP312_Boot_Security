package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id:" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username:" + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveUser(User user, List<Long> roleIds) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleRepository.findAllById(roleIds)
                    .stream().collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, User user, List<Long> roleIds) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleRepository.findAllById(roleIds).stream()
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User findByUsernameWithRoles(String username) {
        return userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username:" + username));
        }
}