package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String userPage(Model model, Principal principal) {

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        String username = principal.getName();
        model.addAttribute("username", username);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        model.addAttribute("roles", authorities);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("user", userDetails);
        return "user";
    }
    @Autowired
    private RoleService roleService;

    @GetMapping("/admin/new")
    public String createUserForm(Model model, Principal principal) {

        model.addAttribute("user", new User());

        String username = principal.getName();
        model.addAttribute("username", username);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        model.addAttribute("roles", roles);

        List<Role> allRoles = roleService.getAllRoles();
        model.addAttribute("allRoles", allRoles);

        return "new-user";
    }
}
