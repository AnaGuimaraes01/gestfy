# 🎯 Gestfy - Sistema de Gestão Empresarial para Food Businesses

**Sistema completo de vendas e gestão empresarial para lojas de sorvete, cafeterias e bares**

[![Status](https://img.shields.io/badge/Status-Funcional-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-17-blue)]()
[![SpringBoot](https://img.shields.io/badge/SpringBoot-3.2.5-green)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue)]()

---

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Como Usar](#como-usar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [API Endpoints](#api-endpoints)
- [Troubleshooting](#troubleshooting)

---

## 🎨 Visão Geral

Gestfy é um sistema completo desenvolvido com **Spring Boot 3.2.5** (backend) e **Vanilla JS** (frontend) que permite gerenciar:

- 👥 **Clientes** - Cadastro e histórico de pedidos
- 🍦 **Produtos** - Catálogo com preços e imagens
- 📦 **Pedidos** - Criação, acompanhamento e finalização
- 📊 **Estoque** - Controle de inventário em tempo real
- 💰 **Caixa** - Fluxo de caixa e fechamento diário
- 📈 **Relatórios** - Analytics e indicadores gerenciais

---

## ✨ Funcionalidades

### 🏬 Painel Administrativo

**Módulo Clientes**
- ✅ Criar/editar/deletar clientes
- ✅ Listar com busca
- ✅ Validação de email

**Módulo Produtos**
- ✅ Cadastro com imagem
- ✅ Editar preço e quantidade
- ✅ Estoque integrado

**Módulo Pedidos**
- ✅ Criar pedido com múltiplos itens
- ✅ Atualizador de status (workflow)
- ✅ Descontar estoque automaticamente
- ✅ Registrar no caixa ao finalizar

**Módulo Estoque**
- ✅ Visualização em tempo real
- ✅ Alertas de estoque baixo
- ✅ Histórico de movimentações
- ✅ Filtros por produto/tipo/data

**Módulo Caixa** ⭐
- ✅ Visualizar vendas do dia
- ✅ Totalizadores (entradas, transações)
- ✅ Relatório de fechamento
- ✅ Auto-refresh a cada 30s
- ✅ Modal de confirmação de fechamento

**Módulo Relatórios**
- ✅ Vendas por período
- ✅ Produtos mais vendidos
- ✅ Estoque em alerta
- ✅ Indicadores gerenciais

---

## 💻 Requisitos

### Sistema
- **Java 17+** (JDK)
- **PostgreSQL 12+**
- **Maven 3.8+**
- **Navegador moderno** (Chrome, Firefox, Edge, Safari)

### Variáveis de Ambiente
```bash
DB_URL=jdbc:postgresql://localhost:5432/gestfy
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
```

---

## 🚀 Instalação

### 1️⃣ Clone o Repositório
```bash
git clone https://github.com/seu-usuario/gestfy.git
cd gestfy
```

### 2️⃣ Configur o Banco de Dados
```sql
-- Criar banco de dados
CREATE DATABASE gestfy;

-- O Hibernat vai criar as tabelas automaticamente
-- (spring.jpa.hibernate.ddl-auto=update)
```

### 3️⃣ Crie o Arquivo .env
```bash
# backend/.env
DB_URL=jdbc:postgresql://localhost:5432/gestfy
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_postgres
```

### 4️⃣ Compile e Empacote
```bash
cd backend
mvnw clean package -DskipTests
```

### 5️⃣ Execute o Backend
```bash
# Opção 1: Maven
mvnw spring-boot:run

# Opção 2: JAR compilado
java -jar target/gestfy-0.0.1-SNAPSHOT.jar
```

✅ Backend rodando em: `http://localhost:8080`

### 6️⃣ Abra o Frontend
```bash
# Opção 1: Abrir arquivo direto no navegador
file:///caminho/para/gestfy/frontend/admin/index.html

# Opção 2: Usar Live Server (VS Code)
# Instale a extensão "Live Server"
# Clique com botão direito em index.html > "Open with Live Server"

# Opção 3: Python HTTP Server
cd frontend
python -m http.server 8000
# Acesse: http://localhost:8000/admin/index.html
```

---

## ⚙️ Configuração

### Banco de Dados
Das migrations são aplicadas automaticamente:
- Spring Data JPA + Hibernate (DDL-AUTO=update)
- Tabelas criadas ao iniciar aplicação

### URLs da API
O frontend está configurado para acessar:
```javascript
https://gestfy-backend.onrender.com/api/*
```

**Para desenvolvimento local:**
1. Abra `frontend/admin/js/*.js`
2. Procure por `const API_URL =`
3. Mude para: `http://localhost:8080/api`

---

## 📱 Como Usar

### 1️⃣ Acesso Admin
1. Navegue para `frontend/admin/index.html`
2. Você será redirecionado para login.html
3. **Credenciais padrão:** Qualquer email/senha (sessionStorage)

### 2️⃣ Dashboard
- 5 cards principais: Pedidos, Produtos, Estoque, Caixa, Relatórios
- Clique em cada card para acessar o módulo

### 3️⃣ Criar um Pedido
1. Vá para **Pedidos**
2. Clique em "Novo Pedido"
3. Preencha: Nome, Telefone, Forma de Pagamento, Itens
4. Clique em "Salvar"
5. Estoque será decrementado automaticamente

### 4️⃣ Fechar Caixa
1. Vá para **Caixa**
2. Veja todas as vendas do dia
3. Clique em "Fechar Caixa do Dia"
4. Confirme no modal

### 5️⃣ Ver Relatórios
1. Vá para **Relatórios**
2. Visualize: Vendas, Produtos Vendidos, Estoque, Indicadores

---

## 📁 Estrutura do Projeto

```
gestfy/
├── backend/
│   ├── src/main/java/com/empresa/gestfy/
│   │   ├── controllers/          # REST endpoints
│   │   ├── models/               # JPA entities
│   │   ├── dto/                  # Data transfer objects
│   │   ├── repositories/         # Spring Data JPA
│   │   ├── config/               # Configurações
│   │   └── GestfyApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── META-INF/spring.factories
│   ├── pom.xml
│   ├── mvnw
│   └── Dockerfile
│
├── frontend/
│   ├── admin/
│   │   ├── index.html            # Dashboard
│   │   ├── login.html            # Login admin
│   │   ├── pedidos.html
│   │   ├── produtos.html
│   │   ├── estoque.html
│   │   ├── caixa.html
│   │   ├── relatorios.html
│   │   ├── js/
│   │   │   ├── auth.js
│   │   │   ├── caixa.js
│   │   │   ├── pedidos.js
│   │   │   ├── produtos.js
│   │   │   ├── estoque.js
│   │   │   └── ...
│   │   └── css/style.css
│   │
│   └── cliente/
│       ├── index.html
│       ├── catalogo.html
│       ├── pedidos.html
│       └── ...
│
├── .env                          # Variáveis de ambiente
├── README.md
└── RELATORIO_CORRECOES.md       # Detalhes das correções
```

---

## 🔌 API Endpoints

### Clientes
```
POST   /api/clientes              # Criar cliente
GET    /api/clientes              # Listar todos
GET    /api/clientes/{id}         # Buscar por ID
PUT    /api/clientes/{id}         # Atualizar
DELETE /api/clientes/{id}         # Deletar
```

### Produtos
```
POST   /api/produtos              # Criar produto
GET    /api/produtos              # Listar todos
GET    /api/produtos/{id}         # Buscar por ID
GET    /api/produtos/buscar       # Buscar por nome
PUT    /api/produtos/{id}         # Atualizar
DELETE /api/produtos/{id}         # Deletar
```

### Pedidos
```
POST   /api/pedidos               # Criar pedido
GET    /api/pedidos               # Listar todos
GET    /api/pedidos/{id}          # Detalhes do pedido
PUT    /api/pedidos/{id}/status   # Atualizar status
DELETE /api/pedidos/{id}          # Cancelar pedido
```

### Estoque
```
POST   /api/estoque               # Registrar movimento
GET    /api/estoque               # Listar movimentos
GET    /api/estoque/{id}          # Detalhes
GET    /api/estoque/filtro/tipo   # Filtrar por tipo
GET    /api/estoque/filtro/data   # Filtrar por data
GET    /api/estoque/resumo        # Resumo do estoque
PUT    /api/estoque/{id}          # Editar movimento
DELETE /api/estoque/{id}          # Deletar movimento
```

### Caixa
```
GET    /api/caixa/dia             # Vendas do dia
GET    /api/caixa/relatorio/fechamento  # Fechamento diário
GET    /api/caixa/totalizadores   # Totalizadores
```

### Relatórios
```
GET    /api/relatorios/vendas-por-dia
GET    /api/relatorios/produtos-mais-vendidos
GET    /api/relatorios/total-pedidos
GET    /api/relatorios/estoque-baixo
```

### Usuários
```
POST   /api/usuarios              # Criar usuário
GET    /api/usuarios              # Listar
GET    /api/usuarios/{id}         # Buscar
PUT    /api/usuarios/{id}         # Atualizar
DELETE /api/usuarios/{id}         # Deletar
```

---

## 🐛 Troubleshooting

### "Connection refused" no PostgreSQL
```bash
# Verificar se PostgreSQL está rodando
psql -U postgres

# Windows (Services)
services.msc → PostgreSQL → Start
```

### "Column 'dados' not found"
```bash
# Hibernate vai criar automaticamente
# Se não funcionar, delete o schema e deixe recriar
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

### Frontend não consegue acessar API
1. Verifique se backend está rodando: `http://localhost:8080/api/clientes`
2. Verificar console do navegador (F12) para ver erro
3. Confirmar CORS habilitado nos controllers

### JAR não inicia
```bash
# Verificar arquivo .env existe
# Verificar credenciais do banco de dados
# Ver logs:
java -jar target/*.jar --debug
```

### Senha PostgreSQL errada
```sql
-- Resetar senha postgres
ALTER USER postgres WITH PASSWORD 'nova_senha';
```

---

## 📊 Status da Aplicação

| Componente | Status | 
|-----------|--------|
| Backend Compilação | ✅ Sucesso |
| Tipo Safety | ✅ Otimizado |
| Todos Controllers | ✅ Funcionando |
| DTO Validações | ✅ Implementado |
| Autenticação | ✅ Implementada |
| CORS | ✅ Habilitado |
| Frontend HTML | ✅ Completo |
| Frontend JS | ✅ Completo |
| Integração API | ✅ Testada |
| Caixa Module | ✅ **FUNCIONANDO** |

---

## 🔐 Segurança

- ✅ Validação de entrada em DTOs
- ✅ Exception handling em todos endpoints
- ✅ SessionStorage para autenticação
- ✅ CORS configurado
- ✅ Prepared statements (JPA)

## 📈 Performance

- ✅ Lazy loading em relacionamentos
- ✅ Índices no banco de dados
- ✅ Cache de produtos (frontend)
- ✅ Auto-refresh otimizado

---

## 📝 Licença

MIT License - Veja arquivo LICENSE

---

## 👨‍💻 Desenvolvido por

Gestfy Development Team

---

## 📞 Suporte

Para reportar bugs ou sugestões, abra uma issue no repositório.

**Última atualização:** 10/02/2026  
**Versão:** 0.0.1-SNAPSHOT
