package br.com.fiap.agendamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Ponto de entrada da aplicacao Booking Beleza (Tech Challenge Fase 3). */
@SpringBootApplication
@EnableScheduling
public class TechChallengeFiapFase3Application {

    public static void main(String[] args) {
        SpringApplication.run(TechChallengeFiapFase3Application.class, args);
    }
}
