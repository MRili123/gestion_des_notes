package com.example.GestionNote;

import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@SpringBootApplication
public class GestionNoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionNoteApplication.class, args);
	}

	@Bean
	public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Check if an admin already exists
			if (userRepository.findByUsername("admin").isEmpty()) {
				// Create the first admin user
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin")); // Hash the password
				admin.setEmail("admin@example.com");
				admin.setFullname("Admin User");
				admin.setRole("ROLE_ADMIN");
				userRepository.save(admin);

				System.out.println("Admin user created with username: admin and password: admin");
			}
		};
	}
}
