# 🧪 GUIA RÁPIDO DE TESTES - INTEGRAÇÃO PEDIDOS ONLINE

## 1️⃣ PREPARAÇÃO

### Execute os Scripts SQL
No Neon Console, execute na seguinte ordem:

```sql
-- 1º Script (copiar tudo de SQL_CAIXA_FIXES.sql)
-- ...

-- 2º Script (copiar tudo de SQL_PEDIDO_ONLINE_SETUP.sql)
-- ...
```

---

## 2️⃣ TESTE DE CRIAÇÃO DE PEDIDO

### POST /api/pedidos

**URL**: `http://localhost:8080/api/pedidos`

**Headers**:
```
Content-Type: application/json
```

**Body**:
```json
{
  "nomeCliente": "João Silva",
  "telefoneCliente": "(85) 98765-4321",
  "formaPagamento": "DINHEIRO",
  "formaRecebimento": "ENTREGA",
  "endereco": "Rua das Flores, 123 - Apto 456",
  "precisaTroco": true,
  "valorTroco": 50.00,
  "itens": [
    {
      "idProduto": 1,
      "quantidade": 2
    },
    {
      "idProduto": 3,
      "quantidade": 1
    }
  ]
}
```

**Resposta Esperada** (201 Created):
```json
{
  "id": 1,
  "nomeCliente": "João Silva",
  "telefone": "(85) 98765-4321",
  "endereco": "Rua das Flores, 123 - Apto 456",
  "formaPagamento": "DINHEIRO",
  "formaRecebimento": "ENTREGA",
  "status": "RECEBIDO",
  "total": 45.50,
  "data": "2026-06-08T22:10:30",
  "precisaTroco": true,
  "valorTroco": 50.00,
  "caixaRegistroId": null,
  "itens": [
    {
      "id": 1,
      "produtoId": 1,
      "nomeProduto": "Sorvete Chocolate",
      "precoUnitario": 15.00,
      "quantidade": 2,
      "subtotal": 30.00
    },
    {
      "id": 2,
      "produtoId": 3,
      "nomeProduto": "Açaí",
      "precoUnitario": 15.50,
      "quantidade": 1,
      "subtotal": 15.50
    }
  ]
}
```

✅ **Validações**:
- `status` = "RECEBIDO" ✅
- `precisaTroco` = true ✅
- `valorTroco` = 50.00 ✅
- `caixaRegistroId` = null (ainda não registrado) ✅
- Itens retornam com preço correto ✅

---

## 3️⃣ TESTE DE LISTAGEM

### GET /api/pedidos

**URL**: `http://localhost:8080/api/pedidos`

**Resposta**: Lista de pedidos com todos os campos

✅ **Validações**:
- Retorna `precisaTroco` para cada pedido ✅
- Retorna `valorTroco` para cada pedido ✅
- Retorna `caixaRegistroId` (null ou preenchido) ✅

---

## 4️⃣ TESTE DE LISTAGEM POR STATUS

### GET /api/pedidos/status/RECEBIDO

**URL**: `http://localhost:8080/api/pedidos/status/RECEBIDO`

**Resposta**: Lista apenas pedidos com status="RECEBIDO"

✅ **Validação**: Retorna apenas pedidos que não foram iniciados

---

## 5️⃣ TESTE CRÍTICO: ATUALIZAR PARA FINALIZADO

### PUT /api/pedidos/1/status?status=FINALIZADO

**URL**: `http://localhost:8080/api/pedidos/1/status?status=FINALIZADO`

**Método**: PUT

**Resposta Esperada** (200 OK):
```json
{
  "id": 1,
  "nomeCliente": "João Silva",
  "telefone": "(85) 98765-4321",
  "endereco": "Rua das Flores, 123 - Apto 456",
  "formaPagamento": "DINHEIRO",
  "formaRecebimento": "ENTREGA",
  "status": "FINALIZADO",
  "total": 45.50,
  "data": "2026-06-08T22:10:30",
  "precisaTroco": true,
  "valorTroco": 50.00,
  "caixaRegistroId": 999,
  "itens": [...]
}
```

✅ **Validações**:
- `status` = "FINALIZADO" ✅
- `caixaRegistroId` != null (agora tem ID!) ✅
- Console mostra: "[CAIXA] Registrando pedido no caixa..." ✅

---

## 6️⃣ TESTE: VERIFICAR REGISTRO NO CAIXA

### Verificar diretamente no banco:

```sql
SELECT id, tipo, origem, descricao, saldo, observacoes, data_abertura 
FROM caixa 
WHERE origem = 'PEDIDO_ONLINE' 
ORDER BY data_abertura DESC 
LIMIT 5;
```

