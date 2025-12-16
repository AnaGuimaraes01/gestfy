# 🎯 COMO O SISTEMA FUNCIONA - EXPLICAÇÃO RÁPIDA

## 🏗️ ARQUITETURA

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  FRONTEND (HTML/JS)          BACKEND (Spring Boot)         │
│  ─────────────────           ────────────────────          │
│                                                             │
│  produtos.html   ─ fetch ──→  ProdutoController            │
│  pedidos.html    ─ fetch ──→  PedidoController             │
│  estoque.html    ─ fetch ──→  EstoqueController ✅ NOVO!   │
│  relatorios.html ─ fetch ──→  RelatorioController          │
│  caixa.html      ─ fetch ──→  CaixaController              │
│  clientes.html   ─ fetch ──→  ClienteController            │
│                                     ↓                       │
│                            ┌─────────────────┐              │
│                            │   BANCO DADOS   │              │
│                            │  PostgreSQL     │              │
│                            └─────────────────┘              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 💾 5 TABELAS PRINCIPAIS

### 1️⃣ CLIENTE
```
id | nome | email | telefone | criado_em
1  | João | jo... | 119...   | 2025-12-16
```

### 2️⃣ PRODUTO
```
id | nome | descricao | preco | urlFoto
1  | Sorvete | Gelado | 15.00 | https://...
```

### 3️⃣ PEDIDO (relação com cliente)
```
id | clienteId | total | status | data
1  | 1         | 45.00 | FINALIZADO | 2025-12-16
```

### 4️⃣ PEDIDO_ITEM (relação com pedido + produto)
```
id | pedidoId | produtoId | quantidade | precoUnitario
1  | 1        | 1         | 3          | 15.00
```

### 5️⃣ ESTOQUE (rastreia movimentações)
```
id | produtoId | tipoMovimento | quantidade | dataMovimento
1  | 1         | SAIDA         | 3          | 2025-12-16 14:30
2  | 1         | ENTRADA       | 10         | 2025-12-16 10:00
```

### 6️⃣ CAIXA (fluxo de caixa)
```
id | descricao | tipo | valor | data
1  | Venda     | CREDITO | 45.00 | 2025-12-16
```

---

## 🔄 FLUXO COMPLETO: UM CLIENTE COMPRANDO

```
PASSO 1: Cliente vê produtos
┌──────────────────────────────┐
│ cliente/catalogo.html        │
│ → GET /api/produtos          │
│ ← JSON com produtos          │
│ ✅ Mostra imagens (200px)    │
└──────────────────────────────┘

PASSO 2: Cliente adiciona ao carrinho
┌──────────────────────────────┐
│ cliente/carrinho.html        │
│ Produtos no carrinho (local) │
│ ✅ localStorage              │
└──────────────────────────────┘

PASSO 3: Cliente finaliza compra
┌──────────────────────────────┐
│ POST /api/pedidos            │
│ {                            │
│   "clienteId": 1,            │
│   "itens": [                 │
│     {"produtoId": 1,         │
│      "quantidade": 2,        │
│      "precoUnitario": 15.00} │
│   ]                          │
│ }                            │
│                              │
│ ← CRIA:                      │
│   - 1 PEDIDO (status=PEND.)  │
│   - 2 PEDIDO_ITEM            │
│   - Calcula TOTAL            │
└──────────────────────────────┘

PASSO 4: Admin finaliza pedido
┌──────────────────────────────┐
│ admin/pedidos.html           │
│ → PUT /api/pedidos/1         │
│   {"status": "FINALIZADO"}   │
│                              │
│ ← CRIA:                      │
│   - MUDA status para FINAL.  │
│   - CRIA 1 ESTOQUE SAIDA     │
│     (quantidade=2)           │
│   - CRIA 1 CAIXA CREDITO     │
│     (valor=30.00)            │
└──────────────────────────────┘

PASSO 5: Admin vê estoque
┌──────────────────────────────┐
│ admin/estoque.html           │
│ → GET /api/estoque           │
│ ← Mostra SAIDA de -2 unid.   │
│   Mostra ENTRADA de +10      │
│   Calcula saldo = 8          │
└──────────────────────────────┘

PASSO 6: Admin vê relatório
┌──────────────────────────────┐
│ admin/relatorios.html        │
│ → GET /api/relatorios/...    │
│ ← Vendas do dia = R$ 30.00   │
│   Ticket médio = R$ 30.00    │
│   Produtos vendidos = 2 un.  │
└──────────────────────────────┘
```

---

## 📦 OS 6 CONTROLLERS (ENDPOINTS)

### ✅ ProdutoController (`/api/produtos`)
```
POST   /api/produtos              → Criar
GET    /api/produtos              → Listar todos
GET    /api/produtos/{id}         → Buscar por ID
GET    /api/produtos/buscar       → Buscar por nome
PUT    /api/produtos/{id}         → Atualizar
DELETE /api/produtos/{id}         → Deletar
```

### ✅ ClienteController (`/api/clientes`)
```
POST   /api/clientes              → Criar
GET    /api/clientes              → Listar todos
GET    /api/clientes/{id}         → Buscar por ID
PUT    /api/clientes/{id}         → Atualizar
DELETE /api/clientes/{id}         → Deletar
```

### ✅ PedidoController (`/api/pedidos`)
```
POST   /api/pedidos               → Criar pedido
GET    /api/pedidos               → Listar todos
GET    /api/pedidos/{id}          → Buscar por ID
PUT    /api/pedidos/{id}          → Finalizar pedido
DELETE /api/pedidos/{id}          → Deletar
```

