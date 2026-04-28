# 🚀 GUIA RÁPIDO - CAIXA SIMPLES

## ⚡ Como Começar

### 1. Arquivos Criados/Modificados

#### 📝 Backend (Java)
- ✅ `CaixaController.java` - **REESCRITO COMPLETAMENTE** com nova lógica
- ✅ `ProdutoRepository.java` - Adicionado método de busca parcial
- ✅ `dto/caixa/ProdutoBuscaResponse.java` - ✨ NOVO
- ✅ `dto/caixa/VendaRequest.java` - ✨ NOVO
- ✅ `dto/caixa/VendaResponse.java` - ✨ NOVO

#### 🎨 Frontend (HTML/JS)
- ✅ `frontend/admin/caixa-novo.html` - ✨ NOVO (interface completa)
- ✅ `frontend/admin/js/caixa-novo.js` - ✨ NOVO (lógica completa)

#### 📚 Documentação
- ✅ `CAIXA_SIMPLES_DOCUMENTACAO.md` - Documentação completa

---

## 🔧 CONFIGURAÇÃO PARA DESENVOLVIMENTO

### Passo 1: Altere a URL da API

Abra: `frontend/admin/js/caixa-novo.js`

Localize (linha ~10):
```javascript
// const API_BASE = 'http://localhost:8080/api'; // Descomente para desenvolvimento
```

**Mude para:**
```javascript
const API_BASE = 'http://localhost:8080/api'; // ← DESCOMENTE ISTO
// const API_BASE = 'https://gestfy-backend.onrender.com/api';
```

### Passo 2: Compile o Backend

```bash
cd backend
./mvnw clean package
# ou no Windows: mvnw.cmd clean package
```

### Passo 3: Inicie o Backend

```bash
./mvnw spring-boot:run
# ou no Windows: mvnw.cmd spring-boot:run
```

Backend estará em: `http://localhost:8080`

### Passo 4: Abra o Frontend

Abra no navegador:
```
file:///C:/Users/Ana Carla/Desktop/gestfy/frontend/admin/caixa-novo.html
```

---

## ✅ CHECKLIST DE TESTES

### Teste 1: Abrir Caixa
- [ ] Clique em "✓ ABRIR CAIXA"
- [ ] Deve aparecer mensagem verde "Caixa aberto com sucesso!"
- [ ] Status muda para "ABERTO ✓"
- [ ] Botão "ABRIR" fica desabilitado
- [ ] Botão "FECHAR" fica habilitado
- [ ] Seção de vendas aparece

### Teste 2: Buscar Produto
- [ ] Digite "sor" no campo de busca
- [ ] Clique "🔍 Buscar" ou pressione Enter
- [ ] Lista de produtos aparece com:
  - [ ] Nome do produto
  - [ ] Preço
  - [ ] Estoque disponível
  - [ ] ID do produto

### Teste 3: Selecionar Produto
- [ ] Clique em um produto da lista
- [ ] Formulário se preenche com:
  - [ ] ID do produto
  - [ ] Preço unitário
  - [ ] Campo de quantidade = 1
- [ ] Resumo da venda aparece
- [ ] Campo "Valor Recebido" fica ativo

### Teste 4: Calcular Venda
- [ ] Mude quantidade para 2
- [ ] Digite valor recebido 50.00
- [ ] Resumo atualiza com:
  - [ ] Quantidade: 2
  - [ ] Valor Total: calculado corretamente
  - [ ] Troco: calculado corretamente (verde)

### Teste 5: Validar Estoque Insuficiente
- [ ] Tente quantidade MAIOR que o estoque
- [ ] Mensagem de erro aparece
- [ ] Botão "CONFIRMAR" não funciona

### Teste 6: Validar Valor Insuficiente
- [ ] Digite quantidade 2 (valor total R$ 30)
- [ ] Digite valor recebido R$ 20
- [ ] Troco fica negativo (vermelho)
- [ ] Mensagem de aviso aparece
- [ ] Botão "CONFIRMAR" não funciona

### Teste 7: Confirmar Venda ✓
- [ ] Quantidade válida, estoque OK, valor OK
- [ ] Clique "✓ CONFIRMAR VENDA"
- [ ] Mensagem de sucesso aparece
- [ ] Formulário limpa automaticamente
- [ ] Venda aparece no histórico
- [ ] Totalizadores atualizam

### Teste 8: Histórico de Vendas
- [ ] Após confirmar uma venda
- [ ] Venda aparece em "Vendas do Dia" com:
  - [ ] Nome e quantidade do produto
  - [ ] Valor da venda
  - [ ] Horário
- [ ] Totalizadores mostram:
  - [ ] Total de vendas (contagem)
  - [ ] Total arrecadado (em R$)

### Teste 9: Fechar Caixa
- [ ] Clique "✕ FECHAR CAIXA"
- [ ] Confirme a ação
- [ ] Mensagem de sucesso mostra:
  - [ ] Total de vendas
  - [ ] Total arrecadado
- [ ] Status muda para "FECHADO ✕"
- [ ] Seção de vendas desaparece
- [ ] Botões mudam de estado

### Teste 10: Validar Banco de Dados
Após confirmar uma venda, verifique no banco:

```sql
-- Venda registrada
SELECT * FROM caixa WHERE tipo = 'ENTRADA' ORDER BY data DESC LIMIT 5;

-- Estoque atualizado
SELECT * FROM produto WHERE id = 1;

-- Movimento registrado
SELECT * FROM estoque WHERE tipo_movimento = 'SAIDA' ORDER BY data_movimento DESC LIMIT 5;
```

