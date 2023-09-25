package com.example;

import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartphoneApplication implements CommandLineRunner {

	@Autowired
	private UtilisateurService utilisateurService;


	public static void main(String[] args) {
		SpringApplication.run(SmartphoneApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		utilisateurService.creerAdmin("Admin", "Admin", "admin@hotmail.com", "admin", "123 rue de l'admin","0488479342");
	}
}
