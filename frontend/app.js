const API = "http://localhost:8080/api/produtos"; // ajuste se backend estiver noutra porta

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("produtoForm");
  const lista = document.getElementById("produtosList");
  const msg = document.getElementById("msg");
  const btnRefresh = document.getElementById("btn-refresh");

  async function fetchProdutos() {
    try {
      const res = await fetch(API);
      if (!res.ok) throw new Error("Erro ao buscar produtos: " + res.status);
      const data = await res.json();
      renderList(data);
    } catch (err) {
      console.error(err);
      msg.textContent = "Erro ao carregar produtos. Veja console.";
      msg.style.color = "red";
    }
  }

  function renderList(produtos) {
    lista.innerHTML = "";
    if (!produtos || produtos.length === 0) {
      lista.innerHTML = "<li>(nenhum produto)</li>";
      return;
    }
    produtos.forEach(p => {
      const li = document.createElement("li");
      li.innerHTML = `<strong>${escapeHtml(p.nome)}</strong> — R$ ${p.preco} — Qt: ${p.quantidade}`;
      lista.appendChild(li);
    });
  }

  async function criarProduto(event) {
    event.preventDefault();
    msg.textContent = "";
    const nome = document.getElementById("nome").value.trim();
    const preco = document.getElementById("preco").value.trim();
    const quantidade = parseInt(document.getElementById("quantidade").value, 10);

    if (!nome || !preco || Number.isNaN(quantidade)) {
      msg.textContent = "Preencha todos os campos corretamente.";
      msg.style.color = "red";
      return;
    }

    const body = {
      nome,
      descricao: "",
      preco: parseFloat(preco),
      quantidade
    };

    try {
      const res = await fetch(API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error("Erro ao criar: " + res.status + " - " + txt);
      }
      const criado = await res.json();
      msg.textContent = `Criado: ${criado.nome} (id ${criado.id})`;
      msg.style.color = "green";
      form.reset();
      fetchProdutos();
    } catch (err) {
      console.error(err);
      msg.textContent = "Erro ao criar produto. Veja console.";
      msg.style.color = "red";
    }
  }

  function escapeHtml(text) {
    return text ? text.replace(/[&<>"']/g, function(m){ return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]) }) : "";
  }

  form.addEventListener("submit", criarProduto);
  btnRefresh.addEventListener("click", fetchProdutos);

  // primeira carga
  fetchProdutos();
});
