# 🧪 TESTES - INTERFACE + THUNDER CLIENT

## 📋 COMO FUNCIONA O SISTEMA

```
CLIENTE                    BACKEND                    BANCO
   ↓                          ↓                         ↓
(HTML/JS) ─ fetch API ────→ Controllers ──→ Repositories ──→ PostgreSQL
   ↑                          ↑                         ↑
   └─────────── JSON response ──────────┘
```

### Fluxo Pedido:
1. Cliente compra → Cria Pedido
2. Pedido finalizado → Cria Movimentação SAIDA em Estoque
3. Admin vê SAIDA em /api/estoque
4. Relatório soma SIDAs para vendas

### DTOs (Request/Response):
- **Request**: Vem do cliente (validado com @NotNull)
- **Response**: Volta para cliente (JSON)

---

## 🎨 TESTANDO NA INTERFACE

### 1️⃣ PRODUTOS (admin/produtos.html)

#### Cadastrar
```
Nome: Sorvete
Descrição: Gelado
Preço: 15.00
URL: https://via.placeholder.com/300
→ Clicar "Salvar Produto"
✅ Deve aparecer com thumbnail (80x80)
```

#### Visualizar
```
→ Abrir cliente/catalogo.html
✅ Mesmo produto com imagem grande (200px)
```

#### Deletar
```
→ Voltar admin/produtos.html
→ Clicar 🗑️ no produto
✅ Desaparece da lista
```

---

### 2️⃣ PEDIDOS (cliente/catalogo.html)

#### Criar Pedido
```
1. Clicar "➕ Adicionar" em um produto
2. Abrir cliente/carrinho.html
✅ Produto aparece com qtd e preço
3. Preencher "Nome Cliente"
4. Clicar "Confirmar Pedido"
✅ Pedido criado, lista atualiza
```

#### Ver Pedidos
```
→ Abrir cliente/pedidos.html
✅ Mostra todos os pedidos do cliente
```

---

### 3️⃣ ESTOQUE (admin/estoque.html)

#### Ver Movimentações
```
→ Abrir admin/estoque.html
✅ Tabela mostra:
   - Entradas (quando compra/reabastecimento)
   - Saídas (quando finaliza pedido)
```

#### Filtrar por Tipo
```
1. Selecionar "SAIDA"
2. Clicar "Recarregar"
✅ Mostra apenas saídas
```

#### Filtrar por Data
```
1. Selecionar uma data
2. Clicar "Recarregar"
✅ Mostra movimentações daquele dia
```

---

### 4️⃣ RELATÓRIOS (admin/relatorios.html)

#### Ver Vendas
```
→ Abrir admin/relatorios.html
✅ Mostra:
   - Total Vendido (R$)
   - Quantidade Vendas
   - Ticket Médio
```

#### Exportar CSV
```
1. Clicar "💾 Exportar CSV"
✅ Download arquivo
```

---

## 🚀 TESTANDO COM THUNDER CLIENT (ou Postman/Curl)

### 🔗 BASE URL
```
http://localhost:8080/api
```

### ============ PRODUTOS ============

#### CREATE (POST)
```
POST http://localhost:8080/api/produtos

Body (JSON):
{
  "nome": "Sorvete Chocolate",
  "descricao": "Delicioso",
  "preco": 12.00,
  "urlFoto": "https://via.placeholder.com/300"
}

✅ Response 200:
{
  "id": 1,
  "nome": "Sorvete Chocolate",
  "descricao": "Delicioso",
  "preco": 12.00,
  "urlFoto": "https://via.placeholder.com/300"
}
```

#### READ (GET ALL)
```
GET http://localhost:8080/api/produtos

✅ Response 200:
[
  {
    "id": 1,
    "nome": "Sorvete Chocolate",
    "descricao": "Delicioso",
    "preco": 12.00,
    "urlFoto": "https://via.placeholder.com/300"
  }
]
```

#### READ (GET BY ID)
```
GET http://localhost:8080/api/produtos/1

✅ Response 200:
{
  "id": 1,
  "nome": "Sorvete Chocolate",
  ...
}
```

#### UPDATE (PUT)
```
PUT http://localhost:8080/api/produtos/1

Body:
{
  "nome": "Sorvete Chocolate Premium",
  "descricao": "Mais delicioso ainda",
  "preco": 18.00,
  "urlFoto": "https://via.placeholder.com/300"
}

✅ Response 200: Produto atualizado
```

#### DELETE
```
DELETE http://localhost:8080/api/produtos/1

✅ Response 204: No Content (deletado)
```

---

### ============ CLIENTES ============

#### CREATE (POST)
```
POST http://localhost:8080/api/clientes

Body:
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "11999999999"
}

✅ Response 201: Cliente criado
```

#### READ (GET ALL)
```
GET http://localhost:8080/api/clientes

✅ Response 200: Array de clientes
```

#### READ (GET BY ID)
```
GET http://localhost:8080/api/clientes/1

✅ Response 200: Cliente específico
```

#### UPDATE (PUT)
```
PUT http://localhost:8080/api/clientes/1

Body:
{
  "nome": "João Silva Updated",
  "email": "joao@email.com",
  "telefone": "11988888888"
}

✅ Response 200: Atualizado
```

#### DELETE
```
DELETE http://localhost:8080/api/clientes/1

✅ Response 204: Deletado
```

---

### ============ PEDIDOS ============

#### CREATE (POST)
```
POST http://localhost:8080/api/pedidos

Body:
{
  "clienteId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2,
      "precoUnitario": 12.00
    }
  ]
}

✅ Response 201: Pedido criado com status PENDENTE
```

