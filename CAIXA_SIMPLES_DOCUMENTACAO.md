# CAIXA SIMPLES - Documentação Completa

## 📋 Visão Geral

O **Caixa Simples** é um sistema de vendas **direto e funcional** para lanchonetes, sorvetarias e pequenos negócios de alimentos. Focado **apenas em pagamento em dinheiro**, sem complicações.

### Características Principais:
✅ **Simples e rápido** - Interface limpa e intuitiva  
✅ **Busca de produtos** - Digite o nome e encontre (busca parcial)  
✅ **Cálculo automático** - Valor total e troco são calculados automaticamente  
✅ **Validação de estoque** - Não vende sem estoque  
✅ **Histórico de vendas** - Veja todas as vendas do dia  
✅ **Dinheiro apenas** - Sem cartão, PIX ou outras formas

---

## 🎯 Fluxo de Funcionamento

```
1. ABRIR CAIXA
   ↓
2. BUSCAR PRODUTO (por nome)
   ↓
3. SELECIONAR PRODUTO DA LISTA
   ↓
4. INFORMAR QUANTIDADE
   ↓
5. INFORMAR VALOR RECEBIDO
   ↓
6. SISTEMA CALCULA TROCO AUTOMATICAMENTE
   ↓
7. CONFIRMAR VENDA
   ↓
8. VENDA REGISTRADA (estoque atualizado)
   ↓
9. REPETIR (volta ao passo 2)
   ↓
10. FECHAR CAIXA (final do dia)
```

---

## 🚀 Como Usar

### 1️⃣ ABRINDO O CAIXA

```
1. Acesse: https://seu-dominio/admin/caixa-novo.html
2. Clique em "✓ ABRIR CAIXA"
3. Pronto! Caixa aberto para o dia
```

**Status visual:**
- 🟢 **ABERTO** = Caixa está ativo para vendas
- 🔴 **FECHADO** = Nenhuma venda pode ser feita

---

### 2️⃣ REGISTRANDO UMA VENDA

#### Passo 1: Buscar Produto
```
Campo: "Buscar Produto"
Exemplo: Digite "sorvete", "lanche", "refrigerante"
Resultado: Lista com todos os produtos encontrados
```

A **busca é parcial e case-insensitive**:
- ✅ "sorv" encontra "Sorvete"
- ✅ "refri" encontra "Refrigerante"
- ✅ "lanch" encontra "Lanche"

#### Passo 2: Selecionar Produto
```
Clique no produto na lista
O sistema preenche automaticamente:
- ID do produto
- Preço unitário
- Campo de quantidade
```

#### Passo 3: Informar Quantidade
```
Campo: "Quantidade"
Exemplo: 2 (dois sorvetes)
```

#### Passo 4: Informar Valor Recebido
```
Campo: "Valor Recebido"
Exemplo: 50.00 (cliente deu uma nota de 50)
```

#### Passo 5: Ver Resumo da Venda
```
O sistema AUTOMATICAMENTE calcula:
✓ Valor total (preço × quantidade)
✓ Valor recebido
✓ TROCO 💰
```

**Exemplo:**
```
Produto: Sorvete de Chocolate
Preço unitário: R$ 15.00
Quantidade: 2
Valor total: R$ 30.00
Valor recebido: R$ 50.00
TROCO: R$ 20.00 ✓
```

#### Passo 6: Confirmar Venda
```
Clique em "✓ CONFIRMAR VENDA"
```

**O sistema então:**
1. Valida o estoque
2. Validapagamento (se valor é suficiente)
3. Registra a venda no banco de dados
4. **ATUALIZA o estoque automaticamente** (-2 unidades)
5. Registra o movimento de estoque (SAÍDA)
6. Mostra o troco em destaque

---

### 3️⃣ HISTÓRICO DE VENDAS

Você vê em tempo real:
- ✅ Total de vendas do dia
- ✅ Total arrecadado
- ✅ Lista de todas as vendas com horário

Cada venda mostra:
```
Venda: Produto X (Quantidade: Y)
14:30 - Preço: R$ 15.00 | Pago: R$ 50.00 | Troco: R$ 35.00
```

