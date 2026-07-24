# Deploy gratuito (Render.com) — banco em produção, aplicação web parcial

O `render.yaml` na raiz do projeto descreve a infraestrutura completa (web service + Postgres gerenciado, ambos no plano free) e pode ser usado como Blueprint. Na prática, o **banco de dados está em produção no Render** e é consumido pela aplicação hospedada na AWS (ver `docs/deploy-aws.md`); o deploy da **aplicação web completa** no Render free tier foi tentado e documentado abaixo, incluindo por que não se sustentou dentro da janela de health check do Render.

## Banco de dados (funcionando)

1. No painel do Render: **New > PostgreSQL**, plano free.
2. Anota o **hostname externo** (algo como `dpg-xxxxxxxxxxxxx-a.oregon-postgres.render.com`), usuário, senha e nome do banco.
3. Qualquer aplicação externa (incluindo a hospedada na AWS) se conecta usando `sslmode=require` na URL JDBC — o Render exige TLS para conexões externas.

## Aplicação web completa — tentativa e diagnóstico

Subir a aplicação inteira (app Spring Boot + Postgres) só no Render, via Blueprint (`render.yaml`), foi tentado várias vezes. Em todas as tentativas, o deploy falhava com `Timed Out after waiting for internal health check`. O diagnóstico, na ordem em que cada hipótese foi testada:

### 1. Porta errada (causa real, parcial)

A aplicação sempre subia fixa na porta `8080` (padrão do Spring Boot). O Render, para serviços do tipo Docker, injeta uma variável de ambiente `PORT` dinâmica (nesse caso, `10000`) e direciona o health check para ela — não para a porta declarada no `EXPOSE` do Dockerfile. Como a aplicação nunca lia essa variável, o health check sempre batia numa porta onde ninguém escutava.

**Correção:** `server.port: ${PORT:8080}` no `application.yml` (mantém `8080` como padrão para uso local, onde a variável `PORT` não existe).

Essa correção era necessária, mas sozinha não resolveu — os deploys continuaram dando timeout.

### 2. Falta de memória (hipótese descartada)

O padrão nos logs — vários `HikariPool-1 - Shutdown initiated/completed` em sequência rápida, pouco antes do timeout — parecia indicar um container sendo derrubado por falta de memória (OOM) e reiniciado em loop.

**Tentativa:** reduzir o heap da JVM via `JAVA_OPTS` (`-Xmx320m`, depois `-Xmx256m`, depois um meio-termo `-Xms128m -Xmx400m`), trocar o coletor de lixo padrão (G1) pelo `SerialGC`, mais leve para heaps pequenos.

**Resultado:** nenhuma mudança mensurável no tempo de boot (67,6s antes do ajuste, 67,7s depois de reduzir o heap, 60,5s no ajuste seguinte — dentro da margem de variação natural entre execuções). Isso descartou memória como o gargalo real; o padrão de shutdowns em sequência era, na verdade, o próprio Render encerrando o container após decidir que o deploy tinha falhado — consequência do timeout, não causa dele.

### 3. CPU compartilhada/limitada do plano free (causa real)

Comparando o tempo de boot local (~11 segundos, medido via Docker Compose) com o tempo no Render (60 a 68 segundos, medido pelos logs — do `Starting TechChallengeFiapFase3Application` até `Started ... in X seconds`), a diferença é de aproximadamente 6x para o mesmo processo (Spring context, JPA/Hibernate, Flyway, Spring Security, geração do schema OpenAPI). Isso é consistente com CPU compartilhada e limitada — não memória.

**Tentativa:** `spring.main.lazy-initialization: true`, que faz o Spring criar cada bean sob demanda (na primeira vez que é usado) em vez de todos no boot. Reduziu o tempo de ~68s para ~60s (~10%) — uma melhora real, mas insuficiente para ficar dentro da janela de health check do Render, que se mostrou mais apertada que 60 segundos.

> **Atenção ao usar `lazy-initialization` global:** um bean que só tem métodos `@Scheduled` e não é injetado em nenhum outro lugar (como o job de lembretes automáticos deste projeto, `LembreteAgendamentoScheduler`) nunca chega a ser instanciado sob lazy-init global — e, portanto, o `@Scheduled` nunca dispara, silenciosamente, sem nenhum erro no log. A correção foi marcar essa classe especificamente com `@Lazy(false)`, opondo-se à configuração global só para ela.

### Decisão final

Como reduzir ainda mais o tempo de boot exigiria técnicas mais invasivas (Class Data Sharing da JVM, reestruturação do processo de build) sem garantia de sucesso e sem ambiente de teste real disponível para validar antes de gastar mais ciclos de deploy, a decisão foi separar as responsabilidades: manter o banco no Render (onde funciona bem — bancos de dados geridos não sofrem o mesmo throttling de CPU que uma JVM inteira subindo) e mover a aplicação web para a AWS (ECS Fargate), que não impõe esse limite de CPU no boot. Essa é a topologia em produção hoje, documentada em `docs/deploy-aws.md`.

## Limitações conhecidas do plano free do Render

- O banco Postgres free expira em 90 dias.
- Qualquer serviço web no plano free "dorme" após 15 minutos de inatividade; a primeira requisição depois disso demora até 50 segundos para acordar. Isso não afeta o banco (que não "dorme" da mesma forma), mas seria relevante se a aplicação web também estivesse hospedada aqui.
