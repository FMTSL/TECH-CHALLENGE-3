# Relatório Técnico — Booking Beleza (Tech Challenge Fase 3)

## 1. Objetivo

Sistema de agendamento para estabelecimentos de beleza e bem-estar, cobrindo as 7 funcionalidades do enunciado: cadastro de estabelecimentos, perfil de profissionais com agenda, agendamento de serviços com notificações, avaliações, busca e filtragem avançada, gerenciamento de agendamentos (cancelamento/reagendamento) e integração com calendários externos.

## 2. Tecnologias e ferramentas utilizadas

| Categoria | Escolha | Motivo |
|---|---|---|
| Linguagem/Framework | Java 21 + Spring Boot 3.2 | Produtividade, ecossistema maduro, exigido pelo curso |
| Persistência | PostgreSQL 16 + Spring Data JPA | Relacional, adequado ao domínio (relacionamentos fortes entre estabelecimento/profissional/serviço/agendamento) |
| Migrations | Flyway | Versionamento de schema auditável e reproduzível entre ambientes |
| Autenticação | Spring Security + JWT (JJWT, HS256) | Stateless, simples de operar sem infraestrutura extra de chaves |
| Notificações | Spring Mail + MailHog (dev) | Confirmações/lembretes automáticos (feature 3); MailHog captura e-mails localmente sem custo |
| Documentação de API | springdoc-openapi (Swagger UI) | Gerada a partir do próprio código, sempre atualizada |
| Testes unitários | JUnit 5 + Mockito + AssertJ | Padrão de mercado para TDD em Java |
| Testes de integração | Testcontainers + MockMvc/RestAssured | Testa contra Postgres real, evitando falsos positivos de compatibilidade SQL |
| BDD | Cucumber 7 (Gherkin em português) | Aproxima especificação de negócio e testes, conforme exigido |
| Testes de carga | k6 | Script simples, versionável, roda em CI |
| Análise estática | Checkstyle + SpotBugs + PMD | Três ferramentas complementares: estilo, bugs prováveis, complexidade/design |
| Cobertura | JaCoCo | Relatório de cobertura integrado ao `mvn verify` |
| CI | GitHub Actions | Build, testes unitários, integração, análise estática e build de imagem Docker a cada push |
| Deploy | Docker + Docker Compose (local), AWS ECS Fargate e Azure App Service (nuvem) | Ver `docs/deploy-aws.md` e `docs/deploy-azure.md` |

## 3. Arquitetura: Clean Architecture aplicada

O projeto é um monolito modular organizado em três camadas com regra de dependência unidirecional (`infrastructure → application → domain`):

- **domain**: entidades e regras de negócio puras (`Agendamento.podeCancelar()`, `HorarioDisponivel.contemHorario()`), interfaces de repositório (ports) e exceções de domínio. Não conhece Spring, JPA, Jackson ou HTTP — as classes de `domain.model` são POJOs simples, sem nenhuma anotação de framework.
- **application**: um caso de uso por responsabilidade (SRP do SOLID) — `CriarAgendamentoUseCase`, `CancelarAgendamentoUseCase`, `ReagendarAgendamentoUseCase`, etc. Depende apenas de interfaces (`AgendamentoRepository`, `NotificacaoService`, `TokenProvider`), nunca de implementações concretas — aplicando o **D** do SOLID (Dependency Inversion).
- **infrastructure**: controllers REST, Spring Security/JWT, envio de e-mail, e a camada de persistência — os "adaptadores" e "frameworks externos" citados no enunciado.

### Persistência: separação completa entre domínio e JPA

A camada de persistência (`infrastructure.persistence`) segue o padrão adapter para cada agregado (`Usuario`, `Estabelecimento`, `Profissional`, `Servico`, `Agendamento`, `Avaliacao`, `HorarioDisponivel`):

- **`entity`**: a entidade JPA (`@Entity`), com todas as anotações de mapeamento (`@Column`, `@Table`, constraints) — é o equivalente de persistência do modelo de domínio, mas vive exclusivamente na infraestrutura.
- **`mapper`**: classe estática com `toDomain(...)` e `toEntity(...)`, convertendo entre o POJO de domínio e a entidade JPA nos dois sentidos.
- **`springdata`**: a interface `JpaRepository` do Spring Data, um detalhe puramente técnico.
- **`adapter`**: implementa o port definido em `domain.repository` (ex.: `AgendamentoRepository`), delegando para o repositório Spring Data e convertendo o resultado de volta para o modelo de domínio via mapper.

Os use cases dependem só do port (`domain.repository.AgendamentoRepository`), nunca da entidade JPA ou do Spring Data diretamente. Isso significa que a constraint única `(profissional_id, data_hora)` que impede double-booking — um detalhe de mapeamento de persistência — está isolada em `AgendamentoEntity`, e não no modelo de domínio `Agendamento`.

Essa separação permitiu, por exemplo, trocar a estratégia de notificação (e-mail) sem tocar em nenhuma linha dos casos de uso, e testar `CriarAgendamentoUseCase` inteiramente com mocks, sem subir Spring Context nem depender de nenhuma anotação JPA.

