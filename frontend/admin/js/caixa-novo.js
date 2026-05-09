/**
 * CAIXA SIMPLES - JavaScript
 * Sistema de vendas em dinheiro para lanchonete/sorveteria
 * 
 * API: https://gestfy-backend.onrender.com/api/caixa
 * Para desenvolvimento: http://localhost:8080/api/caixa
 */

// PADRÃO: usar produção
let API_BASE = 'https://gestfy-backend.onrender.com/api/caixa';
let caixaAberto = false;
let caixaId = null;
let produtoSelecionado = null;
let vendas = [];
let carrinhoItens = []; // Novo: carrinho de itens

// ============================================
// DETECTAR AMBIENTE (localhost vs produção)
// ============================================
(async function detectApi() {
    // Se não está em localhost, usa produção
    if (!window.location.hostname.includes('localhost') && !window.location.hostname.includes('127.0.0.1')) {
        API_BASE = 'https://gestfy-backend.onrender.com/api/caixa';
        return;
    }

    // Se é localhost, tenta conectar ao backend local
    try {
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 300);
        const res = await fetch('http://localhost:8080/api/caixa/status', { signal: controller.signal });
        clearTimeout(timeout);
        if (res && res.ok) {
            API_BASE = 'http://localhost:8080/api/caixa';
        }
    } catch (e) {
        // Mantém produção como fallback
        API_BASE = 'https://gestfy-backend.onrender.com/api/caixa';
    }
})();

// ============================================
// INICIALIZAÇÃO
// ============================================
document.addEventListener('DOMContentLoaded', () => {
    // Ao carregar a página, verifica o status do caixa
    verificarStatusCaixa();
    
    // Auto-buscar quando digita no campo
    const busca = document.getElementById('buscaProduto');
    let debounce;
    busca.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') buscarProduto();
    });
    // Debounce na busca para melhorar UX
    busca.addEventListener('input', (e) => {
        clearTimeout(debounce);
        const val = e.target.value.trim();
        if (val.length < 2) {
            document.getElementById('produtosEncontrados').innerHTML = '';
            document.getElementById('produtosEncontrados').classList.remove('ativo');
            return;
        }
        debounce = setTimeout(() => buscarProduto(), 300);
    });

    // Permitir confirmar venda com Enter no campo de valorRecebido
    document.getElementById('valorRecebido').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') confirmarVenda();
    });
});

