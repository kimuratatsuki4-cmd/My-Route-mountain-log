package com.example.mountainlog.controller;

import com.example.mountainlog.entity.Activity;
import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.entity.MountainNote;
import com.example.mountainlog.service.ActivityService;
import com.example.mountainlog.service.MountainNoteService;
import com.example.mountainlog.service.MountainService;
import com.example.mountainlog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mountains")
public class MountainController {

    @Autowired
    private MountainService mountainService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;
    @Autowired
    private MountainNoteService mountainNoteService;

    /**
     * 山の一覧を表示
     */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String prefecture,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String elevationFilter,
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            Model model) {

        Integer minElevation = null;
        Integer maxElevation = null;

        if (elevationFilter != null && !elevationFilter.isBlank()) {
            switch (elevationFilter) {
                case "UNDER_1000":
                    maxElevation = 1000;
                    break;
                case "1000_TO_2000":
                    minElevation = 1000;
                    maxElevation = 2000;
                    break;
                case "2000_TO_3000":
                    minElevation = 2000;
                    maxElevation = 3000;
                    break;
                case "OVER_3000":
                    minElevation = 3000;
                    break;
            }
        }

        Page<Mountain> mountains = mountainService.searchMountainsWithFilter(keyword, prefecture, difficulty,
                minElevation, maxElevation, pageable);
        model.addAttribute("mountains", mountains);
        model.addAttribute("keyword", keyword);
        model.addAttribute("prefecture", prefecture);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("elevationFilter", elevationFilter);
        return "mountains/list";
    }

    /**
     * 山の詳細を表示
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Integer id, Model model, Principal principal) {
        Mountain mountain = mountainService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mountain Id:" + id));
        model.addAttribute("mountain", mountain);

        // ログインユーザーの場合、この山に関連する活動ログを取得して表示
        if (principal != null) {
            Optional<User> userOpt = userService.findUserByEmail(principal.getName());
            if (userOpt.isPresent()) {
                List<Activity> myActivities = activityService.getActivitiesByUserAndMountain(userOpt.get(), mountain);
                model.addAttribute("myActivities", myActivities);
            }
        }

        // みんなの準備メモを取得
        List<MountainNote> notes = mountainNoteService.getNotesByMountainId(id);
        model.addAttribute("notes", notes);

        return "mountains/detail";
    }
}
