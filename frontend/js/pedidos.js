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
    // Status compatíveis com backend
    const status = [
        "RECEBIDO",
        "EM_PREPARO",
        "PRONTO_RETIRADA",
        "SAIU_ENTREGA",
        "FINALIZADO",
        "CANCELADO"
    ];
    return status.map(s =>
        `<option value="${s}" ${s === statusAtual ? "selected" : ""}>${s.replace('_', ' ')}</option>`
    ).join("");
}

async function atualizarStatus(id, status) {
    await fetch(`${API_URL}/${id}/status?status=${status}`, {
        method: "PUT"
    });
    // Recarrega lista após atualizar
    carregarPedidos();
}

async function verDetalhes(id) {
    const response = await fetch(`${API_URL}/${id}`);
    if (!response.ok) {
        alert("Erro ao buscar detalhes do pedido.");
        return;
    }
    const pedido = await response.json();
    let itens = (pedido.itens || []).map(item =>
        `• ${item.produto?.nome || 'Produto'} (x${item.quantidade})`
    ).join("\n");
    alert(
        `Pedido #${pedido.id}\n\n` +
        `Cliente: ${pedido.nomeCliente || ''}\n` +
        `Total: R$ ${pedido.total || 0}\n` +
        `Status: ${pedido.status || ''}\n` +
        `Pagamento: ${pedido.formaPagamento || ''}\n\n` +
        `Itens:\n${itens}`
    );
}

carregarPedidos();
