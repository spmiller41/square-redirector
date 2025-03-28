package com.powersolutions.squarebridge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SquareRedirectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SquareRedirectorApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo() {
		return (args -> {
			System.out.println("We are up and running.");
		});
	}

}
