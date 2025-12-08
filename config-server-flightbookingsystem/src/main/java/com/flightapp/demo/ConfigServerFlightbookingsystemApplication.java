package com.flightapp.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerFlightbookingsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerFlightbookingsystemApplication.class, args);
	}

}
