package com.example;

import com.example.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// remplace les 3 annotations @Configuration @EnableAutoConfiguration @ComponentScan
// met la "fabrique" en route

public class SmartphoneApplication implements CommandLineRunner {

	@Autowired
	private UtilisateurService utilisateurService;


	public static void main(String[] args) {
		SpringApplication.run(SmartphoneApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		utilisateurService.creerAdmin("Admin", "Admin", "admin@votreentreprise.com", "admin", "123 rue de l'admin");
	}
}
