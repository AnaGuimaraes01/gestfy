# 🧪 PLANO DE TESTE - GESTFY

## Status: EM PROGRESSO

---

## 📱 FUNCIONALIDADES CLIENTE

### 1. Catálogo de Produtos ✅
**Arquivo**: `/frontend/cliente/catalogo.html` + `js/produtos.js`

**Cenários de Teste**:
- [ ] **TC1.1**: Página carrega sem erros
  - Acesso: `/frontend/cliente/catalogo.html`
  - Expected: Produtos listados em cards com foto, nome, descrição, preço
  - Status: Não testado

- [ ] **TC1.2**: Busca por produto funciona
  - Ação: Digitar "sorvete" no campo de busca
  - Expected: Lista filtra apenas produtos com "sorvete" no nome
  - Status: Não testado

- [ ] **TC1.3**: Visualizar detalhes do produto
  - Ação: Clicar em "Detalhes"
  - Expected: Modal/card com descrição completa aparece
  - Status: Não testado

- [ ] **TC1.4**: Adicionar ao carrinho
  - Ação: Clicar "Adicionar ao Carrinho"
  - Expected: Produto adicionado, mensagem de confirmação
  - Status: Não testado

---

### 2. Carrinho de Compras ✅
**Arquivo**: `/frontend/cliente/carrinho.html` + `js/carrinho.js`

**Cenários de Teste**:
- [ ] **TC2.1**: Carrinho lista produtos corretos
  - Expected: Mostra nome, preço unitário, quantidade, subtotal
  - Status: Não testado

- [ ] **TC2.2**: Aumentar/diminuir quantidade
  - Ação: Clica em "+" ou "-"
  - Expected: Quantidade e total recalculam corretamente
  - Status: Não testado

- [ ] **TC2.3**: Remover produto
  - Ação: Clica em "Remover" ou "X"
  - Expected: Produto sai do carrinho, total recalcula
  - Status: Não testado

- [ ] **TC2.4**: Total do carrinho está correto
  - Expected: Sum(subtotal de cada item) = Total exibido
  - Status: Não testado

---

### 3. Pedido - Endereço ✅
**Arquivo**: `/frontend/cliente/pedido.html` + `js/pedido.js`

**Cenários de Teste**:
- [ ] **TC3.1**: Campo endereço aparece apenas para "Entrega"
  - Ação: Selecionar "Entrega" em "Forma de Recebimento"
  - Expected: Campo "Endereço" aparece vazio e obrigatório
  - Status: Não testado

- [ ] **TC3.2**: Campo endereço desaparece para "Retirada"
  - Ação: Selecionar "Retirada no Local"
  - Expected: Campo endereço fica hidden
  - Status: Não testado

- [ ] **TC3.3**: Validação de endereço obrigatório
  - Ação: Selecionar "Entrega", deixar endereço vazio, enviar
  - Expected: Erro "Endereço obrigatório para entrega"
  - Status: Não testado

- [ ] **TC3.4**: Pedido criado com endereço
  - Ação: Preencher form com endereço válido e enviar
  - Expected: 
    - Pedido criado com sucesso
    - Mensagem de confirmação
    - Endereço salvo no banco
  - Status: Não testado

---

### 4. Pagamento ✅
**Arquivo**: `/frontend/cliente/pedido.html`

**Cenários de Teste**:
- [ ] **TC4.1**: Opções de pagamento aparecem
  - Expected: "Pagar ao Retirar" e "Pagar ao Entregar"
  - Status: Não testado

- [ ] **TC4.2**: Seleção de pagamento é obrigatória
  - Ação: Tentar finalizar sem selecionar pagamento
  - Expected: Erro ou campo fica obrigatório
  - Status: Não testado

---

### 5. Acompanhamento de Pedido ✅
**Arquivo**: `/frontend/cliente/acompanhamento.html` + script inline

