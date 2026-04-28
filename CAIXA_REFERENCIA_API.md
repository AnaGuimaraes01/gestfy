# 📚 REFERÊNCIA RÁPIDA - API CAIXA

## 🎯 Base URL

```
Produção:    https://gestfy-backend.onrender.com/api/caixa
Desenvolvimento: http://localhost:8080/api/caixa
```

---

## 1️⃣ ABRIR CAIXA

```http
POST /caixa/abrir

Response 201 Created:
{
  "sucesso": true,
  "mensagem": "Caixa aberto com sucesso!",
  "caixaId": 1,
  "data": "2025-01-11",
  "horario": "2025-01-11T14:25:00"
}

Response 409 Conflict:
{
  "erro": "Caixa já está aberto para hoje",
  "caixaId": 1
}
```

---

## 2️⃣ BUSCAR PRODUTO

```http
GET /caixa/buscar-produto?nome=sorvete

Query Parameters:
  nome (required) - Texto para buscar (case-insensitive, parcial)

Response 200 OK:
{
  "sucesso": true,
  "total": 2,
  "produtos": [
    {
      "id": 1,
      "nome": "Sorvete de Chocolate",
      "preco": 15.00,
      "estoque": 10
    },
    {
      "id": 2,
      "nome": "Sorvete de Morango",
      "preco": 14.00,
      "estoque": 8
    }
  ]
}

Response 404 Not Found:
{
  "erro": "Nenhum produto encontrado com o nome: xyz",
  "produtos": []
}

Response 400 Bad Request:
{
  "erro": "Por favor, informe o nome do produto para buscar"
}
```

---

## 3️⃣ REGISTRAR VENDA

```http
POST /caixa/vender
Content-Type: application/json

Request Body:
{
  "produtoId": 1,
  "quantidade": 2,
  "valorRecebido": 50.00
}

Response 201 Created:
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

Response 404 Not Found:
{
  "erro": "Produto não encontrado com ID: 999"
}

Response 400 Bad Request:
{
  "erro": "Estoque insuficiente",
  "produtoNome": "Sorvete",
  "estoqueDisponivel": 3,
  "quantidadeSolicitada": 5
}

Response 400 Bad Request:
{
  "erro": "Valor recebido é insuficiente",
  "valorTotal": 30.00,
  "valorRecebido": 25.00,
  "falta": 5.00
}
```

---

## 4️⃣ LISTAR VENDAS DO DIA

```http
GET /caixa/vendas-do-dia

Response 200 OK:
{
  "sucesso": true,
  "data": "2025-01-11",
  "totalVendas": 5,
  "totalArrecadado": 150.00,
  "vendas": [
    {
      "id": 1,
      "tipo": "ENTRADA",
      "saldo": 30.00,
      "descricao": "Venda: Sorvete (Qtd: 2)",
      "data": "2025-01-11",
      "horarioAbertura": "2025-01-11T14:30:00",
      "status": "ABERTO",
      "observacoes": "Preço: 15.00 | Pago: 50.00 | Troco: 20.00"
    },
    ...
  ]
}
```

---

## 5️⃣ OBTER STATUS DO CAIXA

```http
GET /caixa/status

Response 200 OK (ABERTO):
{
  "aberto": true,
  "caixaId": 1,
  "horarioAbertura": "2025-01-11T14:25:00",
  "totalVendas": 5,
  "totalArrecadado": 150.00,
  "data": "2025-01-11"
}

Response 200 OK (FECHADO):
{
  "aberto": false,
  "mensagem": "Caixa fechado para hoje. Abra o caixa para começar.",
  "data": "2025-01-11"
}
```

---

## 6️⃣ FECHAR CAIXA

```http
POST /caixa/fechar

Response 200 OK:
{
  "sucesso": true,
  "mensagem": "Caixa fechado com sucesso!",
  "totalVendas": 5,
  "totalArrecadado": 150.00,
  "data": "2025-01-11",
  "horarioFechamento": "2025-01-11T22:00:00"
}

Response 404 Not Found:
{
  "erro": "Nenhum caixa aberto para fechar hoje"
}

Response 409 Conflict:
{
  "erro": "Caixa já foi fechado hoje"
}
```

---

## 🧪 EXEMPLOS COM CURL

### Abrir Caixa
```bash
curl -X POST http://localhost:8080/api/caixa/abrir \
  -H "Content-Type: application/json"
```

### Buscar Produto
```bash
curl "http://localhost:8080/api/caixa/buscar-produto?nome=sorvete"
```

### Registrar Venda
```bash
curl -X POST http://localhost:8080/api/caixa/vender \
  -H "Content-Type: application/json" \
  -d '{
    "produtoId": 1,
    "quantidade": 2,
    "valorRecebido": 50.00
  }'
```

