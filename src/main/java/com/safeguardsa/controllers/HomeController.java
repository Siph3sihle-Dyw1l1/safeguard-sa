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
}
