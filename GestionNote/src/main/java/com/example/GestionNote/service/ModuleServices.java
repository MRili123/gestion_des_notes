package com.example.GestionNote.service;

import com.example.GestionNote.DTO.ModuleDTO;
import com.example.GestionNote.DTO.ModuleGradesUploadDTO;
import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.ModuleRepository;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ModuleServices {
    @Lazy
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LevelServices levelServices;
    @Autowired
    private ProfessorServices professorServices;
    @Autowired
    private EnrollmentServices enrollmentServices;
    @Lazy
    @Autowired
    private StudentServices studentServices;
    @Lazy
    @Autowired
    private ElementServices elementServices;
    @Autowired
    private ExamServices examServices;

    public List<Module> getAllModules(){
        return moduleRepository.findAllByDeleted(false);
    }

    public List<Module> getModulesByLevelId(int levelId){
        return moduleRepository.findAllByLevelIdAndDeleted(levelId, false);
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

            // Get the enrollements for this module
            List<Enrollment> enrollments;
            if(Objects.equals(session, "Normale")){
                enrollments = enrollmentServices.getEnrollmentsForGradesFile(module.getId(), academicYear, null, "NORMALE", "NORMALE");
            }else{
                enrollments = enrollmentServices.getEnrollmentsForGradesFile(module.getId(), academicYear, "R", "NORMALE", "RATTRAPAGE");
            }

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
                // foreach element in the module set the grade
                int cellNum = 4;
                for (Element element : module.getElements()){
                    Exam exam = examServices.getExamByStudentIdAndElementIdAndSessionAndAcademicYear(enrollment.getStudent().getId(), element.getId(), session.toUpperCase(), academicYear);
                    if(exam != null){
                        row.createCell(cellNum).setCellValue(exam.getGrade());
                    }
                    cellNum++;
                }
                // average between 4th and 5th cell
                row.createCell(6).setCellFormula("AVERAGE(E" + (headerRowNum + 2) + ":F" + (headerRowNum + 2) + ")");
                if(Objects.equals(session, "Normale")){
                    // set the result based on the averag: where V if the average is greater than 12, R if the average is less than 12 and NV IF the AVERAGE IS LESS THAN 8
                    row.createCell(7).setCellFormula("IF(G" + (headerRowNum + 2) + ">=12, \"V\", IF(G" + (headerRowNum + 2) + ">=8, \"R\", \"NV\"))");
                }
                else{ // Rattrapage
                    // set the result based on the averag: where V if the average is greater than 12 otherwise NV
                    row.createCell(7).setCellFormula("IF(G" + (headerRowNum + 2) + ">=12, \"V\", \"NV\")");
                }
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

    @Transactional
    public String uploadGrades(ModuleGradesUploadDTO moduleGradesUploadDTO) throws IOException, NonUniqueResultException {
        Module module = moduleRepository.findById(moduleGradesUploadDTO.getModuleId()).orElse(null);
        if (module == null) return "Module not found";
        // Read the file
        Workbook workbook = new XSSFWorkbook(moduleGradesUploadDTO.getExcelFile().getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Test data integrity
        String enseignant = null;
        String annee = null;
        String session = null;
        String title = null;
        try {
            enseignant = sheet.getRow(1).getCell(1).getStringCellValue();
            annee = sheet.getRow(0).getCell(5).getStringCellValue();
            session = sheet.getRow(1).getCell(3).getStringCellValue();
            title = sheet.getRow(0).getCell(1).getStringCellValue();
        } catch (Exception e) {
            throw new RuntimeException("Invalid file format");
        }

        if(!Objects.equals(title, module.getTitle())) throw new RuntimeException("Module title mismatch between the file and existing data");
        if(!Objects.equals(annee, moduleGradesUploadDTO.getAcademicYear())) throw new RuntimeException("Academic year mismatch between the file and the request");
        if(!Objects.equals(enseignant, module.getProfessor().getFirstName() + " " + module.getProfessor().getLastName())) throw new RuntimeException("Professor mismatch between the file and existing data");
        if(!Objects.equals(session.toUpperCase(), moduleGradesUploadDTO.getSession())) throw new RuntimeException("Session mismatch between the file and the request");


        // Get rows beggining from the 4th row
        for (int i = 4; i <= sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row.getCell(0).getCellType() == CellType.BLANK) continue;
            // Get the student by ID
            Student student = null;
            try {
                student = studentServices.getStudentById((int) row.getCell(0).getNumericCellValue());
            } catch (Exception e) {
                throw new RuntimeException("Invalid student ID type at row: " + i);
            }
            if (student == null) throw new RuntimeException("Student not found with ID: " + row.getCell(0).getNumericCellValue());
            // Get the enrollment
            Enrollment enrollment = enrollmentServices.getEnrollmentByModuleIdAndStudentId(module.getId(), student.getId());
            if (enrollment == null) throw new RuntimeException("Student not enrolled in this module");
            // Get the elements
            for (int j = 4; j < 6; j++) {
                Element element = null;
                try {
                    element = elementServices.getElementByTitle(sheet.getRow(3).getCell(j).getStringCellValue());
                } catch (Exception e) {
                    throw new RuntimeException("Invalid element title type at row: " + i);
                }
                if(element == null) continue;

                // Test if the exam already exists in case of update
                Exam existingExam = examServices.getExamByStudentIdAndElementIdAndSessionAndAcademicYear(student.getId(), element.getId(), session.toUpperCase(), moduleGradesUploadDTO.getAcademicYear());
                if(existingExam == null) {
                    if(row.getCell(j).getNumericCellValue() < 0 || row.getCell(j).getNumericCellValue() > 20) throw new RuntimeException("Invalid grade value at row: " + i);
                    Exam newExam = new Exam(
                            row.getCell(j).getNumericCellValue(),
                            moduleGradesUploadDTO.getSession(),
                            moduleGradesUploadDTO.getAcademicYear(),
                            student,
                            element
                    );
                    examServices.saveExam(newExam);
                }else{
                    existingExam.setGrade(row.getCell(j).getNumericCellValue());
                    examServices.saveExam(existingExam);
                }
            }
            String validation = null;
            try {
                validation = row.getCell(7).getStringCellValue();
            } catch (Exception e) {
                throw new RuntimeException("Invalid validation value type");
            }
            switch (validation) {
                case "V" -> {
                    enrollment.setResult("V");
                    enrollment.setResultFromSession(session.toUpperCase());
                    enrollment.setUpdatedAt(LocalDateTime.now());
                    enrollmentServices.saveEnrollment(enrollment);
                }
                case "R" -> {
                    if(Objects.equals(moduleGradesUploadDTO.getSession(), "RATTRAPAGE")) throw new RuntimeException("You can't have validation set to R in the Rattrapage session");
                    enrollment.setResult("R");
                    enrollment.setResultFromSession(session.toUpperCase());
                    enrollment.setUpdatedAt(LocalDateTime.now());
                    enrollmentServices.saveEnrollment(enrollment);
                }
                case "NV" -> {
                    enrollment.setResult("NV");
                    enrollment.setResultFromSession(session.toUpperCase());
                    enrollment.setUpdatedAt(LocalDateTime.now());
                    enrollmentServices.saveEnrollment(enrollment);
                }
                default -> throw new RuntimeException("Invalid validation value");
            }
        }

        return "Grades uploaded successfully";
    }
}
