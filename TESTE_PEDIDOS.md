# 🧪 GUIA DE TESTES - CRUD DE PEDIDOS

## 🚀 Passo 1: Compilar e Rodar o Backend

### Terminal 1 - Compilar
```bash
cd c:\Users\Ana Carla\Desktop\gestfy\backend
mvnw.cmd clean package -DskipTests
```

### Terminal 2 - Rodar o Backend
```bash
cd c:\Users\Ana Carla\Desktop\gestfy\backend
mvnw.cmd spring-boot:run
```

Aguarde até ver:
```
Tomcat started on port(s): 8080 (http)
```

---

## 📝 Passo 2: Testar os Endpoints

### 1️⃣ CRIAR PEDIDO (POST)

**URL:** `POST http://localhost:8080/api/pedidos`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "clienteId": 1,
  "formaPagamento": "DINHEIRO",
  "itens": [
    {
      "idProduto": 1,
      "quantidade": 2
    },
    {
      "idProduto": 2,
      "quantidade": 1
    }
  ]
}
```

**Esperado:** Status 201 + PedidoDTO com total = 38.0

---

### 2️⃣ LISTAR TODOS OS PEDIDOS (GET)

**URL:** `GET http://localhost:8080/api/pedidos`

**Esperado:** Lista de PedidoDTO com todos os campos + itens

---

### 3️⃣ BUSCAR PEDIDO POR ID (GET)

**URL:** `GET http://localhost:8080/api/pedidos/1`

**Esperado:** Um PedidoDTO com detalhes completos

---

### 4️⃣ ATUALIZAR STATUS (PUT)

**URL:** `PUT http://localhost:8080/api/pedidos/1/status?status=FINALIZADO`

**Esperado:** PedidoDTO atualizado com status = FINALIZADO

---

### 5️⃣ DELETAR PEDIDO (DELETE)

**URL:** `DELETE http://localhost:8080/api/pedidos/1`

**Esperado:** Status 204 No Content

---

## 🧪 Testando com cURL (no PowerShell)

### Criar Pedido
```powershell
$body = @{
    clienteId = 1
    formaPagamento = "DINHEIRO"
    itens = @(
        @{ idProduto = 1; quantidade = 2 },
        @{ idProduto = 2; quantidade = 1 }
    )
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/pedidos" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body $body
```

### Listar Pedidos
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/pedidos" `
  -Method GET
```

### Atualizar Status
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/pedidos/1/status?status=FINALIZADO" `
  -Method PUT
```

### Deletar Pedido
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/pedidos/1" `
  -Method DELETE
```

---

## ✅ Validações Esperadas

| Campo | Validação |
|-------|-----------|
| `clienteId` | Obrigatório, deve existir no banco |
| `formaPagamento` | Obrigatório, não pode estar vazio |
| `itens` | Obrigatório, deve ter pelo menos 1 item |
| `idProduto` | Obrigatório, deve existir no banco |
| `quantidade` | Obrigatório, deve ser > 0 |
| `total` | Calculado automaticamente (NUNCA será null) |

---

## 🐛 Se Algo Der Erro

1. **Erro de Cliente não encontrado**: Verifique se existe cliente com ID 1 no banco
2. **Erro de Produto não encontrado**: Verifique se existem produtos com ID 1 e 2 no banco
3. **Total retornando null**: Atualize o banco ou delete e recrie os pedidos
4. **Validação falha**: Verifique se todos os campos obrigatórios foram enviados