---

### 4️⃣ FECHANDO O CAIXA

```
No final do dia:
1. Clique em "✕ FECHAR CAIXA"
2. Confirme a ação
3. Sistema calcula:
   - Total de vendas (quantidade)
   - Total arrecadado (valor em R$)
4. Caixa fica indisponível
```

**Confirmação:**
```
✓ CAIXA FECHADO!
📊 Total de vendas: R$ 500.00
📈 Quantidade: 25 vendas
```

---

## ⚠️ TRATAMENTO DE ERROS

### Erro: "Estoque insuficiente"
```
❌ Estoque insuficiente
- Produto: Sorvete
- Disponível: 3 unidades
- Solicitado: 5 unidades

SOLUÇÃO: Digite uma quantidade menor ou compre mais estoque
```

### Erro: "Valor recebido é insuficiente"
```
❌ Valor recebido é insuficiente
- Valor total: R$ 30.00
- Valor recebido: R$ 25.00
- Falta: R$ 5.00

SOLUÇÃO: Peça mais R$ 5.00 ao cliente
```

### Erro: "Produto não encontrado"
```
❌ Nenhum produto encontrado com o nome: "xis"

SOLUÇÃO: 
- Verifique a grafia
- Tente outra variação do nome
- Deixe em branco para começar nova busca
```

### Erro: "Caixa não está aberto"
```
❌ Nenhum caixa aberto para fechar hoje

SOLUÇÃO: Clique em "✓ ABRIR CAIXA" primeiro
```

---

## 📊 DADOS ARMAZENADOS

Cada venda registra:

### Na Tabela `caixa`:
```
- tipo: "ENTRADA" (tipo de movimento)
- saldo: 30.00 (valor total da venda)
- descricao: "Venda: Sorvete (Qtd: 2)"
- data: 2025-01-11 (data do caixa)
- horarioAbertura: 2025-01-11 14:30:00 (hora da venda)
- status: "ABERTO"
- observacoes: "Preço unitário: R$ 15.00 | Pago: R$ 50.00 | Troco: R$ 35.00"
```

### Na Tabela `produto`:
```
- quantidade: 8 (ATUALIZADA AUTOMATICAMENTE)
Exemplo: tinha 10, vendeu 2, fica com 8
```

### Na Tabela `estoque`:
```
- tipoMovimento: "SAIDA"
- produtoId: 1
- dataMovimento: 2025-01-11 14:30:00
- quantidade: 2
```

---

## 🔧 ENDPOINTS DA API

### 1. Abrir Caixa
```
POST /api/caixa/abrir

Response:
{
  "sucesso": true,
  "mensagem": "Caixa aberto com sucesso!",
  "caixaId": 1,
  "data": "2025-01-11",
  "horario": "2025-01-11T14:25:00"
}
```

### 2. Buscar Produto
```
GET /api/caixa/buscar-produto?nome=sorvete

Response:
{
  "sucesso": true,
  "total": 3,
  "produtos": [
    {
      "id": 1,
      "nome": "Sorvete de Chocolate",
      "preco": 15.00,
      "estoque": 10
    },
    ...
  ]
}
```

### 3. Registrar Venda
```
POST /api/caixa/vender

Request:
{
  "produtoId": 1,
  "quantidade": 2,
  "valorRecebido": 50.00
}

Response:
{
  "sucesso": true,
  "venda": {
    "vendaId": 5,
    "nomeProduct": "Sorvete de Chocolate",
    "quantidade": 2,
    "precoUnitario": 15.00,
    "valorTotal": 30.00,
    "valorRecebido": 50.00,
    "troco": 20.00,
    "mensagem": "Venda registrada com sucesso!"
  },
  "estoqueAtualizado": 8
}
```

### 4. Fechar Caixa
```
POST /api/caixa/fechar

Response:
{
  "sucesso": true,
  "mensagem": "Caixa fechado com sucesso!",
  "totalVendas": 25,
  "totalArrecadado": 500.00,
  "data": "2025-01-11",
  "horarioFechamento": "2025-01-11T22:00:00"
}
```

