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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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

    @RequestMapping("/professors")
    public String professors(Model model) {
        List<Professor> professors = professorServices.getAllProfessors();
        model.addAttribute("professors", professors);
        return "AdminSp/professors";
    }

    @RequestMapping("/professors/delete/{id}")
    public ResponseEntity<String> deleteProfessor(@PathVariable int id) {
        Boolean result = professorServices.deleteProfessor(id);
        if(result) return ResponseEntity.ok("Professor deleted successfully");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the professor");
    }

    @RequestMapping("/professors/edit/{id}")
    public ResponseEntity<String> editProfessor(@PathVariable int id, @RequestBody ProfessorDTO updatedProfessor) {
        Boolean result = professorServices.updateProfessor(id, updatedProfessor);
        if(result) return ResponseEntity.ok("Professor updated successfully");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the professor");
    }

    @RequestMapping("/professors/add")
    public ResponseEntity<String> addProfessor(@RequestBody ProfessorDTO newProfessor) {
        Boolean result = professorServices.createProfessor(newProfessor);
        if(result) return ResponseEntity.ok("Professor added successfully");
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
    public ResponseEntity<String> deleteFiliere(@PathVariable int id) {
        Boolean result = filiereServices.deleteFiliere(id);
        if(result) return ResponseEntity.ok("Filiere deleted successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the filiere");
    }

    @RequestMapping("/filieres/add")
    public ResponseEntity<String> addFiliere(@RequestBody Filiere newFiliere) {
        Boolean result = filiereServices.createFiliere(newFiliere);
        if (result) return ResponseEntity.ok("Filiere added successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the filiere");
    }

    @RequestMapping("/filieres/edit/{id}")
    public ResponseEntity<String> editFiliere(@PathVariable int id, @RequestBody Filiere updatedFiliere) {
        Boolean result = filiereServices.updateFiliere(id, updatedFiliere);
        if (result) return ResponseEntity.ok("Filiere updated successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the filiere");
    }

    @RequestMapping("/filieres/template/download")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] template = filiereServices.getTemplateFileXLSX();

        // Set the headers and return the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "new_structure_filiere.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(template);
    }

    @RequestMapping("/filieres/structure/upload")
    public ResponseEntity<String> uploadStructure(@RequestParam("file") MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes(); // Convert MultipartFile to byte[]
            String result = filiereServices.createFiliereFromXLSX(fileBytes);
            return ResponseEntity.ok(result);
        } catch (IncorrectResultSizeDataAccessException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data already exists in the database, if you want to update it, please use the update function");
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
    public ResponseEntity<String> deleteLevel(@PathVariable int id) {
        Boolean result = levelServices.deleteLevel(id);
        if(result) return ResponseEntity.ok("Level deleted successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the level");
    }

    @RequestMapping("/levels/add")
    public ResponseEntity<String> addLevel(@RequestBody LevelDTO newLevel) {
        Boolean result = levelServices.createLevel(newLevel);
        if (result) return ResponseEntity.ok("Level added successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the level");
    }

    @RequestMapping("/levels/edit/{id}")
    public ResponseEntity<String> editLevel(@PathVariable int id, @RequestBody LevelDTO updatedLevel) {
        Boolean result = levelServices.updateLevel(id, updatedLevel);
        if (result) return ResponseEntity.ok("Level updated successfully");
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
    public ResponseEntity<String> deleteModule(@PathVariable int id) {
        Boolean result = moduleServices.deleteModule(id);
        if(result) return ResponseEntity.ok("Module deleted successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the module");
    }

    @RequestMapping("/modules/add")
    public ResponseEntity<String> addModule(@RequestBody ModuleDTO newModule) {
        Boolean result = moduleServices.createModule(newModule);
        if (result) return ResponseEntity.ok("Module added successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the module");
    }

    @RequestMapping("/modules/edit/{id}")
    public ResponseEntity<String> editModule(@PathVariable int id, @RequestBody ModuleDTO updatedModule) {
        Boolean result = moduleServices.updateModule(id, updatedModule);
        if (result) return ResponseEntity.ok("Module updated successfully");
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
    public ResponseEntity<String> deleteElement(@PathVariable int id) {
        Boolean result = elementServices.deleteElement(id);
        if(result) return ResponseEntity.ok("Element deleted successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the element");
    }

    @RequestMapping("/elements/add")
    public ResponseEntity<String> addElement(@RequestBody ElementDTO newElement) {
        Boolean result = elementServices.createElement(newElement);
        if (result) return ResponseEntity.ok("Element added successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the element");
    }

    @RequestMapping("/elements/edit/{id}")
    public ResponseEntity<String> editElement(@PathVariable int id, @RequestBody ElementDTO updatedElement) {
        Boolean result = elementServices.updateElement(id, updatedElement);
        if (result) return ResponseEntity.ok("Element updated successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the element");
    }



}
