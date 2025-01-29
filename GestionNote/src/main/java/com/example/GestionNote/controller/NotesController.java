package com.example.GestionNote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/AdminNotes")
public class NotesController {
    @RequestMapping("/home")
    public String home() {
        return "AdminNotes/home";
    }
}