---

## 🐛 PROBLEMAS COMUNS

### Erro: "Erro ao conectar com o servidor"
```
❌ Erro ao conectar com o servidor

SOLUÇÕES:
1. Verifique se o backend está rodando (http://localhost:8080)
2. Verifique se a API_BASE está correta no caixa-novo.js
3. Verifique o console do navegador (F12) para mais detalhes
4. Verifique se PostgreSQL está rodando
```

### Erro: "Nenhum produto encontrado"
```
❌ Nenhum produto encontrado com o nome: "xyz"

SOLUÇÕES:
1. Adicione produtos no banco de dados
2. Verifique a grafia
3. Tente buscar por parte do nome
```

### Erro: "Caixa já está aberto"
```
❌ Caixa já está aberto para hoje

SOLUÇÕES:
1. Feche o caixa anterior (POST /api/caixa/fechar)
2. Ou aguarde a meia-noite (novo dia)
3. Ou delete os registros de teste do banco
```

### Erro: "Estoque insuficiente"
```
❌ Estoque insuficiente

SOLUÇÕES:
1. Digite uma quantidade menor
2. Compre mais estoque do produto
3. Venda outro produto
```

---

## 🧪 DADOS DE TESTE

### Criar Produto de Teste

```sql
INSERT INTO produto (nome, descricao, preco, quantidade, url_foto)
VALUES 
('Sorvete de Chocolate', 'Sorvete cremoso de chocolate', 15.00, 50, 'N/A'),
('Refrigerante', 'Refrigerante gelado', 8.00, 100, 'N/A'),
('Lanche', 'Lanche delicioso', 25.00, 30, 'N/A');
```

### Testar Sem Front

Use **Postman** ou **curl**:

```bash
# 1. Abrir Caixa
curl -X POST http://localhost:8080/api/caixa/abrir

# 2. Buscar Produto
curl "http://localhost:8080/api/caixa/buscar-produto?nome=sorvete"

# 3. Registrar Venda
curl -X POST http://localhost:8080/api/caixa/vender \
  -H "Content-Type: application/json" \
  -d '{"produtoId": 1, "quantidade": 2, "valorRecebido": 50.00}'

# 4. Listar Vendas do Dia
curl http://localhost:8080/api/caixa/vendas-do-dia

# 5. Obter Status
curl http://localhost:8080/api/caixa/status

# 6. Fechar Caixa
curl -X POST http://localhost:8080/api/caixa/fechar
```

---

## 📊 QUERIES ÚTEIS PARA VERIFICAÇÃO

```sql
-- Ver caixa do dia
SELECT * FROM caixa 
WHERE data = CURRENT_DATE 
ORDER BY horario_abertura DESC;

-- Ver total de vendas do dia
SELECT 
  COUNT(*) as total_vendas,
  SUM(saldo) as total_arrecadado
FROM caixa
WHERE data = CURRENT_DATE AND tipo = 'ENTRADA';

-- Ver movimentações de estoque
SELECT * FROM estoque ORDER BY data_movimento DESC LIMIT 10;

-- Ver quantidade atual dos produtos
SELECT id, nome, quantidade FROM produto;

-- Verificar se caixa está aberto
SELECT * FROM caixa 
WHERE data = CURRENT_DATE AND status = 'ABERTO';
```

---

## 🔐 SEGURANÇA

⚠️ **O sistema atual:**
- ✅ Valida no backend (não confie só no frontend)
- ✅ Registra todas as transações no banco
- ✅ Não permite venda sem estoque
- ✅ Não permite venda sem valor suficiente
- ✅ Não permite dois caixas abertos no mesmo dia

❌ **TODO para produção:**
- [ ] Adicionar autenticação/login
- [ ] Adicionar permissões por usuário
- [ ] Adicionar logs de auditoria
- [ ] Adicionar backups automáticos
- [ ] Adicionar SSL/HTTPS
- [ ] Validar entrada do usuário no backend

---

## 📞 PRÓXIMAS MELHORIAS

Funcionalidades sugeridas:
- [ ] Editar/Cancelar última venda
- [ ] Relatório de vendas por período
- [ ] Filtro por produto no histórico
- [ ] Devolução/Cancelamento de venda
- [ ] Múltiplas formas de pagamento
- [ ] Integração com PDV (impressora)
- [ ] Dashboard analytics

---

## 📋 RESUMO DE ALTERAÇÕES

| Arquivo | Tipo | Status | Descrição |
|---------|------|--------|-----------|
| CaixaController.java | Backend | 🔄 Modificado | Reescrito com nova lógica |
| ProdutoRepository.java | Backend | ✏️ Editado | Adicionado busca parcial |
| ProdutoBuscaResponse.java | Backend | ✨ Criado | DTO de resposta |
| VendaRequest.java | Backend | ✨ Criado | DTO de requisição |
| VendaResponse.java | Backend | ✨ Criado | DTO de resposta de venda |
| caixa-novo.html | Frontend | ✨ Criado | Interface HTML |
| caixa-novo.js | Frontend | ✨ Criado | Lógica JavaScript |
| CAIXA_SIMPLES_DOCUMENTACAO.md | Doc | ✨ Criado | Documentação |

---

**Status:** ✅ PRONTO PARA TESTE  
**Data:** 11 de Janeiro de 2025  
**Versão:** 1.0.0-BETA
