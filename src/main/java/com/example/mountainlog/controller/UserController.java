package com.example.mountainlog.controller;

import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.UserEditForm;
import com.example.mountainlog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String index(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("user", user);
        return "user/detail";
    }

    @GetMapping("/edit")
    public String edit(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        UserEditForm form = new UserEditForm();
        form.setUserId(user.getUserId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setAddress(user.getAddress());
        if (user.getBirthDate() != null) {
            form.setBirthDate(user.getBirthDate().toLocalDate());
        }
        form.setExperienceLevel(user.getExperienceLevel());

        model.addAttribute("userEditForm", form);
        return "user/edit";
    }

    @PostMapping("/update")
    public String update(@Validated @ModelAttribute UserEditForm userEditForm,
            BindingResult bindingResult,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        userService.updateUser(user, userEditForm);

        redirectAttributes.addFlashAttribute("successMessage", "会員情報を更新しました。");
        return "redirect:/user";
    }
}
