package com.gaes3play.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login"; // tu login.html
    }

    @GetMapping("/redirect")
    public String redirectByRole(Authentication auth) {

        String role = auth.getAuthorities().iterator().next().getAuthority();

        switch(role) {
            case "ROLE_ADMIN":
                return "redirect:/admin/dashboard";
            case "ROLE_EMPLEADO":
                return "redirect:/empleado/dashboard";
            case "ROLE_CLIENTE":
                return "redirect:/cliente/dashboard";
            default:
                return "redirect:/login?error";
        }
    }
}
