# Deploy gratuito (Render.com)

O `render.yaml` na raiz do projeto já descreve a infraestrutura (web service + Postgres gerenciado, ambos no plano free).

## Passos

1. Suba o projeto para o GitHub (já é pré-requisito do entregável).
2. Acesse [render.com](https://render.com), crie uma conta e clique em **New > Blueprint**.
3. Selecione o repositório — o Render lê o `render.yaml` automaticamente e propõe criar o web service (`booking-beleza`) e o banco (`booking-beleza-db`).
4. Confirme; o Render builda a imagem a partir do `Dockerfile`, cria o Postgres gerenciado e injeta a connection string automaticamente via `fromDatabase`.
5. Após o deploy, a aplicação fica em `https://booking-beleza.onrender.com` — Swagger em `/swagger-ui.html`.

> Limitações do plano free: o serviço "dorme" após 15 min de inatividade (primeira requisição depois disso demora alguns segundos) e o banco free expira em 90 dias — suficiente para a avaliação da Fase 3.
