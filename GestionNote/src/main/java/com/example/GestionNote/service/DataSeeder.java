package com.example.GestionNote.service;

import com.example.GestionNote.model.*;
import com.example.GestionNote.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FiliereRepository filiereRepository;
    private final LevelRepository levelRepository;
    private final LevelPathRepository levelPathRepository;

    public DataSeeder(ProfessorRepository professorRepository,
                        UserRepository userRepository,
                        FiliereRepository filiereRepository,
                        LevelRepository levelRepository,
                        LevelPathRepository levelPathRepository,
                        PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.filiereRepository = filiereRepository;
        this.levelRepository = levelRepository;
        this.levelPathRepository = levelPathRepository;
    }

    @Override
    public void run(String... args) {
        if (professorRepository.count() == 0) { // Only seed if empty
            List<Professor> professors = Arrays.asList(
                    new Professor("John", "Doe", "CIN12345", "johndoe@example.com", "0612345678"),
                    new Professor("Alice", "Smith", "CIN67890", "alicesmith@example.com", "0698765432"),
                    new Professor("Bob", "Brown", "CIN54321", "bobbrown@example.com", "0678901234"),
                    new Professor("Emily", "Johnson", "CIN11111", "emilyjohnson@example.com", "0654321987"),
                    new Professor("Michael", "Davis", "CIN22222", "michaeldavis@example.com", "0611122334"),
                    new Professor("Sarah", "Miller", "CIN33333", "sarahmiller@example.com", "0687654321"),
                    new Professor("David", "Wilson", "CIN44444", "davidwilson@example.com", "0676543210"),
                    new Professor("Laura", "Taylor", "CIN55555", "laurataylor@example.com", "0665432109"),
                    new Professor("James", "Anderson", "CIN66666", "jamesanderson@example.com", "0699998887"),
                    new Professor("Sophia", "Martinez", "CIN77777", "sophiamartinez@example.com", "0601234567")
            );

            professors.forEach(professor -> {
                professor.setCreatedAt(LocalDateTime.now());
                professor.setUpdatedAt(LocalDateTime.now());
                professor.setDeleted(false);
            });

            professorRepository.saveAll(professors);
            System.out.println("Professors seeded successfully!");
        } else {
            System.out.println("Professors table is not empty, skipping seeding.");
        }

        // Seed three Admins if the User table is empty
        if (userRepository.count() == 0) {
            List<User> admins = Arrays.asList(
                    new User("Admin", "Admin", "admin_sp", "admin_sp@example.com", "G123456", "ADMIN_SP", "0612345678", passwordEncoder.encode("admin_sp")),
                    new User("Admin", "Admin", "admin_user", "admin_user@example.com", "G123456", "ADMIN_USER", "0612345678", passwordEncoder.encode("admin_user")),
                    new User("Admin", "Admin", "admin_notes", "admin_notes@example.com", "G123456", "ADMIN_NOTES", "0612345678", passwordEncoder.encode("admin_notes"))
            );

            userRepository.saveAll(admins);
            System.out.println("Admins seeded successfully!");
        }

        // Seed filieres if the Filiere table is empty
        if (filiereRepository.count() == 0) {
            List<Filiere> filieres = Arrays.asList(
                    new Filiere("Cycle Préparatoire", "CP", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false)),
                    new Filiere("Génie Informatique", "GI", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false)),
                    new Filiere("Génie Civil", "GC", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false)),
                    new Filiere("Génie de l'Eau et de l'Environnement", "GEE", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false)),
                    new Filiere("Génie Mécanique", "GM", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false)),
                    new Filiere("Génie Energétique et Energies Renouvelables", "GEER", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false)),
                    new Filiere("Ingénierie des Données", "ID", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false)),
                    new Filiere("Génie Environnement (Ancienne Filière)", "GE", LocalDate.of(2020, 1, 1), LocalDate.of(2035, 1, 1), professorRepository.findRandomByDeleted(false))
            );

            filiereRepository.saveAll(filieres);
            System.out.println("Filieres seeded successfully!");
        }

        // Seed levels if the Level table is empty
        if(levelRepository.count() == 0) {
            List<Level> levels = Arrays.asList(
                    new Level("Première Année Cycle Préparatoire", "CP1", filiereRepository.findByAlias("CP")),
                    new Level("Deuxième Année Cycle Préparatoire", "CP2", filiereRepository.findByAlias("CP")),
                    new Level("Première Année Cycle Ingénieur", "C. Ing 1", filiereRepository.findByAlias("CP")),
                    new Level("Génie Informatique 1", "GI1", filiereRepository.findByAlias("GI")),
                    new Level("Génie Informatique 2", "GI2", filiereRepository.findByAlias("GI")),
                    new Level("Génie Informatique 3", "GI3", filiereRepository.findByAlias("GI")),
                    new Level("Génie Informatique 3 Option GL", "GI3 GL", filiereRepository.findByAlias("GI")),
                    new Level("Génie Informatique 3 Option BI", "GI3 BI", filiereRepository.findByAlias("GI")),
                    new Level("Génie Informatique 3 Option Médias et Interactions", "GI3 MI", filiereRepository.findByAlias("GI")),
                    new Level("Génie Civil 1", "GC1", filiereRepository.findByAlias("GC")),
                    new Level("Génie Civil 2", "GC2", filiereRepository.findByAlias("GC")),
                    new Level("Génie Civil 3", "GC3", filiereRepository.findByAlias("GC")),
                    new Level("Génie Civil 3 Option HYD", "GC3 HYD", filiereRepository.findByAlias("GC")),
                    new Level("Génie Civil 3 Option BPC", "GC3 BPC", filiereRepository.findByAlias("GC")),
                    new Level("Génie de l'eau et de l'Environnement 1", "GEE1", filiereRepository.findByAlias("GEE")),
                    new Level("Génie de l'eau et de l'Environnement 2", "GEE2", filiereRepository.findByAlias("GEE")),
                    new Level("Génie de l'eau et de l'Environnement 3", "GEE3", filiereRepository.findByAlias("GEE")),
                    new Level("Génie Mécanique 1", "GM1", filiereRepository.findByAlias("GM")),
                    new Level("Génie Mécanique 2", "GM2", filiereRepository.findByAlias("GM")),
                    new Level("Génie Mécanique 3", "GM3", filiereRepository.findByAlias("GM")),
                    new Level("Génie Energétique et Energies Renouvelables 1", "GEER1", filiereRepository.findByAlias("GEER")),
                    new Level("Génie Energétique et Energies Renouvelables 2", "GEER2", filiereRepository.findByAlias("GEER")),
                    new Level("Génie Energétique et Energies Renouvelables 3", "GEER3", filiereRepository.findByAlias("GEER")),
                    new Level("Ingénierie des Données 1", "ID1", filiereRepository.findByAlias("ID")),
                    new Level("Ingénierie des Données 2", "ID2", filiereRepository.findByAlias("ID")),
                    new Level("Ingénierie des Données 3", "ID3", filiereRepository.findByAlias("ID")),
                    new Level("Génie Environnement 1", "GE1 (Ancienne filière)", filiereRepository.findByAlias("GE")),
                    new Level("Génie Environnement 2", "GE2 (Ancienne filière)", filiereRepository.findByAlias("GE")),
                    new Level("Génie Environnement 3", "GE3 (Ancienne filière)", filiereRepository.findByAlias("GE"))
            );

            levelRepository.saveAll(levels);
            System.out.println("Levels seeded successfully!");
        }

        // Seed LevelPaths if the LevelPath table is empty
        if(levelPathRepository.count() == 0){
            List<LevelPath> levelPaths = Arrays.asList(
                    new LevelPath(levelRepository.findByAlias("CP1"), levelRepository.findByAlias("CP2")),
                    new LevelPath(levelRepository.findByAlias("CP2"), levelRepository.findByAlias("C. Ing 1")),
                    new LevelPath(levelRepository.findByAlias("C. Ing 1"), null),
                    new LevelPath(levelRepository.findByAlias("GI1"), levelRepository.findByAlias("GI2")),
                    new LevelPath(levelRepository.findByAlias("GI2"), levelRepository.findByAlias("GI3")),
                    new LevelPath(levelRepository.findByAlias("GI3"), null),
                    new LevelPath(levelRepository.findByAlias("GI2"), levelRepository.findByAlias("GI3 BI")),
                    new LevelPath(levelRepository.findByAlias("GI3 BI"), null),
                    new LevelPath(levelRepository.findByAlias("GI2"), levelRepository.findByAlias("GI3 GL")),
                    new LevelPath(levelRepository.findByAlias("GI3 GL"), null),
                    new LevelPath(levelRepository.findByAlias("GI2"), levelRepository.findByAlias("GI3 MI")),
                    new LevelPath(levelRepository.findByAlias("GI3 MI"), null),
                    new LevelPath(levelRepository.findByAlias("GC1"), levelRepository.findByAlias("GC2")),
                    new LevelPath(levelRepository.findByAlias("GC2"), levelRepository.findByAlias("GC3")),
                    new LevelPath(levelRepository.findByAlias("GC3"), null),
                    new LevelPath(levelRepository.findByAlias("GC2"), levelRepository.findByAlias("GC3 HYD")),
                    new LevelPath(levelRepository.findByAlias("GC3 HYD"), null),
                    new LevelPath(levelRepository.findByAlias("GC2"), levelRepository.findByAlias("GC3 BPC")),
                    new LevelPath(levelRepository.findByAlias("GC3 BPC"), null),
                    new LevelPath(levelRepository.findByAlias("GEE1"), levelRepository.findByAlias("GEE2")),
                    new LevelPath(levelRepository.findByAlias("GEE2"), levelRepository.findByAlias("GEE3")),
                    new LevelPath(levelRepository.findByAlias("GEE3"), null),
                    new LevelPath(levelRepository.findByAlias("GM1"), levelRepository.findByAlias("GM2")),
                    new LevelPath(levelRepository.findByAlias("GM2"), levelRepository.findByAlias("GM3")),
                    new LevelPath(levelRepository.findByAlias("GM3"), null),
                    new LevelPath(levelRepository.findByAlias("GEER1"), levelRepository.findByAlias("GEER2")),
                    new LevelPath(levelRepository.findByAlias("GEER2"), levelRepository.findByAlias("GEER3")),
                    new LevelPath(levelRepository.findByAlias("GEER3"), null),
                    new LevelPath(levelRepository.findByAlias("ID1"), levelRepository.findByAlias("ID2")),
                    new LevelPath(levelRepository.findByAlias("ID2"), levelRepository.findByAlias("ID3")),
                    new LevelPath(levelRepository.findByAlias("ID3"), null),
                    new LevelPath(levelRepository.findByAlias("GE1 (Ancienne filière)"), levelRepository.findByAlias("GE2 (Ancienne filière)")),
                    new LevelPath(levelRepository.findByAlias("GE2 (Ancienne filière)"), levelRepository.findByAlias("GE3 (Ancienne filière)")),
                    new LevelPath(levelRepository.findByAlias("GE3 (Ancienne filière)"), null)
            );

            levelPathRepository.saveAll(levelPaths);
            System.out.println("LevelPaths seeded successfully!");
        }



    }
}