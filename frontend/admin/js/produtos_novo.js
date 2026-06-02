let API_URL = "https://gestfy-backend.onrender.com/api/produtos";
let API_CATEGORIAS = "https://gestfy-backend.onrender.com/api/categorias";
(async function() { try { const c = new AbortController(); const t = setTimeout(() => c.abort(), 200); const r = await fetch('http://localhost:8080/api/produtos', { signal: c.signal }); clearTimeout(t); if (r && r.ok) { API_URL = 'http://localhost:8080/api/produtos'; API_CATEGORIAS = 'http://localhost:8080/api/categorias'; } } catch (e) {} })();

const produtosList = document.getElementById("produtosList");
const form = document.getElementById("produtoForm");
const msg = document.getElementById("msg");
const selectCategoria = document.getElementById("categoria");

let produtoEmEdicao = null;

// Carregar categorias no select
async function carregarCategorias() {
  try {
    const response = await fetch(API_CATEGORIAS);
    if (!response.ok) throw new Error("Erro ao buscar categorias");
    
    const categorias = await response.json();
    selectCategoria.innerHTML = '<option value="">Selecione uma categoria</option>';
    
    categorias.forEach(cat => {
      const option = document.createElement("option");
      option.value = cat.id;
      option.textContent = cat.nome;
      selectCategoria.appendChild(option);
    });
  } catch (error) {
    console.error("Erro ao carregar categorias:", error);
    msg.textContent = "Erro ao carregar categorias";
  }
}

async function listarProdutos() {
  try {
    console.log("Buscando produtos na API: ", API_URL);
    const response = await fetch(API_URL);

    if (!response.ok) {
      throw new Error("Erro ao buscar produtos");
    }

    const produtos = await response.json();
    produtosList.innerHTML = "";

    if (produtos.length === 0) {
      produtosList.innerHTML = "<li style='text-align: center; color: #999;'>Nenhum produto cadastrado</li>";
      return;
    }

    produtos.forEach(produto => {
      const li = document.createElement("li");
      
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
          <small style="color: #999; display: block; margin-top: 5px;">ID: ${produto.id}</small>
        </div>
        <div class="produto-actions">
          <button class="btn-small" onclick="editarProduto(${produto.id})">Editar</button>
        </div>
      `;

      produtosList.appendChild(li);
    });

  } catch (error) {
    console.error(error);
    msg.textContent = "Erro ao carregar produtos";
  }
}

//Cadastrar ou atualizar produto
form.addEventListener("submit", async (e) => {
  e.preventDefault();

  // Validar campos
  if (!nome.value.trim()) {
    msg.textContent = " Nome do produto é obrigatório";
    return;
  }
  if (!preco.value || parseFloat(preco.value) <= 0) {
    msg.textContent = " Preço deve ser maior que 0";
    return;
  }
  if (!quantidade.value || parseInt(quantidade.value) <= 0) {
    msg.textContent = " Quantidade deve ser maior que 0";
    return;
  }
  if (!selectCategoria.value) {
    msg.textContent = " Categoria é obrigatória";
    return;
  }

  const produto = {
    nome: nome.value.trim(),
    descricao: descricao.value.trim(),
    preco: parseFloat(preco.value),
    urlFoto: urlFoto.value.trim() || null,
    quantidade: parseInt(quantidade.value),
    categoriaId: parseInt(selectCategoria.value)
  };

  try {
    let response;
    let mensagem;
    
    if (produtoEmEdicao) {
      // ATUALIZAR PRODUTO EXISTENTE
      response = await fetch(`${API_URL}/${produtoEmEdicao}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });
      mensagem = "Produto atualizado com sucesso!";
    } else {
      // CRIAR NOVO PRODUTO
      response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });
      mensagem = "Produto cadastrado com sucesso!";
    }

    if (!response.ok) {
      const erro = await response.json();
      throw new Error(erro.message || "Erro ao processar produto");
    }

    msg.textContent = mensagem;
    msg.style.color = "#34a853";
    form.reset();
    selectCategoria.value = "";
    produtoEmEdicao = null;
    document.getElementById("btnCancelar").style.display = "none";
    
    // Atualizar label do botão
    document.querySelector("button[type='submit']").textContent = "Salvar Produto";
    
    listarProdutos();
    setTimeout(() => msg.textContent = "", 3000);

  } catch (error) {
    console.error(error);
    msg.textContent = "Erro: " + error.message;
    msg.style.color = "#f44";
  }
});


async function editarProduto(id) {
  try {
    const response = await fetch(`${API_URL}/${id}`);
    if (!response.ok) {
      throw new Error("Erro ao buscar produto");
    }

    const produto = await response.json();
    
    // Preencher formulário com dados do produto
    document.getElementById("nome").value = produto.nome;
    document.getElementById("descricao").value = produto.descricao;
    document.getElementById("preco").value = produto.preco;
    document.getElementById("urlFoto").value = produto.urlFoto || "";
    document.getElementById("quantidade").value = produto.quantidade;
    document.getElementById("categoria").value = produto.categoriaId;
    
    produtoEmEdicao = id;
    document.querySelector("button[type='submit']").textContent = "Atualizar Produto";
    document.getElementById("btnCancelar").style.display = "block";
    
    form.scrollIntoView({ behavior: "smooth" });
    
  } catch (error) {
    console.error(error);
    msg.textContent = "Erro ao buscar produto para edição";
  }
}

function cancelarEdicao() {
  form.reset();
  produtoEmEdicao = null;
  selectCategoria.value = "";
  document.querySelector("button[type='submit']").textContent = "Salvar Produto";
  document.getElementById("btnCancelar").style.display = "none";
  msg.textContent = "";
}

carregarCategorias();
listarProdutos();
