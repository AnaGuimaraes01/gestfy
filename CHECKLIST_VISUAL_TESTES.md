# ✅ CHECKLIST VISUAL - O QUE MUDOU

Marque conforme testa cada item.

---

## 🛒 CLIENTE - CATÁLOGO

### Adicionar ao Carrinho
- [ ] Página abre sem erros
- [ ] Produtos carregam com imagens/nomes
- [ ] Clico "Adicionar" em um produto
- [ ] **Toast aparece no CANTO INFERIOR DIREITO** ← Principal
- [ ] Toast tem gradiente verde
- [ ] Texto: \"✨ [nome] adicionado ao carrinho!\"
- [ ] **SEM números mostrando**
- [ ] Toast desaparece após ~2,5 segundos
- [ ] Animação suave (slide in/out)

### Busca
- [ ] Campo de busca funciona
- [ ] Filtra produtos enquanto digita

---

## 🛍️ CLIENTE - CARRINHO & PEDIDO

### Carrinho
- [ ] Produtos aparecem
- [ ] Preço total correto
- [ ] Botão \"Finalizar Pedido\"

### Formulário - **NOVO LAYOUT**
- [ ] Nome ✓
- [ ] Telefone ✓
- [ ] Email ✓
- [ ] **NOVO - Endereço** ← Verifique!
  - [ ] Campo visível
  - [ ] Placeholder: \"Rua, número...\"
  - [ ] Obrigatório? Teste deixando vazio
- [ ] Forma Recebimento (Retirada/Entrega)
- [ ] Forma Pagamento (Dinheiro/PIX)

### Validações
- [ ] Nome: \"João Silva\" ✅ aceita
- [ ] Nome: \"João123\" ❌ rejeita
- [ ] Nome: \"Açaí\" ✅ aceita
- [ ] Endereço vazio + ENTREGA selecionado ❌ rejeita
- [ ] Endereço preenchido + ENTREGA ✅ aceita

### Sucesso
- [ ] Mensagem \"Pedido #[id] criado com sucesso!\"
- [ ] Redirecionado ou confirmação

---

## 📍 CLIENTE - ACOMPANHAMENTO

### Layout
- [ ] Informações do cliente aparecem
- [ ] **REMOVIDO - Campo \"Pesquisar por ID\"** ← Verifique não existe
- [ ] Dados do pedido: status, itens, total