**Cenários de Teste**:
- [ ] **TC5.1**: Página carrega com últimos pedido
  - Expected: Mostra pedido salvo em localStorage
  - Status: Não testado

- [ ] **TC5.2**: Status exibido corretamente
  - Expected: Mostra "Recebido", "Em Preparo", "Pronto para Retirada", "Saiu para Entrega", "Finalizado"
  - Verificar emojis: 📋, 👨‍🍳, ✅, 🚗, 🎉
  - Status: Não testado

- [ ] **TC5.3**: Pesquisar por ID de outro pedido
  - Ação: Digitar ID em "pesquisaId" e clicar "Pesquisar"
  - Expected: Carrega pedido solicitado
  - Status: Não testado

- [ ] **TC5.4**: Total e itens listados corretamente
  - Expected: Mostra nome produto, quantidade, preço unitário, subtotal
  - Status: Não testado

---

## 👨‍💼 FUNCIONALIDADES ADMIN

### 6. Login Admin ✅
**Arquivo**: `/frontend/admin/login.html` + `js/auth.js`

**Cenários de Teste**:
- [ ] **TC6.1**: Página login carrega
  - Expected: Form com usuário e senha
  - Status: Não testado

- [ ] **TC6.2**: Credenciais corretas (admin/admin123)
  - Ação: Digitar admin/admin123 e enviar
  - Expected: 
    - sessionStorage setItem("adminAuthenticated", "true")
    - Redireciona para index.html
  - Status: Não testado

- [ ] **TC6.3**: Credenciais incorretas
  - Ação: Digitar user/pass errados
  - Expected: Erro "Credenciais inválidas"
  - Status: Não testado

---

### 7. Menu Admin ✅
**Arquivo**: `/frontend/admin/index.html`

**Cenários de Teste**:
- [ ] **TC7.1**: Menu carrega após login
  - Expected: 5 cards: Pedidos, Produtos, Estoque, Caixa, Relatórios
  - Status: Não testado

- [ ] **TC7.2**: Links de navegação funcionam
  - Ação: Clicar em cada card
  - Expected: Navega para página correta (pedidos.html, produtos.html, etc)
  - Status: Não testado

- [ ] **TC7.3**: Botão "Sair" funciona
  - Ação: Clicar em "Sair" na barra
  - Expected: 
    - sessionStorage limpo
    - Redireciona para login.html
  - Status: Não testado

---

### 8. Gerenciamento de Pedidos ✅
**Arquivo**: `/frontend/admin/pedidos.html` + `js/pedidos.js`

**Cenários de Teste**:
- [ ] **TC8.1**: Lista de pedidos carrega
  - Expected: Tabela com ID, Cliente, Telefone, Pagamento, Total, Status, Ações
  - Status: Não testado

- [ ] **TC8.2**: Atualizar status do pedido
  - Ação: Clicar select status, escolher novo status (ex: EM_PREPARO)
  - Expected: 
    - Status atualizado no backend
    - Tabela recarrega
    - Novo status visível
  - Status: Não testado

- [ ] **TC8.3**: Ver detalhes do pedido
  - Ação: Clicar "Detalhes"
  - Expected: Alert mostrando:
    - ID do pedido
    - Nome cliente
    - Endereço (ou "Retirada no local")
    - Total
    - Status
    - Forma de pagamento
    - Itens (nome, quantidade, preço)
  - Status: Não testado

- [ ] **TC8.4**: Endereço exibido corretamente
  - Ação: Ver detalhes de pedido com entrega
  - Expected: "Endereço: Rua X, nº Y, ..." (ou "Retirada no local")
  - Status: Não testado

- [ ] **TC8.5**: Sem pedidos
  - Scenario: Nenhum pedido existe
  - Expected: "Nenhum pedido registrado"
  - Status: Não testado

---

### 9. Gerenciamento de Produtos ✅
**Arquivo**: `/frontend/admin/produtos.html` + `js/produtos.js`

