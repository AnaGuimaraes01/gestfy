# 💰 ENTENDENDO O FLUXO DO CAIXA - VISÃO COMPLETA

## 📋 OS 13 CAMPOS DA TABELA CAIXA

```
┌─ id                    → PK, auto-incrementado (1, 2, 3...)
├─ tipo                  → ABERTURA | ENTRADA | FECHAMENTO
├─ valor_inicial         → Valor com que começou (sempre 0 ou X)
├─ valor_final           → 🆕 Valor final do caixa (preenchido só no fechamento)
├─ saldo                 → Total acumulado (vendas + valor_inicial)
├─ descricao             → Texto descritivo
├─ data                  → DATA do caixa (YYYY-MM-DD)
├─ data_abertura         → 🆕 TIMESTAMP quando abriu (YYYY-MM-DD HH:MM:SS)
├─ data_fechamento       → 🆕 TIMESTAMP quando fechou (YYYY-MM-DD HH:MM:SS)
├─ horario_abertura      → TIMESTAMP (por compatibilidade)
├─ horario_fechamento    → TIMESTAMP (por compatibilidade)
├─ status                → ABERTO | FECHADO
└─ observacoes           → Anotações extras
```

---

## 🔄 FLUXO DIÁRIO (exemplo real)

### ⏰ **08:00 - ABERTURA**
```
POST /api/caixa/abrir
↓
INSERT INTO caixa VALUES (
  id: 1,
  tipo: 'ABERTURA',                    ← Marca que é abertura
  valor_inicial: 0.0,                  ← Começa com zero
  valor_final: NULL,                   ← Ainda não tem valor final
  saldo: 0.0,                          ← Sem vendas ainda
  descricao: 'Caixa aberto - Início do dia',
  data: 2026-05-05,                    ← Data de hoje
  data_abertura: 2026-05-05 08:30:22,  ← Timestamp preciso de quando abriu
  data_fechamento: NULL,               ← Ainda não fechou
  horario_abertura: 2026-05-05 08:30:22,
  horario_fechamento: NULL,
  status: 'ABERTO',                    ← Estado do caixa
  observacoes: 'Sistema automático'
)
```

### 🛍️ **09:15 - PRIMEIRA VENDA**
```
POST /api/caixa/vender
{ produtoId: 1, quantidade: 2, valorRecebido: 50.00 }
↓
Produto: Sorvete Morango (preço: 15.00)
Total: 15.00 × 2 = 30.00
Troco: 50.00 - 30.00 = 20.00 ✅
↓
INSERT INTO caixa VALUES (
  id: 2,                               ← Novo registro para esta venda
  tipo: 'ENTRADA',                     ← É uma venda
  valor_inicial: 0.0,                  ← Não tem valor inicial em venda
  valor_final: NULL,
  saldo: 30.00,                        ← Valor desta venda
  descricao: 'Venda: Sorvete Morango (Qtd: 2)',
  data: 2026-05-05,
  data_abertura: 2026-05-05 09:15:45,  ← Quando registrou a venda
  data_fechamento: NULL,
  status: 'ABERTO',
  observacoes: 'Preço: 15.00 | Pago: 50.00 | Troco: 20.00'
)
↓
Estoque Sorvete: 10 → 8 (atualizado)
```

### 🛍️ **10:30 - SEGUNDA VENDA**
```
POST /api/caixa/vender
{ produtoId: 2, quantidade: 1, valorRecebido: 25.00 }
↓
Produto: Picolé (preço: 12.00)
Total: 12.00 × 1 = 12.00
Troco: 25.00 - 12.00 = 13.00 ✅
↓
INSERT INTO caixa (id: 3, tipo: 'ENTRADA', saldo: 12.00, ...)
```

### 🛍️ **11:45 - TERCEIRA VENDA**
```
POST /api/caixa/vender
{ produtoId: 1, quantidade: 1, valorRecebido: 20.00 }
↓
Total: 15.00 × 1 = 15.00
Troco: 20.00 - 15.00 = 5.00 ✅
↓
INSERT INTO caixa (id: 4, tipo: 'ENTRADA', saldo: 15.00, ...)
```

### ✅ **18:00 - FECHAMENTO**
```
POST /api/caixa/fechar
↓
SELECT * FROM caixa WHERE data = 2026-05-05 AND tipo = 'ENTRADA'
Resultado:
  - Venda 1: 30.00
  - Venda 2: 12.00
  - Venda 3: 15.00
  ├─ TOTAL: 57.00 ✅
↓
INSERT INTO caixa VALUES (
  id: 5,
  tipo: 'FECHAMENTO',                  ← Marca que é fechamento
  valor_inicial: 0.0,
  valor_final: 57.00,                  ← 🆕 AGORA TEM valor_final!
  saldo: 57.00,                        ← Total arrecadado
  descricao: 'Fechamento de caixa do dia',
  data: 2026-05-05,
  data_abertura: NULL,
  data_fechamento: 2026-05-05 18:00:15,← 🆕 Timestamp do fechamento
  status: 'FECHADO',                   ← Caixa fechado
  observacoes: 'Total vendas: 3 | Total: R$ 57.00'
)
↓
UPDATE caixa SET
  status = 'FECHADO',
  data_fechamento = 2026-05-05 18:00:15,
  valor_final = 57.00
WHERE id = 1 AND tipo = 'ABERTURA'
```

