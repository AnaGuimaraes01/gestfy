# 🧪 GUIA COMPLETO DE TESTE - MÓDULO CAIXA

## ✅ Pré-requisitos

- [ ] PostgreSQL rodando (localhost:5432)
- [ ] Backend compilado (sem erros)
- [ ] Arquivo `.env` configurado com credenciais do BD
- [ ] Frontend em pasta acessível ao navegador
- [ ] Porta 8080 disponível para Spring Boot

---

## 📋 TESTE 1: Verificar Compilação do Backend

### Passo 1: Compilar projeto
```bash
cd backend
./mvnw clean compile
```

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
```

### Passo 2: Verificar se não há erros de import
```bash
./mvnw clean package -DskipTests
```

**Resultado esperado:**
```
[INFO] Building jar: ...gestfy-0.0.1-SNAPSHOT.jar
[INFO] BUILD SUCCESS
```

---

## 🚀 TESTE 2: Iniciar Backend e Verificar Endpoints

### Passo 1: Rodar Spring Boot
```bash
./mvnw spring-boot:run
```

**Resultado esperado:**
```
. . .
Started GestfyApplication in XX.XXX seconds (JVM running for XX.XXX)
```

### Passo 2: Testar endpoints com curl

#### 2.1 Listar todos os registros de caixa
```bash
curl http://localhost:8080/api/caixa
```

**Resultado esperado:**
```json
[]  // Array vazio se é primeira vez
```

#### 2.2 Consultar caixa do dia (antes de haver dados)
```bash
curl http://localhost:8080/api/caixa/dia
```

**Resultado esperado:**
```json
{
  "data": "2025-01-15",
  "totalDia": 0,
  "quantidadeRegistros": 0,
  "registros": []
}
```

---

## 🛒 TESTE 3: Criar um Pedido (Simular Compra)

### Passo 1: Abrir Frontend Cliente
```
file:///C:/caminho/para/frontend/cliente/index.html
```

### Passo 2: Navegar até Catálogo
- Click em "Ver Catálogo" ou link correspondente
- Página `catalogo.html` carrega com lista de produtos

### Passo 3: Adicionar Itens ao Carrinho
- Selecione alguns produtos
- Click em "Adicionar ao Carrinho"
- Carrinho deve mostrar os itens

### Passo 4: Ir para Checkout
- Click em "Finalizar Pedido"
- Página `pedido.html` abre
- Preencha:
  - Nome: "João Silva"
  - Telefone: "11999999999"
  - Email: "joao@email.com"
  - Forma de Pagamento: "DINHEIRO"
  - Forma de Recebimento: "RETIRADA"

### Passo 5: Confirmar Pedido
- Click "Enviar Pedido"
- **Resultado esperado**: Mensagem de sucesso
  ```
  ✅ Pedido enviado com sucesso!
  Número do pedido: #45
  ```

### Passo 6: Verificar Banco de Dados
```bash
curl http://localhost:8080/api/pedidos
```

**Resultado esperado:** Pedido aparece com:
```json
[
  {
    "id": 45,
    "nomeCliente": "João Silva",
    "status": "RECEBIDO",
    "total": 125.50,
    "formaPagamento": "DINHEIRO",
    ...
  }
]
```

### Passo 7: Verificar Estoque (Deve ter registrado SAIDA)
```bash
curl http://localhost:8080/api/estoque
```

**Resultado esperado:** Movimentos de SAIDA para cada produto do pedido

### Passo 8: Verificar Caixa (Ainda Vazio - Pedido não foi finalizado)
```bash
curl http://localhost:8080/api/caixa
```

**Resultado esperado:**
```json
[]  // Ainda vazio, pois pedido está em "RECEBIDO"
```

---

## 📱 TESTE 4: Atualizar Status do Pedido (Admin)

### Passo 1: Abrir Admin Panel
```
file:///C:/caminho/para/frontend/admin/index.html
```

### Passo 2: Ir para Pedidos
- Click em "Pedidos" no menu
- Página `admin/pedidos.html` abre
- Lista o pedido #45 que criamos

### Passo 3: Atualizar Status Progressivamente
1. **RECEBIDO → EM_PREPARO**: Click no botão correspondente
   - Status muda para "EM_PREPARO" ✅
   
2. **EM_PREPARO → PRONTO_RETIRADA**: Click no botão
   - Status muda para "PRONTO_RETIRADA" ✅
   
3. **PRONTO_RETIRADA → FINALIZADO**: Click no botão
   - Status muda para "FINALIZADO" 🎉
   - **🔥 AUTOMÁTICO: CaixaController registra a venda!**

### Passo 4: Verificar Caixa Agora Tem Dados
```bash
curl http://localhost:8080/api/caixa
```

**Resultado esperado:**
```json
[
  {
    "id": 1,
    "saldo": 125.50,
    "descricao": "Venda #45 - Cliente: João Silva",
    "data": "2025-01-15"
  }
]
```

### Passo 5: Consultar Total do Dia
```bash
curl http://localhost:8080/api/caixa/dia
```

**Resultado esperado:**
```json
{
  "data": "2025-01-15",
  "totalDia": 125.50,
  "quantidadeRegistros": 1,
  "registros": [...]
}
```

---

## 💰 TESTE 5: Testar Interface de Caixa

### Passo 1: Abrir Admin Panel
```
file:///C:/caminho/para/frontend/admin/index.html
```

### Passo 2: Ir para Caixa
- Click em "Caixa" no menu (ou navegue para `admin/caixa.html`)
- **Resultado esperado:**
  - Header: "💰 Caixa Diário" + data atual
  - Total Arrecadado: **R$ 125,50** (do pedido que finalizamos)
  - Entradas: **R$ 125,50**
  - Saídas: **R$ 0,00**
  - Quantidade de Transações: **1**
  - Tabela com a venda #45

### Passo 3: Criar Mais Pedidos
Repita os passos 3 a 4 do TESTE 4:
- Crie 2-3 novos pedidos com valores diferentes
- Finalize cada um deles
- Observe a interface de caixa se atualizar em REAL TIME

### Passo 4: Verificar Auto-Refresh
- Espere 30 segundos (intervalo de auto-refresh)
- **Resultado esperado:** Interface atualiza sem você fazer nada

### Passo 5: Testar Filtro de Data
- Click em data anterior no filtro
- Click "Consultar Data"
- **Resultado esperado:** Mostra dados daquela data (se houver)

### Passo 6: Botão "Voltar para Hoje"
- Click "Voltar para Hoje"
- **Resultado esperado:** Volta a mostrar dados do dia atual

### Passo 7: Ver Relatório
- Click em "📊 Ver Relatório Completo"
- **Resultado esperado:** Popup mostra:
  ```
  📊 RELATÓRIO DO DIA 2025-01-15
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  💰 Total de Entradas: R$ 300,00
  💸 Total de Saídas: R$ 0,00
  💵 SALDO LÍQUIDO: R$ 300,00
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  📝 Quantidade de Transações: 3
  ```

---

## 🔒 TESTE 6: Modal de Fechamento do Caixa

### Passo 1: Click em "🔒 Fechar Caixa do Dia"
- Modal abre com:
  - Título: "Fechar Caixa"
  - Mensagem: "Deseja realmente fechar o caixa de hoje?"
  - Total exibido: **R$ 300,00** (exemplo)
  - Botões: [Cancelar] [Confirmar Fechamento]

### Passo 2: Click em "Cancelar"
- Modal fecha
- Dados permanecem iguais
- **Resultado esperado:** Nenhuma mudança

### Passo 3: Reabrir Modal e Click em "Confirmar Fechamento"
- Modal fecha
- Mensagem: "Caixa fechado com sucesso! 🎉"
- **Resultado esperado:** Página recarrega após 2 segundos

---

## 🧮 TESTE 7: Testes de Validação

### Teste 7.1: Tentar Registrar Venda Manualmente (via Postman ou curl)
```bash
curl -X POST http://localhost:8080/api/caixa \
  -H "Content-Type: application/json" \
  -d '{"saldo": 50.00, "descricao": "Teste manual"}'
