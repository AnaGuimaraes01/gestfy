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
const categoriasNav = document.getElementById("categoriasNav"); // Pode ser null agora

// Mapa de categorias para nome
let categoriasMap = {};

// Containers de destaque
const promotoesContainer = document.getElementById("promotoesContainer");
const vendidosContainer = document.getElementById("vendidosContainer");
const popularesContainer = document.getElementById("popularesContainer");
const secaoPromocoes = document.getElementById("secaoPromocoes");
const secaoVendidos = document.getElementById("secaoVendidos");
const secaoPopulares = document.getElementById("secaoPopulares");

let todosProdutos = [];
let categoriaAtiva = 0; // 0 = todas as categorias

// Função para criar card de destaque
function criarCardDestaque(produto) {
    const card = document.createElement("div");
    card.className = "produto-card";
    
    let precoHtml = `<p class="produto-preco">R$ ${parseFloat(produto.preco).toFixed(2)}</p>`;
    let badgePromo = "";
    
    if (produto.emPromo && produto.precoPromo) {
        precoHtml = `
            <p class="produto-preco-original">R$ ${parseFloat(produto.preco).toFixed(2)}</p>
            <p class="produto-preco-promo">R$ ${parseFloat(produto.precoPromo).toFixed(2)}</p>
        `;
        badgePromo = '<span class="badge-promo">PROMOÇÃO</span>';
    }
    
    card.innerHTML = `
        <div class="produto-imagem">
            ${badgePromo}
            <img src="${produto.urlFoto || '/images/placeholder.png'}" alt="${produto.nome}" onerror="this.style.display='none'">
        </div>
        <div class="produto-info">
            <h3 class="produto-nome">${produto.nome}</h3>
            <p class="produto-descricao">${produto.descricao || ""}</p>
            ${precoHtml}
            <button class="btn-carrinho-card" onclick="adicionarCarrinho(${produto.id}, '${produto.nome}', ${produto.emPromo && produto.precoPromo ? produto.precoPromo : produto.preco})">
                Adicionar ao Carrinho
            </button>
        </div>
    `;
    return card;
}

// Carregar produtos em promoção
async function carregarPromocoes() {
    try {
        const response = await fetch(`${API_URL}/destaque/promocoes`);
        if (!response.ok) throw new Error("Erro ao buscar promoções");
        
        const promocoes = await response.json();
        if (promocoes.length > 0) {
            promotoesContainer.innerHTML = "";
            promocoes.forEach(produto => {
                promotoesContainer.appendChild(criarCardDestaque(produto));
            });
            secaoPromocoes.style.display = "block";
        }
    } catch (error) {
        console.error("Erro ao carregar promoções:", error);
    }
}

// Carregar mais vendidos
async function carregarMaisVendidos() {
    try {
        const response = await fetch(`${API_URL}/destaque/vendidos`);
        if (!response.ok) throw new Error("Erro ao buscar mais vendidos");
        
        const vendidos = await response.json();
        if (vendidos.length > 0) {
            vendidosContainer.innerHTML = "";
            vendidos.forEach(produto => {
                vendidosContainer.appendChild(criarCardDestaque(produto));
            });
            secaoVendidos.style.display = "block";
        }
    } catch (error) {
        console.error("Erro ao carregar mais vendidos:", error);
    }
}

// Carregar mais populares
async function carregarMaisPopulares() {
    try {
        const response = await fetch(`${API_URL}/destaque/populares`);
        if (!response.ok) throw new Error("Erro ao buscar populares");
        
        const populares = await response.json();
        if (populares.length > 0) {
            popularesContainer.innerHTML = "";
            populares.forEach(produto => {
                popularesContainer.appendChild(criarCardDestaque(produto));
            });
            secaoPopulares.style.display = "block";
        }
    } catch (error) {
        console.error("Erro ao carregar populares:", error);
    }
}

