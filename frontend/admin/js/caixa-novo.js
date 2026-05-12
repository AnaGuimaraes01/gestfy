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
let vendaAtualItens = []; 

// DETECTAR AMBIENTE (localhost vs produção)
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

// INICIALIZAÇÃO
document.addEventListener('DOMContentLoaded', () => {
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

        exibirProdutos(data.produtos);

    } catch (erro) {
        console.error('Erro:', erro);
        exibirMensagem('Erro ao buscar produtos', 'erro');
    }
}

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

function selecionarProduto(produto) {
    // Validar estoque
    if (produto.estoque <= 0) {
        exibirMensagem(`❌ Produto "${produto.nome}" está sem estoque!`, 'erro');
        return;
    }

    adicionarAoVendaAtual(produto);

    // Fechar lista de produtos
    document.getElementById('produtosEncontrados').classList.remove('ativo');
    document.getElementById('buscaProduto').value = '';
    document.getElementById('buscaProduto').focus();
}

function adicionarAoVendaAtual(produto) {
    if (!caixaAberto) {
        exibirMensagem('Caixa não está aberto!', 'erro');
        return;
    }

    // Verificar se o produto já está na venda
    const itemExistente = vendaAtualItens.find(item => item.id === produto.id);
    
    if (itemExistente) {
        // Se a quantidade disponível permite, aumenta
        if (itemExistente.quantidade < produto.estoque) {
            itemExistente.quantidade += 1;
            itemExistente.total = itemExistente.quantidade * itemExistente.preco;
            exibirMensagem(`✓ ${produto.nome}: agora ${itemExistente.quantidade}x`, 'sucesso');
        } else {
            exibirMensagem(`Estoque insuficiente de "${produto.nome}"`, 'erro');
            return;
        }
    } else {
        // Adicionar novo item
        vendaAtualItens.push({
            id: produto.id,
            nome: produto.nome,
            preco: produto.preco,
            quantidade: 1,
            total: produto.preco
        });
        exibirMensagem(`✓ ${produto.nome} adicionado à venda!`, 'sucesso');
    }

    // Atualizar interface
    exibirVendaAtual();
}


function calcularTroco() {
    if (vendaAtualItens.length === 0) return;

    const valorRecebido = parseFloat(document.getElementById('valorRecebido').value) || 0;

    // Calcular total da venda
    const totalVenda = vendaAtualItens.reduce((sum, item) => sum + item.total, 0);
    const troco = valorRecebido - totalVenda;

    // Atualizar campos
    document.getElementById('valorTotalVenda').value = `R$ ${totalVenda.toFixed(2)}`;
    document.getElementById('trocoValor').value = `R$ ${troco.toFixed(2)}`;

    // Mostrar formulário
    if (valorRecebido > 0 || vendaAtualItens.length > 0) {
        document.getElementById('formularioVendaContainer').style.display = 'block';
    }

    // Avisar se valor insuficiente
    const trocoElement = document.getElementById('trocoValor');
    if (valorRecebido < totalVenda && valorRecebido > 0) {
        trocoElement.style.color = '#ff6b6b';
        trocoElement.style.background = 'rgba(255, 107, 107, 0.1)';
        const falta = (totalVenda - valorRecebido).toFixed(2);
        exibirMensagem(`Valor insuficiente! Falta: R$ ${falta}`, 'info');
    } else if (valorRecebido >= totalVenda && vendaAtualItens.length > 0) {
        trocoElement.style.color = '#4caf50';
        trocoElement.style.background = 'rgba(76, 175, 80, 0.1)';
    }
}

