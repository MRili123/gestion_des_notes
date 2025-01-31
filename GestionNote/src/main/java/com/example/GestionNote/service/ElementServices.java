package com.example.GestionNote.service;

import com.example.GestionNote.DTO.ElementDTO;
import com.example.GestionNote.model.Element;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.ElementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ElementServices {
    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private ModuleServices moduleServices;

    public List<Element> findAllByDeleted(Boolean deleted) {
        return elementRepository.findAllByDeleted(deleted);
    }

    public Boolean deleteElement(Integer id) {
        Element element = elementRepository.findById(id).orElse(null);
        if (element == null) {
            return false;
        }
        element.setDeleted(true);
        elementRepository.save(element);
        return true;
    }

    public Boolean createElement(ElementDTO newElement) {
        try{
            Element element = new Element();
            Module module = moduleServices.getModuleById(newElement.getModuleId());
            element.setTitle(newElement.getTitle());
            element.setModule(module);
            element.setCreatedAt(LocalDateTime.now());
            elementRepository.save(element);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean updateElement(Integer id, ElementDTO updatedElement) {
        try{
            Element element = elementRepository.findById(id).orElse(null);
            if (element == null) {
                return false;
            }
            Module module = moduleServices.getModuleById(updatedElement.getModuleId());
            element.setTitle(updatedElement.getTitle());
            element.setModule(module);
            element.setUpdatedAt(LocalDateTime.now());
            elementRepository.save(element);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
