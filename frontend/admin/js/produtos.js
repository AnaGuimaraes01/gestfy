let API_URL = "https://gestfy-backend.onrender.com/api/produtos";
let API_CATEGORIAS = "https://gestfy-backend.onrender.com/api/categorias";
(async function() { try { const c = new AbortController(); const t = setTimeout(() => c.abort(), 200); const r = await fetch('http://localhost:8080/api/produtos', { signal: c.signal }); clearTimeout(t); if (r && r.ok) { API_URL = 'http://localhost:8080/api/produtos'; API_CATEGORIAS = 'http://localhost:8080/api/categorias'; } } catch (e) {} })();

const produtosList = document.getElementById("produtosList");
const form = document.getElementById("produtoForm");
const msg = document.getElementById("msg");
const btnSalvar = document.getElementById("btnSalvar");
const btnCancelar = document.getElementById("btnCancelar");
const categoriaSelect = document.getElementById("categoria");

// ID do produto em edição (null = novo produto)
let produtoEmEdicao = null;
let categoriasMapa = {}; // Armazenar categorias para lookup rápido

// Carregar categorias no select
async function carregarCategorias() {
  try {
    const response = await fetch(API_CATEGORIAS);
    if (!response.ok) throw new Error("Erro ao buscar categorias");
    
    const categorias = await response.json();
    categoriaSelect.innerHTML = '<option value="">Selecione uma categoria</option>';
    
    categorias.forEach(categoria => {
      const option = document.createElement("option");
      option.value = categoria.id;
      option.textContent = categoria.nome;
      categoriaSelect.appendChild(option);
      
      // Armazenar em mapa para lookup na listagem
      categoriasMapa[categoria.id] = categoria.nome;
    });
  } catch (error) {
    console.error("Erro ao carregar categorias:", error);
    msg.textContent = "Erro ao carregar categorias";
    msg.style.color = "#f44";
  }
}

/*Listar Produtos*/
async function listarProdutos() {
  try {
    console.log("Buscando produtos na API: ", API_URL);
    const response = await fetch(API_URL);

    if (!response.ok) {
      throw new Error("Erro ao buscar produtos, api url : ", API_URL);
    }

    const produtos = await response.json();
    produtosList.innerHTML = "";

    if (produtos.length === 0) {
      produtosList.innerHTML = "<li style='text-align: center; color: #999;'>Nenhum produto cadastrado</li>";
      return;
    }

    produtos.forEach(produto => {
      const li = document.createElement("li");
      const imageUrl = produto.urlFoto || "/images/placeholder.png";
      
      // Se é URL, tenta carregar imagem; senão mostra emoji
      let imagemHtml = "";
      if (produto.urlFoto && produto.urlFoto.startsWith("http")) {
        imagemHtml = `<img src="${produto.urlFoto}" alt="${produto.nome}" class="produto-thumb" onerror="this.textContent='[Sem imagem]'">`;
      } else {
        imagemHtml = `<div class="produto-thumb-emoji">[Sem imagem]</div>`;
      }

      li.className = "produto-item";
      li.innerHTML = `
        <div class="produto-thumb-container">
          ${imagemHtml}
        </div>
        <div class="produto-details">
          <strong>${produto.nome}</strong>
          <p class="descricao-small">${produto.descricao || "Sem descrição"}</p>
          <span class="preco">R$ ${parseFloat(produto.preco).toFixed(2)}</span>
          <span class="quantidade">Qtd: ${produto.quantidade ?? 0}</span>
          <span class="categoria" style="color: #666; font-size: 0.9em; display: block; margin-top: 5px;">📁 ${categoriasMapa[produto.categoriaId] || "Sem categoria"}</span>
          <small style="color: #999; display: block; margin-top: 5px;">ID: ${produto.id}</small>
        </div>
        <div class="produto-actions">
          <button class="btn-small" onclick="editarProduto(${produto.id})">✏️ Editar</button>
        </div>
      `;

      produtosList.appendChild(li);
    });

  } catch (error) {
    console.error(error);
    msg.textContent = "Erro ao carregar produtos, api url" + API_URL;
  }
}


