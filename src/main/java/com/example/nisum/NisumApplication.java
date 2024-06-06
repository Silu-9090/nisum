package com.example.nisum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NisumApplication {

	public static void main(String[] args) {
		SpringApplication.run(NisumApplication.class, args);
	}

}