**Cenários de Teste**:
- [ ] **TC9.1**: Listar produtos
  - Expected: Mostra nome, descrição, preço, quantidade, ID
  - Status: Não testado

- [ ] **TC9.2**: Criar novo produto
  - Ação: Preencher form (nome, descrição, preço, quantidade, foto) e clicar "Salvar"
  - Expected: 
    - POST /api/produtos com dados
    - Mensagem de sucesso
    - Produto aparece na lista
    - Form limpa
  - Status: Não testado

- [ ] **TC9.3**: Validar campos obrigatórios
  - Ação: Tentar salvar produto sem Nome
  - Expected: Erro "Nome obrigatório"
  - Status: Não testado

- [ ] **TC9.4**: Editar produto existente
  - Ação: Clicar "✏️ Editar" em um produto
  - Expected: 
    - Form preenche com dados do produto
    - Botão muda para "💾 Atualizar Produto"
  - Status: Não testado

- [ ] **TC9.5**: Atualizar produto sem duplicar
  - Ação: Editar valor (ex: preço) e clicar "Atualizar"
  - Expected: 
    - PUT /api/produtos/{id}
    - Produto atualizado
    - Não cria novo produto
    - Lista recarrega
  - Status: Não testado

- [ ] **TC9.6**: Cancelar edição
  - Ação: Clicar "Cancelar Edição" durante edição
  - Expected: 
    - Form limpa
    - Botão volta a "Salvar Produto"
    - Modo edição desativado
  - Status: Não testado

- [ ] **TC9.7**: Sem botão Delete
  - Expected: Botão DELETE não aparece na interface
  - Status: Não testado

---

### 10. Controle de Estoque ✅
**Arquivo**: `/frontend/admin/estoque.html` + `js/estoque.js`

**Cenários de Teste**:

#### SEÇÃO 1: Inventário

- [ ] **TC10.1**: Tabela de estoque carrega
  - Expected: Mostra ID, Nome, Preço, Quantidade, Status
  - Status: Não testado

- [ ] **TC10.2**: Alertas de estoque baixo
  - Expected: 
    - Qtd ≤ 0 → "⚠️ EM FALTA" (vermelho/🔴)
    - 0 < Qtd ≤ 5 → "⚠️ ESTOQUE BAIXO" (laranja/🟠)
    - Qtd > 5 → "✅ Disponível" (verde/🟢)
  - Status: Não testado

- [ ] **TC10.3**: Resumo de estoque
  - Expected: 
    - Cards: "Total Produtos", "Em Falta", "Estoque Baixo"
    - Números corretos
  - Status: Não testado

- [ ] **TC10.4**: Filtro por nome
  - Ação: Digitar nome do produto no campo "Filtrar por Nome"
  - Expected: Tabela filtra em tempo real
  - Status: Não testado

- [ ] **TC10.5**: Limpar filtros
  - Ação: Clicar "Limpar"
  - Expected: 
    - Campo fica vazio
    - Tabela mostra todos produtos
  - Status: Não testado

#### SEÇÃO 2: Registrar Movimentação

- [ ] **TC10.6**: Select de produtos funciona
  - Expected: Dropdown mostra todos os produtos com nomes
  - Status: Não testado

- [ ] **TC10.7**: Registrar entrada
  - Ação: Selecionar produto, tipo "📥 Entrada", qtd "10", clicar "Registrar"
  - Expected: 
    - POST /api/estoque com movimento
    - Mensagem sucesso
    - Quantidade do produto aumenta em 10
    - Form reseta
  - Status: Não testado

- [ ] **TC10.8**: Registrar saída
  - Ação: Selecionar produto, tipo "📤 Saída", qtd "2", clicar "Registrar"
  - Expected: 
    - POST /api/estoque com movimento
    - Quantidade diminui
    - Movimento aparece em "Últimas Movimentações"
  - Status: Não testado

