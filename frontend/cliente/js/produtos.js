let API_URL = "https://gestfy-backend.onrender.com/api/produtos"; // Padrão produção
let API_CATEGORIAS = "https://gestfy-backend.onrender.com/api/categorias"; // Padrão produção

// Detectar ambiente (localhost vs produção)
(async function() {
    try {
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 200);
        const res = await fetch('http://localhost:8080/api/produtos', { signal: controller.signal });
        clearTimeout(timeout);
        if (res && res.ok) {
            API_URL = 'http://localhost:8080/api/produtos';
            API_CATEGORIAS = 'http://localhost:8080/api/categorias';
        }
    } catch (e) {
        // Mantém padrão de produção
    }
})();

const container = document.getElementById("produtosContainer");
const msg = document.getElementById("mensagem");
const buscaInput = document.getElementById("buscaInput");
const categoriasNav = document.getElementById("categoriasNav");

let todosProdutos = [];
let categoriaAtiva = 0; // 0 = todas as categorias

// Carregar categorias e criar botões
async function carregarCategorias() {
    try {
        const response = await fetch(API_CATEGORIAS);
        if (!response.ok) throw new Error("Erro ao buscar categorias");
        
        const categorias = await response.json();
        
        // Limpar navegação mantendo "Todas"
        const botaoTodas = categoriasNav.querySelector('[data-categoria="0"]');
        categoriasNav.innerHTML = '';
        categoriasNav.appendChild(botaoTodas);
        
        // Adicionar botões de categorias
        categorias.forEach(categoria => {
            const btn = document.createElement("button");
            btn.className = "btn-categoria";
            btn.textContent = categoria.nome;
            btn.setAttribute("data-categoria", categoria.id);
            btn.onclick = () => filtrarPorCategoria(categoria.id);
            categoriasNav.appendChild(btn);
        });
    } catch (error) {
        console.error("Erro ao carregar categorias:", error);
    }
}

// Filtrar produtos por categoria
function filtrarPorCategoria(categoriaId) {
    categoriaAtiva = categoriaId;
    
    // Atualizar estilo dos botões
    document.querySelectorAll(".btn-categoria").forEach(btn => {
        btn.classList.remove("btn-categoria-ativo");
    });
    document.querySelector(`[data-categoria="${categoriaId}"]`).classList.add("btn-categoria-ativo");
    
    // Filtrar e exibir
    if (categoriaId === 0) {
        exibirProdutos(todosProdutos);
    } else {
        const produtosFiltrados = todosProdutos.filter(p => p.categoriaId === categoriaId);
        exibirProdutos(produtosFiltrados);
    }
}

// Carregar produtos ao abrir página
async function carregarProdutos() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error("Erro ao buscar produtos");
        
        todosProdutos = await response.json();
        exibirProdutos(todosProdutos);
    } catch (error) {
        console.error(error);
        msg.textContent = "Erro ao carregar produtos";
        msg.style.display = "block";
    }
}

// Exibir produtos na tela
function exibirProdutos(produtos) {
    container.innerHTML = "";
    
    if (produtos.length === 0) {
        container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: #888;">Nenhum produto encontrado</p>';
        return;
    }

    produtos.forEach(produto => {
        const card = document.createElement("div");
        card.className = "produto-card";
        card.innerHTML = `
            <div class="produto-imagem">
                <img src="${produto.urlFoto || '/images/placeholder.png'}" alt="${produto.nome}" onerror="this.style.display='none'">
            </div>
            <div class="produto-info">
                <h3 class="produto-nome">${produto.nome}</h3>
                <p class="produto-descricao">${produto.descricao || "Sem descrição"}</p>
                <p class="produto-preco">R$ ${parseFloat(produto.preco).toFixed(2)}</p>
                <button class="btn-carrinho" onclick="adicionarCarrinho(${produto.id}, '${produto.nome}', ${produto.preco})">
                     Adicionar
                </button>
            </div>
        `;
        container.appendChild(card);
    });
}

// Buscar produtos por nome (funciona com categoria ativa)
buscaInput.addEventListener("input", (e) => {
    const termo = e.target.value.toLowerCase();
    let filtrados = todosProdutos.filter(p => 
        p.nome.toLowerCase().includes(termo)
    );
    
    // Se uma categoria está ativa, filtrar também por categoria
    if (categoriaAtiva > 0) {
        filtrados = filtrados.filter(p => p.categoriaId === categoriaAtiva);
    }
    
    exibirProdutos(filtrados);
});

// Adicionar ao carrinho
function adicionarCarrinho(id, nome, preco) {
    let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];
    
    const itemExistente = carrinho.find(i => i.id === id);
    
    if (itemExistente) {
        itemExistente.quantidade += 1;
    } else {
        carrinho.push({
            id: id,
            nome: nome,
            preco: preco,
            quantidade: 1
        });
    }
    
    localStorage.setItem("carrinho", JSON.stringify(carrinho));
    
    msg.textContent = `"${nome}" adicionado ao carrinho!`;
    msg.style.display = "block";
    setTimeout(() => msg.style.display = "none", 2000);
}

// Carregar categorias e produtos na página
carregarCategorias();
carregarProdutos();