# 🚀 GUIA DE TESTE - GESTFY SISTEMA COMPLETO

## Status: PRONTO PARA TESTE

Após todas as implementações, o sistema está pronto para validação. Este guia vai te mostrar como testar cada funcionalidade passo a passo.

---

## 📋 ANTES DE COMEÇAR

### Pré-requisitos:
- ✅ Backend rodando em `https://gestfy-backend.onrender.com`
- ✅ Frontend em `/frontend/admin/` e `/frontend/cliente/`
- ✅ Banco de dados MySQL conectado
- ✅ Navegador atualizado (Chrome, Firefox, Edge, Safari)

### Credenciais de Teste:
```
ADMIN:
Usuário: admin
Senha: admin123

CAIXA:
Usuário: caixa01
Senha: caixa123
```

---

## 🧪 TESTE 1: AUTENTICAÇÃO ADMIN

### 1.1 Acessar Login Admin
1. Abra `frontend/admin/login.html` no navegador
2. **Esperado**: Página de login com campo usuário e senha

### 1.2 Login com Credenciais Corretas
1. Digite: `admin` (usuário)
2. Digite: `admin123` (senha)
3. Clique: "Entrar"
4. **Esperado**:
   - Mensagem: "✅ Login realizado com sucesso!"
   - Redirecionamento para `index.html`
   - Você entra no painel admin

### 1.3 Menu Admin Aparece
1. Você deve ver 5 cards:
   - 📋 Pedidos
   - 📦 Produtos
   - 📊 Estoque
   - 💰 Caixa
   - 📈 Relatórios
2. Deve aparecer botão "Sair" na barra superior

### 1.4 Logout
1. Clique em "Sair" na barra superior
2. **Esperado**:
   - Confirmação: "Deseja realmente sair?"
   - Redirecionamento para `login.html`
   - sessionStorage limpo

---

## 🧪 TESTE 2: AUTENTICAÇÃO CAIXA

### 2.1 Acessar Caixa sem Autenticação
1. Abra `frontend/admin/caixa.html` diretamente
2. **Esperado**: Redirecionamento para `caixa-login.html`

### 2.2 Login do Caixa
1. Você deve estar em `caixa-login.html`
2. Digite: `caixa01` (usuário)
3. Digite: `caixa123` (senha)
4. Clique: "Abrir Caixa"
5. **Esperado**:
   - Mensagem: "✅ Caixa aberto com sucesso!"
   - Redirecionamento para `caixa.html`
   - Na barra: "💰 caixa01 | Aberto às 14:30 | [Fechar Caixa]"

### 2.3 Barra de Informações
1. Você deve ver na barra superior:
   - Ícone 💰
   - Nome do usuário: `caixa01`
   - Hora de abertura
   - Botão "Fechar Caixa"
2. **Esperado**: Botão está visível e funcional

### 2.4 Fechar Caixa
1. Clique em "Fechar Caixa"
2. **Esperado**: Confirmação "Deseja realmente fechar o caixa?"
3. Clique "OK"
4. **Esperado**:
   - Redirecionamento para `caixa-login.html`
   - sessionStorage limpo

---

## 🧪 TESTE 3: GERENCIAMENTO DE PRODUTOS

### 3.1 Acessar Produtos
1. Login como admin
2. Clique no card "📦 Produtos"
3. **Esperado**: Lista de produtos existentes

### 3.2 Listar Produtos
1. Você deve ver cards com:
   - 🍦 Emoji ou foto
   - Nome do produto
   - Descrição
   - Preço (R$)
   - Quantidade (Qtd)
   - ID
   - Botão "✏️ Editar"
2. **Esperado**: Pelo menos 1 produto na lista

### 3.3 Criar Novo Produto
1. Role até o formulário "Cadastrar/Atualizar Produto"
2. Preencha:
   - Nome: `Sorvete de Chocolate`
   - Descrição: `Sorvete de chocolate belga`
   - Preço: `5.50`
   - Quantidade: `20`
   - Foto URL: (deixe em branco)
3. Clique: "Salvar Produto"
4. **Esperado**:
   - Mensagem: "✅ Produto cadastrado com sucesso!"
   - Novo produto aparece na lista
   - Form limpa

### 3.4 Editar Produto
1. Clique em "✏️ Editar" em qualquer produto
2. **Esperado**:
   - Form preenche com dados do produto
   - Botão muda para "💾 Atualizar Produto"
3. Modifique o preço (ex: 6.00)
4. Clique: "Atualizar Produto"
5. **Esperado**:
   - Mensagem: "✅ Produto atualizado com sucesso!"
   - Produto ACTUALIZA (não cria novo)
   - Preço refletido na lista

