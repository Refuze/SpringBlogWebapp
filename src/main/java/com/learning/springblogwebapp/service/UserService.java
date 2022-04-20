package com.learning.springblogwebapp.service;

import com.learning.springblogwebapp.domain.Role;
import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.repos.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final MailSender mailSender;

    public UserService(UserRepo userRepo, MailSender mailSender) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user){
        if (userRepo.findByUsername(user.getUsername()) != null){
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.User));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);


        if(StringUtils.hasText(user.getEmail())){
            String message = String.format("Hello, %s!\n" +
                    "Welcome to our Blog.\n" +
                    "To activate your account please click on this link:\n" +
                    "http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode());
            mailSender.send(user.getEmail(), "Activation code", message);
        }

        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if (user == null) return false;
        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }
}
