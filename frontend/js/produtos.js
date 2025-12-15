const API = "http://localhost:8080/api/produtos";

document.addEventListener("DOMContentLoaded", () => {
  listar();

  document.getElementById("produtoForm").addEventListener("submit", salvar);
});

async function salvar(e) {
  e.preventDefault();

  const body = {
    nome: nome.value,
    descricao: descricao.value,
    preco: parseFloat(preco.value),
    urlFoto: urlFoto.value
  };

  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  if (res.ok) {
    msg.textContent = "Produto salvo com sucesso!";
    produtoForm.reset();
    listar();
  } else {
    msg.textContent = "Erro ao salvar produto.";
  }
}

async function listar() {
  const res = await fetch(API);
  const produtos = await res.json();

  produtosList.innerHTML = "";
  produtos.forEach(p => {
    produtosList.innerHTML += `
      <li>
        <strong>${p.nome}</strong> - R$ ${p.preco}<br>
        ${p.descricao}
      </li>
    `;
  });
}
