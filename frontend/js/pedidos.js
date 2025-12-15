const API_URL = "http://localhost:8080/api/pedidos";
const listaPedidos = document.getElementById("listaPedidos");

async function carregarPedidos() {
    const response = await fetch(API_URL);
    const pedidos = await response.json();

    listaPedidos.innerHTML = "";

    pedidos.forEach(pedido => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${pedido.id}</td>
            <td>
                <strong>${pedido.nomeCliente}</strong><br>
                <small>${pedido.telefone}</small>
            </td>
            <td>${pedido.formaPagamento}</td>
            <td>R$ ${pedido.total.toFixed(2)}</td>
            <td>
                <select onchange="atualizarStatus(${pedido.id}, this.value)">
                    ${gerarOptionsStatus(pedido.status)}
                </select>
            </td>
            <td>
                <button onclick="verDetalhes(${pedido.id})">Detalhes</button>
            </td>
        `;

        listaPedidos.appendChild(tr);
    });
}

function gerarOptionsStatus(statusAtual) {
    const status = ["ABERTO", "EM_PREPARO", "FINALIZADO", "CANCELADO"];
    return status.map(s =>
        `<option value="${s}" ${s === statusAtual ? "selected" : ""}>${s}</option>`
    ).join("");
}

async function atualizarStatus(id, status) {
    await fetch(`${API_URL}/${id}/status?status=${status}`, {
        method: "PUT"
    });
}

async function verDetalhes(id) {
    const response = await fetch(`${API_URL}/${id}`);
    const pedido = await response.json();

    let itens = pedido.itens.map(item =>
        `• ${item.produto.nome} (x${item.quantidade})`
    ).join("\n");

    alert(
        `Pedido #${pedido.id}\n\n` +
        `Cliente: ${pedido.nomeCliente}\n` +
        `Total: R$ ${pedido.total}\n\n` +
        `Itens:\n${itens}`
    );
}

carregarPedidos();
