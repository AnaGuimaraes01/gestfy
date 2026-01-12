
// CAIXA.JS - Lógica do Caixa Diário

const API_URL = "https://gestfy-backend.onrender.com/api/caixa";

const corpo = document.getElementById("corpo-tabela");
const msg = document.getElementById("msg");
const btnFecharCaixa = document.getElementById("btn-fechar-caixa");
const dataAtual = document.getElementById("data-atual");
const modalFechamento = document.getElementById("modal-fechamento");

let dataAtualSelecionada = new Date().toISOString().split("T")[0];

// INICIALIZAÇÃO

document.addEventListener("DOMContentLoaded", () => {
    inicializar();
});

function inicializar() {
    // Define data atual
    dataAtual.textContent = formatarDataBR(new Date());

    // Carrega dados do dia
    carregarCaixaDoDia();

    // Event listeners
    btnFecharCaixa.addEventListener("click", abrirModalFechamento);

    // Auto-refresh a cada 30 segundos
    setInterval(carregarCaixaDoDia, 30000);
}


// CARREGAR DADOS DO CAIXA

async function carregarCaixaDoDia(data = null) {
    try {
        const params = data ? `?data=${data}` : "";
        const response = await fetch(`${API_URL}/dia${params}`);

        if (!response.ok) throw new Error("Erro ao carregar caixa");

        const dados = await response.json();

        // Atualiza totalizadores
        document.getElementById("total-arrecadado").textContent = formatarMoeda(
            dados.totalDia
        );
        document.getElementById("qtd-transacoes").textContent = dados.quantidadeRegistros;

        // Calcula entradas e saídas
        const entradas = dados.registros
            .filter((r) => r.saldo > 0)
            .reduce((sum, r) => sum + r.saldo, 0);

        const saidas = dados.registros
            .filter((r) => r.saldo < 0)
            .reduce((sum, r) => sum + r.saldo, 0);

        document.getElementById("total-entradas").textContent = formatarMoeda(entradas);
        document.getElementById("total-saidas").textContent = formatarMoeda(
            Math.abs(saidas)
        );

        // Limpa tabela
        corpo.innerHTML = "";

        // Verifica se há registros
        if (dados.registros.length === 0) {
            corpo.innerHTML = `
                <tr>
                    <td colspan="4" class="empty-state">
                        <p>Nenhuma transação registrada para essa data.</p>
                    </td>
                </tr>
            `;
            return;
        }

        // Popula tabela com registros
        dados.registros.forEach((registro) => {
            const tr = document.createElement("tr");
            const classe =
                registro.saldo > 0
                    ? "valor-positivo"
                    : registro.saldo < 0
                        ? "valor-negativo"
                        : "valor-neutro";

            tr.innerHTML = `
                <td>#${registro.id}</td>
                <td>${registro.descricao}</td>
                <td class="${classe}">${formatarMoeda(registro.saldo)}</td>
                <td>${formatarDataHora(registro.data)}</td>
            `;

            corpo.appendChild(tr);
        });

        mostrarMensagem("Dados carregados com sucesso!", "sucesso");
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

// FILTRAR POR DATA



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
        
        mostrarMensagem("Caixa fechado! ", "sucesso");
        fecharModal();
        setTimeout(carregarCaixaDoDia, 2000);
    } catch (erro) {
        console.error("Erro:", erro);
        mostrarMensagem("Erro ao fechar caixa", "erro");
    }
}
}


// FUNÇÕES AUXILIARES

function formatarMoeda(valor) {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL",
    }).format(valor);
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
    const data = new Date(dataString + "T00:00:00");
    return new Intl.DateTimeFormat("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(data);
}

function mostrarMensagem(texto, tipo = "info") {
    msg.textContent = texto;
    msg.className = `msg-info msg-${tipo}`;
    msg.style.display = "block";

    setTimeout(() => {
        msg.style.display = "none";
    }, 4000);
}

// FECHAR MODAL AO CLICAR FORA

window.addEventListener("click", (e) => {
    if (e.target === modalFechamento) {
        fecharModal();
    }
});
