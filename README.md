# Booking Beleza — Sistema de Agendamento (Tech Challenge Fase 3)

Sistema de agendamento para estabelecimentos de beleza e bem-estar, desenvolvido em **Java 21 + Spring Boot 3**, aplicando **Clean Architecture**, **SOLID**, **TDD** e **Clean Code**, conforme especificação da Fase 3 (Substitutiva) da Pós-Tech FIAP.

## O que o sistema faz

- **Cadastro de estabelecimentos**: nome, endereço, cidade, horário de funcionamento e fotos, associados ao usuário dono.
- **Perfil de profissionais**: especialidades, tarifa base e agenda semanal recorrente (dia da semana + janela de horário), com cálculo automático de horários livres.
- **Agendamento de serviços**: cliente escolhe profissional, serviço e horário; o sistema impede double-booking (duas camadas de proteção: validação em memória e constraint única no banco) e envia confirmação por e-mail para cliente e profissional.
- **Avaliações**: cliente avalia um atendimento depois de concluído (nota de 1 a 5 + comentário); a nota média aparece na busca de estabelecimentos.
- **Busca e filtragem avançada**: por nome/cidade, serviço oferecido, faixa de preço, nota mínima e disponibilidade numa data específica.
- **Gerenciamento de agendamentos**: painel do estabelecimento para listar e atualizar status (confirmado, concluído, não compareceu); cliente pode cancelar ou reagendar.
- **Lembretes automáticos**: job agendado que roda de hora em hora e notifica cliente e profissional ~24h antes do horário marcado.
- **Integração com calendários**: feed iCalendar (`.ics`) para o cliente (autenticado) e para cada profissional (link público de assinatura), compatível com Google Calendar, Outlook e Apple Calendar.

---

## Sumário

