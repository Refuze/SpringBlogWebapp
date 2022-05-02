/*
package com.learning.springblogwebapp.service;

import com.learning.springblogwebapp.domain.Role;
import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.repos.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    void addUser() {
        User user = new User();
        user.setEmail("some@mail.ru");

        boolean isSuccessAdded = userService.addUser(user);

        Assert.assertTrue(isSuccessAdded);
        Assert.assertNotNull(user.getActivationCode());
        Assert.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.User)));

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
        Mockito.verify(mailSender, Mockito.times(1))
                .send(ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Activation code"),
                        ArgumentMatchers.contains("Welcome to my Blog."));
    }

    @Test
    void addUserFail(){
        User user = new User();

        user.setUsername("admin");

        Mockito.doReturn(new User())
                .when(userRepo)
                .findByUsername("admin");

        boolean isSuccessAdded = userService.addUser(user);

        Assert.assertFalse(isSuccessAdded);

        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSender, Mockito.times(0))
                .send(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    void activateUser() {
        User user = new User();

        user.setActivationCode("Activation code");

        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("Activate");

        boolean isUserActivated = userService.activateUser("Activate");

        Assert.assertTrue(isUserActivated);
        Assert.assertNull(user.getActivationCode());

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    void activateUserFail() {
        Mockito.doReturn(null)
                .when(userRepo)
                .findByActivationCode("Activate");

        boolean isUserActivated = userService.activateUser("Activate");

        Assert.assertFalse(isUserActivated);

        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void saveUser() {
        User user = new User();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.User);
        roles.add(Role.Admin);

        user.setRoles(roles);


        userService.saveUser(user, "username", "password", new HashMap<>());

        Mockito.verify(userRepo, Mockito.times(1)).save(user);

        Assert.assertEquals("username", user.getUsername());
        Assert.assertEquals( passwordEncoder.encode("password"), user.getPassword());
    }

    @Test
    void updateProfile() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setActive(true);
        user.setEmail("email");
        String newPassword = "newPassword";
        String newEmail = "newEmail";
        userService.updateProfile(user, newPassword, newEmail);

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }
}*/
