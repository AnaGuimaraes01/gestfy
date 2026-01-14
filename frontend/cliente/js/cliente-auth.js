// cliente-auth.js - Funções de autenticação removidas
// As páginas do cliente agora não requerem autenticação prévia

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
