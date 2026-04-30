# ⚡ AÇÃO IMEDIATA - PRÓXIMOS 30 MINUTOS

## 🎯 Objetivo
Colocar o Caixa em produção no Render e Vercel.

---

## ✅ PASSO 1: Verificar o Build (2 min)

### Confirmação Local
```powershell
Get-ChildItem "c:\Users\amand\OneDrive\Área de Trabalho\ADS M5\gestfy\gestfy\backend\target\" -Filter "*.jar"

# Esperado: gestfy-0.0.1-SNAPSHOT.jar (49.42 MB) ✓
```

### Se não aparecer
```powershell
cd "c:\Users\amand\OneDrive\Área de Trabalho\ADS M5\gestfy\gestfy\backend"
.\mvnw.cmd clean package -DskipTests
# Aguarde 5-10 minutos
```

---

## ✅ PASSO 2: Git Push (3 min)

```bash
cd "c:\Users\amand\OneDrive\Área de Trabalho\ADS M5\gestfy\gestfy"

# Verificar mudanças
git status

# Adicionar tudo
git add .

# Commit
git commit -m "feat: Caixa integrado com NEON, pronto para produção"

# Push
git push origin main
```

**Resultado Esperado**: Push bem-sucedido para GitHub

---

## ✅ PASSO 3: Deploy Backend - Render (15 min)

### 3.1 Abrir Render Dashboard
- URL: https://dashboard.render.com
- Fazer login

### 3.2 Selecionar Web Service
- Clique no seu serviço: `gestfy-backend` (ou seu nome)

### 3.3 Ir para Settings (Engrenagem)
1. Scroll até "Build Command"
2. **Limpar** e digitar:
   ```bash
   cd backend && ./mvnw clean package -DskipTests
   ```
3. Scroll até "Start Command"
4. **Limpar** e digitar:
   ```bash
   java -jar backend/target/gestfy-0.0.1-SNAPSHOT.jar
   ```
5. **Salvar** (botão no rodapé)

### 3.4 Adicionar/Verificar Environment Variables
1. Em "Settings", procure por "Environment"
2. Clique **"Add Environment Variable"** para cada um:
   
   **Variável 1**:
   - Name: `DB_URL`
   - Value: `postgresql://seu_usuario:sua_senha@ep-seu-projeto.neon.tech/neon`
   
   **Variável 2**:
   - Name: `DB_USERNAME`
   - Value: `seu_usuario_neon`
   
   **Variável 3**:
   - Name: `DB_PASSWORD`
   - Value: `sua_senha_neon`

3. **Salvar** cada uma

### 3.5 Fazer Deploy
1. Clique **"Redeploy"** (ou "Deploy" se for primeira vez)
2. Você será redirecionado para a página de build
3. **Aguarde 10-15 minutos**
4. Quando terminar, deve dizer: **"Your service is live"** ✅

### 3.6 Copiar URL do Render
- Procure por: `https://gestfy-backend-xxxxx.onrender.com`
- **Copie esta URL** (você precisará dela próximo)

**Exemplo Real**:
```
https://gestfy-backend-xyzabc.onrender.com/api/caixa/status
```

---

## ✅ PASSO 4: Testar Backend (2 min)

### Teste 1: Status
```bash
# Copie e cole no navegador (ou curl):
https://sua-url-render.com/api/caixa/status

# Esperado: {"aberto": false, ...} ✓
```

### Teste 2: Buscar Produto
```bash
# Cole no navegador:
https://sua-url-render.com/api/caixa/buscar-produto?nome=a

# Esperado: {"sucesso": true, "total": X, "produtos": [...]} ✓
```

### Se der erro 502
- Ir para Render Dashboard
- Clique no seu serviço
- Abra aba "Logs"
- Procure por "ERROR" ou "Exception"
- Corrija o problema

---

## ✅ PASSO 5: Atualizar URLs Frontend (5 min)

### 5.1 Editar arquivo 1: `frontend/admin/js/caixa-novo.js`
1. Abrir arquivo em VS Code
2. Ir para linha **11** (pressione Ctrl+G)
3. Encontrar:
   ```javascript
   let API_BASE = 'https://gestfy-backend.onrender.com/api';
   ```
