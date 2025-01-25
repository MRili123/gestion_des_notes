package com.example.GestionNote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notes")
public class NotesController {
    @RequestMapping("/home")
    public String home() {
        return "notes/home";
    }
}
