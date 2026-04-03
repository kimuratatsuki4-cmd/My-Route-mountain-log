package com.example.mountainlog.controller;

import com.example.mountainlog.entity.Activity;
import com.example.mountainlog.entity.ActivityDetail;
import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.ActivityEditForm;
import com.example.mountainlog.form.ActivityForm;
import com.example.mountainlog.service.ActivityService;
import com.example.mountainlog.service.MountainService;
import com.example.mountainlog.service.UserService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final UserService userService;
    private final MountainService mountainService;

    public ActivityController(ActivityService activityService,
            UserService userService,
            MountainService mountainService) {
        this.activityService = activityService;
        this.userService = userService;
        this.mountainService = mountainService;
    }

    // 一覧画面
    @GetMapping
    public String list(Model model, Principal principal,
            @RequestParam(name = "sort", defaultValue = "date_desc") String sort) {
        Optional<User> userOpt = userService.findUserByEmail(principal.getName());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("activities", activityService.getActivitiesByUserWithSort(user, sort));
            model.addAttribute("sort", sort);
            model.addAttribute("stats", activityService.getActivityStats(user));
            model.addAttribute("yearlyStats", activityService.getYearlyStats(user));
        } else {
            return "redirect:/auth/login";
        }

        return "activities/list";
    }

    // 詳細画面
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Optional<Activity> activityOpt = activityService.findActivityById(id);
        if (activityOpt.isEmpty()) {
            return "redirect:/activities";
        }
        model.addAttribute("activity", activityOpt.get());
        return "activities/detail";
    }

    // 新規登録フォーム
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("activityForm", new ActivityForm());
        return "activities/form";
    }

    // 登録処理
    @Transactional
    @PostMapping
    public String create(@Validated @ModelAttribute ActivityForm activityForm,
            BindingResult result,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "不備があります");
            return "activities/form";
        }

        try {
            String email = principal.getName();
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("ログイン中のユーザー情報が見つかりません。"));

            // 活動ログ保存
            Activity activity = new Activity();
            activity.setUser(user);
            activity.setTitle(activityForm.getTitle());
            activity.setActivityDate(java.sql.Date.valueOf(activityForm.getActivityDate()));
            activity.setDescription(activityForm.getDescription());

            // 山マスタが選択されていれば紐づける
            if (activityForm.getMountainId() != null) {
                Mountain mountain = mountainService.findById(activityForm.getMountainId()).orElse(null);
                activity.setMountain(mountain);
            }

            ActivityDetail detail = new ActivityDetail();
            detail.setDistanceKm(activityForm.getDistanceKm());
            detail.setDurationMinutes(activityForm.getDurationMinutes());
            detail.setElevationGain(activityForm.getElevationGain());
            detail.setMaxElevation(activityForm.getMaxElevation());
            // DB保存 & 画像アップロード
            activityService.createActivity(activity, detail, activityForm.getImageFile());

            redirectAttributes.addFlashAttribute("successMessage", "活動ログを登録しました！");
            return "redirect:/activities";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "登録中にシステムエラーが発生しました");
            return "activities/form";
        }
    }

    // 削除処理
    @PostMapping("/{id}/delete")
    @Transactional
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        activityService.deleteActivity(id);
        redirectAttributes.addFlashAttribute("successMessage", "削除しました。");
        return "redirect:/activities";
    }

    // 編集画面表示
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        Optional<Activity> activityOpt = activityService.findActivityById(id);
        if (activityOpt.isEmpty()) {
            return "redirect:/activities";
        }

        Activity activity = activityOpt.get();
        ActivityEditForm form = new ActivityEditForm();

        form.setId(activity.getActivityId());
        form.setTitle(activity.getTitle());
        if (activity.getActivityDate() != null) {
            form.setActivityDate(activity.getActivityDate().toLocalDate());
        }
        form.setLocation(activity.getLocation());
        form.setDescription(activity.getDescription());
        if (activity.getMountain() != null) {
            form.setMountainId(activity.getMountain().getMountainId());
        }

        ActivityDetail detail = activity.getActivityDetail();
        if (detail != null) {
            form.setDistanceKm(detail.getDistanceKm());
            form.setDurationMinutes(detail.getDurationMinutes());
            form.setElevationGain(detail.getElevationGain());
            form.setMaxElevation(detail.getMaxElevation());
            form.setPaceNotes(detail.getPaceNotes());
        }

        model.addAttribute("activityEditForm", form);
        return "activities/edit";
    }

    // 更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
            @Validated @ModelAttribute ActivityEditForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // IDの一致確認など行うべきだが、今回はパス変数を優先

        if (result.hasErrors()) {
            // エラー時は編集画面に戻す
            // idはPathから取れているのでFormにセットし直しておく（念のため）
            form.setId(id);
            return "activities/edit";
        }

        try {
            // Form -> Entity (更新用データの作成)
            Activity updatedActivity = new Activity();
            updatedActivity.setTitle(form.getTitle());
            if (form.getActivityDate() != null) {
                updatedActivity.setActivityDate(java.sql.Date.valueOf(form.getActivityDate()));
            }
            // category field removed
            // updatedActivity.setCategory(Category.MOUNTAIN);
            updatedActivity.setLocation(form.getLocation());
            updatedActivity.setDescription(form.getDescription());

            // 山マスタが選択されていれば紐づける
            if (form.getMountainId() != null) {
                Mountain mountain = mountainService.findById(form.getMountainId()).orElse(null);
                updatedActivity.setMountain(mountain);
            }

            ActivityDetail updatedDetail = new ActivityDetail();
            updatedDetail.setDistanceKm(form.getDistanceKm());
            updatedDetail.setDurationMinutes(form.getDurationMinutes());
            updatedDetail.setElevationGain(form.getElevationGain());
            updatedDetail.setMaxElevation(form.getMaxElevation());
            updatedDetail.setPaceNotes(form.getPaceNotes());

            // Service呼び出し（トランザクション内で実行）
            activityService.updateActivity(id, updatedActivity, updatedDetail, form.getImageFile());

            redirectAttributes.addFlashAttribute("successMessage", "活動ログを更新しました！");
            return "redirect:/activities/" + id;

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "更新中にシステムエラーが発生しました");
            return "activities/edit";
        }
    }
}