- [O que o sistema faz](#o-que-o-sistema-faz)
- [Arquitetura](#arquitetura)
- [Ambientes de deploy](#ambientes-de-deploy)
- [Como rodar localmente](#como-rodar-localmente)
- [Como rodar com Docker](#como-rodar-com-docker)
- [Como rodar os testes](#como-rodar-os-testes)
- [Endpoints principais](#endpoints-principais)
- [Testando a API](#testando-a-api)
- [Decisões técnicas e trade-offs](#decisões-técnicas-e-trade-offs)
- [Mapeamento com os critérios de avaliação](#mapeamento-com-os-critérios-de-avaliação)

---

## Arquitetura

O projeto segue **Clean Architecture** em 3 camadas, com separação completa entre modelo de domínio e modelo de persistência (nenhuma anotação de framework dentro de `domain`):

```
src/main/java/br/com/fiap/agendamento/
├── domain/                          # Regras de negócio puras — zero dependência de framework
│   ├── model/                       # Entidades de domínio (POJOs simples: Usuario, Agendamento...)
│   ├── repository/                  # Ports de persistência (interfaces puras, sem JPA)
│   └── exception/                   # Exceções de domínio
├── application/                     # Casos de uso (orquestram o domínio)
│   ├── usecase/                     # Um caso de uso = uma responsabilidade (SRP)
│   ├── dto/                         # Request/Response da API
│   └── port/                        # Ports de saída (ex: TokenProvider, NotificacaoService)
└── infrastructure/                  # Detalhes de framework (o "mundo externo")
    ├── web/controller/              # Controllers REST
    ├── web/exception/                # Tratamento global de erros
    ├── security/                     # JWT, filtros, Spring Security
    ├── notification/                 # Envio de e-mail, scheduler de lembretes
    ├── config/                       # OpenAPI/Swagger
    └── persistence/
        ├── entity/                   # Entidades JPA (@Entity), isoladas aqui
        ├── mapper/                   # Conversão entidade de domínio ↔ entidade JPA
        ├── springdata/                # Interfaces Spring Data JPA (JpaRepository)
        └── adapter/                   # Implementação dos ports de domínio sobre Spring Data
```

**Regra de dependência:** `infrastructure` depende de `application`, que depende de `domain`. O `domain` não conhece Spring, JPA, Jackson ou HTTP — só regras de negócio e tipos da própria linguagem.

**Fluxo de uma requisição:** `Controller → UseCase → Repository (port, interface pura) → Adapter (infrastructure.persistence) → Spring Data JPA → Banco`

As classes em `domain.model` (`Usuario`, `Agendamento`, `Estabelecimento`, etc.) são POJOs com Lombok (`@Getter`/`@Setter`/`@Builder`) e nada além disso — sem `@Entity`, sem `@Column`, sem qualquer anotação do JPA. A persistência é resolvida inteiramente na borda: cada agregado tem uma entidade JPA equivalente em `infrastructure.persistence.entity` (ex.: `AgendamentoEntity`), um mapper estático que converte nos dois sentidos (`infrastructure.persistence.mapper`), e um adapter (`infrastructure.persistence.adapter`) que implementa o port de domínio (`domain.repository.AgendamentoRepository`) por cima de um repositório Spring Data JPA convencional. Os use cases dependem apenas do port — nunca sabem que Hibernate ou Postgres existem.

### Por que monolito e não microsserviços?

O enunciado da Fase 3 não exige microsserviços — pede Clean Architecture, TDD, testes de integração/carga e deploy. Um monolito modular bem separado por camadas atende 100% dos critérios com muito menos complexidade acidental (sem orquestração de múltiplos bancos, mensageria, etc.), o que deixa mais tempo pra aprofundar em testes e qualidade — que é o que pesa na nota.

---

## Ambientes de deploy

O enunciado pede deploy em pelo menos um ambiente, incluindo local e nuvem. Foram efetivamente executados dois ambientes, e documentados outros dois:

| Ambiente | Status | Detalhes |
|---|---|---|
| **Local** (Docker Compose) | ✅ Validado | `docker compose up --build` — app + Postgres + MailHog |
| **AWS ECS Fargate** (app) + **Render** (Postgres) | ✅ Validado, no ar | Ver abaixo |
| Render (aplicação completa, plano free) | ⚠️ Parcial | Banco em produção (usado pela AWS); deploy do serviço web completo não se sustentou — detalhes abaixo |
| Azure App Service | 📄 Documentado, não executado | Guia completo em `docs/deploy-azure.md` |

### Topologia em produção: AWS + Render

A aplicação roda em um cluster ECS Fargate na AWS (sem Load Balancer — a task recebe IP público direto), conectada a um banco Postgres gerenciado no Render através da internet, com conexão criptografada (`sslmode=require`). É uma arquitetura deliberadamente distribuída entre dois provedores: o Postgres do Render já estava em produção e funcionando corretamente, então em vez de duplicar o banco na AWS, a aplicação foi apontada para ele — o mesmo padrão de qualquer aplicação real que consome um banco gerenciado externo.

Passo a passo completo em `docs/deploy-aws.md`. Resumo do fluxo:

1. Build da imagem Docker e push para o ECR.
2. Task definition (`infra/aws/task-definition.json`) aponta `SPRING_DATASOURCE_URL` para o hostname externo do Postgres no Render, com `sslmode=require`.
3. Cluster + serviço Fargate, IP público liberado só na porta 8080.
4. Health check em `/actuator/health`.

### Por que o deploy completo no Render (plano free) não se sustentou

Na tentativa de subir a aplicação inteira (app + banco) só no Render, o serviço web consistentemente ultrapassava a janela de health check do Render durante o boot. O diagnóstico, em ordem:

1. **Primeira hipótese, correta parcialmente: porta errada.** A aplicação sempre subia fixa na porta 8080, mas o Render (para serviços Docker) injeta uma variável `PORT` dinâmica (nesse caso `10000`) e faz o health check nela. Corrigido lendo `server.port: ${PORT:8080}` — necessário, mas não suficiente.
2. **Segunda hipótese, descartada: falta de memória.** O padrão de logs (múltiplos `HikariPool - Shutdown initiated` em sequência) sugeria um container sendo reiniciado por OOM. Reduzir o heap da JVM (`-Xmx256m` a `-Xmx400m`, testado em ambas as direções) não teve efeito mensurável no tempo de boot — descartando memória como causa.
3. **Causa real: CPU compartilhada/limitada do plano free.** O boot local leva ~11s; no Render, entre 60 e 68 segundos — o mesmo processo, ~6x mais lento, consistente com CPU throttled. `spring.main.lazy-initialization=true` (criar beans sob demanda em vez de todos no boot) cortou parte do tempo, mas não o suficiente para ficar dentro da janela de health check do Render.

A solução prática foi separar as responsabilidades: manter o Postgres no Render (onde já funcionava bem — bancos de dados são menos sensíveis a esse tipo de throttling que uma JVM inteira subindo) e mover a aplicação para a AWS, que não tem essa limitação de CPU no Fargate. Essa investigação está detalhada, com os logs reais de cada tentativa, em `docs/deploy-free-tier.md`.

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

Requer [k6](https://k6.io/docs/get-started/installation/) instalado separadamente (não é uma dependência Maven).

```bash
# Linux/macOS (bash/zsh)
BASE_URL=http://localhost:8080 k6 run performance-tests/agendamento-load-test.js
```

```powershell
# Windows (PowerShell) — a sintaxe de variavel de ambiente inline acima nao funciona no PowerShell
$env:BASE_URL="http://localhost:8080"
k6 run performance-tests/agendamento-load-test.js
```

Se `BASE_URL` não for definida, o script usa `http://localhost:8080` como padrão. Detalhes em `performance-tests/README.md`.

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

## Testando a API

O diretório [`postman/`](postman/) contém uma coleção do Postman (`booking-beleza.postman_collection.json`) com os testes manuais dos fluxos principais do sistema, prontos para importar e executar — token e IDs (estabelecimento, profissional, serviço, agendamento) são capturados automaticamente entre requisições via scripts, sem necessidade de copiar valores manualmente.

O documento [`postman/README.md`](postman/README.md) descreve a mesma sequência de testes de duas formas equivalentes:

- **Via Postman**: importação da coleção e execução em lote (`Run collection`) ou requisição por requisição.
- **Via Swagger UI**: os mesmos passos reproduzidos manualmente em `/swagger-ui.html`, com os corpos de requisição prontos para copiar.

A sequência cobre: registro e login de cliente e estabelecimento; cadastro de estabelecimento, profissional e serviço; definição de disponibilidade; criação de agendamento (incluindo o teste da regra de double-booking, que deve retornar `409` na segunda tentativa no mesmo horário); confirmação, reagendamento e conclusão de um agendamento; avaliação; feeds de calendário do cliente e do profissional; e os filtros de busca avançada.

Fluxo rápido via `curl`, para referência:

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
| Separação entidade de domínio / entidade JPA (mapper + adapter por agregado) | `domain.model` livre de qualquer anotação de framework; a troca de ORM ou de mecanismo de persistência não toca em regra de negócio nem em use case |

## Mapeamento com os critérios de avaliação

- **Clean Architecture** → camadas `domain / application / infrastructure`, regra de dependência unidirecional, use cases com responsabilidade única. `domain.model` são POJOs sem nenhuma anotação de framework; a persistência (JPA) é resolvida inteiramente em `infrastructure.persistence` via entidade + mapper + adapter por agregado, implementando os ports definidos em `domain.repository`.
- **TDD** → use cases centrais cobertos (`CriarAgendamentoUseCaseTest`, `CancelarAgendamentoUseCaseTest`, `ReagendarAgendamentoUseCase`, `ConsultarDisponibilidadeUseCaseTest`, `RegistrarUsuarioUseCaseTest`), caminho feliz + exceções + regra de concorrência.
- **Testes integrados / análise de código** → `FluxoAgendamentoIT` (Testcontainers) + Checkstyle/SpotBugs/PMD rodando em `mvn verify`.
- **BDD** → `src/test/resources/features/agendamento.feature` (Gherkin em português) executado via Cucumber + `CucumberIT`, ponta a ponta contra a API real.
- **CI** → `.github/workflows/ci.yml` roda build, testes unitários, `mvn verify` (integração + BDD + análise estática) e valida o `docker build`.
- **Testes não funcionais** → `performance-tests/agendamento-load-test.js` (k6), até 100 VUs simultâneos, thresholds de p95 e taxa de erro.
- **Deploy** → local via `docker-compose.yml` (validado); nuvem via AWS ECS Fargate + Postgres gerenciado no Render, validado e no ar (`docs/deploy-aws.md`); Azure App Service documentado em `docs/deploy-azure.md`; investigação completa da tentativa de deploy 100% gratuito em `docs/deploy-free-tier.md`.
- **Documentação técnica** → Swagger/OpenAPI (`/swagger-ui.html`) + `docs/relatorio-tecnico.md` (tecnologias, desafios e soluções, ênfase em Clean Architecture).