#### READ (GET ALL)
```
GET http://localhost:8080/api/pedidos

✅ Response 200: Array de pedidos
```

#### READ (GET BY ID)
```
GET http://localhost:8080/api/pedidos/1

✅ Response 200: Pedido específico
```

#### FINALIZAR PEDIDO (PUT)
```
PUT http://localhost:8080/api/pedidos/1

Body:
{
  "status": "FINALIZADO"
}

✅ Response 200: Pedido finalizado
⚠️  IMPORTANTE: Ao finalizar, cria SAIDA em estoque!
```

#### DELETE
```
DELETE http://localhost:8080/api/pedidos/1

✅ Response 204: Deletado
```

---

### ============ ESTOQUE ============

#### CREATE (POST) - MANUAL
```
POST http://localhost:8080/api/estoque

Body:
{
  "produtoId": 1,
  "tipoMovimento": "ENTRADA",
  "quantidade": 10
}

✅ Response 201: Movimentação criada
```

#### READ (GET ALL)
```
GET http://localhost:8080/api/estoque

✅ Response 200: Todas as movimentações
```

#### READ (GET BY ID)
```
GET http://localhost:8080/api/estoque/1

✅ Response 200: Movimentação específica
```

#### FILTRAR BY TIPO
```
GET http://localhost:8080/api/estoque/filtro/tipo?tipo=SAIDA

✅ Response 200: Apenas SIDAs
```

#### FILTRAR BY DATA
```
GET http://localhost:8080/api/estoque/filtro/data?data=2025-12-16

✅ Response 200: Movimentações daquele dia
```

#### FILTRAR BY PRODUTO
```
GET http://localhost:8080/api/estoque/filtro/produto?produtoId=1

✅ Response 200: Todas as movimentações do produto
```

#### RESUMO ESTOQUE
```
GET http://localhost:8080/api/estoque/resumo

✅ Response 200:
{
  "totalEntradas": 50,
  "totalSaidas": 30,
  "saldoTotal": 20,
  "totalMovimentacoes": 80
}
```

#### UPDATE (PUT)
```
PUT http://localhost:8080/api/estoque/1

Body:
{
  "tipoMovimento": "ENTRADA",
  "quantidade": 15
}

✅ Response 200: Atualizado
```

#### DELETE
```
DELETE http://localhost:8080/api/estoque/1

✅ Response 204: Deletado
```

---

### ============ CAIXA ============

#### CREATE (POST)
```
POST http://localhost:8080/api/caixa

Body:
{
  "descricao": "Venda do dia",
  "tipo": "CREDITO",
  "valor": 150.00
}

✅ Response 201: Registro criado
```

#### READ (GET ALL)
```
GET http://localhost:8080/api/caixa

✅ Response 200: Todos registros
```

#### READ (GET BY ID)
```
GET http://localhost:8080/api/caixa/1

✅ Response 200: Registro específico
```

#### UPDATE (PUT)
```
PUT http://localhost:8080/api/caixa/1

Body:
{
  "descricao": "Venda do dia - atualizado",
  "tipo": "CREDITO",
  "valor": 200.00
}

✅ Response 200: Atualizado
```

#### DELETE
```
DELETE http://localhost:8080/api/caixa/1

✅ Response 204: Deletado
```

---

### ============ RELATÓRIOS ============

#### VENDAS POR DIA
```
GET http://localhost:8080/api/relatorios/vendas-por-dia?data=2025-12-16

✅ Response 200:
{
  "data": "2025-12-16",
  "totalVendas": 250.00,
  "quantidadePedidos": 5,
  "pedidos": [...]
}
```

#### PRODUTOS MAIS VENDIDOS
```
GET http://localhost:8080/api/relatorios/produtos-mais-vendidos?periodo=7

✅ Response 200:
[
  {
    "produtoId": 1,
    "nome": "Sorvete Chocolate",
    "quantidade": 50,
    "preco": 12.00
  }
]
```

#### TOTAL PEDIDOS
```
GET http://localhost:8080/api/relatorios/total-pedidos?dias=7

✅ Response 200:
{
  "periodo": "7 dias",
  "totalPedidos": 25,
  "pedidosFinalizados": 20,
  "pedidosPendentes": 5,
  "receitaTotal": 5000.00
}
```

---

## ✅ CHECKLIST TESTE COMPLETO

```
PRODUTOS:
☐ POST create (201)
☐ GET all (200)
☐ GET by ID (200)
☐ PUT update (200)
☐ DELETE (204)

CLIENTES:
☐ POST create (201)
☐ GET all (200)
☐ GET by ID (200)
☐ PUT update (200)
☐ DELETE (204)

PEDIDOS:
☐ POST create (201)
☐ GET all (200)
☐ GET by ID (200)
☐ PUT finalize (200)
☐ DELETE (204)

ESTOQUE:
☐ POST create (201)
☐ GET all (200)
☐ GET by ID (200)
☐ GET filtro/tipo (200)
☐ GET filtro/data (200)
☐ GET filtro/produto (200)
☐ GET resumo (200)
☐ PUT update (200)
☐ DELETE (204)

CAIXA:
☐ POST create (201)
☐ GET all (200)
☐ GET by ID (200)
☐ PUT update (200)
☐ DELETE (204)

RELATÓRIOS:
☐ GET vendas-por-dia (200)
☐ GET produtos-mais-vendidos (200)
☐ GET total-pedidos (200)

INTERFACE:
☐ Produtos cadastram e deletam
☐ Catálogo mostra imagens
☐ Carrinho funciona
☐ Pedidos finalizam
☐ Estoque registra movimentações
☐ Relatórios mostram vendas
```

Se todos ☐ = **✅ SISTEMA PRONTO**
