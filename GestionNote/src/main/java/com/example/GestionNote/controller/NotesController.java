package com.example.GestionNote.controller;

import com.example.GestionNote.DTO.*;
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
    private ActivityLogService activityLogService;
    @Autowired
    private ModuleServices moduleServices;
    @Autowired
    private StudentServices studentServices;
    @Autowired
    private EnrollmentServices enrollmentServices;
    @Autowired
    private DeliberationServices deliberationServices;
    @Autowired
    private LevelServices levelServices;

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
        model.addAttribute("students", students);
        List<Level> levels = levelServices.getAllLevels();
        model.addAttribute("levels", levels);
        return "AdminNotes/students";
    }

    @RequestMapping("/students/delete/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> deleteProfessor(@PathVariable int id, Model model) {
        Boolean result = studentServices.deleteStudent(id);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deleted student with ID: " + id, userId);
        }
        return ResponseEntity.ok("Student deleted successfully");
    }

    @RequestMapping("/students/edit/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> editProfessor(@PathVariable int id, @RequestBody StudentDTO updatedStudent, Model model) {
        User admin = (User) model.getAttribute("user");
        Boolean result = studentServices.updateStudent(id, updatedStudent, admin);
        if(result) {
            // LOG ACTIVITY
            Integer userId = admin.getId();
            activityLogService.save("Updated student with ID: " + id, userId);
        }
        return ResponseEntity.ok("Student updated successfully");
    }

    @PostMapping("/students/add")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> addStudent(@RequestBody StudentDTO newStudent, Model model) {
        Boolean result = studentServices.createStudent(newStudent);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new student", userId);
            return ResponseEntity.ok("student added successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the student");
    }

    @RequestMapping("/students/structure/download")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<byte[]> downloadStudentsStructure(Model model) {
        try {
            byte[] studentsFile = studentServices.downloadStudentsStructureFile();
            if (studentsFile == null) return ResponseEntity.notFound().build();

            String fileName = "students_structure.xlsx";

            // Set the headers and return the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Downloaded students structure EXCEL file", userId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(studentsFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/students/structure/upload")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> uploadStudentsStructure(MultipartFile studentsFile, Model model) {
        try {
            // Ensure the file is XLSX
            if (!Objects.equals(studentsFile.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type, please upload an XLSX file");
            }
            // convert the file to bytes
            byte[] byteArrayFile = studentsFile.getBytes();

            String result = studentServices.uploadStudentsStructureFile(byteArrayFile);

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save(result, userId);

            return ResponseEntity.ok(result);
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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
            activityLogService.save("Downloaded module " + module.getId() + " Grades EXCEL file - Session Normale", userId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(gradesFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/module/session-rattrapage/download")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<byte[]> downloadSessionRattrapage(@RequestParam("moduleId") int moduleId, @RequestParam("academicYear") String academicYear , Model model) {
        try {
            Module module = moduleServices.getModuleById(moduleId);
            byte[] gradesFile = moduleServices.downloadGradesStructureFile(module, "Rattrapage", academicYear);
            if (module == null || gradesFile == null) return ResponseEntity.notFound().build();

            String fileName = "session_rattrapage_" + module.getCode() + ".xlsx";

            // Set the headers and return the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Downloaded module " + module.getId() + " Grades EXCEL file - Session Rattrapage", userId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(gradesFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/module/grades-file/upload")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> uploadGrades(ModuleGradesUploadDTO gradesUploadDTO, Model model) {
        try {
            // Ensure the file is XLSX
            if (!Objects.equals(gradesUploadDTO.getExcelFile().getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type, please upload an XLSX file");
            }
            // Test session
            if(!Objects.equals(gradesUploadDTO.getSession(), "NORMALE") && !Objects.equals(gradesUploadDTO.getSession(), "RATTRAPAGE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid session type");
            }

            String result = moduleServices.uploadGrades(gradesUploadDTO);

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Grades uploaded for module " + gradesUploadDTO.getModuleId() + " - " + gradesUploadDTO.getSession(), userId);

            return ResponseEntity.ok(result);
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @RequestMapping("/deliberations")
    public String deliberations(Model model) {
        List<Deliberation> deliberations = deliberationServices.getAll();
        List<Level> levels = levelServices.getAllLevels();
        List<String> academicYears = enrollmentServices.getAcademicYears();
        model.addAttribute("deliberations", deliberations);
        model.addAttribute("levels", levels);
        model.addAttribute("academicYears", academicYears);
        return "AdminNotes/deliberations";
    }

    @RequestMapping("/deliberation/generate")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<byte[]> generateDeliberationFile(@RequestParam("levelId") int levelId, @RequestParam("academicYear") String academicYear, Model model) {
        try {
            Level level = levelServices.getLevelById(levelId);
            if (level == null) return ResponseEntity.notFound().build();

            byte[] deliberationFile = deliberationServices.generateDeliberationFile(level, academicYear);
            String fileName = "deliberation_" + level.getAlias() + ".xlsx";

            // Set the headers and return the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deliberation generated for level " + level.getId(), userId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(deliberationFile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/deliberation/upload")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> uploadDeliberation(MultipartFile deliberationFile, Model model) {
        try {
            // Ensure the file is XLSX
            if (!Objects.equals(deliberationFile.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type, please upload an XLSX file");
            }
            // convert the file to bytes
            byte[] byteArrayFile = deliberationFile.getBytes();

            String result = deliberationServices.uploadDeliberationFile(byteArrayFile);

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save(result, userId);

            return ResponseEntity.ok(result);
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

