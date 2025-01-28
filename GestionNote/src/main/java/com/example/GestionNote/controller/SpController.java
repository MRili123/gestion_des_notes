package com.example.GestionNote.controller;

import com.example.GestionNote.DTO.CustomUserDetails;
import com.example.GestionNote.DTO.ProfessorDTO;
import com.example.GestionNote.model.Professor;
import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.ProfessorRepository;
import com.example.GestionNote.repository.UserRepository;
import com.example.GestionNote.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/AdminSp")
public class SpController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfessorRepository professorRepository;

    // Make the user object accessible to all controller routes
    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userRepository.findByUsername(username);
                model.addAttribute("user", user);
            }
        }
    }

    @RequestMapping("/home")
    public String home() {
        return "AdminSp/home";
    }

    @RequestMapping("/filieres")
    public String filiere() {
        return "AdminSp/filieres";
    }

    @RequestMapping("/classes")
    public String classes() {
        return "AdminSp/classes";
    }

    @RequestMapping("/modules")
    public String modules() {
        return "AdminSp/modules";
    }

    @RequestMapping("/elements")
    public String elements() {
        return "AdminSp/elements";
    }

    @RequestMapping("/professors")
    public String professors(Model model) {
        List<Professor> professors = professorRepository.findByDeletedFalse();
        model.addAttribute("professors", professors);
        return "AdminSp/professors";
    }

    @RequestMapping("/professors/delete/{id}")
    public String deleteProfessor(@PathVariable int id) {
        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor != null) {
            professor.setDeleted(true);
            professorRepository.save(professor);
        }
        return "redirect:/AdminSp/professors";
    }

    @RequestMapping("/professors/edit/{id}")
    public String editProfessor(@PathVariable int id, @RequestBody ProfessorDTO updatedProfessor) {
        Professor professor = professorRepository.findById(id).orElse(null);
        if (professor != null) {
            professor.setFirstName(updatedProfessor.getFirstName());
            professor.setLastName(updatedProfessor.getLastName());
            professor.setCin(updatedProfessor.getCin());
            professor.setEmail(updatedProfessor.getEmail());
            professor.setPhone(updatedProfessor.getPhone());
            professorRepository.save(professor);
        }
        return "redirect:/AdminSp/professors";
    }

    @RequestMapping("/professors/add")
    public String addProfessor(@RequestBody ProfessorDTO newProfessor) {
        Professor professor = new Professor();
        professor.setFirstName(newProfessor.getFirstName());
        professor.setLastName(newProfessor.getLastName());
        professor.setCin(newProfessor.getCin());
        professor.setEmail(newProfessor.getEmail());
        professor.setPhone(newProfessor.getPhone());
        professor.setCreatedAt(LocalDateTime.now());
        professorRepository.save(professor);
        return "redirect:/AdminSp/professors";
    }
}