- [ ] **TC10.9**: Validar quantidade
  - Ação: Tentar registrar saída maior que quantidade disponível
  - Expected: Sistema permite (ou avisa)
  - Status: Não testado

#### SEÇÃO 3: Últimas Movimentações

- [ ] **TC10.10**: Tabela de movimentações
  - Expected: Mostra ID Movimento, Data, Produto, Tipo, Quantidade, Usuário
  - Status: Não testado

- [ ] **TC10.11**: Últimas 20 movimentações
  - Expected: Mostra apenas últimas 20 por padrão
  - Status: Não testado

- [ ] **TC10.12**: Produto por nome (não ID)
  - Expected: Coluna "Produto" mostra NOME não ID
  - Status: Não testado

---

### 11. Caixa com Autenticação ✅
**Arquivo**: `/frontend/admin/caixa-login.html` + `caixa.html` + `js/caixa-auth.js`

**Cenários de Teste**:

#### Login do Caixa

- [ ] **TC11.1**: Acessar página caixa.html sem autenticação
  - Expected: Redireciona para caixa-login.html
  - Status: Não testado

- [ ] **TC11.2**: Página login caixa carrega
  - Expected: Form com usuário e senha, tema verde
  - Status: Não testado

- [ ] **TC11.3**: Login com credenciais corretas (caixa01/caixa123)
  - Ação: Digitar caixa01/caixa123 e enviar
  - Expected: 
    - sessionStorage.caixaAuthenticated = "true"
    - sessionStorage.caixaUser = "caixa01"
    - sessionStorage.caixaOpenedAt = timestamp
    - Redireciona para caixa.html
  - Status: Não testado

- [ ] **TC11.4**: Login com credenciais erradas
  - Ação: Digitar user/pass incorretos
  - Expected: Erro "Credenciais inválidas"
  - Status: Não testado

#### Caixa Aberto

- [ ] **TC11.5**: Barra de informações
  - Expected: Mostra "💰 caixa01 | Aberto às HH:MM | [Fechar Caixa]"
  - Status: Não testado

- [ ] **TC11.6**: Carregamento de dados
  - Expected: 
    - Cards com resumo (Total Arrecadado, Entradas, Saídas, Transações)
    - Tabela de registros de caixa
  - Status: Não testado

- [ ] **TC11.7**: Filtrar por data (opcional)
  - Expected: Pode filtrar movimentações por período
  - Status: Não testado

#### Fechar Caixa

- [ ] **TC11.8**: Botão "Fechar Caixa"
  - Expected: Ao clicar, pede confirmação
  - Status: Não testado

- [ ] **TC11.9**: Confirmar fechamento
  - Ação: Clicar "Fechar Caixa" e confirmar
  - Expected: 
    - sessionStorage limpo (caixaAuthenticated, caixaUser, caixaOpenedAt)
    - Redireciona para caixa-login.html
  - Status: Não testado

- [ ] **TC11.10**: Cancelar fechamento
  - Ação: Clicar "Fechar Caixa", clicar "Cancelar" no confirm
  - Expected: Permanece em caixa.html, dados não apagam
  - Status: Não testado

---

### 12. Relatórios ✅
**Arquivo**: `/frontend/admin/relatorios.html` + script inline

**Cenários de Teste**:

#### Estatísticas Gerais

- [ ] **TC12.1**: Estatísticas carregam
  - Expected: Mostra "Total Vendido (Hoje)", "Quantidade de Vendas", "Ticket Médio"
  - Status: Não testado

- [ ] **TC12.2**: Valores corretos
  - Expected: Valores consultados do backend (/api/relatorios/vendas-por-dia)
  - Status: Não testado

#### Filtros de Período

- [ ] **TC12.3**: Seletores de data
  - Expected: 2 campos de data para início e fim
  - Status: Não testado

