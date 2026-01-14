// cliente-auth.js - Proteção de autenticação para cliente

// Verificar autenticação ao carregar página
document.addEventListener("DOMContentLoaded", () => {
    verificarAutenticacaoCliente();
});

function verificarAutenticacaoCliente() {
    const pagina = window.location.pathname;

    // Se está na página de login, não fazer nada
    if (pagina.includes("login.html")) {
        return;
    }

    // Se está em páginas do cliente, verificar autenticação
    if (pagina.includes("catalogo.html") || pagina.includes("carrinho.html") || pagina.includes("pedido.html") || pagina.includes("acompanhamento.html") || pagina.includes("pedidos.html")) {
        const autenticado = sessionStorage.getItem("clienteAutenticado");
        
        if (autenticado !== "true") {
            // Redirecionar para login
            window.location.replace("login.html");
        }
    }
}

function fazerLogoutCliente() {
    if (confirm("Deseja realmente sair?")) {
        sessionStorage.removeItem("clienteAutenticado");
        sessionStorage.removeItem("clienteId");
        sessionStorage.removeItem("clienteUsuario");
        sessionStorage.removeItem("clienteNome");
        localStorage.removeItem("carrinho");
        
        window.location.replace("login.html");
    }
}
