// cliente-auth.js - Autenticação removida (não necessária)
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
