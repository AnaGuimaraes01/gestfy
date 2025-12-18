const API_URL = "http://localhost:8080/api/produtos";

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
      throw new Error("Erro ao buscar produtos");
    }

    const produtos = await response.json();
    produtosList.innerHTML = "";

    if (produtos.length === 0) {
      produtosList.innerHTML = "<li style='text-align: center; color: #999;'>Nenhum produto cadastrado</li>";
      return;
    }

    // Verificar produtos com estoque baixo
    const produtosBaixo = produtos.filter(p => p.quantidade <= 5);
    if (produtosBaixo.length > 0) {
      const alertaBaixo = document.createElement("li");
      alertaBaixo.style.cssText = `
        list-style: none;
        background: rgba(255, 193, 7, 0.15);
        border-left: 4px solid #fbbc04;
        padding: 16px;
        margin-bottom: 20px;
        border-radius: 6px;
      `;
      
      const quantidadeTotal = produtosBaixo.reduce((acc, p) => acc + p.quantidade, 0);
      const nomesProdutos = produtosBaixo.map(p => p.nome).join(', ');
      
      alertaBaixo.innerHTML = `
        <p style="color: #fbbc04; font-weight: 600; margin: 0 0 8px 0;">⚠️ ${produtosBaixo.length} produto(s) com estoque baixo</p>
        <p style="color: #bdbdbd; font-size: 13px; margin: 0;">
          ${nomesProdutos}
        </p>
      `;
      produtosList.appendChild(alertaBaixo);
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

      // Indicar se está com estoque baixo
      const estoqueBaixo = produto.quantidade <= 5;
      const indicadorEstoque = estoqueBaixo ? `<span style="color: #fbbc04; font-weight: 600;">⚠️ Estoque Baixo</span>` : '';

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
          ${indicadorEstoque}
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
    msg.textContent = "❌ Erro ao carregar produtos";
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
    msg.style.color = "#f44";
    return;
  }
  
  // Validar se nome contém apenas letras, espaços e caracteres especiais válidos
  if (!/^[a-záàâãéèêíïóôõöúçñ\s\-&()]+$/i.test(nome.value.trim())) {
    msg.textContent = "❌ Nome não pode conter números ou caracteres inválidos";
    msg.style.color = "#f44";
    return;
  }
  
  if (!preco.value || parseFloat(preco.value) <= 0) {
    msg.textContent = "❌ Preço deve ser maior que 0";
    msg.style.color = "#f44";
    return;
  }
  if (!quantidade.value || parseInt(quantidade.value) <= 0) {
    msg.textContent = "❌ Quantidade deve ser maior que 0";
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
      // EDITAR
      response = await fetch(`${API_URL}/${produtoEmEdicao.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });
      
      if (!response.ok) {
        const erro = await response.json();
        throw new Error(erro.message || "Erro ao atualizar produto");
      }
      
      msg.textContent = "✅ Produto atualizado com sucesso!";
    } else {
      // CRIAR NOVO
      response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(produto)
      });

      if (!response.ok) {
        const erro = await response.json();
        throw new Error(erro.message || "Erro ao cadastrar produto");
      }
      
      msg.textContent = "✅ Produto cadastrado com sucesso!";
    }

    msg.style.color = "#34a853";
    form.reset();
    produtoEmEdicao = null;
    const btnSubmit = form.querySelector('button[type="submit"]');
    btnSubmit.textContent = "Salvar Produto";
    listarProdutos();
    setTimeout(() => msg.textContent = "", 3000);

  } catch (error) {
    console.error(error);
    msg.textContent = "❌ " + error.message;
    msg.style.color = "#f44";
  }
});


/* =========================
   EDITAR PRODUTO
========================= */
let produtoEmEdicao = null;

async function editarProduto(id) {
  try {
    const response = await fetch(`${API_URL}/${id}`);
    if (!response.ok) throw new Error("Produto não encontrado");
    
    const produto = await response.json();
    produtoEmEdicao = produto;
    
    // Preencher formulário com dados
    nome.value = produto.nome;
    descricao.value = produto.descricao || "";
    preco.value = produto.preco;
    quantidade.value = produto.quantidade;
    urlFoto.value = produto.urlFoto || "";
    
    // Mudar texto do botão
    const btnSubmit = form.querySelector('button[type="submit"]');
    btnSubmit.textContent = "💾 Atualizar Produto";
    
    // Mostrar botão cancelar
    const btnCancelar = document.getElementById('btnCancelar');
    btnCancelar.style.display = 'block';
    
    // Scroll até o formulário
    form.scrollIntoView({ behavior: 'smooth', block: 'start' });
    
  } catch (error) {
    console.error(error);
    msg.textContent = "❌ Erro ao carregar produto para edição";
    msg.style.color = "#f44";
  }
}

function cancelarEdicao() {
  produtoEmEdicao = null;
  form.reset();
  const btnSubmit = form.querySelector('button[type="submit"]');
  btnSubmit.textContent = "Salvar Produto";
  const btnCancelar = document.getElementById('btnCancelar');
  btnCancelar.style.display = 'none';
  msg.textContent = "";
}

/* =========================
   CARREGAR AO ABRIR A PÁGINA
========================= */
listarProdutos();
