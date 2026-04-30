# 🚀 GUIA DE DEPLOYMENT - GESTFY (COMPLETE)

## Visão Geral
- **Backend**: Spring Boot 3.2.5 + Java 17 + PostgreSQL (NEON)
- **Frontend Admin**: Vercel (Vanilla JS)
- **Frontend Cliente**: Vercel (Vanilla JS)
- **Database**: NEON PostgreSQL
- **Production URL**: https://gestfy-backend.onrender.com/api

---

## 1️⃣ PREPARAÇÃO LOCAL

### Requisitos
- Java 17+
- Maven 3.8.1+
- PostgreSQL (local ou NEON)
- Node.js (para Vercel CLI - opcional)

### Clone e Configuração
```bash
git clone <seu-repo>
cd gestfy/backend

# Criar arquivo .env com credenciais NEON
cp .env.example .env
# Editar .env com suas credenciais NEON
```

### Teste Local
```bash
cd backend
./mvnw clean compile
./mvnw spring-boot:run
# Acesse: http://localhost:8080/api/caixa/status
```

---

## 2️⃣ BUILD PARA PRODUÇÃO

### Gerar JAR
```bash
cd backend
./mvnw clean package -DskipTests
# Arquivo gerado: target/gestfy-0.0.1-SNAPSHOT.jar
```

### Validar Build
```bash
# Verificar se não há erros
jar -tf target/gestfy-0.0.1-SNAPSHOT.jar | grep CaixaController
jar -tf target/gestfy-0.0.1-SNAPSHOT.jar | grep CaixaService
```

---

## 3️⃣ DEPLOYMENT NO RENDER

### Passo 1: Conectar GitHub (se ainda não estiver)
1. Vá para https://dashboard.render.com
2. Clique em "Connect Git Repository"
3. Autorize e selecione seu repositório

### Passo 2: Criar/Atualizar Web Service
1. **Clique**: "New +" → "Web Service"
2. **Selecione** seu repositório
3. **Configure**:
   - **Name**: gestfy-backend (ou seu nome)
   - **Runtime**: Java 17
   - **Build Command**: 
     ```bash
     cd backend && ./mvnw clean package -DskipTests
     ```
   - **Start Command**: 
     ```bash
     java -jar backend/target/gestfy-0.0.1-SNAPSHOT.jar
     ```
   - **Plan**: Starter (grátis)

### Passo 3: Adicionar Variáveis de Ambiente
1. Em "Environment", clique em "Add Environment Variable"
2. Adicione as seguintes:
   ```
   DB_URL=postgresql://user:password@ep-seu-projeto.neon.tech/neon
   DB_USERNAME=seu_usuario
   DB_PASSWORD=sua_senha
   ```

### Passo 4: Deploy
1. Clique em **"Deploy"**
2. Aguarde o build completar (10-15 minutos)
3. Acesse: https://seu-dominio-render.com/api/caixa/status
4. Esperado: `{"aberto": false, "mensagem": "..."}` ✓

---

## 4️⃣ VERIFICAR ENDPOINTS

### Testar Caixa no Render
```bash
# Status do caixa
curl https://seu-dominio-render.com/api/caixa/status

# Buscar produto
curl "https://seu-dominio-render.com/api/caixa/buscar-produto?nome=sorvete"

# Abrir caixa
curl -X POST https://seu-dominio-render.com/api/caixa/abrir \
  -H "Content-Type: application/json"
```

### Esperado
- ✅ 200 OK com JSON response
- ✅ Sem erro 502 Bad Gateway
- ✅ Sem erro CORS

---

## 5️⃣ FRONTEND - UPDATE URLs (IMPORTANTE!)

Se ainda não fez, atualize as URLs no Vercel:

### Em `frontend/admin/js/caixa-novo.js` (linha ~11):
```javascript
// MUDE DE:
let API_BASE = 'https://gestfy-backend.onrender.com/api';

// PARA SEU DOMÍNIO DO RENDER:
let API_BASE = 'https://seu-novo-dominio-render.com/api';
```

### Em `frontend/admin/js/produtos.js` (linha ~1):
```javascript
// Mesma atualização
let API_URL = "https://seu-novo-dominio-render.com/api/produtos";
```

### Faça o redeploy no Vercel
```bash
git push origin main
# O Vercel fará redeploy automaticamente
```

---

## 6️⃣ TROUBLESHOOTING

### ❌ Error 502 Bad Gateway
- **Causa**: Backend está offline ou não respondendo
- **Solução**: 
  ```bash
  # Verificar status no Render
  # Menu → Logs
  # Procure por erros de compilation ou runtime
  ```

### ❌ CORS Error no Console
- **Causa**: Backend sem @CrossOrigin
- **Solução**: Verificar se `CaixaController` tem `@CrossOrigin(origins = "*")`

### ❌ Caixa não abre
- **Causa**: Tabela `caixa` não existe no NEON
- **Solução**: 
  1. Rodar uma vez localmente para criar tabela (hibernate ddl-auto=update)
  2. Ou criar manualmente:
  ```sql
  CREATE TABLE caixa (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(50),
    saldo DOUBLE PRECISION,
    descricao VARCHAR(255),
    data DATE,
    horario_abertura TIMESTAMP,
    horario_fechamento TIMESTAMP,
    status VARCHAR(50),
    observacoes TEXT
  );
  ```

### ❌ Produto não aparece na busca
- **Causa**: 
  1. Não há produtos no banco
  2. Nome do produto não está correspondendo
- **Solução**: 
  ```bash
  # Testar endpoint diretamente
  curl "https://seu-dominio/api/caixa/buscar-produto?nome=a"
  # Deve retornar JSON com products
  ```

---

## 7️⃣ CHECKLIST PRÉ-DEPLOYMENT

- [ ] Backend compilado sem erros: `mvnw clean compile`
- [ ] `.env` configurado com credenciais NEON
- [ ] Render Web Service criado e vinculado
- [ ] Variáveis de ambiente no Render: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- [ ] Build Command: `cd backend && ./mvnw clean package -DskipTests`
- [ ] Start Command: `java -jar backend/target/gestfy-0.0.1-SNAPSHOT.jar`
- [ ] Frontend URL atualizada para Render
- [ ] Vercel redeploy feito
- [ ] Teste `/api/caixa/status` ✓
- [ ] Teste `/api/caixa/buscar-produto?nome=teste` ✓
- [ ] Abrir Caixa no frontend ✓

---

## 8️⃣ ÚTEIS

### Ver Logs do Render
1. Dashboard → Seu Web Service
2. "Logs" na aba superior
3. Procure por ERROR ou Exception

### Rebuild sem alterar código
1. Dashboard → Seu Web Service
2. Clique "..." → "Redeploy"

### Limpar Database
⚠️ **CUIDADO - PERDERÁ DADOS**
```sql
DROP TABLE caixa;
DROP TABLE estoque;
DROP TABLE pedido_item;
DROP TABLE pedido;
DROP TABLE produto;
DROP TABLE cliente;
-- Re-rodar backend para criar tabelas novamente
```

---

## 9️⃣ SUPPORT

Para dúvidas:
1. Verificar logs em `Dashboard → Logs`
2. Testar endpoints com `curl` ou Postman
3. Verificar console do navegador (DevTools → Console)
4. Procurar por "CORS Error" ou "Failed to fetch"

---

**Última Atualização**: Jan 2025
**Status**: ✅ PRODUCTION READY
