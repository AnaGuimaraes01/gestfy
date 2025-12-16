# 🚀 START HERE - COMECE POR AQUI

## ⏱️ 5 MINUTOS PARA COMEÇAR

### 1️⃣ Configurar o Banco de Dados

```bash
# Criar arquivo .env no backend/
DB_URL=jdbc:postgresql://localhost:5432/gestfy
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
```

### 2️⃣ Rodar o Backend

```bash
cd backend
./mvnw spring-boot:run
```

✅ Quando ver: `Started GestfyApplication`  
🌐 Teste: http://localhost:8080/api/produtos

### 3️⃣ Abrir o Frontend

**Opção A** (Recomendado):
```bash
cd frontend
python -m http.server 3000
```

**Opção B** (Sem servidor):
- Abra no navegador: `file:///C:/Users/amand/OneDrive/Área de Trabalho/ADS M4/GESTFY/gestfy/frontend/cliente/index.html`

### 4️⃣ Acessar o Sistema

**Para Clientes**:
```
http://localhost:3000/cliente/index.html
```

**Para Dono (Admin)**:
```
http://localhost:3000/admin/index.html
```

---

## 📚 DOCUMENTAÇÃO DISPONÍVEL

### Leia Nesta Ordem

1. **Este arquivo** (5 min) - Rápido start
2. **SUMARIO_FINAL.md** (10 min) - Visão geral do projeto
3. **IMPLEMENTACAO_COMPLETA.md** (20 min) - Detalhes técnicos
4. **GUIA_TESTE.md** (15 min) - Como testar
5. **ARQUIVOS_CRIADOS_MODIFICADOS.md** (10 min) - O que foi feito
6. **README_FINAL.md** (15 min) - Funcionalidades completas

---

## 🧪 TESTE RÁPIDO (2 MINUTOS)

### Como Cliente
1. Abra: http://localhost:3000/cliente/catalogo.html
2. Clique em um produto → "➕ Adicionar"
3. Vá ao carrinho → "💳 Finalizar Pedido"
4. Preencha dados
5. Clique "✅ Confirmar Pedido"
6. Acompanhe o pedido em tempo real

### Como Dono
1. Abra: http://localhost:3000/admin/pedidos.html
2. Veja o pedido que acabou de criar
3. Clique "Alterar Status" → "EM_PREPARO"
4. Mude para "PRONTO_RETIRADA"
5. Mude para "FINALIZADO"
6. Vá em Relatórios → veja o pedido registrado

---

## ✨ FUNCIONALIDADES PRINCIPAIS

### Cliente
- 🛒 Catálogo com busca
- 🛍️ Carrinho de compras
- 📦 Finalizar pedido
- 📍 Acompanhar pedido em tempo real
- 📋 Histórico de pedidos

### Dono
- 📊 Dashboard com visão geral
- 🛍️ Gerenciar produtos
- 📦 Controlar pedidos
- 📦 Controlar estoque
- 💰 Caixa diário
- 📈 Relatórios e análises

---

## 🐛 PROBLEMAS COMUNS

### Backend não conecta
```bash
# Verifique PostgreSQL
psql -U seu_usuario -d gestfy

# Teste a porta
netstat -ano | findstr :5432
```

### Frontend não carrega
```bash
# Abra o console (F12)
# Verifique se backend está rodando
# Teste: http://localhost:8080/api/produtos
```

### Estoque não desconta
```bash
# Verifique admin/estoque.html
# Procure por movimento SAIDA
# Deve ter quantidade = quantidade do pedido
```

---

## 📞 PRECISA DE AJUDA?

1. **Console do Navegador** (F12 → Console)
   - Veja erros de JavaScript
   - Verifique respostas da API

2. **Network Tab** (F12 → Network)
   - Veja requisições HTTP
   - Verifique status codes

3. **Logs do Backend**
   - Veja erros de compilação
   - Verifique queries SQL

4. **Documentação**
   - Leia GUIA_TESTE.md
   - Leia IMPLEMENTACAO_COMPLETA.md

---

## 🎯 PRÓXIMOS PASSOS

1. ✅ Backend rodando
2. ✅ Frontend acessível
3. ✅ Teste como cliente
4. ✅ Teste como dono
5. ✅ Crie alguns pedidos
6. ✅ Teste alteração de status
7. ✅ Veja relatórios

---

## ✅ CHECKLIST

- [ ] Backend compilando sem erros
- [ ] PostgreSQL conectado
- [ ] Frontend acessível
- [ ] Produto carregando na busca
- [ ] Carrinho funcionando
- [ ] Pedido sendo criado
- [ ] Status alterando sem erros
- [ ] Estoque descountando
- [ ] Relatório mostrando dados

---

## 🎉 SUCESSO!

Se chegou aqui, seu **Gestfy** está funcionando! 🎊

Parabéns! O sistema está pronto para:
- ✅ Ser testado
- ✅ Ser customizado
- ✅ Ir para produção

---

**Dúvidas?** Consulte os arquivos de documentação.  
**Precisando de mais?** Veja IMPLEMENTACAO_COMPLETA.md

**Gestfy v1.0 - Dezembro de 2025** 🚀
