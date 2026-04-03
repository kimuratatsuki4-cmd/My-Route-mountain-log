package com.example.mountainlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

// @Controller: このクラスがWebコントローラーであることを示します（画面遷移などを制御）
import com.example.mountainlog.service.MountainService;
import com.example.mountainlog.service.UserService;
import com.example.mountainlog.service.ActivityService;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.Activity;
import com.example.mountainlog.dto.ActivityStatsDto;
import java.util.List;

@Controller
public class HomeController {

    private final MountainService mountainService;
    private final UserService userService;
    private final ActivityService activityService;

    public HomeController(MountainService mountainService, UserService userService, ActivityService activityService) {
        this.mountainService = mountainService;
        this.userService = userService;
        this.activityService = activityService;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findUserByEmail(principal.getName()).orElse(null);

            if (user != null) {
                // 活動スタッツの取得
                ActivityStatsDto stats = activityService.getActivityStats(user);
                model.addAttribute("stats", stats);

                if (user.getRole().getName().equals("ROLE_PREMIUM")) {
                    // おすすめの山
                    List<Mountain> recommendedMountains = mountainService.getRecommendedMountains(user);
                    model.addAttribute("recommendedMountains", recommendedMountains);

                    // ランダムな活動記録 (Premiumのみ表示)
                    List<Activity> randomActivities = activityService.getRandomActivities(user, 3);
                    model.addAttribute("randomActivities", randomActivities);
                }
            }
        }

        return "index";
    }
}
