
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
        document.getElementById("total-saidas").textContent = formatarMoeda(0);

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
        mostrarMensagem("Caixa fechado com sucesso!", "sucesso");
        fecharModal();
        setTimeout(carregarVendasDoDia, 2000);
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
