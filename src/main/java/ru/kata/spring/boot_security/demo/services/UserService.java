package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getAllUsers();

    void saveUser(User user, List<Long> roleIds);

    void deleteUser(Long id);

    void updateUser(Long id, User user, List<Long> roleIds);

    User findByUsernameWithRoles(String username);

}