form.addEventListener("submit", async (e) => {
  e.preventDefault();

  // Validar campos
  if (!nome.value.trim()) {
    msg.textContent = " Nome do produto é obrigatório";
    msg.style.color = "#f44";
    return;
  }
  if (!preco.value || parseFloat(preco.value) <= 0) {
    msg.textContent = " Preço deve ser maior que 0";
    msg.style.color = "#f44";
    return;
  }
  if (!quantidade.value || parseInt(quantidade.value) <= 0) {
    msg.textContent = " Quantidade deve ser maior que 0";
    msg.style.color = "#f44";
    return;
  }
  if (!categoriaSelect.value) {
    msg.textContent = " Categoria é obrigatória";
    msg.style.color = "#f44";
    return;
  }

  const produto = {
    nome: nome.value.trim(),
    descricao: descricao.value.trim(),
    preco: parseFloat(preco.value),
    urlFoto: urlFoto.value.trim() || null,
    quantidade: parseInt(quantidade.value),
    categoriaId: parseInt(categoriaSelect.value)
  };

  try {
    let response;
    
    if (produtoEmEdicao) {
      // ATUALIZAR produto existente
      response = await fetch(`${API_URL}/${produtoEmEdicao}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });
      
      if (!response.ok) {
        const erro = await response.json();
        throw new Error(erro.message || "Erro ao atualizar produto");
      }
      
      msg.textContent = "Produto atualizado com sucesso!";
      produtoEmEdicao = null;
      btnSalvar.textContent = "Salvar Produto";
      btnCancelar.style.display = "none";
    } else {
      // CRIAR novo produto
      response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });

      if (!response.ok) {
        const erro = await response.json();
        throw new Error(erro.message || "Erro ao cadastrar produto");
      }

      msg.textContent = "Produto cadastrado com sucesso!";
    }

    msg.style.color = "#34a853";
    form.reset();
    listarProdutos();
    setTimeout(() => msg.textContent = "", 3000);

  } catch (error) {
    console.error(error);
    msg.textContent = "❌ " + error.message;
    msg.style.color = "#f44";
  }
});

/* 
   EDITAR PRODUTO
 */
async function editarProduto(id) {
  try {
    const response = await fetch(`${API_URL}/${id}`);
    
    if (!response.ok) {
      throw new Error("Erro ao carregar dados do produto");
    }
    
    const produto = await response.json();
    
    // Preencher o form com os dados do produto
    document.getElementById("nome").value = produto.nome;
    document.getElementById("descricao").value = produto.descricao || "";
    document.getElementById("preco").value = produto.preco;
    document.getElementById("quantidade").value = produto.quantidade || 0;
    document.getElementById("urlFoto").value = produto.urlFoto || "";
    document.getElementById("categoria").value = produto.categoriaId || "";
    
    // Marcar que estamos em edição
    produtoEmEdicao = id;
    
    // Atualizar UI
    btnSalvar.textContent = "Atualizar Produto";
    btnCancelar.style.display = "inline-block";
    
    // Scroll até o formulário
    form.scrollIntoView({ behavior: "smooth" });
    
    msg.textContent = "ℹ️ Produto carregado para edição. Altere os dados e clique em salvar.";
    msg.style.color = "#2196F3";
    msg.style.display = "block";
    
  } catch (error) {
    console.error(error);
    msg.textContent = "Erro: " + error.message;
    msg.style.color = "#f44";
  }
}

function cancelarEdicao() {
  produtoEmEdicao = null;
  form.reset();
  btnSalvar.textContent = "Salvar Produto";
  btnCancelar.style.display = "none";
  msg.textContent = "";
}

// Carregar categorias e produtos quando página carrega
document.addEventListener("DOMContentLoaded", () => {
  carregarCategorias();
  listarProdutos();
});