### 5. Listar Vendas do Dia
```
GET /api/caixa/vendas-do-dia

Response:
{
  "sucesso": true,
  "data": "2025-01-11",
  "totalVendas": 25,
  "totalArrecadado": 500.00,
  "vendas": [
    {
      "id": 1,
      "descricao": "Venda: Sorvete (Qtd: 2)",
      "saldo": 30.00,
      ...
    },
    ...
  ]
}
```

### 6. Obter Status do Caixa
```
GET /api/caixa/status

Response (ABERTO):
{
  "aberto": true,
  "caixaId": 1,
  "horarioAbertura": "2025-01-11T14:25:00",
  "totalVendas": 5,
  "totalArrecadado": 150.00,
  "data": "2025-01-11"
}

Response (FECHADO):
{
  "aberto": false,
  "mensagem": "Caixa fechado para hoje. Abra o caixa para começar.",
  "data": "2025-01-11"
}
```

---

## 📁 ARQUIVOS DO PROJETO

### Backend (Java/Spring Boot)
```
backend/src/main/java/com/empresa/gestfy/
├── controllers/
│   └── CaixaController.java          ← Todos os endpoints do caixa
├── repositories/
│   ├── ProdutoRepository.java        ← findByNomeContainingIgnoreCase()
│   ├── CaixaRepository.java
│   └── EstoqueRepository.java
├── models/
│   ├── Caixa.java
│   ├── Produto.java
│   └── Estoque.java
└── dto/caixa/
    ├── ProdutoBuscaResponse.java     ← Response da busca
    ├── VendaRequest.java            ← Request de venda
    └── VendaResponse.java           ← Response de venda confirmada
```

### Frontend (HTML/JS)
```
frontend/admin/
├── caixa-novo.html                  ← Interface do caixa
├── js/
│   └── caixa-novo.js                ← Lógica e chamadas à API
└── css/
    └── style.css                    ← Estilos
```

---

## 🔄 FLUXO TÉCNICO COMPLETO

```
USUÁRIO                  FRONTEND                    BACKEND
  |                        |                           |
  | Clica em Abrir Caixa   |                           |
  |---------------------->|                           |
  |                        | POST /caixa/abrir         |
  |                        |-------------------------->|
  |                        |                    Valida se já existe
  |                        |                    Cria novo registro
  |                        |<--------------------------|
  |                   Sucesso!                        |
  |<----------------------|                           |
  | Interface atualizada  |                           |
  |                        |                           |
  | Digita nome do produto|                           |
  |---------------------->|                           |
  |                        | GET /caixa/buscar-produto |
  |                        |-------------------------->|
  |                        |                   Query no banco
  |                        |<--------------------------|
  |      Lista de produtos |                          |
  |<----------------------|                           |
  | Seleciona produto     |                           |
  |---------------------->| (JavaScript local)        |
  | Formulário preenchido |                           |
  |<----------------------|                           |
  | Digita quantidade     |                           |
  | e valor recebido      |                           |
  |                        |                           |
  | Clica Confirmar Venda |                           |
  |---------------------->|                           |
  |                        | POST /caixa/vender        |
  |                        |-------------------------->|
  |                        |                    Valida estoque
  |                        |                    Valida pagamento
  |                        |                    Atualiza produto
  |                        |                    Registra movimento
  |                        |                    Registra venda
  |                        |<--------------------------|
  |        VENDA CONFIRMADA!|                        |
  |        Troco: R$ 20.00 |                        |
  |<----------------------|                          |
  | Histórico atualizado  |                          |
  |                        |                          |
```

---

## 🎨 INTERFACE

