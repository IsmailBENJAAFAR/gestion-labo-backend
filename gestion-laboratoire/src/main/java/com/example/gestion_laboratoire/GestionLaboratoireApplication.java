package com.example.gestion_laboratoire;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class GestionLaboratoireApplication {
	private static Dotenv dotenv = Dotenv.load();

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(GestionLaboratoireApplication.class);

		Properties properties = new Properties();
		properties.put("spring.datasource.url", dotenv.get("DB_URL"));
		properties.put("spring.datasource.username", dotenv.get("DB_USER"));
		properties.put("spring.datasource.password", dotenv.get("DB_PWD"));

		application.setDefaultProperties(properties);
		application.run(args);
	}

}