✅ **Validações**:
- Existe registro com `origem` = 'PEDIDO_ONLINE' ✅
- `tipo` = 'ENTRADA' ✅
- `saldo` = valor total do pedido ✅
- `descricao` contém "Pedido Online: #1 - Cliente: João Silva" ✅
- `observacoes` contém informações do troco ✅

---

## 7️⃣ TESTE: EVITAR DUPLICIDADE

### Atualizar status para FINALIZADO novamente

```
PUT /api/pedidos/1/status?status=FINALIZADO
```

**Resposta Esperada**: Mesmo pedido com mesmo `caixaRegistroId`

**Console Log**:
```
[CAIXA] Pedido já foi registrado no caixa (ID: 999)
[CAIXA] → Ignorando duplicação
```

✅ **Validação**: NÃO cria novo registro no caixa ✅

---

## 8️⃣ TESTE: STATUS INTERMEDIÁRIOS (sem caixa)

### Atualizar para EM_PREPARACAO

```
PUT /api/pedidos/{id}/status?status=EM_PREPARACAO
```

**Verificar no banco**: Não deve criar novo registro em caixa

```sql
SELECT COUNT(*) FROM caixa 
WHERE data = TODAY 
AND origem = 'PEDIDO_ONLINE' 
AND pedido_id = {id};
```

✅ **Validação**: Conta não aumenta ✅

---

## 9️⃣ TESTE: CANCELAMENTO

### Criar novo pedido e cancelar

```
PUT /api/pedidos/2/status?status=CANCELADO
```

**Verificar no banco**: Não deve registrar no caixa

```sql
SELECT * FROM caixa 
WHERE origem = 'PEDIDO_ONLINE' 
AND pedido_id = 2;
```

✅ **Validação**: Retorna 0 resultados ✅

---

## 🔟 TESTE: COMPATIBILIDADE COM VENDAS PRESENCIAIS

### Fazer uma venda presencial normalmente

```
POST /api/caixa/vender
```

**Verificar no banco**:
```sql
SELECT id, tipo, origem, descricao, saldo, data_abertura 
FROM caixa 
WHERE data = TODAY 
ORDER BY data_abertura DESC;
```

✅ **Validações**:
- Vendas presenciais têm `origem` = 'CAIXA' ✅
- Pedidos online têm `origem` = 'PEDIDO_ONLINE' ✅
- Ambas funcionam lado a lado ✅

---

## 🔍 TROUBLESHOOTING

### Problema: caixaRegistroId sempre null

**Solução**:
1. Verificar se `UPDATE pedido SET caixa_registro_id = X` está sendo executado
2. Verificar logs: "[registrarNoCaixa] Rastreamento guardado no pedido"
3. Se não aparecer, pode ser erro na transação

### Problema: Duplicidade de lançamento

**Solução**:
1. Antes de atualizar, verificar: `caixa_registro_id IS NULL`
2. Após registrar, verificar: `caixa_registro_id IS NOT NULL`
3. Se problema persiste, pode ser concorrência - adicione `@Transactional(isolation = Isolation.SERIALIZABLE)`

### Problema: Campo origem sempre NULL

**Solução**:
1. Executar SQL_CAIXA_FIXES.sql novamente
2. Verificar se coluna foi realmente adicionada:
   ```sql
   SELECT column_name FROM information_schema.columns 
   WHERE table_name = 'caixa' 
   AND column_name = 'origem';
   ```
3. Se não retornar, coluna não existe - executar ALTER TABLE

---

## 📊 RESUMO DO QUE VALIDAR

| Teste | Esperado | Status |
|-------|----------|--------|
| Criar pedido | status=RECEBIDO, caixaRegistroId=null | [ ] |
| Listar | Retorna precisaTroco, valorTroco | [ ] |
| Listar por status | Filtra corretamente | [ ] |
| Atualizar FINALIZADO | caixaRegistroId preenchido | [ ] |
| Registro em caixa | origem='PEDIDO_ONLINE' | [ ] |
| Não duplica | Mesma ID ao atualizar novamente | [ ] |
| EM_PREPARACAO | Sem registro em caixa | [ ] |
| CANCELADO | Sem registro em caixa | [ ] |
| Vendas presenciais | origem='CAIXA' | [ ] |

---

## 🎯 PASSO A PASSO COMPLETO (5 MINUTOS)

1. **Execute scripts SQL no Neon** (1 min)
2. **Teste criar pedido** (1 min)
3. **Teste atualizar FINALIZADO** (1 min)
4. **Verificar caixa no banco** (1 min)
5. **Teste venda presencial** (1 min)

**Total**: ~5 minutos para validar tudo

---

**Tudo funcionando?** ✅ Pronto para deploy!
