
// CAIXA.JS - Lógica do Caixa Diário

const API_CAIXA = "https://gestfy-backend.onrender.com/api/caixa";

const corpo = document.getElementById("corpo-tabela");
const msg = document.getElementById("msg");
const btnFecharCaixa = document.getElementById("btn-fechar-caixa");
const dataAtual = document.getElementById("data-atual");
const modalFechamento = document.getElementById("modal-fechamento");

// INICIALIZAÇÃO

document.addEventListener("DOMContentLoaded", () => {
    inicializar();
});

function inicializar() {
    // Define data atual
    dataAtual.textContent = formatarDataBR(new Date());

    // Carrega dados do dia
    carregarVendasDoDia();

    // Event listeners
    btnFecharCaixa.addEventListener("click", abrirModalFechamento);

    // Auto-refresh a cada 30 segundos
    setInterval(carregarVendasDoDia, 30000);
}


// CARREGAR VENDAS DO DIA

async function carregarVendasDoDia(data = null) {
    try {
        const params = data ? `?data=${data}` : "";
        const response = await fetch(`${API_CAIXA}/dia${params}`);

        if (!response.ok) throw new Error("Erro ao carregar vendas");

        const dados = await response.json();

        // Atualiza totalizadores
        document.getElementById("total-arrecadado").textContent = formatarMoeda(
            dados.totalDia || 0
        );
        document.getElementById("qtd-transacoes").textContent = dados.quantidadeRegistros || 0;

        // Calcula entradas (todas as vendas são positivas)
        const totalEntradas = dados.totalDia || 0;

        document.getElementById("total-entradas").textContent = formatarMoeda(totalEntradas);
        document.getElementById("total-saidas").textContent = formatarMoeda(0); // Sistema sem saídas

        // Atualiza status do caixa
        atualizarStatusCaixa(dados.caixaAberto, dados.caixaFechado);

        // Limpa tabela
        corpo.innerHTML = "";

        // Verifica se há registros
        if (!dados.registros || dados.registros.length === 0) {
            corpo.innerHTML = `
                <tr>
                    <td colspan="4" class="empty-state">
                        <p>Nenhuma venda registrada para esse dia.</p>
                    </td>
                </tr>
            `;
            return;
        }

        // Popula tabela com vendas
        dados.registros.forEach((registro) => {
            const tr = document.createElement("tr");

            tr.innerHTML = `
                <td>#${registro.id}</td>
                <td>${registro.descricao}</td>
                <td class="valor-positivo">${formatarMoeda(registro.saldo || 0)}</td>
                <td>${formatarDataHora(registro.data)}</td>
            `;

            corpo.appendChild(tr);
        });

        // Não mostra mensagem se for auto-refresh
        if (data === null) {
            mostrarMensagem("Dados carregados com sucesso!", "sucesso", true);
        }
    } catch (erro) {
        console.error("Erro:", erro);
        mostrarMensagem("Erro ao carregar dados do caixa", "erro");
        corpo.innerHTML = `
            <tr>
                <td colspan="4" class="empty-state">
                    <p>Erro ao carregar dados. Tente novamente.</p>
                </td>
            </tr>
        `;
    }
}

// ATUALIZAR STATUS DO CAIXA

function atualizarStatusCaixa(caixaAberto, caixaFechado) {
    const btnFechar = document.getElementById("btn-fechar-caixa");
    const statusElement = document.getElementById("status-caixa") || criarStatusElement();

    if (caixaFechado) {
        statusElement.textContent = "✓ Caixa Fechado";
        statusElement.className = "status-fechado";
        btnFechar.disabled = true;
        btnFechar.style.opacity = "0.5";
        btnFechar.style.cursor = "not-allowed";
    } else if (caixaAberto) {
        statusElement.textContent = "⊙ Caixa Aberto";
        statusElement.className = "status-aberto";
        btnFechar.disabled = false;
        btnFechar.style.opacity = "1";
        btnFechar.style.cursor = "pointer";
    } else {
        statusElement.textContent = "○ Caixa Não Aberto";
        statusElement.className = "status-nao-aberto";
        btnFechar.disabled = true;
        btnFechar.style.opacity = "0.5";
        btnFechar.style.cursor = "not-allowed";
    }
}

function criarStatusElement() {
    const div = document.createElement("div");
    div.id = "status-caixa";
    div.style.marginTop = "10px";
    div.style.fontSize = "14px";
    div.style.fontWeight = "bold";
    
    const container = document.querySelector(".acoes-caixa");
    if (container) {
        container.parentElement.insertBefore(div, container.nextSibling);
    }
    return div;
}

// ABRIR CAIXA

async function abrirCaixa() {
    try {
        const response = await fetch(`${API_CAIXA}/abrir`, {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        });

        const dados = await response.json();

        if (response.ok) {
            mostrarMensagem(dados.mensagem, "sucesso");
            setTimeout(carregarVendasDoDia, 1000);
        } else {
            mostrarMensagem(dados.erro || "Erro ao abrir caixa", "erro");
        }
    } catch (erro) {
        console.error("Erro:", erro);
        mostrarMensagem("Erro ao abrir caixa", "erro");
    }
}

// MODAL DE FECHAMENTO

function abrirModalFechamento() {
    const total = document.getElementById("total-arrecadado").textContent;
    document.getElementById("modal-total").textContent = total;
    modalFechamento.classList.add("ativo");
}

function fecharModal() {
    modalFechamento.classList.remove("ativo");
}

async function confirmarFechamento() {
    try {
        const observacoes = document.getElementById("observacoes-fechamento")?.value || 
                           "Fechamento normal do caixa";

        const response = await fetch(`${API_CAIXA}/fechar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ observacoes: observacoes })
        });

        const dados = await response.json();

        if (response.ok) {
            mostrarMensagem("Caixa fechado com sucesso!", "sucesso");
            fecharModal();
            setTimeout(carregarVendasDoDia, 2000);
        } else {
            mostrarMensagem(dados.erro || "Erro ao fechar caixa", "erro");
        }
    } catch (erro) {
        console.error("Erro:", erro);
        mostrarMensagem("Erro ao fechar caixa", "erro");
    }
}


// FUNÇÕES AUXILIARES

function formatarMoeda(valor) {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL",
    }).format(valor || 0);
}

function formatarDataBR(data) {
    const opções = {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
    };
    return data.toLocaleDateString("pt-BR", opções);
}

function formatarDataHora(dataString) {
    if (!dataString) return "-";
    const data = new Date(dataString);
    return new Intl.DateTimeFormat("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(data);
}

function mostrarMensagem(texto, tipo = "info", autoHide = false) {
    msg.textContent = texto;
    msg.className = `msg-info msg-${tipo}`;
    msg.style.display = "block";

    const duracao = autoHide ? 2000 : 4000;
    setTimeout(() => {
        msg.style.display = "none";
    }, duracao);
}

// FECHAR MODAL AO CLICAR FORA

window.addEventListener("click", (e) => {
    if (e.target === modalFechamento) {
        fecharModal();
    }
});
