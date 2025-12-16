# 🧪 GUIA DE TESTES COMPLETO - GESTFY

## 📝 Roteiros de Testes - Sistema 100% Funcional

---

## 🚀 PRÉ-REQUISITOS

### Antes de Iniciar:
1. ✅ PostgreSQL instalado e rodando
2. ✅ Arquivo `.env` criado no backend com credenciais
3. ✅ Backend compilado e rodando em `http://localhost:8080`
4. ✅ Frontend aberto em navegador

---

## 🧪 TESTE 1: VERIFICAR CONEXÃO COM BANCO

### Passo 1: Backend Rodando
```bash
cd backend
./mvnw spring-boot:run
# ou no Windows: mvnw.cmd spring-boot:run

# Deve aparecer:
# Started GestfyApplication in X seconds
```

### Passo 2: Verificar Conexão
```bash
# Terminal - Verificar se backend está respondendo:
curl http://localhost:8080/api/produtos
# Resposta esperada: [] ou array de produtos
```

✅ **Resultado:** Backend conectado ao banco de dados

---

## 🧪 TESTE 2: CRUD DE PRODUTOS

### Teste 2.1: Criar Produto
```javascript
// No console do navegador:
fetch('http://localhost:8080/api/produtos', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    nome: 'Sorvete de Morango',
    descricao: 'Sorvete caseiro',
    preco: 15.00,
    urlFoto: 'https://via.placeholder.com/300'
  })
})
.then(r => r.json())
.then(d => console.log(d))

// Esperado: ID do produto criado
```

### Teste 2.2: Listar Produtos
```javascript
fetch('http://localhost:8080/api/produtos')
  .then(r => r.json())
  .then(d => console.log(d))

// Esperado: Array com produtos
```

### Teste 2.3: Interface Admin
1. Abrir `admin/produtos.html`
2. Preencher formulário:
   - Nome: "Picolé de Chocolate"
   - Descrição: "Picolé cremoso"
   - Preço: 8.00
   - URL Foto: https://via.placeholder.com/300
3. Clicar "Salvar"
4. ✅ Produto deve aparecer na lista

---

## 🧪 TESTE 3: FLUXO DE VENDA COMPLETO

### Passo 1: Cliente Acessa Catálogo
1. Abrir `cliente/catalogo.html`
2. ✅ Deve listar produtos (se houver no banco)
3. ✅ Cada produto deve ter: nome, preço, foto

### Passo 2: Cliente Adiciona ao Carrinho
1. Clicar em um produto
2. Adicionar quantidade
3. Clicar "Adicionar ao Carrinho"
4. ✅ Produto deve aparecer em `cliente/carrinho.html`

### Passo 3: Cliente Finaliza Compra
1. Abrir `cliente/carrinho.html`
2. Revisar total
3. Clicar "Finalizar Compra"
4. Preencher dados:
   - Nome
   - Email
   - Telefone
   - Forma de Pagamento
   - Forma de Recebimento
5. Clicar "Confirmar"
6. ✅ Pedido deve ser criado com status "RECEBIDO"

### Passo 4: Admin Vê Pedido
1. Abrir `admin/pedidos.html`
2. ✅ Novo pedido deve aparecer com status "RECEBIDO"
3. Clicar para ver detalhes
4. ✅ Deve mostrar: ID, Cliente, Produtos, Total

### Passo 5: Admin Finaliza Pedido
1. Em `admin/pedidos.html`, clicar no pedido
2. Mudar status para "FINALIZADO"
3. Salvar
4. ✅ Pedido atualizado

### Passo 6: Verificar Caixa (AUTOMÁTICO)
1. Abrir `admin/caixa.html`
2. ✅ Deve ter registrado automaticamente a venda
3. ✅ Total "Arrecadado" deve estar correto
4. ✅ Tabela deve mostrar a venda

---

## 🧪 TESTE 4: AUTO-REFRESH CAIXA

### Procedimento:
1. Abrir `admin/caixa.html` em primeiro plano
2. Em outro navegador/aba, finalizar um novo pedido
3. ✅ Caixa deve atualizar automaticamente em 30 segundos (sem F5)
4. ✅ Total "Arrecadado" deve aumentar

---

## 🧪 TESTE 5: BANCO DE DADOS

### Verificar Tabelas PostgreSQL
```sql
-- Conectar ao PostgreSQL:
psql -U postgres

-- Listar tabelas:
\dt

-- Esperado:
-- cliente
-- produto
-- pedido
-- pedido_item
-- estoque
-- caixa

-- Ver dados de um pedido:
SELECT * FROM pedido;
SELECT * FROM caixa WHERE data = TODAY();
```

✅ **Resultado:** Todas as tabelas criadas com dados

---

## 🧪 TESTE 6: RESPONSIVIDADE

### Desktop (1920px)
1. Abrir qualquer página HTML
2. F12 → Dispositivo Desktop
3. ✅ Layout deve ocupar espaço adequadamente
4. ✅ Cards devem estar em grid

### Tablet (768px)
1. F12 → Dispositivo "iPad"
2. ✅ Layout deve adaptar para 2 colunas
3. ✅ Botões devem ser clicáveis
4. ✅ Sem overflow horizontal

