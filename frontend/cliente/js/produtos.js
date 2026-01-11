const API_URL = "https://gestfy-backend.onrender.com/api/produtos";
        const container = document.getElementById("produtosContainer");
        const msg = document.getElementById("mensagem");
        const buscaInput = document.getElementById("buscaInput");
        let todosProdutos = [];

        // Carregar produtos ao abrir página
        async function carregarProdutos() {
            try {
                const response = await fetch(API_URL);
                if (!response.ok) throw new Error("Erro ao buscar produtos");
                
                todosProdutos = await response.json();
                exibirProdutos(todosProdutos);
            } catch (error) {
                console.error(error);
                msg.textContent = "❌ Erro ao carregar produtos";
                msg.style.display = "block";
            }
        }

        // Exibir produtos na tela
        function exibirProdutos(produtos) {
            container.innerHTML = "";
            
            if (produtos.length === 0) {
                container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: #888;">Nenhum produto encontrado</p>';
                return;
            }

            produtos.forEach(produto => {
                const card = document.createElement("div");
                card.className = "produto-card";
                card.innerHTML = `
                    <div class="produto-imagem">
                        <img src="${produto.urlFoto || '🍦'}" alt="${produto.nome}" onerror="this.style.display='none'">
                    </div>
                    <div class="produto-info">
                        <h3 class="produto-nome">${produto.nome}</h3>
                        <p class="produto-descricao">${produto.descricao || "Sem descrição"}</p>
                        <p class="produto-preco">R$ ${parseFloat(produto.preco).toFixed(2)}</p>
                        <button class="btn-carrinho" onclick="adicionarCarrinho(${produto.id}, '${produto.nome}', ${produto.preco})">
                             Adicionar
                        </button>
                    </div>
                `;
                container.appendChild(card);
            });
        }

        // Buscar produtos
        buscaInput.addEventListener("input", (e) => {
            const termo = e.target.value.toLowerCase();
            const filtrados = todosProdutos.filter(p => 
                p.nome.toLowerCase().includes(termo)
            );
            exibirProdutos(filtrados);
        });

        // Adicionar ao carrinho
        function adicionarCarrinho(id, nome, preco) {
            let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];
            
            const itemExistente = carrinho.find(i => i.id === id);
            
            if (itemExistente) {
                itemExistente.quantidade += 1;
            } else {
                carrinho.push({
                    id: id,
                    nome: nome,
                    preco: preco,
                    quantidade: 1
                });
            }
            
            localStorage.setItem("carrinho", JSON.stringify(carrinho));
            
            msg.textContent = `✅ "${nome}" adicionado ao carrinho!`;
            msg.style.display = "block";
            setTimeout(() => msg.style.display = "none", 2000);
        }

        // Carregar produtos na página
        carregarProdutos();