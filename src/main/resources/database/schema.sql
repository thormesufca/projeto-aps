PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS escritorio_advogados (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    advogado_id          INTEGER NOT NULL UNIQUE REFERENCES advogados(id) ON DELETE RESTRICT,
    tipo_vinculo         TEXT    NOT NULL CHECK(tipo_vinculo IN ('SOCIO','EMPREGADO')),
    percentual_sociedade REAL,
    salario_mensal       REAL,
    data_ingresso        TEXT,
    data_desligamento    TEXT
);


CREATE TABLE IF NOT EXISTS clientes (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nome          TEXT    NOT NULL,
    identificador TEXT    NOT NULL UNIQUE,
    telefone      TEXT,
    tipo_pessoa   TEXT    NOT NULL CHECK(tipo_pessoa IN ('FISICA','JURIDICA')),
    email         TEXT,
    endereco      TEXT
);


CREATE TABLE IF NOT EXISTS advogados (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nome          TEXT    NOT NULL,
    identificador TEXT    NOT NULL UNIQUE,
    telefone      TEXT,
    tipo_pessoa   TEXT    NOT NULL DEFAULT 'FISICA',
    especialidade TEXT,
    email         TEXT
);


CREATE TABLE IF NOT EXISTS advogado_oabs (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    advogado_id INTEGER NOT NULL REFERENCES advogados(id) ON DELETE CASCADE,
    estado      TEXT    NOT NULL CHECK(length(estado) = 2),
    numero      TEXT    NOT NULL CHECK(length(numero) BETWEEN 4 AND 9),
    UNIQUE(estado, numero)
);


