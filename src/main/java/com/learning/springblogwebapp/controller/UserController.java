package com.learning.springblogwebapp.controller;

import com.learning.springblogwebapp.domain.Role;
import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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

    @GetMapping("/{type}/{username}")
    public String subscribe(@AuthenticationPrincipal User user,
                            @PathVariable String username,
                            @PathVariable String type){
        User requestUser = userService.findByName(username);
        if (type.equals("subscribe")){
            userService.subscribe(user, requestUser);
        } else if (type.equals("unsubscribe")){
            userService.unsubscribe(user, requestUser);
        } else {
            return "redirect:/";
        }
        return "redirect:/user-messages/" + username;
    }

    @GetMapping("{type}/{username}/list")
    public String subscriptionList(@PathVariable String type,
                                   @PathVariable String username,
                                   Model model){
        User requestUser = userService.findByName(username);

        model.addAttribute("type", type);
        model.addAttribute("userPage", requestUser);

        if (type.equals("subscriptions")){
            model.addAttribute("users", requestUser.getSubscriptions());
        } else if (type.equals("subscribers")) {
            model.addAttribute("users", requestUser.getSubscribers());
        } else {
            return "redirect:/";
        }

        return "user/subscriptions";
    }
}
