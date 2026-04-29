let API_ESTOQUE = "https://gestfy-backend.onrender.com/api/estoque";
let API_PRODUTOS = "https://gestfy-backend.onrender.com/api/produtos";
(async function() { try { const c = new AbortController(); const t = setTimeout(() => c.abort(), 200); const r = await fetch('http://localhost:8080/api/estoque', { signal: c.signal }); clearTimeout(t); if (r && r.ok) { API_ESTOQUE = 'http://localhost:8080/api/estoque'; API_PRODUTOS = 'http://localhost:8080/api/produtos'; } } catch (e) {} })();

const tabelaEstoque = document.getElementById("tabelaEstoque");

let produtosCache = [];

// INICIALIZAR
document.addEventListener("DOMContentLoaded", () => {
    carregarProdutos();
    calcularResumo();
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
        let statusTexto = "Disponível";

        if (produto.quantidade <= 0) {
            statusClass = "alerta-baixo";
            statusTexto = "EM FALTA";
        } else if (produto.quantidade <= 5) {
            statusClass = "alerta-baixo";
            statusTexto = "ESTOQUE BAIXO";
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
