package com.example.gestiondossier;

import org.springframework.boot.SpringApplication;

public class TestGestionDossierApplication {

	public static void main(String[] args) {
		SpringApplication.from(GestionDossierApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