- [ ] **TC12.4**: Gerar relatório
  - Ação: Selecionar período e clicar "Gerar Relatório"
  - Expected: Tabelas atualizam com dados do período
  - Status: Não testado

- [ ] **TC12.5**: Exportar CSV
  - Ação: Clicar "Exportar CSV"
  - Expected: Download de arquivo .csv com dados
  - Status: Não testado

#### Vendas por Dia

- [ ] **TC12.6**: Tabela de vendas
  - Expected: Mostra Data, Quantidade de Vendas, Total (R$), Ticket Médio
  - Status: Não testado

- [ ] **TC12.7**: Dados corretos
  - Expected: Sum(Total) = Total Vendido, Count = Quantidade Vendas
  - Status: Não testado

#### Status do Estoque

- [ ] **TC12.8**: Tabela de estoque
  - Expected: Mostra Produto, Últimas Entradas, Últimas Saídas, Data Última Movimentação
  - Status: Não testado

- [ ] **TC12.9**: Movimentações de produtos
  - Expected: Mostra quantidade de entradas/saídas por produto
  - Status: Não testado

---

## 🔐 SEGURANÇA & ACESSO

### 13. Autenticação e Proteção ✅

- [ ] **TC13.1**: Não consegue acessar /admin sem login
  - Ação: Digitar `/admin/index.html` na barra sem estar autenticado
  - Expected: Redireciona para login.html
  - Status: Não testado

- [ ] **TC13.2**: Não consegue acessar caixa sem login
  - Ação: Digitar `/admin/caixa.html` na barra sem estar autenticado
  - Expected: Redireciona para caixa-login.html
  - Status: Não testado

- [ ] **TC13.3**: sessionStorage isolado
  - Expected: 
    - Login admin ≠ Login caixa
    - Cada um com sua sessão independente
  - Status: Não testado

- [ ] **TC13.4**: Cliente não acessa admin
  - Expected: Cliente não consegue acessar `/admin/`
  - Status: Não testado

---

## 🔗 INTEGRAÇÃO BACKEND-FRONTEND

### 14. APIs Funcionando ✅

- [ ] **TC14.1**: API URLs usam HTTPS Render
  - Expected: Nenhuma URL localhost
  - Check: 
    - produtos.js: `https://gestfy-backend.onrender.com/api/produtos`
    - pedidos.js: `https://gestfy-backend.onrender.com/api/pedidos`
    - estoque.js: `https://gestfy-backend.onrender.com/api/estoque`
    - caixa.html: `https://gestfy-backend.onrender.com/api/caixa`
    - relatorios.html: `https://gestfy-backend.onrender.com/api/relatorios`
  - Status: Não testado

- [ ] **TC14.2**: CORS habilitado
  - Expected: Requisições de qualquer origem funcionam
  - Status: Não testado

- [ ] **TC14.3**: Desconto automático de estoque
  - Ação: Cliente finaliza pedido com produto X quantidade Y
  - Expected: 
    - Estoque de X diminui em Y
    - SAIDA criada automaticamente
    - Quantidade refletida em admin/estoque.html
  - Status: Não testado

- [ ] **TC14.4**: Registro automático no caixa
  - Ação: Pedido finalizado
  - Expected: 
    - Entrada criada no caixa com valor correto
    - Aparece em admin/caixa.html
  - Status: Não testado

---

## 📝 RESUMO DO PLANO

**Total de Testes**: 90
**Testes Críticos**: 30
**Testes Passados**: 0
**Status Geral**: EM PROGRESSO

### Testes Críticos (Deve Passar):
1. TC6.2 - Login admin funciona
2. TC8.2 - Atualizar status pedido
3. TC9.4 - Editar produto sem duplicar
4. TC10.7 - Registrar entrada estoque
5. TC11.3 - Login caixa funciona
6. TC14.3 - Desconto automático estoque

---

**Próximo Passo**: Executar testes em sequência e documentar resultados.
