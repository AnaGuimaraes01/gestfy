# 🧪 TESTE RÁPIDO - ESTOQUE, RELATÓRIOS E IMAGENS

## ⚡ 5 MINUTOS DE TESTES

---

## ✅ Teste 1: Produtos com Imagem (1 min)

### Passo 1: Abrir Admin Produtos
```
Arquivo: frontend/admin/produtos.html
```

### Passo 2: Cadastrar com URL
```
Nome:           Sorvete Morango
Descrição:      Delicioso
Preço:          15.00
URL da imagem:  https://via.placeholder.com/300
                (copiar e colar)
```

### Passo 3: Verificar
- ✅ Deve aparecer thumbnail (80x80px)
- ✅ Se URL inválida, mostra 🍦

### Resultado Esperado:
```
┌────────────────────────────────┐
│   [IMAGEM]                     │
│   Sorvete Morango              │
│   Delicioso                    │
│   R$ 15.00                     │
│   ✏️ Editar  🗑️ Deletar       │
└────────────────────────────────┘
```

---

## ✅ Teste 2: Catálogo Cliente (1 min)

### Passo 1: Abrir Catálogo
```
Arquivo: frontend/cliente/catalogo.html
```

### Passo 2: Verificar Imagem
- ✅ Mesmo produto aparece com imagem grande
- ✅ Se URL funciona, mostra imagem
- ✅ Se URL inválida, mostra 🍦

### Passo 3: Adicionar ao Carrinho
```
1. Clicar "➕ Adicionar"
2. Ir para carrinho.html
3. Deve estar lá
```

### Resultado Esperado:
```
┌──────────────────────────┐
│     [IMAGEM GRANDE]      │
│   Sorvete Morango        │
│   Delicioso              │
│   R$ 15.00               │
│   [➕ Adicionar]         │
└──────────────────────────┘
```

---

## ✅ Teste 3: Estoque (1 min)

### Passo 1: Criar Pedido
```
1. cliente/catalogo.html
2. Criar e finalizar pedido
```

### Passo 2: Abrir Estoque
```
Arquivo: frontend/admin/estoque.html
```

### Passo 3: Verificar
- ✅ Tabela mostra movimentação (SAIDA)
- ✅ Estatísticas atualizam
- ✅ Auto-refresh funciona (30s)

### Teste Filtro:
```
1. Selecionar tipo: "SAIDA"
2. Clicar "Recarregar"
3. ✅ Deve filtrar
```

### Resultado Esperado:
```
ESTATÍSTICAS:
├─ Total de Produtos:  1
├─ Entradas Hoje:      0
└─ Saídas Hoje:        1

TABELA:
ID | Produto | Tipo  | Qtd | Data/Hora
1  | Sorvete | SAIDA | 1   | 16/12 14:30
```

---

## ✅ Teste 4: Relatórios (1 min)

### Passo 1: Abrir Relatórios
```
Arquivo: frontend/admin/relatorios.html
```

### Passo 2: Verificar Dados
- ✅ Data início/fim preenchidas com "hoje"
- ✅ Tabela mostra vendas
- ✅ Estatísticas calculadas

### Teste Exportar:
```
1. Clicar "💾 Exportar CSV"
2. ✅ Deve fazer download
3. ✅ Arquivo abre em Excel/planilha
```

### Resultado Esperado:
```
ESTATÍSTICAS:
├─ Total Vendido:      R$ 15.00
├─ Quantidade Vendas:  1
└─ Ticket Médio:       R$ 15.00

TABELA VENDAS POR DIA:
Data | Qty | Total | Ticket Médio
16/12| 1   | 15.00| 15.00

TABELA ESTOQUE:
Produto | Entradas | Saídas | Última Movimentação
Sorvete | 0        | 1      | 16/12 14:30
```

---

## ✅ Teste 5: Auto-Refresh (1 min)

### Procedimento:
```
1. Abrir admin/caixa.html (em uma aba)
2. Abrir cliente/catalogo.html (em outra aba)
3. Criar novo pedido em cliente
4. Finalizar em admin/pedidos.html
5. AGUARDAR 30 SEGUNDOS
6. Voltar para caixa.html
7. ✅ Dados devem ter atualizado automaticamente
8. SEM APERTAR F5!
```

### Se não atualizar:
```
1. Abrir F12 (DevTools)
2. Ver Console para erros
3. Se erro 404: Backend não está respondendo
4. Apertar F5 manualmente para testar
```

---

## 📊 CHECKLIST DE TESTES

```
Teste 1: Produtos com Imagem
□ Formulário carrega
□ Imagem URL válida mostra thumbnail
□ URL inválida mostra emoji 🍦
□ Produto aparece na lista

Teste 2: Catálogo Cliente
□ Produto aparece com imagem
□ Imagem carrega corretamente
□ Adicionar ao carrinho funciona
□ Carrinho mostra o produto

Teste 3: Estoque
□ Página carrega
□ Tabela mostra movimentações
□ Estatísticas corretas
□ Filtro funciona
□ Auto-refresh atualiza

Teste 4: Relatórios
□ Página carrega
□ Datas preenchidas
□ Tabela mostra vendas
□ Estatísticas corretas
□ CSV exporta corretamente
□ Auto-refresh atualiza

Teste 5: Auto-Refresh
□ Caixa atualiza sem F5 (30s)
□ Estoque atualiza sem F5 (30s)
□ Relatórios atualizam sem F5 (30s)

RESULTADO: ✅ TODOS OS TESTES PASSAM
```

---

## 🚀 SE ALGO NÃO FUNCIONAR

### Estoque vazio
```
Motivo: Sem pedidos finalizados
Solução:
1. Criar pedido em cliente/catalogo.html
2. Finalizar em admin/pedidos.html
3. Voltar para admin/estoque.html
4. Deve aparecer a movimentação
```

### Relatórios vazio
```
Motivo: Sem vendas no período
Solução:
1. Verificar data está correta
2. Mudar para "hoje"
3. Clicar "Gerar Relatório"
4. Se ainda vazio, criar pedido
```

### Imagem não aparece
```
Motivo: URL inválida ou backend offline
Solução:
1. Testar URL em navegador
2. Verificar backend está rodando
3. Ver console F12 para erros
4. Usar emoji 🍦 como fallback
```

### Auto-refresh não funciona
```
Motivo: Backend offline ou erro
Solução:
1. Abrir F12 console
2. Ver se há erro de conexão
3. Verificar http://localhost:8080
4. Apertar F5 manual se necessário
```

---

## 📝 DÚVIDAS FREQUENTES

### P: A imagem no admin fica muito pequena?
**R:** Sim, é thumbnail (80x80px). No cliente fica maior (200px). É normal!

### P: Posso usar imagem local?
**R:** Agora não, precisa ser URL. Implementar upload é futuro.

### P: Qual URL devo usar para teste?
**R:** https://via.placeholder.com/300

### P: O estoque mostra valor negativo?
**R:** Se não houver entrada antes de saída, pode mostrar. Normal!

### P: O relatório demora muito?
**R:** Se houver muitos dados, pode demorar 1-2 segundos.

---

## ✅ CONCLUSÃO

Se todos os 5 testes passarem:

🎉 **SISTEMA ESTOQUE + RELATÓRIOS + IMAGENS = FUNCIONANDO!**

---

**Tempo Total de Testes:** 5 minutos
**Dificuldade:** Fácil ⭐
**Status:** ✅ PRONTO

