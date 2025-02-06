package com.example.GestionNote.service;

import com.example.GestionNote.DTO.ModuleDTO;
import com.example.GestionNote.model.Level;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.model.Professor;
import com.example.GestionNote.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModuleServices {
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LevelServices levelServices;
    @Autowired
    private ProfessorServices professorServices;

    public List<Module> getAllModules(){
        return moduleRepository.findAllByDeleted(false);
    }

    public Module getModuleById(int id){
        return moduleRepository.findById(id).orElse(null);
    }

    public Module getModuleByCode(String code){
        return moduleRepository.findByCode(code);
    }

    public Boolean deleteModule(int id){
        Module module = moduleRepository.findById(id).orElse(null);
        if (module != null) {
            module.setDeleted(true);
            moduleRepository.save(module);
            return true;
        }
        return false;
    }

    public Boolean createModule(ModuleDTO newModule) {
        Module module = new Module();
        Level level = levelServices.getLevelById(newModule.getLevelId());
        Professor professor = professorServices.getProfessorById(newModule.getTeacherId());
        if (level != null && professor != null) {
            module.setLevel(level);
            module.setProfessor(professor);
            module.setTitle(newModule.getTitle());
            module.setCode(newModule.getCode());
            module.setCreatedAt(LocalDateTime.now());
            moduleRepository.save(module);
            return true;
        }
        return false;
    }

    public Boolean updateModule(int id, ModuleDTO updatedModule) {
        Module moduleToUpdate = moduleRepository.findById(id).orElse(null);
        Level level = levelServices.getLevelById(updatedModule.getLevelId());
        Professor professor = professorServices.getProfessorById(updatedModule.getTeacherId());
        if (moduleToUpdate != null && level != null && professor != null) {
            moduleToUpdate.setLevel(level);
            moduleToUpdate.setProfessor(professor);
            moduleToUpdate.setTitle(updatedModule.getTitle());
            moduleToUpdate.setCode(updatedModule.getCode());
            moduleToUpdate.setUpdatedAt(LocalDateTime.now());
            moduleRepository.save(moduleToUpdate);
            return true;
        }
        return false;
    }

    public Module updateModule(Module module) {
        return moduleRepository.save(module);
    }
}
