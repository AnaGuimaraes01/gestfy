# 🧪 GUIA RÁPIDO DE TESTE DO GESTFY

## ⚡ TESTE EM 5 MINUTOS

### Pré-requisitos
- ✅ PostgreSQL instalado e rodando
- ✅ Java 17 instalado
- ✅ Arquivo `.env` configurado no backend

### 1️⃣ Iniciar Backend

```bash
cd backend
./mvnw spring-boot:run
```

✅ Quando vir `Started GestfyApplication`, está pronto!  
🌐 Acesse: http://localhost:8080/api/produtos

### 2️⃣ Abrir Frontend

**Opção A** - Servidor HTTP:
```bash
cd frontend
python -m http.server 3000
```

**Opção B** - Direto no navegador:
```
file:///C:/Users/amand/OneDrive/Área de Trabalho/ADS M4/GESTFY/gestfy/frontend/cliente/index.html
```

---

## 🧪 TESTE COMO CLIENTE

### 1. **Acessar Catálogo**
```
http://localhost:3000/cliente/index.html
Clique em "Ir para o Catálogo"
```

### 2. **Buscar Produtos**
- Digite na barra de busca
- Veja produtos filtrados em tempo real

### 3. **Adicionar ao Carrinho**
- Clique em "➕ Adicionar" em qualquer produto
- Veja mensagem de sucesso
- Clique em "🛒 Ver Carrinho"

### 4. **Gerenciar Carrinho**
- Use + e - para aumentar/diminuir
- Clique ✕ para remover
- Veja total atualizar

### 5. **Finalizar Pedido**
- Preencha: nome, telefone, email
- Selecione recebimento (RETIRADA/ENTREGA)
- Selecione pagamento (DINHEIRO/PIX)
- Clique "✅ Confirmar Pedido"

### 6. **Acompanhar Pedido**
- Será redirecionado para acompanhamento
- Veja status do pedido
- A página atualiza a cada 5 segundos

### 7. **Ver Histórico**
```
http://localhost:3000/cliente/pedidos.html
```
- Tabela com todos os pedidos
- Clique "Ver" para acompanhar

---

## 🏪 TESTE COMO DONO

### 1. **Acessar Admin**
```
http://localhost:3000/admin/index.html
```

### 2. **Gerenciar Produtos**
- Clique em "Gerenciar Produtos"
- Preencha formulário
- Clique "Cadastrar"
- Veja produto na lista

### 3. **Ver Pedidos Recebidos**
- Clique em "Controle de Pedidos"
- Tabela com todos os pedidos
- Clique em "Alterar Status"

### 4. **Alterar Status**
- Pedido começa como "RECEBIDO"
- Altere para "EM_PREPARO"
- Depois para "PRONTO_RETIRADA"
- Depois para "FINALIZADO"
- ⚠️ Não pode pular etapas ou voltar

### 5. **Verificar Estoque**
- Clique em "Controle de Estoque"
- Veja movimentos ENTRADA/SAIDA
- Quando cria pedido, estoque desconta automaticamente

### 6. **Ver Caixa**
- Clique em "Caixa"
- Mostra vendas do dia
- Total arrecadado

### 7. **Ver Relatórios**
- Clique em "Relatórios"
- Vendas por dia
- Produtos mais vendidos
- Total de pedidos
- Estoque baixo

---

## 🔍 VERIFICAR INTEGRAÇÕES

### ✅ Integração Estoque-Pedido

1. **Criar Produto**
   - Admin → Produtos → Criar "Pizza"
   
2. **Registrar Entrada no Estoque**
   - Admin → Estoque → ENTRADA - 10 unidades
   
3. **Fazer Pedido como Cliente**
   - Cliente → Catálogo → Buscar "Pizza"
   - Adicionar 3 unidades ao carrinho
   - Finalizar pedido
   
4. **Verificar Estoque**
   - Admin → Estoque
   - Veja: ENTRADA (10) + SAIDA (3) = Estoque atual: 7

---

## 🔄 TESTE DE STATUS

### Validação de Transições

1. **Criar Pedido**
   - Cliente finaliza pedido
   - Status começa como RECEBIDO

2. **Tentar Pular Etapa** ❌ (Deve Falhar)
   - Admin tenta: RECEBIDO → FINALIZADO
   - Sistema deve bloquear

