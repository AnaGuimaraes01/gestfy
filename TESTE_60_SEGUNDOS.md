# ⚡ TESTE RÁPIDO EM 60 SEGUNDOS

## 🚀 Passo 1: Abrir Terminal (5 seg)
```
Pasta: backend
Comando: mvnw spring-boot:run
Aguardar: "Started GestfyApplication"
```

## 🎯 Passo 2: Thunder Client - PRODUTO (10 seg)

**POST** → Criar:
```
URL: http://localhost:8080/api/produtos
{
  "nome": "Sorvete",
  "descricao": "Gelado",
  "preco": 15.00,
  "urlFoto": "https://via.placeholder.com/300"
}
```
✅ Retorna ID 1

**GET** → Listar:
```
URL: http://localhost:8080/api/produtos
```
✅ Retorna array com 1 produto

## 🎯 Passo 3: Interface - CLIENTE (15 seg)

Abrir: `frontend/cliente/catalogo.html`
✅ Vê sorvete com imagem (200px)
Clica: "Adicionar"
Abre: `frontend/cliente/carrinho.html`
✅ Produto está lá

## 🎯 Passo 4: Interface - PEDIDO (15 seg)

Abre: `frontend/cliente/pedido.html`
Preenche: Nome do cliente
Clica: "Confirmar Pedido"
✅ Pedido criado

## 🎯 Passo 5: Interface - ESTOQUE (10 seg)

Abre: `frontend/admin/estoque.html`
✅ Vê tabela com movimentações

## 🎯 Passo 6: Interface - ADMIN FINALIZA (5 seg)

Abre: `frontend/admin/pedidos.html`
Finaliza: Clica em botão finalizar
✅ Estoque atualiza automaticamente

## ✅ RESULTADO

- ✅ Produto criado
- ✅ Produto visível em 2 lugares
- ✅ Pedido criado
- ✅ Estoque registrou

**SISTEMA 100% FUNCIONAL**
