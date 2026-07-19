package br.com.fiap.agendamento.bdd;

import br.com.fiap.agendamento.TestcontainersConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** Liga o Cucumber ao contexto Spring (aplicacao completa + Postgres via Testcontainers). */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