### Dados
- [ ] Nome cliente correto (não \"undefined\")
- [ ] **NOVO - Endereço aparece** (se foi preenchido)
- [ ] Forma recebimento correto
- [ ] Status do pedido com emoji
- [ ] Itens com nomes dos produtos (não IDs)
- [ ] Total em R$ formatado

### Mensagens Dinâmicas
- [ ] Se pedido NÃO FINALIZADO: aparece dica
- [ ] Se pedido FINALIZADO: aparece mensagem sucesso

---

## 📦 ADMIN - ESTOQUE

### Interface - **NOVO LAYOUT**
- [ ] Título: \"Rastreamento de movimentações...\"
- [ ] Estatísticas no topo (3 cards)
- [ ] **Filtros bem organizados (2 COLUNAS)**
  - [ ] Coluna 1: \"Tipo de Movimentação\"
  - [ ] Coluna 2: \"Data\"
  - [ ] Labels acima (cinzas pequenos)
- [ ] **NÃO EXISTE botão \"Recarregar\"** ← Verificar

### Funcionamento
- [ ] Mudo tipo → tabela atualiza automaticamente
- [ ] Seleciono data → tabela atualiza automaticamente
- [ ] Dados carregam rápido

### Tabela
- [ ] **Colunas (SEM ID):**
  - [ ] Produto (com nome, não ID)
  - [ ] Tipo (✅ ENTRADA ou ❌ SAÍDA com cores)
  - [ ] Quantidade
  - [ ] Data/Hora
- [ ] Linhas têm background colorido (verde/vermelho leve)
- [ ] Nomes dos produtos aparecem (não \"Produto #2\")

---

## 📊 ADMIN - RELATÓRIOS

### Interface - **NOVO LAYOUT**
- [ ] Filtros em **UMA LINHA (3 COLUNAS)**:
  - [ ] Coluna 1: Data Início
  - [ ] Coluna 2: Data Fim
  - [ ] Coluna 3: Botões (lado a lado)
    - [ ] \"📊 Gerar Relatório\"
    - [ ] \"💾 Exportar CSV\"
- [ ] Texto \"Período e Filtros\" no topo

### Vendas por Dia
- [ ] Clico \"Gerar Relatório\"
- [ ] Tabela mostra:
  - [ ] Data (em cinza)
  - [ ] Qtd. de Vendas (em verde)
  - [ ] Total (R$) (em rosa)
  - [ ] **NÃO EXISTE \"Ticket Médio\"** ← Remover coluna
- [ ] Dados carregam

### Status do Estoque
- [ ] Tabela com:
  - [ ] Produto
  - [ ] Entradas (número)
  - [ ] Saídas (número)
  - [ ] Última Movimentação
- [ ] Números com cores (verde/vermelho)

### Export
- [ ] Clico \"Exportar CSV\"
- [ ] Arquivo baixa
- [ ] Nome: `relatorio_vendas_YYYY-MM-DD_YYYY-MM-DD.csv`
- [ ] Abre no Excel corretamente

---

## 💰 ADMIN - CAIXA

### Layout Inicial
- [ ] Total Arrecadado visível (grande e rosa)
- [ ] 3 Cards: Entradas, Saídas, Qtd Transações
- [ ] Botões:
  - [ ] \"🔒 Fechar Caixa do Dia\"
  - [ ] \"📊 Ver Relatório Completo\"
  - [ ] **NÃO EXISTE botão \"Recarregar\"** ← Verificar

### Tabela
- [ ] Histórico de transações

### Fechar Caixa - **NOVO FLUXO**

#### Passo 1: Clico em \"Fechar Caixa\"
- [ ] Modal de confirmação aparece
- [ ] Mostra total do dia
- [ ] Botões: [Cancelar] [Confirmar]

#### Passo 2: Confirmo
- [ ] Modal desaparece
- [ ] Mensagem: \"✅ Caixa fechado com sucesso!\"

#### Passo 3: Animação - **NOVO!**
- [ ] Modal com \"🔒 Caixa Fechado\" aparece no centro
- [ ] Animação: popup (cresce suavemente)
- [ ] Texto: \"O caixa foi fechado com sucesso para o dia de hoje.\"
- [ ] Botão: \"🔓 Abrir Caixa Novamente\"

#### Passo 4: Auto-fechar
- [ ] Modal desaparece após ~4 segundos
- [ ] Botão principal muda para:
  - [ ] Texto: \"🔒 Caixa Fechado\"
  - [ ] Desabilitado (opacity reduzida)

#### Passo 5: Reabrir (opcional)
- [ ] Clico \"🔓 Abrir Caixa Novamente\" (se ainda visível)
- [ ] Ou aguardo modal fechar
- [ ] Botão volta ao normal: \"🔒 Fechar Caixa do Dia\"
- [ ] Botão habilitado

---

## 🛠️ ADMIN - PRODUTOS

### Deletar Produto
- [ ] Página carrega
- [ ] Produtos listos
- [ ] Cada produto tem botão \"🗑️ Deletar\"
- [ ] Clico em deletar
- [ ] Confirmação: \"Tem certeza...?\"
  - [ ] Clico \"OK\"
  - [ ] Produto desaparece da lista
  - [ ] Mensagem de sucesso
  - [ ] ✅ FUNCIONA

### Editar Produto
- [ ] Botão \"✏️ Editar\"
- [ ] Carrega dados
- [ ] Altera valores
- [ ] Clica \"💾 Atualizar Produto\"
- [ ] Salva e lista atualiza
- [ ] ✅ FUNCIONA

### Adicionar Novo
- [ ] Nome com acentos: \"Açaí\" ✅
- [ ] Nome com números: \"Açaí123\" ❌
- [ ] Mensagem: \"Nome não pode conter números...\"

---

## 🔧 BACKEND VALIDATION

### Console (F12)
- [ ] **Nenhum erro vermelho**
- [ ] Requests mostram 200/201 (sucesso)
- [ ] Responses têm dados completos

### Network Tab (F12)
- [ ] POST /api/clientes → 201 Created
- [ ] GET /api/produtos → 200 OK
- [ ] DELETE /api/produtos/[id] → 204 No Content
- [ ] POST /api/pedidos → 201 Created

### Banco de Dados
- [ ] Campo `endereco` aparece na tabela `cliente`
  (Hibernate cria automaticamente)

---

## 🎯 RESUMO FINAL

```
Marque quando tudo passar:

CLIENT-SIDE:
[ ] Toast carrinho (canto inf direito)
[ ] Campo endereço (novo e validado)
[ ] Pesquisa removida (acompanhamento)
[ ] Estoque (filtros 2 colunas, automáticos)
[ ] Relatórios (botões em linha, sem ticket médio)
[ ] Caixa (animação novo fluxo)
[ ] Produtos (deletar funciona)

BACKEND:
[ ] Nenhum erro no console
[ ] Requests retornam status correto
[ ] Banco atualizado (campo endereco)

TOTAL: ___ / 15 testes passando
```

---

## ❌ O QUE NÃO DEVE EXISTIR

Verifique que estes foram REMOVIDOS:

- [ ] ❌ Campo pesquisa por ID em acompanhamento
- [ ] ❌ Botão \"Recarregar\" em estoque
- [ ] ❌ Botão \"Recarregar\" em caixa
- [ ] ❌ Coluna \"Ticket Médio\" em relatórios
- [ ] ❌ Números mostrando na mensagem toast

---

## ✅ O QUE DEVE EXISTIR

- [ ] ✅ Toast flutuante carrinho (canto inf direito)
- [ ] ✅ Campo endereço no pedido
- [ ] ✅ Filtros automáticos em estoque
- [ ] ✅ Status \"Caixa Fechado\" com animação
- [ ] ✅ Botão \"Abrir Caixa Novamente\"

---

## 📝 NOTAS

Ao testar:
1. Abra F12 (Developer Tools)
2. Vá em Console
3. Procure por erros (texto vermelho)
4. Se houver erro, anote e informe

---

**Quando TUDO estiver com ✅, o sistema está PRONTO!**

