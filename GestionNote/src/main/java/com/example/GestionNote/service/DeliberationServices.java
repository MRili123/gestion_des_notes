package com.example.GestionNote.service;

import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.DeliberationRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DeliberationServices {
    private final DeliberationRepository deliberationRepository;
    private final EnrollmentServices enrollmentServices;
    private final StudentServices studentServices;
    private final ExamServices examServices;

    public DeliberationServices(DeliberationRepository deliberationRepository, EnrollmentServices enrollmentServices, StudentServices studentServices, ExamServices examServices) {
        this.deliberationRepository = deliberationRepository;
        this.enrollmentServices = enrollmentServices;
        this.studentServices = studentServices;
        this.examServices = examServices;
    }

    public List<Deliberation> getAll() {
        return deliberationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public byte[] generateDeliberationFile(Level level, String academicYear) {
        try {
            // read the file from the resources folder
            ClassPathResource resource = new ClassPathResource("deliberation.xlsx");
            Path path = resource.getFile().toPath();
            // Read the workbook
            InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Get the sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Set the header data
            sheet.getRow(0).getCell(1).setCellValue(academicYear);
            sheet.getRow(0).getCell(3).setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            sheet.getRow(1).getCell(1).setCellValue(level.getAlias());

            // Set modules in header row

            Set<Module> modules = level.getModules();
            Row modulesTitleRow = sheet.getRow(3);
            Row elementsTitleRow = sheet.createRow(4);

            // Set the header style
            // Align the text to the center
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);
            // Set the background color to gray
            XSSFColor gray = new XSSFColor(new Color(208,208,208), null);
            headerStyle.setFillForegroundColor(gray);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Add borders
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            int columnCursor = 4;
            // create hashmap to track all the students enrolled in the modules and modules and their columnstart
            Map<Student, List<Module>> studentsWithEnrollments = new HashMap<>();
            Map<Integer, Integer> modulesIdsWithColumnStart = new HashMap<>();
            for (Module module : modules) {
                // Shift the cells to the right
                modulesTitleRow.shiftCellsRight(columnCursor, columnCursor + 1, 4);
                elementsTitleRow.shiftCellsRight(columnCursor, columnCursor + 1, 4);

                // Apply style to all cells in the merged region
                for (int i = columnCursor; i <= columnCursor + 3; i++) {
                    Cell cell = modulesTitleRow.createCell(i);
                    cell.setCellStyle(headerStyle);
                }

                // merge the cells of the module header
                sheet.addMergedRegion(new CellRangeAddress(3, 3, columnCursor, columnCursor + 3));

                // Set the module name with the professor name and set the style
                modulesTitleRow.createCell(columnCursor).setCellValue(module.getTitle() + " (" + module.getProfessor().getFirstName() + " " + module.getProfessor().getLastName() + ")");
                modulesTitleRow.getCell(columnCursor).setCellStyle(headerStyle);


                // Set the elements header (foreach element in the module)
                int i = columnCursor;
                for (Element element : module.getElements()){
                    elementsTitleRow.createCell(i).setCellValue(element.getTitle());
                    elementsTitleRow.getCell(i).setCellStyle(headerStyle);
                    i++;
                }

                // Set the Moyenne and Validation columns
                elementsTitleRow.createCell(columnCursor + 2).setCellValue("Moyenne");
                elementsTitleRow.getCell(columnCursor + 2).setCellStyle(headerStyle);
                elementsTitleRow.createCell(columnCursor + 3).setCellValue("Validation");
                elementsTitleRow.getCell(columnCursor + 3).setCellStyle(headerStyle);

                // Put the module with its column start
                modulesIdsWithColumnStart.put(module.getId(), columnCursor);

                List<Enrollment> enrollments = enrollmentServices.getEnrollmentsByModuleIdAndAcademicYear(module.getId(), academicYear);
                for (Enrollment enrollment : enrollments) {
                    studentsWithEnrollments.computeIfAbsent(enrollment.getStudent(), _ -> new ArrayList<>()).add(enrollment.getModule());
                }

                // Increment the column cursor
                columnCursor += 4;
            }

            // Now we have all the students and modules with their column start and headers done, we can now fill the table
            int rowCursor = 5;
            Map<Integer, String> studentIdWithGlobalMoyenneFormula = new HashMap<>();
            for (Map.Entry<Student, List<Module>> entry : studentsWithEnrollments.entrySet()) {
                Student student = entry.getKey();
                List<Module> studentModules = entry.getValue();
                Row studentRow = sheet.createRow(rowCursor);
                studentRow.createCell(0).setCellValue(student.getId());
                studentRow.createCell(1).setCellValue(student.getCne());
                studentRow.createCell(2).setCellValue(student.getLastName());
                studentRow.createCell(3).setCellValue(student.getFirstName());

                // Initialize the student's global moyenne formula (going to be used afterwards)
                studentIdWithGlobalMoyenneFormula.put(student.getId(), "AVERAGE(");

                // Fill the table with the student's grades
                for (Module module : studentModules) {
                    // find the column start of the module using module.getId() as key searcher
                    int columnStart = modulesIdsWithColumnStart.get(module.getId());

                    // Fill the grades
                    for (Element element : module.getElements()) {
                        // Get the exam the best grade of the student between NORMALE and RATTRAPAGE
                        Exam exam = examServices.getTheBestGradedExamForStudent(student.getId(), element.getId(), academicYear);

                        // Get the cell of the element
                        Cell elemementCell = studentRow.createCell(columnStart);
                        elemementCell.setCellValue(exam.getGrade());
                        columnStart++;
                    }

                    // Set formulas for Moyenne and Validation
                    columnStart = modulesIdsWithColumnStart.get(module.getId());
                    int columnMoyenne = modulesIdsWithColumnStart.get(module.getId()) + 2;
                    int columnValidation = modulesIdsWithColumnStart.get(module.getId()) + 3;
                    Cell moyenneCell = studentRow.createCell(columnMoyenne);
                    Cell validationCell = studentRow.createCell(columnValidation);

                    // Set the formula for Moyenne
                    String element1 = new CellReference(rowCursor, columnStart).formatAsString();
                    String element2 = new CellReference(rowCursor, columnStart + 1).formatAsString();
                    moyenneCell.setCellFormula("AVERAGE(" + element1 + ":" + element2 + ")");

                    // Add the moyenne cell reference to the student's global moyenne formula
                    studentIdWithGlobalMoyenneFormula.put(student.getId(), studentIdWithGlobalMoyenneFormula.get(student.getId()) + moyenneCell.getAddress().formatAsString() + ":");

                    // Set the formula for Validation
                    validationCell.setCellFormula("IF(" + moyenneCell.getAddress() + " >= 12, \"V\", \"NV\")");
                }
                rowCursor++;
            }
            rowCursor = 5;

            // Calculate global moyenne and Rank
            int globalMoyenneColumnIndex = Collections.max(modulesIdsWithColumnStart.values()) + 4;
            int globalRankColumnIndex = globalMoyenneColumnIndex + 1;

            // Excel is 1-based index, so we need to add 1 to the row index
            int startRow = rowCursor + 1;
            int endRow = startRow + studentsWithEnrollments.size() - 1;
            String colLetter = CellReference.convertNumToColString(globalMoyenneColumnIndex);

            String range = "$" + colLetter + "$" + startRow + ":$" + colLetter + "$" + endRow;

            for (int i = rowCursor; i < rowCursor + studentsWithEnrollments.size(); i++) {
                Row studentRow = sheet.getRow(i);
                Cell globalMoyenneCell = studentRow.createCell(globalMoyenneColumnIndex);
                Cell globalRankCell = studentRow.createCell(globalRankColumnIndex);

                int studentId = (int) studentRow.getCell(0).getNumericCellValue();
                String globalMoyenneFormula = studentIdWithGlobalMoyenneFormula.get(studentId);
                globalMoyenneFormula = globalMoyenneFormula.substring(0, globalMoyenneFormula.length() - 1) + ")";
                globalMoyenneCell.setCellFormula(globalMoyenneFormula);

                // Set the rank formula
                String rankFormula = "RANK(" + globalMoyenneCell.getAddress().formatAsString() + ", " + range + ", 0)";
                globalRankCell.setCellFormula(rankFormula);
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
