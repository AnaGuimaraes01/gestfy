function irPara(pagina) {
  window.location.href = pagina;
}

function redirecionarParaLoginSeNaoEstiverLogado() {
  const token = localStorage.getItem('gestfyToken');
  const role = localStorage.getItem('gestfyRole');

  if (!token || role !== 'ADMIN') {
    window.location.href = 'login.html';
    return false;
  }

  return true;
}

document.addEventListener('DOMContentLoaded', function () {
  redirecionarParaLoginSeNaoEstiverLogado();
});