function exibirVendaAtual() {
    const container = document.getElementById('vendaAtualItens');
    const vendaContainer = document.getElementById('vendaAtualContainer');
    const btnConfirmar = document.getElementById('btnConfirmarVenda');
    const btnLimpar = document.getElementById('btnLimparVenda');

    if (vendaAtualItens.length === 0) {
        vendaContainer.classList.remove('ativo');
        btnConfirmar.style.display = 'none';
        btnLimpar.style.display = 'none';
        document.getElementById('formularioVendaContainer').style.display = 'none';
        document.getElementById('valorRecebido').value = '';
        return;
    }

    container.innerHTML = '';
    let totalItens = 0;
    let valorTotal = 0;

    vendaAtualItens.forEach((item, index) => {
        totalItens += item.quantidade;
        valorTotal += item.total;

        const div = document.createElement('div');
        div.className = 'carrinho-item';
        div.innerHTML = `
            <div class="carrinho-item-info">
                <div class="carrinho-item-nome">${item.nome}</div>
                <div class="carrinho-item-detalhe">
                    <input type="number" id="qtd-${index}" value="${item.quantidade}" min="1" max="999" 
                           style="width: 50px; padding: 4px; margin-right: 8px;"
                           onchange="atualizarQuantidade(${index}, this.value)"> x R$ ${item.preco.toFixed(2)} = R$ ${item.total.toFixed(2)}
                </div>
            </div>
            <div class="carrinho-item-valor">R$ ${item.total.toFixed(2)}</div>
            <button class="carrinho-item-remove" onclick="removerDoVendaAtual(${index})">Remover</button>
        `;
        container.appendChild(div);
    });

    document.getElementById('vendaAtualQtd').textContent = vendaAtualItens.length;
    document.getElementById('vendaAtualTotalItens').textContent = totalItens;
    document.getElementById('vendaAtualValorTotal').textContent = `R$ ${valorTotal.toFixed(2)}`;

    vendaContainer.classList.add('ativo');
    btnConfirmar.style.display = 'block';
    btnLimpar.style.display = 'block';
    calcularTroco();
}

function removerDoVendaAtual(index) {
    if (index >= 0 && index < vendaAtualItens.length) {
        const nomeProduto = vendaAtualItens[index].nome;
        vendaAtualItens.splice(index, 1);
        exibirMensagem(`Removido: ${nomeProduto}`, 'info');
        exibirVendaAtual();
    }
}

function atualizarQuantidade(index, novaQuantidade) {
    const qtd = parseInt(novaQuantidade) || 1;
    
    if (qtd <= 0) {
        removerDoVendaAtual(index);
        return;
    }

    if (index >= 0 && index < vendaAtualItens.length) {
        vendaAtualItens[index].quantidade = qtd;
        vendaAtualItens[index].total = qtd * vendaAtualItens[index].preco;
        exibirVendaAtual();
    }
}

async function confirmarVenda() {
    if (!caixaAberto) {
        exibirMensagem('Caixa não está aberto!', 'erro');
        return;
    }

    if (vendaAtualItens.length === 0) {
        exibirMensagem('Nenhum produto na venda! Adicione produtos primeiro.', 'erro');
        return;
    }

    const valorRecebido = parseFloat(document.getElementById('valorRecebido').value) || 0;
    const totalVenda = vendaAtualItens.reduce((sum, item) => sum + item.total, 0);

    // Validações
    if (valorRecebido < totalVenda) {
        exibirMensagem(`Valor insuficiente! Falta: R$ ${(totalVenda - valorRecebido).toFixed(2)}`, 'erro');
        return;
    }

    try {
        // Montar payload para venda agrupada
        const itens = vendaAtualItens.map(item => ({
            produtoId: item.id,
            quantidade: item.quantidade
        }));

        const payload = {
            itens: itens,
            valorRecebido: valorRecebido
        };

        const response = await fetch(`${API_BASE}/vender-agrupada`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            const erro = data.erro || 'Erro ao registrar venda';
            exibirMensagem(`❌ ${erro}`, 'erro');
            return;
        }

        // ✓ VENDA AGRUPADA CONFIRMADA
        const troco = data.venda.troco.toFixed(2);
        const totalItens = data.venda.totalItens;

        exibirMensagem(
            `✓ VENDA CONFIRMADA!\n Total de itens: ${totalItens}\n Troco: R$ ${troco}`,
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

function limparFormulario() {
    document.getElementById('valorRecebido').value = '';
    document.getElementById('valorTotalVenda').value = 'R$ 0.00';
    document.getElementById('trocoValor').value = 'R$ 0.00';
    document.getElementById('formularioVendaContainer').style.display = 'none';
    document.getElementById('buscaProduto').value = '';
    document.getElementById('buscaProduto').focus();
    produtoSelecionado = null;
    vendaAtualItens = [];
    exibirVendaAtual();
}

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
    }
}

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

function exibirMensagem(texto, tipo) {
    const msg = document.getElementById('msg');
    msg.textContent = texto;
    msg.className = `mensagem ativo ${tipo}`;

    // Auto-desaparecer em 5 segundos
    setTimeout(() => {
        msg.classList.remove('ativo');
    }, 5000);
}
