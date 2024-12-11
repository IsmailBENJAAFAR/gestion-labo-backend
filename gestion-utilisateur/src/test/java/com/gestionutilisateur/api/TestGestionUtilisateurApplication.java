package com.gestionutilisateur.api;

import org.springframework.boot.SpringApplication;

public class TestGestionUtilisateurApplication {

	public static void main(String[] args) {
		SpringApplication.from(GestionUtilisateurApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
