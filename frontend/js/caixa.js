// ==========================================
// CAIXA.JS - Lógica do Caixa Diário
// ==========================================

const API_URL = "http://localhost:8080/api/caixa";
const corpo = document.getElementById("corpo-tabela");
const msg = document.getElementById("msg");
const filtroData = document.getElementById("filtro-data");
const btnFiltrar = document.getElementById("btn-filtrar");
const btnHoje = document.getElementById("btn-hoje");
const btnFecharCaixa = document.getElementById("btn-fechar-caixa");
const btnRelatorio = document.getElementById("btn-relatorio");
const dataAtual = document.getElementById("data-atual");
const modalFechamento = document.getElementById("modal-fechamento");

let dataAtualSelecionada = new Date().toISOString().split("T")[0];
let caixaFechado = false;

// ==========================================
// INICIALIZAÇÃO
// ==========================================
document.addEventListener("DOMContentLoaded", () => {
    inicializar();
});

function inicializar() {
    // Define data atual no filtro
    const hoje = new Date().toISOString().split("T")[0];
    filtroData.value = hoje;
    dataAtual.textContent = formatarDataBR(new Date());

    // Carrega dados do dia
    carregarCaixaDoDia();

    // Event listeners
    btnFiltrar.addEventListener("click", filtrarPorData);
    btnHoje.addEventListener("click", voltarParaHoje);
    btnFecharCaixa.addEventListener("click", abrirModalFechamento);
    btnRelatorio.addEventListener("click", visualizarRelatorio);

    // Auto-refresh a cada 30 segundos
    setInterval(carregarCaixaDoDia, 30000);
}

// ==========================================
// CARREGAR DADOS DO CAIXA
// ==========================================
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

// ==========================================
// FILTRAR POR DATA
// ==========================================
function filtrarPorData() {
    const data = filtroData.value;
    if (!data) {
        mostrarMensagem("Selecione uma data", "erro");
        return;
    }

    dataAtualSelecionada = data;
    carregarCaixaDoDia(data);
    dataAtual.textContent = formatarDataBR(new Date(data + "T00:00:00"));
}

// ==========================================
// VOLTAR PARA HOJE
// ==========================================
function voltarParaHoje() {
    const hoje = new Date().toISOString().split("T")[0];
    filtroData.value = hoje;
    dataAtualSelecionada = hoje;
    dataAtual.textContent = formatarDataBR(new Date());
    carregarCaixaDoDia();
}

// ==========================================
// MODAL DE FECHAMENTO
// ==========================================
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
        // Aqui você poderia fazer uma chamada para marcar o caixa como fechado
        // Por enquanto, apenas mostramos a confirmação
        caixaFechado = true;
        mostrarMensagem("✅ Caixa fechado com sucesso!", "sucesso");
        fecharModal();
        
        // Desabilitar ações
        document.getElementById("btn-fechar-caixa").disabled = true;
        document.getElementById("btn-fechar-caixa").textContent = "🔒 Caixa Fechado";
        document.getElementById("btn-fechar-caixa").style.opacity = "0.6";
        
        // Mostrar estado do caixa fechado
        const estadoCaixa = document.createElement("div");
        estadoCaixa.style.cssText = `
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: linear-gradient(135deg, #34a853, #2e7d32);
            color: white;
            padding: 40px;
            border-radius: 12px;
            text-align: center;
            box-shadow: 0 10px 40px rgba(0,0,0,0.3);
            z-index: 5000;
            animation: popUp 0.3s ease;
        `;
        estadoCaixa.innerHTML = `
            <p style="font-size: 48px; margin: 0 0 16px 0;">🔒</p>
            <h2 style="margin: 0 0 10px 0; font-size: 24px;">Caixa Fechado</h2>
            <p style="margin: 0 0 20px 0; opacity: 0.9;">O caixa foi fechado com sucesso para o dia de hoje.</p>
            <button onclick="abrirCaixaNovamente()" style="
                background: white;
                color: #34a853;
                border: none;
                padding: 12px 30px;
                border-radius: 6px;
                font-weight: 600;
                cursor: pointer;
                font-size: 14px;
            ">🔓 Abrir Caixa Novamente</button>
        `;
        
        document.body.appendChild(estadoCaixa);
        
        setTimeout(() => {
            estadoCaixa.remove();
        }, 4000);
        
    } catch (erro) {
        console.error("Erro:", erro);
        mostrarMensagem("Erro ao fechar caixa", "erro");
    }
}

function abrirCaixaNovamente() {
    caixaFechado = false;
    document.getElementById("btn-fechar-caixa").disabled = false;
    document.getElementById("btn-fechar-caixa").textContent = "🔒 Fechar Caixa do Dia";
    document.getElementById("btn-fechar-caixa").style.opacity = "1";
    mostrarMensagem("✅ Caixa aberto novamente!", "sucesso");
}

// ==========================================
// VISUALIZAR RELATÓRIO
// ==========================================
async function visualizarRelatorio() {
    try {
        const params = dataAtualSelecionada ? `?data=${dataAtualSelecionada}` : "";
        const response = await fetch(`${API_URL}/relatorio/fechamento${params}`);

        if (!response.ok) throw new Error("Erro ao gerar relatório");

        const relatorio = await response.json();

        // Constrói mensagem com dados do relatório
        let mensagem = `
📊 RELATÓRIO DO DIA ${relatorio.data}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
💰 Total de Entradas: ${formatarMoeda(relatorio.totalEntradas)}
💸 Total de Saídas: ${formatarMoeda(relatorio.totalSaidas)}
💵 SALDO LÍQUIDO: ${formatarMoeda(relatorio.saldoLiquido)}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 Quantidade de Transações: ${relatorio.quantidadeTransacoes}
        `;

        alert(mensagem);
    } catch (erro) {
        console.error("Erro:", erro);
        mostrarMensagem("Erro ao gerar relatório", "erro");
    }
}

// ==========================================
// FUNÇÕES AUXILIARES
// ==========================================

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

// ==========================================
// FECHAR MODAL AO CLICAR FORA
// ==========================================
window.addEventListener("click", (e) => {
    if (e.target === modalFechamento) {
        fecharModal();
    }
});
