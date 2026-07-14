package com.agendamento.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI sistemaAgendamentoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Agendamento API")
                        .description("API para gerenciamento de agendamentos, estabelecimentos, profissionais e clientes")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Felipe Matos Lima")
                                .email("felipe@email.com")))
                .addTagsItem(new Tag().name("Agendamentos").description("Endpoints para gerenciamento de agendamentos"))
                .addTagsItem(new Tag().name("Clientes").description("Endpoints para cadastro e consulta de clientes"))
                .addTagsItem(new Tag().name("Estabelecimentos").description("Endpoints para cadastro e consulta de estabelecimentos"))
                .addTagsItem(new Tag().name("Profissionais").description("Endpoints para cadastro e consulta de profissionais"));
    }
}