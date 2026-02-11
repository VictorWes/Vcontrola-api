package com.vcontrola.vcontrola;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VcontrolaApplication {

	public static void main(String[] args) {
		SpringApplication.run(VcontrolaApplication.class, args);
	}

}