### 3.5 Cancelar Edição
1. Clique em "✏️ Editar" em um produto
2. Clique em "Cancelar Edição"
3. **Esperado**:
   - Form limpa
   - Botão volta a "Salvar Produto"

### 3.6 Validar Campos Obrigatórios
1. Deixe o campo "Nome" vazio
2. Clique "Salvar"
3. **Esperado**: Erro "Nome obrigatório"

---

## 🧪 TESTE 4: CONTROLE DE ESTOQUE

### 4.1 Acessar Estoque
1. Login como admin
2. Clique em "📊 Estoque"
3. **Esperado**: Página com 3 seções

### 4.2 Seção 1: Inventário
1. Você deve ver:
   - Cards de resumo: Total Produtos, Em Falta, Estoque Baixo
   - Tabela com: ID, Nome, Preço, Qtd, Status
2. **Esperado**: Todos os produtos listados

### 4.3 Alertas de Estoque
1. Procure por um produto com qtd ≤ 0
2. **Esperado**: Status "⚠️ EM FALTA" (vermelho)
3. Procure por produto com 0 < qtd ≤ 5
4. **Esperado**: Status "⚠️ ESTOQUE BAIXO" (laranja)
5. Procure por produto com qtd > 5
6. **Esperado**: Status "✅ Disponível" (verde)

### 4.4 Filtrar por Nome
1. Digite o nome de um produto (ex: "Chocolate")
2. **Esperado**: Tabela filtra em tempo real
3. Clique "Limpar"
4. **Esperado**: Filtro reseta, mostra todos produtos

### 4.5 Seção 2: Registrar Movimentação
1. Selecione um produto no dropdown
2. Selecione tipo "📥 Entrada"
3. Digite quantidade: `10`
4. Clique "Registrar"
5. **Esperado**:
   - Mensagem: "✅ Movimento registrado com sucesso!"
   - Quantidade do produto aumenta em 10
   - Form reseta

### 4.6 Registrar Saída
1. Selecione um produto
2. Selecione tipo "📤 Saída"
3. Digite quantidade: `2`
4. Clique "Registrar"
5. **Esperado**:
   - Quantidade diminui em 2
   - Movimento aparece em "Últimas Movimentações"

### 4.7 Seção 3: Últimas Movimentações
1. Você deve ver tabela com:
   - ID Movimento
   - Data/Hora
   - **Nome do Produto** (não ID)
   - Tipo (ENTRADA/SAIDA)
   - Quantidade
2. **Esperado**: Últimas 20 movimentações listadas

---

## 🧪 TESTE 5: GERENCIAMENTO DE PEDIDOS

### 5.1 Acessar Pedidos
1. Login como admin
2. Clique em "📋 Pedidos"
3. **Esperado**: Tabela com pedidos

### 5.2 Listar Pedidos
1. Tabela deve mostrar:
   - ID
   - Cliente (nome + telefone)
   - Forma de Pagamento
   - Total (R$)
   - Status (select dropdown)
   - Botão "Detalhes"
2. **Esperado**: Pelo menos 1 pedido

### 5.3 Atualizar Status
1. Clique no dropdown de Status de um pedido
2. Selecione novo status (ex: "EM_PREPARO")
3. **Esperado**:
   - Status atualiza no backend
   - Tabela recarrega
   - Novo status visível

### 5.4 Ver Detalhes
1. Clique em "Detalhes" de um pedido
2. **Esperado**: Alert com informações:
   ```
   Pedido #123
   
   Cliente: João Silva
   Endereço: Rua das Flores, 100 (ou "Retirada no local")
   Total: R$ 25.50
   Status: EM_PREPARO
   Pagamento: CARTAO_DEBITO
   
   Itens:
   • Sorvete de Chocolate (x2) - R$ 11.00
   • Suco Natural (x1) - R$ 5.00
   ```

### 5.5 Endereço nos Detalhes
1. Verifique se o endereço aparece corretamente
2. **Esperado**: 
   - Para entrega: "Endereço: [endereço do cliente]"
   - Para retirada: "Endereço: Retirada no local"

---

## 🧪 TESTE 6: CLIENTE - CATÁLOGO

### 6.1 Acessar Catálogo
1. Abra `frontend/cliente/catalogo.html`
2. **Esperado**: Lista de produtos com cards

### 6.2 Cards de Produtos
1. Cada card deve mostrar:
   - 🍦 Emoji ou Foto
   - Nome
   - Descrição
   - Preço (R$)
   - Botão "Adicionar ao Carrinho"
