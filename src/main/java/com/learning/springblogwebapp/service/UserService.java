package com.learning.springblogwebapp.service;

import com.learning.springblogwebapp.domain.Role;
import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.repos.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);


        sendActivationCode(user);

        return true;
    }

    private void sendActivationCode(User user) {
        if(StringUtils.hasText(user.getEmail())){
            String message = String.format("Hello, %s!\n" +
                    "Welcome to my Blog.\n" +
                    "To activate your account please click on this link:\n" +
                    "http://localhost:8080/activate/%s",
                    user.getUsername(), user.getActivationCode());
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if (user == null) return false;
        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findByName(String username) {
        return userRepo.findByUsername(username);
    }

    public void saveUser(User user, String username, String password, Map<String, String> form) {
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if(roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(user);
    }

    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && email.equals(userEmail) ||
                userEmail != null && !userEmail.equals(email));

        if (isEmailChanged){
            user.setEmail(email);
            if (StringUtils.hasText(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (StringUtils.hasText(password)){
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(user);

        if (isEmailChanged){
            sendActivationCode(user);
        }
    }

    public void subscribe(User user, User requestUser) {
        requestUser.getSubscribers().add(user);
        userRepo.save(requestUser);
    }

    public void unsubscribe(User user, User requestUser) {
        requestUser.getSubscribers().remove(user);
        userRepo.save(requestUser);
    }
}
