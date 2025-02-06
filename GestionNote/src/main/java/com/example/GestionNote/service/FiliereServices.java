package com.example.GestionNote.service;

import com.example.GestionNote.model.*;
import com.example.GestionNote.model.Module;
import com.example.GestionNote.repository.FiliereRepository;
import com.example.GestionNote.repository.LevelPathRepository;
import com.example.GestionNote.repository.ModuleRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FiliereServices {
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private ProfessorServices professorServices;
    @Autowired
    @Lazy
    private LevelServices levelServices;
    @Autowired
    @Lazy
    private LevelPathRepository levelPathRepository;
    @Autowired
    @Lazy
    private ModuleServices moduleServices;
    @Autowired
    @Lazy
    private ElementServices elementServices;
    @Autowired
    private ModuleRepository moduleRepository;

    public List<Filiere> getAllFilieres(){
        return filiereRepository.findByDeleted(false);
    }

    public Filiere getFiliereById(int id){
        return filiereRepository.findById(id).orElse(null);
    }

    public Boolean deleteFiliere(int id){
        Filiere filiere = filiereRepository.findById(id).orElse(null);
        if (filiere != null) {
            filiere.setDeleted(true);
            filiereRepository.save(filiere);
            return true;
        }
        return false;
    }

    public Boolean createFiliere(Filiere newFiliere) {
        Filiere filiere = new Filiere();
        Professor professor = professorServices.getProfessorById(newFiliere.getCoordinator().getId());
        if (professor != null) {
            filiere.setCoordinator(professor);
            filiere.setTitle(newFiliere.getTitle());
            filiere.setAlias(newFiliere.getAlias());
            filiere.setAccreditationStart(newFiliere.getAccreditationStart());
            filiere.setAccreditationEnd(newFiliere.getAccreditationEnd());
            filiere.setCreatedAt(LocalDateTime.now());
            filiereRepository.save(filiere);
            return true;
        }
        return false;
    }

    public Boolean updateFiliere(int id, Filiere updatedFiliere) {
        Filiere filiere = filiereRepository.findById(id).orElse(null);
        if (filiere != null) {
            Professor professor = professorServices.getProfessorById(updatedFiliere.getCoordinator().getId());
            if (professor != null) {
                filiere.setCoordinator(professor);
                filiere.setTitle(updatedFiliere.getTitle());
                filiere.setAlias(updatedFiliere.getAlias());
                filiere.setAccreditationStart(updatedFiliere.getAccreditationStart());
                filiere.setAccreditationEnd(updatedFiliere.getAccreditationEnd());
                filiere.setUpdatedAt(LocalDateTime.now());
                filiereRepository.save(filiere);
                return true;
            }
        }
        return false;
    }

    public byte[] getTemplateFileXLSX() {
        try {
            ClassPathResource resource = new ClassPathResource("new_filiere_structure.xlsx");
            Path path = resource.getFile().toPath();
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }
    }

    public byte[] getStructureFileXLSX(int filiereId){
        try {
            // Get the template file
            ClassPathResource resource = new ClassPathResource("existing_filiere_structure.xlsx");
            Path path = resource.getFile().toPath();
            // Read the workbook
            InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
            Workbook workbook = new XSSFWorkbook(inputStream);
            // Get the filiere
            Filiere filiere = filiereRepository.findById(filiereId).orElse(null);
            if (filiere == null) throw new IllegalArgumentException("The filiere with id " + filiereId + " does not exist");
            // Get the filiere sheet
            Sheet filiereSheet = workbook.getSheetAt(0);
            // Set the filiere data
            Row filiereRow = filiereSheet.createRow(1);
            filiereRow.createCell(0).setCellValue(filiere.getId());
            filiereRow.createCell(1).setCellValue(filiere.getTitle());
            filiereRow.createCell(2).setCellValue(filiere.getAlias());
            filiereRow.createCell(3).setCellValue(filiere.getAccreditationStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            filiereRow.createCell(4).setCellValue(filiere.getAccreditationEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            filiereRow.createCell(5).setCellValue(filiere.getCoordinator().getCin());
            filiereRow.createCell(6).setCellValue(filiere.getCoordinator().getFirstName());
            filiereRow.createCell(7).setCellValue(filiere.getCoordinator().getLastName());
            filiereRow.createCell(8).setCellValue(filiere.getCoordinator().getEmail());
            filiereRow.createCell(9).setCellValue(filiere.getCoordinator().getPhone());
            // Get the levels sheet
            Sheet levelsSheet = workbook.getSheetAt(1);
            // Get the levels
            Set<Level> levels = filiere.getLevels();
            // Loop through the levels
            int i = 1;
            for (Level level : levels) {
                // Get the level path
                Set<LevelPath> levelPaths = level.getCurrentLevelPaths();

                // Set the level data
                for (LevelPath levelPath : levelPaths) {
                    // Get the row (row for each level path)
                    Row row = levelsSheet.createRow(i);
                    row.createCell(0).setCellValue(level.getId());
                    row.createCell(1).setCellValue(level.getTitle());
                    row.createCell(2).setCellValue(level.getAlias());
                    if (levelPath.getNextLevel() != null) {
                        row.createCell(3).setCellValue(levelPath.getNextLevel().getId());
                        row.createCell(4).setCellValue(levelPath.getNextLevel().getAlias());
                    } else {
                        CellStyle style = workbook.createCellStyle();
                        style.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                        style.setFillPattern(FillPatternType.THICK_FORWARD_DIAG);
                        row.createCell(3).setCellStyle(style);
                        row.createCell(4).setCellStyle(style);
                    }
                    i++;
                }
            }
            // Get the modules sheet
            Sheet modulesSheet = workbook.getSheetAt(2);
            // Get the modules
            i = 1;
            for (Level level : levels) {
                Set<Module> modules = level.getModules();
                for (Module module : modules) {
                    Row row = modulesSheet.createRow(i);
                    row.createCell(0).setCellValue(module.getId());
                    row.createCell(1).setCellValue(module.getTitle());
                    row.createCell(2).setCellValue(module.getCode());
                    row.createCell(3).setCellValue(level.getId());
                    row.createCell(4).setCellValue(level.getAlias());
                    row.createCell(5).setCellValue(module.getProfessor().getCin());
                    row.createCell(6).setCellValue(module.getProfessor().getFirstName());
                    row.createCell(7).setCellValue(module.getProfessor().getLastName());
                    row.createCell(8).setCellValue(module.getProfessor().getEmail());
                    row.createCell(9).setCellValue(module.getProfessor().getPhone());
                    i++;
                }
            }
            // Get the elements sheet
            Sheet elementsSheet = workbook.getSheetAt(3);
            // Get the elements
            i = 1;
            for (Level level : levels) {
                Set<Module> modules = level.getModules();
                for (Module module : modules) {
                    Set<Element> elements = module.getElements();
                    for (Element element : elements) {
                        Row row = elementsSheet.createRow(i);
                        row.createCell(0).setCellValue(element.getId());
                        row.createCell(1).setCellValue(element.getTitle());
                        row.createCell(2).setCellValue(module.getId());
                        row.createCell(3).setCellValue(module.getCode());
                        i++;
                    }
                }
            }
            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String createFiliereFromXLSX(byte[] file) throws IOException, NonUniqueResultException {
            // Create a workbook from the file
            InputStream inputStream = new ByteArrayInputStream(file);
            Workbook workbook = new XSSFWorkbook(inputStream);

            /*   GET THE FILIERE SHEET    */

            Sheet filiereSheet = workbook.getSheetAt(0);
            // Check if the first 5 columns are filled
            for (int i = 0; i < 5; i++) {
                if(filiereSheet.getRow(1).getCell(i).getCellType() == CellType.BLANK)
                    throw new IllegalArgumentException("You must fill all the necessary columns in the filiere sheet");
            }

            // Set the filiere data
            Filiere filiere = new Filiere();
            filiere.setTitle(filiereSheet.getRow(1).getCell(0).getStringCellValue());
            filiere.setAlias(filiereSheet.getRow(1).getCell(1).getStringCellValue());
            filiere.setAccreditationStart(filiereSheet.getRow(1).getCell(2).getLocalDateTimeCellValue().toLocalDate());
            filiere.setAccreditationEnd(filiereSheet.getRow(1).getCell(3).getLocalDateTimeCellValue().toLocalDate());
            filiere.setCreatedAt(LocalDateTime.now());

            // Set the coordinator data
            Professor professor = new Professor();
            if(professorServices.getProfessorByCin(filiereSheet.getRow(1).getCell(4).getStringCellValue()) != null) {
                professor = professorServices.getProfessorByCin(filiereSheet.getRow(1).getCell(4).getStringCellValue());
            } else {
                professor.setFirstName(filiereSheet.getRow(1).getCell(5).getStringCellValue());
                professor.setLastName(filiereSheet.getRow(1).getCell(6).getStringCellValue());
                professor.setCin(filiereSheet.getRow(1).getCell(4).getStringCellValue());
                professor.setEmail(filiereSheet.getRow(1).getCell(7).getStringCellValue());
                professor.setPhone(filiereSheet.getRow(1).getCell(8).getStringCellValue());
                professor.setCreatedAt(LocalDateTime.now());
                professorServices.createProfessor(professor);
            }
            filiere.setCoordinator(professor);

            // Save the filiere
            Filiere savedFiliere = filiereRepository.save(filiere);

            /*   GET THE LEVELS SHEET    */

            Sheet levelsSheet = workbook.getSheetAt(1);

            // Read all rows into a list
            ArrayList<Row> rows = new ArrayList<>();
            for (int i = 1; i <= levelsSheet.getPhysicalNumberOfRows(); i++) {
                Row row = levelsSheet.getRow(i);
                // Only consider rows that has the first three columns filled
                if (row != null) {
                    int nonBlankCount = 0;
                    for (int j = 0; j < 3; j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null && cell.getCellType() != CellType.BLANK) {
                            nonBlankCount++;
                        }
                    }
                    if (nonBlankCount == 3) {
                        rows.add(row);
                    }
                }
            }

            Comparator<Row> comparator = (row1, row2) -> {
                int value1 = (int) row1.getCell(2).getNumericCellValue();
                int value2 = (int) row2.getCell(2).getNumericCellValue();
                return Integer.compare(value2, value1); // Descending order
            };

            // Sort the rows based on the "Order" column
            rows.sort(comparator);

            // Loop through the levels desc order
            int nextLevelId = 0; // The last level id
            int maxLevelOrder = 0;
            for (int i = 0; i < rows.size(); i++) {
                Row currentRow = rows.get(i);
                if (currentRow.getCell(0).getStringCellValue().isEmpty())
                    throw new IllegalArgumentException("You must fill all the columns in the levels sheet");
                Level level = new Level();
                level.setTitle(currentRow.getCell(0).getStringCellValue());
                level.setAlias(currentRow.getCell(1).getStringCellValue());
                level.setCreatedAt(LocalDateTime.now());
                level.setFiliere(savedFiliere);
                levelServices.createLevel(level);

                // Resolve the level path
                LevelPath levelPath = new LevelPath();
                levelPath.setLevel(level);
                levelPath.setCreatedAt(LocalDateTime.now());

                // If the level has no next levels, add a LevelPath with nextLevel set to null
                // i == 0 meaning we are at the first element in the list which is the last level because we sorted the list desc
                if(i == 0 || (int) currentRow.getCell(2).getNumericCellValue() == maxLevelOrder) {
                    levelPath.setNextLevel(null);
                    nextLevelId = level.getId();
                    maxLevelOrder = (int) currentRow.getCell(2).getNumericCellValue();
                } else {
                    levelPath.setNextLevel(levelServices.getLevelById(nextLevelId));
                    nextLevelId = level.getId();
                }
                levelPathRepository.save(levelPath);
            }

            /*   GET THE MODULES SHEET    */

            Sheet modulesSheet = workbook.getSheetAt(2);

            // Loop through the modules
            for (int i = 1; i <= modulesSheet.getPhysicalNumberOfRows(); i++) {
                Row row = modulesSheet.getRow(i);
                // Check if rows are not null and the first 4 columns are filled
                if(row == null) continue;
                for (int j = 0; j < 4; j++) {
                    if(modulesSheet.getRow(1).getCell(j).getCellType() == CellType.BLANK)
                        throw new IllegalArgumentException("You must fill all the columns in the Module sheet");
                }

                Module module = new Module();
                module.setTitle(row.getCell(0).getStringCellValue());
                module.setCode(row.getCell(1).getStringCellValue());
                module.setCreatedAt(LocalDateTime.now());

                // Get the level
                Level level = levelServices.getLevelByAlias(row.getCell(2).getStringCellValue());
                if (level == null) throw new IllegalArgumentException("The level with alias " + row.getCell(2).getStringCellValue() + " does not exist");
                module.setLevel(level);

                // Get the teacher
                Professor teacher = professorServices.getProfessorByCin(row.getCell(3).getStringCellValue());
                if(teacher == null) {
                    teacher = new Professor();
                    // Check if the teacher columns are filled
                    for (int j = 4; j < 8; j++) {
                        if(modulesSheet.getRow(1).getCell(j).getCellType() == CellType.BLANK)
                            throw new IllegalArgumentException("You must fill all the columns for the teacher in case the teacher is not existant");
                    }
                    teacher.setFirstName(row.getCell(4).getStringCellValue());
                    teacher.setLastName(row.getCell(5).getStringCellValue());
                    teacher.setCin(row.getCell(3).getStringCellValue());
                    teacher.setEmail(row.getCell(6).getStringCellValue());
                    teacher.setPhone(row.getCell(7).getStringCellValue());
                    teacher.setCreatedAt(LocalDateTime.now());
                    professorServices.createProfessor(teacher);
                }
                module.setProfessor(teacher);
                moduleRepository.save(module);
            }

            /*   GET THE ELEMENTS SHEET    */

            Sheet elementsSheet = workbook.getSheetAt(3);

            // Loop through the elements
            for (int i = 1; i <= elementsSheet.getPhysicalNumberOfRows(); i++) {
                Row row = elementsSheet.getRow(i);
                if(row == null) continue;
                // Check if all the columns are filled
                for (int j = 0; j < 2; j++) {
                    if(elementsSheet.getRow(1).getCell(j).getCellType() == CellType.BLANK)
                        throw new IllegalArgumentException("You must fill all the columns in the Element sheet");
                }

                // Fill the element data
                Element element = new Element();
                element.setTitle(row.getCell(0).getStringCellValue());

                // Get the module
                Module module = moduleServices.getModuleByCode(row.getCell(1).getStringCellValue());
                if (module == null) throw new IllegalArgumentException("The module with code " + row.getCell(1).getStringCellValue() + " does not exist");

                element.setModule(module);
                element.setCreatedAt(LocalDateTime.now());
                elementServices.createElement(element);
            }

        return "Filiere created successfully";
    }

    @Transactional
    public String updateFiliereFromXLSX(byte[] file) throws IOException {
        // Create a workbook from the file
        InputStream inputStream = new ByteArrayInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);

        /*   GET THE FILIERE SHEET    */
        Sheet filiereSheet = workbook.getSheetAt(0);
        // Check if the first 5 columns are filled
        for (int i = 0; i < 6; i++) {
            if(filiereSheet.getRow(1).getCell(i).getCellType() == CellType.BLANK)
                throw new IllegalArgumentException("You must fill all the columns in the filiere sheet");
        }
        // Get the filiere from db
        Filiere filiere = filiereRepository.findById((int) filiereSheet.getRow(1).getCell(0).getNumericCellValue()).orElse(null);
        if (filiere == null) throw new IllegalArgumentException("The filiere with id " + (int) filiereSheet.getRow(0).getCell(0).getNumericCellValue() + " does not exist");

        // Set the filiere with the new data
        filiere.setTitle(filiereSheet.getRow(1).getCell(1).getStringCellValue());
        filiere.setAlias(filiereSheet.getRow(1).getCell(2).getStringCellValue());

        LocalDate accreditationStart = filiereSheet.getRow(1).getCell(3).getCellType() == CellType.NUMERIC ?
                filiereSheet.getRow(1).getCell(3).getLocalDateTimeCellValue().toLocalDate() :
                LocalDate.parse(filiereSheet.getRow(1).getCell(3).getStringCellValue(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate accreditationEnd = filiereSheet.getRow(1).getCell(4).getCellType() == CellType.NUMERIC ?
                filiereSheet.getRow(1).getCell(4).getLocalDateTimeCellValue().toLocalDate() :
                LocalDate.parse(filiereSheet.getRow(1).getCell(4).getStringCellValue(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        filiere.setAccreditationStart(accreditationStart);
        filiere.setAccreditationEnd(accreditationEnd);

        // Set the new coordinator
        Professor newCoordinator = professorServices.getProfessorByCin(filiereSheet.getRow(1).getCell(5).getStringCellValue());
        if(filiere.getCoordinator() != newCoordinator) {
            // Check if the new coordinator exists already in db
            if(newCoordinator != null) {
                filiere.setCoordinator(newCoordinator);
            } else { // Otherwise create a new professor
                Professor professor = new Professor();
                professor.setFirstName(filiereSheet.getRow(1).getCell(6).getStringCellValue());
                professor.setLastName(filiereSheet.getRow(1).getCell(7).getStringCellValue());
                professor.setCin(filiereSheet.getRow(1).getCell(5).getStringCellValue());
                professor.setEmail(filiereSheet.getRow(1).getCell(8).getStringCellValue());
                professor.setPhone(filiereSheet.getRow(1).getCell(9).getStringCellValue());
                professor.setCreatedAt(LocalDateTime.now());
                professorServices.createProfessor(professor);
                filiere.setCoordinator(professor);
            }
        }
        filiere.setUpdatedAt(LocalDateTime.now());
        filiereRepository.save(filiere);

        /* Get the levels sheet */
        Sheet levelsSheet = workbook.getSheetAt(1);

        // Read all rows into a list exclude blank rows
        ArrayList<Row> levelsRows = new ArrayList<>();
        for (int i = 1; i <= levelsSheet.getPhysicalNumberOfRows(); i++) {
            Row row = levelsSheet.getRow(i);
            // Only consider rows that has their title and alias columns filled
            if (row != null) {
                int nonBlankCount = 0;
                for (int j = 1; j < 3; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        nonBlankCount++;
                    }
                }
                if (nonBlankCount == 2) {
                    levelsRows.add(row);
                }
            }
        }

        // Create a hashmap to store created/updated levels aliases and their IDs
        Map<String, Integer> trackedLevels = new HashMap<>();

        levelsRows.forEach(levelRow -> {
            // Check if the ID column is filled
            if (levelRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellType() == CellType.BLANK) {
                // Create a new level
                Level newLevel = new Level();
                newLevel.setTitle(levelRow.getCell(1).getStringCellValue());
                newLevel.setAlias(levelRow.getCell(2).getStringCellValue());
                newLevel.setCreatedAt(LocalDateTime.now());
                newLevel.setFiliere(filiere);

                // Save in db
                levelServices.createLevel(newLevel);

                trackedLevels.put(newLevel.getAlias(), newLevel.getId());

            } else {
                // Get the level from db
                Level level = levelServices.getLevelById((int) levelRow.getCell(0).getNumericCellValue());
                // Update normal data
                level.setTitle(levelRow.getCell(1).getStringCellValue());
                level.setAlias(levelRow.getCell(2).getStringCellValue());
                level.setFiliere(filiere);

                // delete all the levelPaths where they have the current level as this one (we will recreate them based on the new data)
                List<LevelPath> levelPaths = levelPathRepository.getAllByLevel(level);
                levelPathRepository.deleteAll(levelPaths);

                level.setUpdatedAt(LocalDateTime.now());
                Level updatedLevel = levelServices.updateLevel(level);
                trackedLevels.put(updatedLevel.getAlias(), updatedLevel.getId());
            }
        });

        // Resolve level paths using the trackedLevels hashmap
        levelsRows.forEach(levelRow -> {
            Cell nextLevelIdCell = levelRow.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell nextLevelAliasCell = levelRow.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell levelAliasCell = levelRow.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            boolean bothCellsBlank = nextLevelIdCell.getCellType() == CellType.BLANK && nextLevelAliasCell.getCellType() == CellType.BLANK;

            // CASE WHERE NEXT LEVEL ID AND ALIAS CELLS ARE BLANK => NEXT LEVEL IS NULL AKA THE CURRENT LEVEL IS THE LAST ONE
            if (bothCellsBlank) {
                // Get the ID from the tracked hashmap using the alias
                int levelId = trackedLevels.get(levelAliasCell.getStringCellValue());
                Level currentLevel = levelServices.getLevelById(levelId);

                // Now create the levelPath relationship
                LevelPath levelPath = new LevelPath();
                levelPath.setLevel(currentLevel);
                levelPath.setNextLevel(null);
                levelPath.setCreatedAt(LocalDateTime.now());
                levelPathRepository.save(levelPath);
            }

            // CASE WHERE THE NEXT LEVEL ID CELL IS NOT NUMERIC OR THE NEXT LEVEL ALIAS CELL IS NOT STRING => INVALID DATA
            if(!bothCellsBlank && nextLevelIdCell.getCellType() != CellType.NUMERIC && nextLevelAliasCell.getCellType() != CellType.STRING)
                throw new IllegalArgumentException("Invalid data in the levels sheet, you must provide either the next level id or the next level alias in a valid format");

            // CASE WHERE THE NEXT LEVEL ID IS PRESENT => GET IT FROM DB
            if(nextLevelIdCell.getCellType() == CellType.NUMERIC){
                // Get the ID from the tracked hashmap using the alias
                int levelId = trackedLevels.get(levelAliasCell.getStringCellValue());
                Level currentLevel = levelServices.getLevelById(levelId);
                // Get the next level
                int nextLevelId = (int) nextLevelIdCell.getNumericCellValue();
                Level nextLevel = levelServices.getLevelById(nextLevelId);

                // Now create the levelPath relationship
                LevelPath levelPath = new LevelPath();
                levelPath.setLevel(currentLevel);
                levelPath.setNextLevel(nextLevel);
                levelPath.setCreatedAt(LocalDateTime.now());
                levelPathRepository.save(levelPath);
            }

            // CASE WHERE THE NEXT LEVEL ID IS NOT PRESENT => GET IT FROM THE ALIAS
            if(nextLevelIdCell.getCellType() == CellType.BLANK && nextLevelAliasCell.getCellType() == CellType.STRING){
                // Get the ID from the tracked hashmap using the alias
                int levelId = trackedLevels.get(levelAliasCell.getStringCellValue());
                Level currentLevel = levelServices.getLevelById(levelId);
                // Get the next level
                String nextLevelAlias = nextLevelAliasCell.getStringCellValue();
                Level nextLevel = levelServices.getLevelByAlias(nextLevelAlias);

                // Now create the levelPath relationship
                LevelPath levelPath = new LevelPath();
                levelPath.setLevel(currentLevel);
                levelPath.setNextLevel(nextLevel);
                levelPath.setCreatedAt(LocalDateTime.now());
                levelPathRepository.save(levelPath);
            }

        });

        /* Get the modules sheet */
        Sheet modulesSheet = workbook.getSheetAt(2);
        for (int i = 1; i <= modulesSheet.getPhysicalNumberOfRows(); i++) {
            Row row = modulesSheet.getRow(i);
            // Check if rows are not null and the "title->teacher cin" columns are filled
            if (row == null) continue;
            for (int j = 1; j < 6; j++) {
                if (modulesSheet.getRow(1).getCell(j).getCellType() == CellType.BLANK)
                    throw new IllegalArgumentException("You must fill all the columns in the Module sheet");
            }

            Cell moduleIdCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell moduleLevelIdCell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell moduleLevelAliasCell = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell moduleTeacherCinCell = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            Module module = new Module();
            // CASE WHERE THE MODULE ID CELL IS BLANK => CREATE A NEW MODULE
            if(moduleIdCell.getCellType() == CellType.BLANK){
                module.setTitle(row.getCell(1).getStringCellValue());
                module.setCode(row.getCell(2).getStringCellValue());
                module.setCreatedAt(LocalDateTime.now());
            }else{
                // CASE WHERE THE ID IS PRESENT => GET THE MODULE FROM DB
                if(moduleLevelIdCell.getCellType() != CellType.NUMERIC)
                    throw new IllegalArgumentException("Invalid level data in the modules sheet, module id must be numeric");

                module = moduleServices.getModuleById((int) moduleIdCell.getNumericCellValue());
                module.setTitle(row.getCell(1).getStringCellValue());
                module.setCode(row.getCell(2).getStringCellValue());
                module.setUpdatedAt(LocalDateTime.now());
            }

            // SET THE LEVEL
            // IF THE LEVEL ID IS PRESENT => GET THE LEVEL FROM DB
            if(moduleLevelIdCell.getCellType() == CellType.NUMERIC){
                Level level = levelServices.getLevelById((int) moduleLevelIdCell.getNumericCellValue());
                module.setLevel(level);
            } else {
                // IF THE LEVEL ALIAS IS PRESENT => GET THE LEVEL FROM DB
                if(moduleLevelAliasCell.getCellType() == CellType.STRING){
                    Level level = levelServices.getLevelByAlias(moduleLevelAliasCell.getStringCellValue());
                    module.setLevel(level);
                } else {
                    throw new IllegalArgumentException("Invalid level data in the modules sheet, you must provide either the level id or the level alias");
                }
            }

            // SET THE TEACHER
            // IF THE TEACHER CIN IS PRESENT => GET THE TEACHER FROM DB
            if(moduleTeacherCinCell.getCellType() == CellType.STRING){
                Professor teacher = professorServices.getProfessorByCin(moduleTeacherCinCell.getStringCellValue());
                if(teacher == null) {
                    teacher = new Professor();
                    // Check if the teacher columns are filled
                    for (int j = 6; j < 10; j++) {
                        if(modulesSheet.getRow(1).getCell(j).getCellType() == CellType.BLANK)
                            throw new IllegalArgumentException("You must fill all the columns for the teacher in case the teacher is not existant");
                    }
                    teacher.setCin(row.getCell(5).getStringCellValue());
                    teacher.setFirstName(row.getCell(6).getStringCellValue());
                    teacher.setLastName(row.getCell(7).getStringCellValue());
                    teacher.setEmail(row.getCell(8).getStringCellValue());
                    teacher.setPhone(row.getCell(9).getStringCellValue());
                    teacher.setCreatedAt(LocalDateTime.now());
                    professorServices.createProfessor(teacher);
                }
                module.setProfessor(teacher);
            } else {
                throw new IllegalArgumentException("Invalid teacher data in the modules sheet, you must provide the teacher cin");
            }

            // Save the module
            moduleServices.updateModule(module);
        }

        /* Get the elements sheet */
        Sheet elementsSheet = workbook.getSheetAt(3);

        for (int i = 1; i <= elementsSheet.getPhysicalNumberOfRows(); i++) {
            Row row = elementsSheet.getRow(i);
            if(row == null) continue;

            Cell elementIdCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell elementTitleCell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell elementModuleIdCell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell elementModuleCodeCell = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            // Check if all the columns are filled
            if(elementTitleCell.getCellType() != CellType.STRING ||
                    (elementModuleIdCell.getCellType() != CellType.NUMERIC && elementModuleCodeCell.getCellType() != CellType.STRING))
                throw new IllegalArgumentException("Invalid data in the elements sheet, you must provide the element title and the module id or code");

            // Fill the element data
            Element element = new Element();

            if(elementModuleIdCell.getCellType() == CellType.BLANK){
               element.setTitle(elementTitleCell.getStringCellValue());
               element.setCreatedAt(LocalDateTime.now());
            }else{
                if(elementModuleIdCell.getCellType() != CellType.NUMERIC)
                    throw new IllegalArgumentException("Invalid data in the elements sheet, the module id must be numeric");
                Element elementFromDb = elementServices.getElementById((int) elementIdCell.getNumericCellValue());
                if(elementFromDb == null)
                    throw new IllegalArgumentException("The element with id " + (int) elementIdCell.getNumericCellValue() + " does not exist, elements sheet");
                element = elementFromDb;
                element.setTitle(elementTitleCell.getStringCellValue());
                element.setUpdatedAt(LocalDateTime.now());
            }

            // SET THE MODULE
            if(elementModuleIdCell.getCellType() != CellType.BLANK) {
                if (elementModuleIdCell.getCellType() != CellType.NUMERIC)
                    throw new IllegalArgumentException("Invalid data in the elements sheet, the module id must be numeric");
                Module module = moduleServices.getModuleById((int) elementModuleIdCell.getNumericCellValue());
                if (module == null)
                    throw new IllegalArgumentException("The module with id " + (int) elementModuleIdCell.getNumericCellValue() + " does not exist, elements sheet");
                element.setModule(module);
            }else{
                if(elementModuleCodeCell.getCellType() != CellType.STRING)
                    throw new IllegalArgumentException("Invalid data in the elements sheet, the module code must be a string");
                Module module = moduleServices.getModuleByCode(elementModuleCodeCell.getStringCellValue());
                if (module == null)
                    throw new IllegalArgumentException("The module with code " + elementModuleCodeCell.getStringCellValue() + " does not exist, elements sheet");
                element.setModule(module);
            }

            // Save the element
            elementServices.updateElement(element);
        }

        return "Filiere updated successfully";
    }

}


