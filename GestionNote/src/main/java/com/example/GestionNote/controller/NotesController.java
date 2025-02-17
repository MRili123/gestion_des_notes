package com.example.GestionNote.controller;

import com.example.GestionNote.DTO.ElementDTO;
import com.example.GestionNote.DTO.LevelDTO;
import com.example.GestionNote.DTO.ModuleDTO;
import com.example.GestionNote.DTO.ProfessorDTO;
import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.UserRepository;
import com.example.GestionNote.service.*;
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
    private ModuleServices moduleServices;
    @Autowired
    private StudentServices studentServices;
    @Autowired
    private ActivityLogService activityLogService;
    @Autowired
    private EnrollmentServices enrollmentServices;

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
    public String students(Model model) {
        List<Student> students = studentServices.getStudents();
        model.addAttribute("professors", students);
        return "AdminNotes/students";
    }

    @RequestMapping("/grades")
    public String grades(Model model) {
        List<Module> modules = moduleServices.getAllModules();
        List<String> academicYears = enrollmentServices.getAcademicYears();
        model.addAttribute("modules", modules);
        model.addAttribute("academicYears", academicYears);
        return "AdminNotes/grades";
    }

    @RequestMapping("/module/session-normale/download")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<byte[]> downloadSessionNormale(@RequestParam("moduleId") int moduleId, @RequestParam("academicYear") String academicYear , Model model) {
        try {
            Module module = moduleServices.getModuleById(moduleId);
            byte[] gradesFile = moduleServices.downloadGradesStructureFile(module, "Normale", academicYear);
            if (module == null || gradesFile == null) return ResponseEntity.notFound().build();

            String fileName = "session_normale_" + module.getCode() + ".xlsx";

            // Set the headers and return the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Downloaded grades structure file for module " + module.getId(), userId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(gradesFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

