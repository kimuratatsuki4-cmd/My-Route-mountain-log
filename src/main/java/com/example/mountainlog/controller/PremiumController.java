package com.example.mountainlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/premium")
public class PremiumController {

    @GetMapping("/gear-guide")
    public String gearGuide() {
        return "premium/gear-guide";
    }
}
