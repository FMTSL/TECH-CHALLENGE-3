# Booking Beleza — Sistema de Agendamento (Tech Challenge Fase 3)

Sistema de agendamento para estabelecimentos de beleza e bem-estar, desenvolvido em **Java 21 + Spring Boot 3**, aplicando **Clean Architecture**, **SOLID**, **TDD** e **Clean Code**, conforme especificação da Fase 3 (Substitutiva) da Pós-Tech FIAP.

---

## Sumário

- [Arquitetura](#arquitetura)
- [Como rodar localmente](#como-rodar-localmente)
- [Como rodar com Docker](#como-rodar-com-docker)
- [Como rodar os testes](#como-rodar-os-testes)
- [Endpoints principais](#endpoints-principais)
- [Fluxo de teste manual (happy path)](#fluxo-de-teste-manual-happy-path)
- [Decisões técnicas e trade-offs](#decisões-técnicas-e-trade-offs)
- [Mapeamento com os critérios de avaliação](#mapeamento-com-os-critérios-de-avaliação)

---

## Arquitetura

O projeto segue **Clean Architecture** em 3 camadas, isolando regras de negócio de detalhes de framework:

```
src/main/java/br/com/fiap/agendamento/
├── domain/                    # Regras de negócio puras
│   ├── model/                 # Entidades (Usuario, Estabelecimento, Agendamento...)
│   ├── repository/            # Ports de persistência (interfaces)
│   └── exception/             # Exceções de domínio
├── application/                # Casos de uso (orquestram o domínio)
│   ├── usecase/                # Um caso de uso = uma responsabilidade (SRP)
│   ├── dto/                    # Request/Response da API
│   └── port/                   # Ports de saída (ex: TokenProvider)
└── infrastructure/             # Detalhes de framework (o "mundo externo")
    ├── web/controller/         # Controllers REST
    ├── web/exception/          # Tratamento global de erros
    ├── security/               # JWT, filtros, Spring Security
    └── config/                 # OpenAPI/Swagger
```

**Regra de dependência:** `infrastructure` depende de `application`, que depende de `domain`. O `domain` não conhece Spring, JPA ou HTTP — apenas regras de negócio.

**Fluxo de uma requisição:** `Controller → UseCase → Repository (port) → Banco`

---

## Como rodar localmente

Pré-requisitos: Java 21, Maven, Docker (só para o banco).

Não é preciso criar banco/usuário manualmente via `psql` — o container do Postgres já nasce configurado com o database `booking_beleza` e o usuário `booking`, a partir das variáveis `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` definidas no `docker-compose.yml`.

```bash
# 1. Suba apenas o banco (a app roda local, fora do container, para facilitar debug)
docker compose up -d db

# 2. Rode a aplicação (profile local por padrão, aponta para localhost:5432)
./mvnw spring-boot:run

# App sobe em http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

Para derrubar o banco depois: `docker compose down` (ou `docker compose down -v` para apagar também os dados).

## Como rodar com Docker

```bash
docker compose up --build
```

Isso sobe o Postgres e a aplicação. As migrations do Flyway rodam automaticamente no start. A API fica disponível em `http://localhost:8080`.

## Como rodar os testes

```bash
# Testes unitários (TDD dos use cases, com Mockito) — não sobem Postgres
./mvnw test

# Testes de integração + BDD (Testcontainers sobe um Postgres real em container)
# Inclui FluxoAgendamentoIT (MockMvc) e CucumberIT (cenarios Gherkin em
# src/test/resources/features/agendamento.feature)
./mvnw verify

# Relatório de cobertura JaCoCo
./mvnw verify
# abrir target/site/jacoco/index.html
```

> Os testes de integração (`*IT.java`) exigem Docker rodando localmente (Testcontainers).

### Análise estática de código

```bash
./mvnw checkstyle:check   # convenções de estilo
./mvnw spotbugs:check     # bugs prováveis
./mvnw pmd:check          # complexidade e design
```

Todas rodam automaticamente na fase `verify` (e portanto no `./mvnw verify` e no CI), com `failOnViolation=false` por padrão — os relatórios aparecem no console/target sem quebrar o build. Para torná-las bloqueantes (recomendado antes da entrega final), mude `failOnViolation` para `true` em cada plugin no `pom.xml`.

### Testes de performance/carga

```bash
# com a aplicacao rodando (local ou docker compose)
BASE_URL=http://localhost:8080 k6 run performance-tests/agendamento-load-test.js
```

Requer [k6](https://k6.io/docs/get-started/installation/) instalado. Detalhes em `performance-tests/README.md`.

---

## Endpoints principais

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/api/auth/registrar` | não | Cria usuário (CLIENTE ou ESTABELECIMENTO) |
| POST | `/api/auth/login` | não | Retorna JWT |
| POST | `/api/estabelecimentos` | sim | Cadastra estabelecimento |
| GET | `/api/estabelecimentos?q=&servico=&precoMin=&precoMax=&notaMinima=&disponivelEm=` | não | Busca e filtragem avançada (nome/cidade, serviço, faixa de preço, avaliação mínima, disponibilidade em uma data) |
| POST | `/api/profissionais` | sim | Cadastra profissional |
| GET | `/api/profissionais?estabelecimentoId=` | não | Lista profissionais |
| POST | `/api/profissionais/{id}/disponibilidades` | sim | Cadastra janela recorrente de agenda (dia da semana + hora início/fim) |
| GET | `/api/profissionais/{id}/disponibilidades` | não | Lista as janelas de agenda cadastradas |
| GET | `/api/profissionais/{id}/disponibilidades/slots-livres?data=` | não | Lista horários livres do profissional em uma data |
| POST | `/api/servicos` | sim | Cadastra serviço |
| GET | `/api/servicos?estabelecimentoId=` | não | Lista serviços |
| POST | `/api/agendamentos` | sim | Cria agendamento (valida disponibilidade + double-booking, envia confirmação por e-mail) |
| GET | `/api/agendamentos/meus` | sim | Agendamentos do cliente logado |
| GET | `/api/agendamentos/estabelecimento/{id}` | sim | Painel do estabelecimento |
| PATCH | `/api/agendamentos/{id}/cancelar` | sim | Cliente cancela (envia e-mail de cancelamento) |
| PATCH | `/api/agendamentos/{id}/reagendar` | sim | Cliente reagenda para novo horário (revalida disponibilidade/double-booking) |
| PATCH | `/api/agendamentos/{id}/status?status=` | sim | Estabelecimento atualiza status (confirmar, concluir, no-show) |
| POST | `/api/avaliacoes` | sim | Avalia atendimento concluído |
| GET | `/api/calendario/meu-feed.ics` | sim | Feed iCalendar do cliente (Google/Outlook/Apple) |
| GET | `/api/calendario/profissionais/{id}/feed.ics` | não | Feed iCalendar do profissional (link de assinatura, sem login próprio) |

Documentação interativa completa: `/swagger-ui.html`. E-mails de confirmação/cancelamento/lembrete (dev) ficam visíveis na UI do MailHog em `http://localhost:8025` quando rodando via Docker Compose.

### Notificações e calendário também para o profissional

O enunciado pede confirmação/lembrete e sincronização de calendário "para os profissionais **e** clientes". Como o profissional não tem login no sistema (quem se autentica é o dono do estabelecimento), ele recebe um `emailContato` opcional no cadastro (`POST /api/profissionais`) — se preenchido, passa a receber as mesmas notificações de confirmação/cancelamento que o cliente, e tem seu próprio feed `.ics` público em `/api/calendario/profissionais/{id}/feed.ics` (funciona como link de assinatura, no mesmo modelo que Google Calendar/Outlook usam para calendários externos).

> **Nota de privacidade:** o `emailContato` do profissional nunca é retornado pelos endpoints públicos (`GET /api/profissionais`) — existe um `ProfissionalResponse` dedicado que omite esse campo, já que é usado apenas internamente para notificação.

A busca de estabelecimentos (`GET /api/estabelecimentos`) também retorna `notaMedia` calculada a partir das avaliações, para que o critério "avaliação" da feature 5 seja visível, não só filtrável.

### Gerando a documentação Javadoc

```bash
./mvnw javadoc:javadoc
# abrir target/site/apidocs/index.html
```

Todas as classes de domínio, casos de uso, controllers e ports têm Javadoc de classe explicando responsabilidade e, quando relevante, o trade-off de design por trás da implementação. O Swagger (`/swagger-ui.html`) continua sendo a documentação primária da API — o enunciado aceita "Javadoc **ou** Swagger", este projeto entrega os dois.

## Fluxo de teste manual (happy path)

```bash
# 1) Registrar dono de estabelecimento
curl -X POST localhost:8080/api/auth/registrar -H "Content-Type: application/json" \
  -d '{"nome":"Salao Bela","email":"dono@salao.com","senha":"senha123","role":"ESTABELECIMENTO"}'

# 2) Login
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"dono@salao.com","senha":"senha123"}' | jq -r .token)

# 3) Cadastrar estabelecimento
ESTAB_ID=$(curl -s -X POST localhost:8080/api/estabelecimentos -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Salao Bela","endereco":"Rua A, 123","cidade":"Sao Paulo","horarioFuncionamento":"09h-19h"}' | jq -r .id)

# 4) Cadastrar profissional e servico (use $ESTAB_ID), depois criar agendamento como CLIENTE...
```

---

## Decisões técnicas e trade-offs

| Decisão | Motivo |
|---|---|
| Monolito modular (não microsserviços) | Enunciado não exige; reduz complexidade acidental sem perder pontos de arquitetura |
| JWT stateless (HS256, via JJWT) | Simples de rodar localmente sem infraestrutura extra de chaves assimétricas |
| Constraint única `(profissional_id, data_hora)` no Postgres | Garante ausência de double-booking mesmo sob concorrência real, além da checagem em memória no use case |
| Flyway para migrations | Versionamento de schema auditável, como no projeto de referência |
| `ddl-auto: validate` | Nunca deixa o Hibernate alterar o schema em produção — só o Flyway pode |
| Testcontainers nos testes de integração | Testa contra um Postgres real, não H2, evitando falsos positivos de compatibilidade SQL |
| DTOs (`record`) na borda da API | Desacopla o contrato HTTP do modelo de domínio |

## Mapeamento com os critérios de avaliação

- **Clean Architecture** → camadas `domain / application / infrastructure`, regra de dependência unidirecional, use cases com responsabilidade única.
- **TDD** → use cases centrais cobertos (`CriarAgendamentoUseCaseTest`, `CancelarAgendamentoUseCaseTest`, `ReagendarAgendamentoUseCase`, `ConsultarDisponibilidadeUseCaseTest`, `RegistrarUsuarioUseCaseTest`), caminho feliz + exceções + regra de concorrência.
- **Testes integrados / análise de código** → `FluxoAgendamentoIT` (Testcontainers) + Checkstyle/SpotBugs/PMD rodando em `mvn verify`.
- **BDD** → `src/test/resources/features/agendamento.feature` (Gherkin em português) executado via Cucumber + `CucumberIT`, ponta a ponta contra a API real.
- **CI** → `.github/workflows/ci.yml` roda build, testes unitários, `mvn verify` (integração + BDD + análise estática) e valida o `docker build`.
- **Testes não funcionais** → `performance-tests/agendamento-load-test.js` (k6), até 100 VUs simultâneos, thresholds de p95 e taxa de erro.
- **Deploy** → local via `docker-compose.yml`; nuvem via `docs/deploy-aws.md` (ECS Fargate), `docs/deploy-azure.md` (App Service) e `docs/deploy-free-tier.md` (Render, gratuito, `render.yaml` na raiz).
- **Documentação técnica** → Swagger/OpenAPI (`/swagger-ui.html`) + `docs/relatorio-tecnico.md` (tecnologias, desafios e soluções, ênfase em Clean Architecture).
