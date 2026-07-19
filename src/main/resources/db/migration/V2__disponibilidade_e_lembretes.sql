CREATE TABLE horarios_disponiveis (
    id UUID PRIMARY KEY,
    profissional_id UUID NOT NULL REFERENCES profissionais(id) ON DELETE CASCADE,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL
);

CREATE INDEX idx_horarios_disponiveis_profissional_dia ON horarios_disponiveis(profissional_id, dia_semana);

ALTER TABLE agendamentos ADD COLUMN lembrete_enviado BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_agendamentos_lembrete ON agendamentos(status, lembrete_enviado, data_hora);