// Carregar categorias (para mapeamento de nomes)
async function carregarCategorias() {
    try {
        const response = await fetch(API_CATEGORIAS);
        if (!response.ok) throw new Error("Erro ao buscar categorias");
        
        const categorias = await response.json();
        
        // Preencher mapa de categorias para acesso rápido
        categorias.forEach(categoria => {
            categoriasMap[categoria.id] = categoria.nome;
        });
    } catch (error) {
        console.error("Erro ao carregar categorias:", error);
    }
}

// Filtrar produtos por categoria (função pública, mantida para compatibilidade)
function filtrarPorCategoria(categoriaId) {
    categoriaAtiva = categoriaId;
    
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

        // Incrementar visualizações para cada produto
        todosProdutos.forEach(produto => {
            const endpoint = `${API_URL}/${produto.id}/incrementar-visualizacoes`;
            fetch(endpoint, { method: 'PUT' }).catch(() => {});
        });
    } catch (error) {
        console.error(error);
        msg.textContent = "Erro ao carregar produtos";
        msg.style.display = "block";
    }
}

// Exibir produtos agrupados por categoria
function exibirProdutos(produtos) {
    container.innerHTML = "";
    
    if (produtos.length === 0) {
        container.innerHTML = '<div class="sem-produtos" style="grid-column: 1/-1; text-align: center; color: rgba(255,255,255,0.6); padding: 40px 20px;">Nenhum produto encontrado</div>';
        return;
    }

    // Agrupar produtos por categoria
    const produtosPorCategoria = {};
    produtos.forEach(produto => {
        const catId = produto.categoriaId || 0;
        if (!produtosPorCategoria[catId]) {
            produtosPorCategoria[catId] = [];
        }
        produtosPorCategoria[catId].push(produto);
    });

    // Exibir cada categoria com seus produtos
    Object.keys(produtosPorCategoria).forEach(categoriaId => {
        const categoriaNome = categoriasMap[categoriaId] || `Categoria ${categoriaId}`;
        
        // Criar título da categoria
        const tituloCat = document.createElement("div");
        tituloCat.className = "categoria-titulo-section";
        tituloCat.innerHTML = `<h2 class="categoria-titulo">${categoriaNome}</h2>`;
        container.appendChild(tituloCat);
        
        // Criar grid para a categoria
        const gridCat = document.createElement("div");
        gridCat.className = "catalogo-grid-categoria";
        
        produtosPorCategoria[categoriaId].forEach(produto => {
            const card = document.createElement("div");
            card.className = "produto-card";
            
            let precoHtml = `<p class="produto-preco">R$ ${parseFloat(produto.preco).toFixed(2)}</p>`;
            let badgePromo = "";
            
            if (produto.emPromo && produto.precoPromo) {
                precoHtml = `
                    <p class="produto-preco-original">R$ ${parseFloat(produto.preco).toFixed(2)}</p>
                    <p class="produto-preco-promo">R$ ${parseFloat(produto.precoPromo).toFixed(2)}</p>
                `;
                badgePromo = '<span class="badge-promo">PROMOÇÃO</span>';
            }
            
            card.innerHTML = `
                <div class="produto-imagem">
                    ${badgePromo}
                    <img src="${produto.urlFoto || '/images/placeholder.png'}" alt="${produto.nome}" onerror="this.style.display='none'">
                </div>
                <div class="produto-info">
                    <h3 class="produto-nome">${produto.nome}</h3>
                    <p class="produto-descricao">${produto.descricao || ""}</p>
                    ${precoHtml}
                    <button class="btn-carrinho-card" onclick="adicionarCarrinho(${produto.id}, '${produto.nome}', ${produto.emPromo && produto.precoPromo ? produto.precoPromo : produto.preco})">
                        Adicionar ao Carrinho
                    </button>
                </div>
            `;
            gridCat.appendChild(card);
        });
        
        container.appendChild(gridCat);
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

// Carregar dados na página
(async function() {
    await carregarCategorias();
    carregarProdutos();
    carregarPromocoes();
    carregarMaisVendidos();
    carregarMaisPopulares();
})();