---

## 🎯 QUERY PARA ENTENDER OS DADOS

### Ver todo o movimento do dia
```sql
SELECT 
  id,
  tipo,
  saldo,
  data_abertura,
  status,
  descricao
FROM caixa
WHERE data = 2026-05-05
ORDER BY id;
```

**Resultado:**
```
id | tipo       | saldo  | data_abertura           | status   | descricao
---|------------|--------|-------------------------|----------|-------------------
1  | ABERTURA   | 0.00   | 2026-05-05 08:30:22    | FECHADO  | Caixa aberto...
2  | ENTRADA    | 30.00  | 2026-05-05 09:15:45    | ABERTO   | Venda: Sorvete...
3  | ENTRADA    | 12.00  | 2026-05-05 10:30:10    | ABERTO   | Venda: Picolé...
4  | ENTRADA    | 15.00  | 2026-05-05 11:45:33    | ABERTO   | Venda: Sorvete...
5  | FECHAMENTO | 57.00  | 2026-05-05 18:00:15    | FECHADO  | Fechamento...
```

### Calcular total do dia
```sql
SELECT 
  SUM(saldo) as total_vendas
FROM caixa
WHERE data = 2026-05-05 AND tipo = 'ENTRADA';
```

**Resultado:** `57.00`

### Ver quando abriu e fechou
```sql
SELECT 
  (SELECT data_abertura FROM caixa WHERE data = 2026-05-05 AND tipo = 'ABERTURA' LIMIT 1) as hora_abertura,
  (SELECT data_fechamento FROM caixa WHERE data = 2026-05-05 AND tipo = 'FECHAMENTO' LIMIT 1) as hora_fechamento;
```

**Resultado:**
```
hora_abertura            | hora_fechamento
2026-05-05 08:30:22     | 2026-05-05 18:00:15
```

---

## 🔍 CAMPOS IMPORTANTES EXPLICADOS

### **valor_inicial** (sempre 0 para nós)
- Serve se você quisesse começar o caixa com dinheiro pré-existente
- Exemplo: "Comecei com R$ 100"
- Para nós: sempre 0.0

### **valor_final** 🆕 (NOVO!)
- Valor que o caixa fechou
- Preenchido APENAS no fechamento
- Representa: valor_inicial + todas as vendas
- Exemplo: 0 + 57.00 = 57.00

### **saldo** (contexto de uso)
- Para ABERTURA: começa em 0
- Para ENTRADA (venda): saldo = total da venda
- Para FECHAMENTO: saldo = total de todas as vendas

### **data vs data_abertura vs horario_abertura**
```
data              → DATE: 2026-05-05 (apenas data)
data_abertura     → TIMESTAMP: 2026-05-05 08:30:22 (data + hora completa)
horario_abertura  → TIMESTAMP: idem (por compatibilidade)
```

---

## 🚨 REGRAS DE NEGÓCIO GARANTIDAS

✅ **Só um caixa ABERTO por dia**
```java
Optional<Caixa> caixaAberto = findByDataAndStatus(hoje, "ABERTO");
if (caixaAberto.isPresent()) return "erro: já está aberto";
```

✅ **Só um FECHAMENTO por dia**
```java
Optional<Caixa> jaFechado = findByDataAndTipoAndStatus(hoje, "FECHAMENTO", "FECHADO");
if (jaFechado.isPresent()) return "erro: já foi fechado";
```

✅ **Valor recebido sempre >= total**
```java
if (valorRecebido < total) return "erro: valor insuficiente";
Double troco = valorRecebido - total;
```

✅ **Estoque sempre atualizado**
```java
int novaQtd = produtoEstoque - quantidadeSolicitada;
atualizarEstoque(produto, novaQtd);
```

✅ **Timestamp sempre preenchido**
```java
@PrePersist
public void prePersist() {
    if (dataAbertura == null) dataAbertura = LocalDateTime.now();
}
```

---

## 🎓 EXEMPLO DE INTEGRAÇÃO (Frontend esperado)

```javascript
// ABRIR CAIXA
async function abrirCaixa() {
  const res = await fetch('http://localhost:8080/api/caixa/abrir', {
    method: 'POST'
  });
  const data = await res.json();
  console.log(`Caixa aberto! ID: ${data.caixaId}, Horário: ${data.horario}`);
}

// REGISTRAR VENDA
async function vender(produtoId, quantidade, valorRecebido) {
  const res = await fetch('http://localhost:8080/api/caixa/vender', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ produtoId, quantidade, valorRecebido })
  });
  const data = await res.json();
  if (data.sucesso) {
    console.log(`Troco: R$ ${data.venda.troco.toFixed(2)}`);
  } else {
    console.error(`Erro: ${data.erro}`);
  }
}

// FECHAR CAIXA
async function fecharCaixa() {
  const res = await fetch('http://localhost:8080/api/caixa/fechar', {
    method: 'POST'
  });
  const data = await res.json();
  console.log(`Total do dia: R$ ${data.totalArrecadado.toFixed(2)}`);
}
```

---

**Agora você entende EXATAMENTE como o caixa funciona!** 💡
