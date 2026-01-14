const API_URL = "https://gestfy-backend.onrender.com/api/produtos";

const produtosList = document.getElementById("produtosList");
const form = document.getElementById("produtoForm");
const msg = document.getElementById("msg");
const btnSalvar = document.getElementById("btnSalvar");
const btnCancelar = document.getElementById("btnCancelar");

// ID do produto em edição (null = novo produto)
let produtoEmEdicao = null;

/*Listar Pedidos*/
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

/*
   CADASTRAR/ATUALIZAR PRODUTO
*/
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

  const produto = {
    nome: nome.value.trim(),
    descricao: descricao.value.trim(),
    preco: parseFloat(preco.value),
    urlFoto: urlFoto.value.trim() || null,
    quantidade: parseInt(quantidade.value)
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

// Carregar produtos quando página carrega
document.addEventListener("DOMContentLoaded", () => {
  listarProdutos();
});
