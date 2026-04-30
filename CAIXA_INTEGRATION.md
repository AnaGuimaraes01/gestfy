# 🏪 CAIXA - GESTFY

## Visão Geral
Sistema de **Caixa Simples** integrado ao Gestfy para gerenciar vendas em dinheiro de forma rápida e eficiente.

### Funcionalidades
✅ Abrir/Fechar caixa do dia  
✅ Buscar produtos por nome (busca parcial)  
✅ Registrar vendas com cálculo automático de troco  
✅ Atualizar estoque automaticamente  
✅ Histórico de vendas do dia  
✅ Totalizadores em tempo real  

---

## 📊 Arquitetura

### Backend Endpoints
| Endpoint | Método | Descrição |
|----------|--------|-----------|
| `/api/caixa/abrir` | POST | Abre o caixa do dia |
| `/api/caixa/vender` | POST | Registra uma venda |
| `/api/caixa/fechar` | POST | Fecha o caixa do dia |
| `/api/caixa/status` | GET | Retorna status atual |
| `/api/caixa/buscar-produto` | GET | Busca produto por nome |
| `/api/caixa/vendas-do-dia` | GET | Lista vendas do dia |

### Request/Response

#### Abrir Caixa
```bash
POST /api/caixa/abrir
Content-Type: application/json

# Response 201
{
  "sucesso": true,
  "mensagem": "Caixa aberto com sucesso!",
  "caixaId": 1,
  "data": "2025-01-30",
  "horario": "2025-01-30T10:30:00"
}
```

#### Registrar Venda
```bash
POST /api/caixa/vender
Content-Type: application/json

{
  "produtoId": 5,
  "quantidade": 2,
  "valorRecebido": 50.00
}

# Response 201
{
  "sucesso": true,
  "venda": {
    "vendaId": 123,
    "nomeProduct": "Sorvete de Chocolate",
    "quantidade": 2,
    "precoUnitario": 12.50,
    "valorTotal": 25.00,
    "valorRecebido": 50.00,
    "troco": 25.00,
    "mensagem": "Venda registrada com sucesso!"
  },
  "estoqueAtualizado": 8
}
```

#### Buscar Produto
```bash
GET /api/caixa/buscar-produto?nome=sorvete
Content-Type: application/json

# Response 200
{
  "sucesso": true,
  "total": 3,
  "produtos": [
    {
      "id": 1,
      "nome": "Sorvete de Chocolate",
      "preco": 12.50,
      "estoque": 10
    },
    {
      "id": 2,
      "nome": "Sorvete de Morango",
      "preco": 12.50,
      "estoque": 5
    }
  ]
}
```

#### Status do Caixa
```bash
GET /api/caixa/status
Content-Type: application/json

# Response 200
{
  "aberto": true,
  "caixaId": 1,
  "horarioAbertura": "2025-01-30T10:30:00",
  "totalVendas": 15,
  "totalArrecadado": 225.50,
  "data": "2025-01-30"
}
```

#### Listar Vendas do Dia
```bash
GET /api/caixa/vendas-do-dia
Content-Type: application/json

# Response 200
{
  "sucesso": true,
  "data": "2025-01-30",
  "totalVendas": 15,
  "totalArrecadado": 225.50,
  "vendas": [
    {
      "id": 1,
      "tipo": "ENTRADA",
      "saldo": 25.00,
      "descricao": "Venda: Sorvete de Chocolate (Qtd: 2)",
      "data": "2025-01-30",
      "horarioAbertura": "2025-01-30T10:35:00",
      "observacoes": "Preço unitário: R$ 12.50 | Valor pago: R$ 50.00 | Troco: R$ 25.00"
    }
  ]
}
```

---

## 🗄️ Database Schema

### Tabela: `caixa`
```sql
CREATE TABLE caixa (
  id BIGSERIAL PRIMARY KEY,
  tipo VARCHAR(50),              -- "ABERTURA", "ENTRADA", "FECHAMENTO"
  saldo DOUBLE PRECISION,        -- Valor total da venda ou fechamento
  descricao VARCHAR(255),        -- Descrição da operação
  data DATE,                     -- Data da operação
  horario_abertura TIMESTAMP,    -- Timestamp de abertura
  horario_fechamento TIMESTAMP,  -- Timestamp de fechamento
  status VARCHAR(50),            -- "ABERTO", "FECHADO"
  observacoes TEXT               -- Observações adicionais
);
```

---

## 🧩 Integração com Outras Funcionalidades

### Produtos
- Quando busca um produto no caixa, ele vem do banco de `PRODUTOS`
- Busca é case-insensitive e parcial: `nome LIKE '%termo%'`

### Estoque
- Cada venda registra automaticamente uma **SAÍDA** em `ESTOQUE`
- A quantidade do `PRODUTO` é decrementada automaticamente

### Pedidos
- Futura integração: vincular vendas do caixa com pedidos do sistema

---

## 🎨 Frontend Integration

### Arquivo: `frontend/admin/caixa-novo.html`
- Página HTML5 com design responsivo
- 900px max-width, grid layout
- Cores: #b0305f (rosa/roxo principal), #4caf50 (sucesso), #f44336 (erro)

