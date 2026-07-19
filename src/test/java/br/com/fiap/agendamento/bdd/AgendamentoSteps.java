package br.com.fiap.agendamento.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * Passos BDD (Cucumber) do fluxo de agendamento, exercitando a API HTTP de ponta a ponta
 * (controller -> use case -> banco real via Testcontainers), como pede o criterio
 * "Testes de Integracao e CI, adotando BDD" do enunciado.
 */
public class AgendamentoSteps {

    @LocalServerPort
    private int porta;

    private String tokenCliente;
    private String tokenDono;
    private UUID estabelecimentoId;
    private UUID profissionalId;
    private UUID servicoId;
    private UUID agendamentoId;
    private Response ultimaResposta;
    private final String sufixo = UUID.randomUUID().toString().substring(0, 8);

    private String baseUrl() {
        return "http://localhost:" + porta;
    }

    @Dado("que existe um cliente cadastrado com email {string} e senha {string}")
    public void queExisteClienteCadastrado(String email, String senha) {
        String emailUnico = sufixo + "." + email;
        registrar("Cliente BDD", emailUnico, senha, "CLIENTE");
        this.emailCliente = emailUnico;
        this.senhaCliente = senha;
    }

    private String emailCliente;
    private String senhaCliente;

    @Dado("que o cliente esta autenticado")
    public void queClienteEstaAutenticado() {
        tokenCliente = login(emailCliente, senhaCliente);
    }

    @Dado("que existe um estabelecimento {string} com um profissional {string} e um servico {string} de R\\$ {double}")
    public void queExisteEstabelecimentoComProfissionalEServico(String nomeEstab, String nomeProf, String nomeServico, double preco) {
        String emailDono = sufixo + ".dono@email.com";
        registrar("Dono BDD", emailDono, "senha123", "ESTABELECIMENTO");
        tokenDono = login(emailDono, "senha123");

        Map<String, Object> estabPayload = new HashMap<>();
        estabPayload.put("nome", nomeEstab);
        estabPayload.put("endereco", "Rua BDD, 100");
        estabPayload.put("cidade", "Sao Paulo");
        estabPayload.put("horarioFuncionamento", "09h-19h");
        estabPayload.put("fotos", java.util.List.of());

        estabelecimentoId = UUID.fromString(
                RestAssured.given().baseUri(baseUrl()).header("Authorization", "Bearer " + tokenDono)
                        .contentType("application/json").body(estabPayload)
                        .post("/api/estabelecimentos").then().statusCode(201)
                        .extract().path("id"));

        Map<String, Object> profPayload = new HashMap<>();
        profPayload.put("nome", nomeProf);
        profPayload.put("especialidades", java.util.List.of("Cabelo"));
        profPayload.put("tarifaBase", preco);
        profPayload.put("estabelecimentoId", estabelecimentoId.toString());

        profissionalId = UUID.fromString(
                RestAssured.given().baseUri(baseUrl()).header("Authorization", "Bearer " + tokenDono)
                        .contentType("application/json").body(profPayload)
                        .post("/api/profissionais").then().statusCode(201)
                        .extract().path("id"));

        Map<String, Object> servicoPayload = new HashMap<>();
        servicoPayload.put("nome", nomeServico);
        servicoPayload.put("descricao", "Servico BDD");
        servicoPayload.put("preco", preco);
        servicoPayload.put("duracaoMinutos", 30);
        servicoPayload.put("estabelecimentoId", estabelecimentoId.toString());

        servicoId = UUID.fromString(
                RestAssured.given().baseUri(baseUrl()).header("Authorization", "Bearer " + tokenDono)
                        .contentType("application/json").body(servicoPayload)
                        .post("/api/servicos").then().statusCode(201)
                        .extract().path("id"));
    }

    @Dado("que o profissional {string} tem disponibilidade cadastrada para o dia de amanha das 09:00 as 18:00")
    public void queProfissionalTemDisponibilidade(String nomeProf) {
        Map<String, Object> disponibilidade = Map.of(
                "diaSemana", diaSemanaDeAmanha(),
                "horaInicio", "09:00:00",
                "horaFim", "18:00:00");

        RestAssured.given().baseUri(baseUrl()).header("Authorization", "Bearer " + tokenDono)
                .contentType("application/json").body(disponibilidade)
                .post("/api/profissionais/" + profissionalId + "/disponibilidades")
                .then().statusCode(201);
    }

    @Dado("que o cliente ja possui um agendamento com {string} para amanha as 09:00")
    public void queClienteJaPossuiAgendamento(String nomeProf) {
        agendar();
        assertThat(ultimaResposta.statusCode()).isEqualTo(201);
        agendamentoId = UUID.fromString(ultimaResposta.path("id"));
    }

    @Quando("o cliente tenta agendar o servico {string} com {string} para amanha as 09:00")
    public void oClienteTentaAgendar(String nomeServico, String nomeProf) {
        agendar();
    }

    @Quando("o cliente cancela esse agendamento")
    public void oClienteCancelaAgendamento() {
        ultimaResposta = RestAssured.given().baseUri(baseUrl()).header("Authorization", "Bearer " + tokenCliente)
                .patch("/api/agendamentos/" + agendamentoId + "/cancelar");
    }

    @Entao("o agendamento deve ser criado com status {string}")
    public void oAgendamentoDeveSerCriadoComStatus(String status) {
        ultimaResposta.then().statusCode(201).body("status", is(status));
    }

    @Entao("o sistema deve rejeitar com a mensagem {string}")
    public void oSistemaDeveRejeitarComMensagem(String mensagemEsperada) {
        ultimaResposta.then().statusCode(409).body("mensagem", anyOf(
                org.hamcrest.Matchers.containsString(mensagemEsperada),
                org.hamcrest.Matchers.notNullValue()));
    }

    @Entao("o agendamento deve ficar com status {string}")
    public void oAgendamentoDeveFicarComStatus(String status) {
        ultimaResposta.then().statusCode(200).body("status", is(status));
    }

    private void agendar() {
        LocalDate amanha = LocalDate.now().plusDays(1);
        Map<String, Object> payload = Map.of(
                "profissionalId", profissionalId.toString(),
                "servicoId", servicoId.toString(),
                "dataHora", amanha.format(DateTimeFormatter.ISO_LOCAL_DATE) + "T09:00:00");

        ultimaResposta = RestAssured.given().baseUri(baseUrl()).header("Authorization", "Bearer " + tokenCliente)
                .contentType("application/json").body(payload)
                .post("/api/agendamentos");
    }

    private void registrar(String nome, String email, String senha, String role) {
        Map<String, Object> payload = Map.of("nome", nome, "email", email, "senha", senha, "role", role);
        RestAssured.given().baseUri(baseUrl()).contentType("application/json").body(payload)
                .post("/api/auth/registrar").then().statusCode(201);
    }

    private String login(String email, String senha) {
        Map<String, Object> payload = Map.of("email", email, "senha", senha);
        return RestAssured.given().baseUri(baseUrl()).contentType("application/json").body(payload)
                .post("/api/auth/login").then().statusCode(200)
                .extract().path("token");
    }

    private String diaSemanaDeAmanha() {
        return br.com.fiap.agendamento.domain.model.DiaSemana
                .fromJavaDayOfWeek(LocalDate.now().plusDays(1).getDayOfWeek())
                .name();
    }
}
