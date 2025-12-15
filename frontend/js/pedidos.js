const API_URL = "http://localhost:8080/api/pedidos";
const tabela = document.getElementById("listaPedidos");

async function carregarPedidos() {
  try {
    const res = await fetch(API_URL);
    const pedidos = await res.json();

    tabela.innerHTML = "";

    pedidos.forEach(p => {
      const tr = document.createElement("tr");

      tr.innerHTML = `
        <td>${p.nomeCliente}</td>
        <td>${p.formaPagamento}</td>
        <td>R$ ${p.total.toFixed(2)}</td>
        <td>${p.status}</td>
        <td>
          <select onchange="alterarStatus(${p.id}, this.value)">
            <option ${p.status === 'RECEBIDO' ? 'selected' : ''}>RECEBIDO</option>
            <option ${p.status === 'EM_PREPARO' ? 'selected' : ''}>EM_PREPARO</option>
            <option ${p.status === 'FINALIZADO' ? 'selected' : ''}>FINALIZADO</option>
            <option ${p.status === 'CANCELADO' ? 'selected' : ''}>CANCELADO</option>
          </select>
        </td>
      `;

      tabela.appendChild(tr);
    });

  } catch (e) {
    alert("Erro ao carregar pedidos");
  }
}

async function alterarStatus(id, status) {
  await fetch(`${API_URL}/${id}/status`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status })
  });

  carregarPedidos();
}

carregarPedidos();