3. **Sequência Correta** ✅ (Deve Funcionar)
   - RECEBIDO → EM_PREPARO → PRONTO_RETIRADA → FINALIZADO
   - Todas as transições devem funcionar

4. **Tentar Alterar Finalizado** ❌ (Deve Falhar)
   - Pedido em FINALIZADO
   - Tente alterar status
   - Sistema deve bloquear

---

## 🐛 DEBUGGING

### Se não carregar produtos
1. Verifique se backend está rodando
2. Verifique console do navegador (F12)
3. Verifique network (F12 → Network)

### Se não criar pedido
1. Verifique se todos os campos estão preenchidos
2. Verifique console (F12 → Console)
3. Verifique se cliente foi criado na API

### Se estoque não descontar
1. Crie pedido com cliente novo
2. Verifique `admin/estoque.html`
3. Procure por movimento SAIDA

---

## 📊 TESTE DE RESPONSIVIDADE

### Mobile (375px)
1. F12 → Toggle device toolbar (Ctrl+Shift+M)
2. Selecione iPhone X
3. Veja layout reposicionar

### Tablet (768px)
1. Altere para iPad
2. Grid deve ter 2 colunas

### Desktop (1200px+)
1. Redimensione navegador
2. Layout deve expandir normalmente

---

## ✅ CHECKLIST DE TESTES

- [ ] Produto criado com sucesso
- [ ] Busca funciona em tempo real
- [ ] Item adicionado ao carrinho
- [ ] Quantidade aumenta/diminui
- [ ] Item removido do carrinho
- [ ] Pedido criado com sucesso
- [ ] Status começa com RECEBIDO
- [ ] Status muda para EM_PREPARO
- [ ] Estoque desconta após pedido
- [ ] Acompanhamento atualiza a cada 5s
- [ ] Relatórios mostram vendas
- [ ] Design é responsivo
- [ ] Não há erros no console
- [ ] Mensagens aparecem corretamente

---

## 🎬 CENÁRIO DE TESTE COMPLETO

### Cenário: Vender 2 Pizzas para João

#### Cliente (João)
```
1. Vai para catalogo.html
2. Busca por "Pizza"
3. Clica em Adicionar (quantidade 2)
4. Vai para carrinho.html
5. Vê 2 pizzas no carrinho com R$ total
6. Clica em Finalizar Pedido
7. Preenche:
   - Nome: João Silva
   - Telefone: (11) 99999-9999
   - Email: joao@email.com
   - Recebimento: RETIRADA
   - Pagamento: DINHEIRO
8. Clica Confirmar
9. Vê mensagem de sucesso
10. Redirecionado para acompanhamento (ex: Pedido #5)
11. Vê status: 📋 RECEBIDO
```

#### Dono
```
1. Acessa admin/pedidos.html
2. Vê novo pedido: "João Silva - R$ 60,00 - RECEBIDO"
3. Clica em "Alterar Status"
4. Muda para EM_PREPARO
5. Vê atualização imediata
6. Quando pronto, muda para PRONTO_RETIRADA
7. João clica F5 em acompanhamento
8. Vê status: ✅ PRONTO_RETIRADA
9. João vem retirar
10. Admin muda para FINALIZADO
11. Acessa Relatórios
12. Vê venda de hoje: "João Silva - R$ 60,00"
13. Vê em Produtos Mais Vendidos: "Pizza - 2 unidades"
```

---

## 💾 DADOS DE TESTE

### Produtos para Cadastrar
```
1. Pizza Margherita
   - Descrição: Pizza clássica com tomate, mozzarela e manjericão
   - Preço: R$ 30,00
   - Foto: 🍕

2. Sorvete Chocolate
   - Descrição: Sorvete artesanal sabor chocolate intenso
   - Preço: R$ 15,00
   - Foto: 🍦

3. Refrigerante 2L
   - Descrição: Bebida gelada sabor cola
   - Preço: R$ 10,00
   - Foto: 🥤
```

### Estoque Inicial
```
- Pizza Margherita: ENTRADA 20
- Sorvete Chocolate: ENTRADA 50
- Refrigerante 2L: ENTRADA 30
```

---

## 🎉 PRONTO!

Seu Gestfy está totalmente funcional! Divirta-se testando!

**Dúvidas?** Verifique:
- Console do navegador (F12)
- Logs do backend
- Arquivo `README_FINAL.md`
- Arquivo `IMPLEMENTACAO_COMPLETA.md`

---

**Gestfy v1.0 - Dezembro de 2025**