## 4. Desafios técnicos e soluções

### 4.1 Double-booking sob concorrência real

**Desafio:** duas requisições simultâneas podem passar pela validação "horário livre?" antes de qualquer uma delas ter persistido o agendamento.

**Solução:** duas camadas de defesa. (1) checagem otimista em memória (`existsByProfissionalIdAndDataHora`) para feedback rápido no caso comum; (2) constraint `UNIQUE(profissional_id, data_hora)` no Postgres (migration `V1`), que garante a integridade mesmo na corrida — a segunda escrita é rejeitada pelo banco e traduzida para `RegraDeNegocioException` (HTTP 409). Testado com `DataIntegrityViolationException` simulada em `CriarAgendamentoUseCaseTest`.

### 4.2 Modelagem de disponibilidade do profissional

**Desafio:** representar agenda recorrente semanal e calcular slots livres cruzando com agendamentos já feitos, sem introduzir um motor de regras complexo.

**Solução:** `HorarioDisponivel` guarda janelas recorrentes (dia da semana + hora início/fim). `ConsultarDisponibilidadeUseCase` gera slots de 30 em 30 minutos dentro dessas janelas e subtrai os horários já ocupados. Trade-off assumido: granularidade fixa de 30 min (documentado no código) — suficiente para o domínio de beleza/bem-estar, evitando um motor de agendamento genérico fora de escopo.

### 4.3 Busca e filtragem avançada sem motor de busca dedicado

**Desafio:** o enunciado pede filtro por nome, localização, serviço, avaliação e disponibilidade — um conjunto de critérios que naturalmente pede um índice de busca (o projeto de referência usado como benchmark resolve isso com Elasticsearch/CQRS).

**Solução:** para o volume de um projeto acadêmico, os filtros são aplicados em memória sobre o resultado da busca textual inicial (`BuscarEstabelecimentosUseCase`), mantendo o código simples e testável. Documentamos essa decisão como trade-off explícito — a evolução natural em produção seria um índice dedicado.

### 4.4 Notificações sem acoplar use cases a infraestrutura de e-mail

**Desafio:** enviar confirmações/lembretes sem que os casos de uso conheçam SMTP/JavaMail.

**Solução:** port `NotificacaoService` na camada `application`, implementado por `EmailNotificacaoService` em `infrastructure`. Falhas de envio são capturadas e logadas (`log.warn`), nunca derrubam o fluxo principal do agendamento — notificação é efeito colateral, não deve quebrar a transação de negócio.

### 4.5 Lembretes automáticos sem duplicidade

**Desafio:** dispersar lembretes 24h antes do horário sem reenviar a cada execução do job.

**Solução:** `LembreteAgendamentoScheduler` (`@Scheduled` a cada hora) busca agendamentos confirmados na janela de 23–25h à frente com a flag `lembreteEnviado = false`, envia e marca a flag, tornando a operação idempotente.

### 4.6 Notificação e calendário também para o profissional

**Desafio:** o enunciado pede confirmações/lembretes e sincronização de calendário "para os profissionais e clientes", mas `Profissional` não é um `Usuario` autenticável no sistema — ele não faz login.

**Solução:** em vez de criar um segundo fluxo de autenticação (fora de escopo), adicionamos um campo opcional `emailContato` ao perfil do profissional. Quando preenchido, ele passa a receber as mesmas notificações de confirmação/cancelamento do cliente, e ganha um feed iCalendar próprio (`/api/calendario/profissionais/{id}/feed.ics`), público como link de assinatura — o mesmo modelo usado por Google Calendar/Outlook para calendários externos, sem exigir login.

## 5. Qualidade de software

- **TDD**: praticamente todos os casos de uso têm testes unitários dedicados — criação/cancelamento/reagendamento de agendamento, disponibilidade, cadastro de estabelecimento/profissional/serviço, avaliação, autenticação, atualização de status e busca/filtragem — cobrindo caminho feliz, regras de negócio e casos de borda.
- **BDD**: cenários Gherkin em `src/test/resources/features/agendamento.feature`, executados via Cucumber + Testcontainers, validando o fluxo ponta a ponta (HTTP → banco real).
- **Testes de integração**: `FluxoAgendamentoIT` e o runner `CucumberIT` sobem o contexto Spring completo com Postgres real.
- **Análise estática**: Checkstyle (convenções), SpotBugs (bugs prováveis) e PMD (complexidade/design) rodam em `mvn verify`, publicando relatório em CI.
- **Testes não funcionais**: script k6 (`performance-tests/`) simula até 100 usuários simultâneos, validando p95 < 800ms e taxa de erro < 1%.

## 6. Conclusão

O sistema cobre as 7 funcionalidades do enunciado com ênfase em Clean Architecture e qualidade de software. O modelo de domínio é isolado de qualquer framework — a persistência JPA é resolvida inteiramente na infraestrutura, via entidade + mapper + adapter por agregado. As demais decisões de simplificação (filtros de busca avançada em memória, granularidade fixa de slots de disponibilidade) foram deliberadas para manter o escopo entregável dentro do prazo da Fase 3, com os próximos passos de evolução documentados junto de cada trade-off.
