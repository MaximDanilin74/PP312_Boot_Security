package ru.kata.spring.boot_security.demo.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
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

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
            model.addAttribute("user", userService.getUserById(id));
            return "edit-user"; // Возвращает шаблон edit.html
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser (@ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult,
                              @RequestParam(value = "roles", required = false) List<Long> roleIds,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit-user";
        }
        try {
            userService.updateUser(user.getId(), user, roleIds); // Передаем id, user и roleIds
            redirectAttributes.addFlashAttribute("message", "User has been updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }// Перенаправляем на страницу администратора
        return "redirect:/admin";
    }
}

