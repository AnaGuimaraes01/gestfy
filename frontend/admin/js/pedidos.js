const API_URL = "https://gestfy-backend.onrender.com/api/pedidos";
const listaPedidos = document.getElementById("listaPedidos");

async function carregarPedidos() {
    try {
        const response = await fetch(API_URL);
        
        if (!response.ok) {
            throw new Error(`Erro na API: ${response.status} - ${response.statusText}`);
        }
        
        const pedidos = await response.json();
        
        if (!Array.isArray(pedidos) || pedidos.length === 0) {
            listaPedidos.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #999;">Nenhum pedido registrado</td></tr>';
            return;
        }

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
    } catch (error) {
        console.error("Erro ao carregar pedidos:", error);
        listaPedidos.innerHTML = `<tr><td colspan="6" style="text-align: center; color: #f44;">Erro ao carregar pedidos: ${error.message}</td></tr>`;
    }
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
    try {
        const response = await fetch(`${API_URL}/${id}/status?status=${status}`, {
            method: "PUT"
        });

        if (!response.ok) {
            throw new Error(`Erro ao atualizar status`);
        }

        // Recarrega lista após atualizar
        await carregarPedidos();
    } catch (error) {
        console.error("Erro:", error);
        alert("Erro ao atualizar status do pedido");
    }
}

async function verDetalhes(id) {
    window.location.href = `pedido-detalhes.html?id=${id}`;
}

// Carregar pedidos quando página carrega
document.addEventListener("DOMContentLoaded", () => {
    carregarPedidos();
});
