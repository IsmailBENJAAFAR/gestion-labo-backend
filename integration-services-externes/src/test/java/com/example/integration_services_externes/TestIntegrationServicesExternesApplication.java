package com.example.integration_services_externes;

import org.springframework.boot.SpringApplication;

public class TestIntegrationServicesExternesApplication {

	public static void main(String[] args) {
		SpringApplication.from(IntegrationServicesExternesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
