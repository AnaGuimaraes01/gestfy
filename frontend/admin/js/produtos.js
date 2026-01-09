const API_URL = `${BASE_URL}/produtos`;

const produtosList = document.getElementById("produtosList");
const form = document.getElementById("produtoForm");
const msg = document.getElementById("msg");

/* =========================
   LISTAR PRODUTOS
========================= */
async function listarProdutos() {
  try {
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
      const imageUrl = produto.urlFoto || "🍦";
      
      // Se é URL, tenta carregar imagem; senão mostra emoji
      let imagemHtml = "";
      if (produto.urlFoto && produto.urlFoto.startsWith("http")) {
        imagemHtml = `<img src="${produto.urlFoto}" alt="${produto.nome}" class="produto-thumb" onerror="this.textContent='🍦'">`;
      } else {
        imagemHtml = `<div class="produto-thumb-emoji">🍦</div>`;
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
          <button class="btn-small btn-danger" onclick="deletarProduto(${produto.id})">🗑️ Deletar</button>
        </div>
      `;

      produtosList.appendChild(li);
    });

  } catch (error) {
    console.error(error);
    msg.textContent = "❌ Erro ao carregar produtos, api url" + API_URL;
  }
}

/* =========================
   CADASTRAR PRODUTO
========================= */
form.addEventListener("submit", async (e) => {
  e.preventDefault();

  // Validar campos
  if (!nome.value.trim()) {
    msg.textContent = "❌ Nome do produto é obrigatório";
    return;
  }
  if (!preco.value || parseFloat(preco.value) <= 0) {
    msg.textContent = "❌ Preço deve ser maior que 0";
    return;
  }
  if (!quantidade.value || parseInt(quantidade.value) <= 0) {
    msg.textContent = "❌ Quantidade deve ser maior que 0";
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
    const response = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(produto)
    });

    if (!response.ok) {
      const erro = await response.json();
      throw new Error(erro.message || "Erro ao cadastrar produto");
    }

    msg.textContent = "✅ Produto cadastrado com sucesso!";
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

/* =========================
   DELETAR PRODUTO
========================= */
async function deletarProduto(id) {
  if (!confirm("Tem certeza que deseja deletar este produto?")) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "DELETE"
    });

    if (!response.ok) {
      throw new Error("Erro ao deletar produto");
    }

    msg.textContent = "✅ Produto deletado com sucesso!";
    msg.style.color = "#34a853";
    listarProdutos();
    setTimeout(() => msg.textContent = "", 3000);

  } catch (error) {
    console.error(error);
    msg.textContent = "❌ " + error.message;
    msg.style.color = "#f44";
  }
}

/* =========================
   EDITAR PRODUTO
========================= */
async function editarProduto(id) {
  alert("⚠️ Edição em desenvolvimento.\nPor enquanto, delete e crie novamente.");
}

/* =========================
   CARREGAR AO ABRIR A PÁGINA
========================= */
listarProdutos();
