package com.example.GestionNote;

import com.example.GestionNote.model.Role;
import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.UserRepository;
import org.hibernate.type.descriptor.java.LocalDateTimeJavaType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootApplication
public class GestionNoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionNoteApplication.class, args);
	}
	@Bean
	public CommandLineRunner createDefaultAdminUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Add admin accounts if they don't exist
			if (userRepository.findByUsername("admin_user").isEmpty()) {
				User admin = new User();
				admin.setFirstName("Admin");
				admin.setLastName("Admin");
				admin.setCin("G123456");
				admin.setPhone("0612345678");
				admin.setUsername("admin_user");
				admin.setPassword(passwordEncoder.encode("admin_user"));
				admin.setEmail("admin_user@email.com");
				admin.setRole(Role.ADMIN_USER);
				admin.setEnabled(true);
				admin.setLocked(false);
				admin.setCreatedAt(LocalDateTime.now());

				userRepository.save(admin);

				System.out.println("Admin user created with username: admin_user and password: admin_user");
			}
			if (userRepository.findByUsername("admin_sp").isEmpty()) {
				// Create the first admin sp
				User admin = new User();
				admin.setFirstName("Admin");
				admin.setLastName("Admin");
				admin.setCin("G123456");
				admin.setPhone("0612345678");
				admin.setUsername("admin_sp");
				admin.setPassword(passwordEncoder.encode("admin_sp")); // Hash the password
				admin.setEmail("admin_sp@email.com");
				admin.setRole(Role.ADMIN_SP);
				admin.setEnabled(true);
				admin.setLocked(false);
				admin.setCreatedAt(LocalDateTime.now());
				userRepository.save(admin);

				System.out.println("Admin user created with username: admin_sp and password: admin_sp");
			}
			if (userRepository.findByUsername("admin_notes").isEmpty()) {
				// Create the first admin notes
				User admin = new User();
				admin.setFirstName("Admin");
				admin.setLastName("Admin");
				admin.setCin("G123456");
				admin.setPhone("0612345678");
				admin.setUsername("admin_notes");  // Corrected username
				admin.setPassword(passwordEncoder.encode("admin_notes")); // Hash the password
				admin.setEmail("admin_notes@email.com");
				admin.setRole(Role.ADMIN_NOTES);
				admin.setEnabled(true);
				admin.setLocked(false);
				admin.setCreatedAt(LocalDateTime.now());
				userRepository.save(admin);

				System.out.println("Admin user created with username: admin_notes and password: admin_notes");
			}

		};
	}
}
