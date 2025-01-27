package com.example.GestionNote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/AdminSp")
public class SpController {
    @RequestMapping("/home")
    public String home() {
        return "AdminSp/home";
    }
}
