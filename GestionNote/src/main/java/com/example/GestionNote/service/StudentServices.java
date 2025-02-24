package com.example.GestionNote.service;

import com.example.GestionNote.DTO.StudentDTO;
import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.LevelRepository;
import com.example.GestionNote.repository.StudentRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Service
public class StudentServices {
    private final StudentRepository studentRepository;
    private final LevelRepository levelRepository;
    private final LevelServices levelServices;
    private final ModuleServices moduleServices;
    private final EnrollmentServices enrollmentServices;
    private final StudentDataHistoryService studentDataHistoryService;

    public StudentServices(StudentRepository studentRepository, LevelRepository levelRepository, LevelServices levelServices, ModuleServices moduleServices, EnrollmentServices enrollmentServices, StudentDataHistoryService studentDataHistoryService) {
        this.studentRepository = studentRepository;
        this.levelRepository = levelRepository;
        this.levelServices = levelServices;
        this.moduleServices = moduleServices;
        this.enrollmentServices = enrollmentServices;
        this.studentDataHistoryService = studentDataHistoryService;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(int id) {
        Student student = studentRepository.getStudentById(id);
        int a = 5;
        return student;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findByDeleted(false);
    }

    public Student getStudentByCne(String cne) {
        return studentRepository.findByCne(cne);
    }


    public Boolean deleteStudent(int id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            student.setDeleted(true);
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    public Boolean updateStudent(int id, StudentDTO updatedStudent, User admin) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            int oldLevelId = student.getCurrentLevel().getId();

            // Save old data in history
            StudentDataHistory studentDataHistory = new StudentDataHistory(
                    student.getCne(),
                    student.getFirstName(),
                    student.getLastName(),
                    admin,
                    student,
                    student.getCurrentLevel()
            );
            studentDataHistoryService.save(studentDataHistory);

            // Update student data
            student.setFirstName(updatedStudent.getFirstName());
            student.setLastName(updatedStudent.getLastName());
            student.setCne(updatedStudent.getCne());
            Level currentlevel = levelServices.getLevelById(updatedStudent.getCurrentLevelId());
            student.setCurrentLevel(currentlevel);
            studentRepository.save(student);



            // DELETE ALL OLD ENROLLMENTS
            String academicYear = LocalDate.now().getYear() + "/" + (LocalDate.now().getYear() + 1);
            List<Module> oldModukles = moduleServices.getModulesByLevelId(oldLevelId);
            for (Module module : oldModukles) {
                Enrollment enrollment = enrollmentServices.getEnrollmentsToDeleteForUpdate(module.getId(), student.getId(), academicYear);
                if (enrollment != null) {
                    enrollmentServices.deleteEnrollment(enrollment);
                }
            }

            // Enroll student to all modules
            List<Module> newModules = moduleServices.getModulesByLevelId(currentlevel.getId());
            for (Module module : newModules) {
                Enrollment enrollment = new Enrollment(
                        academicYear,
                        student,
                        module
                );
                enrollmentServices.saveEnrollment(enrollment);
            }

            return true;
        }
        return false;
    }

    public Boolean createStudent(StudentDTO newStudent) {
        Student student = new Student(
                newStudent.getCne(),
                newStudent.getFirstName(),
                newStudent.getLastName(),
                levelServices.getLevelById(newStudent.getCurrentLevelId())
        );

        // Enroll student to all modules
        List<Module> modules = moduleServices.getModulesByLevelId(newStudent.getCurrentLevelId());
        for (Module module : modules) {
            String academicYear = LocalDate.now().getYear() + "/" + (LocalDate.now().getYear() + 1);
            Enrollment enrollment = new Enrollment(
                    academicYear,
                    student,
                    module
            );
            enrollmentServices.saveEnrollment(enrollment);
        }

        studentRepository.save(student);
        return true;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public byte[] downloadStudentsStructureFile() throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource("students_file.xlsx");
            Path path = resource.getFile().toPath();
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }
    }
    @Transactional
    public String uploadStudentsStructureFile(byte[] file)  throws IOException, NonUniqueResultException {
        // Create a workbook from the file
        InputStream inputStream = new ByteArrayInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet studentsSheet = workbook.getSheetAt(0);

        for (int i = 1; i < studentsSheet.getPhysicalNumberOfRows(); i++){
            String studentCne = null;
            String studentLastName = null;
            String studentFirstName = null;
            Integer currentLevelId = null;
//            String inscriptionType = null;
            try {
                studentCne = studentsSheet.getRow(i).getCell(0).getStringCellValue();
                studentLastName = studentsSheet.getRow(i).getCell(1).getStringCellValue();
                studentFirstName = studentsSheet.getRow(i).getCell(2).getStringCellValue();
                currentLevelId = (int) studentsSheet.getRow(i).getCell(3).getNumericCellValue();
//                inscriptionType = studentsSheet.getRow(i).getCell(4).getStringCellValue();
            } catch (Exception e) {
                throw new RuntimeException("Invalid student data at row " + (i + 1));
            }

            Level level = levelServices.getLevelById(currentLevelId);
            Student student = new Student(
                    studentCne,
                    studentFirstName,
                    studentLastName,
                    level
            );
            studentRepository.save(student);

            // Enroll student to all modules
            List<Module> modules = moduleServices.getModulesByLevelId(currentLevelId);
            for (Module module : modules) {
                String academicYear = LocalDate.now().getYear() + "/" + (LocalDate.now().getYear() + 1);
                Enrollment enrollment = new Enrollment(
                        academicYear,
                        student,
                        module
                );
                enrollmentServices.saveEnrollment(enrollment);
            }
        }

        return "Students uploaded successfully";
    }
}
