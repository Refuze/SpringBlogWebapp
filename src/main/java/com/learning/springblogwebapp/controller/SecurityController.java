package com.learning.springblogwebapp.controller;

import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.domain.dto.CaptchaResponseDto;
import com.learning.springblogwebapp.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class SecurityController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private final UserService userService;
    private final RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    private String captchaSecret;

    public SecurityController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/registration")
    public String registration(){
        return "security/registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam("passwordConfirmation") String passwordConfirmation,
                          @RequestParam("g-recaptcha-response") String captchaResponse,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model){
        String url = String.format(CAPTCHA_URL, captchaSecret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if (!response.isSuccess()){
            model.addAttribute("captchaError", "Captcha error");
        }

        boolean isConfirmationEmpty = !StringUtils.hasText(passwordConfirmation);

        if (isConfirmationEmpty){
            model.addAttribute("passwordConfirmationError", "Password confirmation shouldn't be empty");
        }

        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirmation)){
            model.addAttribute("passwordDifferent", "Passwords are not equals");
            model.addAttribute("user", user);
            return "security/registration";
        }

        if (isConfirmationEmpty || bindingResult.hasErrors() || !response.isSuccess()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("user", user);
            return "security/registration";
        }

        if (!userService.addUser(user)){
            model.addAttribute("usernameExist", "Username already exist.");
            return "security/registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model,
                           @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if (isActivated){
            model.addAttribute("success", "User successfully activated");
        } else{
            model.addAttribute("invalid", "Activation code is invalid");
        }

        return "security/login";
    }
}
