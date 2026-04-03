package com.example.mountainlog.controller;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.AdminUserForm;
import com.example.mountainlog.form.MountainForm;
import com.example.mountainlog.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalUsers = adminService.getTotalUsers();
        long premiumUsers = adminService.getPremiumUsers();
        long totalMountains = adminService.getTotalMountains();
        long recentActivities = adminService.getRecentActivities();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("premiumUsers", premiumUsers);
        model.addAttribute("totalMountains", totalMountains);
        model.addAttribute("recentActivities", recentActivities);
        model.addAttribute("salesData", adminService.getSalesForecastData());

        return "admin/dashboard";
    }

    // --- User Management ---

    @GetMapping("/users")
    public String userList(@PageableDefault(size = 20) Pageable pageable, Model model) {
        Page<User> userPage = adminService.getAllUsers(pageable);
        model.addAttribute("userPage", userPage);
        return "admin/users/index";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("adminUserForm", new AdminUserForm());
        model.addAttribute("roles", adminService.getAllRoles());
        return "admin/users/form";
    }

    @PostMapping("/users/create")
    public String createUser(@Validated @ModelAttribute AdminUserForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        // 新規作成時はパスワード必須
        if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
            bindingResult.rejectValue("password", "error.password", "パスワードを入力してください");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", adminService.getAllRoles());
            return "admin/users/form";
        }

        adminService.saveUserFromAdmin(form);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーを新規登録しました。");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        User user = adminService.getUserById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたユーザーが見つかりませんでした。");
            return "redirect:/admin/users";
        }

        AdminUserForm form = new AdminUserForm();
        form.setUserId(user.getUserId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setEnabled(user.getEnabled());
        form.setRoleId(user.getRole().getId());

        model.addAttribute("adminUserForm", form);
        model.addAttribute("roles", adminService.getAllRoles());
        return "admin/users/form";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable("id") Integer id,
            @Validated @ModelAttribute AdminUserForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", adminService.getAllRoles());
            return "admin/users/form";
        }

        form.setUserId(id);
        adminService.saveUserFromAdmin(form);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザー情報を更新しました。");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        adminService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "ユーザーを削除しました。");
        return "redirect:/admin/users";
    }

    // --- Mountain Master Management ---

    @GetMapping("/mountains")
    public String mountainList(@PageableDefault(size = 20) Pageable pageable, Model model) {
        Page<Mountain> mountainPage = adminService.getAllMountains(pageable);
        model.addAttribute("mountainPage", mountainPage);
        return "admin/mountains/index";
    }

    @GetMapping("/mountains/new")
    public String newMountain(Model model) {
        model.addAttribute("mountainForm", new MountainForm());
        return "admin/mountains/form";
    }

    @PostMapping("/mountains/create")
    public String createMountain(@Validated @ModelAttribute MountainForm mountainForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/mountains/form";
        }

        adminService.saveMountainFromForm(mountainForm);
        redirectAttributes.addFlashAttribute("successMessage", "山データを新規登録しました。");
        return "redirect:/admin/mountains";
    }

    @GetMapping("/mountains/{id}/edit")
    public String editMountain(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Mountain mountain = adminService.getMountainById(id).orElse(null);
        if (mountain == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定された山が見つかりませんでした。");
            return "redirect:/admin/mountains";
        }

        MountainForm form = new MountainForm();
        form.setMountainId(mountain.getMountainId());
        form.setName(mountain.getName());
        form.setNameEn(mountain.getNameEn());
        form.setNameKana(mountain.getNameKana());
        form.setElevation(mountain.getElevation());
        form.setPrefecture(mountain.getPrefecture());
        form.setLatitude(mountain.getLatitude());
        form.setLongitude(mountain.getLongitude());
        form.setTypicalDistanceKm(mountain.getTypicalDistanceKm());
        form.setTypicalDurationMinutes(mountain.getTypicalDurationMinutes());
        form.setTypicalElevationGain(mountain.getTypicalElevationGain());
        form.setDifficulty(mountain.getDifficulty());
        form.setDescription(mountain.getDescription());
        form.setIsHyakumeizan(mountain.getIsHyakumeizan());
        form.setImageUrl(mountain.getImageUrl());
        form.setImageCitation(mountain.getImageCitation());

        model.addAttribute("mountainForm", form);
        return "admin/mountains/form";
    }

    @PostMapping("/mountains/{id}/update")
    public String updateMountain(@PathVariable("id") Integer id,
            @Validated @ModelAttribute MountainForm mountainForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/mountains/form";
        }

        mountainForm.setMountainId(id);
        adminService.saveMountainFromForm(mountainForm);
        redirectAttributes.addFlashAttribute("successMessage", "山データを更新しました。");
        return "redirect:/admin/mountains";
    }

    @PostMapping("/mountains/{id}/delete")
    public String deleteMountain(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        adminService.deleteMountain(id);
        redirectAttributes.addFlashAttribute("successMessage", "山データを削除しました。");
        return "redirect:/admin/mountains";
    }
}