### Layout Principal
```
┌────────────────────────────────────────┐
│      🏪 CAIXA SIMPLES                  │
│   Sistema de vendas em dinheiro        │
├────────────────────────────────────────┤
│ Status: ABERTO ✓  | Total: R$ 500.00  │
├────────────────────────────────────────┤
│  ✓ ABRIR CAIXA  |  ✕ FECHAR CAIXA    │
├────────────────────────────────────────┤
│  Buscar Produto                        │
│  [Digite aqui...] [🔍 Buscar]         │
│  ┌─ Sorvete de Chocolate - R$ 15.00 ─┐│
│  └────────────────────────────────────┘│
├────────────────────────────────────────┤
│  Registrar Venda                       │
│  ┌─ RESUMO DA VENDA ──────────────────┐│
│  │ Produto: Sorvete                  ││
│  │ Preço: R$ 15.00                   ││
│  │ Quantidade: 2                     ││
│  │ Valor Total: R$ 30.00             ││
│  │ Valor Recebido: R$ 50.00          ││
│  │ 💰 TROCO: R$ 20.00 ✓              ││
│  └───────────────────────────────────┘│
│  [✓ CONFIRMAR] [↻ LIMPAR]            │
├────────────────────────────────────────┤
│  Vendas do Dia                         │
│  Total: 25 | Arrecadado: R$ 500.00    │
│  ┌────────────────────────────────────┐│
│  │ Sorvete (Qtd: 2) - R$ 30.00 14:30 ││
│  │ Refrigerante (Qtd: 1) - R$ 8.00   ││
│  │ Lanche (Qtd: 3) - R$ 45.00 14:45  ││
│  └────────────────────────────────────┘│
└────────────────────────────────────────┘
```

---

## ✅ VALIDAÇÕES IMPLEMENTADAS

### Entrada
- ✅ Nome do produto não pode estar vazio
- ✅ Quantidade deve ser maior que zero
- ✅ Valor recebido não pode ser negativo

### Negócio
- ✅ Produto deve existir no banco
- ✅ Produto deve ter estoque suficiente
- ✅ Valor recebido deve ser >= valor total
- ✅ Caixa deve estar aberto para registrar vendas

### Banco de Dados
- ✅ Estoque é atualizado automaticamente
- ✅ Movimento de estoque é registrado
- ✅ Venda é registrada com todos os detalhes
- ✅ Apenas um caixa por dia pode estar aberto

---

## 🚨 PONTOS CRÍTICOS

⚠️ **IMPORTANTE:**

1. **Backup de dados**: O sistema persiste no banco de dados PostgreSQL
2. **Sincronização**: Múltiplos navegadores/caixas podem estar abertos simultaneamente
3. **Relatório diário**: Feche o caixa todo dia - isso registra o fechamento
4. **Estoque**: Sempre verificado antes de vender
5. **API URL**: Altere em `caixa-novo.js` para desenvolvimento local

```javascript
// PRODUÇÃO
const API_BASE = 'https://gestfy-backend.onrender.com/api';

// DESENVOLVIMENTO LOCAL
const API_BASE = 'http://localhost:8080/api';
```

---

## 📝 Exemplo Real de Uso

```
CENÁRIO: Lanchonete no sábado

14:00 - Abrir caixa
        Clico "✓ ABRIR CAIXA"
        Status: ABERTO ✓

14:05 - Primeira venda
        Busco "sorvete"
        Seleciono "Sorvete de Chocolate"
        Quantidade: 2
        Valor recebido: 50.00
        Sistema calcula: Troco R$ 20.00
        Confirmo venda
        Estoque: 10 → 8 ✓

14:15 - Segunda venda
        Busco "refri"
        Seleciono "Refrigerante"
        Quantidade: 3
        Valor recebido: 30.00
        Sistema calcula: Troco R$ 6.00
        Confirmo venda
        Estoque: 50 → 47 ✓

18:00 - Ver histórico
        Total de vendas: 45
        Total arrecadado: R$ 1.050,00

18:05 - Fechar caixa
        Clico "✕ FECHAR CAIXA"
        Confirmado!
        ✓ CAIXA FECHADO!
        📊 Total de vendas: R$ 1.050,00
        📈 Quantidade: 45 vendas
```

---

## 📞 Suporte

Qualquer dúvida sobre o sistema de caixa simples, verifique:
1. Os logs do navegador (F12)
2. O status do backend
3. A configuração da API URL
4. O banco de dados PostgreSQL

---

**Versão:** 1.0  
**Data:** 11 de Janeiro de 2025  
**Sistema:** Gestfy v1.0  
**Autor:** Desenvolvimento