2. **Esperado**: Pelos menos 3 produtos visíveis

### 6.3 Busca por Produto
1. Digite no campo de busca: "Chocolate"
2. **Esperado**: Lista filtra para mostrar só produtos com "Chocolate"
3. Limpe a busca
4. **Esperado**: Volta a mostrar todos produtos

### 6.4 Adicionar ao Carrinho
1. Clique "Adicionar ao Carrinho" em um produto
2. **Esperado**: 
   - Mensagem de confirmação
   - Badge de carrinho atualiza (mostra quantidade)

---

## 🧪 TESTE 7: CLIENTE - CARRINHO

### 7.1 Acessar Carrinho
1. Clique no ícone 🛒 Carrinho (ou botão "Ver Carrinho")
2. **Esperado**: Página com lista de produtos

### 7.2 Produtos no Carrinho
1. Cada item deve mostrar:
   - Nome
   - Preço unitário
   - Quantidade
   - Botões +/-
   - Subtotal
   - Botão "Remover"
2. **Esperado**: Produtos que você adicionou aparecem

### 7.3 Aumentar Quantidade
1. Clique em "+" de um produto
2. **Esperado**: Quantidade aumenta, subtotal e total recalculam

### 7.4 Diminuir Quantidade
1. Clique em "-"
2. **Esperado**: Quantidade diminui

### 7.5 Remover Produto
1. Clique em "Remover" ou "X"
2. **Esperado**: Produto sai do carrinho, total recalcula

### 7.6 Total do Carrinho
1. Verifique se o Total está correto
2. **Esperado**: Total = Sum(subtotal de cada item)

---

## 🧪 TESTE 8: CLIENTE - PEDIDO COM ENDEREÇO

### 8.1 Finalizar Pedido
1. Clique "Finalizar Pedido"
2. **Esperado**: Formulário com campos:
   - Nome
   - Telefone
   - Forma de Recebimento (Retirada/Entrega)
   - Forma de Pagamento

### 8.2 Campo Endereço - Retirada
1. Selecione "Retirada no Local"
2. **Esperado**: Campo "Endereço" fica HIDDEN/oculto

### 8.3 Campo Endereço - Entrega
1. Selecione "Entrega"
2. **Esperado**: Campo "Endereço" aparece
   - Placeholder: "Rua, número, complemento, bairro"
   - Campo obrigatório

### 8.4 Validação de Endereço
1. Selecione "Entrega"
2. Deixe endereço VAZIO
3. Clique "Confirmar Pedido"
4. **Esperado**: Erro "⚠️ Endereço obrigatório para entrega"

### 8.5 Criar Pedido com Endereço
1. Preencha:
   - Nome: `João Silva`
   - Telefone: `11999999999`
   - Forma Recebimento: `Entrega`
   - Endereço: `Rua das Flores, 100, Apto 201, Vila Maria`
   - Forma Pagamento: `Pagar ao Entregar`
2. Clique: "Confirmar Pedido"
3. **Esperado**:
   - Mensagem: "✅ Pedido criado com sucesso!"
   - ID do pedido salvo em localStorage
   - Redirecionamento para acompanhamento

---

## 🧪 TESTE 9: CLIENTE - ACOMPANHAMENTO

### 9.1 Status do Pedido
1. Após criar pedido, você já está em acompanhamento
2. **Esperado**: Mostra:
   - ID do pedido
   - Dados do cliente
   - Endereço (se entrega)
   - Total
   - Status com emoji (📋 Recebido)
   - Itens

### 9.2 Atualização de Status
1. Abra em outra aba: `frontend/admin/pedidos.html`
2. Mude o status do seu pedido para "EM_PREPARO"
3. Volte à aba do cliente e atualize (F5)
4. **Esperado**: Status muda para "👨‍🍳 Em Preparo"

### 9.3 Outros Status
1. Continue atualizando no admin
2. Verifique cada status no cliente:
   - 📋 Recebido
   - 👨‍🍳 Em Preparo
   - ✅ Pronto para Retirada
   - 🚗 Saiu para Entrega
   - 🎉 Finalizado

### 9.4 Pesquisar por ID
1. Na página de acompanhamento, digite ID de outro pedido
2. Clique "Pesquisar"
3. **Esperado**: Carrega pedido solicitado

---

## 🧪 TESTE 10: RELATÓRIOS

### 10.1 Acessar Relatórios
1. Login como admin
2. Clique em "📈 Relatórios"
3. **Esperado**: Página com vários gráficos e tabelas

