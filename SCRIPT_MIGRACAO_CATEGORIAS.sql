-- ============================================================
-- SCRIPT DE MIGRAÇÃO: IMPLEMENTAÇÃO DE CATEGORIAS
-- ============================================================
-- Este script cria a tabela de categorias e relaciona com produtos

-- 1. Criar tabela categoria
CREATE TABLE IF NOT EXISTS categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

-- 2. Adicionar coluna categoria_id à tabela produto
ALTER TABLE produto
ADD COLUMN IF NOT EXISTS categoria_id BIGINT;

-- 3. Adicionar constraint de chave estrangeira (se não existir)
-- Nota: Se a constraint já existe, esta linha será ignorada
ALTER TABLE produto
ADD CONSTRAINT IF NOT EXISTS fk_produto_categoria
FOREIGN KEY (categoria_id) REFERENCES categoria(id);

-- ============================================================
-- INSERIR CATEGORIAS PADRÃO (APENAS SE TABELA ESTIVER VAZIA)
-- ============================================================
INSERT INTO categoria (nome) VALUES 
('Salgados'),
('Lanches'),
('Sucos Naturais'),
('Refrigerantes'),
('Bolos'),
('Sorvetes')
ON CONFLICT (nome) DO NOTHING;

-- ============================================================
-- ATUALIZAR PRODUTOS EXISTENTES (SE HOUVER)
-- ============================================================
-- Atribuir a primeira categoria (Salgados) aos produtos que não têm categoria
-- Isto é necessário porque categoria_id será NOT NULL após a migração
UPDATE produto 
SET categoria_id = (SELECT id FROM categoria WHERE nome = 'Salgados' LIMIT 1)
WHERE categoria_id IS NULL;

-- ============================================================
-- TORNAR categoria_id NOT NULL (FINAL)
-- ============================================================
ALTER TABLE produto
ALTER COLUMN categoria_id SET NOT NULL;

-- ============================================================
-- CRIAR ÍNDICES PARA MELHOR PERFORMANCE
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_produto_categoria_id 
ON produto(categoria_id);

CREATE INDEX IF NOT EXISTS idx_categoria_nome 
ON categoria(nome);

-- ============================================================
-- VERIFICAÇÃO FINAL (execute para validar)
-- ============================================================
-- SELECT 'Categorias criadas:' as info, COUNT(*) as total FROM categoria;
-- SELECT 'Produtos com categoria:' as info, COUNT(*) as total FROM produto WHERE categoria_id IS NOT NULL;
-- SELECT 'Colunas de produto:' as info;
-- \d produto
