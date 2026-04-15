package com.project.edugov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.project.edugov")
public class EduGovApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduGovApplication.class, args);
	}

}
