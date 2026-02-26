package com.vcontrola.vcontrola;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VcontrolaApplicationTests  extends  AbstractIntegrationTest{

	@Test
	void contextLoads() {

		System.out.println("Banco esta rodando: " + postgresContainer.isRunning());
		assertTrue(postgresContainer.isRunning());
	}
}
