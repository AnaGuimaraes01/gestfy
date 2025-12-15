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

    produtos.forEach(produto => {
      const li = document.createElement("li");

      li.innerHTML = `
        <strong>${produto.nome}</strong>
        <span>R$ ${produto.preco.toFixed(2)}</span>
      `;

      produtosList.appendChild(li);
    });

  } catch (error) {
    console.error(error);
    msg.textContent = "Erro ao carregar produtos";
  }
}

/* =========================
   CADASTRAR PRODUTO
========================= */
form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const produto = {
    nome: nome.value,
    descricao: descricao.value,
    preco: parseFloat(preco.value),
    urlFoto: urlFoto.value
  };

  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(produto)
    });

    if (!response.ok) {
      throw new Error("Erro ao cadastrar produto");
    }

    msg.textContent = "Produto cadastrado com sucesso!";
    form.reset();
    listarProdutos(); // 🔥 ATUALIZA A LISTA

  } catch (error) {
    console.error(error);
    msg.textContent = "Erro ao cadastrar produto";
  }
});

/* =========================
   CARREGAR AO ABRIR A PÁGINA
========================= */
listarProdutos();