### Arquivo: `frontend/admin/js/caixa-novo.js`
- ~550 linhas de código JavaScript
- Auto-detecção de ambiente (localhost vs produção)
- Validações no client-side
- UX melhorada com debounce e real-time calculations

### URLs
```javascript
// Produção (Render)
let API_BASE = 'https://gestfy-backend.onrender.com/api';

// Desenvolvimento (local)
// Auto-detecta se localhost está disponível
```

---

## 🚀 Fluxo de Uso Típico

### Passo 1: Abrir Caixa
```
Usuário clica "Abrir Caixa"
  ↓
Backend registra abertura (tipo: "ABERTURA")
  ↓
Frontend ativa seção de vendas
  ↓
Status muda para "ABERTO ✓"
```

### Passo 2: Registrar Venda
```
Usuário digita nome do produto
  ↓
Frontend busca: GET /caixa/buscar-produto?nome=X
  ↓
Backend retorna lista de produtos
  ↓
Usuário clica no produto
  ↓
Usuário insere quantidade e valor recebido
  ↓
Frontend calcula troco em tempo real
  ↓
Usuário clica "Confirmar Venda"
  ↓
Backend: valida estoque, atualiza produto, registra venda, registra movimento estoque
  ↓
Frontend exibe "VENDA CONFIRMADA! Troco: R$ X.XX"
  ↓
Histórico atualiza automaticamente
```

### Passo 3: Fechar Caixa
```
Usuário clica "Fechar Caixa"
  ↓
Confirmação: "Tem certeza? Esta ação não pode ser desfeita!"
  ↓
Backend calcula total do dia, registra fechamento
  ↓
Frontend desativa seção de vendas
  ↓
Status muda para "FECHADO ✕"
  ↓
Exibe: "Total de vendas: R$ X.XX | Quantidade: N"
```

---

## 🔍 Validações

### No Frontend
- ✅ Caixa deve estar aberto
- ✅ Produto deve estar selecionado
- ✅ Quantidade deve ser > 0
- ✅ Valor recebido deve ser ≥ valor total
- ✅ Debounce em busca (300ms)
- ✅ Auto-detect de ambiente

### No Backend
- ✅ Produto existe
- ✅ Estoque suficiente
- ✅ Valor recebido válido (>= valor total)
- ✅ Caixa já não está aberto (409 Conflict se tentar abrir 2x)
- ✅ Jakarta Validation em todos os DTOs

---

## 🧪 Testes de Integração

### Teste Local
```bash
# 1. Rodar backend
cd backend
./mvnw spring-boot:run

# 2. Abrir frontend
open frontend/admin/caixa-novo.html
# Ou: http://localhost:5500/frontend/admin/caixa-novo.html

# 3. Testar fluxo
- Clique "Abrir Caixa" → Deve abrir ✓
- Digite "sorvete" → Deve buscar e retornar produtos ✓
- Clique em produto → Deve preencher formulário ✓
- Coloque valor → Deve calcular troco ✓
- Clique "Confirmar" → Deve registrar ✓
- Clique "Fechar Caixa" → Deve fechar e exibir totalizadores ✓
```

### Teste Curl
```bash
# Abrir caixa
curl -X POST http://localhost:8080/api/caixa/abrir

# Buscar produto
curl "http://localhost:8080/api/caixa/buscar-produto?nome=sorvete"

# Status
curl http://localhost:8080/api/caixa/status

# Fechar
curl -X POST http://localhost:8080/api/caixa/fechar
```

---

## 📝 Notas de Desenvolvimento

### Estrutura de Arquivos Backend
```
com/empresa/gestfy/
├── controllers/
│   └── CaixaController.java          (Endpoints REST)
├── services/
│   ├── CaixaService.java             (Lógica de negócio)
│   ├── ProdutoService.java           (Suporte a produtos)
│   └── EstoqueService.java           (Suporte a estoque)
├── models/
│   └── Caixa.java                    (Entity JPA)
├── repositories/
│   └── CaixaRepository.java          (Queries BD)
└── dto/
    └── caixa/
        ├── VendaRequest.java         (Entrada)
        ├── VendaResponse.java        (Saída)
        ├── ProdutoBuscaResponse.java (Saída)
        └── CaixaDTO.java             (Saída)
```

### Padrões Utilizados
- **DTOs**: Separação entre modelo e API
- **Services**: Lógica de negócio centralizada
- **Repositories**: Acesso a dados isolado
- **Records**: DTOs imutáveis com validação
- **@Transactional**: Garantir integridade de dados
- **Streams**: Transformações de collections

### Melhorias Futuras
- [ ] Auditoria de vendas (quem vendeu, quando)
- [ ] Relatório de caixa em PDF
- [ ] Integração com pedidos do sistema
- [ ] Múltiplos pontos de venda
- [ ] Cupom fiscal (integração)
- [ ] Dashboard de vendas em tempo real
- [ ] Alertas de estoque baixo durante venda
- [ ] Histórico de trocos

---

## 📞 Support

Para dúvidas ou problemas:
1. Verificar console do navegador (F12 → Console)
2. Verificar logs do backend (Spring Boot logs)
3. Testar endpoints com Postman ou curl
4. Consultar DEPLOYMENT_GUIDE.md para issues de produção

---

**Status**: ✅ PRODUCTION READY  
**Última Atualização**: Jan 2025  
**Versão**: 1.0.0
