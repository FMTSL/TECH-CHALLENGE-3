CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    criado_em TIMESTAMP NOT NULL
);

CREATE TABLE estabelecimentos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(500) NOT NULL,
    cidade VARCHAR(255),
    horario_funcionamento VARCHAR(255),
    usuario_dono_id UUID NOT NULL REFERENCES usuarios(id),
    criado_em TIMESTAMP NOT NULL
);

CREATE TABLE estabelecimento_fotos (
    estabelecimento_id UUID NOT NULL REFERENCES estabelecimentos(id) ON DELETE CASCADE,
    url_foto VARCHAR(1000)
);

CREATE TABLE profissionais (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tarifa_base NUMERIC(10,2),
    estabelecimento_id UUID NOT NULL REFERENCES estabelecimentos(id)
);

CREATE TABLE profissional_especialidades (
    profissional_id UUID NOT NULL REFERENCES profissionais(id) ON DELETE CASCADE,
    especialidade VARCHAR(255)
);

CREATE TABLE servicos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(1000),
    preco NUMERIC(10,2) NOT NULL,
    duracao_minutos INTEGER NOT NULL,
    estabelecimento_id UUID NOT NULL REFERENCES estabelecimentos(id)
);

CREATE TABLE agendamentos (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL REFERENCES usuarios(id),
    profissional_id UUID NOT NULL REFERENCES profissionais(id),
    servico_id UUID NOT NULL REFERENCES servicos(id),
    estabelecimento_id UUID NOT NULL REFERENCES estabelecimentos(id),
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    CONSTRAINT uk_profissional_data_hora UNIQUE (profissional_id, data_hora)
);

CREATE TABLE avaliacoes (
    id UUID PRIMARY KEY,
    agendamento_id UUID NOT NULL UNIQUE REFERENCES agendamentos(id),
    cliente_id UUID NOT NULL REFERENCES usuarios(id),
    estabelecimento_id UUID NOT NULL REFERENCES estabelecimentos(id),
    profissional_id UUID NOT NULL REFERENCES profissionais(id),
    nota INTEGER NOT NULL CHECK (nota BETWEEN 1 AND 5),
    comentario VARCHAR(2000),
    criado_em TIMESTAMP NOT NULL
);

CREATE INDEX idx_agendamentos_cliente ON agendamentos(cliente_id);
CREATE INDEX idx_agendamentos_estabelecimento ON agendamentos(estabelecimento_id);
CREATE INDEX idx_estabelecimentos_cidade ON estabelecimentos(cidade);
