package com.example.GestionNote.controller;

import com.example.GestionNote.DTO.*;
import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.UserRepository;
import com.example.GestionNote.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/AdminNotes")
public class NotesController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private com.example.GestionNote.service.studentsServices studentsServices;
    @Autowired
    private ActivityLogService activityLogService;

    // Make the user object accessible to all controller routes
    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                // Handle Optional<User> properly
                Optional<User> userOptional = userRepository.findByUsername(username);
                userOptional.ifPresent(user -> model.addAttribute("user", user));
            }
        }
    }
    @RequestMapping("/home")
    public String home() {
        return "AdminNotes/home";
    }
    @RequestMapping("/students")
    public String professors(Model model) {
        List<Student> students = studentsServices.getAllStudents();
        model.addAttribute("students", students);
        return "AdminNotes/students";
    }

    @RequestMapping("/students/delete/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> deleteProfessor(@PathVariable int id, Model model) {
        Boolean result = studentsServices.deleteStudent(id);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deleted student with ID: " + id, userId);
            return ResponseEntity.ok("Student deleted successfully");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the student");
    }

    @RequestMapping("/students/edit/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> editProfessor(@PathVariable int id, @RequestBody StudentDTO updatedStudent, Model model) {
        Boolean result = studentsServices.updateStudent(id, updatedStudent);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Updated student with ID: " + id, userId);
            return ResponseEntity.ok("Student updated successfully");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the student");
    }

    @PostMapping("/students/add")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> addStudent(@RequestBody StudentDTO newStudent, Model model) {
        Boolean result = studentsServices.createStudent(newStudent);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new student", userId);
            return ResponseEntity.ok("student added successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the student");
    }
}