### Listar Vendas do Dia
```bash
curl http://localhost:8080/api/caixa/vendas-do-dia
```

### Obter Status
```bash
curl http://localhost:8080/api/caixa/status
```

### Fechar Caixa
```bash
curl -X POST http://localhost:8080/api/caixa/fechar \
  -H "Content-Type: application/json"
```

---

## 📋 VALIDAÇÕES

### POST /caixa/vender - Validações
| Campo | Tipo | Obrigatório | Restrições |
|-------|------|-------------|-----------|
| produtoId | Long | ✅ | Deve existir no banco |
| quantidade | Integer | ✅ | > 0 |
| valorRecebido | Double | ✅ | >= valorTotal |

### GET /caixa/buscar-produto - Validações
| Parâmetro | Tipo | Obrigatório | Restrições |
|-----------|------|-------------|-----------|
| nome | String | ✅ | Não pode estar vazio |

---

## 🔄 CÓDIGOS HTTP

| Código | Significado | Quando |
|--------|-------------|--------|
| 200 | OK | Busca bem-sucedida |
| 201 | Created | Venda ou caixa criado |
| 400 | Bad Request | Validação falhou |
| 404 | Not Found | Recurso não encontrado |
| 409 | Conflict | Conflito (ex: dois caixas) |

---

## 💾 DTO MODELS

### ProdutoBuscaResponse
```java
class ProdutoBuscaResponse {
  Long id;
  String nome;
  Double preco;
  Integer estoque;
}
```

### VendaRequest
```java
record VendaRequest(
  Long produtoId,
  Integer quantidade,
  Double valorRecebido
)
```

### VendaResponse
```java
class VendaResponse {
  Long vendaId;
  String nomeProduct;
  Integer quantidade;
  Double precoUnitario;
  Double valorTotal;
  Double valorRecebido;
  Double troco;
  String mensagem;
}
```

---

## 🚨 CÓDIGOS DE ERRO COMUNS

### 400 Bad Request
```json
{
  "erro": "Estoque insuficiente"
}
```

### 400 Bad Request
```json
{
  "erro": "Valor recebido é insuficiente",
  "falta": 5.00
}
```

### 404 Not Found
```json
{
  "erro": "Produto não encontrado com ID: 999"
}
```

### 404 Not Found
```json
{
  "erro": "Nenhum produto encontrado com o nome: xyz"
}
```

### 409 Conflict
```json
{
  "erro": "Caixa já está aberto para hoje"
}
```

---

## 📊 CAMPOS EM RESPOSTA

### Venda Completa (Caixa)
```json
{
  "id": 5,
  "tipo": "ENTRADA",
  "saldo": 30.00,
  "descricao": "Venda: Sorvete (Qtd: 2)",
  "data": "2025-01-11",
  "horarioAbertura": "2025-01-11T14:30:00",
  "horarioFechamento": null,
  "status": "ABERTO",
  "observacoes": "Preço: 15.00 | Pago: 50.00 | Troco: 20.00"
}
```

---

## 🔐 HEADERS

### Request
```
Content-Type: application/json
```

### Response
```
Content-Type: application/json
```

---

## ⏱️ TIMEOUTS

- Abertura de caixa: ~500ms
- Busca de produtos: ~200ms
- Registro de venda: ~300ms
- Fechamento de caixa: ~400ms

---

## 📱 JAVASCRIPT - EXEMPLO DE USO

### Abrir Caixa
```javascript
const response = await fetch('http://localhost:8080/api/caixa/abrir', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' }
});
const data = await response.json();
console.log(data.caixaId);
```

### Buscar Produto
```javascript
const response = await fetch('http://localhost:8080/api/caixa/buscar-produto?nome=sorvete');
const data = await response.json();
data.produtos.forEach(p => console.log(p.nome, p.preco));
```

### Registrar Venda
```javascript
const response = await fetch('http://localhost:8080/api/caixa/vender', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    produtoId: 1,
    quantidade: 2,
    valorRecebido: 50.00
  })
});
const data = await response.json();
console.log(`Troco: R$ ${data.venda.troco}`);
```

---

## 🎯 FLUXO RECOMENDADO

```
1. GET  /status          → Verifica se caixa está aberto
2. POST /abrir           → Abre se estiver fechado
3. GET  /buscar-produto  → Busca produtos
4. POST /vender          → Registra venda (repetir)
5. GET  /vendas-do-dia   → Ver histórico
6. POST /fechar          → Fecha no final do dia
```

---

## 📞 SUPORTE

Qualquer erro não documentado:
1. Verifique os logs (F12 > Console)
2. Verifique se backend está rodando
3. Verifique URL da API
4. Verifique banco de dados

---

**Última atualização:** 11 de Janeiro de 2025  
**Versão:** 1.0.0  
**Pronto para usar:** ✅ SIM
