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
    
    // Criar tabela de itens
    let itensHTML = (pedido.itens || []).map(item => `
        <tr>
            <td>${item.nomeProduto || 'Produto sem nome'}</td>
            <td style="text-align: center;">${item.quantidade}</td>
            <td style="text-align: right;">R$ ${parseFloat(item.precoUnitario || 0).toFixed(2)}</td>
            <td style="text-align: right;">R$ ${parseFloat(item.subtotal || 0).toFixed(2)}</td>
        </tr>
    `).join("");
    
    // Criar modal com detalhes
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
    `;
    
    const content = document.createElement('div');
    content.style.cssText = `
        background: var(--cinza-card);
        padding: 30px;
        border-radius: 12px;
        max-width: 600px;
        width: 90%;
        max-height: 90vh;
        overflow-y: auto;
        border: 1px solid #444;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
    `;
    
    content.innerHTML = `
        <div style="margin-bottom: 24px;">
            <h2 style="color: var(--rosa); margin-bottom: 20px; font-size: 22px;">📋 Pedido #${pedido.id}</h2>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px;">
                <div style="background: #1a1a1a; padding: 12px; border-radius: 8px;">
                    <p style="font-size: 11px; color: #888; margin-bottom: 4px;">CLIENTE</p>
                    <p style="font-weight: 600; font-size: 14px;">${pedido.nomeCliente || 'N/A'}</p>
                </div>
                <div style="background: #1a1a1a; padding: 12px; border-radius: 8px;">
                    <p style="font-size: 11px; color: #888; margin-bottom: 4px;">TELEFONE</p>
                    <p style="font-weight: 600; font-size: 14px;">📞 ${pedido.telefone || 'N/A'}</p>
                </div>
                <div style="background: #1a1a1a; padding: 12px; border-radius: 8px;">
                    <p style="font-size: 11px; color: #888; margin-bottom: 4px;">PAGAMENTO</p>
                    <p style="font-weight: 600; font-size: 14px;">${pedido.formaPagamento || 'N/A'}</p>
                </div>
                <div style="background: #1a1a1a; padding: 12px; border-radius: 8px;">
                    <p style="font-size: 11px; color: #888; margin-bottom: 4px;">STATUS</p>
                    <p style="font-weight: 600; font-size: 14px; color: var(--rosa);">${pedido.status?.replace(/_/g, ' ') || 'N/A'}</p>
                </div>
            </div>
        </div>

        <div style="margin-bottom: 20px;">
            <h3 style="color: #bdbdbd; margin-bottom: 12px; font-size: 16px;">🛒 Itens do Pedido</h3>
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse; font-size: 13px;">
                    <thead>
                        <tr style="border-bottom: 2px solid #444;">
                            <th style="text-align: left; padding: 8px; color: #888;">Produto</th>
                            <th style="text-align: center; padding: 8px; color: #888;">Qtd</th>
                            <th style="text-align: right; padding: 8px; color: #888;">Preço</th>
                            <th style="text-align: right; padding: 8px; color: #888;">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${itensHTML}
                    </tbody>
                </table>
            </div>
        </div>

        <div style="background: linear-gradient(135deg, #2b2b2b, #1a1a1a); padding: 16px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #3a3a3a;">
            <p style="font-size: 12px; color: #888; margin-bottom: 4px;">TOTAL DO PEDIDO</p>
            <p style="font-size: 24px; font-weight: 700; color: var(--rosa);">R$ ${parseFloat(pedido.total || 0).toFixed(2)}</p>
        </div>

        <div style="display: flex; gap: 10px; justify-content: flex-end;">
            <button onclick="this.closest('[data-modal]').remove()" style="
                background: #444;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 6px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
            ">Fechar</button>
        </div>
    `;
    
    content.setAttribute('data-modal', 'true');
    modal.appendChild(content);
    document.body.appendChild(modal);
    
    // Fechar ao clicar fora
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.remove();
        }
    });
}

carregarPedidos();
