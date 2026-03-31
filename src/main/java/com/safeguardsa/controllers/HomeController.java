package com.safeguardsa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*@author S DYWILI
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Points to dashboard.html later
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin"; // Points to admin.html later
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // This looks for login.html in your static folder
    }

    //I will remove this when Kb creates her SafetyMapController.java
    @GetMapping("/map")
    public String mapTemp() {
        return "placeholder";
    }

    //I will remove this when Kgomo creates her SafetyMapController.java
    @GetMapping("/chat")
    public String chatTemp() {
        return "placeholder";
    }
}
