# language: pt
Funcionalidade: Agendamento de servicos
  Como cliente do Booking Beleza
  Eu quero agendar um servico com um profissional
  Para poder ser atendido em um estabelecimento de beleza e bem-estar

  Contexto:
    Dado que existe um cliente cadastrado com email "cliente.bdd@email.com" e senha "senha123"
    E que o cliente esta autenticado
    E que existe um estabelecimento "Salao Bela" com um profissional "Ana" e um servico "Corte" de R$ 50.00
    E que o profissional "Ana" tem disponibilidade cadastrada para o dia de amanha das 09:00 as 18:00

  Cenario: Cliente agenda um horario disponivel com sucesso
    Quando o cliente tenta agendar o servico "Corte" com "Ana" para amanha as 09:00
    Entao o agendamento deve ser criado com status "PENDENTE"

  Cenario: Cliente nao consegue agendar o mesmo horario duas vezes
    Dado que o cliente ja possui um agendamento com "Ana" para amanha as 09:00
    Quando o cliente tenta agendar o servico "Corte" com "Ana" para amanha as 09:00
    Entao o sistema deve rejeitar com a mensagem "ja possui um agendamento"

  Cenario: Cliente cancela um agendamento pendente
    Dado que o cliente ja possui um agendamento com "Ana" para amanha as 09:00
    Quando o cliente cancela esse agendamento
    Entao o agendamento deve ficar com status "CANCELADO"