### Mobile (375px)
1. F12 → Dispositivo "iPhone SE"
2. ✅ Layout deve adaptar para 1 coluna
3. ✅ Menu deve ser acessível
4. ✅ Texto legível
5. ✅ Sem scroll horizontal

---

## 🧪 TESTE 7: VALIDAÇÕES

### Teste 7.1: Produto Sem Nome
```javascript
fetch('http://localhost:8080/api/produtos', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    nome: '',  // ❌ Vazio
    descricao: 'Teste',
    preco: 10
  })
})
.then(r => r.json())
.then(d => console.log(d))

// Esperado: Erro 400 - "O nome do produto é obrigatório"
```

### Teste 7.2: Email Inválido
1. Abrir `cliente/catalogo.html`
2. Clicar "Finalizar Compra"
3. Email: "teste@invalido" (sem .com)
4. ✅ Deve aparecer mensagem: "Email inválido"

---

## 🧪 TESTE 8: RELATÓRIOS

### Teste 8.1: Vendas por Dia
1. Abrir `admin/relatorios.html`
2. Clicar em "Vendas por Dia"
3. ✅ Deve mostrar gráfico/tabela com vendas

### Teste 8.2: Estoque
1. Em relatórios, clicar "Estoque"
2. ✅ Deve mostrar nível de cada produto

---

## 🧪 TESTE 9: ACOMPANHAMENTO CLIENTE

### Procedimento:
1. Cliente finaliza compra (Teste 3)
2. Recebe número do pedido
3. Abrir `cliente/acompanhamento.html`
4. Digitar número do pedido
5. ✅ Deve mostrar status RECEBIDO/CONFIRMADO/etc

---

## 🧪 TESTE 10: MENU NAVEGAÇÃO

### Admin
1. Abrir `admin/index.html`
2. ✅ Deve ter 5 cards: Pedidos, Produtos, Estoque, Caixa, Relatórios
3. Clicar em cada um
4. ✅ Deve navegar para a página correspondente

### Cliente
1. Abrir `cliente/index.html`
2. ✅ Deve ter opções: Catálogo, Acompanhamento
3. Clicar "Ir para o Catálogo"
4. ✅ Deve ir para `cliente/catalogo.html`

---

## 📊 RESULTADO ESPERADO PARA TODOS OS TESTES

| Teste | Esperado | Status |
|-------|----------|--------|
| 1 - Conexão BD | Backend respondendo | ✅ |
| 2 - CRUD Produtos | Criar, listar | ✅ |
| 3 - Fluxo Venda | Pedido criado → Caixa automático | ✅ |
| 4 - Auto-Refresh | Atualizar sem F5 | ✅ |
| 5 - Banco de Dados | Tabelas criadas com dados | ✅ |
| 6 - Responsividade | Funciona em 3 tamanhos | ✅ |
| 7 - Validações | Mensagens de erro | ✅ |
| 8 - Relatórios | Gráficos funcionam | ✅ |
| 9 - Acompanhamento | Cliente vê status | ✅ |
| 10 - Menu | Navegação funciona | ✅ |

---

## 🎯 CHECKLIST RÁPIDO (5 MINUTOS)

```
☐ 1. Backend rodando (http://localhost:8080)
☐ 2. Admin index.html carrega
☐ 3. Cliente catalogo.html mostra produtos
☐ 4. Criar novo produto em admin/produtos.html
☐ 5. Criar novo pedido em cliente/catalogo.html
☐ 6. Admin finaliza pedido
☐ 7. Caixa mostra venda automaticamente
☐ 8. Responsividade (testar no mobile)
☐ 9. Menu navegação funciona
☐ 10. Sem erros no console (F12)

Se todos marcados: ✅ SISTEMA 100% FUNCIONAL
```

---

## 🔍 SE ALGO NÃO FUNCIONAR

### Backend não responde
```bash
# Verificar:
1. PostgreSQL está rodando?
2. Arquivo .env existe com credenciais?
3. Porta 8080 está livre?
4. Revisar logs do Spring Boot
```

### Produtos não aparecem
```bash
# Verificar:
1. Tabela 'produto' existe em PostgreSQL?
2. Backend compilou sem erros?
3. Endpoint /api/produtos responde?
4. Abrir F12 → Network → ver requisição
```

### Caixa não atualiza
```bash
# Verificar:
1. Pedido foi finalizado?
2. F5 em admin/caixa.html
3. Verificar console (F12)
4. Ver logs do backend
```

### Interface feia no mobile
```bash
# Verificar:
1. CSS carregou (F12 → Network → style.css)
2. Meta viewport em HTML
3. Zoom 100% (não 110%)
4. Limpar cache (Ctrl+Shift+Delete)
```

---

## ✅ CONCLUSÃO

Se todos os 10 testes passarem:
🎉 **SISTEMA COMPLETAMENTE FUNCIONAL**
🎉 **INTERFACE RESPONSIVA**
🎉 **BANCO DE DADOS CORRETO**
🎉 **PRONTO PARA PRODUÇÃO**

---

**Última atualização:** 16/12/2025
**Versão:** 1.0.0
**Status:** ✅ PRONTO PARA TESTAR