### 10.2 Estatísticas Gerais
1. Cards devem mostrar:
   - Total Vendido (Hoje)
   - Quantidade de Vendas
   - Ticket Médio
2. **Esperado**: Valores corretos

### 10.3 Filtro por Período
1. Selecione data de início
2. Selecione data de fim
3. Clique "Gerar Relatório"
4. **Esperado**: Tabelas atualizam

### 10.4 Vendas por Dia
1. Tabela deve mostrar:
   - Data
   - Quantidade de Vendas
   - Total (R$)
   - Ticket Médio
2. **Esperado**: Dados corretos para o período

### 10.5 Status do Estoque
1. Tabela deve mostrar:
   - Produto
   - Últimas Entradas
   - Últimas Saídas
   - Data Última Movimentação
2. **Esperado**: Dados sincronizados

### 10.6 Exportar CSV
1. Clique "Exportar CSV"
2. **Esperado**: Download de arquivo `relatorio.csv`
3. Abra no Excel ou similar
4. **Esperado**: Dados formatados corretamente

---

## 🧪 TESTE 11: INTEGRAÇÃO DESCONTO DE ESTOQUE

### 11.1 Verificar Quantidade Inicial
1. Admin → Estoque
2. Anote a quantidade de um produto (ex: Sorvete tem 20)

### 11.2 Cliente Compra
1. Cliente → Catálogo → Adiciona 3 Sorvetes ao carrinho
2. Finaliza pedido

### 11.3 Admin Confirma Desconto
1. Admin → Estoque
2. **Esperado**: Quantidade de Sorvete agora é 17 (20 - 3)

### 11.4 Movimento Registrado
1. Admin → Estoque → Últimas Movimentações
2. **Esperado**: SAIDA de 3 Sorvete registrada

---

## ✅ CHECKLIST FINAL

Marque com ✅ cada funcionalidade testada com sucesso:

### Cliente
- [ ] Catálogo carrega sem erros
- [ ] Busca filtra produtos
- [ ] Carrinho funciona (add/remove/qty)
- [ ] Campo endereço aparece para entrega
- [ ] Pedido criado com endereço
- [ ] Acompanhamento mostra status correto
- [ ] Status atualiza em tempo real

### Admin
- [ ] Login funciona (admin/admin123)
- [ ] Logout funciona
- [ ] Menu com 5 opções aparece
- [ ] Produtos listam e editam sem duplicar
- [ ] Estoque mostra alertas (baixo/falta)
- [ ] Movimento de estoque registra
- [ ] Pedidos listam e status atualiza
- [ ] Detalhes do pedido mostram endereço
- [ ] Relatórios carregam dados
- [ ] Exportar CSV funciona

### Caixa
- [ ] Login caixa funciona (caixa01/caixa123)
- [ ] Barra mostra informações do caixa
- [ ] Fechar caixa redireciona corretamente
- [ ] Dados aparecem corretamente

### Autenticação
- [ ] Admin não acessa sem login
- [ ] Caixa não acessa sem login
- [ ] Sessões são independentes
- [ ] sessionStorage salva/limpa corretamente

### APIs
- [ ] Nenhuma URL localhost
- [ ] Todas APIs usam HTTPS Render
- [ ] CORS funciona
- [ ] Erros são tratados com mensagens

---

## 🐛 SE ENCONTRAR BUGS

Se algo não funcionar como esperado:

1. **Abra o Console do Navegador** (F12 → Console)
2. **Procure por mensagens de erro** (vermelho)
3. **Copie o erro completo**
4. **Verifique a URL da API** (deve ser HTTPS Render)
5. **Tente recarregar a página** (Ctrl+F5)
6. **Verifique o sessionStorage** (F12 → Application → Storage)

---

## 🚀 PRÓXIMOS PASSOS

Após testar tudo com sucesso:

1. ✅ Fazer backup do código
2. ✅ Fazer deploy do backend se necessário
3. ✅ Fazer deploy do frontend (Vercel)
4. ✅ Testar em produção com dados reais
5. ✅ Coletar feedback dos usuários

---

## 📞 SUPORTE

Se tiver dúvidas:
- Verifique o arquivo `MELHORIAS_IMPLEMENTADAS.md`
- Verifique o arquivo `TESTE_FUNCIONALIDADES.md`
- Abra o console (F12) para ver erros
- Verifique se o backend está online: `https://gestfy-backend.onrender.com/api/pedidos`

---

**Status**: ✅ PRONTO PARA TESTE
**Data**: 11 de janeiro de 2026
**Versão**: 1.0 - Teste Completo

Bom teste! 🎉
