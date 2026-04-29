/**
 * CAIXA SIMPLES - JavaScript
 * Sistema de vendas em dinheiro para lanchonete/sorveteria
 * 
 * API: https://gestfy-backend.onrender.com/api/caixa
 * Para desenvolvimento: http://localhost:8080/api/caixa
 */

// ============================================
// VARIÁVEIS GLOBAIS
// ============================================
let API_BASE = 'https://gestfy-backend.onrender.com/api'; // Padrão de produção
let caixaAberto = false;
let caixaId = null;
let produtoSelecionado = null;
let vendas = [];

// ============================================
// DETECTAR AMBIENTE (localhost vs produção)
// ============================================
(async function detectApi() {
    try {
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 200);
        const res = await fetch('http://localhost:8080/api/caixa/status', { signal: controller.signal });
        clearTimeout(timeout);
        if (res && res.ok) {
            API_BASE = 'http://localhost:8080/api';
        }
    } catch (e) {
        // Mantém padrão de produção
    }
})();

// ============================================
// INICIALIZAÇÃO
// ============================================
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
});

// ============================================
// 1. ABRIR CAIXA
// ============================================
async function abrirCaixa() {
    try {
        const response = await fetch(`${API_BASE}/caixa/abrir`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await response.json();

        if (!response.ok) {
            if (response.status === 409) {
                // Caixa já aberto
                verificarStatusCaixa();
                exibirMensagem('Caixa já está aberto para hoje!', 'info');
                return;
            }
            exibirMensagem(data.erro || 'Erro ao abrir caixa', 'erro');
            return;
        }

        caixaAberto = true;
        caixaId = data.caixaId;

        // Atualizar interface
        document.getElementById('btnAbrirCaixa').disabled = true;
        document.getElementById('btnFecharCaixa').disabled = false;
        document.getElementById('secaoVenda').style.display = 'block';
        document.getElementById('statusBox').classList.add('aberto');
        document.getElementById('statusText').textContent = 'ABERTO ✓';

        exibirMensagem('✓ Caixa aberto com sucesso!', 'sucesso');
        atualizarTotalizadores();

    } catch (erro) {
        console.error('Erro:', erro);
        exibirMensagem('Erro ao conectar com o servidor', 'erro');
    }
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
        const response = await fetch(`${API_BASE}/caixa/buscar-produto?nome=${encodeURIComponent(nomeBusca)}`);

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

    // Mostrar resumo
    document.getElementById('resumoProduto').textContent = produto.nome;
    document.getElementById('resumoPreco').textContent = produto.preco.toFixed(2);
    document.getElementById('resumoQtd').textContent = '1';
    document.getElementById('resumoTotal').textContent = produto.preco.toFixed(2);
    document.getElementById('resumoRecebido').textContent = '0.00';
    document.getElementById('resumoTroco').textContent = '0.00';
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
    if (!produtoSelecionado) return;

    const quantidade = Math.max(1, parseInt(document.getElementById('quantidade').value) || 0);
    const valorRecebido = parseFloat(document.getElementById('valorRecebido').value) || 0;

    if (quantidade <= 0) {
        exibirMensagem('Quantidade deve ser maior que zero', 'erro');
        document.getElementById('quantidade').value = 1;
        return;
    }

    const valorTotal = produtoSelecionado.preco * quantidade;
    const troco = valorRecebido - valorTotal;

    // Atualizar resumo
    document.getElementById('resumoQtd').textContent = quantidade;
    document.getElementById('resumoTotal').textContent = valorTotal.toFixed(2);
    document.getElementById('resumoRecebido').textContent = valorRecebido.toFixed(2);
    document.getElementById('resumoTroco').textContent = troco.toFixed(2);
    document.getElementById('valorTotal').value = valorTotal.toFixed(2);

    // Avisar se valor insuficiente
    if (valorRecebido < valorTotal && valorRecebido > 0) {
        document.getElementById('resumoTroco').closest('.resumo-linha').style.color = '#f44336';
        const falta = (valorTotal - valorRecebido).toFixed(2);
        exibirMensagem(`⚠️ Valor insuficiente! Falta: R$ ${falta}`, 'info');
    } else {
        document.getElementById('resumoTroco').closest('.resumo-linha').style.color = '#4caf50';
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

    if (!produtoSelecionado) {
        exibirMensagem('Selecione um produto!', 'erro');
        return;
    }

    const quantidade = Math.max(1, parseInt(document.getElementById('quantidade').value) || 0);
    const valorRecebido = parseFloat(document.getElementById('valorRecebido').value) || 0;
    const valorTotal = produtoSelecionado.preco * quantidade;

    // Validações
    if (quantidade <= 0) {
        exibirMensagem('Quantidade deve ser maior que zero', 'erro');
        return;
    }

    if (valorRecebido < valorTotal) {
        exibirMensagem(`❌ Valor insuficiente! Falta: R$ ${(valorTotal - valorRecebido).toFixed(2)}`, 'erro');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/caixa/vender`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                produtoId: produtoSelecionado.id,
                quantidade: quantidade,
                valorRecebido: valorRecebido
            })
        });

        const data = await response.json();

        if (!response.ok) {
            const erro = data.erro || 'Erro ao registrar venda';
            exibirMensagem(`❌ ${erro}`, 'erro');
            return;
        }

        // ✓ VENDA REGISTRADA COM SUCESSO
        const venda = data.venda;
        const troco = venda.troco.toFixed(2);

        exibirMensagem(
            `✓ VENDA CONFIRMADA!\n💰 Troco: R$ ${troco}`,
            'sucesso'
        );

        // Adicionar à lista de vendas
        vendas.push(venda);

        // Limpar formulário
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
    document.getElementById('buscaProduto').value = '';
    document.getElementById('buscaProduto').focus();
    produtoSelecionado = null;
}

// ============================================
// 8. FECHAR CAIXA
// ============================================
async function fecharCaixa() {
    if (!confirm('Tem certeza que deseja fechar o caixa?\nEsta ação não pode ser desfeita!')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/caixa/fechar`, {
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
        const response = await fetch(`${API_BASE}/caixa/status`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await response.json();

        if (data.aberto) {
            caixaAberto = true;
            caixaId = data.caixaId;

            document.getElementById('btnAbrirCaixa').disabled = true;
            document.getElementById('btnFecharCaixa').disabled = false;
            document.getElementById('secaoVenda').style.display = 'block';
            document.getElementById('statusBox').classList.add('aberto');
            document.getElementById('statusText').textContent = 'ABERTO ✓';
            document.getElementById('totalDia').textContent = data.totalArrecadado.toFixed(2);

            atualizarTotalizadores();
        } else {
            caixaAberto = false;
            document.getElementById('btnAbrirCaixa').disabled = false;
            document.getElementById('btnFecharCaixa').disabled = true;
            document.getElementById('secaoVenda').style.display = 'none';
            document.getElementById('statusBox').classList.add('fechado');
            document.getElementById('statusText').textContent = 'FECHADO ✕';
        }

    } catch (erro) {
        console.error('Erro ao verificar status:', erro);
    }
}

// ============================================
// 10. ATUALIZAR TOTALIZADORES
// ============================================
async function atualizarTotalizadores() {
    try {
        const response = await fetch(`${API_BASE}/caixa/vendas-do-dia`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await response.json();

        if (!response.ok) return;

        document.getElementById('totalVendas').textContent = data.totalVendas;
        document.getElementById('totalArrecadado').textContent = data.totalArrecadado.toFixed(2);
        document.getElementById('totalDia').textContent = data.totalArrecadado.toFixed(2);

        // Exibir histórico de vendas
        exibirHistoricoVendas(data.vendas);

    } catch (erro) {
        console.error('Erro ao atualizar totalizadores:', erro);
    }
}

// ============================================
// 11. EXIBIR HISTÓRICO DE VENDAS
// ============================================
function exibirHistoricoVendas(vendas) {
    const container = document.getElementById('vendasLista');
    container.innerHTML = '';

    if (!vendas || vendas.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999; padding: 20px;">Nenhuma venda registrada</p>';
        return;
    }

    vendas.forEach((venda, index) => {
        const div = document.createElement('div');
        div.className = 'venda-item';
        const hora = new Date(venda.horarioAbertura).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
        div.innerHTML = `
            <div class="venda-detalhes">
                <div class="venda-produto">${venda.descricao}</div>
                <div class="venda-info">${hora} - ${venda.observacoes}</div>
            </div>
            <div class="venda-valor">R$ ${(venda.saldo || 0).toFixed(2)}</div>
        `;
        container.appendChild(div);
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