```

**Resultado esperado:**
```json
{
  "id": 4,
  "saldo": 50.00,
  "descricao": "Teste manual",
  "data": "2025-01-15"
}
```

### Teste 7.2: Tentar com Descricao Vazia (deve falhar)
```bash
curl -X POST http://localhost:8080/api/caixa \
  -H "Content-Type: application/json" \
  -d '{"saldo": 50.00, "descricao": ""}'
```

**Resultado esperado:**
```
400 Bad Request
```

### Teste 7.3: Atualizar um Registro
```bash
curl -X PUT http://localhost:8080/api/caixa/4 \
  -H "Content-Type: application/json" \
  -d '{"saldo": 100.00, "descricao": "Teste atualizado"}'
```

**Resultado esperado:**
```json
{
  "id": 4,
  "saldo": 100.00,
  "descricao": "Teste atualizado",
  "data": "2025-01-15"
}
```

### Teste 7.4: Deletar um Registro
```bash
curl -X DELETE http://localhost:8080/api/caixa/4
```

**Resultado esperado:**
```
204 No Content
```

---

## 📊 TESTE 8: Testar Filtro de Data

### Passo 1: Consultar Data Específica
```bash
curl http://localhost:8080/api/caixa/dia?data=2025-01-15
```

**Resultado esperado:**
```json
{
  "data": "2025-01-15",
  "totalDia": 300.00,
  "quantidadeRegistros": 3,
  "registros": [...]
}
```

### Passo 2: Consultar Data sem Dados
```bash
curl http://localhost:8080/api/caixa/dia?data=2025-01-10
```

**Resultado esperado:**
```json
{
  "data": "2025-01-10",
  "totalDia": 0,
  "quantidadeRegistros": 0,
  "registros": []
}
```

### Passo 3: Testar Período
```bash
# Não tem endpoint específico, mas você pode consultar dia por dia
curl http://localhost:8080/api/caixa/dia?data=2025-01-14
curl http://localhost:8080/api/caixa/dia?data=2025-01-15
curl http://localhost:8080/api/caixa/dia?data=2025-01-16
```

---

## ⚠️ TESTE 9: Edge Cases e Erros

### Teste 9.1: Buscar ID que não existe
```bash
curl http://localhost:8080/api/caixa/99999
```

**Resultado esperado:**
```
404 Not Found
```

### Teste 9.2: Deletar ID que não existe
```bash
curl -X DELETE http://localhost:8080/api/caixa/99999
```

**Resultado esperado:**
```
404 Not Found
```

### Teste 9.3: POST sem body
```bash
curl -X POST http://localhost:8080/api/caixa
```

**Resultado esperado:**
```
400 Bad Request
```

### Teste 9.4: Desconectar banco durante requisição (simular erro)
- Desconecte postgres
- Tente fazer requisição
- **Resultado esperado:** Erro de conexão

---

## 🎯 TESTE 10: Integração Completa

### Cenário: Um dia completo de vendas

#### Manhã (08:00)
- Crie 3 pedidos
- Finalize 2 deles
- Caixa deve mostrar: R$ 200,00 (2 vendas)

#### Meio-dia (12:00)
- Crie 2 pedidos
- Finalize 1 deles
- Caixa deve mostrar: R$ 350,00 (3 vendas)

#### Tarde (17:00)
- Crie 4 pedidos
- Finalize 3 deles
- Caixa deve mostrar: R$ 550,00 (6 vendas)

#### Encerramento (18:00)
- Abra caixa.html
- Veja "Total Arrecadado: R$ 550,00"
- Click "Fechar Caixa do Dia"
- Sistema registra fechamento ✅

---

## 📈 Checklist de Validação Final

- [ ] Backend compila sem erros
- [ ] Endpoints respondem corretamente
- [ ] Pedido criado registra estoque
- [ ] Pedido finalizado registra caixa
- [ ] Interface caixa.html carrega
- [ ] Total arrecadado correto
- [ ] Tabela mostra transações
- [ ] Auto-refresh funciona (30s)
- [ ] Filtro por data funciona
- [ ] Relatório mostra corretamente
- [ ] Modal de fechamento abre/fecha
- [ ] Formatação de moeda em português
- [ ] Sem erros no console (DevTools)
- [ ] Responsivo em mobile
- [ ] Sem quebras visuais

---

## 🐛 Troubleshooting

### Problema: "Erro ao carregar dados"
**Solução:**
1. Verificar se backend está rodando: `http://localhost:8080/api/caixa`
2. Verificar console (F12) para ver erro exato
3. Verificar logs do Spring Boot

### Problema: "Tabela vazia mesmo após finalizar pedidos"
**Solução:**
1. Verificar se pedido foi realmente finalizado em `/api/pedidos`
2. Verificar logs: "✅ Venda registrada no caixa"
3. Chamar `/api/caixa` diretamente no navegador

### Problema: "Data não muda"
**Solução:**
1. F12 → Console → Verificar erro
2. Verificar se JS está loadando: `frontend/js/caixa.js`
3. Limpar cache do navegador (Ctrl+Shift+Delete)

### Problema: "Valores com pontos em vez de vírgulas"
**Solução:**
1. Não é erro, é apenas formatação visual
2. Valores no BD estão corretos (Double)
3. Frontend formata com `Intl.NumberFormat`

---

## ✅ TESTE CONCLUÍDO COM SUCESSO!

Se todos os testes passarem, o módulo CAIXA está **100% funcional** e pronto para produção! 🚀

