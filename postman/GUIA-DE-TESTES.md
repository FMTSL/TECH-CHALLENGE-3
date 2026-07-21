# Guia de Testes Manuais — Booking Beleza

Este guia cobre os mesmos 26 testes de duas formas: **Swagger UI** (passo a passo manual) e **Postman** (coleção pronta pra importar). Os dois seguem exatamente a mesma ordem e os mesmos dados, então dá pra usar qualquer um dos dois — ou os dois, pra comparar.

> **Pré-requisito:** a aplicação precisa estar rodando (`docker compose up`) em `http://localhost:8080`.

---

## Parte 1 — Postman (coleção pronta)

Essa é a forma mais rápida: a coleção já vem com todas as 26 requisições, na ordem certa, e com scripts que capturam token e IDs automaticamente — você não precisa copiar e colar nada entre requisições.

### 1.1 Importar a coleção

1. Abre o Postman.
2. Clica em **Import**.
3. Arrasta o arquivo `booking-beleza.postman_collection.json` dentro da pasta "postman" pra janela que abrir, ou clica em **Upload Files** e seleciona ele.
4. Clica em **Import**. Vai aparecer uma coleção chamada **"Booking Beleza - Tech Challenge Fase 3"** na aba **Collections**, à esquerda.

### 1.2 Rodar os testes

A coleção está organizada em 7 pastas numeradas, na ordem que devem ser executadas:

1. **Autenticacao** — cria e loga o cliente e o dono do estabelecimento
2. **Cadastro** — estabelecimento, profissional, serviço
3. **Disponibilidade** — agenda do profissional
4. **Agendamento** — criar, tentar duplicar (deve falhar), confirmar, reagendar, concluir
5. **Avaliação** — avalia o atendimento concluído
6. **Calendário** — feeds `.ics` do cliente e do profissional
7. **Busca Avançada** — filtros de estabelecimento

**Dentro de cada pasta, clica em cada requisição e depois em "Send" — uma de cada vez, de cima pra baixo.** Não precisa preencher nada: os campos (token, IDs, datas) já vêm prontos usando variáveis como `{{tokenCliente}}`, `{{estabelecimentoId}}`, etc., que são preenchidas automaticamente conforme você avança.

Dica: pra rodar tudo de uma vez sem clicar requisição por requisição, clica com o botão direito na coleção → **Run collection** → **Run Booking Beleza**. O Postman executa as 26 em sequência e mostra um relatório com o que passou/falhou.

### 1.3 O que esperar em cada uma

| # | Requisição | Resultado esperado |
|---|---|---|
| 1.1–1.4 | Registro e login (cliente e dono) | `201` no registro, `200` no login com um `token` no corpo |
| 2.1–2.3 | Cadastro de estabelecimento/profissional/serviço | `201`, cada um retorna um `id` |
| 2.4–2.5 | Busca e busca por ID | `200`, lista ou objeto do estabelecimento |
| 3.1 | Definir disponibilidade | `201` |
| 3.2–3.3 | Listar janelas / slots livres | `200`, lista de horários de 30 em 30 min |
| 4.1 | Criar agendamento | `201` |
| 4.2 | Tentar o **mesmo** horário de novo | **`409`** — isso é esperado! É o teste de double-booking funcionando |
| 4.3–4.4 | Listagens | `200` |
| 4.5 | Confirmar (dono muda status) | `200` |
| 4.6 | Reagendar | `200` |
| 4.7 | Marcar concluído | `200` |
| 5.1 | Avaliar | `201` (só funciona porque o 4.7 marcou como concluído antes) |
| 6.1–6.2 | Feeds de calendário | `200`, corpo em texto começando com `BEGIN:VCALENDAR` |
| 7.1–7.4 | Filtros de busca | `200`, lista filtrada |

Se **4.2 não der 409**, ou se qualquer outro der um status diferente do esperado, essa é a hora de me colar a resposta que veio.

---

## Parte 2 — Swagger UI (passo a passo manual)

Se preferir testar direto no navegador, sem Postman, segue a mesma sequência manualmente em `http://localhost:8080/swagger-ui.html`.

### Passo 1 — Registrar e logar os dois usuários

Você precisa de **dois usuários**: um `CLIENTE` (quem agenda) e um `ESTABELECIMENTO` (quem cadastra o salão). Guarde os dois tokens separadamente (num bloco de notas, por exemplo) — vai alternar entre eles.

1. Abre **POST /api/auth/registrar**, clica **Try it out**, cola:
   ```json
   {"nome": "Cliente Teste", "email": "cliente1@teste.com", "senha": "senha123", "role": "CLIENTE"}
   ```
   **Execute**. Espera `201`.
2. Repete, mudando pra:
   ```json
   {"nome": "Dono do Salao", "email": "dono1@teste.com", "senha": "senha123", "role": "ESTABELECIMENTO"}
   ```
