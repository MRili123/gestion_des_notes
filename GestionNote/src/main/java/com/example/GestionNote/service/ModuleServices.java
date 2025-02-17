package com.example.GestionNote.service;

import com.example.GestionNote.DTO.ModuleDTO;
import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.ModuleRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    @Autowired
    private EnrollmentServices enrollmentServices;

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

    public byte[] downloadGradesStructureFile(Module module, String session, String academicYear) {
        try {
            // read the file from the resources folder
            ClassPathResource resource = new ClassPathResource("module_grades.xlsx");
            Path path = resource.getFile().toPath();
            // Read the workbook
            InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Get the sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Get the enrollements for thsi module
            List<Enrollment> enrollments = enrollmentServices.getEnrollmentsByModuleAndAcademicYear(module.getId(), academicYear);

            // set the header data
            Row firstRow = sheet.getRow(0);
            Row secondRow = sheet.getRow(1);
            firstRow.getCell(1).setCellValue(module.getTitle());
            firstRow.getCell(5).setCellValue(academicYear);
            secondRow.getCell(1).setCellValue(module.getProfessor().getFirstName() + " " + module.getProfessor().getLastName());
            secondRow.getCell(3).setCellValue(session);
            secondRow.getCell(5).setCellValue(module.getLevel().getAlias());

            // set elements headers
            Row headerRow = sheet.getRow(3);
            // foreach element in the module set the header
            int headerCell = 4;
            for (Element element : module.getElements()){
                headerRow.getCell(headerCell).setCellValue(element.getTitle());
                headerCell++;
            }
            // foreach enrollment, add a row
            int headerRowNum = 3;
            for (Enrollment enrollment : enrollments){
                Row row = sheet.createRow(headerRowNum + 1);
                row.createCell(0).setCellValue(enrollment.getStudent().getId());
                row.createCell(1).setCellValue(enrollment.getStudent().getCne());
                row.createCell(2).setCellValue(enrollment.getStudent().getLastName());
                row.createCell(3).setCellValue(enrollment.getStudent().getFirstName());
                // 4 and 5 are for the grades
                // average between 4th and 5th cell
                row.createCell(6).setCellFormula("AVERAGE(E" + (headerRowNum + 2) + ":F" + (headerRowNum + 2) + ")");
                // set the result based on the averag: where V if the average is greater than 12, R if the average is less than 12 and NV IF the AVERAGE IS LESS THAN 8
                row.createCell(7).setCellFormula("IF(G" + (headerRowNum + 2) + ">=12, \"V\", IF(G" + (headerRowNum + 2) + ">=8, \"R\", \"NV\"))");
                headerRowNum++;
            }

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
