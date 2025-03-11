package ru.kata.spring.boot_security.demo.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage(Model model, Principal principal) {

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        String username = principal.getName();
        model.addAttribute("username", username);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        model.addAttribute("roles", authorities);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("user", userDetails);

        List<Role> allRoles = roleService.getAllRoles();
        model.addAttribute("allRoles", allRoles);

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
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

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveUser(@ModelAttribute("user")@Valid User user,
                           BindingResult bindingResult,
                           @RequestParam(value = "roles", required = false) List<Long> roleIds,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "new-user";
        }
        try {
            userService.saveUser(user, roleIds); // Передаем user и roleIds
            redirectAttributes.addFlashAttribute("message", "User has been added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding user: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String editUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());

        return "edit"; // Возвращает шаблон edit.html
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser (@ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult,
                              @RequestParam("rolesIds") List<Long> roleIds,
                              RedirectAttributes redirectAttributes) {
            if (bindingResult.hasErrors()) {
                return "edit";
            }
            try {
                userService.updateUser(user.getId(), user, roleIds);
                redirectAttributes.addFlashAttribute("message", "User updated successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
            }
            return "redirect:/admin";
    }
    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}

