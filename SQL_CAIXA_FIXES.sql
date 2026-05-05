-- ========================================
-- SCRIPT PARA CORRIGIR TABELA CAIXA (NEON)
-- ========================================
-- Executar no PostgreSQL via Neon Console
-- ========================================

-- 1. ADICIONAR COLUNA valor_final (se não existir)
ALTER TABLE caixa
ADD COLUMN IF NOT EXISTS valor_final DOUBLE PRECISION;

-- 2. ADICIONAR COLUNA data_abertura (se não existir) - TIMESTAMP NOT NULL
-- Se já existe, converter para NOT NULL
ALTER TABLE caixa
ADD COLUMN IF NOT EXISTS data_abertura TIMESTAMP NOT NULL DEFAULT NOW();

-- 3. ADICIONAR COLUNA data_fechamento (se não existir) - TIMESTAMP
ALTER TABLE caixa
ADD COLUMN IF NOT EXISTS data_fechamento TIMESTAMP;

-- 4. ADICIONAR COLUNA horario_abertura (se não existir) - para compatibilidade
ALTER TABLE caixa
ADD COLUMN IF NOT EXISTS horario_abertura TIMESTAMP;

-- 5. ADICIONAR COLUNA horario_fechamento (se não existir) - para compatibilidade
ALTER TABLE caixa
ADD COLUMN IF NOT EXISTS horario_fechamento TIMESTAMP;

-- 6. GARANTIR que status é NOT NULL (se não for)
ALTER TABLE caixa
ALTER COLUMN status SET NOT NULL;

-- 7. GARANTIR que valor_inicial é NOT NULL DEFAULT 0
ALTER TABLE caixa
ALTER COLUMN valor_inicial SET NOT NULL,
ALTER COLUMN valor_inicial SET DEFAULT 0;

-- 8. CRIAR ÍNDICES para melhor performance
CREATE INDEX IF NOT EXISTS idx_caixa_data ON caixa(data);
CREATE INDEX IF NOT EXISTS idx_caixa_status ON caixa(status);
CREATE INDEX IF NOT EXISTS idx_caixa_data_status ON caixa(data, status);
CREATE INDEX IF NOT EXISTS idx_caixa_data_tipo ON caixa(data, tipo);
CREATE INDEX IF NOT EXISTS idx_caixa_data_tipo_status ON caixa(data, tipo, status);

-- ========================================
-- VERIFICAR ESTRUTURA FINAL
-- ========================================
-- SELECT * FROM information_schema.columns WHERE table_name = 'caixa' ORDER BY ordinal_position;

-- ========================================
-- TABELA ESPERADA (FINAL):
-- ========================================
-- id                 | bigint         | PK, SERIAL
-- valor_inicial      | DOUBLE PRECISION | NOT NULL DEFAULT 0
-- valor_final        | DOUBLE PRECISION | NULL
-- saldo              | DOUBLE PRECISION | NULL
-- data               | DATE           | NULL
-- data_abertura      | TIMESTAMP      | NOT NULL DEFAULT NOW()
-- data_fechamento    | TIMESTAMP      | NULL
-- horario_abertura   | TIMESTAMP      | NULL
-- horario_fechamento | TIMESTAMP      | NULL
-- status             | VARCHAR        | NOT NULL
-- descricao          | VARCHAR        | NULL
-- observacoes        | VARCHAR        | NULL
-- tipo               | VARCHAR        | NULL
-- ========================================
