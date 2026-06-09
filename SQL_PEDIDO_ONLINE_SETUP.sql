-- ============================================
-- INTEGRAÇÃO PEDIDOS ONLINE + CAIXA
-- Script de Setup do Banco de Dados
-- ============================================

-- 1. TABELA PEDIDO - Adicionar colunas para pedidos online
ALTER TABLE pedido
ADD COLUMN IF NOT EXISTS precisa_troco BOOLEAN DEFAULT false;

ALTER TABLE pedido
ADD COLUMN IF NOT EXISTS valor_troco DOUBLE PRECISION;

ALTER TABLE pedido
ADD COLUMN IF NOT EXISTS caixa_registro_id BIGINT;

-- 2. CRIAR ÍNDICES na tabela pedido para melhor performance
CREATE INDEX IF NOT EXISTS idx_pedido_status ON pedido(status);
CREATE INDEX IF NOT EXISTS idx_pedido_cliente_id ON pedido(cliente_id);
CREATE INDEX IF NOT EXISTS idx_pedido_data ON pedido(data);
CREATE INDEX IF NOT EXISTS idx_pedido_caixa_registro_id ON pedido(caixa_registro_id);

-- 3. Mensagens de confirmação
-- Verificar colunas adicionadas
-- SELECT column_name, data_type, is_nullable FROM information_schema.columns 
-- WHERE table_name = 'pedido' AND column_name IN ('precisa_troco', 'valor_troco', 'caixa_registro_id');

-- Verificar se caixa tem a coluna origem
-- SELECT column_name, data_type, is_nullable FROM information_schema.columns 
-- WHERE table_name = 'caixa' AND column_name = 'origem';

-- ============================================
-- ESTRUTURA FINAL ESPERADA
-- ============================================

-- TABELA PEDIDO:
-- id                | bigint            | PK, SERIAL
-- cliente_id        | bigint            | FK -> cliente
-- forma_pagamento   | VARCHAR           |
-- forma_recebimento | VARCHAR           | RETIRADA ou ENTREGA
-- endereco          | VARCHAR           |
-- status            | VARCHAR           | RECEBIDO, EM_PREPARACAO, PRONTO, FINALIZADO, CANCELADO
-- data              | TIMESTAMP         |
-- total             | DOUBLE PRECISION  |
-- precisa_troco     | BOOLEAN           | DEFAULT false
-- valor_troco       | DOUBLE PRECISION  | NULL se não precisa
-- caixa_registro_id | BIGINT            | NULL até ser finalizado (rastreamento)

-- TABELA CAIXA (após SQL_CAIXA_FIXES.sql):
-- origem            | VARCHAR           | CAIXA (presencial) ou PEDIDO_ONLINE

-- ============================================
-- REGRAS DE NEGÓCIO IMPLEMENTADAS
-- ============================================

-- 1. CRIAÇÃO DE PEDIDO (status = RECEBIDO):
--    - NÃO registra entrada no caixa automaticamente
--    - Aguarda transição de status

-- 2. TRANSIÇÃO DE STATUS (EM_PREPARACAO ou PRONTO):
--    - NÃO registra entrada no caixa
--    - Apenas atualiza status

-- 3. FINALIZAÇÃO (status = FINALIZADO):
--    - Registra entrada NO CAIXA com valor total do pedido
--    - Origem = PEDIDO_ONLINE
--    - Armazena ID do caixa em caixa_registro_id para evitar duplicidade

-- 4. CANCELAMENTO (status = CANCELADO):
--    - NÃO gera movimentação financeira
--    - Pedido é marcado como cancelado

-- ============================================
