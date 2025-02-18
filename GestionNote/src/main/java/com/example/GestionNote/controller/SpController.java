package com.example.GestionNote.controller;

import com.example.GestionNote.DTO.*;
import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.*;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Controller
@RequestMapping("/AdminSp")
public class SpController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfessorServices professorServices;
    @Autowired
    private FiliereServices filiereServices;
    @Autowired
    private LevelServices levelServices;
    @Autowired
    private ModuleServices moduleServices;
    @Autowired
    private ElementServices elementServices;
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
        return "AdminSp/home";
    }

    @RequestMapping("/professors")
    public String professors(Model model) {
        List<Professor> professors = professorServices.getAllProfessors();
        model.addAttribute("professors", professors);
        return "AdminSp/professors";
    }

    @RequestMapping("/professors/delete/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> deleteProfessor(@PathVariable int id, Model model) {
        Boolean result = professorServices.deleteProfessor(id);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deleted professor with ID: " + id, userId);
            return ResponseEntity.ok("Professor deleted successfully");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the professor");
    }

    @RequestMapping("/professors/edit/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> editProfessor(@PathVariable int id, @RequestBody ProfessorDTO updatedProfessor, Model model) {
        Boolean result = professorServices.updateProfessor(id, updatedProfessor);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Updated professor with ID: " + id, userId);
            return ResponseEntity.ok("Professor updated successfully");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the professor");
    }

    @RequestMapping("/professors/add")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> addProfessor(@RequestBody ProfessorDTO newProfessor, Model model) {
        Boolean result = professorServices.createProfessor(newProfessor);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new professor", userId);
            return ResponseEntity.ok("Professor added successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the professor");
    }

    @RequestMapping("/filieres")
    public String filiere(Model model) {
        List<Filiere> filieres = filiereServices.getAllFilieres();
        List<Professor> professors = professorServices.getAllProfessors();
        model.addAttribute("filieres", filieres);
        model.addAttribute("professors", professors);
        return "AdminSp/filieres";
    }

    @RequestMapping("/filieres/delete/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> deleteFiliere(@PathVariable int id, Model model) {

        Boolean result = filiereServices.deleteFiliere(id);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deleted filiere with ID: " + id, userId);
            return ResponseEntity.ok("Filiere deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the filiere");
    }

    @RequestMapping("/filieres/add")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> addFiliere(@RequestBody Filiere newFiliere, Model model) {

        Boolean result = filiereServices.createFiliere(newFiliere);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new filiere", userId);
            return ResponseEntity.ok("Filiere added successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the filiere");
    }

    @RequestMapping("/filieres/edit/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> editFiliere(@PathVariable int id, @RequestBody Filiere updatedFiliere, Model model) {

        Boolean result = filiereServices.updateFiliere(id, updatedFiliere);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Updated filiere with ID: " + id, userId);
            return ResponseEntity.ok("Filiere updated successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the filiere");
    }

    @RequestMapping("/filieres/template/download")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<byte[]> downloadTemplate(Model model) {

        byte[] template = filiereServices.getTemplateFileXLSX();

        // Set the headers and return the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "new_structure_filiere.xlsx");

        // LOG ACTIVITY
        Integer userId = ((User) model.getAttribute("user")).getId();
        activityLogService.save("Downloaded filiere template", userId);

        return ResponseEntity.ok()
                .headers(headers)
                .body(template);
    }

    @RequestMapping("/filieres/structure/download/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<byte[]> downloadStructure(@PathVariable int id, Model model) {

        try {
            byte[] structure = filiereServices.getStructureFileXLSX(id);
            String filiereName = filiereServices.getFiliereById(id).getTitle();
            // Set the headers and return the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filiereName + ".xlsx");

            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Downloaded filiere structure with ID: " + id, userId);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(structure);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    @RequestMapping("/filieres/structure/upload")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> uploadStructure(@RequestParam("file") MultipartFile file, Model model) {

        try {
            // Ensure the file is XLSX
            if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type, please upload an XLSX file");
            }

            // Convert MultipartFile to byte[]
            byte[] fileBytes = file.getBytes();

            String result = filiereServices.createFiliereFromXLSX(fileBytes);
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new filiere through XLSX file", userId);
            return ResponseEntity.ok(result);
        } catch (IncorrectResultSizeDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data already exists in the database, if you want to update it, please use the update function");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @RequestMapping("/filieres/structure/update")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> updateStructure(@RequestParam("file") MultipartFile file, Model model) {
        try {
            // Ensure the file is XLSX
            if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type, please upload an XLSX file");
            }

            // Convert MultipartFile to byte[]
            byte[] fileBytes = file.getBytes();

            String result = filiereServices.updateFiliereFromXLSX(fileBytes);
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Updated filiere through XLSX file", userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @RequestMapping("/levels")
    public String classes(Model model) {
        List<Filiere> filieres = filiereServices.getAllFilieres();
        List<Level> levels = levelServices.getAllLevels();
        model.addAttribute("filieres", filieres);
        model.addAttribute("levels", levels);
        return "AdminSp/levels";
    }

    @RequestMapping("/levels/delete/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> deleteLevel(@PathVariable int id, Model model) {

        Boolean result = levelServices.deleteLevel(id);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deleted level with ID: " + id, userId);
            return ResponseEntity.ok("Level deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the level");
    }

    @RequestMapping("/levels/add")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> addLevel(@RequestBody LevelDTO newLevel, Model model) {

        Boolean result = levelServices.createLevel(newLevel);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new level", userId);
            return ResponseEntity.ok("Level added successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the level");
    }

    @RequestMapping("/levels/edit/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> editLevel(@PathVariable int id, @RequestBody LevelDTO updatedLevel, Model model) {

        Boolean result = levelServices.updateLevel(id, updatedLevel);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Updated level with ID: " + id, userId);
            return ResponseEntity.ok("Level updated successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the level");
    }

    @RequestMapping("/modules")
    public String modules(Model model) {
        List<Level> levels = levelServices.getAllLevels();
        List<Professor> professors = professorServices.getAllProfessors();
        List<Module> modules = moduleServices.getAllModules();
        model.addAttribute("levels", levels);
        model.addAttribute("professors", professors);
        model.addAttribute("modules", modules);
        return "AdminSp/modules";
    }

    @RequestMapping("/modules/delete/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> deleteModule(@PathVariable int id, Model model) {

        Boolean result = moduleServices.deleteModule(id);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deleted module with ID: " + id, userId);
            return ResponseEntity.ok("Module deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the module");
    }

    @RequestMapping("/modules/add")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> addModule(@RequestBody ModuleDTO newModule, Model model) {

        Boolean result = moduleServices.createModule(newModule);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new module", userId);
            return ResponseEntity.ok("Module added successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the module");
    }

    @RequestMapping("/modules/edit/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> editModule(@PathVariable int id, @RequestBody ModuleDTO updatedModule, Model model) {

        Boolean result = moduleServices.updateModule(id, updatedModule);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Updated module with ID: " + id, userId);
            return ResponseEntity.ok("Module updated successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the module");
    }

    @RequestMapping("/elements")
    public String elements(Model model) {
        List<Element> elements = elementServices.findAllByDeleted(false);
        List<Module> modules = moduleServices.getAllModules();
        model.addAttribute("elements", elements);
        model.addAttribute("modules", modules);
        return "AdminSp/elements";
    }

    @RequestMapping("/elements/delete/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> deleteElement(@PathVariable int id, Model model) {

        Boolean result = elementServices.deleteElement(id);
        if(result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Deleted element with ID: " + id, userId);
            return ResponseEntity.ok("Element deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the element");
    }

    @RequestMapping("/elements/add")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> addElement(@RequestBody ElementDTO newElement, Model model) {

        Boolean result = elementServices.createElement(newElement);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Added new element", userId);
            return ResponseEntity.ok("Element added successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the element");
    }

    @RequestMapping("/elements/edit/{id}")
    @PreAuthorize("@userRepository.findByUsername(authentication.name).get().enabled == true")
    public ResponseEntity<String> editElement(@PathVariable int id, @RequestBody ElementDTO updatedElement, Model model) {

        Boolean result = elementServices.updateElement(id, updatedElement);
        if (result) {
            // LOG ACTIVITY
            Integer userId = ((User) model.getAttribute("user")).getId();
            activityLogService.save("Updated element with ID: " + id, userId);
            return ResponseEntity.ok("Element updated successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the element");
    }


}