// ============================================
// 1. ABRIR CAIXA
// ============================================
async function abrirCaixa() {
    try {
        const response = await fetch(`${API_BASE}/abrir`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        // Se falhar a requisição
        if (!response.ok) {
            console.error('Erro HTTP:', response.status);
            if (response.status === 500) {
                exibirMensagem('Erro no servidor. Tente novamente.', 'erro');
            } else {
                exibirMensagem('Erro ao abrir caixa', 'erro');
            }
            return;
        }

        const data = await response.json();

        // Validar resposta
        if (!data || typeof data.sucesso === 'undefined') {
            console.error('Resposta inválida:', data);
            exibirMensagem('Resposta inválida do servidor', 'erro');
            return;
        }

        // Sempre vai ser sucesso=true agora
        if (data.sucesso === true) {
            caixaAberto = true;
            caixaId = data.caixaId;

            // Atualizar interface
            document.getElementById('btnAbrirCaixa').disabled = true;
            document.getElementById('btnFecharCaixa').disabled = false;
            document.getElementById('secaoVenda').style.display = 'block';
            document.getElementById('statusBox').classList.remove('fechado');
            document.getElementById('statusBox').classList.add('aberto');
            document.getElementById('statusText').textContent = 'ABERTO ✓';

            // Mensagem diferente se já estava aberto
            if (data.jaAberto) {
                exibirMensagem('✓ Caixa já estava aberto. Carregando...', 'info');
            } else {
                exibirMensagem('✓ Caixa aberto com sucesso!', 'sucesso');
            }

            atualizarTotalizadores();
        } else {
            exibirMensagem(data.erro || 'Erro ao abrir caixa', 'erro');
        }

    } catch (erro) {
        console.error('Erro ao abrir caixa:', erro);
        exibirMensagem('Erro ao conectar com o servidor. Verifique sua conexão.', 'erro');
    }
}

// Carregar um caixa que já está aberto
function carregarCaixaAberto(id) {
    caixaAberto = true;
    caixaId = id;
    
    document.getElementById('btnAbrirCaixa').disabled = true;
    document.getElementById('btnFecharCaixa').disabled = false;
    document.getElementById('secaoVenda').style.display = 'block';
    document.getElementById('statusBox').classList.remove('fechado');
    document.getElementById('statusBox').classList.add('aberto');
    document.getElementById('statusText').textContent = 'ABERTO ✓';
    
    atualizarTotalizadores();
}

// ============================================
// 2. BUSCAR PRODUTOS
// ============================================
async function buscarProduto() {
    const nomeBusca = document.getElementById('buscaProduto').value.trim();

    if (!nomeBusca) {
        exibirMensagem('Digite o nome do produto para buscar', 'info');
        return;
    }

    if (!caixaAberto) {
        exibirMensagem('Abra o caixa primeiro!', 'erro');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/buscar-produto?nome=${encodeURIComponent(nomeBusca)}`);

        const data = await response.json();

        if (!response.ok) {
            exibirMensagem(data.erro || 'Nenhum produto encontrado', 'info');
            document.getElementById('produtosEncontrados').innerHTML = '';
            document.getElementById('produtosEncontrados').classList.remove('ativo');
            return;
        }

        // Exibir produtos encontrados
        exibirProdutos(data.produtos);

    } catch (erro) {
        console.error('Erro:', erro);
        exibirMensagem('Erro ao buscar produtos', 'erro');
    }
}

// ============================================
// 3. EXIBIR PRODUTOS ENCONTRADOS
// ============================================
function exibirProdutos(produtos) {
    const container = document.getElementById('produtosEncontrados');
    container.innerHTML = '';

    if (!produtos || produtos.length === 0) {
        container.innerHTML = '<p style="padding: 20px; text-align: center; color: #999;">Nenhum produto encontrado</p>';
        container.classList.remove('ativo');
        return;
    }

    produtos.forEach(produto => {
        const div = document.createElement('div');
        div.className = 'produto-item';
        div.innerHTML = `
            <div class="produto-info">
                <div class="produto-nome">${produto.nome}</div>
                <div class="produto-detalhes">
                    Estoque: ${produto.estoque} | ID: ${produto.id}
                </div>
            </div>
            <div class="produto-preco">R$ ${produto.preco.toFixed(2)}</div>
        `;
        div.onclick = () => selecionarProduto(produto);
        container.appendChild(div);
    });

    container.classList.add('ativo');
}

// ============================================
// 4. SELECIONAR PRODUTO
// ============================================
function selecionarProduto(produto) {
    // Validar estoque
    if (produto.estoque <= 0) {
        exibirMensagem(`❌ Produto "${produto.nome}" está sem estoque!`, 'erro');
        return;
    }

    produtoSelecionado = produto;

    // Preencher formulário
    document.getElementById('produtoId').value = produto.id;
    document.getElementById('quantidade').value = 1;
    document.getElementById('valorTotal').value = produto.preco.toFixed(2);

    // Mostrar resumo do produto selecionado
    document.getElementById('resumoProduto').textContent = produto.nome;
    document.getElementById('resumoPreco').textContent = produto.preco.toFixed(2);
    document.getElementById('resumoQtd').textContent = '1';
    document.getElementById('resumoTotal').textContent = produto.preco.toFixed(2);
    document.getElementById('resumoVenda').classList.add('ativo');

    // Fechar lista de produtos
    document.getElementById('produtosEncontrados').classList.remove('ativo');
    document.getElementById('buscaProduto').value = '';

    // Focar no campo de quantidade
    document.getElementById('quantidade').focus();

    exibirMensagem(`✓ Produto "${produto.nome}" selecionado!`, 'sucesso');
}

// ============================================
// 5. CALCULAR TROCO
// ============================================
function calcularTroco() {
    if (carrinhoItens.length === 0) return;

    const valorRecebido = parseFloat(document.getElementById('valorRecebido').value) || 0;

    // Calcular total do carrinho
    const totalCarrinho = carrinhoItens.reduce((sum, item) => sum + item.total, 0);
    const troco = valorRecebido - totalCarrinho;

    // Atualizar resumo com troco
    document.getElementById('resumoTrocoTotal').textContent = totalCarrinho.toFixed(2);
    document.getElementById('resumoTrocoRecebido').textContent = valorRecebido.toFixed(2);
    document.getElementById('resumoTrocoValor').textContent = troco.toFixed(2);

    // Mostrar seção de troco
    if (valorRecebido > 0 || carrinhoItens.length > 0) {
        document.getElementById('resumoTroco').classList.add('ativo');
    }

    // Avisar se valor insuficiente
    const trocoElement = document.getElementById('resumoTrocoValor').closest('.resumo-linha');
    if (valorRecebido < totalCarrinho && valorRecebido > 0) {
        trocoElement.classList.add('troco');
        trocoElement.style.color = '#ff6b6b';
        const falta = (totalCarrinho - valorRecebido).toFixed(2);
        exibirMensagem(`⚠️ Valor insuficiente! Falta: R$ ${falta}`, 'info');
    } else if (valorRecebido >= totalCarrinho && carrinhoItens.length > 0) {
        trocoElement.classList.add('troco');
        trocoElement.style.color = 'white';
    }
}

// ============================================
// 5.5 ADICIONAR AO CARRINHO
// ============================================
function adicionarAoCarrinho() {
    if (!caixaAberto) {
        exibirMensagem('Caixa não está aberto!', 'erro');
        return;
    }

    if (!produtoSelecionado) {
        exibirMensagem('Selecione um produto!', 'erro');
        return;
    }

    let quantidade = parseInt(document.getElementById('quantidade').value) || 1;

    // Validações
    if (quantidade <= 0) {
        exibirMensagem('Quantidade deve ser maior que zero', 'erro');
        return;
    }

    if (produtoSelecionado.estoque < quantidade) {
        exibirMensagem(`❌ Quantidade disponível em estoque: ${produtoSelecionado.estoque}`, 'erro');
        return;
    }

    // Verificar se o produto já está no carrinho
    const itemExistente = carrinhoItens.find(item => item.id === produtoSelecionado.id);
    
    if (itemExistente) {
        // Atualizar quantidade se já existe
        itemExistente.quantidade += quantidade;
        itemExistente.total = itemExistente.quantidade * itemExistente.preco;
    } else {
        // Adicionar novo item
        carrinhoItens.push({
            id: produtoSelecionado.id,
            nome: produtoSelecionado.nome,
            preco: produtoSelecionado.preco,
            quantidade: quantidade,
            total: produtoSelecionado.preco * quantidade
        });
    }

    exibirMensagem(`✓ ${produtoSelecionado.nome} adicionado ao carrinho!`, 'sucesso');
    exibirCarrinho();
    
    // Limpar seleção do formulário
    document.getElementById('quantidade').value = '1';
    document.getElementById('buscaProduto').value = '';
    document.getElementById('buscaProduto').focus();
    document.getElementById('produtosEncontrados').classList.remove('ativo');
}

// ============================================
// 5.6 EXIBIR CARRINHO
// ============================================
function exibirCarrinho() {
    const container = document.getElementById('carrinhoItens');
    const carrinhoContainer = document.getElementById('carrinhoContainer');
    const btnAdicionar = document.getElementById('btnAdicionarCarrinho');
    const btnConfirmar = document.getElementById('btnConfirmarVenda');

    if (carrinhoItens.length === 0) {
        carrinhoContainer.classList.remove('ativo');
        btnConfirmar.style.display = 'none';
        btnAdicionar.style.display = 'block';
        document.getElementById('resumoTroco').classList.remove('ativo');
        document.getElementById('valorRecebido').value = '';
        return;
    }

    container.innerHTML = '';
    let totalItens = 0;
    let valorTotal = 0;

    carrinhoItens.forEach((item, index) => {
        totalItens += item.quantidade;
        valorTotal += item.total;

        const div = document.createElement('div');
        div.className = 'carrinho-item';
        div.innerHTML = `
            <div class="carrinho-item-info">
                <div class="carrinho-item-nome">${item.nome}</div>
                <div class="carrinho-item-detalhe">${item.quantidade}x R$ ${item.preco.toFixed(2)} = R$ ${item.total.toFixed(2)}</div>
            </div>
            <div class="carrinho-item-valor">R$ ${item.total.toFixed(2)}</div>
            <button class="carrinho-item-remove" onclick="removerDoCarrinho(${index})">Remover</button>
        `;
        container.appendChild(div);
    });

    document.getElementById('carrinhoQtd').textContent = carrinhoItens.length;
    document.getElementById('carrinhoTotalItens').textContent = totalItens;
    document.getElementById('carrinhoValorTotal').textContent = `R$ ${valorTotal.toFixed(2)}`;

    carrinhoContainer.classList.add('ativo');
    btnConfirmar.style.display = 'block';
    btnAdicionar.style.display = 'block';
    calcularTroco();
}

// ============================================
// 5.7 REMOVER DO CARRINHO
// ============================================
function removerDoCarrinho(index) {
    if (index >= 0 && index < carrinhoItens.length) {
        const nomeProduto = carrinhoItens[index].nome;
        carrinhoItens.splice(index, 1);
        exibirMensagem(`Removido: ${nomeProduto}`, 'info');
        exibirCarrinho();
    }
}

// ============================================
// 6. CONFIRMAR VENDA
// ============================================
async function confirmarVenda() {
    if (!caixaAberto) {
        exibirMensagem('Caixa não está aberto!', 'erro');
        return;
    }

    if (carrinhoItens.length === 0) {
        exibirMensagem('Carrinho vazio! Adicione produtos primeiro.', 'erro');
        return;
    }

    const valorRecebido = parseFloat(document.getElementById('valorRecebido').value) || 0;
    const totalCarrinho = carrinhoItens.reduce((sum, item) => sum + item.total, 0);

    // Validações
    if (valorRecebido < totalCarrinho) {
        exibirMensagem(`❌ Valor insuficiente! Falta: R$ ${(totalCarrinho - valorRecebido).toFixed(2)}`, 'erro');
        return;
    }

    try {
        // Processar cada item do carrinho
        let totalVendas = 0;
        let trocoTotal = 0;

        for (let i = 0; i < carrinhoItens.length; i++) {
            const item = carrinhoItens[i];

            const response = await fetch(`${API_BASE}/vender`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    produtoId: item.id,
                    quantidade: item.quantidade,
                    valorRecebido: i === carrinhoItens.length - 1 ? valorRecebido : 0
                })
            });

            const data = await response.json();

            if (!response.ok) {
                const erro = data.erro || 'Erro ao registrar venda';
                exibirMensagem(`❌ ${erro}`, 'erro');
                return;
            }

            const venda = data.venda;
            totalVendas += item.quantidade;
            if (i === carrinhoItens.length - 1) {
                trocoTotal = venda.troco;
            }

            vendas.push(venda);
        }

        // ✓ TODAS AS VENDAS REGISTRADAS COM SUCESSO
        const troco = trocoTotal.toFixed(2);

        exibirMensagem(
            `✓ VENDAS CONFIRMADAS!\n📦 Total de itens: ${totalVendas}\n💰 Troco: R$ ${troco}`,
            'sucesso'
        );

        // Limpar
        setTimeout(() => {
            limparFormulario();
            atualizarTotalizadores();
        }, 500);

    } catch (erro) {
        console.error('Erro:', erro);
        exibirMensagem('Erro ao conectar com o servidor', 'erro');
    }
}

// ============================================
// 7. LIMPAR FORMULÁRIO
// ============================================
function limparFormulario() {
    document.getElementById('produtoId').value = '';
    document.getElementById('quantidade').value = '1';
    document.getElementById('valorTotal').value = '0.00';
    document.getElementById('valorRecebido').value = '';
    document.getElementById('resumoVenda').classList.remove('ativo');
    document.getElementById('resumoTroco').classList.remove('ativo');
    document.getElementById('buscaProduto').value = '';
    document.getElementById('buscaProduto').focus();
    produtoSelecionado = null;
    carrinhoItens = [];
    exibirCarrinho();
}

// ============================================
// 8. FECHAR CAIXA
// ============================================
async function fecharCaixa() {
    if (!confirm('Tem certeza que deseja fechar o caixa?\nEsta ação não pode ser desfeita!')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/fechar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await response.json();

        if (!response.ok) {
            exibirMensagem(data.erro || 'Erro ao fechar caixa', 'erro');
            return;
        }

        caixaAberto = false;

        // Atualizar interface
        document.getElementById('btnAbrirCaixa').disabled = false;
        document.getElementById('btnFecharCaixa').disabled = true;
        document.getElementById('secaoVenda').style.display = 'none';
        document.getElementById('statusBox').classList.remove('aberto');
        document.getElementById('statusBox').classList.add('fechado');
        document.getElementById('statusText').textContent = 'FECHADO ✕';

        exibirMensagem(
            `✓ CAIXA FECHADO!\n📊 Total de vendas: R$ ${data.totalArrecadado.toFixed(2)}\n📈 Quantidade: ${data.totalVendas}`,
            'sucesso'
        );

        vendas = [];
        atualizarTotalizadores();

    } catch (erro) {
        console.error('Erro:', erro);
        exibirMensagem('Erro ao fechar caixa', 'erro');
    }
}

// ============================================
// 9. VERIFICAR STATUS DO CAIXA
// ============================================
async function verificarStatusCaixa() {
    try {
        const response = await fetch(`${API_BASE}/status`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            console.error('Erro ao verificar status:', response.status);
            // Se falhar, deixa como fechado (padrão)
            return;
        }

        const data = await response.json();

        // Validar se há resposta correta
        if (!data || typeof data.aberto === 'undefined') {
            console.error('Resposta inválida:', data);
            return;
        }

        if (data.aberto === false) {
            // Caixa fechado - padrão inicial
            caixaAberto = false;
            document.getElementById('btnAbrirCaixa').disabled = false;
            document.getElementById('btnFecharCaixa').disabled = true;
            document.getElementById('secaoVenda').style.display = 'none';
            document.getElementById('statusBox').classList.remove('aberto');
            document.getElementById('statusBox').classList.add('fechado');
            document.getElementById('statusText').textContent = 'FECHADO ✕';
            return;
        }

        // Caixa aberto - carregar dados
        if (data.caixaId) {
            carregarCaixaAberto(data.caixaId);
            if (data.totalArrecadado) {
                document.getElementById('totalDia').textContent = (data.totalArrecadado || 0).toFixed(2);
            }
        }

    } catch (erro) {
        console.error('Erro ao verificar status:', erro);
        // Se houver erro, deixa o padrão (fechado)
        caixaAberto = false;
    }
}

// ============================================
// 10. ATUALIZAR TOTALIZADORES
// ============================================
async function atualizarTotalizadores() {
    try {
        const response = await fetch(`${API_BASE}/vendas-do-dia`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            console.error('Erro ao carregar vendas:', response.status);
            return;
        }

        const data = await response.json();

        if (!data.sucesso && !data.totalVendas) {
            console.error('Resposta inválida:', data);
            return;
        }

        // Atualizar valores
        document.getElementById('totalVendas').textContent = data.totalVendas || 0;
        document.getElementById('totalArrecadado').textContent = (data.totalArrecadado || 0).toFixed(2);
        document.getElementById('totalDia').textContent = (data.totalArrecadado || 0).toFixed(2);

        // Exibir histórico de vendas
        if (data.vendas && Array.isArray(data.vendas)) {
            exibirHistoricoVendas(data.vendas);
        }

    } catch (erro) {
        console.error('Erro ao atualizar totalizadores:', erro);
        // Não quebra a interface se houver erro
    }
}

// ============================================
// 11. EXIBIR HISTÓRICO DE VENDAS
// ============================================
function exibirHistoricoVendas(vendas) {
    const container = document.getElementById('vendasLista');
    container.innerHTML = '';

    if (!vendas || !Array.isArray(vendas) || vendas.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999; padding: 20px;">Nenhuma venda registrada</p>';
        return;
    }

    vendas.forEach((venda, index) => {
        try {
            const div = document.createElement('div');
            div.className = 'venda-item';
            
            const descricao = venda.descricao || 'Venda sem descrição';
            const observacoes = venda.observacoes || '';
            const saldo = venda.saldo || 0;
            
            div.innerHTML = `
                <div class="venda-detalhes">
                    <div class="venda-produto">${descricao}</div>
                    <div class="venda-info">${observacoes}</div>
                </div>
                <div class="venda-valor">R$ ${(saldo).toFixed(2)}</div>
            `;
            container.appendChild(div);
        } catch (e) {
            console.error('Erro ao exibir venda:', e, venda);
        }
    });
}

// ============================================
// 12. EXIBIR MENSAGENS
// ============================================
function exibirMensagem(texto, tipo) {
    const msg = document.getElementById('msg');
    msg.textContent = texto;
    msg.className = `mensagem ativo ${tipo}`;

    // Auto-desaparecer em 5 segundos
    setTimeout(() => {
        msg.classList.remove('ativo');
    }, 5000);
}
