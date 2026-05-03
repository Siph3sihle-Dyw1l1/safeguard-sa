package com.safeguardsa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*@author S DYWILI
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index.html";
    }

    //LOGIN — Spring Security posts to /login , this just serves the page
    @GetMapping("/login")
    public String login() {
        return "login.html"; // templates/login.html
    }

    // TEMP: Remove this when Member C creates SafetyMapController.java
    @GetMapping("/map")
    public String mapTemp() {
        return "placeholder.html"; // templates/placeholder.html
    }

    // TEMP: Remove this when Member B creates ChatController.java
    @GetMapping("/chat")
    public String chatTemp() {
        return "placeholder.html"; // templates/placeholder.html
    }
}