4. **Substituir** pelo seu domínio:
   ```javascript
   let API_BASE = 'https://sua-url-render-aqui.com/api';
   ```
5. **Salvar** (Ctrl+S)

### 5.2 Editar arquivo 2: `frontend/admin/js/produtos.js`
1. Abrir arquivo em VS Code
2. Ir para linha **1**
3. Encontrar:
   ```javascript
   let API_URL = "https://gestfy-backend.onrender.com/api/produtos";
   ```
4. **Substituir**:
   ```javascript
   let API_URL = "https://sua-url-render-aqui.com/api/produtos";
   ```
5. **Salvar** (Ctrl+S)

### 5.3 Git Push (redeploy automático do Vercel)
```bash
git add .
git commit -m "fix: Update API URLs to production Render domain"
git push origin main
```

**Resultado**: Vercel redeploy automático (~2-3 minutos) ✅

---

## ✅ PASSO 6: Testar Frontend (5 min)

### 6.1 Abrir Caixa
1. URL: `https://seu-vercel-domain.com/admin/caixa-novo.html`
2. Clique botão **"Abrir Caixa"**
3. **Esperado**: Mensagem "✓ Caixa aberto com sucesso!" ✅

### 6.2 Buscar Produto
1. Digite nome de um produto (ex: "sorvete")
2. Clique botão **"Buscar"** ou pressione Enter
3. **Esperado**: Lista de produtos aparece ✅

### 6.3 Registrar Venda
1. Clique em um produto da lista
2. Campo "Quantidade": coloque **2**
3. Campo "Valor Recebido": coloque **50**
4. Clique **"Confirmar Venda"**
5. **Esperado**: "✓ VENDA CONFIRMADA! 💰 Troco: R$ X.XX" ✅

### 6.4 Fechar Caixa
1. Clique botão **"Fechar Caixa"**
2. Confirmação: clique **OK**
3. **Esperado**: "✓ CAIXA FECHADO! 📊 Total de vendas: R$ X.XX | 📈 Quantidade: 1" ✅

---

## 🎉 SUCESSO!

Se chegou aqui, o Caixa está 100% funcionando em produção! 🚀

### Próximos Passos (Opcional)
- [ ] Adicionar autenticação
- [ ] Adicionar mais produtos ao banco
- [ ] Treinar usuários
- [ ] Monitorar performance

---

## 🚨 SE ALGO DER ERRADO

### 502 Bad Gateway
1. Render Dashboard → Seu serviço → "Logs"
2. Procure por "ERROR"
3. Leia a mensagem de erro
4. Se compilação falhou: verificar Build Command

### CORS Error no Console
1. F12 → Console → procure por "CORS"
2. Verificar que CaixaController tem `@CrossOrigin(origins = "*")`
3. Verificar que URL no frontend está correta

### Produto não busca
1. Verificar se há produtos no banco
2. Testar diretamente: `GET /api/caixa/buscar-produto?nome=a`

### Outro erro
1. Ler: **DEPLOYMENT_GUIDE.md** (seção Troubleshooting)
2. Ler: **CHECKLIST_PRODUCTION.md**

---

## ⏱️ Timeline

| Passo | Tempo | Status |
|-------|-------|--------|
| 1. Build ✓ | 2 min | Feito ✅ |
| 2. Git Push | 3 min | **👈 Faça agora** |
| 3. Deploy Render | 15 min | **👈 Faça agora** |
| 4. Testar Backend | 2 min | **👈 Faça agora** |
| 5. Update URLs | 5 min | **👈 Faça agora** |
| 6. Testar Frontend | 5 min | **👈 Faça agora** |
| **TOTAL** | **30 min** | ⏱️ |

---

## 📞 Ajuda Rápida

- **Backend não compila**: Ver DEPLOYMENT_GUIDE.md
- **502 Error**: Verificar Render Logs
- **CORS Error**: Verificar URL do frontend
- **Produto não aparece**: Testar endpoint `/caixa/buscar-produto?nome=a`

---

**🎯 Objetivo**: Caixa em produção em 30 minutos  
**✅ Status**: PRONTO  
**🚀 Vamos começar!**
