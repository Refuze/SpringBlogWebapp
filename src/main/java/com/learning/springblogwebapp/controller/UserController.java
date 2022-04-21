package com.learning.springblogwebapp.controller;

import com.learning.springblogwebapp.domain.Role;
import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userService.findAll());
        return "user/userList";
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user,
                       Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user/userEdit";
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public String userEditSave(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam Map<String, String> form,
                               @RequestParam("userId") User user){
        userService.saveUser(user, username, password, form);
        return "redirect:/user";
    }

    @GetMapping("/profile")
    public String getProfile(Model model,
                             @AuthenticationPrincipal User user){
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String password,
                                @RequestParam String email){
        userService.updateProfile(user, password, email);
        return "redirect:/user/profile";
    }
}
