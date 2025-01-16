package com.yash.Vegetabledeliveryonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class VegetabledeliveryonlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VegetabledeliveryonlineApplication.class, args);
	}

}
