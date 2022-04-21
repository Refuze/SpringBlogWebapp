package com.learning.springblogwebapp.controller;

import com.learning.springblogwebapp.domain.Message;
import com.learning.springblogwebapp.domain.User;
import com.learning.springblogwebapp.repos.MessageRepo;
import com.learning.springblogwebapp.repos.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class BlogController {
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;

    @Value("${upload.path}")
    private String uploadPath;

    public BlogController(MessageRepo messageRepo, UserRepo userRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    @GetMapping()
    public String greeting(Model model){
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter,
                       Model model){
        Iterable<Message> messages;

        if(filter != null && !filter.isEmpty()){
            messages = messageRepo.findByTag(filter);
        } else{
            messages = messageRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user,
                      @RequestParam MultipartFile file,
                      @Valid Message message,
                      BindingResult bindingResult,
                      Model model) throws IOException {

        message.setAuthor(user);

        if (bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
        } else {

            saveFile(file, message);
            model.addAttribute("message", null);
            messageRepo.save(message);
        }
        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }

    private void saveFile(MultipartFile file, Message message) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadFolder = new File(uploadPath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
            message.setFilename(resultFilename);
        }
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String filter,
                         Model model){
        Iterable<Message> messages = messageRepo.findByTag(filter);
        if (!messages.iterator().hasNext())
            messages = messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }

    @GetMapping("/user-messages/{requestUsername}")
    public String userMessages(@AuthenticationPrincipal User currentUser,
                               @PathVariable String requestUsername,
                               @RequestParam(required = false) Message message,
                               Model model){

        User requestUser = userRepo.findByUsername(requestUsername);
        Set<Message> messages = requestUser.getMessages();
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(requestUser));
        return "user/userMessages";
    }

    @PostMapping("/user-messages/{requestUsername}")
    public String userMessageUpdate(@AuthenticationPrincipal User currentUser,
                                    @RequestParam("id") Message message,
                                    @RequestParam("text") String text,
                                    @RequestParam("tag") String tag,
                                    @RequestParam MultipartFile file) throws IOException {
        if (message.getAuthor().equals(currentUser)){
            if (StringUtils.hasText(text)){
                message.setText(text);
            }
            if (StringUtils.hasText(tag)){
                message.setTag(tag);
            }
            messageRepo.save(message);
            saveFile(file, message);
        }
        return "redirect:/user-messages/"+currentUser.getUsername();
    }
}