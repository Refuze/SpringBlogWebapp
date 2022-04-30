package com.learning.springblogwebapp.controller;

import com.learning.springblogwebapp.domain.Message;
import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.repos.MessageRepo;
import com.learning.springblogwebapp.repos.UserRepo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@Controller
@RequestMapping("/user-messages")
public class UserMessagesController {
    private UserRepo userRepo;
    private MessageRepo messageRepo;

    public UserMessagesController(UserRepo userRepo, MessageRepo messageRepo) {
        this.userRepo = userRepo;
        this.messageRepo = messageRepo;
    }


    @GetMapping("/{requestUsername}")
    public String userMessages(@AuthenticationPrincipal User currentUser,
                               @PathVariable String requestUsername,
                               @RequestParam(required = false) Message message,
                               Model model){

        User requestUser = userRepo.findByUsername(requestUsername);
        Set<Message> messages = requestUser.getMessages();
        model.addAttribute("userPage", requestUser);
        model.addAttribute("subscriptionsCount", requestUser.getSubscriptions().size());
        model.addAttribute("subscribersCount", requestUser.getSubscribers().size());
        model.addAttribute("isSubscriber", requestUser.getSubscribers().contains(currentUser));
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(requestUser));
        return "user/userMessages";
    }

    @PostMapping("/{requestUsername}")
    public String userMessageUpdate(@AuthenticationPrincipal User currentUser,
                                    @RequestParam("id") Message message,
                                    @RequestParam("tag") String tag,
                                    @RequestParam("text") String text,
                                    @PathVariable String requestUsername) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if (StringUtils.hasText(text)) {
                message.setText(text);
            }
            if (StringUtils.hasText(tag)) {
                message.setTag(tag);
            }
            messageRepo.save(message);
        }
        return "redirect:/user-messages/" + currentUser.getUsername();
    }
}
