package com.gaes3play.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmpleadoController {

    @GetMapping("/empleado/dashboard")
    public String dashboard() {
        return "empleado/dashboard";
    }
}
