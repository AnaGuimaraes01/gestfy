let API_CATEGORIAS = "https://gestfy-backend.onrender.com/api/categorias";
(async function() { try { const c = new AbortController(); const t = setTimeout(() => c.abort(), 200); const r = await fetch('http://localhost:8080/api/categorias', { signal: c.signal }); clearTimeout(t); if (r && r.ok) { API_CATEGORIAS = 'http://localhost:8080/api/categorias'; } } catch (e) {} })();

const form = document.getElementById("categoriaForm");
const msgCat = document.getElementById("msgCat");
const btnSalvarCat = document.getElementById("btnSalvarCat");
const btnCancelarCat = document.getElementById("btnCancelarCat");
const categoriasList = document.getElementById("categoriasList");

let categoriaEmEdicao = null;

// Listar categorias
async function listarCategorias() {
  try {
    const response = await fetch(API_CATEGORIAS);
    if (!response.ok) throw new Error("Erro ao buscar categorias");
    
    const categorias = await response.json();
    categoriasList.innerHTML = "";

    if (categorias.length === 0) {
      categoriasList.innerHTML = "<li style='text-align: center; color: #999;'>Nenhuma categoria cadastrada</li>";
      return;
    }

    categorias.forEach(categoria => {
      const li = document.createElement("li");
      li.className = "produto-item";
      li.innerHTML = `
        <div class="produto-details">
          <strong>${categoria.nome}</strong>
          <small style="color: #999; display: block; margin-top: 5px;">ID: ${categoria.id}</small>
        </div>
        <div class="produto-actions">
          <button class="btn-small" onclick="editarCategoria(${categoria.id}, '${categoria.nome}')">✏️ Editar</button>
          <button class="btn-small" style="background: #f44;" onclick="removerCategoria(${categoria.id})">🗑️ Remover</button>
        </div>
      `;
      categoriasList.appendChild(li);
    });
  } catch (error) {
    console.error(error);
    msgCat.textContent = "Erro ao carregar categorias";
    msgCat.style.color = "#f44";
  }
}

// Cadastrar ou atualizar categoria
form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const nome = document.getElementById("nomeCat").value.trim();
  
  if (!nome) {
    msgCat.textContent = "Nome da categoria é obrigatório";
    msgCat.style.color = "#f44";
    return;
  }

  const categoria = { nome };

  try {
    let response;
    let mensagem;
    
    if (categoriaEmEdicao) {
      response = await fetch(`${API_CATEGORIAS}/${categoriaEmEdicao}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(categoria)
      });
      mensagem = "Categoria atualizada com sucesso!";
      categoriaEmEdicao = null;
      btnSalvarCat.textContent = "Salvar Categoria";
      btnCancelarCat.style.display = "none";
    } else {
      response = await fetch(API_CATEGORIAS, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(categoria)
      });
      mensagem = "Categoria cadastrada com sucesso!";
    }

    if (!response.ok) {
      const erro = await response.json();
      throw new Error(erro.message || "Erro ao processar categoria");
    }

    msgCat.textContent = mensagem;
    msgCat.style.color = "#34a853";
    form.reset();
    listarCategorias();
    setTimeout(() => msgCat.textContent = "", 3000);

  } catch (error) {
    console.error(error);
    msgCat.textContent = "❌ " + error.message;
    msgCat.style.color = "#f44";
  }
});

// Editar categoria
async function editarCategoria(id, nome) {
  document.getElementById("nomeCat").value = nome;
  categoriaEmEdicao = id;
  
  btnSalvarCat.textContent = "Atualizar Categoria";
  btnCancelarCat.style.display = "inline-block";
  
  form.scrollIntoView({ behavior: "smooth" });
  msgCat.textContent = "Alterando categoria...";
  msgCat.style.color = "#2196F3";
}

// Cancelar edição
function cancelarEdicaoCat() {
  form.reset();
  categoriaEmEdicao = null;
  btnSalvarCat.textContent = "Salvar Categoria";
  btnCancelarCat.style.display = "none";
  msgCat.textContent = "";
}

// Remover categoria
async function removerCategoria(id) {
  if (!confirm("Tem certeza que deseja remover esta categoria?")) return;

  try {
    const response = await fetch(`${API_CATEGORIAS}/${id}`, {
      method: "DELETE"
    });

    if (!response.ok) throw new Error("Erro ao remover categoria");

    msgCat.textContent = "Categoria removida com sucesso!";
    msgCat.style.color = "#34a853";
    listarCategorias();
    setTimeout(() => msgCat.textContent = "", 3000);

  } catch (error) {
    console.error(error);
    msgCat.textContent = "Erro ao remover categoria";
    msgCat.style.color = "#f44";
  }
}

// Carregar categorias ao abrir página
document.addEventListener("DOMContentLoaded", () => {
  listarCategorias();
});
