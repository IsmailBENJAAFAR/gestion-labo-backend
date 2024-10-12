package com.example.gestioncontact;

import org.springframework.boot.SpringApplication;

public class TestGestionContactApplication {

    public static void main(String[] args) {
        SpringApplication.from(GestionContactApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