3. Abre **POST /api/auth/login**, loga com `cliente1@teste.com` / `senha123`. Copia o `token` da resposta — chama ele de **TOKEN_CLIENTE**.
4. Loga de novo com `dono1@teste.com` / `senha123`. Copia o token — chama ele de **TOKEN_DONO**.
5. Clica no botão **Authorize** (canto superior direito da página), cola um dos tokens no formato `Bearer SEU_TOKEN`, clica **Authorize** e **Close**. É esse token que vai valer pra todas as chamadas seguintes, até você trocar.

> Sempre que uma ação abaixo disser "(como DONO)" ou "(como CLIENTE)", clica em **Authorize** de novo e troca o token antes de executar.

### Passo 2 — Cadastro (como DONO)

1. **POST /api/estabelecimentos**:
   ```json
   {"nome": "Salao Bela", "endereco": "Rua das Flores, 123", "cidade": "Sao Paulo", "horarioFuncionamento": "09h-19h", "fotos": []}
   ```
   Guarda o `id` da resposta → **ESTABELECIMENTO_ID**.

2. **POST /api/profissionais**:
   ```json
   {"nome": "Ana Paula", "especialidades": ["Corte"], "tarifaBase": 80.00, "estabelecimentoId": "ESTABELECIMENTO_ID", "emailContato": "ana@salaobela.com"}
   ```
   Guarda o `id` → **PROFISSIONAL_ID**.

3. **POST /api/servicos**:
   ```json
   {"nome": "Corte Feminino", "descricao": "Corte + escova", "preco": 90.00, "duracaoMinutos": 60, "estabelecimentoId": "ESTABELECIMENTO_ID"}
   ```
   Guarda o `id` → **SERVICO_ID**.

### Passo 3 — Disponibilidade (como DONO)

**POST /api/profissionais/{profissionalId}/disponibilidades** — no campo `profissionalId` da URL, cola o **PROFISSIONAL_ID**. Corpo:
```json
{"diaSemana": "SEGUNDA", "horaInicio": "09:00:00", "horaFim": "18:00:00"}
```
⚠️ Repare: `horaInicio`/`horaFim` são **strings** (`"09:00:00"`), não objetos — se o Swagger preencher automaticamente como `{"hour": 9, ...}`, apaga e digita a string manualmente.

Depois testa **GET .../disponibilidades/slots-livres?data=2026-07-27** (troca a data pra uma **próxima segunda-feira** de verdade) — deve listar horários de 30 em 30 min.

### Passo 4 — Agendamento (como CLIENTE)

Troca o token pro **TOKEN_CLIENTE** no Authorize.

**POST /api/agendamentos**:
```json
{"profissionalId": "PROFISSIONAL_ID", "servicoId": "SERVICO_ID", "dataHora": "2026-07-27T09:00:00"}
```
(mesma data da segunda-feira que você usou na disponibilidade, às 09:00). Guarda o `id` → **AGENDAMENTO_ID**.

**Teste do double-booking:** manda a **mesma** requisição de novo. Dessa vez tem que dar **409 Conflict** — é o sistema bloqueando o mesmo profissional no mesmo horário duas vezes. Se der `201` de novo, é bug (mas não deveria acontecer, já testamos isso).

### Passo 5 — Gestão do agendamento

- **(como DONO)** PATCH `/api/agendamentos/{AGENDAMENTO_ID}/status?status=CONFIRMADO`
- **(como CLIENTE)** PATCH `/api/agendamentos/{AGENDAMENTO_ID}/reagendar`, corpo `{"novaDataHora": "2026-07-27T10:00:00"}`
- **(como DONO)** PATCH `/api/agendamentos/{AGENDAMENTO_ID}/status?status=CONCLUIDO` — precisa estar `CONCLUIDO` antes de avaliar

### Passo 6 — Avaliação (como CLIENTE)

**POST /api/avaliacoes**:
```json
{"agendamentoId": "AGENDAMENTO_ID", "nota": 5, "comentario": "Atendimento excelente!"}
```

### Passo 7 — Calendário

- **(como CLIENTE)** GET `/api/calendario/meu-feed.ics` — resposta em texto, começando com `BEGIN:VCALENDAR`
- Sem token: GET `/api/calendario/profissionais/{PROFISSIONAL_ID}/feed.ics` — esse é público, não precisa estar autenticado

### Passo 8 — Busca avançada (sem token necessário)

Testa `GET /api/estabelecimentos` variando os parâmetros:
- `?servico=Corte`
- `?precoMin=50&precoMax=150`
- `?notaMinima=4` (só vai aparecer o Salão Bela se a nota 5 do passo 6 já tiver sido salva)
- `?disponivelEm=2026-07-27`

---