### ✅ EstoqueController (`/api/estoque`) - NOVO!
```
POST   /api/estoque               → Criar movimentação manual
GET    /api/estoque               → Listar todas
GET    /api/estoque/{id}          → Buscar por ID
GET    /api/estoque/filtro/tipo   → Filtrar por ENTRADA/SAIDA
GET    /api/estoque/filtro/data   → Filtrar por data
GET    /api/estoque/filtro/produto → Filtrar por produto
GET    /api/estoque/resumo        → Saldo total
PUT    /api/estoque/{id}          → Atualizar
DELETE /api/estoque/{id}          → Deletar
```

### ✅ CaixaController (`/api/caixa`)
```
POST   /api/caixa                 → Criar entrada
GET    /api/caixa                 → Listar todas
GET    /api/caixa/{id}            → Buscar por ID
PUT    /api/caixa/{id}            → Atualizar
DELETE /api/caixa/{id}            → Deletar
```

### ✅ RelatorioController (`/api/relatorios`)
```
GET    /api/relatorios/vendas-por-dia        → Vendas do dia
GET    /api/relatorios/produtos-mais-vendidos → Top 5
GET    /api/relatorios/total-pedidos         → Resumo período
GET    /api/relatorios/estoque-baixo         → Produtos críticos
```

---

## 🎨 FRONTEND: 12 PÁGINAS

### ADMIN (admin/)
```
index.html       → Menu principal
produtos.html    → CRUD Produtos
pedidos.html     → Ver/Finalizar Pedidos
estoque.html     → Movimentações + Resumo
relatorios.html  → Vendas + CSV
caixa.html       → Fluxo de caixa
```

### CLIENTE (cliente/)
```
index.html       → Login/Bem-vindo
catalogo.html    → Ver Produtos (com imagens)
carrinho.html    → Adicionar items (localStorage)
pedido.html      → Criar novo pedido
pedidos.html     → Ver meus pedidos
acompanhamento.html → Status do pedido
```

---

## 🖼️ COMO IMAGENS FUNCIONAM

```
ADMIN CADASTRA:
┌─────────────────┐
│ URL da Imagem   │ ← Admin coloca link
│ https://via...  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│ Salva em PRODUTO.urlFoto        │
└─────────┬───────────────────────┘
          │
          ├──→ admin/produtos.html
          │    Mostra 80x80px thumbnail
          │    + nome + preço + botões
          │
          └──→ cliente/catalogo.html
               Mostra 200px card
               + nome + preço
               + "Adicionar"

SE URL INVÁLIDA:
┌──────────────┐
│ onerror      │ ← img alt
│ 🍦 emoji     │
└──────────────┘
```

---

## ⚡ AUTO-REFRESH

```
admin/estoque.html
setInterval(() => { carregarEstoque() }, 30000)
↑ A cada 30 segundos, recarrega dados SEM F5

admin/relatorios.html
setInterval(() => { gerarRelatorio() }, 30000)
↑ A cada 30 segundos, recarrega relatório
```

---

## 🔌 RESUMO: O QUE ESTÁ FUNCIONANDO

```
✅ Criar Produto            (POST /api/produtos)
✅ Listar Produtos          (GET /api/produtos)
✅ Deletar Produto          (DELETE /api/produtos/{id})
✅ Criar Cliente            (POST /api/clientes)
✅ Criar Pedido             (POST /api/pedidos)
✅ Finalizar Pedido         (PUT /api/pedidos/{id})
  ├→ Cria SAIDA em estoque  (AUTO)
  └→ Cria entrada em caixa  (AUTO)
✅ Ver Estoque              (GET /api/estoque)
✅ Filtrar Estoque          (GET /api/estoque/filtro/*)
✅ Ver Relatórios           (GET /api/relatorios/*)
✅ Exportar CSV             (JavaScript blob)
✅ Imagens em 2 tamanhos    (80px admin, 200px client)
✅ Emoji fallback           (Se URL inválida)
✅ Auto-refresh             (30 segundos)
```

---

## 📊 EXEMPLO: CRIAR E TESTAR TUDO

### 1. Criar Produto (Thunder Client)
```
POST http://localhost:8080/api/produtos
{
  "nome": "Sorvete",
  "descricao": "Gelado",
  "preco": 15.00,
  "urlFoto": "https://via.placeholder.com/300"
}
→ ✅ ID: 1 retornado
```

### 2. Ver em Admin (Interface)
```
→ Abrir admin/produtos.html
→ Deve listar "Sorvete" com thumbnail
```

### 3. Ver em Cliente (Interface)
```
→ Abrir cliente/catalogo.html
→ Deve mostrar "Sorvete" com imagem grande
```

### 4. Comprar (Interface)
```
→ Clicar "Adicionar"
→ Abrir cliente/carrinho.html
→ Preencher nome do cliente
→ Clicar "Confirmar"
→ ✅ Pedido criado
```

### 5. Ver em Estoque (Thunder Client)
```
GET http://localhost:8080/api/estoque
→ ✅ Deve ter 1 ENTRADA (de quando?)
→ Admin finaliza pedido em admin/pedidos.html
→ GET novamente
→ ✅ Deve ter 1 SAIDA
```

### 6. Ver em Relatório (Interface)
```
→ Abrir admin/relatorios.html
→ ✅ Deve mostrar venda
```

---

**Sistema pronto para testar!** 🚀
