# Testes manuais da API

Este diretório contém uma coleção do Postman (`booking-beleza.postman_collection.json`) com os testes manuais da API, cobrindo os fluxos principais do sistema. A mesma sequência pode ser reproduzida no Swagger UI, descrita na segunda parte deste documento.

Pré-requisito: aplicação em execução (`docker compose up`) em `http://localhost:8080`, ou a variável `baseUrl` da coleção apontando para o ambiente de produção na AWS (ver `docs/deploy-aws.md`).

## Estrutura da coleção

A coleção está organizada em 7 pastas, numeradas na ordem de execução:

| Pasta | Conteúdo |
|---|---|
| 1. Autenticação | Registro e login de um usuário CLIENTE e de um usuário ESTABELECIMENTO |
| 2. Cadastro | Estabelecimento, profissional e serviço |
| 3. Disponibilidade | Agenda semanal do profissional |
| 4. Agendamento | Criação, tentativa de double-booking, confirmação, reagendamento, conclusão |
| 5. Avaliação | Avaliação de um atendimento concluído |
| 6. Calendário | Feeds iCalendar do cliente e do profissional |
| 7. Busca avançada | Filtros de estabelecimento (serviço, preço, nota, disponibilidade) |

Token e IDs (estabelecimento, profissional, serviço, agendamento) são capturados automaticamente de uma requisição para a próxima via scripts de teste do Postman (`pm.collectionVariables.set`), sem necessidade de copiar valores manualmente.

## Importando e executando

1. No Postman: **Import** → seleciona `booking-beleza.postman_collection.json`.
2. Executa as requisições em ordem, pasta por pasta, de cima para baixo.
3. Alternativa: botão direito na coleção → **Run collection**, para executar todas em sequência com relatório de resultado.

## Resultado esperado por requisição

| # | Requisição | Resultado esperado |
|---|---|---|
| 1.1–1.4 | Registro e login (cliente e dono) | `201` no registro; `200` no login, com `token` no corpo |
| 2.1–2.3 | Cadastro de estabelecimento/profissional/serviço | `201`, cada um retornando um `id` |
| 2.4–2.5 | Busca e busca por ID | `200` |
| 3.1 | Definir disponibilidade | `201` |
| 3.2–3.3 | Listar janelas / slots livres | `200`, lista de horários de 30 em 30 minutos |
| 4.1 | Criar agendamento | `201` |
| 4.2 | Repetir o mesmo agendamento | `409` (bloqueio de double-booking; resultado esperado) |
| 4.3–4.4 | Listagens | `200` |
| 4.5 | Confirmar agendamento | `200` |
| 4.6 | Reagendar | `200` |
| 4.7 | Marcar como concluído | `200` |
| 5.1 | Avaliar | `201` (depende do agendamento estar concluído) |
| 6.1–6.2 | Feeds de calendário | `200`, corpo iniciando em `BEGIN:VCALENDAR` |
| 7.1–7.4 | Filtros de busca | `200`, lista filtrada |

A requisição 4.2 é a única que espera um código de erro (`409`) como resultado correto — valida a regra de negócio de double-booking.

## Equivalente no Swagger UI

Para reproduzir a mesma sequência manualmente em `/swagger-ui.html`, sem o Postman:

### 1. Autenticação

Criar dois usuários (registro em `POST /api/auth/registrar`): um com `role: "CLIENTE"` e outro com `role: "ESTABELECIMENTO"`. Logar os dois em `POST /api/auth/login` e guardar os dois tokens retornados. Usar o botão **Authorize** do Swagger para alternar entre eles conforme indicado em cada etapa abaixo.

### 2. Cadastro (autenticado como ESTABELECIMENTO)

```
POST /api/estabelecimentos
{"nome": "Salao Bela", "endereco": "Rua das Flores, 123", "cidade": "Sao Paulo", "horarioFuncionamento": "09h-19h", "fotos": []}
```
Guardar o `id` retornado (estabelecimentoId).

```
POST /api/profissionais
{"nome": "Ana Paula", "especialidades": ["Corte"], "tarifaBase": 80.00, "estabelecimentoId": "<estabelecimentoId>", "emailContato": "ana@salaobela.com"}
```
Guardar o `id` retornado (profissionalId).

```
POST /api/servicos
{"nome": "Corte Feminino", "descricao": "Corte + escova", "preco": 90.00, "duracaoMinutos": 60, "estabelecimentoId": "<estabelecimentoId>"}
```
Guardar o `id` retornado (servicoId).

### 3. Disponibilidade (autenticado como ESTABELECIMENTO)

```
POST /api/profissionais/{profissionalId}/disponibilidades
{"diaSemana": "SEGUNDA", "horaInicio": "09:00:00", "horaFim": "18:00:00"}
```

Nota: `horaInicio`/`horaFim` são strings no formato `HH:mm:ss`. Se o Swagger preencher o exemplo como objeto (`{"hour": 9, ...}`), o valor deve ser substituído manualmente pela string.

```
GET /api/profissionais/{profissionalId}/disponibilidades/slots-livres?data=<uma-proxima-segunda-feira>
```

### 4. Agendamento (autenticado como CLIENTE)

```
POST /api/agendamentos
{"profissionalId": "<profissionalId>", "servicoId": "<servicoId>", "dataHora": "<mesma-data>T09:00:00"}
```
Guardar o `id` retornado (agendamentoId).

Repetir a mesma requisição: o resultado esperado é `409 Conflict` (double-booking bloqueado).

### 5. Gestão do agendamento

```
PATCH /api/agendamentos/{agendamentoId}/status?status=CONFIRMADO   (autenticado como ESTABELECIMENTO)
PATCH /api/agendamentos/{agendamentoId}/reagendar                  (autenticado como CLIENTE)
{"novaDataHora": "<data>T10:00:00"}
PATCH /api/agendamentos/{agendamentoId}/status?status=CONCLUIDO    (autenticado como ESTABELECIMENTO)
```

### 6. Avaliação (autenticado como CLIENTE)

```
POST /api/avaliacoes
{"agendamentoId": "<agendamentoId>", "nota": 5, "comentario": "Atendimento excelente"}
```

### 7. Calendário

```
GET /api/calendario/meu-feed.ics                                   (autenticado como CLIENTE)
GET /api/calendario/profissionais/{profissionalId}/feed.ics        (sem autenticação)
```

### 8. Busca avançada (sem autenticação)

```
GET /api/estabelecimentos?servico=Corte
GET /api/estabelecimentos?precoMin=50&precoMax=150
GET /api/estabelecimentos?notaMinima=4
GET /api/estabelecimentos?disponivelEm=<data>
```
