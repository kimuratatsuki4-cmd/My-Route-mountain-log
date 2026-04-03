package com.example.mountainlog.controller;

import com.example.mountainlog.entity.User;
import com.example.mountainlog.security.UserDetailsImpl;
import com.example.mountainlog.service.SubscriptionService;
import com.example.mountainlog.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {

    private SubscriptionService subscriptionService;
    private UserService userService;

    @Autowired
    public void setSubscriptionService(SubscriptionService subscriptionService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
    }

    // Webhookシークレット（Stripeダッシュボードから取得）
    @Value("${stripe.premium.price.id}")
    private String premiumPriceId;

    @Value("${stripe.success.url}")
    private String stripeSuccessUrl;

    @Value("${stripe.cancel.url}")
    private String stripeCancelUrl;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    // Register Page (GET) - not defined in original but probably needed.
    @GetMapping("/register")
    public String registerPage() {
        return "subscription/register";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes) {
        // セッション内のユーザーデータは古い可能性があるため、DBから最新のユーザー情報を取得する
        User user = userService.findUserByEmail(userDetailsImpl.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String stripeCustomerId = user.getStripeId();

        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            try {
                Customer customer = subscriptionService.createUser(user);
                stripeCustomerId = customer.getId();
                userService.saveStripeId(user.getEmail(), stripeCustomerId);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "有料プランへの登録に失敗しました（顧客作成エラー）。再度お試しください。");
                return "redirect:/";
            }
        }

        try {
            com.stripe.model.checkout.Session session = subscriptionService.createStripeSession(premiumPriceId,
                    stripeCustomerId, stripeSuccessUrl, stripeCancelUrl);
            return "redirect:" + session.getUrl();
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "有料プランへの登録に失敗しました（セッション作成エラー）。再度お試しください。");
            return "redirect:/";
        }
    }

    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes,
            Model model) {
        // セッション内のデータ（古い状態）ではなく、DBから最新の情報を取得
        User user = userService.findUserByEmail(userDetailsImpl.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStripeId() == null || user.getStripeId().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Stripeの顧客IDが登録されていません。");
            return "redirect:/";
        }

        try {
            PaymentMethod paymentMethod = subscriptionService.getDefaultPaymentMethod(user.getStripeId());

            if (paymentMethod != null) {
                model.addAttribute("card", paymentMethod.getCard());
                model.addAttribute("cardHolderName", paymentMethod.getBillingDetails().getName());
            }
            model.addAttribute("stripePublicKey", stripePublicKey);
        } catch (Exception e) {
            e.printStackTrace(); // エラーの詳細をコンソールに出力
            redirectAttributes.addFlashAttribute("errorMessage", "お支払い方法を取得できませんでした。再度お試しください。");
            return "redirect:/";
        }

        return "subscription/edit";
    }

    @PostMapping("/update")
    public String update(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @RequestParam String paymentMethodId,
            RedirectAttributes redirectAttributes) {
        // DBから最新情報を取得
        User user = userService.findUserByEmail(userDetailsImpl.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String stripeCustomerId = user.getStripeId();

        try {
            String currentDefaultPaymentMethodId = subscriptionService.getDefaultPaymentMethodId(stripeCustomerId);

            // 1. 新しい支払い方法を顧客に紐づける
            subscriptionService.attachPaymentMethodToCustomer(paymentMethodId, stripeCustomerId);
            // 2. 顧客のデフォルトの支払い方法を新しいものに設定する
            subscriptionService.setDefaultPaymentMethod(paymentMethodId, stripeCustomerId);

            // 3. 【重要】すでに稼働中のサブスクリプション（定期課金）の支払い方法も新しいものに更新し、次回の決済失敗を防ぐ
            subscriptionService.updateSubscriptionPaymentMethod(stripeCustomerId, paymentMethodId);

            // 4. これまで使用していた古い支払い方法が存在する場合のみ、紐づけを解除（削除）する
            if (currentDefaultPaymentMethodId != null && !currentDefaultPaymentMethodId.equals(paymentMethodId)) {
                subscriptionService.detachPaymentMethodFromCustomer(currentDefaultPaymentMethodId);
            }
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "お支払い方法の変更に失敗しました。再度お試しください。");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("successMessage", "お支払い方法を変更しました。");

        return "redirect:/";
    }

    @GetMapping("/success")
    public String success(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes) {
        User user = userDetailsImpl.getUser();

        userService.updateRole(user.getEmail(), "ROLE_PREMIUM");
        userService.refreshAuthenticationByRole("ROLE_PREMIUM");

        redirectAttributes.addFlashAttribute("successMessage", "有料プランへの登録が完了しました！");
        return "redirect:/";
    }

    // 登録キャンセルページ
    @GetMapping("/register-cancel")
    public String registerCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "有料プランへの登録をキャンセルしました。");
        return "redirect:/subscription/register";
    }

    // 解約ページ
    @GetMapping("/cancel")
    public String cancel() {
        return "subscription/cancel";
    }

    // 解約
    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes) {
        User user = userService.findUserByEmail(userDetailsImpl.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStripeId() == null || user.getStripeId().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "現在、有料プランには登録されていません。");
            return "redirect:/";
        }

        try {
            List<Subscription> subscriptions = subscriptionService.getSubscriptions(user.getStripeId());

            subscriptionService.cancelSubscriptions(subscriptions);

            String defaultPaymentMethodId = subscriptionService.getDefaultPaymentMethodId(user.getStripeId());

            if (defaultPaymentMethodId != null) {
                subscriptionService.detachPaymentMethodFromCustomer(defaultPaymentMethodId);
            }
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "有料プランの解約に失敗しました。詳細: " + e.getMessage());
            return "redirect:/";
        }

        userService.updateRole(user.getEmail(), "ROLE_GENERAL");
        userService.refreshAuthenticationByRole("ROLE_GENERAL");

        redirectAttributes.addFlashAttribute("successMessage", "有料プランを解約しました。");

        return "redirect:/";
    }
}