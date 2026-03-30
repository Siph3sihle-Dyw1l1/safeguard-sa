package com.safeguardsa.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author S DYWILI
 */
@Controller
public class ExitController {

    @GetMapping("/exit")
    public String exit() {
        return "redirect:https://www.google.com";
    }
}
