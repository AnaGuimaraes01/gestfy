// caixa-auth.js - Proteção de autenticação para caixa

// Verificar autenticação ao carregar página
document.addEventListener("DOMContentLoaded", () => {
    verificarAutenticacaoCaixa();
    adicionarBotaoFecharCaixa();
});

function verificarAutenticacaoCaixa() {
    const pagina = window.location.pathname;

    // Se está na página de login, não fazer nada
    if (pagina.includes("caixa-login.html")) {
        return;
    }

    // Se está na página de caixa, verificar autenticação
    if (pagina.includes("caixa.html")) {
        const autenticado = sessionStorage.getItem("caixaAuthenticated");
        
        if (autenticado !== "true") {
            // Redirecionar para login
            window.location.href = "caixa-login.html";
        }
    }
}

function adicionarBotaoFecharCaixa() {
    // Procurar pela header e adicionar botão de fechar caixa
    const header = document.querySelector(".admin-header");
    
    if (header) {
        const usuario = sessionStorage.getItem("caixaUser") || "Caixa";
        const horaAbertura = new Date(sessionStorage.getItem("caixaOpenedAt")).toLocaleTimeString("pt-BR");
        
        // Criar container para informações do caixa
        const caixaDiv = document.createElement("div");
        caixaDiv.style.cssText = `
            position: absolute;
            right: 20px;
            top: 50%;
            transform: translateY(-50%);
            display: flex;
            align-items: center;
            gap: 10px;
            color: var(--texto-primario);
            font-size: 13px;
        `;
        
        caixaDiv.innerHTML = `
            <span>${usuario} | Aberto às ${horaAbertura}</span>
            <button onclick="fecharCaixa()" style="
                padding: 6px 12px;
                background: #f44;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 12px;
                font-weight: bold;
            ">Fechar Caixa</button>
        `;
        
        header.style.position = "relative";
        header.appendChild(caixaDiv);
    }
}

function fecharCaixa() {
    if (confirm("Deseja realmente fechar o caixa?")) {
        // Limpar sessão do caixa
        sessionStorage.removeItem("caixaAuthenticated");
        sessionStorage.removeItem("caixaUser");
        sessionStorage.removeItem("caixaOpenedAt");
        
        // Redirecionar para login do caixa (usa replace para não voltar ao histórico)
        window.location.replace("caixa-login.html");
    }
}
