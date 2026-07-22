# Testes de performance/carga

Requer [k6](https://k6.io/docs/get-started/installation/) instalado separadamente (não é uma dependência Maven).

```bash
# Linux/macOS (bash/zsh), com a aplicacao rodando localmente ou via docker compose
BASE_URL=http://localhost:8080 k6 run agendamento-load-test.js
```

```powershell
# Windows (PowerShell)
$env:BASE_URL="http://localhost:8080"
k6 run agendamento-load-test.js
```

Se `BASE_URL` não for definida, o script usa `http://localhost:8080` como padrão — em ambos os casos, `k6 run agendamento-load-test.js` sozinho já funciona se a aplicação estiver na porta padrão.

O cenario sobe gradualmente ate 100 usuarios virtuais simultaneos e valida:
- p95 do tempo de resposta abaixo de 800ms
- taxa de erros abaixo de 1%

Para simular concorrencia real de double-booking, aponte varios VUs para o
mesmo `profissionalId`/`dataHora` no endpoint `POST /api/agendamentos` e
confirme que apenas uma requisicao recebe `201` e as demais recebem `409`.