CREATE TABLE IF NOT EXISTS contratos (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    data_contratacao  TEXT    NOT NULL,
    data_encerramento TEXT,
    tipo_valor        TEXT    NOT NULL DEFAULT 'FIXO' CHECK(tipo_valor IN ('FIXO','PERCENTUAL')),
    valor             REAL    NOT NULL DEFAULT 0,
    descricao         TEXT,
    observacoes       TEXT,
    cliente_id        INTEGER NOT NULL REFERENCES clientes(id) ON DELETE RESTRICT,
    processo_id       INTEGER UNIQUE REFERENCES processos(id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS processos (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    numero         TEXT    NOT NULL UNIQUE,
    tipo           TEXT    NOT NULL CHECK(tipo IN ('CIVEL','CRIMINAL','TRABALHISTA')),
    status         TEXT    NOT NULL CHECK(status IN ('ATIVO','ARQUIVADO','ENCERRADO','SUSPENSO')),
    fase           TEXT    NOT NULL CHECK(fase IN ('CONHECIMENTO','EXECUCAO','RECURSAL','LIQUIDACAO')),
    data_abertura  TEXT    NOT NULL,
    orgao_julgador             TEXT,
    descricao                  TEXT,
    valor_causa                REAL,
    valor_condenacao           REAL,
    favoravel                  INTEGER,
    honorarios_sucumbenciais   REAL,
    data_pagamento             TEXT,
    advogado_id                INTEGER REFERENCES advogados(id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS audiencias (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao   TEXT    NOT NULL,
    data_hora   TEXT    NOT NULL,
    local       TEXT    NOT NULL,
    tipo        TEXT    NOT NULL CHECK(tipo IN ('INSTRUCAO','CONCILIACAO','JULGAMENTO','PRELIMINAR')),
    status      TEXT    NOT NULL CHECK(status IN ('AGENDADA','REALIZADA','CANCELADA')),
    resultado   TEXT,
    observacoes TEXT,
    processo_id INTEGER NOT NULL REFERENCES processos(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS interessados (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nome          TEXT    NOT NULL,
    identificador TEXT,
    telefone      TEXT,
    tipo_pessoa   TEXT    NOT NULL CHECK(tipo_pessoa IN ('FISICA','JURIDICA')),
    tipo_parte    TEXT    NOT NULL CHECK(tipo_parte IN ('AUTOR','REU','PERITO','TERCEIRO_INTERESSADO')),
    processo_id   INTEGER NOT NULL REFERENCES processos(id) ON DELETE CASCADE,
    advogado_id   INTEGER REFERENCES advogados(id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS testemunhas (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nome          TEXT    NOT NULL,
    identificador TEXT    UNIQUE,
    telefone      TEXT,
    tipo_pessoa   TEXT    NOT NULL CHECK(tipo_pessoa IN ('FISICA','JURIDICA')),
    depoimento    TEXT
);


CREATE TABLE IF NOT EXISTS testemunha_interessado (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    testemunha_id  INTEGER NOT NULL REFERENCES testemunhas(id) ON DELETE CASCADE,
    interessado_id INTEGER NOT NULL REFERENCES interessados(id) ON DELETE CASCADE,
    processo_id    INTEGER NOT NULL REFERENCES processos(id) ON DELETE CASCADE,
    UNIQUE(testemunha_id, interessado_id, processo_id)
);


CREATE TABLE IF NOT EXISTS documentos (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo           TEXT    NOT NULL,
    descricao        TEXT,
    sequencial       INTEGER NOT NULL DEFAULT 0,
    caminho_arquivo  TEXT,
    tipo             TEXT    NOT NULL CHECK(tipo IN ('PETICAO','SENTENCA','RECURSO','CONTRATO','PROCURACAO','LAUDO','OUTRO')),
    data_upload      TEXT    NOT NULL,
    is_assinado      INTEGER NOT NULL DEFAULT 0,
    is_protocolado   INTEGER NOT NULL DEFAULT 0,
    numero_protocolo TEXT,
    is_urgente       INTEGER NOT NULL DEFAULT 0,
    processo_id      INTEGER NOT NULL REFERENCES processos(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS movimentacoes (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao   TEXT    NOT NULL,
    data_hora   TEXT    NOT NULL,
    responsavel TEXT,
    tipo        TEXT    NOT NULL CHECK(tipo IN ('DESPACHO','DECISAO','SENTENCA','RECURSO','AUDIENCIA','JUNTADA','OUTRO')),
    processo_id INTEGER NOT NULL REFERENCES processos(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS assuntos_cnj (
    cod_item     INTEGER PRIMARY KEY,
    cod_item_pai INTEGER,
    nome         TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS processo_assuntos (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    processo_id INTEGER NOT NULL REFERENCES processos(id) ON DELETE CASCADE,
    cod_item    INTEGER NOT NULL REFERENCES assuntos_cnj(cod_item),
    principal   INTEGER NOT NULL DEFAULT 0,
    UNIQUE(processo_id, cod_item)
);


CREATE INDEX IF NOT EXISTS idx_processos_numero     ON processos(numero);
CREATE INDEX IF NOT EXISTS idx_processos_status     ON processos(status);
CREATE INDEX IF NOT EXISTS idx_audiencias_processo  ON audiencias(processo_id);
CREATE INDEX IF NOT EXISTS idx_audiencias_data      ON audiencias(data_hora);
CREATE INDEX IF NOT EXISTS idx_audiencias_status    ON audiencias(status);
CREATE INDEX IF NOT EXISTS idx_movimentacoes_proc   ON movimentacoes(processo_id);
CREATE INDEX IF NOT EXISTS idx_contratos_cliente    ON contratos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_oabs_advogado        ON advogado_oabs(advogado_id);
CREATE INDEX IF NOT EXISTS idx_interessados_proc    ON interessados(processo_id);
CREATE INDEX IF NOT EXISTS idx_test_int_testemunha  ON testemunha_interessado(testemunha_id);
CREATE INDEX IF NOT EXISTS idx_test_int_interessado ON testemunha_interessado(interessado_id);
CREATE INDEX IF NOT EXISTS idx_test_int_interessado ON testemunha_interessado(processo_id);
CREATE INDEX IF NOT EXISTS idx_proc_assuntos        ON processo_assuntos(processo_id);
CREATE INDEX IF NOT EXISTS idx_escr_adv_advogado    ON escritorio_advogados(advogado_id);

