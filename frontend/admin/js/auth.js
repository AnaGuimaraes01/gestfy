// auth.js - Proteção de autenticação para páginas admin

// Verificar autenticação ao carregar página
document.addEventListener("DOMContentLoaded", () => {
    verificarAutenticacao();
    adicionarBotaoLogout();
});

function verificarAutenticacao() {
    const pagina = window.location.pathname;
    const naoProtegidas = ["/admin/login.html", "/admin/"];

    // Se está na página de login, não fazer nada
    if (pagina.includes("login.html")) {
        return;
    }

    // Verificar se está autenticado
    const autenticado = sessionStorage.getItem("adminAuthenticated");
    
    if (autenticado !== "true") {
        // Redirecionar para login
        window.location.href = "login.html";
    }
}

function adicionarBotaoLogout() {
    // Procurar pela header e adicionar botão de logout
    const header = document.querySelector(".admin-header");
    
    if (header) {
        const usuario = sessionStorage.getItem("adminUser") || "Admin";
        
        // Criar container para usuário
        const userDiv = document.createElement("div");
        userDiv.style.cssText = `
            position: absolute;
            right: 20px;
            top: 50%;
            transform: translateY(-50%);
            display: flex;
            align-items: center;
            gap: 10px;
            color: var(--texto-primario);
        `;
        
        userDiv.innerHTML = `
            <span style="font-size: 14px;">${usuario}</span>
            <button onclick="fazerLogout()" style="
                padding: 6px 12px;
                background: var(--rosa);
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 12px;
                font-weight: bold;
            ">Sair</button>
        `;
        
        header.style.position = "relative";
        header.appendChild(userDiv);
    }
}

function fazerLogout() {
    if (confirm("Deseja realmente sair?")) {
        // Limpar sessão
        sessionStorage.removeItem("adminAuthenticated");
        sessionStorage.removeItem("adminUser");
        
        // Redirecionar para login
        window.location.href = "login.html";
    }
}
