const API_URL = "http://localhost:8080/api/produtos";

document.addEventListener("DOMContentLoaded", () => {

  const form = document.getElementById("produtoForm");
  const lista = document.getElementById("produtosList");
  const msg = document.getElementById("msg");
  const btnRefresh = document.getElementById("btn-refresh");

  async function fetchProdutos() {
    try {
      const res = await fetch(API_URL);
      const data = await res.json();
      renderList(data);
    } catch (err) {
      console.error(err);
      msg.textContent = "Erro ao carregar produtos.";
      msg.style.color = "red";
    }
  }

  function renderList(produtos) {
    lista.innerHTML = "";

    if (!produtos.length) {
      lista.innerHTML = "<li>Nenhum produto encontrado.</li>";
      return;
    }

    produtos.forEach(p => {
      const li = document.createElement("li");
      li.innerHTML = `
        <strong>${p.nome}</strong> — R$ ${p.preco} <br>
        <em>${p.descricao}</em>
      `;
      lista.appendChild(li);
    });
  }

  async function criarProduto(event) {
    event.preventDefault();
    msg.textContent = "";

    const nome = document.getElementById("nome").value.trim();
    const descricao = document.getElementById("descricao").value.trim();
    const preco = document.getElementById("preco").value.trim();
    const urlFoto = document.getElementById("urlFoto").value.trim();

    const body = {
      nome,
      descricao,
      preco: parseFloat(preco),
      urlFoto
    };

    try {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });

      if (!res.ok) throw new Error(await res.text());

      msg.textContent = "Produto cadastrado!";
      msg.style.color = "green";

      form.reset();
      fetchProdutos();

    } catch (err) {
      console.error(err);
      msg.textContent = "Erro ao criar produto.";
      msg.style.color = "red";
    }
  }

  form.addEventListener("submit", criarProduto);
  btnRefresh.addEventListener("click", fetchProdutos);

  fetchProdutos();
});
