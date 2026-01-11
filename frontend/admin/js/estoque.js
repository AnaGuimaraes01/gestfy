const API_ESTOQUE = "https://gestfy-backend.onrender.com/api/estoque";
const API_PRODUTOS = "https://gestfy-backend.onrender.com/api/produtos";
const API_PEDIDOS = "https://gestfy-backend.onrender.com/api/pedidos";

const tabelaEstoque = document.getElementById("tabelaEstoque");
const tabelaMovimentos = document.getElementById("tabelaMovimentos");
const msgMovimento = document.getElementById("msgMovimento");

let produtosCache = [];
let movimentoesCache = [];

// INICIALIZAR
document.addEventListener("DOMContentLoaded", () => {
    carregarProdutos();
    carregarMovimentos();
    calcularResumo();
    preencherSelectProdutos();
});

// CARREGAR PRODUTOS E MOSTRAR INVENTÁRIO
async function carregarProdutos() {
    try {
        const response = await fetch(API_PRODUTOS);
        if (!response.ok) throw new Error("Erro ao buscar produtos");
        
        produtosCache = await response.json();
        exibirInventario(produtosCache);
        calcularResumo();
    } catch (error) {
        console.error(error);
        tabelaEstoque.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #f44;">Erro ao carregar produtos</td></tr>`;
    }
}

// EXIBIR INVENTÁRIO
function exibirInventario(produtos) {
    if (!produtos || produtos.length === 0) {
        tabelaEstoque.innerHTML = `<tr><td colspan="5" style="text-align: center;">Nenhum produto encontrado</td></tr>`;
        return;
    }

    tabelaEstoque.innerHTML = "";
    
    produtos.forEach(produto => {
        const tr = document.createElement("tr");
        let statusClass = "alerta-normal";
        let statusTexto = "✅ Disponível";

        if (produto.quantidade <= 0) {
            statusClass = "alerta-baixo";
            statusTexto = "⚠️ EM FALTA";
        } else if (produto.quantidade <= 5) {
            statusClass = "alerta-baixo";
            statusTexto = "⚠️ ESTOQUE BAIXO";
        }

        tr.innerHTML = `
            <td>${produto.id}</td>
            <td><strong>${produto.nome}</strong></td>
            <td>R$ ${parseFloat(produto.preco).toFixed(2)}</td>
            <td>${produto.quantidade || 0} un.</td>
            <td><span class="${statusClass}">${statusTexto}</span></td>
        `;

        tabelaEstoque.appendChild(tr);
    });
}

// CALCULAR RESUMO
function calcularResumo() {
    const totalProdutos = produtosCache.length;
    const emFalta = produtosCache.filter(p => !p.quantidade || p.quantidade <= 0).length;
    const estoqueBaixo = produtosCache.filter(p => p.quantidade && p.quantidade > 0 && p.quantidade <= 5).length;

    document.getElementById("totalProdutos").textContent = totalProdutos;
    document.getElementById("produtosEmFalta").textContent = emFalta;
    document.getElementById("estoqueBaixo").textContent = estoqueBaixo;
}

// FILTRAR ESTOQUE
function filtrarEstoque() {
    const filtro = document.getElementById("filtroNome").value.toLowerCase();
    const filtrados = produtosCache.filter(p => 
        p.nome.toLowerCase().includes(filtro)
    );
    exibirInventario(filtrados);
}

// LIMPAR FILTROS
function limparFiltros() {
    document.getElementById("filtroNome").value = "";
    exibirInventario(produtosCache);
}

// PREENCHER SELECT DE PRODUTOS
function preencherSelectProdutos() {
    const select = document.getElementById("produtoSelect");
    select.innerHTML = '<option value="">-- Selecione o Produto --</option>';
    
    produtosCache.forEach(produto => {
        const option = document.createElement("option");
        option.value = produto.id;
        option.textContent = `${produto.nome} (Qtd: ${produto.quantidade || 0})`;
        select.appendChild(option);
    });
}

// REGISTRAR MOVIMENTO
async function registrarMovimento() {
    const produtoId = document.getElementById("produtoSelect").value;
    const tipo = document.getElementById("tipoMovimento").value;
    const quantidade = parseInt(document.getElementById("quantidadeMovimento").value);

    if (!produtoId || !tipo || !quantidade || quantidade <= 0) {
        msgMovimento.textContent = "⚠️ Preencha todos os campos";
        msgMovimento.style.color = "#ffa500";
        msgMovimento.style.display = "block";
        return;
    }

    try {
        const movimento = {
            produtoId: parseInt(produtoId),
            tipoMovimento: tipo,
            quantidade: quantidade
        };

        const response = await fetch(API_ESTOQUE, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(movimento)
        });

        if (!response.ok) throw new Error("Erro ao registrar movimento");

        msgMovimento.textContent = `✅ Movimento registrado com sucesso!`;
        msgMovimento.style.color = "#34a853";
        msgMovimento.style.display = "block";

        document.getElementById("produtoSelect").value = "";
        document.getElementById("tipoMovimento").value = "";
        document.getElementById("quantidadeMovimento").value = "";

        // Recarregar dados
        setTimeout(() => {
            carregarProdutos();
            carregarMovimentos();
            preencherSelectProdutos();
            msgMovimento.style.display = "none";
        }, 1500);

    } catch (error) {
        console.error(error);
        msgMovimento.textContent = `❌ ${error.message}`;
        msgMovimento.style.color = "#f44";
        msgMovimento.style.display = "block";
    }
}

// CARREGAR MOVIMENTOS
async function carregarMovimentos() {
    try {
        const response = await fetch(API_ESTOQUE);
        if (!response.ok) throw new Error("Erro ao buscar movimentos");
        
        movimentoesCache = await response.json();
        exibirMovimentos(movimentoesCache.slice(0, 20)); // últimas 20
    } catch (error) {
        console.error(error);
        tabelaMovimentos.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #f44;">Erro ao carregar movimentos</td></tr>`;
    }
}

// EXIBIR MOVIMENTOS
function exibirMovimentos(movimentos) {
    if (!movimentos || movimentos.length === 0) {
        tabelaMovimentos.innerHTML = `<tr><td colspan="5" style="text-align: center;">Nenhuma movimentação registrada</td></tr>`;
        return;
    }

    tabelaMovimentos.innerHTML = "";
    
    movimentos.forEach(mov => {
        const tr = document.createElement("tr");
        const produto = produtosCache.find(p => p.id === mov.produtoId);
        const nomeProduto = produto ? produto.nome : `Produto #${mov.produtoId}`;
        const badge = mov.tipoMovimento === "ENTRADA" ? "📥" : "📤";

        tr.innerHTML = `
            <td>#${mov.id}</td>
            <td>${nomeProduto}</td>
            <td>${badge} ${mov.tipoMovimento}</td>
            <td>${mov.quantidade} un.</td>
            <td>${new Date(mov.dataMovimento).toLocaleString("pt-BR")}</td>
        `;

        tabelaMovimentos.appendChild(tr);
    });
}
